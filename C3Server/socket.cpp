#include "socket.h"
#include <QDataStream>
#include <QByteArray>
#include <QHostAddress>
#include <resampling.h>
#include <QtGlobal>
#include <QSqlDatabase>
#include <QSqlTableModel>
#include <QSqlError>
#include <QSqlQuery>
#include <QMultiMap>
//#define IMAGE "image" // where is image data
extern QString IMAGE;//图像文件夹
extern QString PREAPO;//预重建的输入apo文件夹
extern QString PRESWC;//检查的swc输入文件夹
extern QString PRERESSWC;//预重建结果文件夹
extern QString PROOFSWC;//校验数据的文件夹
extern QString FULLSWC;//swc数据存放文件夹
//extern QString BRAININFO;//放置brainInfo的文件夹
extern QString PROOFTRUESWC;
extern QString PROOFFALSESWC;
extern QString PROOFNONESWC;
extern QString IMAGETABLENAME;//图像数据表
extern QString PRERETABLENAME;//预重建数据表
extern QString RESWCTABLENAME;//重建完成数据表
extern QString PROOFTABLENAME;//校验数据表
extern QString CHECKTABLENAME;//校验结果数据表

Socket::Socket(qintptr socketID,QObject *parent):QThread(parent)
{
    socketDescriptor=socketID;
    dataInfo.dataSize=0;
    dataInfo.stringSize=0;
    dataInfo.dataReadedSize=0;
}

void Socket::run()
{
    socket=new QTcpSocket;
    socket->setSocketDescriptor(socketDescriptor);
    connect(socket,SIGNAL(readyRead()),this,SLOT(onReadyRead()),Qt::DirectConnection);
    connect(socket,SIGNAL(disconnected()),this,SLOT(quit()),Qt::DirectConnection);
    connect(this,SIGNAL(finished()),this,SLOT(deleteLater()));
    qDebug()<<"start socket thread "<<socketDescriptor<<" "<<QThread::currentThreadId();
    this->exec();
}

void Socket::sendMsg(const QString &msg) const
{
    QByteArray block;
    qint32 total=0;
    qint32 datasize=0;
    {
        QDataStream dts(&block,QIODevice::WriteOnly);
        dts<<qint32(0)<<qint32(0);
        block+=msg.toUtf8();
        total=block.size();
        datasize=total-2*sizeof (qint32);
        if(datasize<0||datasize!=msg.toUtf8().size())
        {
            qFatal("datasize < 0");
            return;
        }
        block.clear();
    }
    QDataStream dts(&block,QIODevice::WriteOnly);
    dts<<qint32(total)<<qint32(datasize);
    block+=msg.toUtf8();
}

