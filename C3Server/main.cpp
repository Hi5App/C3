#include <QCoreApplication>
#include "server.h"

#include <iostream>
#include <QFileInfo>
#include <limits>

#include "dataprepro.h"

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

//    QDir dir("C:/Users/Brain/Desktop/C3_check_test");
//    auto list=dir.entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
//    for(auto i : list)
//    {
//        writeSWC_file(i.baseName().left(11)+".swc",readSWC_file(i.absoluteFilePath()));
//    }
//                combineDataWithoutCombine("C:/Users/Brain/Desktop/18454","C:/Users/Brain/Desktop/18454_to C3","C:/Users/Brain/Desktop/res");




       auto dir=QDir("/Users/huanglei/Desktop/1");
       auto files=dir.entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
       for(auto f:files)
       {
           auto temp=readSWC_file(f.absoluteFilePath());
           auto BB=getBB(NeuronTree__2__V_NeuronSWC_list(temp));
           qDebug()<<f.fileName()<<" :"<<BB[0]<<","<<BB[1]<<","<<BB[2]<<","<<BB[3]<<","<<BB[4]<<","<<BB[5];
       }
       exit(0);
//    Server server;
//    if(!server.listen(QHostAddress::Any,9000))
//        exit(0);
//    else
//        std::cout<<"Server start:Version 1.2(HL)\n";

    if(argc==1)
    {
            Server server;
            if(!server.listen(QHostAddress::Any,9000))
                exit(0);
            else
                std::cout<<"Server start:Version 1.2(HL)\n";
            return a.exec();
    }else if(argc==4)
    {
        if(QString(argv[1]).toUInt()==0)//pre construction
        {
            QDir(QCoreApplication::applicationDirPath()).mkdir("tmpDirForDataPrePro");
            QDir dir=QDir(QCoreApplication::applicationDirPath());
            dir.cd("tmpDirForDataPrePro");

            getApo(argv[2],QCoreApplication::applicationDirPath()+"/tmpDirForDataPrePro");
            writeBrainInfo(QCoreApplication::applicationDirPath()+"/tmpDirForDataPrePro",argv[3]);
            QDir(QCoreApplication::applicationDirPath()+"/tmpDirForDataPrePro").removeRecursively();

            qDebug()<<"end";
        //    getApo("C:/Users/Brain/Desktop/C3-preconstruction-8200/18454_to C3","C:/Users/Brain/Desktop/18454");
        //    writeBrainInfo("C:/Users/Brain/Desktop/18454","C:/Users/Brain/Desktop/mouse18454_teraconvert.txt");
        }else if(QString(argv[1]).toUInt()==1)
        {
                writeCheckInfos1(argv[2],argv[3]);
        }
    }
    else if(argc==5)
    {
        if(QString(argv[1]).toUInt()==1)
        {
            combineData(argv[2],argv[3],argv[4]);
                        qDebug()<<"end";
//            combineData("C:/Users/Brain/Desktop/18454","C:/Users/Brain/Desktop/18454_to C3","C:/Users/Brain/Desktop/res");
        }else if(QString(argv[1]).toUInt()==2)
        {
            combineDataWithoutCombine(argv[2],argv[3],argv[4]);
                        qDebug()<<"end";
//            combineDataWithoutCombine("C:/Users/Brain/Desktop/18454","C:/Users/Brain/Desktop/18454_to C3","C:/Users/Brain/Desktop/res");
        }

    }
    exit(0);
}



