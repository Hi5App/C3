#include "inlogWidget.h"
#include <QSettings>

InLogWidget::InLogWidget(QStringList userInfo,QWidget *parent) : QWidget(parent)
{
    fileBtn=new QPushButton(tr("文件列表"),this);
    createBtn=new QPushButton(tr("创建新协作"),this);
    userBtn=new QPushButton(userInfo.at(0),this);
    userLabel=new QLabel(userInfo.at(0),this);
    currCollListWidget=new QListWidget(this);

    currCollListWidget=new QListWidget(this);

    userlayout=new QHBoxLayout(this);
    userlayout->addWidget(userBtn);
    userlayout->addWidget(userLabel);

    sessionlayout=new QHBoxLayout(this);
    sessionlayout->addWidget(createBtn);
    sessionlayout->addWidget(fileBtn);

    mainLayout=new QVBoxLayout(this);
    mainLayout->addLayout(userlayout);
    mainLayout->addLayout(sessionlayout);
    mainLayout->addWidget(currCollListWidget);

    this->setLayout(mainLayout);

    connect(fileBtn,&QPushButton::pressed,this,[=]{
      if(fileWidget)
      {
          disconnect(fileWidget);
          delete fileWidget;
          fileWidget=nullptr;
      }
      fileWidget=new FileListWidget(0);
      connect(fileWidget,&FileListWidget::updateData,this,&InLogWidget::updateFileListWidgetData);
      connect(fileWidget,&FileListWidget::downloadFiles,this,&InLogWidget::downLoadFiles);

    });

    connect(createBtn,&QPushButton::pressed,this,[=]{
      if(fileWidget)
      {
          disconnect(fileWidget);
          delete fileWidget;
          fileWidget=nullptr;
      }
      fileWidget=new FileListWidget(0);
      connect(fileWidget,&FileListWidget::updateData,this,&InLogWidget::updateFileListWidgetData);
      connect(fileWidget,&FileListWidget::loadFiles,this,&InLogWidget::loadFiles);
    });
    this->show();

}

void InLogWidget::slotUpdateFileWidgetData(QString)//not connect
{
    if(fileWidget)
    {
        fileWidget->setData({},{});
    }
}

void InLogWidget::slotSetMpData(QStringList roomInfoList)
{
    mp.clear();
    for(auto roomInfoStr:roomInfoList)
    {
        QStringList roominfolist=roomInfoStr.split(";;");
        if(roominfolist.size()!=4)
        {
            mp.clear();
            return;
        }
        RoomInfo roominfo={roominfolist[0],roominfolist[1],roominfolist[2],roominfolist[3]};
        mp[roominfo.roomname]=roominfo;
    }
    updateCurrCollList();
}

void InLogWidget::slotDoubleClicked(QListWidgetItem * item)
{
    RoomInfo roomInfo=mp[item->text().split('\n').at(0)];
    //need to do more
}

void InLogWidget::updateCurrCollList()
{
    currCollListWidget->clear();
    for(auto iter=mp.begin();iter!=mp.end();iter++)
    {
        QListWidgetItem *item=new QListWidgetItem(currCollListWidget);
        item->text()=iter->first+"\n"+iter->second.master;
    }
}