bool Socket::sendFile(const QString &filePath,const QString filename)const
{
    QFile f(filePath);
    int count=5;
    if(f.exists()&&socket->state()==QAbstractSocket::ConnectedState)
    {
        __START:
        count--;
        if(f.open(QIODevice::ReadOnly))
        {
            QByteArray filedata=f.readAll();
            f.close();
            qint32 total=0;
            qint32 datasize=0;
            QByteArray block;
            {
                QDataStream dts(&block,QIODevice::WriteOnly);
                dts<<qint32(0)<<qint32(0);
                block+=filename.toUtf8();
                block+=filedata;
                total=block.size();
                datasize=total-2*sizeof (qint32)-filedata.size();
                if(datasize<0||datasize!=filename.toUtf8().size())
                {
                    qFatal("datasize < 0");
                    return false;
                }
                block.clear();
            }
            QDataStream dts(&block,QIODevice::WriteOnly);
            dts<<(qint32)(total)<<(qint32)(datasize);
            block+=filename.toUtf8();
            block+=filedata;

            socket->write(block);
            socket->waitForBytesWritten();
            qDebug()<<"send "<<filePath<<" success ";
            return true;
        }else
        {
            if(count<=0)
            {
                qCritical()<<"Critical Error:can not open file "<<filePath<<" "<<f.errorString();
                return false;
            }
            QElapsedTimer t;
            t.start();
            while(t.elapsed()<2000);
            goto __START;
        }
    }
    else
    {
        qDebug()<<"can not send "<<filePath<<" please check it!";
    }
}
//data file :mul read and write
//image file:mul read
//brainInfo file:mul read
void Socket::onReadyRead()
{
    if(dataInfo.dataReadedSize==0)
    {
        qDebug()<<"read dataSize&&stringSize";
        if(socket->bytesAvailable()>=sizeof(qint32)*2)
        {
            QDataStream in(socket);
            in>>dataInfo.dataSize>>dataInfo.stringSize;
//            qDebug()<<socket->peerAddress().toString()<<" "<<socket->socketDescriptor()<<" "<<"dataSize="<<dataInfo.dataSize<<",stringSize="<<dataInfo.stringSize;
            dataInfo.dataReadedSize=2*sizeof(qint32);
            if(dataInfo.stringSize<0)
            {
                socket->disconnectFromHost();
                socket->waitForDisconnected();
                return;
            }
            if(socket->bytesAvailable()+dataInfo.dataReadedSize>=dataInfo.dataSize)
            {
                QString filename=QString::fromUtf8(socket->read(dataInfo.stringSize),dataInfo.stringSize);
                dataInfo.dataReadedSize+=dataInfo.stringSize;
                if(dataInfo.dataReadedSize==dataInfo.dataSize)
                {
                    qDebug()<<"process Msg";
                    dataInfo.dataSize=0;dataInfo.stringSize=0;dataInfo.dataReadedSize=0;//reset dataInfo
                    processMsg(filename);
                }
                else
                {
                    qDebug()<<"Read file";
                    readFile(filename);
                }
            }
        }
    }else
    {
        if(socket->bytesAvailable()+dataInfo.dataReadedSize>=dataInfo.dataSize)
        {
            QString filename=QString::fromUtf8(socket->read(dataInfo.stringSize),dataInfo.stringSize);
            dataInfo.dataReadedSize+=dataInfo.stringSize;
            if(dataInfo.dataReadedSize==dataInfo.dataSize)
            {
                qDebug()<<"process Msg";
                dataInfo.dataSize=0;dataInfo.stringSize=0;dataInfo.dataReadedSize=0;//reset dataInfo
                processMsg(filename);
            }
            else
            {
                qDebug()<<"Read file";
                readFile(filename);
            }
        }
    }
}

void Socket::readFile(const QString &filename)
{
    QByteArray block=socket->read(dataInfo.dataSize-dataInfo.dataReadedSize);

    QString filePath=QCoreApplication::applicationDirPath()+"/tmp/"+filename;
    QFile file(filePath);
    file.open(QIODevice::WriteOnly);
    file.write(block);
    file.close();
    dataInfo.dataSize=0;dataInfo.stringSize=0;dataInfo.dataReadedSize=0;//reset dataInfo
    qDebug()<<"Read file end "<<filename;

    processFile(filePath,filename);
}

void Socket::SendFile(const QString &filename, int type) const
{
    //type:use it to find Dir;
    //need to modify
    //type:0 down file from data dir
    //type:1 down brain file from neuronInfo dir;
    //type:2 send image block/bb block from tmp dir;
    //type:3 send arbor mark txt ;
    QString filePath;
    filePath.clear();
    switch (type) {
        case 2:filePath=QCoreApplication::applicationDirPath()+"/tmp/"+filename;break;
    default: return;
    }
    if(sendFile(filePath,filename)&&type==2){
        QFile(filePath).remove();
    }
}

