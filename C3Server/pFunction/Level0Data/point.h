#ifndef POINT_H
#define POINT_H
#include <QVector>

class Point
{
public:
    Point(float a,float b,float c)
    {
        x=a;y=b;z=c;
    }

public:
    float x,y,z;
};
typedef QVector<Point> PointCloud ;
#endif // POINT_H
