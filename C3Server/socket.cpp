#include "socket.h"
#include "respond.h"
Socket::Socket(qintptr handle,QObject *parent)
{
    dataInfo.dataSize=0;
    dataInfo.dataReadedSize=0;
    dataInfo.stringSize=0;
    this->handle=handle;


}
void Socket::threadStart()
{
    socket=new QTcpSocket;
    socket->setSocketDescriptor(handle);
    connect(socket,SIGNAL(readyRead()),this,SLOT(onReadyread()));
    connect(socket,SIGNAL(disconnected()),this,SIGNAL(disconnect()));
}
void Socket::sendMsg(const QString& msg)const
{
    qDebug()<<"send:"+msg;
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
    socket->write(block);
    socket->waitForBytesWritten();
}

bool Socket::sendFile(const QString& filePath,const QString& fileName)
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
                block+=fileName.toUtf8();
                block+=filedata;
                total=block.size();
                datasize=total-2*sizeof (qint32)-filedata.size();
                if(datasize<0||datasize!=fileName.toUtf8().size())
                {
                    qFatal("datasize < 0");
                    return false;
                }
                block.clear();
            }
            QDataStream dts(&block,QIODevice::WriteOnly);
            dts<<(qint32)(total)<<(qint32)(datasize);
            block+=fileName.toUtf8();
            block+=filedata;

            socket->write(block);
            socket->waitForBytesWritten();
            qDebug()<<"send "<<filePath<<" success ";
            QFile(filePath).remove();
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
        return false;
    }
}

void Socket::onReadyread()
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

void Socket::readFile(const QString& fileName)
{
    QByteArray block=socket->read(dataInfo.dataSize-dataInfo.dataReadedSize);
    QString filePath=QCoreApplication::applicationDirPath()+"/tmp/"+fileName;
    QFile file(filePath);
    file.open(QIODevice::WriteOnly);
    file.write(block);
    file.close();
    dataInfo.dataSize=0;dataInfo.stringSize=0;dataInfo.dataReadedSize=0;//reset dataInfo
    qDebug()<<"Read file end "<<fileName;
    processFile(filePath,fileName);

}

void Socket::processMsg(const QString& msg)
{
    qDebug()<<"process:"+msg;
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
     * p0:脑图像名称
     * p1:是否是下一个/列表 0：下一个，1:列表
     * p2：预重建/校验 0:预重建，1：校验
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
    }else if(GetBBSWCRex.indexIn(msg)!=-1)
    {
        processBBSWC(GetBBSWCRex.cap(1).trimmed());
    }else if(ArborCheckRex.indexIn(msg)!=-1)
    {
        processProof(ArborCheckRex.cap(1).trimmed());
    }else if(GetArborResultRex.indexIn(msg)!=-1)
    {
        processResult(GetArborResultRex.cap(1).trimmed());
    }else if(BRINRESRex.indexIn(msg)!=-1){
        processRes(BRINRESRex.cap(1).trimmed());
    }else if(msg == "updateImage"){
        emit imageChanged();
    }else if(msg == "updateApo"){
        emit apoForPreChanged();
    }else if(msg == "updateFullSwc"){
        emit fullSwcChanged();
    }else if(msg == "updatearbor"){
        emit arborsSwcForProofChanged();
    }
    qDebug()<<"process Msg end";
}

void Socket::processBrain(const QString & paraString)
{
    sendMsg(paraString+";BRAINS;"+Respond::getBrainList(paraString));
}
void Socket::processBrainNumber(const QString & paraString)
{
    QStringList paras=paraString.split(";");
    QString brain_id=paras.at(0);
    bool nextOrList=paras.at(1).toInt()==0;
    bool preOrProof=paras.at(2).toInt()==0;

    if(nextOrList){
       auto list=Respond::nextAvailableNeuron(brain_id,preOrProof,socket->socketDescriptor());
       if(list.size()!=2) return;
       if(!sendFile(list[1],list[0]))
       {

       }
    }else{
       sendMsg(paras.at(2)+":List:"+Respond::getAllNeuronList(brain_id,preOrProof));
    }
}
void Socket::processImageBlock(const QString & paraString)
{
    auto list=Respond::getImageBlock(paraString+";"+QString::number(socket->socketDescriptor()));
    if(list.size()!=2) return;
    if(!sendFile(list[1],list[0]))
    {

    }
}
void Socket::processBBSWC(const QString &paraString)
{
    auto list=Respond::getSwcInBlock(paraString);
    if(list.size()!=2) return;
    if(!sendFile(list[1],list[0]))
    {

    }
}
void Socket::processProof(const QString &paraString)
{
    Respond::recordCheck(paraString);
}
void Socket::processResult(const QString &paraString)
{

}
void Socket::processRes(const QString &paraString)
{
    sendMsg("RES:"+Respond::getResCnt(paraString));
}
void Socket::processFile(const QString& filePath,const QString &fileName)//调用文件处理函数
{
    if(Respond::setSwcInBlock(filePath,fileName))
    {
        QFile(filePath).remove();
    }
}

