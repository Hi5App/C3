#include <QCoreApplication>
#include "server.h"

#include <iostream>
#include <QFileInfo>
#include <limits>
#include "dataprepro.h"

const QString c3Image="image";
const QString c3Apo="APO";
const QString c3CSwc="SWC";
const QString c3RSwc="PeReconstructionResult";
const QString c3PSwc="ProofedSWC";
const QString c3FSwc="FullSWC";

const QString c3Input="input";
const QString c3Data="data";

QString IMAGE;//图像文件夹
QString PREAPO;//预重建的输入apo文件夹
QString PRESWC;//检查的swc输入文件夹
QString PRERESSWC;//预重建结果文件夹
QString PROOFSWC;//校验数据的文件夹
QString FULLSWC;


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

    const QString inputDir=QCoreApplication::applicationDirPath()+"/"+c3Input;
    const QString dataDir=QCoreApplication::applicationDirPath()+"/"+c3Data;


    IMAGE=QCoreApplication::applicationDirPath()+"/"+c3Image;//图像文件夹
    PREAPO=inputDir+"/"+c3Apo;//预重建的输入apo文件夹
    PRESWC=inputDir+"/"+c3CSwc;//检查的swc输入文件夹
    PRERESSWC=dataDir+"/"+c3RSwc;//预重建结果文件夹
    PROOFSWC=dataDir+"/"+c3PSwc;//校验数据的文件夹
    FULLSWC=dataDir+"/"+c3PSwc;

    if(!QDir(IMAGE).exists()){
        QDir(QCoreApplication::applicationDirPath()).mkdir(c3Image);
    }
    if(!QDir(inputDir).exists()){
        QDir(QCoreApplication::applicationDirPath()).mkdir(c3Input);
    }
    if(!QDir(PREAPO).exists()){
        QDir(inputDir).mkdir(c3Apo);
    }
    if(!QDir(PRESWC).exists()){
        QDir(inputDir).mkdir(c3CSwc);
    }
    if(!QDir(dataDir).exists()){
        QDir(QCoreApplication::applicationDirPath()).mkdir(c3Data);
    }
    if(!QDir(PRERESSWC).exists()){
        QDir(dataDir).mkdir(c3RSwc);
    }
    if(!QDir(PROOFSWC).exists()){
        QDir(dataDir).mkdir(c3PSwc);
    }
    if(!QDir(FULLSWC).exists()){
        QDir(dataDir).mkdir(c3FSwc);
    }
    qDebug()<<QSqlDatabase::drivers();
    Server server;
    if(!server.listen(QHostAddress::Any,9000))
        exit(0);
    else
        std::cout<<"Server start:Version 1.2(HL)\n";
    return a.exec();
}



