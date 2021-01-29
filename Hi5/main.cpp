#include <filelistwidget.h>

#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    a.setWindowIcon(QIcon(":/icon/Hi5.jpg"));
    FileListWidget filewidget(0);
    filewidget.setfilename({"1","2"},{0,1});
    filewidget.updateWidget();
    return a.exec();
}
