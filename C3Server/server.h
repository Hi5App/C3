#ifndef SERVER_H
#define SERVER_H
#include "QtNetwork"
#include "socket.h"
#include <QSettings>
#include <QSqlDatabase>
#include <array>

class Server : public QTcpServer
{
    Q_OBJECT
public:
    Server(QObject *parent=0);
    ~Server();
private:
    void incomingConnection(qintptr handle) override;

private slots:
//    void directoryUpdated(const QString &path);
private:
    QFileSystemWatcher fileWatcher;
    QSqlDatabase db;
private:
//    bool isTableExist(QString);
    bool initImage();//初始化C3数据库的图像表
    bool initPreApo();
    bool initPreSwc();
    bool initReswc();
    bool initCheck();
};

inline Server::~Server()
{

}

inline QString cac_pos(const QFileInfo &info){
    //计算arbor的中心坐标
    //移动swc文件到FULLSWC文件夹
    return QString("%1;%2_%3_%4").arg(info.baseName()).arg(QString::number(0)).arg(QString::number(0)).arg(QString::number(0));
    //return name;pos
}
#endif // SERVER_H
