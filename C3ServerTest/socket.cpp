#include "socket.h"
#include <QDataStream>
#include <QtGlobal>
Socket::Socket(QObject *parent):QThread(parent),i(i)
{

}

void Socket::sendMsg(QString msg)
{
    QByteArray block;
    QDataStream dts(&block,QIODevice::WriteOnly);
    block.clear();
    dts<<quint64(0)<<quint64(0);


    block+=msg.toUtf8();


    dts.device()->seek(0);
    quint64 size1=(quint64)(block.size());
    quint64 size2=(quint64)(block.size()-sizeof(quint64)*2);

    {
        QByteArray block;
        QDataStream dts(&block,QIODevice::WriteOnly);
        block.clear();
        dts<<quint64(size1);
          dts<<quint64(size2);
        block+=msg.toUtf8();
        qDebug()<<block;
        socket->write(block);
        socket->waitForBytesWritten();

    }
}

void Socket::onreadyRead()
{
    qDebug()<<"readyread";
    if(dataReadedSize==0)
    {
        if(socket->bytesAvailable()>=2*sizeof (quint64))
        {
            QDataStream in(socket);
            in>>dataSize>>stringSize;
            dataReadedSize=2*sizeof (quint64);
        }
        if(socket->bytesAvailable()+dataReadedSize>=dataSize)
        {
            QString filename=QString::fromUtf8(socket->read(stringSize),stringSize);
            dataReadedSize+=stringSize;
            if(dataReadedSize==dataSize)
            {
                qDebug()<<"process Msg";
                dataSize=0;stringSize=0;dataReadedSize=0;//reset dataInfo

            }
            else
            {
                qDebug()<<"Read file";
                socket->read(dataSize-dataReadedSize);
                dataSize=0;stringSize=0;dataReadedSize=0;
            }
        }
    }else
    {
        if(socket->bytesAvailable()+dataReadedSize>=dataSize)
        {
            QString filename=QString::fromUtf8(socket->read(stringSize),stringSize);
            dataReadedSize+=stringSize;
            if(dataReadedSize==dataSize)
            {
                qDebug()<<"process Msg";
                dataSize=0;stringSize=0;dataReadedSize=0;//reset dataInfo

            }
            else
            {
                qDebug()<<"Read file";
                socket->read(dataSize-dataReadedSize);
                dataSize=0;stringSize=0;dataReadedSize=0;
            }
        }
    }
}

void Socket::run()
{
    socket=new QTcpSocket;

    socket->connectToHost(ip,port);
    qsrand(socket->socketDescriptor());
    int ran=abs(qrand()%200);
    QString msg1=QString("mouse18454_teraconvert/RES(26298x35000x11041)__%1__%2__%3__128:imgblock.\n")
            .arg(26390-ran).arg(9482-ran).arg(7004-ran);
    msgList.push_back(msg1);

    if(!socket->waitForConnected())
    {
        qDebug()<<"error:cannot connect "<<order<<socket->localPort();
    }
    else
    {
        qDebug()<<"connected."<<order<<socket->localPort();

    }
    connect(socket,SIGNAL(readyRead()),this,SLOT(onreadyRead()));
    for(auto msg:msgList)
    {
        sendMsg(msg);
    }
}





