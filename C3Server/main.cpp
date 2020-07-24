#include <QCoreApplication>
#include "server.h"

#include <iostream>

//image dir:put brain image
//image
//  --brainnumber dir
//    --RES1
//    --RES2

//data dir:put anotation data such as swc
//tmp dir:put some temp file(after use will delete)
//neuronInfo dir:a whole brain info save as a .txt in it,will send to user.
int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);
    Server server;
    if(!server.listen(QHostAddress::Any,9000))
        exit(0);
    else
        std::cout<<"Server start:Version 1.0(HL)\n";
    return a.exec();
}
