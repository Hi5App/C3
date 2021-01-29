#include "tcpsocket.h"
#include <QCoreApplication>
#include <QDir>
#include <iostream>
TcpSocket::TcpSocket(QObject *parent) : QObject(parent)
{

}

void TcpSocket::processMsg(QString msg)
{

}

void TcpSocket::sendMsg(QString str)
{
    const QString data=str+"\n";
    int datalength=data.size();
    QString header=QString("DataTypeWithSize:%1 %2\n").arg(0).arg(datalength);
    this->socket.write(header.toStdString().c_str(),header.size());
    this->socket.write(data.toStdString().c_str(),data.size());
    this->socket.flush();
}

void TcpSocket::sendFiles(QStringList filepaths)
{
    for(auto filepath:filepaths)
    {
        QString filename=filepath.section('/',-1);
        QFile f(filepath);
        if(!f.open(QIODevice::ReadOnly))
        {
            std::cout<<"can not read file "<<filename.toStdString().c_str()<<","<<f.errorString().toStdString().c_str()<<std::endl;
            continue;
        }
        QByteArray fileData=f.readAll();
        QString header=QString("DataTypeWithSize:%1 %2 %3\n").arg(1).arg(filename).arg(fileData.size());
        this->socket.write(header.toStdString().c_str(),header.size());
        this->socket.write(fileData,fileData.size());
        this->socket.flush();
        f.close();
    }
}

void TcpSocket::onread()
{
    if(!datatype.isFile)
    {
        if(datatype.datasize==0)
        {
            if(socket.canReadLine())
            {
                //read head
                QString msg=socket.readLine(100);
                if(!msg.endsWith('\n'))
                {
                    emit errorprocess(1);
                    return;
                }else
                {
                    msg=msg.trimmed();
                    if(msg.startsWith("DataTypeWithSize:"))
                    {
                        msg=msg.right(msg.size()-QString("DataTypeWithSize:").size());
                        QStringList paras=msg.split(' ',QString::SkipEmptyParts);
                        if(paras.size()==2&&paras[0]=="0")
                        {
                            datatype.datasize=paras[1].toInt();
                        }else if(paras.size()==3&&paras[0]=="1")
                        {
                            datatype.isFile=true;
                            datatype.datasize=paras[2].toInt();
                            datatype.filename=paras[1];
                        }else
                        {
                            errorprocess(2,msg);
                        }
                    }else
                    {
                        errorprocess(3,msg);
                    }
                    onread();
                }
            }
        }else
        {
            //read msg
            if(socket.bytesAvailable()>=datatype.datasize)
            {
                QString msg=socket.readLine(datatype.datasize);
                if(!msg.endsWith('\n'))
                {
                    errorprocess(1);
                    return;
                }else
                {
                    msg=msg.trimmed();
                    processMsg(msg);
                    resetDataType();
                    onread();
                }
            }
        }
    }else{
        if(socket.bytesAvailable()>=datatype.datasize)
        {
            {
                if(!QDir(QCoreApplication::applicationDirPath()+"/download").exists())
                    QDir(QCoreApplication::applicationDirPath()).mkdir("download");
                QString filePath=QCoreApplication::applicationDirPath()+"/download/"+datatype.filename;
                QFile file(filePath);
                file.open(QIODevice::WriteOnly);
                int length=file.write(socket.read(datatype.datasize));
                if(length!=datatype.datasize)
                {
                    qDebug()<<"Error:read file";
                }
                file.flush();
                file.close();
            }
            resetDataType();
            onread();
        }
    }
}

void TcpSocket::resetDataType()
{
    datatype.isFile=false;
    datatype.datasize=0;
    datatype.filename.clear();
}


void TcpSocket::errorprocess(int errcode,QString msg)
{
    if(errcode==1)
    {
        std::cout<<"ERROR:msg not end with '\n',We will disconnect the socket."<<std::endl;
    }else if(errcode==2)
    {
        std::cout<<QString("ERROR:%1 not format error,We will disconnect the socket.").arg(msg).toStdString().c_str()<<std::endl;
    }else if(errcode==3)
    {
        std::cout<<QString("ERROR:%1 not start with DataTypeWithSize:,We will disconnect the socket.").arg(msg).toStdString().c_str()<<std::endl;
    }
    this->socket.disconnectFromHost();
}
