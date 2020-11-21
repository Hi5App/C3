#ifndef RESPOND_H
#define RESPOND_H

#include <QString>
#include <QStringList>
#include <QSqlDatabase>
#include <QSqlQuery>
#include <QSqlError>
#include <QSet>
#include <QVariant>
#include <QCoreApplication>
#include <QDir>
#include <QProcess>
//namespace Lock {
//QMutex mutex;
//}

class Respond
{
public:
    Respond()=default;
    //(连接名，数据库名，ip地址，用户名，密码)
    static QSqlDatabase getDataBase();
    //返回可用的Brain
    static QString getBrainList(const QString type);
    //返回（文件名，文件路径）
    static QStringList nextAvailableNeuron(QString brainId,bool preOrProof,qintptr handle);
    //返回 所有Neuron列表 Name；Tag；Position/...
    static QString getAllNeuronList(QString brainId,bool preOrProof);
    //返回（文件名，文件路径）
    static QStringList getImageBlock(QString msg,QString fromNext="");
    //返回（文件名，文件路径）
    static QStringList getSwcInBlock(QString msg);
    //返回记录的成功/失败
    static bool recordCheck(QString msg);
    //返回分辨率数
    static QString getResCnt(QString paraString);

    static bool setSwcInBlock(QString fileName,QString filePath);

    static bool initDB();

    static int index;
    static QMutex *mutex;
};
inline void dirCheck(QString dirBaseName)
{
    if(!QDir(QCoreApplication::applicationDirPath()+"/"+dirBaseName).exists())
    {
        QDir(QCoreApplication::applicationDirPath()).mkdir(dirBaseName);
    }
}

inline QProcess* getProcess()
{
    return new QProcess;
}

inline void releaseProcess(QProcess *p)
{
    delete p;
}

inline QString cac_pos(const QFileInfo &info){
    //计算arbor的中心坐标
    //移动swc文件到FULLSWC文件夹
    return QString("%1;%2_%3_%4").arg(info.baseName()).arg(QString::number(0)).arg(QString::number(0)).arg(QString::number(0));
    //return name;pos
}

bool initTableForImage();
bool apoForPreChanged();
bool fullSwcChanged();
bool arborChanged();
#endif // RESPOND_H
