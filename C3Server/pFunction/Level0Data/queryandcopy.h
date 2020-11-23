#ifndef QUERYANDCOPY_H
#define QUERYANDCOPY_H
#include "layer.h"
#include "block.h"
#include "point.h"


class QueryAndCopy
{
public:
    QueryAndCopy(QStringList swcfile,QString inputDir,QString outputdir,float ratio);
    QueryAndCopy(QString inputdir);


public:
    bool readSWC(QString filename,int ratio);
    bool readMetaData(QString filename,bool mDataDebug=false);
    bool copyblock(QString srcFile,QString dstFile);


    bool query(float xx,float y,float z);
    QVector<QString> splitFilePath(QString filepath);
    QString getDirName(QString filepath);
    bool createDir(QString prePath,QString dirNmae);
    bool label(long index);
    long findClosest(OffsetType offsets,long idx);
    long findOffset(OffsetType offsets,long idx);

    OneScaleTree tree;
    PointCloud pc;

    float org_V,org_H,org_D;
    enum axis {vertical =1,inv_vertical =-1,horizontal =2,inv_horizontal = -2,depth =3,inv_depth =-3,axis_invalid =0};
    axis reference_V,reference_H,reference_D;
    float mdata_version;
    unsigned int color,bytesPerVoxel;
    long cubex,cubey,cubez;
    long sx,sy,sz;
    OffsetType xoff,yoff,zoff;
    ZeroBlock zeroblocks;


    Layer layer;
};

inline void getlevel0data(const QStringList swcLists,const QString inputImageDir,const QString outputDir,int ratio)
{
    QueryAndCopy qc(swcLists,inputImageDir,outputDir,pow(2,ratio));
}

#endif // QUERYANDCOPY_H
