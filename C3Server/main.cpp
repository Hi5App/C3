#include <QCoreApplication>
#include "manage.h"
#include "customdebug.h"

int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);
    qInstallMessageHandler(customMessageHandler);
    ManageServer manageserver;
    if(!manageserver.listen(QHostAddress::Any,9000))
        exit(0);
    else
    {
        qDebug("manage server is started !");
        return a.exec();
    }
}
