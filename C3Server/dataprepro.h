#ifndef DATAPREPRO_H
#define DATAPREPRO_H
#include <QString>
#include <QFile>
#include <QDir>
#include <QCoreApplication>
#include <QDebug>
#include "basic_c_fun/basic_surf_objs.h"
#include "neuron_editing/v_neuronswc.h"
#include "neuron_editing/neuron_format_converter.h"
#include <array>
#include <QHash>
struct checkInfo
{
    QString brainId;
    std::array<uint,9> cor;
};
void getSwcFromExcelTxt(QString txtName,QString dirpath)
{
    QFile f(txtName);
    QDir dir(dirpath);
    if(f.open(QIODevice::ReadOnly|QIODevice::Text))
    {
        f.readLine();
        while(!f.atEnd())
        {
            QString str=QString(f.readLine()).split(" ",QString::SkipEmptyParts).at(0).trimmed();
            QString filepath=dirpath+"/"+str;
            if(QFile(filepath).exists())
            {
                QFile(filepath).copy(QCoreApplication::applicationDirPath()+"/"+str);
            }else
            {
                qDebug()<<str;
            }
        }
    }
    qDebug()<<"end....";

}
void combineDataWithoutCombine(QString swcPath,QString apoPath,QString dstPath)
{
    QDir swcDir(swcPath);
    QDir apoDir(apoPath);
    QDir dstDir(dstPath);
    auto apoList=apoDir.entryInfoList(QDir::Dirs|QDir::NoDotAndDotDot);

    QStringList noSwcList;
    QStringList enSwcList;
    QStringList emSwcList;

    for(auto apo:apoList)
    {
        QString apoBaseName=apo.baseName();
        if(QFile(swcPath+"/"+apoBaseName+".swc").exists())
        {
            auto nt=readSWC_file(swcPath+"/"+apoBaseName+".swc");
            if(nt.listNeuron.size()!=0)
            {
                enSwcList.push_back(apoBaseName);
            }else
            {
                emSwcList.push_back(apoBaseName);
            }
        }else
        {
            noSwcList.push_back(apoBaseName);
        }
    }

    QFile resF(dstPath+"/result.txt");
    if(resF.open(QIODevice::Text|QIODevice::WriteOnly))
    {
        QTextStream stream(&resF);
        stream<<"noSwcList:"<<noSwcList.size()<<endl;
        for(auto i:noSwcList)
        {
            stream<<i<<endl;
        }
        stream<<"enSwcList:"<<enSwcList.size()<<endl;
        for(auto i:enSwcList)
        {
            stream<<i<<endl;
        }
        stream<<"emSwcList:"<<emSwcList.size()<<endl;
        for(auto i:emSwcList)
        {
            stream<<i<<endl;
        }
        resF.close();
    }

    qDebug()<<"end";
}

