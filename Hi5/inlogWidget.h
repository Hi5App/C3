#ifndef INLOGWIDGET_H
#define INLOGWIDGET_H

#include <QObject>
#include <filelistwidget.h>
#include <QLabel>
#include <QWidget>
#include <QLayout>
class InLogWidget : public QWidget
{
    Q_OBJECT
    struct RoomInfo
    {
        QString roomname;
        QString ip;
        QString port;
        QString master;
    };

public:
    explicit InLogWidget(QStringList userInfo,QWidget *parent = nullptr);
public slots:
    //for extern
    void slotUpdateFileWidgetData(QString);//not connect
    void slotSetMpData(QStringList roomInfoList);
    //for intern
    void slotDoubleClicked(QListWidgetItem *);
private:
    FileListWidget* fileWidget=nullptr;
    QListWidget *currCollListWidget=nullptr;

    QPushButton *fileBtn=nullptr;
    QPushButton *createBtn=nullptr;
    QPushButton *userBtn=nullptr;
    QLabel *userLabel=nullptr;

    QHBoxLayout *userlayout=nullptr;
    QHBoxLayout *sessionlayout=nullptr;
    QVBoxLayout *mainLayout=nullptr;

    std::map<QString,RoomInfo> mp;

    void updateCurrCollList();
signals:
    // for extern
    void updateFileListWidgetData(QString); //no
//    void updateCurrConWidgetData();
    void downLoadFiles(QStringList);//no
    void loadFiles(QStringList,int);//no
};

#endif // INLOGWIDGET_H
