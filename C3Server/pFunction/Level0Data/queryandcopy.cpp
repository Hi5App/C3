#include "queryandcopy.h"
#include <basic_c_fun/basic_surf_objs.h>
#include <algorithm>
#include <iostream>
#include <fstream>
bool QueryAndCopy::readSWC(QString filename, int ratio)
{
    NeuronTree nt = readSWC_file(filename);
    if(ratio<=1) ratio=1;
    for(long i=0;i<nt.listNeuron.size();i++)
    {
        pc.push_back(Point(nt.listNeuron[i].x/ratio,
                           nt.listNeuron[i].y/ratio,
                           nt.listNeuron[i].z/ratio));
    }
    return true;
}

bool QueryAndCopy::readMetaData(QString inputImageDir, bool mDataDebug)
{
    QString inputDir=inputImageDir;
    QDir outdir(inputDir);
    if(!outdir.exists()) return false;
    QString blockNamePrefix=inputDir+"/";
    QString fileName=inputDir+"/mdata.bin";

    if(!QFile(fileName).exists()) return false;
    {
        FILE *file=fopen(fileName.toStdString().c_str(),"rb");
        {
            fread(&(mdata_version),sizeof (float),1,file);
            fread(&(reference_V),sizeof (axis),1,file);
            fread(&(reference_H),sizeof (axis),1,file);
            fread(&(reference_D),sizeof(axis),1,file);
            fread(&(layer.vs_x),sizeof (float),1,file);
            fread(&(layer.vs_y),sizeof (float),1,file);
            fread(&(layer.vs_z),sizeof (float),1,file);

            fread(&(layer.vs_x),sizeof (float),1,file);
            fread(&(layer.vs_y),sizeof (float),1,file);
            fread(&(layer.vs_z),sizeof (float),1,file);

            fread(&(org_V),sizeof (float),1,file);
            fread(&(org_H),sizeof (float),1,file);
            fread(&(org_D),sizeof (float),1,file);

            fread(&(layer.dim_V),sizeof (float),1,file);
            fread(&(layer.dim_H),sizeof (float),1,file);
            fread(&(layer.dim_D),sizeof (float),1,file);
            fread(&(layer.rows),sizeof (float),1,file);
            fread(&(layer.cols),sizeof (float),1,file);
            sx=layer.dim_H;
            sy=layer.dim_V;
            sz=layer.dim_D;
        }
        int count =0;
        if(mDataDebug)
        {
            qDebug()<<"filename "<<fileName<<endl;
            qDebug()<<"meta.mdata_version "<<mdata_version<<endl;
            qDebug()<<"meta.reference_V "<<reference_V<<endl;
            qDebug()<<"meta.reference_H "<<reference_H<<endl;
            qDebug()<<"meta.reference_D "<<reference_D<<endl;
            qDebug()<<"layer.vs_x "<<layer.vs_x<<endl;
            qDebug()<<"layer.vs_y "<<layer.vs_y<<endl;
            qDebug()<<"layer.vs_z "<<layer.vs_z<<endl;
            qDebug()<<"meta.org_V "<<org_V<<endl;
            qDebug()<<"meta.org_H "<<org_H<<endl;
            qDebug()<<"meta.org_D "<<org_D<<endl;
            qDebug()<<"layer.dim_V "<<layer.dim_V<<endl;
            qDebug()<<"layer.dim_H "<<layer.dim_H<<endl;
            qDebug()<<"layer.dim_D "<<layer.dim_D<<endl;
            qDebug()<<"layer.rows "<<layer.rows<<endl;
            qDebug()<<"layer.cols "<<layer.cols<<endl;
        }

        int n = layer.rows*layer.cols;
        for(int i=0;i<n;i++)
        {
            YXFolder yxfolder;
            {
                fread(&(yxfolder.height),sizeof (unsigned int),1,file);
                fread(&(yxfolder.width),sizeof (unsigned int),1,file);
                fread(&(layer.dim_D),sizeof (unsigned int),1,file);
                fread(&(yxfolder.ncubes),sizeof (unsigned int),1,file);
                fread(&(color),sizeof (unsigned int),1,file);
                fread(&(yxfolder.offset_V),sizeof ( int),1,file);
                fread(&(yxfolder.offset_H),sizeof ( int),1,file);
                fread(&(yxfolder.lengthDirName),sizeof (unsigned short),1,file);
                std::string dirName(yxfolder.lengthDirName,'\0');
                fread(&dirName,sizeof (unsigned int),1,file);
                yxfolder.dirName=QString::fromStdString(dirName);
            }
            if(mDataDebug)
            {
                qDebug()<<"... "<<endl;
                qDebug()<<"HEIGHT "<<yxfolder.height<<endl;
                qDebug()<<"WIDTH "<<yxfolder.width<<endl;
                qDebug()<<"DEPTH "<<layer.dim_D<<endl;
                qDebug()<<"N_BLOCKS "<<yxfolder.ncubes<<endl;
                qDebug()<<"N_CHANS "<<color<<endl;
                qDebug()<<"ABS_V "<<yxfolder.offset_V<<endl;
                qDebug()<<"ABS_H "<<yxfolder.offset_H<<endl;
                qDebug()<<"str_size "<<yxfolder.lengthDirName<<endl;
                qDebug()<<"DIR_NAME "<<yxfolder.dirName<<endl;
            }

            for(int j=0;j<yxfolder.ncubes;j++)
            {
                Cube cube;
                fread(&(yxfolder.lengthFileName),sizeof (unsigned int),1,file);
                std::string fileName(yxfolder.lengthFileName,'\0');
                fread(&(fileName[0]),sizeof (char),yxfolder.lengthFileName,file);
                fread(&(cube.depth),sizeof (unsigned int),1,file);
                fread(&(cube.offset_D),sizeof (int),1,file);
                cube.fileName=QString::fromStdString(fileName);
                yxfolder.cubes.insert(cube.offset_D,cube);

                Block block(blockNamePrefix+yxfolder.dirName+"/"+cube.fileName,
                            long(yxfolder.offset_H),long(yxfolder.offset_V),long(cube.offset_D),
                            long(yxfolder.width),long(yxfolder.height),long(cube.depth));
                if(count==0)
                {
                    count=yxfolder.width;
                    count=yxfolder.height;
                    count=cube.depth;
                    count++;
                }
                if(std::find(xoff.begin(),xoff.end(),long(block.offset_x))==xoff.end()) xoff.push_back(long(block.offset_x));
                if(std::find(yoff.begin(),yoff.end(),long(block.offset_y))==yoff.end()) yoff.push_back(long(block.offset_y));
                if(std::find(zoff.begin(),zoff.end(),long(block.offset_z))==zoff.end()) zoff.push_back(long(block.offset_z));
                tree.insert(long(block.offset_z)*sx*sy+long(block.offset_y)*sx+long(block.offset_x),block);

                if(mDataDebug){
                    qDebug()<<"... ..."<<endl;
                    qDebug()<<"str_size "<<yxfolder.lengthFileName<<endl;
                    qDebug()<<"FILENAMES["<<cube.offset_D<<"] "<<cube.fileName<<endl;
                    qDebug()<<"BLOCK_SIZE+i "<<cube.depth<<endl;
                    qDebug()<<"BLOCK_ABS_D+i "<<cube.offset_D<<endl;
                }
            }
            fread(&(bytesPerVoxel),sizeof (unsigned int),1,file);
            if(mDataDebug) qDebug()<<"N_BYTESxCHAN "<<bytesPerVoxel<<endl;
            layer.yxfolders.insert(yxfolder.dirName,yxfolder);
        }
        fclose(file);
    }
    return true;
}

