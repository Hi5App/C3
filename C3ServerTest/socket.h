#ifndef SOCKET_H
#define SOCKET_H

#include <QTcpSocket>
#include <QString>
#include <QObject>
#include <QThread>
class Socket:public QThread
{
    Q_OBJECT
public:
    Socket(QObject *parent=nullptr);
protected:
    void run();
public slots:
    void onreadyRead();
private:
    QTcpSocket *socket=nullptr;
    QString string;

    void sendMsg(QString);
    quint64 dataSize=0;
    quint64 stringSize=0;
    quint64 dataReadedSize=0;
    QStringList msgList;
    QString ip="223.3.33.234";
//    QString ip="127.0.0.1";
    int port=8000;
    QString order;
    int i=0;

};

#endif // SOCKET_H
