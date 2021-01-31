#include "filelistwidget.h"
#include <QListWidgetItem>
#include <QIcon>
#include <QSettings>
#include <iostream>
FileListWidget::FileListWidget(char type,std::vector<QString> filenameList,
    std::vector<char> filetypeList,QWidget *parent)
    :type(type) ,QWidget(parent)
{
    listwidget=new QListWidget(this);
    if(type==0)
    {
        typeBtn=new QPushButton("Download selected files",listwidget);
    }else if(type==1)
    {
        typeBtn=new QPushButton("Load selected Ano",listwidget);
    }else{
        std::cerr<<"FileListWidget type!= 0/1"<<std::endl;
    }
    cancelBtn=new QPushButton("Cancel",listwidget);
    btnlayout=new QHBoxLayout;
    currConPathLabel=new QLabel(this);

    btnlayout->addWidget(typeBtn);
    btnlayout->addWidget(cancelBtn);

    mainLayout=new QVBoxLayout;
    mainLayout->addWidget(currConPathLabel);
    mainLayout->addWidget(listwidget);
    mainLayout->addLayout(btnlayout);
    this->setLayout(mainLayout);
    setData(filenameList,filetypeList);
    QSettings setting;
    QString temp="/";
    if(setting.contains("lastpath"))
        temp=setting.value("lastpath").toString();
    setCurrConPath(temp);

    connect(typeBtn,&QPushButton::pressed,this,&FileListWidget::slotTypeBtnPressed);
}

bool FileListWidget::setData(std::vector<QString> filenameList,std::vector<char> filetypeList)
{
    if(filenameList.size()!=filetypeList.size()) return false;
    for(int i=0;i<filenameList.size();i++)
    {
        name_type.insert(std::pair<QString,char>(filenameList.at(i),filetypeList.at(i)));
    }
    updateWidget();
    return true;
}

void FileListWidget::slotTypeBtnPressed()
{
    auto selectItems=listwidget->selectedItems();

    if(type==0&&selectItems.size())
    {
        QStringList res;
        for(auto item:selectItems)
        {
            res.push_back(currConPath+"/"+item->text());
            emit downloadFiles(res);
        }
    }else if(type==1)
    {
        if(selectItems.size()==1&&selectItems[0]->text().endsWith(".ano"))
        {
            bool ok=false;
            QString anoName;
            anoName=QInputDialog::getText(this,"从零开始","请输入文件名",QLineEdit::Normal,"",&ok);
            if(ok)
            {
                //从零开始
                if(!anoName.isEmpty())
                    emit loadFiles({currConPath+"/"+selectItems[0]->text()
                        ,currConPath+"/"+anoName},0);
                else
                    QMessageBox::information(this,"Error","New ano name is empty");
            }else
            {
                anoName=QInputDialog::getText(this,"是否新建文件继续","请输入文件名",QLineEdit::Normal,"",&ok);
                if(ok)
                {
                    if(!anoName.isEmpty())
                        emit loadFiles({currConPath+"/"+selectItems[0]->text()
                        ,currConPath+"/"+anoName},1);
                    else
                        QMessageBox::information(this,"Error","New ano name is empty");
                }else if(!ok)
                {
                    emit loadFiles({currConPath+"/"+selectItems[0]->text(),
                        currConPath+"/"+selectItems[0]->text()},2);
                }
            }
        }

    }
}

void FileListWidget::slotDoubleClicked(QListWidgetItem * item)
{
    QString text=item->text();
    if(text==".")
        setCurrConPath(currConPath);
    else if(text=="..")
        setCurrConPath(currConPath.section('/',0,-2));
    else
    {
        int type=name_type.find(text)->second;
        if(type==0)
        {
            setCurrConPath(currConPath+"/"+text);
        }
    }
}

void FileListWidget::setCurrConPath(QString str)
{
    currConPath=str;
    currConPathLabel->setText(currConPath);
    emit updateData(currConPath);
}

void FileListWidget::updateWidget()
{
    listwidget->clear();
    const QIcon dirIcon(":/icon/dir.png");
    const QIcon fileIcon(":/icon/file.png");
    QListWidgetItem *item1=new QListWidgetItem(dirIcon,".",listwidget);
    QListWidgetItem *item2=new QListWidgetItem(dirIcon,"..",listwidget);

    for(auto iter=name_type.begin();iter!=name_type.end();iter++)
    {
        if(iter->second==0)
        {
            QListWidgetItem *item=new QListWidgetItem(dirIcon,iter->first,listwidget);
        }else if(iter->second==1)
        {
            if(type==0)
                QListWidgetItem *item=new QListWidgetItem(fileIcon,iter->first,listwidget);
            else if(type==1&&iter->first.endsWith(".ano"))
                QListWidgetItem *item=new QListWidgetItem(fileIcon,iter->first,listwidget);
        }
    }
    this->show();
}