void Socket::processFile(QString filepath,QString filename)
{
    QRegExp blockSetRex("blockSet__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*).swc");
    if(blockSetRex.indexIn(filename)!=-1)
    {
        QString name=blockSetRex.cap(1).trimmed();
        int x1=blockSetRex.cap(2).toInt();
        int x2=blockSetRex.cap(3).toInt();
        int y1=blockSetRex.cap(4).toInt();
        int y2=blockSetRex.cap(5).toInt();
        int z1=blockSetRex.cap(6).toInt();
        int z2=blockSetRex.cap(7).toInt();
        int cnt=blockSetRex.cap(8).toInt();
        if(!(setSwcInBB(name,filepath,x1,x2,y1,y2,z1,z2,cnt)&&QFile(filepath).remove()) ){
         qFatal("failed to set");
        }
        qDebug()<<"set SWC END";
    }else
    {

    }
}
void Socket::processMsg(const QString &msg)
{
    QRegExp ImageDownRex("(.*):choose3.\n");
    /*
     * 要求发送全脑图像列表//db
     * 0:预重建
     * 1:检查
     */
    QRegExp BrainNumberRex("(.*):BrainNumber.\n");
    /*
     * 根据脑图的id和模式返回神经元列表
     * 17302;0;0
     * 0:脑图像名称
     * 1:是否是下一个/列表 0：下一个，1:列表
     * 2：预重建/校验 0:预重建，1：校验
     */
    QRegExp ImgBlockRex("(.*):imgblock.\n");//选定的神经元的名称，返回图像
    QRegExp GetBBSWCRex("(.*):GetBBSwc.\n");//获取局部神经元处理数据
    QRegExp ArborCheckRex("(.*):ArborCheck.\n");
    QRegExp GetArborResultRex("(.*):GetArborResult.\n");
    QRegExp BRINRESRex("(.*):BRAINRES.\n");
    if(ImageDownRex.indexIn(msg)!=-1)
    {
        processBrain(ImageDownRex.cap(1).trimmed());
    }else if(BrainNumberRex.indexIn(msg)!=-1)//db
    {
        processBrainNumber(BrainNumberRex.cap(1).trimmed());
    }
    else if(ImgBlockRex.indexIn(msg)!=-1)
    {
        processImageBlock(ImgBlockRex.cap(1).trimmed());
//        getAndSendImageBlock(ImgBlockRex.cap(1).trimmed());
    }else if(GetBBSWCRex.indexIn(msg)!=-1)
    {
        processBBSWC(GetBBSWCRex.cap(1).trimmed());
//        getAndSendSWCBlock(GetBBSWCRex.cap(1).trimmed());//mul read and write
    }else if(ArborCheckRex.indexIn(msg)!=-1)
    {
        processProof(ArborCheckRex.cap(1).trimmed());
//        ArborCheck(ArborCheckRex.cap(1).trimmed());
    }else if(GetArborResultRex.indexIn(msg)!=-1)
    {
        processResult(GetArborResultRex.cap(1).trimmed());
//        SendFile("arborcheck.txt",3);
    }else if(BRINRESRex.indexIn(msg)!=-1){
        processRes(BRINRESRex.cap(1).trimmed());
    }
    qDebug()<<"process Msg end";
}

void Socket::processBrain(const QString & paraString)
{
    QSqlDatabase db=QSqlDatabase::addDatabase("QMYSQL",QString::number(socket->socketDescriptor()));
    db.setDatabaseName("BrainTell");
    db.setHostName("localhost");

    db.setUserName("root");
    db.setPassword("1234");
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return;
    }

    QSqlQuery query(db);
    QString sql;
    if(paraString=="0"){
        //0:预重建用
        sql=QString("SELECT Brain_id FROM %1 WHERE tag = ? ORDER BY Brain_id").arg(PRERETABLENAME);
    }else if(paraString=="1"){
        sql=QString("SELECT Brain_id FROM %1 WHERE tag = ? ORDER BY Brain_id").arg(PROOFTABLENAME);
    }
    query.prepare(sql);
    query.addBindValue("0");
    QSet<QString> brains;
    if(query.exec()){
        while(query.next()){
            brains.insert(query.value(0).toString());
        }
    }

    sendMsg(paraString+";BRAINS;"+brains.values().join("_"));
}