bool QueryAndCopy::copyblock(QString srcFile, QString dstFile)
{
    std::ifstream src(srcFile.toUtf8().constData(),std::ios::out|std::ios_base::binary);
    if(src.is_open())
    {
        std::ofstream dst(dstFile.toUtf8().constData(),std::ios_base::out | std::ios_base::binary);
        if(dst.is_open()){
            dst<<src.rdbuf();
            if(!dst.bad()) {
                dst.close();
                src.close();
                return true;
            }
        }
        dst.close();
    }
    src.close();
    return false;

}

bool QueryAndCopy::query(float x, float y, float z)
{
    if(tree.size()>0)
    {
        long lx=findOffset(xoff,long(x));
        long ly=findOffset(yoff,long(y));
        long lz=findOffset(zoff,long(z));

        long olx=lx;
        long oly=ly;

        long index=lz*sz*sy+ly*sz+lx;
        label(index);
        {
            lx=findOffset(xoff,long(x-cubex));
            index=lz*sz*sy+ly*sz+lx;
            label(index);

            lx=findOffset(xoff,long(x+cubex));
            index=lz*sz*sy+ly*sz+lx;
            label(index);
            lx=olx;
        }
        {
            ly=findOffset(yoff,long(y-cubey));
            index=lz*sz*sy+ly*sz+lx;
            label(index);

            ly=findOffset(yoff,long(y+cubey));
            index=lz*sz*sy+ly*sz+lx;
            label(index);
            ly=oly;
        }
        {
            lz=findOffset(zoff,long(z-cubez));
            index=lz*sz*sy+ly*sz+lx;
            label(index);

            lz=findOffset(zoff,long(z+cubez));
            index=lz*sz*sy+ly*sz+lx;
            label(index);
        }
        return true;
    }
    return false;
}

