#ifndef BASICDATATYPE_H
#define BASICDATATYPE_H
#include <QString>
#include <QList>

#if defined(_MSC_VER) && (_WIN64)
#define V3DLONG long long
#else
#define V3DLONG long
#endif

struct RGBA8 {
    unsigned char r,g,b,a;
};

struct BasicSurfObj
{
    V3DLONG n;				// index
    RGBA8 color;
    bool on;
    bool selected;
    QString name;
    QString comment;
    BasicSurfObj() {n=0; color.r=color.g=color.b=color.a=255; on=true;selected=false; name=comment="";}
};

struct CellAPO  : public BasicSurfObj
{
    float x, y, z;		// point coordinates
    float intensity;
    float sdev, pixmax, mass;
    float volsize;		// volume size
    QString orderinfo;
    //char *timestamp;    // timestamp  LMG 26/9/2018
    QString timestamp;		// timestamp  LMG 27/9/2018
    CellAPO() {x=y=z=intensity=volsize=sdev=pixmax=mass=0; timestamp=""; orderinfo="";}
};

bool writeAPO_file(const QString& filename, const QList <CellAPO> & listCell)
{
    QString curFile = filename;

    FILE * fp = fopen(curFile.toLatin1(), "wt");
    if (!fp)	return false;

    fprintf(fp, "##n,orderinfo,name,comment,z,x,y, pixmax,intensity,sdev,volsize,mass,,,, color_r,color_g,color_b\n");
    CellAPO * p_pt=0;
    for (int i=0;i<listCell.size(); i++)
    {
        //then save
        p_pt = (CellAPO *)(&(listCell.at(i)));
        fprintf(fp, "%ld, %s, %s,%s, %5.3f,%5.3f,%5.3f, %5.3f,%5.3f,%5.3f,%5.3f,%5.3f,,,,%d,%d,%d\n", //change from V3DLONG type to float, 20121212, by PHC
                p_pt->n, //i+1,

                qPrintable(p_pt->orderinfo),
                qPrintable(p_pt->name),
                qPrintable(p_pt->comment),

                p_pt->z,
                p_pt->x,
                p_pt->y,

                p_pt->pixmax,
                p_pt->intensity,
                p_pt->sdev,
                p_pt->volsize,
                p_pt->mass,

                p_pt->color.r,
                p_pt->color.g,
                p_pt->color.b
        );
    }

    fclose(fp);
    return true;
}

#endif // BASICDATATYPE_H
