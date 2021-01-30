#include "unlogwidget.h"

UnLogWidget::UnLogWidget(QWidget *parent) : QWidget(parent)
{
    loginBtn=new QPushButton("登陆",this);
    registerBtn=new QPushButton("注册",this);
    forgetBtn=new QPushButton("忘记密码",this);
    useridEdit=new QLineEdit(this);
    passwordEdit=new QLineEdit(this);
    mainlayout=new QVBoxLayout(this);
    mainlayout->addWidget(loginBtn);
    mainlayout->addWidget(registerBtn);
    mainlayout->addWidget(forgetBtn);
    mainlayout->addWidget(useridEdit);
    mainlayout->addWidget(passwordEdit);
    this->setLayout(mainlayout);

    QObject::connect(loginBtn,SIGNAL(pressed()),this,SLOT(slotLoginPressed()));
    QObject::connect(registerBtn,SIGNAL(pressed()),this,SLOT(slotRegisterPress()));
    QObject::connect(forgetBtn,SIGNAL(pressed()),this,SLOT(slotForgetPressed()));
}

void UnLogWidget::slotLoginPressed()
{
    if(useridEdit&&!useridEdit->text().isEmpty()
       &&passwordEdit&&!passwordEdit->text().isEmpty())
    {
        emit signalLogin(useridEdit->text(),passwordEdit->text());
    }
}

void UnLogWidget::slotRegisterPress()
{
    if(useridEdit&&!useridEdit->text().isEmpty()
       &&passwordEdit&&!passwordEdit->text().isEmpty())
    {
        emit signalRegister(useridEdit->text(),passwordEdit->text());
    }
}

void UnLogWidget::slotForgetPressed()
{
    if(useridEdit&&!useridEdit->text().isEmpty())
    {
        emit signalForget(useridEdit->text());
    }
}
