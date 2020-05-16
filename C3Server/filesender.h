#ifndef FILESENDER_H
#define FILESENDER_H

#include <QtNetwork>
#include <QFile>
#include <QList>


class FileSenderSocket:public QTcpSocket
{
    Q_OBJECT
public:
    explicit FileSenderSocket(QObject *parent=0);
    void sendFile(QString filepath,QString filename);
};

class FileSenderServer:public QTcpServer
{
    Q_OBJECT
public:
    explicit FileSenderServer(QObject *parent=0);
    void sendAnnotation(QString ip,QString filename);
    void sendRAW(QString ip,QString filename);
    ~FileSenderServer();

public slots:
    void onSocketDisconnected();
private:
    void incomingConnection(int handle) override;
private:
    QList<FileSenderSocket *> list;
};


#endif // FILESENDER_H
