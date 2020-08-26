#include <QCoreApplication>
#include "server.h"

#include <iostream>
//<<<<<<< HEAD
#include <QFileInfo>
#include <limits>
void getApo(QString brainDir,QString apoDir);
void writeBrainInfo(QString apoDir,QString infoWithTxt);
void getBB(const V_NeuronSWC_list& T,const QString & Filename);

//image dir:put brain image
//image
//  --brainnumber dir
//    --RES1
//    --RES2

//data dir:put anotation data such as swc
//tmp dir:put some temp file(after use will delete)
//neuronInfo dir:a whole brain info save as a .txt in it,will send to user.
//int sum=0;
int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);
    Server server;
    if(!server.listen(QHostAddress::Any,9000))
        exit(0);
    else
        std::cout<<"Server start:Version 1.1(HL)\n";

//    getApo("C:/Users/Brain/Desktop/C3-preconstruction-8200/18454_to C3","C:/Users/Brain/Desktop/18454");
//    writeBrainInfo("C:/Users/Brain/Desktop/18454","C:/Users/Brain/Desktop/mouse18454_teraconvert.txt");
    return a.exec();
}
//    if(argc==1)
//    {
//        Server server;
//        if(!server.listen(QHostAddress::Any,9000))
//            exit(0);
//        else
//            std::cout<<"Server start:Version 1.1(HL)\n";
//    }else
//    {
//        if(argc==4)
//        {
//            if(argv[1]=="1")
//            {
//                writeBrainInfo(QString(argv[2]),QString(argv[3]));
//            }else if(argv[1]=="2")
//            {

//            }else if(argv[1]=="3")
//            {

//            }
//        }
//    }



//    qDebug()<<list.size();


//        QDir dir("/home/allencenter/Desktop/17302_check");
//        auto list=dir.entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
//        for(auto i :list)
//        {
//           qDebug()<<i.fileName();
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
//            qDebug()<<i.fileName();
//            qDebug()<<"T2";
//            getBB(T2,i.baseName());
//            qDebug()<<"T3";
//            getBB(T3,i.baseName());
//            qDebug()<<"other";
//            getBB(others,i.baseName());

//        }
//    qDebug()<<sum;
//    return a.exec();
//}

vector<uint> getBB(const V_NeuronSWC_list& T)
{
    uint x1,x2,y1,y2,z1,z2;
    x1=y1=z1=UINT_MAX;
    x2=y2=z2=0;
    for(const auto &seg:T.seg)
    {
        for(const auto &p:seg.row)
        {
            if(p.x<x1) x1=floor(p.x);
            if(p.x>x2) x2=ceil(p.x);
            if(p.y<y1) y1=floor(p.y);
            if(p.y>y2) y2=ceil(p.y);
            if(p.z<z1) z1=floor(p.z);
            if(p.z>z2) z2=ceil(p.z);
        }
    }
    if(x1<x2&&y1<y2&&z1<z2){
        qDebug()<<ceil((x2-x1)/4)*ceil((y2-y1)/4)*ceil((z2-z1)/4)/1000000000.0;

        x1=floor(x1/4);
        x2=ceil(x2/4);
        y1=floor(y1/4);
        y2=ceil(y2/4);
        z1=floor(z1/4);
        z2=ceil(z2/4);
        return vector<uint>{x1,x2,y1,y2,z1,z2};
    }
    return vector<uint>();

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
                writeSWC_file(apoDir+"/"+list[i].baseName()+".swc",nt);
                if(i!=list.count()-1)
                    stream<<endl;

            }

        }else
        {
            qDebug()<<"failed!"<<f.errorString();
        }

}

