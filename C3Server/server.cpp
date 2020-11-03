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

extern QString IMAGE;//图像文件夹
extern QString PREAPO;//预重建的输入apo文件夹
extern QString PROSWC;//检查的swc输入文件夹
extern QString FULLSWC;//swc数据存放文件夹
Server::Server(QObject *parent):QTcpServer(parent)
{
    qDebug()<<"Thread ID"<<QThread::currentThreadId();
    db=QSqlDatabase::addDatabase("QSQLITE","C3");
    if(!imageInit()){
        qFatal("cannot init image,please check image data");
    }


    fileWatcher.addPath(QCoreApplication::applicationDirPath()+"/"+IMAGE);
    fileWatcher.addPath(QCoreApplication::applicationDirPath()+"/input/"+PREAPO);
    fileWatcher.addPath(QCoreApplication::applicationDirPath()+"/input/"+PROSWC);
    connect(&fileWatcher,SIGNAL(directoryChanged(const QString &)),this,SLOT(directoryUpdated(const QString &)));
}

void Server::directoryUpdated(const QString &path){
    if(path.contains(IMAGE)){
        if(!imageChanged()){
            exit(-1);
        }
    }else if(path.contains(PREAPO)){
        if(!apoChanged()){
            exit(-1);
        }
    }else if(path.contains(PROSWC)){
        if(!swcChanged()){
            exit(-1);
        }
    }
}

bool Server::swcChanged(){
    if(isTableExist("Swc")){
         //create table Swc
        QSqlQuery query(db);
        if(!query.exec("CREATE TABLE Swc ("
                       "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                       "Neuron_id VARCHAR,"
                       "Brain_id VARCHAR,"
                       "Tag VARCHAR,"
                       "Time0 VARCHAR,"
                       "Time1 VARCHAR,"
                       "Soma_position VARCHAR,"
                       "Pre_Swc VARCHAR,"
                       "Re_Swc VARCHAR,"
                       "ArborN VARCHAR)")
                ){
            qDebug()<<query.lastError().text();
            return false;
        }
    }

    QFileInfoList swcList=QDir(QCoreApplication::applicationDirPath()+"/input/"+PROSWC).entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
    QStringList future = QtConcurrent::blockingMapped(swcList,cac_pos);
    QStringList neuron_ids;
    QStringList brain_ids;
    QStringList tags;
    QStringList arborN;
    QStringList reSWCs;
    for(QString & s:future){
        auto list=s.split(";");
        neuron_ids.push_back(list[0]);
        brain_ids.push_back(list[0].split("_")[0]);
        tags.push_back("3");
        arborN.push_back(list[1]);
        reSWCs.push_back(QCoreApplication::applicationDirPath()+"/"+FULLSWC+"/"+list[0]+".swc");
    }
    QSqlQuery query(db);
    query.prepare("REPLACE INTO Swc (Neuron_id,Brain_id,Tag,Re_Swc,ArborN) VALUES (?,?,?,?,?)");
    query.addBindValue(neuron_ids);
    query.addBindValue(brain_ids);
    query.addBindValue(tags);
    query.addBindValue(arborN);
    query.addBindValue(reSWCs);
    if(!query.execBatch()){
        return false;
    }
    return true;
}

bool  Server::imageChanged()
{
    QStringList newList=QDir(QCoreApplication::applicationDirPath()+"/"+IMAGE).entryList(QDir::NoDotAndDotDot|QDir::Dirs);
    QSqlQuery query(db);
    if(!query.exec("SELECT name FROM ImageRes")){
        return false;
    }
    QStringList oldList;
    while (query.next()) {
        oldList.push_back(query.value(0).toString());
    }
    QSet<QString> delSet=QSet<QString>::fromList(oldList)-QSet<QString>::fromList(newList);
    QSet<QString> addSet=QSet<QString>::fromList(newList)-QSet<QString>::fromList(oldList);
    query.prepare("DELETE FROM ImageRes WHERE name = ?");
    query.addBindValue(QStringList(delSet.begin(),delSet.end()));
    if(!query.execBatch()){
        return false;
    }

    QStringList imageNames;
    std::array<QStringList,6> resLists;
    for(auto & imageName:addSet){
        QFileInfo info=QFileInfo(QCoreApplication::applicationDirPath()+"/"+IMAGE+"/"+imageName);
        imageNames.push_back(info.fileName());
        QStringList resList=QDir(info.absoluteFilePath()).entryList(QDir::NoDotAndDotDot|QDir::Dirs);
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
            }else{
                qDebug()<<QString("error:cannot compare %1 %2").arg(A).arg(B);
                exit(-1);
            }
        });
    }
    query.prepare("INSERT INTO ImageRes (name,RES1,RES2,RES2,RES3,RES4,RES5,RES6) VALUES (?,?,?,?,?,?,?)");
    query.addBindValue(imageNames);
    for(auto & list:resLists){
        query.addBindValue(list);
    }
    if(!query.execBatch()){

        qDebug()<<query.lastError().text();
        return false;
    }
    return true;
}

