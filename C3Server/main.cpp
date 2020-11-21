#include <QCoreApplication>
#include "server.h"
#include "respond.h"

const QString databaseName="BrainTell";
const QString dbHostName="localhost";
const QString dbUserName="root";
const QString dbPassword="1234";

QStringList dbParas={databaseName,dbHostName,dbUserName,dbPassword};
QString vaa3dPath;
QString IMAGE;//图像文件夹
QString InApoPreReconstruct;//输入：用于预重建的apo
QString InSwcFull;//输入：完全重建的swc
QString InSwcProof;//输入：检查的swc
QString ResultPreReConstruct;//结果：预重建的swc
QString ResultForProofSwc;//结果：检查的swc
QString ResultFullSwc;

QString TableForImage="TableForImage";//图像数据表
QString TableForPreReConstruct="TableForApoPreRestruct";//预重建数据表
QString TableForFullSwc="TableForSwcFull";//重建完成数据表
QString TableForProof="TableForSwcProof";//校验数据表
QString TableForCheckResult="TableForCheckResult";//校验结果数据表
int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);

    {
        const QString c3Image="image";
        const QString c3Apo ="ApoPreRestruct";
        const QString c3CSwc="SwcProof";
        const QString c3FSwc="SwcFull";

        const QString c3RSwc="PeReconstructionResult";
        const QString c3PSwc="ProofedSWC";

        const QString c3Input="input";
        const QString c3Data="data";

        const QString inputDir=QCoreApplication::applicationDirPath()+"/"+c3Input;
        const QString dataDir=QCoreApplication::applicationDirPath()+"/"+c3Data;
        vaa3dPath=QCoreApplication::applicationDirPath()+"/vaa3d";
        IMAGE=QCoreApplication::applicationDirPath()+"/"+c3Image;//图像文件夹
        InApoPreReconstruct=inputDir+"/"+c3Apo;//预重建的输入apo文件夹
        InSwcFull=inputDir+"/"+c3FSwc;
        InSwcProof=inputDir+"/"+c3CSwc;//检查的swc输入文件夹
        ResultPreReConstruct=dataDir+"/"+c3RSwc;//预重建结果文件夹
        ResultForProofSwc=dataDir+"/"+c3PSwc;//校验数据的文件夹
        ResultFullSwc=dataDir+"/"+c3FSwc;

        if(!QDir(inputDir).exists()){
            QDir(QCoreApplication::applicationDirPath()).mkdir(c3Input);
        }
        if(!QDir(dataDir).exists()){
            QDir(QCoreApplication::applicationDirPath()).mkdir(c3Data);
        }
        if(!QDir(IMAGE).exists()){
            QDir(QCoreApplication::applicationDirPath()).mkdir(c3Image);
        }
        if(!QDir(InApoPreReconstruct).exists()){
            QDir(inputDir).mkdir(c3Apo);
        }
        if(!QDir(InSwcFull).exists()){
            QDir(inputDir).mkdir(c3FSwc);
        }
        if(!QDir(InSwcProof).exists()){
            QDir(inputDir).mkdir(c3CSwc);
        }

        if(!QDir(ResultPreReConstruct).exists()){
            QDir(dataDir).mkdir(c3RSwc);
        }
        if(!QDir(ResultForProofSwc).exists()){
            QDir(dataDir).mkdir(c3PSwc);
        }
        if(!QDir(ResultFullSwc).exists()){
            QDir(dataDir).mkdir(c3FSwc);
        }
    }

    Server server;

    if(!server.init()||!server.listen(QHostAddress::Any,9000))
        exit(0);
    else
        qDebug()<<"Server start:Version 2.0(HL)."<<__TIME__;
    return a.exec();
}
