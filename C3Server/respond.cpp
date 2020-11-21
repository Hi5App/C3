#include "respond.h"
#include <QSqlDatabase>
#include <QCoreApplication>
#include <QDir>
#include <QProcess>
#include <basic_c_fun/basic_surf_objs.h>
#include <neuron_editing/v_neuronswc.h>
#include <neuron_editing/neuron_format_converter.h>
#include <QtConcurrent>
#include <QMutex>
#include <QFile>
extern QString TableForImage;//图像数据表
extern QString TableForPreReConstruct;//预重建数据表
extern QString TableForFullSwc;//重建完成数据表
extern QString TableForProof;//校验数据表
extern QString TableForCheckResult;//校验结果数据表

extern QString IMAGE;//图像文件夹
extern QString InApoPreReconstruct;//输入：用于预重建的apo
extern QString InSwcFull;//输入：完全重建的swc
extern QString InSwcProof;//输入：检查的swc
extern QString ResultPreReConstruct;//结果：预重建的swc
extern QString ResultForProofSwc;//结果：检查的swc
extern QString ResultFullSwc;

extern QStringList dbParas;
extern QString vaa3dPath;
const int columns=2;//columns brfore res1 in TableImage

namespace Respond {

int index=0;
QMutex mutex;
QSqlDatabase getDataBase()
{
    mutex.lock();
    index++;
    mutex.unlock();
    QSqlDatabase db=QSqlDatabase::addDatabase("QMYSQL",QString::number(index));
    db.setDatabaseName(dbParas[0]);
    db.setHostName(dbParas[1]);
    db.setUserName(dbParas[2]);
    db.setPassword(dbParas[3]);
    return db;
}

QString  getBrainList(const QString type)
{
    QSqlDatabase db=Respond::getDataBase();
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return "";
    }

    QSqlQuery query(db);
    QString sql;
    if(type=="0"){
        //0:预重建用
        sql=QString("SELECT Brain_id FROM %1 WHERE tag = ? ORDER BY Brain_id").arg(TableForPreReConstruct);
    }else if(type=="1"){
        sql=QString("SELECT Brain_id FROM %1 WHERE tag = ? ORDER BY Brain_id").arg(TableForProof);
    }

