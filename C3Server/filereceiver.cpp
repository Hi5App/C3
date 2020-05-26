#include "filereceiver.h"
#include "string.h"
#include <QDataStream>
FileReceiveSocket::FileReceiveSocket(QObject *parent):QTcpSocket(parent)
{

}

void FileReceiveSocket::readFile()
{
    QDataStream in(this);in.setVersion(QDataStream::Qt_4_7);
    if(this->m_bytesreceived==0)
    {
        if(this->bytesAvailable()>=sizeof (quint64)*2)
        {
            in>>totalsize>>filenamesize;
            qDebug()<<totalsize <<"\t"<<filenamesize;
            m_bytesreceived+=sizeof (quint64)*2;
            if(this->bytesAvailable()+m_bytesreceived>=totalsize)
            {
                QString filename=QString::fromUtf8(this->read(filenamesize),filenamesize);
                qDebug()<<filename;
                QByteArray block=this->readAll();
                QFile file("./data/"+filename);
                file.open(QIODevice::WriteOnly);
                file.write(block);
                file.close();
                m_bytesreceived=0;
                disconnectFromHost();
            }
        }
    }else {
        if(this->bytesAvailable()+m_bytesreceived>=totalsize)
        {
            QString filename=QString::fromUtf8(this->read(filenamesize),filenamesize);
            qDebug()<<filename;
            QByteArray block=this->readAll();
            QFile file("./data/"+filename);
            file.open(QIODevice::WriteOnly);
            file.write(block);
            file.close();
            m_bytesreceived=0;
            disconnectFromHost();
        }
    }


}

FileReceiveServer::FileReceiveServer(QObject *parent):QTcpServer(parent)
{
    if(!QDir("data").exists())
    {
        QDir("./").mkdir("data");
    }
    list.clear();

}

void FileReceiveServer::incomingConnection(qintptr handle)
{
    FileReceiveSocket *filesendsocket=new FileReceiveSocket(this);
    filesendsocket->setSocketDescriptor(handle);
    qDebug()<<"file receive "<<filesendsocket->peerAddress();
    list.push_back(filesendsocket);
    connect(filesendsocket,SIGNAL(readyRead()),filesendsocket,SLOT(readFile()));
    connect(filesendsocket,SIGNAL(disconnected()),this,SLOT(onSocketDisconnected()));
}

void FileReceiveServer::onSocketDisconnected()
{
    FileReceiveSocket *filesendsocket=qobject_cast<FileReceiveSocket*>(sender());
    for(int i=0;i<list.size();i++)
    {
            if(list[i]->peerAddress()==filesendsocket->peerAddress())
            {
                qDebug()<<list[i]->peerAddress()<<" file receive disconnected ";

                list.removeAt(i);
            }
    }
    filesendsocket->deleteLater();
    if(list.size()==0)
    {
        close();
    }
}
FileReceiveServer::~FileReceiveServer()
{
    for(int i=0;i<list.size();i++)
    {
        list[i]->deleteLater();
    }
    list.clear();
}
