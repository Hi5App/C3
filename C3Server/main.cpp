#include <QCoreApplication>
#include "server.h"

#include <iostream>
#include <QFileInfo>
void getApo(QString brainDir,QString apoDir);
void writeBrainInfo(QString apoDir,QString infoWithTxt);
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
    if(argc==1)
    {
        Server server;
        if(!server.listen(QHostAddress::Any,9000))
            exit(0);
        else
            std::cout<<"Server start:Version 1.1(HL)\n";
    }else
    {
        if(argc==4)
        {
            if(argv[1]=="1")
            {
                writeBrainInfo(QString(argv[2]),QString(argv[3]));
            }else if(argv[1]=="2")
            {

            }else if(argv[1]=="3")
            {

            }
        }
    }



//    qDebug()<<list.size();


//        QDir dir("C:/Users/Brain/Desktop/17302_check");
//        auto list=dir.entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
//        for(auto i:list)
//        {
//            QString filename=i.fileName().left(15);
//            QFile f(i.absoluteFilePath());
//            f.rename(i.absolutePath()+"/"+filename);
//        }

//        list=dir.entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
//        for(auto i :list)
//        {
//            QString swcname=i.absoluteFilePath();
//            auto nt=readSWC_file(swcname);
//            V_NeuronSWC_list V_list= NeuronTree__2__V_NeuronSWC_list(nt);
//            V_NeuronSWC_list T3;
//            V_NeuronSWC_list T2;
//            V_NeuronSWC_list others;
//            for(V_NeuronSWC& seg:V_list.seg)
//            {
//                if(seg.row.at(0).type==3)
//                {
//                    T3.seg.push_back(seg);
//                }else if (seg.row.at(0).type==2) {
//                    T2.seg.push_back(seg);
//                }else
//                {
//                    others.seg.push_back(seg);
//                }
//            }
//        }

    return a.exec();
}

void getApo(QString brainDir,QString apoDir)
{
        QDir dir(brainDir);
        QFileInfoList list=dir.entryInfoList(QDir::Dirs|QDir::NoDotAndDotDot);
        for(auto t:list)
        {
            QFileInfoList list1=QDir(t.absoluteFilePath()).entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
            for(auto tt:list1)
            {
                if(tt.suffix()=="apo")
                {
                    QFile f(tt.absoluteFilePath());
                    f.copy(apoDir+"/"+tt.fileName());
                }
            }
        }
}

void writeBrainInfo(QString apoDir,QString infoWithTxt)
{
//    infoWithTxt需要手动先行维护图像分辨率信息
        QDir dir(apoDir);
        QFileInfoList list=dir.entryInfoList(QDir::Files|QDir::NoDotAndDotDot);

        qDebug()<<list.count();
        QFile f(infoWithTxt);
        if(f.open(QIODevice::Append|QIODevice::Text))
        {
            QTextStream stream(&f);
            stream<<endl<<endl<<"#Neuron_number:"<<list.count()<<endl;
            for(int i=0;i<list.count();i++)
            {
                stream<<QString::number(i+1)<<":"<<list[i].baseName()<<endl;
            }
            stream<<endl<<"#Neuron Info"<<endl;
            for(int i=0;i<list.count();i++)
            {
                auto apos=readAPO_file(list[i].absoluteFilePath());
                if(apos.count()!=1)
                {
                    qDebug()<<"error:"<<list[i].fileName();
                    continue;
                }
                stream<<"##"<<list[i].baseName()<<endl
                     <<"soma:"<<QString::number(int(apos[0].x))
                     <<";"<<QString::number(int(apos[0].y))
                     <<";"<<QString::number(int(apos[0].z))<<endl
                     <<"arbor:0";
                NeuronTree nt;
                writeSWC_file("C:/Users/Brain/Desktop/data/"+list[i].baseName()+".swc",nt);
                if(i!=list.count()-1)
                    stream<<endl;

            }

        }else
        {
            qDebug()<<"failed!"<<f.errorString();
        }

}