void Socket::processBrainNumber(const QString & paraString)
{
    QStringList paras=paraString.split(";");
    QString brain_id=paras.at(0);
    bool nextOrList=paras.at(1).toInt()==0;
    bool preOrProof=paras.at(2).toInt()==0;

    if(nextOrList){
       funcNext(brain_id,preOrProof);
    }else{
        funcList(brain_id,preOrProof);
    }
}

void Socket::processImageBlock(const QString & paraString)
{
    getAndSendImageBlock(paraString);
}

void Socket::processBBSWC(const QString &paraString)
{
    getAndSendSWCBlock(paraString);
}

void Socket::processProof(const QString &paraString)
{
    ArborCheck(paraString);
}

void Socket::processResult(const QString &paraString)
{

}

void Socket::processRes(const QString &paraString)
{
    QSqlDatabase db=QSqlDatabase::addDatabase("QMYSQL",QString::number(socket->socketDescriptor()));
    db.setDatabaseName("BrainTell");
    db.setHostName("localhost");

    db.setUserName("root");
    db.setPassword("1234");
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return;
    }
    QSqlQuery query(db);
    QString sql;
    sql=QString("SELECT * FROM %1 WHERE Brain_id = ?").arg(IMAGETABLENAME);
    query.prepare(sql);
    query.addBindValue(paraString);
            int cnt=0;
    if(query.exec()&&query.next()){

        while(query.value(3+(++cnt))!="-1");
    }
    sendMsg("RES:"+QString::number(cnt));

}

void Socket::funcNext(QString brain_id,bool preOrProof)
{
    QSqlDatabase db=QSqlDatabase::addDatabase("QMYSQL",QString::number(socket->socketDescriptor()));
    db.setDatabaseName("BrainTell");
    db.setHostName("localhost");

    db.setUserName("root");
    db.setPassword("1234");
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return;
    }
    QSqlQuery query(db);
    QString sql;
    if(preOrProof){
        sql=QString("SELECT Neuron_id,Soma_position FROM %1 WHERE tag = ? and Brain_id = ? ORDER BY Neuron_id").arg(PRERETABLENAME);
    }else{
        sql=QString("SELECT name,Arbor_Position FROM %1 WHERE tag = ? and Brain_id = ? ORDER BY Neuron_id").arg(PROOFTABLENAME);
    }
    query.prepare(sql);
    query.addBindValue("0");
    query.addBindValue(brain_id);
    if(query.exec()){
        if(query.next()){
            QStringList list;
            list.push_back(brain_id);
            list.push_back("1");
            list.push_back(query.value(1).toString().split("_").join(";"));
            list.push_back("128");
            getAndSendImageBlock(list.join(";"),query.value(0).toString());
        }
    }
}

void Socket::funcList(QString brain_id,bool preOrProof)
{
    QStringList result;
    QSqlDatabase db=QSqlDatabase::addDatabase("QMYSQL",QString::number(socket->socketDescriptor()));
    db.setDatabaseName("BrainTell");

    db.setHostName("localhost");
    db.setUserName("root");
    db.setPassword("1234");
    if(!db.open()){
        qFatal("cannot connect DB when processBrain");
        return;
    }
    QSqlQuery query(db);
    QString sql;
    if(preOrProof){
        sql=QString("SELECT Neuron_id,Tag,Soma_position FROM %1 WHERE Brain_id = ? ORDER BY Neuron_id").arg(PRERETABLENAME);
    }else{
        sql=QString("SELECT name,Tag,Arbor_Position FROM %1 WHERE Brain_id = ? ORDER BY Neuron_id").arg(PROOFTABLENAME);
    }
    query.prepare(sql);
    query.addBindValue("0");
    if(query.exec()){
        while(query.next()){
            QStringList list;
            list.push_back(query.value(0).toString());
            list.push_back(query.value(1).toString());
            list.push_back(query.value(2).toString());
            result.push_back(list.join(";"));
        }
    }
    sendMsg("List:"+result.join("/"));
}

