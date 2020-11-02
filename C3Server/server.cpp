#include "server.h"
#include <QFileSystemWatcher>
#include <QFile>
#include <QFileInfoList>
#include <QFileInfo>
#include <QSet>
#include <QString>
#include <QtConcurrent>
#include <QFuture>
#include <QSqlQuery>
#include <array>
#include <QSqlError>
#include <QtGlobal>
extern QString IMAGE;
extern QString PREAPO;
Server::Server(QObject *parent):QTcpServer(parent)
{
    qDebug()<<"Thread ID"<<QThread::currentThreadId();
    db=QSqlDatabase::addDatabase("QSQLITE","C3");
    if(!imageInit()){
        qFatal("cannot init image,please check image data");
    }else if(!anoInit()){
        qFatal("cannot init ano,please check image data");
    }


    fileWatcher.addPath(QCoreApplication::applicationDirPath()+"/"+IMAGE);
    fileWatcher.addPath(QCoreApplication::applicationDirPath()+"/input/"+PREAPO);

    connect(&fileWatcher,SIGNAL(directoryChanged(const QString &)),this,SLOT(directoryUpdated(const QString &)));
}

void Server::directoryUpdated(const QString &path){
    if(path.contains(IMAGE)){
        if(!imageChanged()){
            exit(-1);
        }
    }else if(path.contains(PREAPO)){

    }
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
        QFileInfo info=(QCoreApplication::applicationDirPath()+"/"+IMAGE+"/"+imageName);
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
                   "RES1 VARCHAR"
                   "RES2 VARCHAR"
                   "RES3 VARCHAR"
                   "RES4 VARCHAR"
                   "RES5 VARCHAR"
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

bool Server::anoInit(){
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
Server::~Server()
{

}
