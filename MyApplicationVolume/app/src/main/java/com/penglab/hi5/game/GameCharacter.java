package com.penglab.hi5.game;

import android.opengl.Matrix;

import java.util.ArrayList;

public class GameCharacter {
    private float [] position;
    private float [] dir;
    private float [] head;

    private float [] thirdPosition;
    private float [] thirdDir;
    private float [] thirdHead;

    public GameCharacter(){
        position = new float[3];
        dir = new float[3];
        head = new float[3];
        thirdPosition = new float[3];
        thirdDir = new float[3];
        thirdHead = new float[3];
    }

    public GameCharacter(float [] position, float [] dir, float [] head){
        this.position = position;
        this.dir = dir;
        this.head = head;
        thirdPosition = new float[3];
        thirdDir = new float[3];
        thirdHead = new float[3];
    }

    public void setPosition(float [] position){
        this.position = position;
    }

    public void setDir(float [] dir){
        this.dir = dir;
    }

    public void setHead(float [] head){
        this.head = head;
    }

    public float [] getPosition(){
        return position;
    }

    public float [] getDir(){
        return dir;
    }

    public float [] getHead(){
        return head;
    }

    public void setThirdPosition(float [] thirdPos){
        this.thirdPosition = thirdPos;
    }

    public void setThirdDir(float [] thirdDir){
        this.thirdDir = thirdDir;
    }

    public void setThirdHead(float [] thirdHead){
        this.thirdHead = thirdHead;
    }

    public float [] getThirdPosition(){
        return thirdPosition;
    }

    public float [] getThirdDir(){
        return thirdDir;
    }

    public float [] getThirdHead() {
        return thirdHead;
    }

    public void rotateDir(float angleH, float angleV){
        float [] dirE = new float[]{dir[0], dir[1], dir[2], 1};
        float [] headE = new float[]{head[0], head[1], head[2], 1};

        if (angleH != 0 && angleV != 0) {

            float[] rotationHMatrix = new float[16];
            float[] rotationVMatrix = new float[16];

            Matrix.setRotateM(rotationHMatrix, 0, angleH, head[0], head[1], head[2]);

            Matrix.multiplyMV(dirE, 0, rotationHMatrix, 0, dirE, 0);

            float [] axisV = new float[]{dirE[1] * head[2] - dirE[2] * head[1], dirE[2] * head[0] - dirE[0] * head[2], dirE[0] * head[1] - dirE[1] * head[0]};

            Matrix.setRotateM(rotationVMatrix, 0, -angleV, axisV[0], axisV[1], axisV[2]);

            Matrix.multiplyMV(dirE, 0, rotationVMatrix, 0, dirE, 0);
            Matrix.multiplyMV(headE, 0, rotationVMatrix, 0, headE, 0);

            dir = new float[]{dirE[0], dirE[1], dirE[2]};
            head = new float[]{headE[0], headE[1], headE[2]};
        }
    }

    public void movePosition(float x, float y){


        float [] axisV = new float[]{dir[1] * head[2] - dir[2] * head[1], dir[2] * head[0] - dir[0] * head[2], dir[0] * head[1] - dir[1] * head[0]};
        float XL = (float)Math.sqrt(axisV[0] * axisV[0] + axisV[1] * axisV[1] + axisV[2] * axisV[2]);
        float [] X = new float[]{axisV[0] / XL, axisV[1] / XL, axisV[2] / XL};
        float YL = (float)Math.sqrt(dir[0] * dir[0] + dir[1] * dir[1] + dir[2] * dir[2]);
        float [] Y = new float[]{dir[0] / YL, dir[1] / YL, dir[2] / YL};

        position[0] = position[0] + X[0] * x - Y[0] * y;
        position[1] = position[1] + X[1] * x - Y[1] * y;
        position[2] = position[2] + X[2] * x - Y[2] * y;

//            myrenderer.clearMarkerList();
//            myrenderer.addMarker(position);

    }

