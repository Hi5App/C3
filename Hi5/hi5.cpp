#include "hi5.h"
#include <QSettings>
#include <QMessageBox>
HI5::HI5(QObject *parent) : QObject(parent)
{
       QString ip="";
       QString port = "";
       managesocket=new ManageSocket(ip,port,this);

       connect(managesocket,&ManageSocket::unconnected,[=]{
           QMessageBox::information(nullptr,"Network error","Connect server failed");
           managesocket->deleteLater();
           managesocket=nullptr;
       });

       connect(managesocket,&ManageSocket::disconnected,[=]{
           QMessageBox::information(nullptr,"Network error","Disconnect server");
           managesocket->deleteLater();
           managesocket=nullptr;
       });


       QSettings setting;
       if(setting.contains("user"))
       {
           QStringList userInfo=setting.value("User").toStringList();
           if(userInfo.size()==2&&slotLogin(userInfo.at(0),userInfo.at(1)))  return ;
       }

       unlogwidget=new UnLogWidget(nullptr);

       connect(unlogwidget,SIGNAL(signalLogin(QString,QString)),this,SLOT(slotLogin(QString,QString)));
       connect(unlogwidget,SIGNAL(signalRegister(QString,QString)),this,SLOT(slotRegister(QString,QString)));
       connect(unlogwidget,SIGNAL(signalForget(QString)),this,SLOT(slotForget(QString)));

       connect(managesocket,SIGNAL(signalLoginACK(QString)),this,SLOT(slotLoginACK(QString)));
       connect(managesocket,SIGNAL(signalRegisterACK(QString)),this,SLOT(slotRegisterACK(QString)));
       connect(managesocket,SIGNAL(signalForgetACK(QString)),this,SLOT(slotForgetACK(QString)));
       unlogwidget->show();
}

bool HI5::slotLogin(QString id,QString password)
{
    QString msg=QString("LOGIN:%1 %2").arg(id).arg(password);
    return managesocket->sendMsg(msg);
}

bool HI5::slotRegister(QString id,QString password)
{
    QString msg=QString("REGISTER:%1 %2").arg(id).arg(password);
    return managesocket->sendMsg(msg);
}

bool HI5::slotForget(QString id)
{
    QString msg=QString("FOEGETPASSWORD:%1").arg(id);
    return managesocket->sendMsg(msg);
}

void HI5::slotLoginACK(QString ackmsg)
{
    QStringList ackmsglist=ackmsg.split(';;');
    if(ackmsglist.size()&&ackmsglist[0]=="1")
    {
        if(ackmsglist.size()==4)
        {
            userinfo.userName=ackmsglist[1];
            userinfo.userId=ackmsglist[2];
            userinfo.password=ackmsglist[3];
            ackmsglist.removeAt(0);
            ackmsglist.removeAt(1);
            QSettings setting;
            setting.setValue("User",ackmsglist);
            if(!unlogwidget)
            {
                delete  unlogwidget;
                unlogwidget=nullptr;
            }
            inlogwidget=new InLogWidget({});
            connect(managesocket,&ManageSocket::signalUpdateFileWidgetData,inlogwidget,&InLogWidget::slotUpdateFileWidgetData);
            connect(managesocket,&ManageSocket::signalSetMpData,inlogwidget,&InLogWidget::slotSetMpData);

            connect(inlogwidget,&InLogWidget::updateFileListWidgetData,this,&HI5::slotUpdateFileLisWidgetData);
            connect(inlogwidget,&InLogWidget::downLoadFiles,this,&HI5::slotDownload);
            connect(inlogwidget,&InLogWidget::loadFiles,this,&HI5::slotLoadFiles);

            getCurrCollData();
            inlogwidget->show();
        }
    }else
    {
        if(unlogwidget)
        {
            QMessageBox::information(nullptr,"Pass Wrong","Please check your password,and retry");
        }
    }
}

void HI5::slotRegisterACK(QString ackmsg)
{

}

void HI5::slotForgetACK(QString ackmsg)
{

}

void HI5::slotUpdateFileLisWidgetData(QString conPath)
{
    QString msg="GETFILELIST:"+conPath;
    managesocket->sendMsg(msg);
}

void HI5::slotDownload(QStringList conPathLists)
{
    QString conPaths=conPathLists.join(";;");
    conPaths="DOWNLOAD:"+conPaths;
    managesocket->sendMsg(conPaths);
}

void HI5::slotLoadFiles(QStringList conPathLists,int type)
{
    QString conPaths=conPathLists.join(";;");
    (conPaths+=";;")+=QString::number(type);
    conPaths="LoadFiles:"+conPaths;
    managesocket->sendMsg(conPaths);
}

void HI5::getCurrCollData()
{
    QString msg="GETALLACTIVECollABORATE";
    managesocket->sendMsg(msg);
}
