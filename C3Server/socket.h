#ifndef SOCKET_H
#define SOCKET_H

#include <QTcpSocket>
#include <QThread>

#include <QRegExp>
#include <QDir>
#include <QFileInfo>
#include "basic_c_fun/basic_surf_objs.h"
#include "neuron_editing/v_neuronswc.h"
#include "neuron_editing/neuron_format_converter.h"
#include <QHash>
#include <QReadWriteLock>
struct DataInfo{
    qint32 dataSize;
    qint32 stringSize;
    qint32 dataReadedSize;
    //数据包格式
    //dataSize+stringSize+STRING+(FILE);
};
//需要去分辨率的层数


class Socket : public QThread
{
    Q_OBJECT
public:
    Socket(qintptr socketID,QObject *parent=0);//
protected:
    void run() override; //
private:
    QTcpSocket *socket;
    qintptr socketDescriptor;
    DataInfo dataInfo;    
public slots:
    void onReadyRead();//
private:
    //translate function
    void sendMsg(const QString &msg)const;//
    bool sendFile(const QString &filePath,const QString filename)const;//
    //read file
    void readFile(const QString &filename);//
    void processFile(QString filepath,QString filename);
    //send file
    void SendFile(const QString &filename,int type)const;
    //Msg Process
    void processMsg(const QString & msg);//需要确定消息格式
    void processBrain(const QString & paraString);
    void processBrainNumber(const QString & paraString);
    void processImageBlock(const QString & paraString);
    void processBBSWC(const QString &paraString);
    void processProof(const QString &paraString);
    void processResult(const QString &paraString);
    void processRes(const QString &paraString);

    void funcNext(QString brain_id, bool preOrProof);
    void funcList(QString brain_id, bool preOrProof);
    void getAndSendImageBlock(QString msg,QString N="0");
    void getAndSendSWCBlock(QString msg);
    void ArborCheck(QString msg);
//    QString currentDir()const;

    QString getNeuronList(const QString brain_id,const int i)const;
    QString currentArbors()const;


    bool setSwcInBB(QString name,QString filepath,int x1,int x2,int y1,int y2,int z1,int z2,int cnt);
    void swcCheck(QString msg);

    void getAndSendArborBlock(QString msg);
    void getAndSendArborSwcBlock(QString msg);


    void currentBrain(const int i)const;//发送的是脑图的id

//
//private:
//    static const int ProcessCnt=10;
//    static QProcess p[ProcessCnt];


};

#endif // SOCKET_H
