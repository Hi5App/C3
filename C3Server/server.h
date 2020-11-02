#ifndef SERVER_H
#define SERVER_H
#include "QtNetwork"
#include "socket.h"
#include <QSettings>
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
private slots:
    void directoryUpdated(const QString &path);
    void fileupdated(const QString &updated);
private:
    QFileSystemWatcher *fileWatcher=nullptr;
    QMap<QString,QStringList> currentContentsMap;
    QSettings *lastImage=nullptr;

};

#endif // SERVER_H
