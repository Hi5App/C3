#include <QCoreApplication>
#include "server.h"

#include <iostream>
#include <QFileInfo>
#include <limits>

#include "dataprepro.h"

QString IMAGE;//图像文件夹
QString PREAPO;//预重建的输入apo文件夹
QString PRESWC;//检查的swc输入文件夹
QString PRERESSWC;//预重建结果文件夹
QString PROOFSWC;//校验数据的文件夹
QString FULLSWC;//swc数据存放文件夹
//extern QString BRAININFO;//放置brainInfo的文件

 QString IMAGETABLENAME="Table0";//图像数据表
 QString PRERETABLENAME="Table1";//预重建数据表
 QString RESWCTABLENAME="Table2";//重建完成数据表
 QString PROOFTABLENAME="Table3";//校验数据表
 QString CHECKTABLENAME="Table4";//校验结果数据表
void getApo(QString brainDir,QString apoDir);
void writeBrainInfo(QString apoDir,QString infoWithTxt);
void getBB(const V_NeuronSWC_list& T,const QString & Filename);
void writeCheckBrainInfo(QString swcPath,QString resInfo,QString resPath);
void combineData(QString swcPath,QString apoPath,QString dstPath);
void combineDataWithoutCombine(QString swcPath,QString apoPath,QString dstPath);
//image dir:put brain image
//image
//  --brainnumber dir
//    --RES1
//    --RES2

//data dir:put anotation data such as swc
//tmp dir:put some temp file(after use will delete)
//neuronInfo dir:a whole brain info save as a .txt in it,will send to user.


int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);
     IMAGE=QCoreApplication::applicationDirPath()+"/image";//图像文件夹
     PREAPO=QCoreApplication::applicationDirPath()+"/input/APO";//预重建的输入apo文件夹
     PRESWC=QCoreApplication::applicationDirPath()+"/input/SWC";//检查的swc输入文件夹
     PRERESSWC=QCoreApplication::applicationDirPath()+"/data/PeReconstructionResult";//预重建结果文件夹
     PROOFSWC=QCoreApplication::applicationDirPath()+"/data/ProofedSWC";//校验数据的文件夹
     FULLSWC=QCoreApplication::applicationDirPath()+"/data/FullSWC";//swc数据存放文件夹
    //extern QString BRAININFO;//放置brainInfo的文件


    qDebug()<<QSqlDatabase::drivers();
    Server server;
    if(!server.listen(QHostAddress::Any,9000))
        exit(0);
    else
        std::cout<<"Server start:Version 1.2(HL)\n";
    return a.exec();
}