    query.prepare(sql);
    query.addBindValue("0");
    QSet<QString> brains;
    if(query.exec()){
        while(query.next()){
            brains.insert(query.value(0).toString());
        }
    }
    return brains.values().join("_");
}
QStringList nextAvailableNeuron(QString brainId,bool preOrProof,qintptr handle)
{
    /*
     * 返回：文件名，文件路径
     */
    QSqlDatabase db=Respond::getDataBase();
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return {""};
    }

    QSqlQuery query(db);
    QString sql;
    if(preOrProof){
        sql=QString("SELECT Neuron_id,Soma_position FROM %1 WHERE tag = ? and Brain_id = ? ORDER BY Neuron_id").arg(TableForPreReConstruct);
    }else{
        sql=QString("SELECT Name,Arbor_Position FROM %1 WHERE tag = ? and Brain_id = ? ORDER BY Neuron_id").arg(TableForProof);
    }
    query.prepare(sql);
    query.addBindValue("0");
    query.addBindValue(brainId);
    if(query.exec()){
        if(query.next()){
            QStringList list;
            list.push_back(brainId);
            list.push_back("1");
            list.push_back(query.value(1).toString().split("_").join(";"));
            list.push_back("128");
            list.push_back(QString::number(handle));
            return getImageBlock(list.join(";"),query.value(0).toString());
        }
    }
    return {};
}
QString getAllNeuronList(QString brainId,bool preOrProof)
{
    QSqlDatabase db=Respond::getDataBase();
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return "";
    }

    QSqlQuery query(db);
    QString sql;
    if(preOrProof){
        sql=QString("SELECT Neuron_id,Tag,Soma_position FROM %1 WHERE Brain_id = ? ORDER BY Neuron_id").arg(TableForPreReConstruct);
    }else{
        sql=QString("SELECT Name,Tag,Arbor_Position FROM %1 WHERE Brain_id = ? ORDER BY Neuron_id").arg(TableForProof);
    }
    query.prepare(sql);
    query.addBindValue(brainId);
    QStringList result;
    if(query.exec()){
        while(query.next()){
            QStringList list;
            list.push_back(query.value(0).toString());
            list.push_back(query.value(1).toString());
            list.push_back(query.value(2).toString());
            result.push_back(list.join(";"));
        }
    }
    return result.join("/");
}
QStringList getImageBlock(QString msg,QString fromNext)
{
    qDebug()<<msg;
    /*
     * p1:brain_id;res;x;y;z;size;socket.descriptor
     * p2:Neuron_id/name
     * 返回：文件名，文件路径
     */
    QStringList paraList=msg.split(";",QString::SkipEmptyParts);
    QString brain_id=paraList.at(0).trimmed();//1. tf name/RES  2. .v3draw// test:17302;RES;x;y;z;b
    //0: 18465/RESx18000x13000x5150
    //1: 12520
    //2: 7000
    //3: 2916
    int res=paraList.at(1).toInt();//>0
    int xpos=paraList.at(2).toInt();
    int ypos=paraList.at(3).toInt();
    int zpos=paraList.at(4).toInt();
    int blocksize=paraList.at(5).toInt();

    dirCheck("tmp");
    QString apoName=QCoreApplication::applicationDirPath()+"/tmp/"+paraList[6]+"__"+brain_id+"__"
                  + QString::number(xpos)+ "__"
                  + QString::number(ypos)+ "__"
                  + QString::number(zpos)+ "__"
                  + QString::number(blocksize)+"__"
                  + QString::number(blocksize)+ "__"
                  + QString::number(blocksize);


    {
        CellAPO centerAPO;
        centerAPO.x=xpos;centerAPO.y=ypos;centerAPO.z=zpos;
        QList <CellAPO> List_APO_Write;
        List_APO_Write.push_back(centerAPO);
        if(!writeAPO_file(apoName+".apo",List_APO_Write))
        {
            qDebug()<<"fail to write apo";
            return {};//get .apo to get .v3draw
        }
    }

    QString namepart1;
    if(fromNext.isEmpty())
        namepart1=paraList[6]+"_"+brain_id+"_"+QString::number(blocksize)+"_";
    else
        namepart1=paraList[6]+"_"+fromNext+"_"+QString::number(blocksize)+"_";
    //sockeddes_(brian_id(neuron_id/name))_size_

    QString filepath;
    {
        QSqlDatabase db=Respond::getDataBase();
        if(!db.open()){
            qFatal("cannot connect DB when processBrain");
            return {""};
        }
        QSqlQuery query(db);
        QString sql;
        sql=QString("SELECT * FROM %1 WHERE Brainid = ? ").arg(TableForImage);
        query.prepare(sql);
        query.addBindValue(brain_id);
        if(query.exec())
        {
            if(query.next()){
                filepath=query.value(columns-1).toString()+"/"+query.value(columns+res).toString();
            }
        }
    }
    if(filepath.isEmpty()||!QFile(filepath).exists())
    {
        return {};
    }

    QString order =QString("xvfb-run -d %0/vaa3d -x %0/plugins/image_geometry/crop3d_image_series/libcropped3DImageSeries.so "
                            "-f cropTerafly -i %1/ %2.apo %3/tmp/%4 -p %5 %5 %5")
            .arg(vaa3dPath)
            .arg(filepath).arg(apoName).arg(QCoreApplication::applicationDirPath()).arg(namepart1).arg(blocksize);
    qDebug()<<"order="<<order;
    auto p=getProcess();
    if(p->execute(order.toStdString().c_str())!=-1||p->execute(order.toStdString().c_str())!=-2)
    {
        QFile f1(apoName+".apo"); qDebug()<<f1.remove();
        QString fName=namepart1+QString("%1.000_%2.000_%3.000.v3dpbd").arg(xpos).arg(ypos).arg(zpos);
        qDebug()<<fName<<"*************";releaseProcess(p);
        return {fName,QCoreApplication::applicationDirPath()+"/tmp/"+fName};
    }else
    {
        releaseProcess(p);
        return {};
    }
}
QStringList getSwcInBlock(const QString msg)
{
    QRegExp tmp("(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)");
    int n=5;//重复5次，每次延时2S
    if(tmp.indexIn(msg)!=-1)
    {
        QString name=tmp.cap(1).trimmed();
        int x1=tmp.cap(2).toInt();
        int x2=tmp.cap(3).toInt();
        int y1=tmp.cap(4).toInt();
        int y2=tmp.cap(5).toInt();
        int z1=tmp.cap(6).toInt();
        int z2=tmp.cap(7).toInt();
        int cnt=tmp.cap(8).toInt();
        cnt=pow(2,cnt-1);
        //根据名称查询相关的数据库-> 找到swc的路径
        QString filepath;
        {
            QSqlDatabase db=Respond::getDataBase();
            if(!db.open()){
                qFatal("cannot connect DB when processBrain");
                return {};
            }

            QSqlQuery query(db);
            QString sql;

            if(name.count('_')==1){
                sql=QString("SELECT Pre_Swc FROM %1 WHERE Neuron_id = ? ").arg(TableForPreReConstruct);
            }else if(name.count('_')==2){
                sql=QString("SELECT Swc FROM %1 WHERE Name = ? ").arg(TableForProof);
            }else{
                return {};
            }
            query.prepare(sql);
            query.addBindValue(name);
            if(query.exec())
            {
                if(query.next()){
                    filepath=query.value(0).toString();
                }
            }
        }

        dirCheck("tmp");

        QString BBSWCNAME=QCoreApplication::applicationDirPath()+"/tmp/blockGet__"+name+QString("__%1__%2__%3__%4__%5__%6__%7.swc")
                .arg(x1).arg(x2).arg(y1).arg(y2).arg(z1).arg(z2).arg(cnt);

        NeuronTree nt;
        if(filepath=="0")
        {
            writeSWC_file(BBSWCNAME,nt);
            return {BBSWCNAME.right(BBSWCNAME.size()-BBSWCNAME.lastIndexOf('/')),BBSWCNAME};
        }
        x1*=cnt;x2*=cnt;y1*=cnt;y2*=cnt;z1*=cnt;z2*=cnt;
        __START:

        --n;
        qDebug()<<"Get SWC in BB:"<<5-n;

        nt=readSWC_file(filepath);
        if(nt.flag!=false)
        {
            V_NeuronSWC_list testVNL=NeuronTree__2__V_NeuronSWC_list(nt);
            V_NeuronSWC_list tosave;
            for(int i=0;i<testVNL.seg.size();i++)
            {
                NeuronTree SS;
                V_NeuronSWC seg_temp =  testVNL.seg.at(i);
                seg_temp.reverse();
                for(int j=0;j<seg_temp.row.size();j++)
                {
                    if(seg_temp.row.at(j).x>=x1&&seg_temp.row.at(j).x<=x2
                            &&seg_temp.row.at(j).y>=y1&&seg_temp.row.at(j).y<=y2
                            &&seg_temp.row.at(j).z>=z1&&seg_temp.row.at(j).z<=z2)
                    {
                        tosave.seg.push_back(seg_temp);
                        break;
                    }
                }
            }
            qDebug()<<"get nt size:"<<tosave.seg.size();
            nt=V_NeuronSWC_list__2__NeuronTree(tosave);
            for(int i=0;i<nt.listNeuron.size();i++)
            {
                (nt.listNeuron[i].x-=x1)/=cnt;
                (nt.listNeuron[i].y-=y1)/=cnt;
                (nt.listNeuron[i].z-=z1)/=cnt;
            }
//            nt = resample(nt, cnt);
            writeSWC_file(BBSWCNAME,nt);
            return {BBSWCNAME.right(BBSWCNAME.size()-BBSWCNAME.lastIndexOf('/')),BBSWCNAME};
        }
        else
        {
            if(!n)
            {
                qDebug()<<"error:"<<msg<<" failed 5 times to get SWC IN BB:"<<msg;
                goto __ERROR;
            }else
            {

                QElapsedTimer t;
                t.start();
                while(t.elapsed()<2000);
                goto __START;
            }
        }
    }else
    {
        qDebug()<<"error:"<<msg<<" does not match tmp";
    }
    __ERROR:
    return {};
}

