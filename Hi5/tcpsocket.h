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
    explicit TcpSocket(QObject *parent = nullptr):QObject(parent)
    {
        connect(&socket,&QTcpSocket::disconnected,this,&TcpSocket::disconnected);
    }
    virtual ~TcpSocket()=default;

    virtual bool processMsg(const QString);
    virtual bool processFile(const QString)=0;

    void sendMsg(QString str);
    void sendFiles(QStringList filepath);

public slots:
    void onread();
private:
    void resetDataType();
    bool connectHost(QString ip,QString port);
    bool processHeader(const QString msg);
    void errorprocess(int,QString msg="");

    QTcpSocket socket;
    DataType datatype;
signals:
    //for extern
    void unconnected();
    void disconnected();
};

class ManageSocket:public TcpSocket
{
    Q_OBJECT
public:
    explicit ManageSocket(QObject *parent=nullptr):TcpSocket(parent){}
    bool processMsg(const QString);
    bool processFile(const QString);
    ~ManageSocket()=default;
public slots:

signals:
     void signalLoginACK(QString ackmsg);
     void signalRegisterACK(QString ackmsg);
     void signalForgetACK(QString ackmsg);
};

class MessageSocket:public TcpSocket
{
    Q_OBJECT
    explicit MessageSocket(QObject *parent=nullptr) :TcpSocket(parent){}
     bool processMsg(const QString);
     bool processFile(const QString);
    ~MessageSocket()=default;
};





#endif // TCPSOCKET_H
