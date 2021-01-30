#ifndef HI5_H
#define HI5_H

#include <QObject>
#include <tcpsocket.h>
#include <unlogwidget.h>
#include <inlogWidget.h>

class HI5 : public QObject
{
    Q_OBJECT
    struct UserInfo
    {
        QString userName;
        QString userId;
        QString password;
    };

public:
    explicit HI5(QObject *parent = nullptr);

private:
    UnLogWidget* unlogwidget=nullptr;
    InLogWidget* inlogwidget=nullptr;
    ManageSocket* managesocket=nullptr;
    UserInfo userinfo;


public slots:
    //slot with signal from unloginwidget
    bool slotLogin(QString id,QString password);
    bool slotRegister(QString id,QString password);
    bool slotForget(QString id);
    //slot with signal from managesocket
    void slotLoginACK(QString ackmsg);
    void slotRegisterACK(QString ackmsg);
    void slotForgetACK(QString ackmsg);
    //slot with inlogwidget
    void slotUpdateFileLisWidgetData(QString);
    void slotDownload(QStringList pathlist);
    void slotLoadFiles(QStringList pathlist,int type);
private:
    void getCurrCollData();
signals:

};

#endif // HI5_H
