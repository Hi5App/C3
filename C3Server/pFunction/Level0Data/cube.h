#ifndef CUBE_H
#define CUBE_H
#include <QString>

class Cube
{
public:
    Cube();    
public:
    int offset_D;
    QString fileName; //000000_000000_000000.tif
    QString filePath; //outdir/RESXXxXXxXX/000000/000000_000000/000000_000000_000000.tif
    unsigned int depth;//depth
    bool toBeCopied=false;
};

#endif // CUBE_H
