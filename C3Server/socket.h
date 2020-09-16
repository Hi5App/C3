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
    quint64 dataSize;
    quint64 stringSize;
    quint64 dataReadedSize;
    //数据包格式
    //dataSize+stringSize+STRING+(FILE);
};


class Socket : public QThread
{
    Q_OBJECT
public:
    Socket(qintptr socketID,QObject *parent=0);
    static QHash<QString,QReadWriteLock> fileLocks;
protected:
    void run() override;
    void sendMsg(const QString &msg)const;//
    void sendFile(const QString &filename,int type)const;//
private:
    QTcpSocket *socket;
    qintptr socketDescriptor;
    DataInfo dataInfo;
    void processMsg(const QString & msg);
    void readFile(const QString &filename);//need to do something
    QString currentDir()const;
    QString currentBrain(const int i)const;
    QString currentArbors()const;
    void getAndSendImageBlock(QString msg);
    void getAndSendSWCBlock(QString msg);
    void setSwcInBB(QString name,int x1,int x2,int y1,int y2,int z1,int z2,int cnt);
    void swcCheck(QString msg);
    void ArborCheck(QString msg);
    void getAndSendArborBlock(QString msg);
    void getAndSendArborSwcBlock(QString msg);


public slots:
    void onReadyRead();//
signals:


};

#endif // SOCKET_H
