#include "socket.h"
#include <QDataStream>
#include <QByteArray>
#include <QHostAddress>
#include <resampling.h>
#define IMAGEDIR "image" // where is image data
QHash<QString,QReadWriteLock> Socket::fileLocks;
Socket::Socket(qintptr socketID,QObject *parent):QThread(parent)
{
    socketDescriptor=socketID;
    dataInfo.dataSize=0;
    dataInfo.stringSize=0;
    dataInfo.dataReadedSize=0;
    qDebug()<<"a new socket create "<<socketID<<" "<<QThread::currentThreadId();
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

void Socket::onReadyRead()
{
    if(dataInfo.dataReadedSize==0)
    {
        qDebug()<<"read dataSize&&stringSize";
        if(socket->bytesAvailable()>=sizeof(quint64)*2)
        {
            QDataStream in(socket);
            in>>dataInfo.dataSize>>dataInfo.stringSize;

            qDebug()<<socket->peerAddress().toString()<<" "<<socket->socketDescriptor()<<" "<<"dataSize="<<dataInfo.dataSize<<",stringSize="<<dataInfo.stringSize;
            dataInfo.dataReadedSize=2*sizeof(quint64);
            if(dataInfo.dataSize>256*1024*1024)
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
    QRegExp blockSetRex("blockSet__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*).swc");
    QString filePath;
    if(blockSetRex.indexIn(filename)!=-1)
        filePath=QCoreApplication::applicationDirPath()+"/tmp/"+filename;
    else
        filePath=QCoreApplication::applicationDirPath()+"/data/"+filename;
    QFile file(filePath);
    file.open(QIODevice::WriteOnly);
    file.write(block);
    file.close();
    dataInfo.dataSize=0;dataInfo.stringSize=0;dataInfo.dataReadedSize=0;//reset dataInfo

    qDebug()<<"Read file end "<<filename;
    if(blockSetRex.indexIn(filename)!=-1)
    {
        QString name=blockSetRex.cap(1)+".swc";
        int x1=blockSetRex.cap(2).toInt();
        int x2=blockSetRex.cap(3).toInt();
        int y1=blockSetRex.cap(4).toInt();
        int y2=blockSetRex.cap(5).toInt();
        int z1=blockSetRex.cap(6).toInt();
        int z2=blockSetRex.cap(7).toInt();
        int cnt=blockSetRex.cap(8).toInt();
        setSwcInBB(name,x1,x2,y1,y2,z1,z2,cnt);
        qDebug()<<"set SWC END";
    }
    //should move set to server to keep only one to change
}

void Socket::processMsg(const QString &msg)
{
    QRegExp LoginRex("(.*):login.\n");

    QRegExp DownRex("(.*):down.\n");
    QRegExp FileDownRex("(.*):choose1.\n");

    QRegExp ImageDownRex("(.*):choose3.\n");//要求发送全脑图像列表
    QRegExp BrainNumberRex("(.*):BrainNumber.\n");//脑图像编号
    QRegExp ImgBlockRex("(.*):imgblock.\n");

    QRegExp GetBBSWCRex("(.*):GetBBSwc.\n");
    QRegExp SwcCheckRex("(.*):SwcCheck.\n");

    QRegExp GetArborListRex("(.*):GetArborList.\n");
    QRegExp GetArborRex("(.*):GetArbor.\n");
    QRegExp GetArborSwcRex("(.*):GetArborSwc.\n");
    QRegExp ArborCheckRex("(.*):ArborCheck.\n");
    QRegExp GetArborResultRex("(.*):GetArborResult.\n");
    qDebug()<<"MSG:"<<msg;
    if(LoginRex.indexIn(msg)!=-1)
    {
        QString username=LoginRex.cap(1).trimmed();
        sendMsg(QString(username+":log in success."));
    }else if(DownRex.indexIn(msg)!=-1)
    {
        sendMsg(QString(currentDir()+":currentDir_down"));
    }else if(FileDownRex.indexIn(msg)!=-1)
    {
        QString filename=FileDownRex.cap(1).trimmed();
        sendFile(filename,0);//0:data/filename //
    }else if(ImageDownRex.indexIn(msg)!=-1)
    {
        sendMsg(currentBrain()+":currentDirImg");
    }else if(BrainNumberRex.indexIn(msg)!=-1)
    {
        QString filename=BrainNumberRex.cap(1).trimmed();
        sendFile(filename+".txt",1);//image/brainnumber/filename.txt
    }
    else if(ImgBlockRex.indexIn(msg)!=-1)
    {
        getAndSendImageBlock(ImgBlockRex.cap(1).trimmed());
    }else if(GetBBSWCRex.indexIn(msg)!=-1)
    {
        getAndSendSWCBlock(GetBBSWCRex.cap(1).trimmed());//mul read and write
    }else if(SwcCheckRex.indexIn(msg)!=-1)
    {
        swcCheck(SwcCheckRex.cap(1).trimmed());
    }else if(GetArborListRex.indexIn(msg)!=-1)
    {
        sendMsg(currentArbors()+":currentDirArbor");
    }
    else if(GetArborRex.indexIn(msg)!=-1)
    {
        getAndSendArborBlock(GetArborRex.cap(1).trimmed());
    }else if(GetArborSwcRex.indexIn(msg)!=-1)
    {
        getAndSendArborSwcBlock(GetArborSwcRex.cap(1).trimmed());
    }else if(ArborCheckRex.indexIn(msg)!=-1)
    {
        ArborCheck(ArborCheckRex.cap(1).trimmed());
    }else if(GetArborResultRex.indexIn(msg)!=-1)
    {
        sendFile("arborcheck.txt",3);
    }
    qDebug()<<"process Msg end";
}
void Socket::ArborCheck(QString msg)
{
    QRegExp tmp("(.*);(.*);(.*);(.*);(.*);(.*);(.*);(.*);(.*);(.*)");//17302_00001;x1;x2;y1;y2;z1;z2;flag;id;arN
    qDebug()<<msg;
    if(tmp.indexIn(msg)!=-1)
    {
        QString filename=tmp.cap(1).trimmed();

        int x1=tmp.cap(2).toInt();
        int x2=tmp.cap(3).toInt();
        int y1=tmp.cap(4).toInt();
        int y2=tmp.cap(5).toInt();
        int z1=tmp.cap(6).toInt();
        int z2=tmp.cap(7).toInt();

        int flag=tmp.cap(8).toInt();
        QString id=tmp.cap(9).trimmed();
        int arN=tmp.cap(10).toUInt();
        int n=5;


        if(!QDir(QCoreApplication::applicationDirPath()+"/arbormark").exists())
        {
            QDir(QCoreApplication::applicationDirPath()).mkdir("arbormark");
        }
        QFile *f=new QFile(QCoreApplication::applicationDirPath()+"/arbormark/"+"arborcheck.txt");
        __START1:
        if(f->open(QIODevice::WriteOnly|QIODevice::Text|QIODevice::Append))
        {
            QTextStream tsm(f);
            qDebug()<<filename<<" "<<arN<<" "<<x1<<" "<<x2<<" "<<y1<<" "<<y2<<" "<<z1<<" "<<z2<<" "<<flag<<" "<<id<<endl;
            tsm<<filename<<" "<<arN<<" "<<x1<<" "<<x2<<" "<<y1<<" "<<y2<<" "<<z1<<" "<<z2<<" "<<flag<<" "<<id<<endl;
            f->close();

        }else if(n-->0)
        {
            qDebug()<<f->errorString();
            QElapsedTimer t;
            t.start();
            while(t.elapsed()<2000);
            goto __START1;
        }

    }
}

void Socket::swcCheck(QString msg)
{
    QRegExp tmp("(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)");
    if(tmp.indexIn(msg)!=-1)
    {
        QString filename=tmp.cap(1).trimmed();
        int cnt=tmp.cap(8).toInt();
        int x1=tmp.cap(2).toInt()*cnt;
        int x2=tmp.cap(3).toInt()*cnt;
        int y1=tmp.cap(4).toInt()*cnt;
        int y2=tmp.cap(5).toInt()*cnt;
        int z1=tmp.cap(6).toInt()*cnt;
        int z2=tmp.cap(7).toInt()*cnt;

        int flag=tmp.cap(9).toInt();
        int n=5;
        if(!QDir(QCoreApplication::applicationDirPath()+"/mark").exists())
        {
            QDir(QCoreApplication::applicationDirPath()).mkdir("mark");
        }
        QFile *f=new QFile(QCoreApplication::applicationDirPath()+"/mark/"+filename+".txt");
        __START:
        if(f->open(QIODevice::WriteOnly|QIODevice::Text|QIODevice::Append))
        {
            QTextStream tsm(f);
            tsm<<x1<<" "<<x2<<" "<<y1<<" "<<y2<<" "<<z1<<" "<<z2<<" "<<flag<<endl;
            f->close();

        }else if(n-->0)
        {
            QElapsedTimer t;
            t.start();
            while(t.elapsed()<2000);
            goto __START;
        }
    }

}

//data file :mul read and write
//image file:mul read
//brainInfo file:mul read
void Socket::sendMsg(const QString &msg) const
{
    QByteArray block;
    QDataStream dts(&block,QIODevice::WriteOnly);
    dts<<quint64(0)<<quint64(0)<<msg.toUtf8();
    dts.device()->seek(0);
    dts<<(quint64)(block.size())<<(quint64)(block.size()-sizeof(quint64)*2)<<msg.toUtf8();
    qDebug()<<"send MSG:"<<block;
    socket->write(block);
    socket->waitForBytesWritten();
}
void Socket::sendFile(const QString &filename, int type) const
{
    //type:use it to find Dir;
    //need to modify
    //type:0 down file from data dir
    //type:1 down brain file from neuronInfo dir;
    //type:2 send image block/bb block from tmp dir;
    QString filePath;
    switch (type) {
        case 0:filePath.clear();filePath=QCoreApplication::applicationDirPath()+"/data/"+filename;break;
        case 1:filePath.clear();filePath=QCoreApplication::applicationDirPath()+"/brainInfo/"+filename;break;
        case 2:filePath.clear();filePath=QCoreApplication::applicationDirPath()+"/tmp/"+filename;break;
        case 3:filePath.clear();filePath=QCoreApplication::applicationDirPath()+"/arbormark/"+filename;break;
    default: break;
    }
    qDebug()<<"filepath:"<<filePath;
    QFile f(filePath);
    if(f.exists()&&socket->state()==QAbstractSocket::ConnectedState)
    {
        __START:
        if(f.open(QIODevice::ReadOnly))
        {

            QByteArray filedata=f.readAll();
            QByteArray block;
            QDataStream dts(&block,QIODevice::WriteOnly);
            dts<<quint64(0)<<quint64(0)<<filename.toUtf8()<<filedata;
            dts.device()->seek(0);
            dts<<(quint64)(block.size())
              <<(quint64)(block.size()-sizeof(quint64)*2-filedata.size())<<filename.toUtf8()<<filedata;

            socket->write(block);
            socket->waitForBytesWritten();
            qDebug()<<"send "<<filePath<<" success ";

            f.close();

            if(type==2) f.remove();
        }else
        {
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
QString Socket::currentDir() const
{
    QString dataPath=QCoreApplication::applicationDirPath()+"/data";
    QStringList dataFileList=QDir(dataPath).entryList(QDir::Files|QDir::NoDotAndDotDot);
    return  dataFileList.join(";");
}

QString Socket::currentBrain() const
{
    QString brainPath=QCoreApplication::applicationDirPath()+"/"+"brainInfo";

    auto list=QDir(brainPath).entryInfoList(QDir::Files|QDir::NoDotAndDotDot);

    QStringList imgDirList;
    imgDirList.clear();
    for(auto i:list)
        imgDirList.push_back(i.baseName());

    return imgDirList.join(";");
}

QString Socket::currentArbors() const
{
    QString imgPath=QCoreApplication::applicationDirPath()+"/arbors";
    QStringList imgDirList=QDir(imgPath).entryList(QDir::Files|QDir::NoDotAndDotDot);
    return imgDirList.join(";");
}


void Socket::getAndSendSWCBlock(QString msg)
{
    QRegExp tmp("(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)__(.*)");
    int n=5;//重复5次，每次延时2S
    if(tmp.indexIn(msg)!=-1)
    {
        QString name=tmp.cap(1)+".swc";
        int x1=tmp.cap(2).toInt();
        int x2=tmp.cap(3).toInt();
        int y1=tmp.cap(4).toInt();
        int y2=tmp.cap(5).toInt();
        int z1=tmp.cap(6).toInt();
        int z2=tmp.cap(7).toInt();
        int cnt=tmp.cap(8).toInt();
        x1*=cnt;x2*=cnt;y1*=cnt;y2*=cnt;z1*=cnt;z2*=cnt;
        NeuronTree nt;
        if(!QFile(QCoreApplication::applicationDirPath()+"/data/"+name).exists())
        {
            if(!QDir(QCoreApplication::applicationDirPath()+"/tmp").exists())
            {
                QDir(QCoreApplication::applicationDirPath()).mkdir("tmp");
            }
            x1/=cnt;x2/=cnt;y1/=cnt;y2/=cnt;z1/=cnt;z2/=cnt;
            QString BBSWCNAME=QCoreApplication::applicationDirPath()+"/tmp/blockGet__"+QFileInfo(name).baseName()+QString("__%1__%2__%3__%4__%5__%6__%7.swc")
                    .arg(x1).arg(x2).arg(y1).arg(y2).arg(z1).arg(z2).arg(cnt);
//            nt = resample(nt, cnt);
            writeSWC_file(BBSWCNAME,nt);
            sendFile("blockGet__"+QFileInfo(name).baseName()+QString("__%1__%2__%3__%4__%5__%6__%7.swc")
                     .arg(x1).arg(x2).arg(y1).arg(y2).arg(z1).arg(z2).arg(cnt),2);
            return;
        }

        __START:

        --n;
        qDebug()<<"Get SWC in BB:"<<5-n;

        nt=readSWC_file(QCoreApplication::applicationDirPath()+"/data/"+name);
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
            if(!QDir(QCoreApplication::applicationDirPath()+"/tmp").exists())
            {
                QDir(QCoreApplication::applicationDirPath()).mkdir("tmp");
            }
            x1/=cnt;x2/=cnt;y1/=cnt;y2/=cnt;z1/=cnt;z2/=cnt;
            QString BBSWCNAME=QCoreApplication::applicationDirPath()+"/tmp/blockGet__"+QFileInfo(name).baseName()+QString("__%1__%2__%3__%4__%5__%6__%7.swc")
                    .arg(x1).arg(x2).arg(y1).arg(y2).arg(z1).arg(z2).arg(cnt);
//            nt = resample(nt, cnt);
            writeSWC_file(BBSWCNAME,nt);
            sendFile("blockGet__"+QFileInfo(name).baseName()+QString("__%1__%2__%3__%4__%5__%6__%7.swc")
                     .arg(x1).arg(x2).arg(y1).arg(y2).arg(z1).arg(z2).arg(cnt),2);
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

void Socket::getAndSendImageBlock(QString msg)
{
    qDebug()<<"getAndSendImageBlock:"<<msg;
    QStringList paraList=msg.split("__",QString::SkipEmptyParts);
    QString filename=paraList.at(0).trimmed();//1. tf name/RES  2. .v3draw// test:17302/RES54600x34412x9847__x__y__z_b;
    QString filename1=filename;
    filename1=filename1.remove('/');
    qDebug()<<"filename:"<<filename;
    qDebug()<<"filename1:"<<filename1;
//0: 18465/RESx18000x13000x5150
//1: 12520
//2: 7000
//3: 2916
    int xpos=paraList.at(1).toInt();
    int ypos=paraList.at(2).toInt();
    int zpos=paraList.at(3).toInt();
    int blocksize=paraList.at(4).toInt();
    if(!QDir(QCoreApplication::applicationDirPath()+"/tmp").exists())
    {
        QDir(QCoreApplication::applicationDirPath()).mkdir("tmp");
    }
    QString string=QCoreApplication::applicationDirPath()+"/tmp/"+QString::number(socket->socketDescriptor())+filename1+"__"
                  + QString::number(xpos)+ "__"
                  + QString::number(ypos)+ "__"
                  + QString::number(zpos)+ "__"
                  + QString::number(blocksize)+"__"
                  + QString::number(blocksize)+ "__"
                  + QString::number(blocksize);

    qDebug()<<string;
    QProcess p;

    CellAPO centerAPO;
    centerAPO.x=xpos;centerAPO.y=ypos;centerAPO.z=zpos;
    QList <CellAPO> List_APO_Write;
    List_APO_Write.push_back(centerAPO);
    if(!writeAPO_file(string+".apo",List_APO_Write))
    {
        qDebug()<<"fail to write apo";
        return;//get .apo to get .v3draw
    }

    QString namepart1=QString::number(socket->socketDescriptor())+"_"+filename1+QString::number(blocksize)+"_";
    QString vaa3dPath=QCoreApplication::applicationDirPath();
    QString order =QString("xvfb-run -a %0/vaa3d -x %1/plugins/image_geometry/crop3d_image_series/libcropped3DImageSeries.so "
                            "-f cropTerafly -i %2/%3/ %4.apo %5/tmp/%6 -p %7 %8 %9")
            .arg(vaa3dPath).arg(vaa3dPath)
            .arg(QCoreApplication::applicationDirPath()+"/"+IMAGEDIR).arg(filename).arg(string).arg(QCoreApplication::applicationDirPath()).arg(namepart1).arg(blocksize).arg(blocksize).arg(blocksize);
    qDebug()<<"order="<<order;
    if(p.execute(order.toStdString().c_str())!=-1||p.execute(order.toStdString().c_str())!=-2)
    {
        QFile f1(string+".apo"); qDebug()<<f1.remove();
        QString fName=namepart1+QString("%1.000_%2.000_%3.000.v3dpbd").arg(xpos).arg(ypos).arg(zpos);
        qDebug()<<fName<<"*************";
        sendFile(fName,2);
    }else
    {
        sendMsg(QString("Can't get the image in BB ,please try again??%1:ERROR").arg(msg+":imgblock.\n"));
    }
}

void Socket::setSwcInBB(QString name, int x1, int x2, int y1, int y2, int z1, int z2,int cnt)
{
    V_NeuronSWC_list testVNL;
    V_NeuronSWC_list resVNL;
    resVNL=testVNL;
    resVNL.seg.clear();
    int n=5;
    __START:
    qDebug()<<"try "<<5-n+1;
    if(QFile(QCoreApplication::applicationDirPath()+"/data/"+name).exists())
    {
        --n;
        NeuronTree nt=readSWC_file(QCoreApplication::applicationDirPath()+"/data/"+name);
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
                return;
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
    }
    qDebug()<<"open blockSet file";

    x1/=cnt;x2/=cnt;y1/=cnt;y2/=cnt;z1/=cnt;z2/=cnt;
    QString BBSWCNAME="blockSet__"+QFileInfo(name).baseName()+QString("__%1__%2__%3__%4__%5__%6__%7.swc")
            .arg(x1).arg(x2).arg(y1).arg(y2).arg(z1).arg(z2).arg(cnt);
    NeuronTree nt=readSWC_file(QCoreApplication::applicationDirPath()+"/tmp/"+BBSWCNAME);
    for(int i=0;i<nt.listNeuron.size();i++)
    {
        (nt.listNeuron[i].x+=x1)*=cnt;
        (nt.listNeuron[i].y+=y1)*=cnt;
        (nt.listNeuron[i].z+=z1)*=cnt;
    }

    V_NeuronSWC_list testVNL1=NeuronTree__2__V_NeuronSWC_list(nt);
    qDebug()<<"set Nt number:"<<testVNL1.seg.size();
    for(int i=0;i<testVNL1.seg.size();i++)
    {
        resVNL.seg.push_back(testVNL1.seg.at(i));
    }
    qDebug()<<"after set nt number :"<<resVNL.seg.size();
    nt=V_NeuronSWC_list__2__NeuronTree(resVNL);
    while(!writeESWC_file(QCoreApplication::applicationDirPath()+"/data/"+name,nt))
    {
        QElapsedTimer t;
        t.start();
        while(t.elapsed()<2000);
    }
    QFile f(QCoreApplication::applicationDirPath()+"/tmp/"+BBSWCNAME);
    qDebug()<<"set file size:"<<f.fileName()<<":"<<f.size();
    f.remove();
}

void Socket::getAndSendArborBlock(QString msg)
{
    qDebug()<<"getAndSendImageBlock:"<<msg;
    QStringList paraList=msg.split(";",QString::SkipEmptyParts);
    QString filename1=paraList.at(0).trimmed();
    QString filename=filename1+".v3draw";//1. tf name/RES  2. .v3draw// test:RES54600x34412x9847__x1__x2__y1__y2__z1__z2;

    int x1pos=paraList.at(1).toInt();
    int x2pos=paraList.at(2).toInt();
    int y1pos=paraList.at(3).toInt();
    int y2pos=paraList.at(4).toInt();
    int z1pos=paraList.at(5).toInt();
    int z2pos=paraList.at(6).toInt();
    if(!QDir(QCoreApplication::applicationDirPath()+"/tmp").exists())
    {
        QDir(QCoreApplication::applicationDirPath()).mkdir("tmp");
    }
    QString vaa3dPath=QCoreApplication::applicationDirPath();
    QString string=QCoreApplication::applicationDirPath()+"/tmp/"+filename1+
            QString("%0__%1__%2__%3__%4__%5__%6.v3dpbd").arg(socket->socketDescriptor()).arg(x1pos).arg(x2pos).arg(y1pos).arg(y2pos).arg(z1pos).arg(z2pos);
    QString order =QString("xvfb-run -a %0/vaa3d -x %1/plugins/image_geometry/crop3d_image_series/libcropped3DImageSeries.so "
                            "-f crop3d_raw -i %2/%3 -o %4 -p %5 %6 %7 %8 %9 %10 0")
            .arg(vaa3dPath).arg(vaa3dPath).arg(QCoreApplication::applicationDirPath()+"/arbors").arg(filename).
            arg(string).arg(x1pos).arg(x2pos).arg(y1pos).arg(y2pos).arg(z1pos).arg(z2pos);
    qDebug()<<"order="<<order;
        QProcess p;
    if(p.execute(order.toStdString().c_str())!=-1||p.execute(order.toStdString().c_str())!=-2)
    {
        sendFile(filename1+QString("%0__%1__%2__%3__%4__%5__%6.v3dpbd").arg(socket->socketDescriptor()).arg(x1pos).arg(x2pos).arg(y1pos).arg(y2pos).arg(z1pos).arg(z2pos),2);
    }else
    {
        sendMsg(QString("Can't get the image in BB ,please try again??%1:ERROR").arg(msg+":imgblock.\n"));
    }
}

void Socket::getAndSendArborSwcBlock(QString msg)
{
    QRegExp tmp("(.*)RES(.*);(.*);(.*);(.*);(.*);(.*);(.*)");
    int n=5;//重复5次，每次延时2S
    if(tmp.indexIn(msg)!=-1)
    {
        QString name=tmp.cap(1)+".swc";
        int x1=tmp.cap(3).toInt();
        int x2=tmp.cap(4).toInt();
        int y1=tmp.cap(5).toInt();
        int y2=tmp.cap(6).toInt();
        int z1=tmp.cap(7).toInt();
        int z2=tmp.cap(8).toInt();


        __START:
        NeuronTree nt;
        --n;
        qDebug()<<"Get SWC in BB:"<<5-n;
        nt=readSWC_file(QCoreApplication::applicationDirPath()+"/arborswc/"+name);
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
                (nt.listNeuron[i].x/=4)-=x1;
                (nt.listNeuron[i].y/=4)-=y1;
                (nt.listNeuron[i].z/=4)-=z1;
            }
            if(!QDir(QCoreApplication::applicationDirPath()+"/tmp").exists())
            {
                QDir(QCoreApplication::applicationDirPath()).mkdir("tmp");
            }

            QString BBSWCNAME=QCoreApplication::applicationDirPath()+"/tmp/blockGet__"+QFileInfo(name).baseName()+QString("__%1__%2__%3__%4__%5__%6.swc")
                    .arg(x1).arg(x2).arg(y1).arg(y2).arg(z1).arg(z2);
            writeSWC_file(BBSWCNAME,nt);
            sendFile("blockGet__"+QFileInfo(name).baseName()+QString("__%1__%2__%3__%4__%5__%6.swc")
                     .arg(x1).arg(x2).arg(y1).arg(y2).arg(z1).arg(z2),2);
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
        sendMsg(QString("Can't get the SWC in BB ,please try again??%1:ERROR").arg(msg+":GetArborSwc.\n"));
}