    public void thirdPersonAngle(float behind, float up, float front, float [] thirdPos, float [] thirdDir){
        // (x0,y0,z0)是mark的坐标，（m,n,p）是mark前进方向的向量,(h1,h2,h3)是防止头脚颠倒的向量
        //用behind来控制视角相对于mark向后移了多少
        //用up来控制视角相对于mark向后上抬了多少
        //用front来控制mark在方向向量的方向上，视角可以看到的赛道长度,然后就可以确定法向量
        // 1.首先求防止头脚颠倒的向量。 先求一个与方向向量垂直，且与XOZ 平行的向量（则与XOZ法向量垂直（0,1,0））,然后可以直接利用向量积来求。
        // 然后防止头脚颠倒的向量是与前面两个向量(方向向量和与其垂直的向量)垂直的向量（也可以直接利用向量积就可以求出来）
        // 向量积：(a1,b1,c1)与(a2,b2,c2) 向量积 (b1c2-b2c1,a2c1-a1c2,a1b2-a2b1)
        // 方向向量(m,n,p)是 (a1,b1,c1)
        // 2.视角所在的点与前面的方向向量和求出来的防止颠倒的向量在同一平面，视角看进去的法向量应该也是在这个平面里的，可以先求法向量好求一点。
        // 如何求这个平面呢：方向向量和求出来的防止颠倒的向量做向量积得到平面的法向量，然后利用平面的点法式。
        // 到这里可以得到他和方向向量所在的平面方程：p(m^2-n^2-p^2)(X-x0)+m(p^2-m^2-n^2)(Z-z0)=0,没有Y，所以平面一定过Y轴
        // 最后没有用到这个平面. 视角在mark的斜上后方，那视角的后肯定是针对方向向量来说的后，上应该就和防止头脚颠倒的这个方向。
//        float[] XOZ = {0,1,0}; // (a2,b2,c2)
//        float[] dir_ver = new float[3];

        float [] tempDir = new float[]{dir[0], dir[1], dir[2]};
        float x0 = position[0];
        float y0 = position[1];
        float z0 = position[2];
        float [] tempHead = new float[]{head[0], head[1], head[2]};
        float[] des = new float[6]; //数组的前三位存视角坐标，后三位用来存看进去的法向量

//        final float rad = (float) (45*(Math.PI/180)); //一般都要把角度转换成弧度。 前面的45就是我们常用的角度，是可以看情况定的

//        dir_ver[0] = p; //a2
//        dir_ver[1] = 0; //b2
//        dir_ver[2] = m; //c2
        float tempDirLength = (float)Math.sqrt(tempDir[0] * tempDir[0] + tempDir[1] * tempDir[1] + tempDir[2] * tempDir[2]);
        tempDir[0] = tempDir[0] / tempDirLength;
        tempDir[1] = tempDir[1] / tempDirLength;
        tempDir[2] = tempDir[2] / tempDirLength; //都先归一化一下，防止方向向量会超过1 （其实也没必要好像，但是考虑到block大小是1）

        tempHead[0] = (float) (head[0]/Math.sqrt(head[0]*head[0]+head[1]*head[1]+head[2]*head[2]));
        tempHead[1] = (float) (head[1]/Math.sqrt(head[0]*head[0]+head[1]*head[1]+head[2]*head[2]));
        tempHead[2] = (float) (head[2]/Math.sqrt(head[0]*head[0]+head[1]*head[1]+head[2]*head[2]));


        des[0] = x0-behind*tempDir[0]+up*tempHead[0];
        des[1] = y0-behind*tempDir[0]+up*tempHead[1];
        des[2] = z0-behind*tempDir[0]+up*tempHead[2];  // 求出来的这个视角的法向量，就用mark沿着方向向量前进一点的点，减去视角的坐标

        float[] aid = {des[0]-x0,des[1]-y0,des[2]-z0}; // 只是一个辅助的中间变量
        if ((tempHead[0]*aid[0]+tempHead[1]*aid[1]+tempHead[2]*aid[2])/(Math.sqrt(tempHead[0]*tempHead[0]+tempHead[1]*tempHead[1]+tempHead[2]*tempHead[2])*Math.sqrt(aid[0]*aid[0]+aid[1]*aid[1]+aid[2]*aid[2])) < 0){
            des[0] = x0-behind*tempDir[0]-up*tempHead[0];
            des[1] = y0-behind*tempDir[1]-up*tempHead[1];
            des[2] = z0-behind*tempDir[2]-up*tempHead[2];  // 为了使视角跟防止头脚颠倒的向量 保持一致： 同时在上或者同时在下
        }

        des[3] = (x0+front*tempDir[0])-des[0];
        des[4] = (y0+front*tempDir[1])-des[1];
        des[5] = (z0+front*tempDir[2])-des[2];

//        thirdPos = new float[]{des[0], des[1], des[2]};
        thirdPos[0] = des[0];
        thirdPos[1] = des[1];
        thirdPos[2] = des[2];
//        thirdDir = new float[]{des[3], des[4], des[5]};
        thirdDir[0] = des[3];
        thirdDir[1] = des[4];
        thirdDir[2] = des[5];

    }

