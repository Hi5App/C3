#include "tcpsocket.h"
#include <QCoreApplication>
#include <QDir>
#include <iostream>
extern QString ip;
extern QString port;

void TcpSocket::sendMsg(QString str)
{
    if(socket.state()!=QAbstractSocket::ConnectedState&&!connectHost(ip,port))
    {
        unconnected();
    }

    const QString data=str+"\n";
    int datalength=data.size();
    QString header=QString("DataTypeWithSize:%1 %2\n").arg(0).arg(datalength);
    this->socket.write(header.toStdString().c_str(),header.size());
    this->socket.write(data.toStdString().c_str(),data.size());
    this->socket.flush();
}

void TcpSocket::sendFiles(QStringList filepaths)
{
    if(socket.state()!=QAbstractSocket::ConnectedState&&!connectHost(ip,port))
    {
        unconnected();
    }

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
        f.close();
        QString header=QString("DataTypeWithSize:%1 %2 %3\n").arg(1).arg(filename).arg(fileData.size());
        this->socket.write(header.toStdString().c_str(),header.size());
        this->socket.write(fileData,fileData.size());
        this->socket.flush();
    }
}

void TcpSocket::onread()
{
    if(!datatype.isFile)
    {
        if(datatype.datasize==0)
        {
            if(socket.bytesAvailable()>=1024||socket.canReadLine())
            {
                //read head
                QString msg=socket.readLine(1024);
                if(processHeader(msg))
                    onread();
            }
        }else
        {
            //read msg
            if(socket.bytesAvailable()>=datatype.datasize)
            {
                QString msg=socket.readLine(datatype.datasize);
                if(processMsg(msg))
                {
                    onread();
                }
            }
        }
    }else if(socket.bytesAvailable())
    {
        int ret = 0;
        auto bytes=socket.read(datatype.datasize);
        if(datatype.f->write(bytes)==bytes.size())
        {
            datatype.datasize-=bytes.size();
            if(datatype.datasize==0)
            {
                datatype.f->close();
                delete datatype.f;
                datatype.f=nullptr;
                resetDataType();
                processFile(datatype.f->fileName());
                onread();
            }else if(datatype.datasize<0)
            {
                ret = 6;
            }
        }else{
            ret = 5;
        }
        if(ret!=0)
        {
            errorprocess(ret,datatype.f->fileName());
        }
    }
}

void TcpSocket::resetDataType()
{
    datatype.isFile=false;
    datatype.datasize=0;
    datatype.filename.clear();
    if(datatype.f)
    {
        delete datatype.f;
        datatype.f=nullptr;
    }

}

bool TcpSocket::processMsg(const QString msg)
{
    if(msg.endsWith('\n')) return true;
    else {
        errorprocess(1,msg);
    }
}

bool TcpSocket::connectHost(QString ip,QString port)
{
    socket.connectToHost(ip,port.toUInt());
    return socket.waitForConnected(30000);
}

bool TcpSocket::processHeader(const QString rmsg)
{
    int ret = 0;
    if(rmsg.endsWith('\n'))
    {
        QString msg=rmsg.trimmed();
        if(msg.startsWith("DataTypeWithSize:"))
        {
            msg=msg.right(msg.size()-QString("DataTypeWithSize:").size());
            QStringList paras=msg.split(";;",QString::SkipEmptyParts);
            if(paras.size()==2&&paras[0]=="0")
            {
                datatype.datasize=paras[1].toInt();
            }else if(paras.size()==3&&paras[0]=="1")
            {
                datatype.isFile=true;
                datatype.filename=paras[1];
                datatype.datasize=paras[2].toInt();
                if(!QDir(QCoreApplication::applicationDirPath()+"/tmp").exists())
                    QDir(QCoreApplication::applicationDirPath()).mkdir("tmp");
                QString filePath=QCoreApplication::applicationDirPath()+"/tmp/"+datatype.filename;
                datatype.f=new QFile(filePath);
                if(!datatype.f->open(QIODevice::WriteOnly))
                    ret=4;
            }else
            {
                ret=3;
            }
        }else
        {
            ret = 2;
        }
    }else
    {
        ret = 1;
    }
    if(!ret) return true;
    errorprocess(ret,rmsg.trimmed()); return false;
}

void TcpSocket::errorprocess(int errcode,QString msg)
{
    //errcode
    //1:not end with '\n';
    //2:not start wth "DataTypeWithSize"
    //3:msg not 2/3 paras
    //4:cannot open file
    //5:read socket != write file
    //6:next read size < 0
    if(errcode==1)
    {
        std::cerr<<"ERROR:msg not end with '\n',";
    }else if(errcode==2)
    {
        std::cerr<<QString("ERROR:%1 not start wth \"DataTypeWithSize\"").arg(msg).toStdString().c_str();
    }else if(errcode==3)
    {
        std::cerr<<QString("ERROR:%1 not 2/3 paras").arg(msg).toStdString().c_str();
    }else if(errcode==4){
        std::cerr<<QString("ERROR:%1 cannot open file").arg(msg).toStdString().c_str();
    }else if(errcode==5){
        std::cerr<<QString("ERROR:%1 read socket != write file").arg(msg).toStdString().c_str();
    }else if(errcode==6){
        std::cerr<<QString("ERROR:%1 next read size < 0").arg(msg).toStdString().c_str();
    }
    std::cerr<<",We will disconnect the socket\n";
    this->socket.disconnectFromHost();
}


bool ManageSocket::processFile(const QString filepath)
{
    return true;
}

bool ManageSocket::processMsg(const QString msg)
{
    if(TcpSocket::processMsg(msg)==false) return false;
    return true;
}


bool MessageSocket::processFile(const QString filepath)
{
return true;
}

bool MessageSocket::processMsg(const QString msg)
{
    if(TcpSocket::processMsg(msg)==false) return false;
    return true;
}



