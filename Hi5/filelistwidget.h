#ifndef FILELISTWIDGET_H
#define FILELISTWIDGET_H

#include <QWidget>
#include <QListWidget>
#include <QPushButton>
#include <QLayout>
#include <QLabel>
#include <QMessageBox>
#include <QInputDialog>
class FileListWidget : public QWidget
{
    Q_OBJECT
public:
    explicit FileListWidget(char type,std::vector<std::string> filenameList={},
        std::vector<char> filetypeList={},QWidget *parent = nullptr);
    ~FileListWidget()=default;
public slots:
    //for extern
    bool setData(std::vector<std::string> filenameList,std::vector<char> filetypeList);
    //for intern
    void slotTypeBtnPressed();
    void slotDoubleClicked(QListWidgetItem *);


private:
    QListWidget *listwidget=nullptr;
    QPushButton *typeBtn=nullptr;
    QPushButton *cancelBtn=nullptr;
    QLabel *currConPathLabel=nullptr;

    QHBoxLayout *btnlayout=nullptr;
    QVBoxLayout *mainLayout=nullptr;

    std::map<std::string,char> name_type;
    std::string currConPath;
    char type=0;

    void setCurrConPath(QString);
    void updateWidget();

signals:
    //for extern
    void updateData(std::string str);
    void downloadFiles(QStringList);
    /**
     * @brief loadFiles
     * @param 2:0 从零开始，1 继承新开始， 2继承
     */
    void loadFiles(QStringList,int);




};

#endif // FILELISTWIDGET_H
