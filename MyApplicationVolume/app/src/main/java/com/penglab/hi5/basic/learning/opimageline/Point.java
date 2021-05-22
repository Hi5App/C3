package com.penglab.hi5.basic.learning.opimageline;

public class Point {
    public float x,y,z;

    public Point(){
        x = y = z = 0;
    }

    public Point(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(Point other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public void setXYZ(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setXYZ(Point other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public static double distanceTwoPoint(Point p1, Point p2){
        return Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y) + (p1.z - p2.z)*(p1.z - p2.z));
    }

    /**
     *
     * @param p1 作为中间的点，其他点作为两个角点
     * @param p2
     * @param p3
     * @return 返回一个角度（0~360）
     */
    public static double angleThreePoint(Point p1, Point p2, Point p3){
        Angle a1 = new Angle(p1,p2);
        Angle a2 = new Angle(p1,p3);
        return Math.acos(a1.dot(a2)/(distanceTwoPoint(p1,p2)*distanceTwoPoint(p1,p3)));
    }

    /**
     *
     * @param p1 作为输入点
     * @param p2 作为线的一个端点
     * @param p3 作为线的另一个端点
     * @return 点到线的距离
     */
    public static double pointToLine(Point p1, Point p2, Point p3){
        Angle a = new Angle(p2,p1);
        Angle c = new Angle(p2,p3);

        double aLength = a.length();
        double cLength = c.length();

        double acAngle = angleThreePoint(p2,p1,p3);
        double acSin = Math.sin(acAngle);
        double acCos = Math.cos(acAngle);

        if(aLength*acCos>cLength || acCos<0){
            double d1 = distanceTwoPoint(p1,p2);
            double d2 = distanceTwoPoint(p1,p3);
            return Math.min(d1, d2);
        }

        return aLength*acSin;
    }

}
