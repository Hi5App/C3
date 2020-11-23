#ifndef LAYER_H
#define LAYER_H

#include "yxfolder.h"
class Layer
{
public:
    unsigned short rows,cols;
    unsigned int ncubes;
    float vs_x=1,vs_y=1,vs_z=1;
    unsigned int dim_V,dim_H,dim_D;
    QString layerName;
    QMap<QString, YXFolder> yxfolders;
};

#endif // LAYER_H