bool Server::apoChanged(){
    if(isTableExist("Swc")){
         //create table Swc
        QSqlQuery query(db);
        if(!query.exec("CREATE TABLE Swc ("
                       "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                       "Neuron_id VARCHAR,"
                       "Brain_id VARCHAR,"
                       "Tag VARCHAR,"
                       "Time0 VARCHAR,"
                       "Time1 VARCHAR,"
                       "Soma_position VARCHAR,"
                       "Pre_Swc VARCHAR,"
                       "Re_Swc VARCHAR,"
                       "ArborN VARCHAR)")
                ){
            qDebug()<<query.lastError().text();
            return false;
        }
    }
    QFileInfoList apoList=QDir(QCoreApplication::applicationDirPath()+"/input/"+PREAPO).entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
    QSqlQuery query(db);
    query.prepare("INSERT OR IGNORE INTO Swc (Neuron_id,Brain_id,Tag,Time0,Soma_position) VALUES (?,?,?,?,?)");
    QStringList neuron_ids;
    QStringList brain_ids;
    QStringList tags;
    QStringList time0s;
    QStringList somaPositions;
    for(auto & apo:apoList){
        neuron_ids.push_back(apo.baseName());
        brain_ids.push_back(apo.baseName().split('_')[0]);
        tags.push_back("0");
        time0s.push_back(QDateTime::currentDateTime().toString("yyyy/MM/dd hh:mm:ss"));
        auto cords=readAPO_file(apo.absoluteFilePath());
        somaPositions.push_back(QString("%1_%2_%3").arg(int(cords[0].x)).arg(int(cords[0].y)).arg(int(cords[0].z)));
    }
    query.addBindValue(neuron_ids);
    query.addBindValue(brain_ids);
    query.addBindValue(tags);
    query.addBindValue(time0s);
    query.addBindValue(somaPositions);
    if(!query.execBatch()){
        return false;
    }
    for(auto & apo:apoList){
        if(!QFile::remove(apo.absoluteFilePath())){
            qDebug()<<"failed to remove "<<apo.fileName();
        }
    }
    return true;

}
void Server::incomingConnection(qintptr handle)
{
    Socket* socket=new Socket(handle);
    qDebug()<<handle<<" connected.";
    socket->start();
}


bool Server::imageInit(){
    QDir imageDir(QCoreApplication::applicationDirPath()+"/"+IMAGE);
    QFileInfoList imageList=imageDir.entryInfoList(QDir::NoDotAndDotDot|QDir::Dirs);
    QStringList imageNames;
    std::array<QStringList,6> resLists;
    for(auto &image:imageList){
        imageNames.push_back(image.fileName());
        QStringList resList=QDir(image.absoluteFilePath()).entryList(QDir::NoDotAndDotDot|QDir::Dirs);
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
            }else{
                qDebug()<<QString("error:cannot compare %1 %2").arg(A).arg(B);
                exit(-1);
            }
        });
       for(int i=0;i<resList.size();i++){
           resLists[i].push_back(resList[i]);
       }
    }

    QSqlQuery query(db);
    if(!query.exec("drop table if exists ImageRes")){
        qDebug()<<query.lastError().text();
        return false;
    }

    if(!query.exec("CREATE TABLE ImageRes ("
                   "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   "name VARCHAR,"
                   "RES1 VARCHAR,"
                   "RES2 VARCHAR,"
                   "RES3 VARCHAR,"
                   "RES4 VARCHAR,"
                   "RES5 VARCHAR,"
                   "RES6 VARCHAR)")
            ){
        qDebug()<<query.lastError().text();
        return false;
    }
    query.prepare("INSERT INTO ImageRes (name,RES1,RES2,RES2,RES3,RES4,RES5,RES6) VALUES (?,?,?,?,?,?,?)");
    query.addBindValue(imageNames);
    for(auto & list:resLists){
        query.addBindValue(list);
    }
    if(!query.execBatch()){

        qDebug()<<query.lastError().text();
        return false;
    }
    return true;
}



bool Server::isTableExist(QString tableName){

    QSqlQuery query;
    query.exec(QString("select count(*) from sqlite_master where type='table' and name='%1'").arg(tableName));
    if(query.next())
    {
        if(query.value(0).toInt()==0)
        {
            return false;
          // 表不存在
        }else{
            return true;
            //表存在
        }
    }
}

