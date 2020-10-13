#include <QCoreApplication>
#include "socket.h"
const int N=96;
int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);
    Socket t[N];
    for(auto &ts:t)
    {
        ts.start();
    }
    return a.exec();
}
