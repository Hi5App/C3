#include "filelistwidget.h"
#include <QListWidgetItem>
#include <QIcon>

FileListWidget::FileListWidget(char type,std::vector<std::string> filenameList,std::vector<char> filetypeList,QWidget *parent) : QWidget(parent)
{
    listwidget=new QListWidget;
    if(type==0)
    {
        typeBtn=new QPushButton("Download selected files");
    }else
    {
        typeBtn=new QPushButton("Load selected Ano");
    }
    cancelBtn=new QPushButton("Cancel");
    btnlayout=new QHBoxLayout;
    btnlayout->addWidget(typeBtn);
    btnlayout->addWidget(cancelBtn);
    mainLayout=new QVBoxLayout;
    mainLayout->addWidget(listwidget);
    mainLayout->addLayout(btnlayout);
    this->setLayout(mainLayout);
//    delete mainLayout;
//    delete btnlayout;

}

bool FileListWidget::setfilename(std::vector<std::string> filenameList,std::vector<char> filetypeList)
{

    if(filenameList.size()!=filetypeList.size()) return false;
    for(int i=0;i<filenameList.size();i++)
    {
        name_type.insert(std::pair<std::string,char>(filenameList.at(i),filetypeList.at(i)));
    }
    return true;
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
            QListWidgetItem *item=new QListWidgetItem(fileIcon,QString::fromStdString(iter->first),listwidget);
        }
    }
    this->show();
}

FileListWidget::~FileListWidget()
{
    if(listwidget)
    {
        listwidget->clear();
        delete listwidget;
    }
    if(btnlayout) delete btnlayout;
    if(mainLayout) delete mainLayout;
}
