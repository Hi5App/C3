#ifndef YXFOLDER_H
#define YXFOLDER_H

#include "cube.h"
#include <QMap>
class YXFolder
{
public:
public:
    int offset_V;
    int offset_H;
    unsigned short lengthFileName=25;//25 len("000000_000000_000000.tif")+1
    unsigned short lengthDirName=21;//21 len("000000/000000_000000")+1

    QString dirName;
    QString xDirPath;
    QString yDirPath;

    unsigned int height,width;
    bool toBeCopied=false;
    unsigned int ncubes;
    QMap<int,Cube> cubes;

};



#endif // YXFOLDER_H