bool recordCheck(QString msg)
{
    QRegExp tmp("(.*);(.*);(.*)");//17302_00001_00001;flag;id;
    if(tmp.indexIn(msg)!=-1)
    {
        QString filename=tmp.cap(1).trimmed();
        {
            QSqlDatabase db=Respond::getDataBase();
            if(!db.open()){
                qFatal("cannot connect DB when processBrain");
                return false;
            }

            QSqlQuery query(db);
            QString sql;

            sql=QString("INSERT INTO %1 (Name,Tag,User,Time) VALUES (?,?,?,?)"
                                ).arg(TableForCheckResult);
            query.prepare(sql);
            query.addBindValue(filename);
            query.addBindValue(tmp.cap(2));
            query.addBindValue(tmp.cap(3));
            query.addBindValue(QDateTime::currentDateTime().toString("yyyy/MM/dd hh:mm:ss"));

            if(query.exec())
            {
                sql=QString("UPDATE %1 SET Tag = %2 WHERE Name = ? ")
                        .arg(TableForProof).arg(QString::number(1));
                query.prepare(sql);
                query.addBindValue(filename);
                if(query.exec()){
                   return true;
                }
            }
        }
    }
    return false;
}

QString getResCnt(QString paraString)
{
    QSqlDatabase db=Respond::getDataBase();
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return "";
    }

    QSqlQuery query(db);
    QString sql;
    sql=QString("SELECT * FROM %1 WHERE Brainid = ?").arg(TableForImage);
    query.prepare(sql);
    query.addBindValue(paraString);
    if(query.exec()&&query.next()){
        int cnt=query.value(columns).toString().toInt();
        QStringList result;

        for(int i=0;i<cnt+1;i++)
        {
            result.push_back(query.value(columns+i).toString());
        }
        return result.join(';');

    }else
    {
        return "0";
    }

}
bool setSwcInBlock(QString filePath,QString fileName)
{

    QRegExp blockSetRex("blockSet__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*).swc");
    if(blockSetRex.indexIn(fileName)==-1)
    {
        return false;
    }
    QString name=blockSetRex.cap(1).trimmed();
    int x1=blockSetRex.cap(2).toInt();
    int x2=blockSetRex.cap(3).toInt();
    int y1=blockSetRex.cap(4).toInt();
    int y2=blockSetRex.cap(5).toInt();
    int z1=blockSetRex.cap(6).toInt();
    int z2=blockSetRex.cap(7).toInt();
    int cnt=blockSetRex.cap(8).toInt();
    cnt=pow(2,cnt-1);
    V_NeuronSWC_list testVNL1;
    {
        NeuronTree nt=readSWC_file(filePath);
        for(int i=0;i<nt.listNeuron.size();i++)
        {
            (nt.listNeuron[i].x+=x1)*=cnt;
            (nt.listNeuron[i].y+=y1)*=cnt;
            (nt.listNeuron[i].z+=z1)*=cnt;
        }
        testVNL1=NeuronTree__2__V_NeuronSWC_list(nt);
    }

    QSqlDatabase db=Respond::getDataBase();
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return {};
    }
    QSqlQuery query(db);
    QString sql=QString("SELECT Pre_Swc FROM %1 WHERE Neuron_id = ?").arg(TableForPreReConstruct);
    query.prepare(sql);
    query.addBindValue(name);
    if(query.exec()&&query.next()){
        if(query.value(0).toString()!="0"){
            filePath=query.value(0).toString();
        }else{
            filePath=ResultPreReConstruct+"/"+name+".swc";
            writeSWC_file(filePath,NeuronTree());
        }

        {
            V_NeuronSWC_list testVNL;testVNL.seg.clear();
            V_NeuronSWC_list resVNL;resVNL.seg.clear();
            int n=5;
            __START:
            --n;
            NeuronTree nt=readSWC_file(filePath);
            if(nt.flag==false)
            {
                if(n)
                {
                    QElapsedTimer t;
                    t.start();
                    while(t.elapsed()<2000);
                    goto __START;
                }else
                {
                    qDebug()<<"FATAL:can not set SWC "<<name<<" call coder to check";
                    return false;
                }
            }

            testVNL=NeuronTree__2__V_NeuronSWC_list(nt);
            x1*=cnt;x2*=cnt;y1*=cnt;y2*=cnt;z1*=cnt;z2*=cnt;
            for(int i=0;i<testVNL.seg.size();i++)
            {
                testVNL.seg[i].to_be_deleted=0;
                V_NeuronSWC seg_temp =  testVNL.seg.at(i);
                seg_temp.reverse();
                for(int j=0;j<seg_temp.row.size();j++)
                {
                    if(seg_temp.row.at(j).x>=x1&&seg_temp.row.at(j).x<=x2
                            &&seg_temp.row.at(j).y>=y1&&seg_temp.row.at(j).y<=y2
                            &&seg_temp.row.at(j).z>=z1&&seg_temp.row.at(j).z<=z2)
                    {
                        testVNL.seg[i].to_be_deleted=1;
                        break;
                    }
                }
            }
            qDebug()<<"before remove nt number :"<<testVNL.seg.size();
            for(int i=0;i<testVNL.seg.size();i++)
            {
                if(testVNL.seg[i].to_be_deleted==0)
                    resVNL.seg.push_back(testVNL.seg.at(i));
            }
            qDebug()<<"after remove nt number :"<<resVNL.seg.size();
            for(int i=0;i<testVNL1.seg.size();i++)
            {
                resVNL.seg.push_back(testVNL1.seg.at(i));
            }
            nt=V_NeuronSWC_list__2__NeuronTree(resVNL);
            while(!writeESWC_file(filePath,nt))
            {
                QElapsedTimer t;
                t.start();
                while(t.elapsed()<2000);
            }

            sql=QString("UPDATE %1 SET Pre_Swc = ?,Tag = ?,Time1 = ?,User = ? WHERE Neuron_id = ?").arg(TableForPreReConstruct);
            query.prepare(sql);
            query.addBindValue(filePath);
            if(nt.listNeuron.size()!=0)
                query.addBindValue("2");
            else
                query.addBindValue("1");
            query.addBindValue(QDateTime::currentDateTime().toString("yyyy/MM/dd hh:mm:ss"));
            query.addBindValue(blockSetRex.cap(8).trimmed());
            query.addBindValue(name);

            if(!query.exec()){
                qDebug()<<query.lastError();
                return false;
            }
            return true;
        }

    }
}