    public void setThirdPersonal(){

        thirdPersonAngle(0.1f, 0.03f, 0.3f, thirdPosition, thirdDir);

        float [] axis = new float[]{thirdDir[1] * head[2] - head[1] * thirdDir[2], thirdDir[2] * head[0] - head[2] * thirdDir[0], thirdDir[0] * head[1] - head[0] * thirdDir[1]};

//        float [] thirdHead = locateHead(dir[0], dir[1], dir[2]);
        thirdHead = new float[]{axis[1] * thirdDir[2] - thirdDir[1] * axis[2], axis[2] * thirdDir[0] - axis[0] * thirdDir[2], axis[0] * thirdDir[1] - axis[1] * thirdDir[0]};
        float acos = thirdHead[0] * head[0] + thirdHead[1] * head[1] + thirdHead[2] * head[2];
        if (acos > 0){

        } else {
            thirdHead = new float[]{-thirdHead[0], -thirdHead[1], -thirdHead[2]};
        }
    }

    public ArrayList<Float> tangentPlane(float [] pos, float [] d, float Pix){
// (x0,y0,z0)是视角的那个点，(m,n,p)是法向量，t是法线参数方程的参数t，Pix是block的像素
        ArrayList<Float> sec = new ArrayList<Float>();
        float x1 = pos[0]; // + m*t;
        float y1 = pos[1]; // + n*t;
        float z1 = pos[2]; // + p*t;

        float m = d[0];
        float n = d[1];
        float p = d[2];

        if (m!=0  & ((n*y1+p*z1)/m+x1) <= Pix & ((n*y1+p*z1)/m+x1)>=0){
            sec.add((n*y1+p*z1)/m+x1);
            sec.add((float) 0.0);
            sec.add((float) 0.0);
        }
        if (m!=0 & ((n*y1+p*(z1-Pix))/m+x1)<=Pix & ((n*y1+p*(z1-Pix))/m+x1)>=0){
            sec.add((n*y1+p*(z1-Pix))/m+x1);
            sec.add((float)0.0);
            sec.add(Pix);
        }
        if (m!=0 & ((n*(y1-Pix)+p*(z1-Pix))/m+x1)<=Pix & ((n*(y1-Pix)+p*(z1-Pix))/m+x1)>=0){
            sec.add((n*(y1-Pix)+p*(z1-Pix))/m+x1);
            sec.add(Pix);
            sec.add(Pix);
        }
        if (m!=0 & ((n*(y1-Pix)+p*z1)/m+x1)<=Pix & ((n*(y1-Pix)+p*z1)/m+x1)>=0){
            sec.add((n*(y1-Pix)+p*z1)/m+x1);
            sec.add(Pix);
            sec.add((float)0.0);
        }

        if (n!=0 & ((m*x1+p*z1)/n+y1)<Pix & ((m*x1+p*z1)/n+y1)>0){
            sec.add((float)0.0);
            sec.add((m*x1+p*z1)/n+y1);
            sec.add((float)0.0);
        }
        if (n!=0 & ((m*x1+p*(z1-Pix))/n+y1)<Pix & ((m*x1+p*(z1-Pix))/n+y1)>0){
            sec.add((float)0.0);
            sec.add((m*x1+p*(z1-Pix))/n+y1);
            sec.add(Pix);
        }
        if (n!=0 & ((m*(x1-Pix)+p*z1)/n+y1)<Pix & ((m*(x1-Pix)+p*z1)/n+y1)>0){
            sec.add(Pix);
            sec.add((m*(x1-Pix)+p*z1)/n+y1);
            sec.add((float)0.0);
        }
        if(n!=0 & ((m*(x1-Pix)+p*(z1-Pix))/n+y1)<Pix & ((m*(x1-Pix)+p*(z1-Pix))/n+y1)>0){
            sec.add(Pix);
            sec.add((m*(x1-Pix)+p*(z1-Pix))/n+y1);
            sec.add(Pix);
        }

        if (p!=0 & ((m*x1+n*y1)/p+z1)<Pix & ((m*x1+n*y1)/p+z1)>0){
            sec.add((float)0.0);
            sec.add((float)0.0);
            sec.add((m*x1+n*y1)/p+z1);
        }
        if (p!=0 & ((m*x1+n*(y1-Pix))/p+z1)<Pix & ((m*x1+n*(y1-Pix))/p+z1)>0){
            sec.add((float)0.0);
            sec.add(Pix);
            sec.add((m*x1+n*(y1-Pix))/p+z1);
        }
        if (p!=0 & ((m*(x1-Pix)+n*y1)/p+z1)<Pix & ((m*(x1-Pix)+n*y1)/p+z1)>0){
            sec.add(Pix);
            sec.add((float)0.0);
            sec.add((m*(x1-Pix)+n*y1)/p+z1);
        }
        if (p!=0 & ((m*(x1-Pix)+n*(y1-Pix))/p+z1)<Pix & ((m*(x1-Pix)+n*(y1-Pix))/p+z1)>0){
            sec.add(Pix);
            sec.add(Pix);
            sec.add((m*(x1-Pix)+n*(y1-Pix))/p+z1);
        }

        return sec;
    }