QVector<QString> QueryAndCopy::splitFilePath(QString filepath)
{
    return {filepath.section('/',-1)};
}

bool QueryAndCopy::label(long index)
{
    if(tree.find(index)!=tree.end())
    {
        Block block = tree[index];
        if(block.visited==false)
        {
            QString filePath=block.filePath;
            QString dirName;
            auto splits=filePath.split("/",QString::SkipEmptyParts);
            int n=splits.size();
            if(splits.size()<3) dirName="" ;
            else
                dirName=splits[n-3]+"/"+splits[n-2];
            layer.yxfolders[dirName].cubes[block.offset_z].toBeCopied=true;
            layer.yxfolders[dirName].toBeCopied=true;
            tree[index].visited=true;
        }
        return true;
    }
    return false;
}


bool QueryAndCopy::createDir(QString prePath, QString dirNmae)
{
    QStringList splits=dirNmae.split("/",QString::SkipEmptyParts);
    if(splits.size()<2) return false;
    QString folder=prePath+"/"+splits[0];
    layer.yxfolders[dirNmae].xDirPath=folder;

    if(!QDir(folder).exists())
    {
        if(!QDir(prePath).mkdir(splits[0])) return false;
    }

    folder+="/"+splits[1];
    layer.yxfolders[dirNmae].yDirPath=folder;
    if(!QDir(folder).exists())
    {
        if(!QDir(prePath).mkdir(splits[0])) return false;
    }
    return true;

}

QueryAndCopy::QueryAndCopy(QStringList swcfile,QString inputDir,QString outputdir,float ratio)
{
    for(auto & swc:swcfile)
    {
        readSWC(swc,ratio);
    }

    long n=pc.size();
    for(long i=0;i<n;i++)
    {
        query(pc[i].x,pc[i].y,pc[i].z);
    }
    createDir("./",outputdir);

    {
        auto iter=layer.yxfolders.begin();
        while (iter!=layer.yxfolders.end()) {
            YXFolder yxfolder=(iter++).value();
            layer.yxfolders[yxfolder.dirName].ncubes=yxfolder.cubes.size();
        }
    }

    QString mdatabin=outputdir+"/mdata.bin";
    FILE *file=fopen(mdatabin.toStdString().c_str(),"wb");
    fwrite(&(mdata_version), sizeof(float), 1, file);
    fwrite(&(reference_V), sizeof(axis), 1, file);
    fwrite(&(reference_H), sizeof(axis), 1, file);
    fwrite(&(reference_D), sizeof(axis), 1, file);
    fwrite(&(layer.vs_x), sizeof(float), 1, file);
    fwrite(&(layer.vs_y), sizeof(float), 1, file);
    fwrite(&(layer.vs_z), sizeof(float), 1, file);
    fwrite(&(layer.vs_x), sizeof(float), 1, file);
    fwrite(&(layer.vs_y), sizeof(float), 1, file);
    fwrite(&(layer.vs_z), sizeof(float), 1, file);
    fwrite(&(org_V), sizeof(float), 1, file);
    fwrite(&(org_H), sizeof(float), 1, file);
    fwrite(&(org_D), sizeof(float), 1, file);
    fwrite(&(layer.dim_V), sizeof(unsigned int), 1, file);
    fwrite(&(layer.dim_H), sizeof(unsigned int), 1, file);
    fwrite(&(layer.dim_D), sizeof(unsigned int), 1, file);
    fwrite(&(layer.rows), sizeof(unsigned short), 1, file); // need to be updated by hits
    fwrite(&(layer.cols), sizeof(unsigned short), 1, file); // need to be updated by hits

    QString dirName = "zeroblocks/zeroblock"; //
    int count=0;
    int nyxfolders=layer.yxfolders.size();
    auto iter=layer.yxfolders.begin();
    while(iter!=layer.yxfolders.end())
    {
        if(count++ >= nyxfolders)
        {
            iter++;
            continue;
        }
    }
}
