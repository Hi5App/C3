#include "server.h"

Server::Server(QObject *parent):QTcpServer(parent)
{

}

Server::~Server()
{
//    for(auto i=setInfos.begin();i!=setInfos.end();)
//    {
//        if(setSwcInBBByStruct(i->name,i->x1,i->x2,i->y1,i->y2,i->z1,i->z2))
//            i=setInfos.erase(i);
//        else
//            i++;
//    }
}

void Server::incomingConnection(qintptr handle)
{
    Socket* socket=new Socket(handle);
    qDebug()<<handle<<" connected.";
//    connect(socket,SIGNAL(setSwcInBB(QString ,int ,int ,int ,int ,int ,int ))
//            ,this,SLOT(setSwcInBB(QString ,int ,int ,int ,int ,int ,int )));
    socket->start();
}
//bool Server::setSwcInBBByStruct(QString name, int x1, int x2, int y1, int y2, int z1, int z2)
//{
//    V_NeuronSWC_list testVNL;
//    V_NeuronSWC_list resVNL;
//    resVNL=testVNL;
//    resVNL.seg.clear();
//    if(QFile(QCoreApplication::applicationDirPath()+"/data/"+name).exists())
//    {
//        NeuronTree nt=readSWC_file(QCoreApplication::applicationDirPath()+"/data/"+name);
//        if(nt.flag==false) return false;
//        testVNL=NeuronTree__2__V_NeuronSWC_list(nt);
//        for(int i=0;i<testVNL.seg.size();i++)
//        {
//            testVNL.seg[i].to_be_deleted=0;
//            V_NeuronSWC seg_temp =  testVNL.seg.at(i);
//            seg_temp.reverse();
//            for(int j=0;j<seg_temp.row.size();j++)
//            {
//                if(seg_temp.row.at(j).x>=x1&&seg_temp.row.at(j).x<=x2
//                        &&seg_temp.row.at(j).y>=y1&&seg_temp.row.at(j).y<=y2
//                        &&seg_temp.row.at(j).z>=z1&&seg_temp.row.at(j).z<=z2)
//                {
//                    testVNL.seg[i].to_be_deleted=1;
//                    break;
//                }
//            }
//        }
//        for(int i=0;i<testVNL.seg.size();i++)
//        {
//            if(testVNL.seg[i].to_be_deleted==0)
//                resVNL.seg.push_back(testVNL.seg.at(i));
//        }
//    }
//    QString BBSWCNAME="blockSet__"+QFileInfo(name).baseName()+QString("__%1__%2__%3__%4__%5__%6.swc")
//            .arg(x1).arg(x2).arg(y1).arg(y2).arg(z1).arg(z2);
//    NeuronTree nt=readSWC_file(QCoreApplication::applicationDirPath()+"/tmp/"+BBSWCNAME);
//    for(int i=0;i<nt.listNeuron.size();i++)
//    {
//        nt.listNeuron[i].x+=x1;
//        nt.listNeuron[i].y+=y1;
//        nt.listNeuron[i].z+=z1;
//    }
//    V_NeuronSWC_list testVNL1=NeuronTree__2__V_NeuronSWC_list(nt);
//    for(int i=0;i<testVNL1.seg.size();i++)
//    {
//        resVNL.seg.push_back(testVNL1.seg.at(i));
//    }
//    nt=V_NeuronSWC_list__2__NeuronTree(resVNL);
//    writeESWC_file(QCoreApplication::applicationDirPath()+"/data/"+name,nt);
//    QFile f(QCoreApplication::applicationDirPath()+"/tmp/"+BBSWCNAME);
//    f.remove();
//    return true;
//}

//void Server::setSwcInBB(QString name, int x1, int x2, int y1, int y2, int z1, int z2)
//{
//    SetInfo info{name,x1,x2,y1,y2,z1,z2};
//    setInfos.push_back(info);
//    for(auto i=setInfos.begin();i!=setInfos.end();)
//    {
//        if(setSwcInBBByStruct(i->name,i->x1,i->x2,i->y1,i->y2,i->z1,i->z2))
//            i=setInfos.erase(i);
//        else
//            i++;
//    }
//}
