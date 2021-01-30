#include <filelistwidget.h>

#include <QApplication>

QString ip;
QString port;
int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    QCoreApplication::setOrganizationName("BrainTell");

    QCoreApplication::setOrganizationDomain("BrainTell.com");

    QCoreApplication::setApplicationName("Hi5");
    a.setWindowIcon(QIcon(":/icon/Hi5.jpg"));
    FileListWidget filewidget(0);
    filewidget.setData({"1","2"},{0,1});
    filewidget.updateWidget();
    return a.exec();
}