void writeCheckBrainInfo(QString swcPath,QString infoWithTxt)
{
    QDir dir(swcPath);
    QFileInfoList list=dir.entryInfoList(QDir::Files|QDir::NoDotAndDotDot);

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
            auto nt=readSWC_file(list[i].absoluteFilePath());
            if(nt.listNeuron.size()==0)
            {
                qDebug()<<"error:"<<list[i].fileName();
                continue;
            }
            for(int i=0;i<nt.listNeuron.size();i++)
            {
                if(nt.listNeuron[i].pn==-1)
                {
                    stream<<"##"<<list[i].baseName()<<endl
                         <<"soma:"<<QString::number(int(nt.listNeuron[i].x))
                         <<";"<<QString::number(int(nt.listNeuron[i].y))
                         <<";"<<QString::number(int(nt.listNeuron[i].z))<<endl;
                           break;
                }
            }

            V_NeuronSWC_list V_list= NeuronTree__2__V_NeuronSWC_list(nt);
            V_NeuronSWC_list T3;
            V_NeuronSWC_list T2;
            V_NeuronSWC_list others;
            for(V_NeuronSWC& seg:V_list.seg)
            {
                if(seg.row.at(0).type==3)
                {
                    T3.seg.push_back(seg);
                }else if (seg.row.at(0).type==2) {
                    T2.seg.push_back(seg);
                }else
                {
                    others.seg.push_back(seg);
                }
            }

            int arborN=0;
            if(T3.seg.size()!=0)
            {
                arborN++;

            }
            if(T2.seg.size()!=0)
            {

                arborN++;
            }
            if(others.seg.size()!=0)
            {
                arborN++;
            }

            int cnt=1;
            stream<<"arbor:"<<QString::number(arborN)<<endl;
            if(T3.seg.size()!=0)
            {
                auto nt3=V_NeuronSWC_list__2__NeuronTree(T3);
                auto v3=getBB(T3);
                for(int i=0;i<nt3.listNeuron.size();i++)
                {
                    if(nt3.listNeuron[i].pn==-1)
                    {
                            stream<<QString::number(cnt++)<<":"<<QString::number(nt3.listNeuron[i].x)<<";"<<QString::number(nt3.listNeuron[i].y)
                                 <<";"<<QString::number(nt3.listNeuron[i].z)<<";"<<QString::number(v3.at(0))<<";"<<QString::number(v3.at(1))
                                <<";"<<QString::number(v3.at(2))<<";"<<QString::number(v3.at(3))
                                <<";"<<QString::number(v3.at(4))<<";"<<QString::number(v3.at(5))<<endl;

                               break;
                    }
                }
            }
            if(T2.seg.size()!=0)
            {
                auto nt2=V_NeuronSWC_list__2__NeuronTree(T2);
                auto v2=getBB(T2);
                for(int i=0;i<nt2.listNeuron.size();i++)
                {
                    if(nt2.listNeuron[i].pn==-1)
                    {
                            stream<<QString::number(cnt++)<<":"<<QString::number(nt2.listNeuron[i].x)<<";"<<QString::number(nt2.listNeuron[i].y)
                                 <<";"<<QString::number(nt2.listNeuron[i].z)<<";"<<QString::number(v2.at(0))<<";"<<QString::number(v2.at(1))
                                <<";"<<QString::number(v2.at(2))<<";"<<QString::number(v2.at(3))
                                <<";"<<QString::number(v2.at(4))<<";"<<QString::number(v2.at(5))<<endl;

                               break;
                    }
                }
            }
            if(others.seg.size()!=0)
            {
                auto ntother=V_NeuronSWC_list__2__NeuronTree(others);
                auto vo=getBB(others);
                for(int i=0;i<ntother.listNeuron.size();i++)
                {
                    if(ntother.listNeuron[i].pn==-1)
                    {
                            stream<<QString::number(cnt++)<<":"<<QString::number(ntother.listNeuron[i].x)<<";"<<QString::number(ntother.listNeuron[i].y)
                                 <<";"<<QString::number(ntother.listNeuron[i].z)<<";"<<QString::number(vo.at(0))<<";"<<QString::number(vo.at(1))
                                <<";"<<QString::number(vo.at(2))<<";"<<QString::number(vo.at(3))
                                <<";"<<QString::number(vo.at(4))<<";"<<QString::number(vo.at(5))<<endl;

                               break;
                    }
                }
            }
            }
        }
}

