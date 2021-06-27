package com.penglab.hi5.basic.tracingfunc.TRe;

public class MyPoint implements Comparable<MyPoint>
{
    public final int compareTo(MyPoint otherInstance)
    {
        if (lessThan(otherInstance))
        {
            return -1;
        }
        else if (otherInstance.lessThan(this))
        {
            return 1;
        }

        return 0;
    }

    public int x;
    public int y;
    public int z;
    //WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: boolean operator <(const MyPoint & other) const
    public boolean lessThan(MyPoint other)
    {
        if (z > other.z)
            return false;
        if (z < other.z)
            return true;
        if (y > other.y)
            return false;
        if (y < other.y)
            return true;
        if (x > other.x)
            return false;
        if (x < other.x)
            return true;
        return false;
    }
    public MyPoint()
    {
        x = 0;
        y = 0;
        z = 0;
    }
    public MyPoint(int _x, int _y, int _z)
    {
        x = _x;
        y = _y;
        z = _z;
    }
}