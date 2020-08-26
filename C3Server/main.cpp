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
//    Server server;
//    if(!server.listen(QHostAddress::Any,9000))
//        exit(0);
//    else
//        std::cout<<"Server start:Version 1.1(HL)\n";

    getApo("C:/Users/Brain/Desktop/C3-preconstruction-8200/18454_to C3","C:/Users/Brain/Desktop/18454");
    writeBrainInfo("C:/Users/Brain/Desktop/18454","C:/Users/Brain/Desktop/mouse18454_teraconvert.txt");

//    getApo("C:/Users/Brain/Desktop/C3-preconstruction-8200/18455_to C3","C:/Users/Brain/Desktop/18455");
//    writeBrainInfo("C:/Users/Brain/Desktop/18455","C:/Users/Brain/Desktop/mouse18455_teraconvert.txt");

//    getApo("C:/Users/Brain/Desktop/C3-preconstruction-8200/18872_to C3","C:/Users/Brain/Desktop/18872");
//    writeBrainInfo("C:/Users/Brain/Desktop/18872","C:/Users/Brain/Desktop/mouse18872_teraconvert.txt");
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

//void getBB(const V_NeuronSWC_list& T,const QString & Filename)
//{
//    uint x1,x2,y1,y2,z1,z2;
//    x1=y1=z1=UINT_MAX;
//    x2=y2=z2=0;
//    for(const auto &seg:T.seg)
//    {
//        for(const auto &p:seg.row)
//        {
//            if(p.x<x1) x1=floor(p.x);
//            if(p.x>x2) x2=ceil(p.x);
//            if(p.y<y1) y1=floor(p.y);
//            if(p.y>y2) y2=ceil(p.y);
//            if(p.z<z1) z1=floor(p.z);
//            if(p.z>z2) z2=ceil(p.z);
//        }
//    }
//    if(x1<x2&&y1<y2&&z1<z2){
//        qDebug()<<ceil((x2-x1)/4)*ceil((y2-y1)/4)*ceil((z2-z1)/4)/1000000000.0;
//        sum+=ceil((x2-x1)/4)*ceil((y2-y1)/4)*ceil((z2-z1)/4)/1000000000.0;
//        x1=floor(x1/4);
//        x2=ceil(x2/4);
//        y1=floor(y1/4);
//        y2=ceil(y2/4);
//        z1=floor(z1/4);
//        z2=ceil(z2/4);

//        uint xc=(x1+x2)/2;
//        uint yc=(y1+y2)/2;
//        uint zc=(z1+z2)/2;

//        uint xs=(x2-x1)/2*2;
//        uint ys=(y2-y1)/2*2;
//        uint zs=(z2-z1)/2*2;
//        QString vaa3dPath=QCoreApplication::applicationDirPath();
//        QString apoName=QCoreApplication::applicationDirPath()+"/tmp/"
//                      + QString::number(xc)+ "__"
//                      + QString::number(yc)+ "__"
//                      + QString::number(zc)+ "__"
//                      + QString::number(xs)+"__"
//                      + QString::number(ys)+ "__"
//                      + QString::number(zs);
//        CellAPO centerAPO;
//        centerAPO.x=xc;centerAPO.y=yc;centerAPO.z=zc;
//        QList <CellAPO> List_APO_Write;
//        List_APO_Write.push_back(centerAPO);
//        if(!writeAPO_file(apoName+".apo",List_APO_Write))
//        {
//            qDebug()<<"fail to write apo";
//            return;//get .apo to get .v3draw
//        }

//        QString firstName=Filename+"RES\(13650x8603x2461\)";
//        QString order =QString("xvfb-run -a %0/vaa3d -x %1/plugins/image_geometry/crop3d_image_series/libcropped3DImageSeries.so "
//                                "-f cropTerafly -i %2/%3/ %4.apo %5/arbors/%6 -p %7 %8 %9")
//                .arg(vaa3dPath).arg(vaa3dPath)
//                .arg(QCoreApplication::applicationDirPath()+"/image").arg("mouse17302_teraconvert/RES\(13650x8603x2461\)")
//                .arg(apoName).arg(QCoreApplication::applicationDirPath()).arg(firstName).arg(ys).arg(xs).arg(zs);
//                QProcess p;
//        qDebug()<<p.execute(order.toStdString().c_str());
//    }


//    else if(!(!(x1<x2))&&(!(y1<y2))&&(!(z1<z2)))
//    {
//        qDebug()<<"error";
//        qDebug()<<x1<<" "<<x2;
//        qDebug()<<y1<<" "<<y2;
//        qDebug()<<z1<<" "<<z2;
//    }

//}

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

