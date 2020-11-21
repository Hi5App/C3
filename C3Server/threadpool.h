#ifndef THREADPOOL_H
#define THREADPOOL_H

#include <QObject>
#include <QVector>
class ThreadPool final:public QObject
{
    Q_OBJECT
public:

    static ThreadPool* GetInstance();
    static void resetInstance();
    static QThread* getThread();
    static bool releaseThread(QThread *thread);
    ~ThreadPool();
private:
    ThreadPool();
    static ThreadPool* instance;
    QVector<QThread* > vector;

};

#endif // THREADPOOL_H