    public ArrayList<Float> sortVertex(ArrayList<Float> tangent){
        ArrayList<Integer> sec_proj1 = new ArrayList<Integer>();
        ArrayList<Integer> sec_proj2 = new ArrayList<Integer>();
        ArrayList<Integer> sec_proj3 = new ArrayList<Integer>();
        ArrayList<Integer> sec_proj4 = new ArrayList<Integer>();
        ArrayList<Float> sec_anti = new ArrayList<Float>();
        ArrayList<Float> sec_copy = new ArrayList<Float>();
        float gravity_X = 0;
        float gravity_Y = 0;
        float gravity_Z = 0;

        sec_copy = (ArrayList<Float>) tangent.clone();

        System.out.println("TangentPlane:::::");
        System.out.println(tangent.size());

        for (int i=0;i<tangent.size();i+=3) {
            gravity_X += tangent.get(i);
        }
        for (int i=0;i<tangent.size();i+=3) {
            gravity_Y += tangent.get(i+1);
        }
        for (int i=0;i<tangent.size();i+=3) {
            gravity_Z += tangent.get(i+2);
        }
        gravity_X /= (tangent.size()/3);
        gravity_Y /= (tangent.size()/3);
        gravity_Z /= (tangent.size()/3);

        for (int i=0;i<tangent.size();i+=3) {
            tangent.set(i,tangent.get(i)-gravity_X);
        }
        for (int i=0;i<tangent.size();i+=3) {
            tangent.set(i+1, tangent.get(i+1)-gravity_Y);
        }
        for (int i=0;i<tangent.size();i+=3) {
            tangent.set(i+2, tangent.get(i+2)-gravity_Z);
        }

        //然后对三维坐标进行映射
        if (thirdDir[2]==0)
        //先判断切面是不是与XOY面垂直，如果垂直就映射到XOZ平面
        {
            for (int i=0;i<tangent.size();i+=3) {
                if(tangent.get(i)>=0 & tangent.get(i+2)>=0) {

                    sec_proj1.add(i);

                }// 第一象限
                else if(tangent.get(i)<=0 & tangent.get(i+2)>=0) {

                    sec_proj2.add(i);

                }// 第二象限
                else if(tangent.get(i)<=0 & tangent.get(i+2)<=0) {

                    sec_proj3.add(i);

                }// 第三象限
                else if(tangent.get(i)>=0 & tangent.get(i+2)<=0) {

                    sec_proj4.add(i);

                }// 第四象限

            }



            //只用判断大于1的情况，如果没有那就刚好不用管了，如果只有一个元素，那也不用排序了
            if (sec_proj1.size()>1) {
                for (int i=0;i<sec_proj1.size();i++) {
                    for (int j=0;j<sec_proj1.size()-i-1;j++) {
                        if(tangent.get(sec_proj1.get(j))!=0 & tangent.get(sec_proj1.get(j+1))!=0) {
                            if(tangent.get(sec_proj1.get(j)+2)/tangent.get(sec_proj1.get(j)) > tangent.get(sec_proj1.get(j+1)+2)/tangent.get(sec_proj1.get(j+1))) {
                                int temp = sec_proj1.get(j);
                                sec_proj1.set(j, sec_proj1.get(j+1));
                                sec_proj1.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj1.get(j))==0 & tangent.get(sec_proj1.get(j+1))==0) {
                                if(tangent.get(sec_proj1.get(j)+2)<tangent.get(sec_proj1.get(j+1)+2)) {
                                    int temp = sec_proj1.get(j);
                                    sec_proj1.set(j, sec_proj1.get(j+1));
                                    sec_proj1.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj1.get(j))==0) {
                                    int temp = sec_proj1.get(j);
                                    sec_proj1.set(j, sec_proj1.get(j+1));
                                    sec_proj1.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }

            if (sec_proj2.size()>1) {
                for (int i=0;i<sec_proj2.size();i++) {
                    for (int j=0;j<sec_proj2.size()-i-1;j++) {
                        if(tangent.get(sec_proj2.get(j))!=0 & tangent.get(sec_proj2.get(j+1))!=0) {
                            if(tangent.get(sec_proj2.get(j)+2)/tangent.get(sec_proj2.get(j)) > tangent.get(sec_proj2.get(j+1)+2)/tangent.get(sec_proj2.get(j+1))) {
                                int temp = sec_proj2.get(j);
                                sec_proj2.set(j, sec_proj2.get(j+1));
                                sec_proj2.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj2.get(j))==0 & tangent.get(sec_proj2.get(j+1))==0) {
                                if(tangent.get(sec_proj2.get(j)+2)<tangent.get(sec_proj2.get(j+1)+2)) {
                                    int temp = sec_proj2.get(j);
                                    sec_proj2.set(j, sec_proj2.get(j+1));
                                    sec_proj2.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj2.get(j))==0) {
                                    int temp = sec_proj2.get(j);
                                    sec_proj2.set(j, sec_proj2.get(j+1));
                                    sec_proj2.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }

            if (sec_proj3.size()>1) {
                for (int i=0;i<sec_proj3.size();i++) {
                    for (int j=0;j<sec_proj3.size()-i-1;j++) {
                        if(tangent.get(sec_proj3.get(j))!=0 & tangent.get(sec_proj3.get(j+1))!=0) {
                            if(tangent.get(sec_proj3.get(j)+2)/tangent.get(sec_proj3.get(j)) > tangent.get(sec_proj3.get(j+1)+2)/tangent.get(sec_proj3.get(j+1))) {
                                int temp = sec_proj3.get(j);
                                sec_proj3.set(j, sec_proj3.get(j+1));
                                sec_proj3.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj3.get(j))==0 & tangent.get(sec_proj3.get(j+1))==0) {
                                if(tangent.get(sec_proj3.get(j)+2)<tangent.get(sec_proj3.get(j+1)+2)) {
                                    int temp = sec_proj3.get(j);
                                    sec_proj3.set(j, sec_proj3.get(j+1));
                                    sec_proj3.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj3.get(j))==0) {
                                    int temp = sec_proj3.get(j);
                                    sec_proj3.set(j, sec_proj3.get(j+1));
                                    sec_proj3.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }

            if (sec_proj4.size()>1) {
                for (int i=0;i<sec_proj4.size();i++) {
                    for (int j=0;j<sec_proj4.size()-i-1;j++) {
                        if(tangent.get(sec_proj4.get(j))!=0 & tangent.get(sec_proj4.get(j+1))!=0) {
                            if(tangent.get(sec_proj4.get(j)+2)/tangent.get(sec_proj4.get(j)) > tangent.get(sec_proj4.get(j+1)+2)/tangent.get(sec_proj4.get(j+1))) {
                                int temp = sec_proj4.get(j);
                                sec_proj4.set(j, sec_proj4.get(j+1));
                                sec_proj4.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj4.get(j))==0 & tangent.get(sec_proj4.get(j+1))==0) {
                                if(tangent.get(sec_proj4.get(j)+1)<tangent.get(sec_proj4.get(j+1)+1)) {
                                    int temp = sec_proj4.get(j);
                                    sec_proj4.set(j, sec_proj4.get(j+1));
                                    sec_proj4.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj4.get(j))==0) {
                                    int temp = sec_proj4.get(j);
                                    sec_proj4.set(j, sec_proj4.get(j+1));
                                    sec_proj4.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }


        }
        else {
            for (int i=0;i<tangent.size();i+=3) {
                if(tangent.get(i)>=0 & tangent.get(i+1)>=0) {

                    sec_proj1.add(i);

                }// 第一象限
                else if(tangent.get(i)<=0 & tangent.get(i+1)>=0) {

                    sec_proj2.add(i);

                }// 第二象限
                else if(tangent.get(i)<=0 & tangent.get(i+1)<=0) {

                    sec_proj3.add(i);

                }// 第三象限
                else if(tangent.get(i)>=0 & tangent.get(i+1)<=0) {

                    sec_proj4.add(i);

                }// 第四象限

            }




            //只用判断大于1的情况，如果没有那就刚好不用管了，如果只有一个元素，那也不用排序了
            if (sec_proj1.size()>1) {
                for (int i=0;i<sec_proj1.size();i++) {
                    for (int j=0;j<sec_proj1.size()-i-1;j++) {
                        if(tangent.get(sec_proj1.get(j))!=0 & tangent.get(sec_proj1.get(j+1))!=0) {
                            if(tangent.get(sec_proj1.get(j)+1)/tangent.get(sec_proj1.get(j)) > tangent.get(sec_proj1.get(j+1)+1)/tangent.get(sec_proj1.get(j+1))) {
                                int temp = sec_proj1.get(j);
                                sec_proj1.set(j, sec_proj1.get(j+1));
                                sec_proj1.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj1.get(j))==0 & tangent.get(sec_proj1.get(j+1))==0) {
                                if(tangent.get(sec_proj1.get(j)+1)<tangent.get(sec_proj1.get(j+1)+1)) {
                                    int temp = sec_proj1.get(j);
                                    sec_proj1.set(j, sec_proj1.get(j+1));
                                    sec_proj1.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj1.get(j))==0) {
                                    int temp = sec_proj1.get(j);
                                    sec_proj1.set(j, sec_proj1.get(j+1));
                                    sec_proj1.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }

            if (sec_proj2.size()>1) {
                for (int i=0;i<sec_proj2.size();i++) {
                    for (int j=0;j<sec_proj2.size()-i-1;j++) {
                        if(tangent.get(sec_proj2.get(j))!=0 & tangent.get(sec_proj2.get(j+1))!=0) {
                            if(tangent.get(sec_proj2.get(j)+1)/tangent.get(sec_proj2.get(j)) > tangent.get(sec_proj2.get(j+1)+1)/tangent.get(sec_proj2.get(j+1))) {
                                int temp = sec_proj2.get(j);
                                sec_proj2.set(j, sec_proj2.get(j+1));
                                sec_proj2.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj2.get(j))==0 & tangent.get(sec_proj2.get(j+1))==0) {
                                if(tangent.get(sec_proj2.get(j)+1)<tangent.get(sec_proj2.get(j+1)+1)) {
                                    int temp = sec_proj2.get(j);
                                    sec_proj2.set(j, sec_proj2.get(j+1));
                                    sec_proj2.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj2.get(j))==0) {
                                    int temp = sec_proj2.get(j);
                                    sec_proj2.set(j, sec_proj2.get(j+1));
                                    sec_proj2.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }

            if (sec_proj3.size()>1) {
                for (int i=0;i<sec_proj3.size();i++) {
                    for (int j=0;j<sec_proj3.size()-i-1;j++) {
                        if(tangent.get(sec_proj3.get(j))!=0 & tangent.get(sec_proj3.get(j+1))!=0) {
                            if(tangent.get(sec_proj3.get(j)+1)/tangent.get(sec_proj3.get(j)) > tangent.get(sec_proj3.get(j+1)+1)/tangent.get(sec_proj3.get(j+1))) {
                                int temp = sec_proj3.get(j);
                                sec_proj3.set(j, sec_proj3.get(j+1));
                                sec_proj3.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj3.get(j))==0 & tangent.get(sec_proj3.get(j+1))==0) {
                                if(tangent.get(sec_proj3.get(j)+1)<tangent.get(sec_proj3.get(j+1)+1)) {
                                    int temp = sec_proj3.get(j);
                                    sec_proj3.set(j, sec_proj3.get(j+1));
                                    sec_proj3.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj3.get(j))==0) {
                                    int temp = sec_proj3.get(j);
                                    sec_proj3.set(j, sec_proj3.get(j+1));
                                    sec_proj3.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }

            if (sec_proj4.size()>1) {
                for (int i=0;i<sec_proj4.size();i++) {
                    for (int j=0;j<sec_proj4.size()-i-1;j++) {
                        if(tangent.get(sec_proj4.get(j))!=0 & tangent.get(sec_proj4.get(j+1))!=0) {
                            if(tangent.get(sec_proj4.get(j)+1)/tangent.get(sec_proj4.get(j)) > tangent.get(sec_proj4.get(j+1)+1)/tangent.get(sec_proj4.get(j+1))) {
                                int temp = sec_proj4.get(j);
                                sec_proj4.set(j, sec_proj4.get(j+1));
                                sec_proj4.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj4.get(j))==0 & tangent.get(sec_proj4.get(j+1))==0) {
                                if(tangent.get(sec_proj4.get(j)+1)<tangent.get(sec_proj4.get(j+1)+1)) {
                                    int temp = sec_proj4.get(j);
                                    sec_proj4.set(j, sec_proj4.get(j+1));
                                    sec_proj4.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj4.get(j))==0) {
                                    int temp = sec_proj4.get(j);
                                    sec_proj4.set(j, sec_proj4.get(j+1));
                                    sec_proj4.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }        }



        for(int i=0;i<sec_proj1.size();i++) {
            sec_anti.add(sec_copy.get(sec_proj1.get(i)));
            sec_anti.add(sec_copy.get(sec_proj1.get(i)+1));
            sec_anti.add(sec_copy.get(sec_proj1.get(i)+2));
        }
        for(int i=0;i<sec_proj2.size();i++) {
            sec_anti.add(sec_copy.get(sec_proj2.get(i)));
            sec_anti.add(sec_copy.get(sec_proj2.get(i)+1));
            sec_anti.add(sec_copy.get(sec_proj2.get(i)+2));
        }
        for(int i=0;i<sec_proj3.size();i++) {
            sec_anti.add(sec_copy.get(sec_proj3.get(i)));
            sec_anti.add(sec_copy.get(sec_proj3.get(i)+1));
            sec_anti.add(sec_copy.get(sec_proj3.get(i)+2));
        }
        for(int i=0;i<sec_proj4.size();i++) {
            sec_anti.add(sec_copy.get(sec_proj4.get(i)));
            sec_anti.add(sec_copy.get(sec_proj4.get(i)+1));
            sec_anti.add(sec_copy.get(sec_proj4.get(i)+2));
        }

        return sec_anti;
    }

    public boolean closeToBoundary(){
        if (position[0] > 0.8 || position[0] < 0.2 || position[1] > 0.8 || position[1] < 0.2 || position[2] > 0.8 || position[2] < 0.2)
            return true;

        return false;
    }

    public void move(float [] dir, float dis){

        position[0] += dir[0] * dis;
        position[1] += dir[1] * dis;
        position[2] += dir[2] * dis;
    }
}