void combineData(QString swcPath,QString apoPath,QString dstPath)
{
    QDir swcDir(swcPath);
    QDir apoDir(apoPath);
    QDir dstDir(dstPath);
    auto apoList=apoDir.entryInfoList(QDir::Dirs|QDir::NoDotAndDotDot);

    QStringList noSwcList;
    QStringList enSwcList;
    QStringList emSwcList;

    for(auto apo:apoList)
    {
        QString apoBaseName=apo.baseName();
        if(QFile(swcPath+"/"+apoBaseName+".swc").exists())
        {
            auto nt=readSWC_file(swcPath+"/"+apoBaseName+".swc");
            if(nt.listNeuron.size()!=0)
            {
                enSwcList.push_back(apoBaseName);
                {
                    auto markers=readAPO_file(apoPath+"/"+apoBaseName+"/"+apoBaseName+".apo");
                    {
                        dstDir.mkdir(apoBaseName);
                        QString tempname =dstPath+"/"+apoBaseName+"/"+apoBaseName+".ano";
                        QFile anofile(tempname);
                        anofile.open(QIODevice::WriteOnly);
                        QString str1="APOFILE="+apoBaseName+".ano.apo";
                        QString str2="SWCFILE="+apoBaseName+".ano.eswc";

                        QTextStream out(&anofile);
                        out<<str1<<endl<<str2;
                        anofile.close();
                    }
                    writeAPO_file(dstPath+"/"+apoBaseName+"/"+apoBaseName+".ano.apo",markers);
                    writeESWC_file(dstPath+"/"+apoBaseName+"/"+apoBaseName+".ano.eswc",nt);
                }
            }else
            {
                emSwcList.push_back(apoBaseName);
            }
        }else
        {
            noSwcList.push_back(apoBaseName);
        }
    }

    QFile resF(dstPath+"/result.txt");
    if(resF.open(QIODevice::Text|QIODevice::WriteOnly))
    {
        QTextStream stream(&resF);
        stream<<"noSwcList:"<<noSwcList.size()<<endl;
        for(auto i:noSwcList)
        {
            stream<<i<<endl;
        }
        stream<<"enSwcList:"<<enSwcList.size()<<endl;
        for(auto i:enSwcList)
        {
            stream<<i<<endl;
        }
        stream<<"emSwcList:"<<emSwcList.size()<<endl;
        for(auto i:emSwcList)
        {
            stream<<i<<endl;
        }
        resF.close();
    }

    qDebug()<<"end";
}

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

        x1=floor(x1);
        x2=ceil(x2);
        y1=floor(y1);
        y2=ceil(y2);
        z1=floor(z1);
        z2=ceil(z2);
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
//                NeuronTree nt;
//                writeSWC_file(apoDir+"/"+list[i].baseName()+".swc",nt);
                if(i!=list.count()-1)
                    stream<<endl;

            }
            f.close();
        }else
        {
            qDebug()<<"failed!"<<f.errorString();
        }

}