void Socket::getAndSendImageBlock(QString msg,QString N)
{
    /*
     * p1:brain_id;res;x;y;z;size
     * p2:Neuron_id/name
     */
    qDebug()<<"getAndSendImageBlock:"<<msg;
    QStringList paraList=msg.split(";",QString::SkipEmptyParts);
    QString brain_id=paraList.at(0).trimmed();//1. tf name/RES  2. .v3draw// test:17302;RES;x;y;z;b
//    QString filename1=filename;
//    filename1=filename1.remove('/');
//    qDebug()<<"filename:"<<filename;
//    qDebug()<<"filename1:"<<filename1;
//0: 18465/RESx18000x13000x5150
//1: 12520
//2: 7000
//3: 2916
    int res=paraList.at(1).toInt();
    int xpos=paraList.at(2).toInt();
    int ypos=paraList.at(3).toInt();
    int zpos=paraList.at(4).toInt();
    int blocksize=paraList.at(5).toInt();
    if(!QDir(QCoreApplication::applicationDirPath()+"/tmp").exists())
    {
        QDir(QCoreApplication::applicationDirPath()).mkdir("tmp");
    }
    QString apoName=QCoreApplication::applicationDirPath()+"/tmp/"+QString::number(socket->socketDescriptor())+brain_id+"__"
                  + QString::number(xpos)+ "__"
                  + QString::number(ypos)+ "__"
                  + QString::number(zpos)+ "__"
                  + QString::number(blocksize)+"__"
                  + QString::number(blocksize)+ "__"
                  + QString::number(blocksize);
    QProcess p;

    CellAPO centerAPO;
    centerAPO.x=xpos;centerAPO.y=ypos;centerAPO.z=zpos;
    QList <CellAPO> List_APO_Write;
    List_APO_Write.push_back(centerAPO);
    if(!writeAPO_file(apoName+".apo",List_APO_Write))
    {
        qDebug()<<"fail to write apo";
        return;//get .apo to get .v3draw
    }
    QString namepart1;
    if(N.isEmpty())
        namepart1=QString::number(socket->socketDescriptor())+"_"+brain_id+QString::number(blocksize)+"_";
    else
        namepart1=QString::number(socket->socketDescriptor())+"_"+N+QString::number(blocksize)+"_";
    //sockeddes_(brian_id(neuron_id/name))_size_

    QString vaa3dPath=QCoreApplication::applicationDirPath();
    QString filepath;
    {
        QSqlDatabase db=QSqlDatabase::addDatabase("QMYSQL",QString::number(socket->socketDescriptor()));
        db.setDatabaseName("BrainTell");
        db.setHostName("localhost");

        db.setUserName("root");
        db.setPassword("1234");
        if(!db.open()){
            qFatal("cannot connect DB when processBrain");
            return;
        }
        QSqlQuery query(db);
        QString sql;
        sql=QString("SELECT * FROM %1 WHERE Brain_id = ? ").arg(IMAGETABLENAME);
        query.prepare(sql);
        query.addBindValue(brain_id);
        if(query.exec())
        {
            if(query.next()){
                filepath=query.value(2).toString()+"/"+query.value(3 +res).toString();
            }
        }
    }
    if(filepath.isEmpty()) return ;

    QString order =QString("xvfb-run -d %0/vaa3d -x %1/plugins/image_geometry/crop3d_image_series/libcropped3DImageSeries.so "
                            "-f cropTerafly -i %2/ %3.apo %4/tmp/%5 -p %6 %7 %8")
            .arg(vaa3dPath).arg(vaa3dPath)
            .arg(filepath).arg(apoName).arg(QCoreApplication::applicationDirPath()).arg(namepart1).arg(blocksize).arg(blocksize).arg(blocksize);
    qDebug()<<"order="<<order;
    if(p.execute(order.toStdString().c_str())!=-1||p.execute(order.toStdString().c_str())!=-2)
    {
        QFile f1(apoName+".apo"); qDebug()<<f1.remove();
        QString fName=namepart1+QString("%1.000_%2.000_%3.000.v3dpbd").arg(xpos).arg(ypos).arg(zpos);
        qDebug()<<fName<<"*************";
        SendFile(fName,2);
    }else
    {
        sendMsg(QString("Can't get the image in BB ,please try again??%1:ERROR").arg(msg+":imgblock.\n"));
    }
}

