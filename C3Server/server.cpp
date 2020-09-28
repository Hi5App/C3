#include "server.h"

Server::Server(QObject *parent):QTcpServer(parent)
{
    qDebug()<<"Thread ID"<<QThread::currentThreadId();
}

Server::~Server()
{

}

void Server::incomingConnection(qintptr handle)
{
    Socket* socket=new Socket(handle);
    qDebug()<<handle<<" connected.";
    socket->start();
}
