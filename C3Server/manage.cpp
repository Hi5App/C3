#include "manage.h"
#include <QProcess>
#include "basic_surf_objs.h"
#define IMAGEDIR "image" // where is image data
ManageSocket::ManageSocket(QObject* parent):QTcpSocket(parent)
{

}

void ManageSocket::readManage()
{
    QRegExp LoginRex("(.*):login.\n");
    QRegExp ImportRex("(.*):import.\n");
    QRegExp DownRex("(.*):down.\n");
    QRegExp FileDownRex("(.*):choose1.\n");
    QRegExp ImageDownRex("(.*):choose3.\n");
    QRegExp ImgBlockRex("(.*):imgblock.\n");
    QRegExp DisconnectRex("(.*):disconnected.\n");

    while(this->canReadLine())
    {
        QString manageMSG=QString::fromUtf8(this->readLine());
        qDebug()<<"manageMSG"<<this->peerAddress().toString()<<":"<<manageMSG;
        if(LoginRex.indexIn(manageMSG)!=-1)
        {
            QString username=LoginRex.cap(1);
//            qDebug()<<QString(username+":log in success.");
            this->write(QString(username+":log in success."+"\n").toUtf8());
        }else if(ImportRex.indexIn(manageMSG)!=-1)
        {
//            qDebug()<<"ImportRex";
            emit startReceiveServer();
        }else if(DownRex.indexIn(manageMSG)!=-1)
        {
//            qDebug()<<"DownRex";
            this->write(QString(currentDir()+":currentDir_down."+"\n").toUtf8());
            this->waitForBytesWritten();
        }else if(FileDownRex.indexIn(manageMSG)!=-1)
        {
//            qDebug()<<"FileDownRex";
            QString filename=FileDownRex.cap(1).trimmed();
            emit sendFile(this->peerAddress().toString(),filename);
        }else if(ImageDownRex.indexIn(manageMSG)!=-1){
//             qDebug()<<"ImageDownRex";
            this->write(QString(currentDirImg()+":currentDirImg."+"\n").toUtf8());
             this->waitForBytesWritten();
        }else if(ImgBlockRex.indexIn(manageMSG)!=-1){
            qDebug()<<"ImgBlockRex";
            QStringList paraList=ImgBlockRex.cap(1).trimmed().split("__",QString::SkipEmptyParts);
            QString filename=paraList.at(0).trimmed();//1. tf name/RES  2. .v3draw// test:17302_00001/RES(54600x34412x9847);

            int xpos=paraList.at(1).toInt();
            int ypos=paraList.at(2).toInt();
            int zpos=paraList.at(3).toInt();
            int blocksize=paraList.at(4).toInt();

            QString string=filename+"__" +QString::number(xpos)+ "__" + QString::number(ypos) + "__" + QString::number(zpos)+
                    "__" +QString::number(blocksize)+"__"+QString::number(blocksize)+ "__"+QString::number(blocksize);

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

            if(!QDir("./rawimage").exists())
            {
                QDir("./").mkdir("rawimage");
            }
            QString namepart1=QString::number(this->peerAddress().toIPv4Address())+"_"+filename+"_"+QString::number(blocksize)+"_";

            QString order =QString("xvfb-run -a ./vaa3d -x ./plugins/image_geometry/crop3d_image_series/libcropped3DImageSeries.so "
                                    "-f cropTerafly -i ./%0/%1/ %2.apo ./rawimage/%3 -p %4 %5 %6")
                    .arg(IMAGEDIR).arg(filename).arg(string).arg(namepart1).arg(blocksize).arg(blocksize).arg(blocksize);
                            qDebug()<<"order="<<order;
            qDebug()<<p.execute(order.toStdString().c_str());

            QString fName=namepart1+QString("%1.000_%2.000_%3.000.v3dpbd").arg(xpos).arg(ypos).arg(zpos);
            qDebug()<<fName;
            qDebug()<<"1";
            emit sendFile(this->peerAddress().toString(),fName);

            QFile f1(string+".apo"); qDebug()<<f1.remove();
        }else if(DisconnectRex.indexIn(manageMSG)!=-1)
        {
            disconnectFromHost();
        }
    }

}
QString ManageSocket::currentDir()
{
    QDir rootDir("./data");
    QFileInfoList list=rootDir.entryInfoList(QStringList()<<"*.ano"<<"*.apo"<<"*.swc"<<"*.eswc",QDir::Files|QDir::NoDotAndDotDot);

    QStringList TEMPLIST;
    TEMPLIST.clear();
    for(unsigned i=0;i<list.size();i++)
    {
        QFileInfo tmpFileInfo=list.at(i);
        if(!tmpFileInfo.isDir())
        {
            QString fileName = tmpFileInfo.fileName();
                TEMPLIST.append(fileName);
        }
    }
    return TEMPLIST.join(";");
}
QString ManageSocket::currentDirImg()
{
    QDir rootDir("./"+QString(IMAGEDIR));
    if(!rootDir.exists()) return QString();
    QFileInfoList list=rootDir.entryInfoList(QDir::NoDotAndDotDot|QDir::Dirs);
    QStringList TEMPLIST;
    TEMPLIST.clear();
    for(unsigned i=0;i<list.size();i++)
    {
        QString fileName=list.at(i).fileName();
        if(fileName.contains("RES"))
        {
            TEMPLIST.append(fileName);
        }
    }
    return TEMPLIST.join(";");
}