void Socket::getAndSendSWCBlock(QString msg)
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


        //根据名称查询相关的数据库-> 找到swc的路径
        QString filepath;
        {
            QSqlDatabase db=QSqlDatabase::addDatabase("QMYSQL",QString::number(socket->socketDescriptor()));
            db.setDatabaseName("BrainTell");

            db.setHostName("localhost");
            db.setUserName("root");
            db.setPassword("1234");
            if(!db.open()){
                qFatal("cannot connect DB when processBrain");
                return;
            }
            QSqlQuery query(db);
            QString sql;
            if(name.count('_')==1){
                sql=QString("SELECT Pre_Swc FROM %1 WHERE Neuron_id = ? ").arg(PRERETABLENAME);

            }else if(name.count('_')==2){
                sql=QString("SELECT MAINPATH FROM %1 WHERE name = ? ").arg(PROOFTABLENAME);
            }else{
                return ;
            }
            query.prepare(sql);
            query.addBindValue(name);
            if(query.exec())
            {
                if(query.next()){
                    filepath=query.value(1).toString();
                }
            }
        }

        if(!QDir(QCoreApplication::applicationDirPath()+"/tmp").exists())
        {
            QDir(QCoreApplication::applicationDirPath()).mkdir("tmp");
        }

        QString BBSWCNAME=QCoreApplication::applicationDirPath()+"/tmp/blockGet__"+name+QString("__%1__%2__%3__%4__%5__%6__%7.swc")
                .arg(x1).arg(x2).arg(y1).arg(y2).arg(z1).arg(z2).arg(cnt);


        NeuronTree nt;
        if(filepath=="-1")
        {
            writeSWC_file(BBSWCNAME,nt);
            SendFile(BBSWCNAME.right(BBSWCNAME.size()-BBSWCNAME.lastIndexOf('/')),2);
            return;
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
            SendFile(BBSWCNAME.right(BBSWCNAME.size()-BBSWCNAME.lastIndexOf('/')),2);
            return;
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
        sendMsg(QString("Can't get the SWC in BB ,please try again??%1:ERROR").arg(msg+":GetBBSwc.\n"));
}

void Socket::ArborCheck(QString msg)
{
    QRegExp tmp("(.*);(.*);(.*)");//17302_00001_00001;flag;id;
    if(tmp.indexIn(msg)!=-1)
    {
        QString filename=tmp.cap(1).trimmed();
        int n=5;
        QString filepath;
        {
            QSqlDatabase db=QSqlDatabase::addDatabase("QMYSQL",QString::number(socket->socketDescriptor()));
            db.setDatabaseName("BrainTell");
            db.setHostName("localhost");
            db.setUserName("root");
            db.setPassword("1234");
            if(!db.open()){
                qFatal("cannot connect DB when processBrain");
                return;
            }
            QSqlQuery query(db);
            QString sql;

            sql=QString("INSERT INTO %1 (Name,Neuron_id,Brain_id,Tag,Time,User) VALUES (?,?,?,?,?,?)"
                                ).arg(CHECKTABLENAME);
            query.prepare(sql);
            query.addBindValue(filename);
            query.addBindValue(filename.left(filename.lastIndexOf('_')));
            query.addBindValue(filename.left(filename.indexOf('_')));
            query.addBindValue(tmp.cap(2));
            query.addBindValue(QDateTime::currentDateTime().toString("yyyy/MM/dd hh:mm:ss"));
            query.addBindValue(tmp.cap(3));
            if(query.exec())
            {
                sql=QString("SELECT Tag INTO %1 WHERE Name = ?").arg(PROOFTABLENAME);
                query.prepare(sql);
                query.addBindValue(filename);
                if(query.exec()&&query.value(0)=="0")
                {

                    sql=QString("UPDATE %1 SET Tag = %2 WHERE name = ? ")
                            .arg(PROOFTABLENAME).arg(QString::number(1));
                    query.prepare(sql);
                    query.addBindValue(filename);
                    if(!query.exec()){

                    }

                }
            }
        }
    }
}

