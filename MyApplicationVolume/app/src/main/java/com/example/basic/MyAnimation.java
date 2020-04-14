package com.example.basic;

import android.opengl.Matrix;

enum RotationType
{
    X,
    XY,
    XYZ,
    Y,
    YX,
    YXZ,
    Z,
    ZXY,
    ZYZ
}

public class MyAnimation {
    private boolean status;
    public int speed;
    public RotationType rotationType;
    private float angleX;
    private float angleY;
    private float angleZ;
    float[] rotationXMatrix = new float[16];
    float[] rotationYMatrix = new float[16];
    float[] rotationZMatrix = new float[16];
    private int count;

    public MyAnimation(){

        status = false;
        speed = 12;
        rotationType = RotationType.XYZ;
        angleX = 0f;
        angleY = 0f;
        angleZ = 0f;
        count = 0;
        Matrix.setIdentityM(rotationXMatrix,0);
        Matrix.setIdentityM(rotationYMatrix,0);
        Matrix.setIdentityM(rotationZMatrix,0);

    }

    public boolean getStatus(){
        return status;
    }

    public void setStatus(boolean status){
        this.status = status;
    }

    public float[] Rotation(float[] current_rotation){

        float[] x_axis = new float[4];
        float[] y_axis = new float[4];
        float[] z_axis = new float[4];
        float[] rotationMatrix = new float[16];


        Matrix.multiplyMV(x_axis, 0, current_rotation, 0, new float[]{1, 0, 0, 1}, 0);
        Matrix.multiplyMV(y_axis, 0, current_rotation, 0, new float[]{0, 1, 0, 1}, 0);
        Matrix.multiplyMV(z_axis, 0, current_rotation, 0, new float[]{0, 0, 1, 1}, 0);


        switch (rotationType){
            case XYZ:
                if (count < 600){
                    angleX += speed;
                    Matrix.setRotateM(rotationXMatrix, 0, angleX, x_axis[0], x_axis[1], x_axis[3]);
                    count ++;
                }else if (count>= 600 && count < 1200){
                    angleY += speed;
                    Matrix.setRotateM(rotationYMatrix, 0, angleY, x_axis[0], x_axis[1], x_axis[3]);
                    count ++;
                }else {
                    if (count == 1799){
                        count = -1;
                    }
                    angleY += speed;
                    Matrix.setRotateM(rotationYMatrix, 0, angleY, x_axis[0], x_axis[1], x_axis[3]);
                    count ++;
                }


        }

        Matrix.multiplyMM(rotationMatrix,0, rotationMatrix,0, rotationXMatrix,0);
        Matrix.multiplyMM(rotationMatrix,0, rotationMatrix,0, rotationYMatrix,0);
        Matrix.multiplyMM(rotationMatrix,0, rotationMatrix,0, rotationZMatrix,0);
        return rotationMatrix;
    }



}

