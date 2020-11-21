#include "threadpool.h"
#include <QThread>
ThreadPool* ThreadPool::instance = nullptr;

QThread* ThreadPool::getThread()
{
    auto instance=ThreadPool::GetInstance();
    QThread *thread = new QThread(nullptr);
    connect(thread,SIGNAL(finished()),thread,SLOT(deleteLater()));
    instance->vector.push_back(thread);
    return  thread;
}

ThreadPool* ThreadPool::GetInstance()
{
    if(ThreadPool::instance == nullptr)
    {
        instance = new ThreadPool;
    }
    return instance;
}

bool ThreadPool::releaseThread(QThread *thread)
{
    auto instance=ThreadPool::GetInstance();
    auto index=instance->vector.indexOf(thread);
    if(index==-1)
        return false;
    else{
        instance->vector.remove(index);
        thread->quit();
        return true;
    }
}

void ThreadPool::resetInstance()
{
    if(ThreadPool::instance!=nullptr)
    {
        delete ThreadPool::instance;
        ThreadPool::instance = nullptr;
    }
    return;
}

ThreadPool::~ThreadPool()
{
    for(auto p:vector){
        if(p)
        {
            p->quit();
        }
    }
    vector.clear();
}

ThreadPool::ThreadPool()
{
    vector.clear();
}

