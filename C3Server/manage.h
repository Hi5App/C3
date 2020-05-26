#ifndef MANAGE_H
#define MANAGE_H

#include "filereceiver.h"
#include "filesender.h"

#include <QtNetwork>
#include <QRegExp>
#include <QDir>
#include <QFileInfo>
#include <QFileInfoList>

class ManageSocket:public QTcpSocket
{
    Q_OBJECT
public:
    explicit ManageSocket(QObject *parent=0);
public slots:
    void readManage();
private:
    QString currentDir();
    QString currentDirImg();
signals:
    void startReceiveServer();
    void sendFile(QString,QString);
};

class ManageServer:public QTcpServer
{
    Q_OBJECT
public:
    explicit ManageServer(QObject *parent=0);
    ~ManageServer();
private:
    void incomingConnection(qintptr handle) override;
public slots:
    void onSocketDisconnected();
    void startReceiveServer();
    void sendFile(QString ip,QString filename);
    void FileConnected(QString);
private:
    QList<ManageSocket *> list;
    FileSenderServer sendServer;
    FileReceiveServer receiveServer;
};


#endif // MANAGE_H
