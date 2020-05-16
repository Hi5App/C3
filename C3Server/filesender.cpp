#include "filesender.h"
#include <QDir>
FileSenderSocket::FileSenderSocket(QObject *parent):QTcpSocket(parent)
{

}

void FileSenderSocket::sendFile(QString filepath, QString filename)
{
    QFile f(filepath);
    if(f.exists())
    {
        qDebug()<<filepath <<" exist";
        if(f.open(QIODevice::ReadOnly))
        {
            QByteArray data=f.readAll();
            QByteArray block;

            QDataStream dts(&block,QIODevice::WriteOnly);
            dts.setVersion(QDataStream::Qt_4_7);

            dts<<qint64(0)<<qint64(0)<<filename;
            dts.device()->seek(0);
            dts<<(qint64)(block.size()+f.size());
            dts<<(qint64)(block.size()-sizeof(qint64)*2);
            dts<<filename;
            dts<<data;

            this->write(block);
            this->flush();
         }
       f.close();
    }
    disconnectFromHost();
    while(!(state() == QAbstractSocket::UnconnectedState ||waitForDisconnected(1000))) ;
}

FileSenderServer::FileSenderServer(QObject *parnet):QTcpServer(parnet)
{
    if(!QDir("data").exists())
    {
        QDir("./").mkdir("data");
    }
    list.clear();
}

void FileSenderServer::incomingConnection(int handle)
{
    FileSenderSocket *filesendsocket=new FileSenderSocket(this);
    filesendsocket->setSocketDescriptor(handle);
    list.push_back(filesendsocket);
    connect(filesendsocket,SIGNAL(disconnected()),this,SLOT(onSocketDisconnected()));
}

void FileSenderServer::onSocketDisconnected()
{
    FileSenderSocket *filesendsocket=qobject_cast<FileSenderSocket*>(sender());
    for(int i=0;i<list.size();i++)
    {
            if(list[i]->peerAddress()==filesendsocket->peerAddress())
            {
                qDebug()<<list[i]->peerAddress()<<" file send disconnected ";
                list[i]->deleteLater();
                list.removeAt(i);
                break;
            }
    }
}

void FileSenderServer::sendAnnotation(QString ip, QString filename)
{
    for(int i=0;i<list.size();i++)
    {
        if(list[i]->peerAddress().toString()==ip)
        {
            FileSenderSocket *filesendsocket=list[i];
            filesendsocket->sendFile("./data/"+filename,filename);
            break;
        }
    }

}

void FileSenderServer::sendRAW(QString ip, QString filename)
{
    for(int i=0;i<list.size();i++)
    {
        if(list[i]->peerAddress().toString()==ip)
        {
            FileSenderSocket *filesendsocket=list[i];
            qDebug()<<"find send socket"<<ip;
            filesendsocket->sendFile("./rawimage/"+filename,filename);
            QFile f2("./rawimage/"+filename); f2.remove();
            break;
        }
    }
    qDebug()<<"send for end";
}

FileSenderServer::~FileSenderServer()
{
    for(int i=0;i<list.size();i++)
    {
        list[i]->deleteLater();
    }
    list.clear();
}
