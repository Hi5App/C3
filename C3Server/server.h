#ifndef SERVER_H
#define SERVER_H
#include "QtNetwork"
#include "socket.h"

//struct SetInfo
//{
//    QString name;
//    int x1, x2, y1, y2, z1, z2;
//};

class Server : public QTcpServer
{
    Q_OBJECT
public:
    Server(QObject *parent=0);
    ~Server();
private:
    void incomingConnection(qintptr handle) override;
//    void setSwcInBB(QString name,int x1,int x2,int y1,int y2,int z1,int z2);
private slots:
//    bool setSwcInBBByStruct(QString name,int x1,int x2,int y1,int y2,int z1,int z2);
private:
//    QList<SetInfo> setInfos;

};

#endif // SERVER_H