void writeCheckBrainInfo(QString swcPath,QString resInfo,QString resPath)
{
    QDir dir(swcPath);

    QFileInfoList list=dir.entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
    QVector<checkInfo> swcList;
    qDebug()<<swcPath<<list.size();
    for(int i=0;i<list.count();i++)
    {
        QRegExp swcNameExp("(.*)_(.*)_(.*)_(.*).v3draw_x(.*)_y(.*)_z(.*)_(.*)");
        qDebug()<<list[i].fileName();
        if(swcNameExp.indexIn(list[i].fileName())!=-1)
        {
            QString brainId=swcNameExp.cap(1).trimmed();
            uint centerx=swcNameExp.cap(2).toDouble();
            uint centery=swcNameExp.cap(3).toDouble();
            uint centerz=swcNameExp.cap(4).toDouble();
            uint lx=swcNameExp.cap(5).toUInt();
            uint ly=swcNameExp.cap(6).toUInt();
            uint lz=swcNameExp.cap(7).toUInt();

            QString neuronId=list[i].fileName().chopped(4).trimmed().replace('.',"_");
//            QTextStream s(&neuronId);
//            s<<brainId<<"_";
//            s.setFieldWidth(8);
//            s.setPadChar('0');
//            s<<i+1;

            writeSWC_file(resPath+"/"+neuronId+".swc",readSWC_file(list[i].absoluteFilePath()));
            {
                auto nt=readSWC_file(list[i].absoluteFilePath());
                auto V_list=NeuronTree__2__V_NeuronSWC_list(nt);
                auto bb=getBB(V_list);
                checkInfo info={neuronId,{centerx,centery,centerz,bb[0],bb[1],bb[2],bb[3],bb[4],bb[5]}};
                swcList.push_back(info);
            }
        }
    }

    QString infoWithTxt=resPath+"/check"+QFileInfo(resInfo).fileName();
//    qDebug()<<"info:"<<infoWithTxt;
    QFile f(infoWithTxt);

    if(f.open(QIODevice::WriteOnly|QIODevice::Text))
    {
        QTextStream stream(&f);
        //读写分辨率的信息
        QFile resF(resInfo);
        if(resF.open(QIODevice::ReadOnly|QIODevice::Text))
        {
            stream<<resF.readAll();
            resF.close();
        }
        else
        {
            qDebug()<<resF.errorString()<<endl;
            exit(0);
        }
        stream<<endl<<endl<<"#Neuron_number:"<<list.count()<<endl;
        for(int i=0;i<swcList.size();i++)
        {
            stream<<QString::number(i+1)<<":"<<swcList[i].brainId<<endl;
        }
        stream<<endl<<"#Neuron Info"<<endl;
        const int arborCnt=1;
        for(int i=0;i<swcList.size();i++)
        {
            stream<<"##"<<swcList[i].brainId<<endl
                 <<"soma:"<<0<<";"<<0<<";"<<0<<endl;
                        stream<<"arbor:"<<arborCnt<<endl;
            stream<<arborCnt<<":"<<swcList[i].cor[0]*2<<";"<<swcList[i].cor[1]*2<<";"<<swcList[i].cor[2]*2<<";"
                 <<swcList[i].cor[3]<<";"<<swcList[i].cor[4]<<";"<<swcList[i].cor[5]<<";"
                 <<swcList[i].cor[6]<<";"<<swcList[i].cor[7]<<";"<<swcList[i].cor[8]
                 <<endl;
        }
        f.close();
    }


//    QFile f(infoWithTxt);
//    if(f.open(QIODevice::Append|QIODevice::Text))
//    {
//
//        stream<<endl<<endl<<"#Neuron_number:"<<list.count()<<endl;
//        for(int i=0;i<list.count();i++)
//        {
//            stream<<QString::number(i+1)<<":"<<list[i].baseName()<<endl;
//        }
//        stream<<endl<<"#Neuron Info"<<endl;
//        for(int i=0;i<list.count();i++)
//        {
//            auto nt=readSWC_file(list[i].absoluteFilePath());
//            if(nt.listNeuron.size()==0)
//            {
//                qDebug()<<"error:"<<list[i].fileName();
//                continue;
//            }
//            for(int j=0;j<nt.listNeuron.size();j++)
//            {
//                if(nt.listNeuron[j].pn==-1)
//                {
//                    stream<<"##"<<list[i].baseName()<<endl
//                         <<"soma:"<<QString::number(int(nt.listNeuron[j].x))
//                         <<";"<<QString::number(int(nt.listNeuron[j].y))
//                         <<";"<<QString::number(int(nt.listNeuron[j].z))<<endl;
//                           break;
//                }
//            }

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

//            int arborN=0;
//            if(T3.seg.size()!=0)
//            {
//                arborN++;

//            }
//            if(T2.seg.size()!=0)
//            {

//                arborN++;
//            }
//            if(others.seg.size()!=0)
//            {
//                arborN++;
//            }

//            int cnt=1;
//            stream<<"arbor:"<<QString::number(arborN)<<endl;
//            if(T3.seg.size()!=0)
//            {
//                auto nt3=V_NeuronSWC_list__2__NeuronTree(T3);
//                auto v3=getBB(T3);
//                for(int i=0;i<nt3.listNeuron.size();i++)
//                {
//                    if(nt3.listNeuron[i].pn==-1)
//                    {
//                            stream<<QString::number(cnt++)<<":"<<QString::number(int(nt3.listNeuron[i].x))<<";"<<QString::number(int(nt3.listNeuron[i].y))
//                                 <<";"<<QString::number(int(nt3.listNeuron[i].z))<<";"<<QString::number(v3.at(0))<<";"<<QString::number(v3.at(1))
//                                <<";"<<QString::number(v3.at(2))<<";"<<QString::number(v3.at(3))
//                                <<";"<<QString::number(v3.at(4))<<";"<<QString::number(v3.at(5))<<endl;

//                               break;
//                    }
//                }
//            }
//            if(T2.seg.size()!=0)
//            {
//                auto nt2=V_NeuronSWC_list__2__NeuronTree(T2);
//                auto v2=getBB(T2);
//                for(int i=0;i<nt2.listNeuron.size();i++)
//                {
//                    if(nt2.listNeuron[i].pn==-1)
//                    {
//                            stream<<QString::number(cnt++)<<":"<<QString::number(int(nt2.listNeuron[i].x))<<";"<<QString::number(int(nt2.listNeuron[i].y))
//                                 <<";"<<QString::number(int(nt2.listNeuron[i].z))<<";"<<QString::number(v2.at(0))<<";"<<QString::number(v2.at(1))
//                                <<";"<<QString::number(v2.at(2))<<";"<<QString::number(v2.at(3))
//                                <<";"<<QString::number(v2.at(4))<<";"<<QString::number(v2.at(5))<<endl;

//                               break;
//                    }
//                }
//            }
//            if(others.seg.size()!=0)
//            {
//                auto ntother=V_NeuronSWC_list__2__NeuronTree(others);
//                auto vo=getBB(others);
//                for(int i=0;i<ntother.listNeuron.size();i++)
//                {
//                    if(ntother.listNeuron[i].pn==-1)
//                    {
//                            stream<<QString::number(cnt++)<<":"<<QString::number(int(ntother.listNeuron[i].x))<<";"<<QString::number(int(ntother.listNeuron[i].y))
//                                 <<";"<<QString::number(int(ntother.listNeuron[i].z))<<";"<<QString::number(vo.at(0))<<";"<<QString::number(vo.at(1))
//                                <<";"<<QString::number(vo.at(2))<<";"<<QString::number(vo.at(3))
//                                <<";"<<QString::number(vo.at(4))<<";"<<QString::number(vo.at(5))<<endl;

//                               break;
//                    }
//                }
//            }
//            }
//        }
}