bool initDB()
{
    if(!initTableForImage()) return false;

    QSqlDatabase db=Respond::getDataBase();
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return false;
    }
    QSqlQuery query(db);
    {
        QString order="Neuron_id VARCHAR(100) NOT NULL PRIMARY KEY,"
                      "Brain_id VARCHAR(100) NOT NULL,"
                      "Tag VARCHAR(100) NOT NULL,"
                      "Time0 VARCHAR(100) NOT NULL,"
                      "Time1 VARCHAR(100) NOT NULL,"
                      "Soma_position VARCHAR(200) NOT NULL,"
                      "Pre_Swc VARCHAR(1000) NOT NULL,"
                      "User VARCHAR(50) NOT NULL";
        QString sql=QString("CREATE TABLE IF NOT EXISTS %1 (%2)").arg(TableForPreReConstruct).arg(order);
        //qDebug()<<sql;
        if(!query.exec(sql)){
            qDebug()<<query.lastError().text();
            return false;
        }
    }
    {
        QString order="Neuron_id VARCHAR(100) NOT NULL PRIMARY KEY,"
                      "Brain_id VARCHAR(100) NOT NULL,"
                      "Ano VARCHAR(1000) NOT NULL,"
                      "Apo VARCHAR(1000) NOT NULL,"
                      "Swc VARCHAR(1000) NOT NULL,"
                      "Fold VARCHAR(1000) NOT NULL,"
                      "Tag VARCHAR(40) NOT NULL,"
                      "CelltypeRough VARCHAR(100) NOT NULL,"
                      "Celltype VARCHAR(100) NOT NULL";
        QString sql=QString("CREATE TABLE IF NOT EXISTS %1 (%2)").arg(TableForFullSwc).arg(order);
        //qDebug()<<sql;
        if(!query.exec(sql)){
            qDebug()<<query.lastError().text();
            return false;
        }
    }
    {
        QString  order="Name VARCHAR(100) NOT NULL PRIMARY KEY,"
                       "Neuron_id VARCHAR(100) NOT NULL,"
                       "Brain_id VARCHAR(100) NOT NULL,"
                       "Arbor_Position VARCHAR(200) NOT NULL,"
                       "Tag VARCHAR(40) NOT NULL,"
                       "Swc VARCHAR(1000) NOT NULL";
        QString sql=QString("CREATE TABLE IF NOT EXISTS %1 (%2)").arg(TableForProof).arg(order);
        if(!query.exec(sql)){
            qDebug()<<query.lastError().text();
            return false;
        }
    }
    {
        QString order="id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                      "Name VARCHAR(100) NOT NULL,"
                      "Tag VARCHAR(40) NOT NULL,"
                      "Time VARCHAR(100) NOT NULL,"
                      "User VARCHAR(40) NOT NULL";
        QString sql=QString("CREATE TABLE IF NOT EXISTS %1 (%2)").arg(TableForCheckResult).arg(order);
        //qDebug()<<sql;
        if(!query.exec(sql)){
            qDebug()<<query.lastError().text();
            return false;
        }
    }
    return true;


}
}
bool initTableForImage()
{
    QDir imageDir(IMAGE);
//    qDebug()<<imageDir.absolutePath();
    QFileInfoList imageList=imageDir.entryInfoList(QDir::NoDotAndDotDot|QDir::Dirs);
    int maxRES=-1;
    for(auto & image:imageList){
        int resCNT;
        if((resCNT=QDir(image.absoluteFilePath()).entryInfoList(QDir::Dirs|QDir::NoDotAndDotDot).size())>maxRES){
            maxRES=resCNT;
        }
    }

    QSqlDatabase db=Respond::getDataBase();
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return "";
    }
    QSqlQuery query(db);
    {
        QString sql=QString("drop table if exists %1").arg(TableForImage);
        //qDebug()<<sql;
        if(!query.exec(sql)){
            qDebug()<<query.lastError().text();
            return false;
        }
    }

    {
        QString resOrder="";
        for(int i=1;i<=maxRES;i++)
        {
            resOrder+=QString(",RES%1 VARCHAR(100) NOT NULL").arg(i);
        }
        QString sql=QString("CREATE TABLE %1 ("
                          "BrainId VARCHAR(100) NOT NULL PRIMARY KEY,"
                          "MainPath VARCHAR(500) NOT NULL,"
                          "ResCnt VARCHAR(20) NOT NULL"
                          "%2)").arg(TableForImage).arg(resOrder);
        //qDebug()<<sql;
        if(!query.exec(sql)){
            qDebug()<<query.lastError().text();
            return false;
        }
    }

    QStringList imageNames;
    QStringList mainPathLists;
    QStringList NList;
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
        NList.push_back(QString::number(resList.count()));
       for(int i=0;i<resList.size();i++){
           resLists[i].push_back(resList[i]);
       }
       for(int i=resList.size();i<maxRES;i++)
       {
           resLists[i].push_back("");
       }
    }
