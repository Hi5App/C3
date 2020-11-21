#include "server.h"
#include "socket.h"
#include "threadpool.h"
#include <QtGlobal>
#include "respond.h"
bool Server::init()
{
    return initTableForImage();
}

void Server::incomingConnection(qintptr handle)
{
    Socket *socket=new Socket(handle);
    auto thread=ThreadPool::GetInstance()->getThread();
    socket->moveToThread(thread);
    QObject::connect(socket,&Socket::disconnect,[=]{
        ThreadPool::GetInstance()->releaseThread(thread);
    });//需要完善
    connect(socket,SIGNAL(disconnect()),socket,SLOT(deleteLater()));
    connect(socket,SIGNAL(imageChanged()),this,SLOT(slotImageChanged()));
    connect(socket,SIGNAL(apoForPreChanged()),this,SLOT(slotApoForPreChanged()));
    connect(socket,SIGNAL(fullSwcChanged()),this,SLOT(slotFullSwcChanged()));
    connect(socket,SIGNAL(arborsSwcForProofChanged()),this,SLOT(slotArborSwcForProofChanged()));
    thread->start();
}

void Server::slotImageChanged()
{
    if(!ImageChanged()){
        qFatal("change image failed!");
    }
}
void Server::slotApoForPreChanged()
{
    if(!ApoForPreChanged())
    {
        qFatal("change apo for pre_restruction failed");
    }
}
void Server::slotFullSwcChanged()
{
    if(!FullSwcChanged())
    {
        qFatal("change full swc failed");
    }
}
void Server::slotArborSwcForProofChanged()
{
    if(!ArborSwcForProofChanged())
    {
        qFatal("change arbor swc for proof failed");
    }
}

bool Server::ImageChanged()
{
    return initTableForImage();
}
bool Server::ApoForPreChanged()
{
    return apoForPreChanged();
}
bool Server::FullSwcChanged()
{
    return fullSwcChanged();
}
bool Server::ArborSwcForProofChanged()
{
    return arborChanged();
}

