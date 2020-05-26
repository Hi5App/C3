#ifndef CUSTOMDEBUG_H
#define CUSTOMDEBUG_H
#include <stdio.h>
#include <stdlib.h>
#include <QFile>
#include <QTextStream>
#include <QDateTime>
#include <iostream>


void customMessageHandler(QtMsgType type, const QMessageLogContext &, const QString & str)
{
    QString txtMessage;
    QString time;
    switch (type)
    {
        case QtDebugMsg:    //调试信息提示
            txtMessage = QString("Debug: %1").arg(str);
            break;

        case QtWarningMsg:    //一般的warning提示
            txtMessage = QString("Warning: %1").arg(str);
            break;

        case QtCriticalMsg:    //严重错误提示
            txtMessage = QString("Critical: %1").arg(str);
            break;

        case QtFatalMsg:    //致命错误提示
            txtMessage = QString("Fatal: %1").arg(str);
            abort();
    }
    time = QDateTime::currentDateTime().toString("yyyy/MM/dd hh:mm:ss");
    std::cout<<time.toStdString().data()<<txtMessage.toStdString().data()<<"\n";
}

#endif // CUSTOMDEBUG_H