//    qDebug()<<resLists;

    {
        QString partOrder1="BrainId,MainPath,ResCnt";
        QString partOrder2="?,?,?";
        for(int i=1;i<=maxRES;i++)
        {
            partOrder1+=QString(",RES%1").arg(i);
            partOrder2+=",?";
        }
        QString sql=QString("INSERT IGNORE INTO %1 (%2) VALUES (%3)").arg(TableForImage).arg(partOrder1).arg(partOrder2);
        //qDebug()<<sql;
        query.prepare(sql);
        query.addBindValue(imageNames);
        query.addBindValue(mainPathLists);
        query.addBindValue(NList);
        for(int i=0;i<maxRES;i++)
            query.addBindValue(resLists[i]);
        if(!query.execBatch()){

            qDebug()<<query.lastError().text();
            return false;
        }
    }
    return true;
}

bool apoForPreChanged()
{
    QStringList neuron_ids;
    QStringList brain_ids;
    QStringList tags;
    QStringList time0s;
    QStringList time1s;
    QStringList somaPositions;
    QStringList preSWCs;
    QStringList users;

    {
        QFileInfoList apoList=QDir(InApoPreReconstruct).entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
        for(auto & apo:apoList){
            neuron_ids.push_back(apo.baseName());
            brain_ids.push_back(apo.baseName().left(apo.baseName().indexOf('_')).trimmed());
            tags.push_back("0");
            time0s.push_back(QDateTime::currentDateTime().toString("yyyy/MM/dd hh:mm:ss"));
            time1s.push_back("0");
            {
                auto cords=readAPO_file(apo.absoluteFilePath());
                if(cords.size()!=1) continue;
                somaPositions.push_back(QString("%1_%2_%3").arg(int(cords[0].x)).arg(int(cords[0].y)).arg(int(cords[0].z)));
            }
            preSWCs.push_back("0");
            users.push_back("0");
            if(!QFile::remove(apo.absoluteFilePath())){
                qDebug()<<"failed to remove "<<apo.fileName();
            }
        }
    }

    {
        QSqlDatabase db=Respond::getDataBase();
        if(!db.open()){
            qFatal("cannot connect DB when processBrain");
            return "";
        }
        QSqlQuery query(db);
        QString sql=QString("INSERT IGNORE INTO %1 (Neuron_id,Brain_id,Tag,Time0,Time1,Soma_position,Pre_Swc,User) VALUES (?,?,?,?,?,?,?,?)").arg(TableForPreReConstruct);
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
            qDebug()<<query.lastError().text();
            return false;
        }
    }

    {
        QFileInfoList apoList=QDir(InApoPreReconstruct).entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
        for(auto & info:apoList){
            QFile(info.absoluteFilePath()).remove();
        }
    }
    return true;
}
bool fullSwcChanged()
{
    QSqlDatabase db=Respond::getDataBase();
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return "";
    }
    QSqlQuery query(db);
    return true;
}
bool arborChanged()
{
    QSqlDatabase db=Respond::getDataBase();
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return "";
    }
    QSqlQuery query(db);

    QFileInfoList swcList=QDir(InSwcProof).entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
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
            QFile f(InSwcProof+"/"+list[0]+".swc");
            if(!(f.rename(ResultForProofSwc+"/"+list[0]+".swc"))) {
                qDebug()<<InSwcProof+"/"+list[0]+".swc"<<f.errorString();
                continue;}
            nameList.push_back(list[0]);
            neuronList.push_back(list[0].left(list[0].lastIndexOf('_')));
            brainList.push_back(list[0].left(list[0].indexOf('_')));
            positionList.push_back(list[1]);
            tagList.push_back("0");
            swcPath.push_back(ResultForProofSwc+"/"+list[0]+".swc");
        }

        QString sql=QString("INSERT IGNORE INTO %1 (Name,Neuron_id,Brain_id,Arbor_Position,Tag,Swc) VALUES (?,?,?,?,?,?)"
                            ).arg(TableForProof);
        query.prepare(sql);
        query.addBindValue(nameList);
        query.addBindValue(neuronList);
        query.addBindValue(brainList);
        query.addBindValue(positionList);
        query.addBindValue(tagList);
        query.addBindValue(swcPath);
        if(!query.execBatch()){
            qDebug()<<query.lastError().text();
            return false;
        }
    }
    return true;
}


