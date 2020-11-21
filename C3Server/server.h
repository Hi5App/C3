#ifndef SERVER_H
#define SERVER_H

#include <QObject>
#include <QtNetwork>


/*
 * Server
 * 监听端口
 * 响应client发来的输入数据变动信息，更新部分数据库
 */
class Server:public QTcpServer
{
    Q_OBJECT
public:
    Server()=default;
    ~Server()=default;
    bool init();
private:
    void incomingConnection(qintptr handle) override;
protected slots:
    void slotImageChanged();
    void slotApoForPreChanged();
    void slotFullSwcChanged();
    void slotArborSwcForProofChanged();
private:
    bool ImageChanged();
    bool ApoForPreChanged();
    bool FullSwcChanged();
    bool ArborSwcForProofChanged();
};

#endif // SERVER_H
