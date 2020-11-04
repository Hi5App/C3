#include "server.h"

#include <QFile>
#include <QFileInfo>
#include <QFileInfoList>
#include <QFileSystemWatcher>

#include <QtConcurrent>
#include <QFuture>

#include <QSet>
#include <QString>

#include <QSqlError>
#include <QSqlQuery>

#include <QtGlobal>
#include <QVector>
/*
 * 图像文件放置在/IMAGE文件夹下
 * 输入用于pre-reconstruction的apo文件，请放置在/input/PREAPO文件夹下
 * 输入用于proof-read的swc文件，请放置在/input/PRESWC文件夹下
 * 上述IMAGE、PREAPO、PRESWC文件夹跟新后请重启服务器程序，程序将根据上述文件的变动维护数据库（防止读写冲突及其他方式下可能的死锁）黄磊
 */
extern QString IMAGE;//图像文件夹
extern QString PREAPO;//预重建的输入apo文件夹
extern QString PRESWC;//检查的swc输入文件夹
extern QString PRERESSWC;//预重建结果文件夹
extern QString PROOFSWC;//校验数据的文件夹
extern QString FULLSWC;//swc数据存放文件夹
extern QString BRAININFO;//放置brainInfo的文件夹

extern QString IMAGETABLENAME;//图像数据表
extern QString PRERETABLENAME;//预重建数据表
extern QString RESWCTABLENAME;//重建完成数据表
extern QString PROOFTABLENAME;//校验数据表
extern QString CHECKTABLENAME;//校验结果数据表
Server::Server(QObject *parent):QTcpServer(parent)
{
    qDebug()<<"Thread ID"<<QThread::currentThreadId();
    db=QSqlDatabase::addDatabase("QMYSQL","C3");
    db.setHostName("localhost");
    db.setUserName("root");
    db.setPassword("");

    if(!db.open()){
        qFatal("cannot connect DB");
        exit(-1);
    }else if(!initImage()){
        qFatal("cannot init IMAGE");
        exit(-1);
    }else if(!initPreApo()){
        qFatal("cannot init PREAPO");
        exit(-1);
    }else if(!initPreSwc()){
        qFatal("cannot init PRESWC");
        exit(-1);
    }else if(!initReswc()){
        qFatal("cannot init PROOF");
        exit(-1);
    }else if(!initCheck()){
        qFatal("cannot init CHECK");
        exit(-1);
    }
    qDebug()<<">---------init server sucess!---------<";
}
bool Server::initImage(){

    QDir imageDir(QCoreApplication::applicationDirPath()+"/"+IMAGE);
    QFileInfoList imageList=imageDir.entryInfoList(QDir::NoDotAndDotDot|QDir::Dirs);
    int maxRES=-1;
    for(auto & image:imageList){
        int resCNT;
        if((resCNT=QDir(image.absoluteFilePath()).entryInfoList(QDir::Dirs|QDir::NoDotAndDotDot).size())>maxRES){
            maxRES=resCNT;
        }
    }

    QSqlQuery query(db);
    {
        QString sql=QString("drop table if exists %1").arg(IMAGETABLENAME);
        if(!query.exec(sql)){
            qDebug()<<query.lastError().text();
            return false;
        }
    }

    {
        QString resOrder="";
        for(int i=1;i<=maxRES;i++)
        {
            resOrder+=QString(",RES%1 VARCHAR NOT NULL").arg(i);
        }
        QString sql=QString("CREATE TABLE %1 ("
                          "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                          "BrainId VARCHAR NOT NULL,"
                          "MainPath VARCHAR NOT NULL"
                          "%2)").arg(IMAGETABLENAME).arg(resOrder);
        if(!query.exec(sql)){
            qDebug()<<query.lastError().text();
            return false;
        }
    }

    QStringList imageNames;
    QStringList mainPathLists;
    QVector<QStringList> resLists(maxRES);
    for(auto &image:imageList){
        imageNames.push_back(image.fileName());
        mainPathLists.push_back(image.absoluteFilePath());
        QStringList resList=QDir(image.absoluteFilePath()).entryList(QDir::NoDotAndDotDot|QDir::Dirs,QDir::Name);
        sort(resList.begin(),resList.end(),
             [](QString A,QString B)->bool{
            QRegExp regExpA("RES\((.*)x(.*)x(.*))");
            QRegExp regExpB("RES\((.*)x(.*)x(.*))");
            if(regExpA.indexIn(A)!=-1&&regExpB.indexIn(B)!=-1){
                int resZA=regExpA.cap(1).toInt();
                int resZB=regExpB.cap(1).toInt();
                if(resZA>resZB){
                    return true;
                }else {
                    return false;
                }
            }
            else{
                return false;
            }
        });
       for(int i=0;i<resList.size();i++){
           resLists[i].push_back(resList[i]);
       }
       for(int i=resList.size();i<maxRES;i++)
       {
           resLists[i].push_back("");
       }
    }

    {
        QString partOrder1="BrainId MainPath";
        QString partOrder2="?,?";
        for(int i=1;i<=maxRES;i++)
        {
            partOrder1+=QString(" RES%1").arg(i);
            partOrder2+=",?";
        }
        QString sql=QString("INSERT INTO %1 (%2) VALUES (%3))").arg(IMAGETABLENAME).arg(partOrder1).arg(partOrder2);
        query.prepare(sql);
        query.addBindValue(imageNames);
        query.addBindValue(mainPathLists);
        for(int i=0;i<maxRES;i++)
            query.addBindValue(resLists[i]);
        if(!query.execBatch()){

            qDebug()<<query.lastError().text();
            return false;
        }
    }
    return true;
}
bool Server::initPreApo(){
    QSqlQuery query(db);
    {
        QString order="id INTEGER PRIMARY KEY AUTOINCREMENT,"
                      "Neuron_id VARCHAR NOT NULL,"
                      "Brain_id VARCHAR NOT NULL,"
                      "Tag VARCHAR NOT NULL,"
                      "Time0 VARCHAR NOT NULL,"
                      "Time1 VARCHAR NOT NULL,"
                      "Soma_position VARCHAR NOT NULL,"
                      "Pre_Swc VARCHAR NOT NULL,"
                      "User VARCHAR NOT NULL";
        QString sql=QString("CREATE TABLE (%1) IF NOT EXISTS %2").arg(order).arg(PRERETABLENAME);
        if(!query.exec(sql)){
            qDebug()<<query.lastError().text();
            return false;
        }
    }

    QStringList neuron_ids;
    QStringList brain_ids;
    QStringList tags;
    QStringList time0s;
    QStringList time1s;
    QStringList somaPositions;
    QStringList preSWCs;
    QStringList users;

    {
        QFileInfoList apoList=QDir(QCoreApplication::applicationDirPath()+PREAPO).entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
        //    query.prepare("INSERT OR IGNORE INTO Swc (Neuron_id,Brain_id,Tag,Time0,Soma_position) VALUES (?,?,?,?,?)");
        for(auto & apo:apoList){
            neuron_ids.push_back(apo.baseName());
            brain_ids.push_back(apo.baseName().left(apo.baseName().indexOf('_')).trimmed());
            tags.push_back("0");
            time0s.push_back(QDateTime::currentDateTime().toString("yyyy/MM/dd hh:mm:ss"));
            time1s.push_back("-1");
            {
                auto cords=readAPO_file(apo.absoluteFilePath());
                if(cords.size()!=1) continue;
                somaPositions.push_back(QString("%1_%2_%3").arg(int(cords[0].x)).arg(int(cords[0].y)).arg(int(cords[0].z)));
            }
            preSWCs.push_back("-1");
            users.push_back("-1");
            if(!QFile::remove(apo.absoluteFilePath())){
                qDebug()<<"failed to remove "<<apo.fileName();
            }
        }
    }

    {
        QString sql=QString("INSERT IGNORE INTO %1 (Neuron_id,Brain_id,Tag,Time0,Time1,Soma_position,Pre_Swc,User) VALUES (?,?,?,?,?,?,?,?)");
        query.prepare(sql);
        query.addBindValue(neuron_ids);
        query.addBindValue(brain_ids);
        query.addBindValue(tags);
        query.addBindValue(time0s);
        query.addBindValue(time1s);
        query.addBindValue(somaPositions);
        query.addBindValue(preSWCs);
        query.addBindValue(users);
        if(!query.execBatch()){
            return false;
        }
    }
    return true;
}
bool Server::initPreSwc(){
    QSqlQuery query(db);

    {
        //create table Swc
        QString  order="id INTEGER PRIMARY KEY AUTOINCREMENT,"
                       "name VARCHAR NOT NULL,"
                       "Neuron_id VARCHAR NOT NULL,"
                       "Brain_id VARCHAR NOT NULL,"
                       "Arbor_Position VARCHAR NOT NULL,"
                       "Tag VARCHAR NOT NULL,"
                       "MAINPATH VARCHAR NOT NULL";
        QString sql=QString("CREATE TABLE (%1) IF NOT EXISTS %2").arg(order).arg(PROOFTABLENAME);
        if(!query.exec(sql)){
            qDebug()<<query.lastError().text();
            return false;
        }
    }
//QString sql=QString("drop table if exists %1").arg(IMAGETABLENAME);
    QFileInfoList swcList=QDir(QCoreApplication::applicationDirPath()+PRESWC).entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
    QStringList future = QtConcurrent::blockingMapped(swcList,cac_pos);
    QStringList nameList;
    QStringList neuronList;
    QStringList brainList;
    QStringList positionList;
    QStringList tagList;
    QStringList swcPath;
    {
        for(QString & s:future){
            auto list=s.trimmed().split(";");
            nameList.push_back(list[0]);
            neuronList.push_back(list[0].left(list[0].lastIndexOf('_')));
            brainList.push_back(list[0].left(list[0].indexOf('_')));
            positionList.push_back(list[1]);
            tagList.push_back("0");
            swcPath.push_back(QCoreApplication::applicationDirPath()+"/data/"+PROOFSWC+"/"+list[0]);
        }

        QString sql="INSERT INTO %1 (name,Neuron_id,Brain_id,Arbor_Position,Tag,MAINPATH) VALUES (?,?,?,?,?,?)";
        query.prepare(sql);
        query.addBindValue(nameList);
        query.addBindValue(neuronList);
        query.addBindValue(brainList);
        query.addBindValue(positionList);
        query.addBindValue(tagList);
        query.addBindValue(swcPath);
        if(!query.execBatch()){
            return false;
        }
    }
    return true;
}

