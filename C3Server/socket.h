#ifndef SOCKET_H
#define SOCKET_H

#include <QObject>
#include <QtNetwork>

struct DataInfo
{
    qint32 dataSize;
    qint32 stringSize;
    qint32 dataReadedSize;
};

class Socket:public QObject
{
    Q_OBJECT
public:
    Socket(qintptr handle,QObject *parent=nullptr);
public slots:
    void onReadyread();
    void threadStart();
private:    qintptr socketDescriptor;
    QTcpSocket *socket;
    qintptr handle;
    DataInfo dataInfo;

    void sendMsg(const QString& msg)const;
    bool sendFile(const QString& filepath,const QString& fileName);

    void readFile(const QString& fileName);

    void processMsg(const QString& msg);
    void processBrain(const QString & paraString);
    void processBrainNumber(const QString & paraString);
    void processImageBlock(const QString & paraString);
    void processBBSWC(const QString &paraString);
    void processProof(const QString &paraString);
    void processResult(const QString &paraString);
    void processRes(const QString &paraString);
    void processFile(const QString& filePath,const QString &fileName);//调用文件处理函数

signals:
    void disconnect();
    void imageChanged();
    void apoForPreChanged();
    void fullSwcChanged();
    void arborsSwcForProofChanged();
};

#endif // SOCKET_H
