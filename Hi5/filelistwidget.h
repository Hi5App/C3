#ifndef FILELISTWIDGET_H
#define FILELISTWIDGET_H

#include <QWidget>
#include <QListWidget>
#include <QPushButton>
#include <QLayout>
class FileListWidget : public QWidget
{
    Q_OBJECT
public:
    explicit FileListWidget(char type,std::vector<std::string> filenameList={},std::vector<char> filetypeList={},QWidget *parent = nullptr);
    ~FileListWidget();
    bool setfilename(std::vector<std::string> filenameList,std::vector<char> filetypeList);
    void updateWidget();
private:
    QListWidget *listwidget=nullptr;
    QPushButton *typeBtn=nullptr;
    QPushButton *cancelBtn=nullptr;
    std::map<std::string,char> name_type;

    QHBoxLayout *btnlayout=nullptr;
    QVBoxLayout *mainLayout=nullptr;
};

#endif // FILELISTWIDGET_H