ManageServer::ManageServer(QObject *parent):QTcpServer(parent)
{
    list.clear();
    if(!sendServer.listen(QHostAddress::Any,9002))
    {
        qDebug()<<"failed to open FileSenderServer";
        deleteLater();
        qApp->quit();
    }
    connect(&sendServer,SIGNAL(FileConnected(QString)),this,SLOT(FileConnected(QString)));
}

ManageServer::~ManageServer()
{
    for(int i=0;i<list.size();i++)
    {
        list[i]->deleteLater();
    }
}

void ManageServer::incomingConnection(qintptr handle)
{

    ManageSocket *managesocket=new ManageSocket(this);
    managesocket->setSocketDescriptor(handle);
    qDebug()<<managesocket->peerAddress().toString()<<" "<<"manage connected";
    list.push_back(managesocket);
    connect(managesocket,SIGNAL(readyRead()),managesocket,SLOT(readManage()));
    connect(managesocket,SIGNAL(startReceiveServer()),this,SLOT(startReceiveServer()));
    connect(managesocket,SIGNAL(sendFile(QString,QString)),this,SLOT(sendFile(QString,QString)));
    connect(managesocket,SIGNAL(disconnected()),this,SLOT(onSocketDisconnected()));
}

void ManageServer::onSocketDisconnected()
{
    ManageSocket *managesocket=qobject_cast<ManageSocket*>(sender());
    for(int i=0;i<list.size();i++)
    {
            if(list[i]->peerAddress()==managesocket->peerAddress())
            {
                qDebug()<<list[i]->peerAddress().toString()<<" manage socket disconnected ";
                list.removeAt(i);
                break;
            }
    }
    managesocket->deleteLater();
}

void ManageServer::startReceiveServer()
{
    ManageSocket *managesocket=qobject_cast<ManageSocket*>(sender());
    if(receiveServer.isListening())
    {
        qDebug()<<"ReceiveServer isListening";
        return;
    }
    if(!receiveServer.listen(QHostAddress::Any,9001))
    {
        qDebug()<<"startReceiveServer failed";
        managesocket->write(QString(QString::number(-1)+":import port."+"\n").toUtf8());
    }
    else
    {
        qDebug()<<"startReceiveServer success\n";
        managesocket->write(QString(QString::number(receiveServer.serverPort())+":import port."+"\n").toUtf8());
    }
}

void ManageServer::sendFile(QString ip,QString filename)
{
    if(filename.contains("v3draw")||filename.contains("v3dpbd"))
        sendServer.sendRAW(ip,filename);
    else
        sendServer.sendAnnotation(ip,filename);

}

void ManageServer::FileConnected(QString ip)
{
    for(int i=0;i<list.size();i++)
    {
        if(list[i]->peerAddress().toString()==ip)
        {
            list[i]->write(QString(ip+":file connected."+"\n").toUtf8());
            break;
        }
    }
}
