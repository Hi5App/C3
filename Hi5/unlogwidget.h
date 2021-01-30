#ifndef UNLOGWIDGET_H
#define UNLOGWIDGET_H
#include <QObject>
#include <QWidget>
#include <QPushButton>
#include <QLayout>
#include <QLabel>
#include <QLineEdit>
class UnLogWidget : public QWidget
{
    Q_OBJECT
public:
    explicit UnLogWidget(QWidget *parent = nullptr);
private:
    QPushButton *loginBtn=nullptr;
    QPushButton *registerBtn=nullptr;
    QPushButton *forgetBtn=nullptr;

    QLineEdit *useridEdit=nullptr;
    QLineEdit *passwordEdit=nullptr;
    QVBoxLayout *mainlayout=nullptr;

public slots:
    void slotLoginPressed();
    void slotRegisterPress();
    void slotForgetPressed();
signals:
    void signalLogin(QString id,QString password);
    void signalRegister(QString id,QString passsword);
    void signalForget(QString id);

};

#endif // UNLOGWIDGET_H