bool Server::initReswc()
{
    QSqlQuery query(db);
    {
        QString order="id INTEGER PRIMARY KEY AUTOINCREMENT,"
                      "Neuron_id VARCHAR NOT NULL,"
                      "Brain_id VARCHAR NOT NULL,"
                      "Ano VARCHAR NOT NULL,"
                      "APO VARCHAR NOT NULL,"
                      "SWC VARCHAR NOT NULL,"
                      "Fold VARCHAR NOT NULL,"
                      "Tag VARCHAR NOT NULL,"
                      "CelltypeRough VARCHAR NOT NULL,"
                      "Celltype VARCHAR NOT NULL";
        QString sql=QString("CREATE TABLE (%1) IF NOT EXISTS %2").arg(order).arg(RESWCTABLENAME);
        if(!query.exec(sql)){
            qDebug()<<query.lastError().text();
            return false;
        }else{
            return true;
        }
    }
    return false;
}

bool Server::initCheck()
{
    QSqlQuery query(db);
    {
        QString order="id INTEGER PRIMARY KEY AUTOINCREMENT,"
                      "name VARCHAR NOT NULL,"
                      "Neuron_id VARCHAR NOT NULL,"
                      "Brain_id VARCHAR NOT NULL,"
                      "Tag VARCHAR NOT NULL,"
                      "Time VARCHAR NOT NULL,"
                      "User VARCHAR NOT NULL";
        QString sql=QString("CREATE TABLE (%1) IF NOT EXISTS %2").arg(order).arg(CHECKTABLENAME);
        if(!query.exec(sql)){
            qDebug()<<query.lastError().text();
            return false;
        }else{
            return true;
        }
    }
    return false;
}
void Server::incomingConnection(qintptr handle)
{
    Socket* socket=new Socket(handle);
    qDebug()<<handle<<" connected.";
    socket->start();
}

//bool Server::isTableExist(QString tableName){

//    QSqlQuery query;
//    query.exec(QString("select count(*) from sqlite_master where type='table' and name='%1'").arg(tableName));
//    if(query.next())
//    {
//        if(query.value(0).toInt()==0)
//        {
//            return false;
//          // 表不存在
//        }else{
//            return true;
//            //表存在
//        }
//    }
//    return false;
//}