void writeCheckInfos1(QString swcDirPath,QString resDirPath)
{
    auto swcList=QDir(swcDirPath).entryInfoList(QDir::Files|QDir::NoDotAndDotDot);
    auto resList=QDir(resDirPath).entryInfoList(QDir::Files|QDir::NoDotAndDotDot);

    QMap<QString,QString> hash_swc_res;
    QDir dir("./");

    for(auto item:swcList)
    {
        QString brainid=item.fileName().left(5).trimmed();
        for(auto res:resList)
        {
            if(res.fileName().contains(brainid))
            {
                if(!QDir(brainid).exists())
                {
                    dir.mkdir(brainid);
                }

                QFile(item.absoluteFilePath()).rename("./"+brainid+"/"+item.fileName());
                if(!hash_swc_res.keys().contains(brainid))
                    hash_swc_res[brainid]=res.absoluteFilePath();
                break;
            }

        }
    }
    dir.mkdir("result");
    for(auto item=hash_swc_res.begin();item!=hash_swc_res.end();item++)
    {
        writeCheckBrainInfo("./"+item.key(),item.value(),"./result");
        QDir("./"+item.key()).removeRecursively();
    }
        for(auto item=hash_swc_res.begin();item!=hash_swc_res.end();item++)
            qDebug()<<item.key()<<" "<<item.value();
}


void getInBB(QString filename,vector<uint> BB)
{
    auto nt=readSWC_file(filename);
    V_NeuronSWC_list testVNL=NeuronTree__2__V_NeuronSWC_list(nt);
    V_NeuronSWC_list tosave;
    for(int i=0;i<testVNL.seg.size();i++)
    {
        NeuronTree SS;
        V_NeuronSWC seg_temp =  testVNL.seg.at(i);
        seg_temp.reverse();
        for(int j=0;j<seg_temp.row.size();j++)
        {
            if(seg_temp.row.at(j).x>=BB[0]&&seg_temp.row.at(j).x<=BB[1]
                    &&seg_temp.row.at(j).y>=BB[2]&&seg_temp.row.at(j).y<=BB[3]
                    &&seg_temp.row.at(j).z>=BB[4]&&seg_temp.row.at(j).z<=BB[5])
            {
                tosave.seg.push_back(seg_temp);
                break;
            }
        }
    }
    nt=V_NeuronSWC_list__2__NeuronTree(tosave);
    for(auto & node:nt.listNeuron)
    {
        node.x-=BB[0];
        node.y-=BB[2];
        node.z-=BB[4];
    }
    writeSWC_file(QFileInfo(filename).fileName().left(11)+".swc",nt);
}

#endif // DATAPREPRO_H