bool Socket::setSwcInBB(QString neuron_id,QString tmpfilepath, int x1, int x2, int y1, int y2, int z1, int z2,int cnt)
{
    V_NeuronSWC_list testVNL1;

    {
        NeuronTree nt=readSWC_file(tmpfilepath);
        for(int i=0;i<nt.listNeuron.size();i++)
        {
            (nt.listNeuron[i].x+=x1)*=cnt;
            (nt.listNeuron[i].y+=y1)*=cnt;
            (nt.listNeuron[i].z+=z1)*=cnt;
        }
        testVNL1=NeuronTree__2__V_NeuronSWC_list(nt);
    }
    if(testVNL1.nrows()==0)
    {
        QSqlDatabase db=QSqlDatabase::addDatabase("QMYSQL",QString::number(socket->socketDescriptor()));
        db.setDatabaseName("BrainTell");
        db.setHostName("localhost");
        db.setUserName("root");
        db.setPassword("1234");
        if(!db.open()){
            qFatal("cannot connect DB when processBrain");
            return false;
        }
        QSqlQuery query(db);
        QString sql=QString("UPDATE %1 SET Tag = ?,Time1 = ? WHERE Neuron_id = ? AND Tag = 0").append(PRERETABLENAME);
        query.prepare(sql);
        query.addBindValue("1");
        query.addBindValue(QDateTime::currentDateTime().toString("yyyy/MM/dd hh:mm:ss"));
        query.addBindValue(neuron_id);
        if(!query.exec()){
            qFatal("can not update in db");
        }
        return true;
    }else{
        QString filePath;
        QSqlDatabase db=QSqlDatabase::addDatabase("QMYSQL",QString::number(socket->socketDescriptor()));
        db.setDatabaseName("BrainTell");
        db.setHostName("localhost");
        db.setUserName("root");
        db.setPassword("1234");
        if(!db.open()){
            qFatal("cannot connect DB when processBrain");
            return false;
        }
        QSqlQuery query(db);
        QString sql=QString("SELECT Pre_Swc FROM %1 WHERE Neuron_id = ?").arg(PRERETABLENAME);
        query.prepare(sql);
        query.addBindValue(neuron_id);
        if(query.exec()){
            if(query.value(0).toString()!="-1"){
                filePath=query.value(0).toString();
            }else{
                filePath=PRERESSWC+"/"+neuron_id+".swc";
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
                        qDebug()<<"FATAL:can not set SWC "<<neuron_id<<" call coder to check";
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
                if(query.value(0).toString()=="-1")
                {
                    sql=QString("UPDATE %1 SET Pre_Swc = ?,Tag = ?,Time1 = ? WHERE Neuron_id = ?").append(PRERETABLENAME);
                    query.prepare(sql);
                    query.addBindValue(filePath);
                    query.addBindValue("2");
                    query.addBindValue(QDateTime::currentDateTime().toString("yyyy/MM/dd hh:mm:ss"));
                    query.addBindValue(neuron_id);
                }else{
                    sql=QString("UPDATE %1 SET Pre_Swc = ?,Time1 = ? WHERE Neuron_id = ?").append(PRERETABLENAME);
                    query.prepare(sql);
                    query.addBindValue(filePath);
                    query.addBindValue(QDateTime::currentDateTime().toString("yyyy/MM/dd hh:mm:ss"));
                    query.addBindValue(neuron_id);
                }

                if(!query.exec()){
                    qFatal("can not update in db");
                    return false;
                }
                return true;
            }

        }
    }
}

