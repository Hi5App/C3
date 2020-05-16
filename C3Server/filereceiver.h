#ifndef FILERECEIVER_H
#define FILERECEIVER_H
#include <QtNetwork>
#include <QFile>

class FileReceiveSocket:public QTcpSocket
{
    Q_OBJECT
public:
    explicit FileReceiveSocket(QObject *parent=0);
public slots:
    void readFile();
private:
    quint64 totalsize=0;
    quint64 filenamesize=0;
    quint64 m_bytesreceived=0;
};

class FileReceiveServer:public QTcpServer
{
    Q_OBJECT
public:
    explicit FileReceiveServer(QObject *parent=0);
    ~FileReceiveServer();
public slots:
    void onSocketDisconnected();
private:
    void incomingConnection(int handle) override;
private:
    QList<FileReceiveSocket *> list;

};

#endif // FILERECEIVER_H
