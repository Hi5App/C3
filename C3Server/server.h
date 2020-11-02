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
    void directoryUpdated(const QString &path);
private:
    QFileSystemWatcher fileWatcher;
    QSqlDatabase db;
private:
    void dataBaseInit();
    bool isTableExist(QString);
    //table0 ImageRes
    bool imageInit();//初始化C3数据库的图像表
    bool imageChanged();//图像文件夹发生变化
    bool apoChanged();//apo输入文件夹有新增数据
    bool swcChanged();//swc输入文件夹，该文件夹放置已完整重建等待检查的神经元


    bool anoInit();


};
inline Server::~Server()
{

}

inline QString cac_pos(const QString &filePath){
    //计算arbor的中心坐标
    //移动swc文件到FULLSWC文件夹
    QFileInfo info(filePath);
    return QString("%1:%2_%3_%4");
}
#endif // SERVER_H
