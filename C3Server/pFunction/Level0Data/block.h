#ifndef BLOCK_H
#define BLOCK_H

#include <QString>
#include <QVector>
class Block
{
public:
    Block()=default;
    Block(QString fn,long xoff,long yoff,long zoff,long sx,long sy,long sz)
    {
        filePath=fn;offset_x=xoff;offset_y=yoff;offset_z=zoff;
        size_x=sx;size_y=sy;size_z=sz;visited=false;
    }
public:
    QString filePath;
    long offset_x,offset_y,offset_z;
    long size_x,size_y,size_z;
    bool visited;
};

typedef QMap<long,Block> OneScaleTree ;
typedef QVector<long> OffsetType;
typedef QMap<long,QString> ZeroBlock;
#endif // BLOCK_H
