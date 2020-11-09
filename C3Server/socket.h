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

//    QRegExp ImageDownRex("(.*):choose3_(.*).\n");//要求发送全脑图像列表//db
//    /*
//     * "0":表示预重建
//     * "1":表示校验
//     */
//    QRegExp BrainNumberRex("(.*):BrainNumber.\n");//根据脑图的id和模式返回神经元列表
//    /*模式：2位数
//     * 第一位决定是否是要求列表还是下一个可用的神经元
//     * 0:下一个
//     * 1:列表
//     * 第二位决定预重建/校验
//     * 0:预重建
//     * 1:校验
//     */
//    QRegExp ImgBlockRex("(.*):imgblock.\n");//选定的神经元的名称，返回图像
//    QRegExp GetBBSWCRex("(.*):GetBBSwc.\n");//获取局部神经元处理数据
//    QRegExp ArborCheckRex("(.*):ArborCheck.\n");
//    QRegExp GetArborResultRex("(.*):GetArborResult.\n");

//    qDebug()<<"MSG:"<<msg;

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
