#ifndef TCPSOCKET_H
#define TCPSOCKET_H

#include <QTcpSocket>

class TcpSocket : public QObject
{
    Q_OBJECT
    struct DataType{
        bool isFile=false;//false msg,true file
        qint64 datasize=0;
        QString filename;
    };
public:

    explicit TcpSocket(QObject *parent = nullptr);
    void sendMsg(QString str);
    void sendFiles(QStringList filepath);
public slots:
    void onread();
private:
    QTcpSocket socket;
    DataType datatype;
    void resetDataType();
    void errorprocess(int,QString msg="");
    void processMsg(QString);
signals:


};

#endif // TCPSOCKET_H
