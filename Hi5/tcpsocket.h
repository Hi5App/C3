#ifndef TCPSOCKET_H
#define TCPSOCKET_H

#include <QTcpSocket>
#include <QFile>
#include <QMessageBox>
class TcpSocket : public QObject
{
    Q_OBJECT
    struct DataType{
        bool isFile=false;//false msg,true file
        qint64 datasize=0;
        QString filename;
        QFile *f=nullptr;
    };
public:
    explicit TcpSocket(QString ip,QString port,QObject *parent = nullptr)
        :ip(ip),port(port),QObject(parent)
    {
        connect(&socket,&QTcpSocket::disconnected,this,&TcpSocket::disconnected);
        connectHost(this->ip,this->port);
    }
    virtual ~TcpSocket()=default;

    virtual bool processMsg(const QString);
    virtual bool processFile(const QString)=0;

    bool sendMsg(QString str);
    bool sendFiles(QStringList filepath);
    bool connectHost(QString ip,QString port);
public slots:
    void onread();
private:
    void resetDataType();

    bool processHeader(const QString msg);
    void errorprocess(int,QString msg="");

    QTcpSocket socket;
    DataType datatype;
    QString ip;
    QString port;
signals:
    //for extern
    void unconnected();
    void disconnected();
};

class ManageSocket:public TcpSocket
{
    Q_OBJECT
public:
    explicit ManageSocket(QString ip,QString port,QObject *parent = nullptr)
        :TcpSocket(ip,port,parent){}
    bool processMsg(const QString);
    bool processFile(const QString);
    ~ManageSocket()=default;
public slots:

signals:
     void signalLoginACK(QString ackmsg);
     void signalRegisterACK(QString ackmsg);
     void signalForgetACK(QString ackmsg);
     void signalUpdateFileWidgetData(QString);
     void signalSetMpData(QStringList roomInfoList);
};

class MessageSocket:public TcpSocket
{
    Q_OBJECT
    explicit MessageSocket(QString ip,QString port,QObject *parent = nullptr)
        :TcpSocket(ip,port,parent){}
     bool processMsg(const QString);
     bool processFile(const QString);
    ~MessageSocket()=default;
};





#endif // TCPSOCKET_H
