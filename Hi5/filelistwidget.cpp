#include "filelistwidget.h"
#include <QListWidgetItem>
#include <QIcon>
#include <QSettings>
#include <iostream>
FileListWidget::FileListWidget(char type,std::vector<std::string> filenameList,
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
    btnlayout=new QHBoxLayout(this);
    currConPathLabel=new QLabel(this);

    btnlayout->addWidget(typeBtn);
    btnlayout->addWidget(cancelBtn);

    mainLayout=new QVBoxLayout(this);
    mainLayout->addWidget(currConPathLabel);
    mainLayout->addWidget(listwidget);
    mainLayout->addLayout(btnlayout);
    this->setLayout(mainLayout);
    setData(filenameList,filetypeList);
    QSettings setting;
    QString temp="";
    if(setting.contains("lastpath"))
        temp=setting.value("lastpath").toString();
    setCurrConPath(temp);

    connect(typeBtn,&QPushButton::pressed,this,&FileListWidget::slotTypeBtnPressed);
}

void FileListWidget::setCurrConPath(QString str)
{
    currConPath=str.toStdString();
    currConPathLabel->setText(QString::fromStdString(currConPath));
    emit updateData(currConPath);
}

bool FileListWidget::setData(std::vector<std::string> filenameList,std::vector<char> filetypeList)
{
    if(filenameList.size()!=filetypeList.size()) return false;
    for(int i=0;i<filenameList.size();i++)
    {
        name_type.insert(std::pair<std::string,char>(filenameList.at(i),filetypeList.at(i)));
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
            res.push_back(QString::fromStdString(currConPath)+"/"+item->text());
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
                    emit loadFiles({QString::fromStdString(currConPath)+"/"+selectItems[0]->text()
                        ,QString::fromStdString(currConPath)+"/"+anoName},0);
                else
                    QMessageBox::information(this,"Error","New ano name is empty");
            }else
            {
                anoName=QInputDialog::getText(this,"是否新建文件继续","请输入文件名",QLineEdit::Normal,"",&ok);
                if(ok)
                {
                    if(!anoName.isEmpty())
                        emit loadFiles({QString::fromStdString(currConPath)+"/"+selectItems[0]->text()
                        ,QString::fromStdString(currConPath)+"/"+anoName},1);
                    else
                        QMessageBox::information(this,"Error","New ano name is empty");
                }else if(!ok)
                {
                    emit loadFiles({QString::fromStdString(currConPath)+"/"+selectItems[0]->text(),
                        QString::fromStdString(currConPath)+"/"+selectItems[0]->text()},2);
                }
            }
        }

    }
}

void FileListWidget::updateWidget()
{
    listwidget->clear();
    const QIcon dirIcon(":/icon/dir.png");
    const QIcon fileIcon(":/icon/file.png");

    for(auto iter=name_type.begin();iter!=name_type.end();iter++)
    {
        if(iter->second==0)
        {
            QListWidgetItem *item=new QListWidgetItem(dirIcon,QString::fromStdString(iter->first),listwidget);
        }else if(iter->second==1)
        {
            if(type==0)
                QListWidgetItem *item=new QListWidgetItem(fileIcon,QString::fromStdString(iter->first),listwidget);
            else if(type==1&&QString::fromStdString(iter->first).endsWith(".ano"))
                QListWidgetItem *item=new QListWidgetItem(fileIcon,QString::fromStdString(iter->first),listwidget);
        }
    }
    this->show();
}





void FileListWidget::slotDoubleClicked(QListWidgetItem * item)
{
    QString text=item->text();
    int type=name_type.find(text.toStdString())->second;
    if(type==0)
    {
        setCurrConPath(QString::fromStdString(currConPath+"/"+text.toStdString()));
    }
}

