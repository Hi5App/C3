package com.example.basic;

import android.opengl.Matrix;
import android.util.Log;

import java.util.Arrays;

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
    public boolean status;
    public float speed;
    public RotationType rotationType;
    private float angleX;
    private float angleY;
    private float angleZ;
    float[] rotationXMatrix = new float[16];
    float[] rotationYMatrix = new float[16];
    float[] rotationZMatrix = new float[16];
    private int count;

    float[] x_axis = new float[4];
    float[] y_axis = new float[4];
    float[] z_axis = new float[4];
    float[] current_axis = new float[4];


    public MyAnimation(){

        status = false;
        speed = 36/60f;                            //36度每秒钟
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

        float[] rotationMatrix = new float[16];
        Matrix.setIdentityM(rotationMatrix,0);

//        Matrix.multiplyMV(x_axis, 0, current_rotation, 0, new float[]{-1, 0, 0, 1}, 0);
//        Matrix.multiplyMV(y_axis, 0, current_rotation, 0, new float[]{0, 1, 0, 1}, 0);
//        Matrix.multiplyMV(z_axis, 0, current_rotation, 0, new float[]{0, 0, 1, 1}, 0);

//                    Matrix.multiplyMM(rotationMatrix,0, rotationMatrix,0, rotationYMatrix,0);


        switch (rotationType){
            case X:
                if (count == 0){
                    setX_axis(current_rotation);
                }
                break;

            case XY:
                if (count < 600){

                    if(count == 0){
                        setX_axis(current_rotation);
                    }

                }else {

                    if (count == 600){
                        setY_axis(current_rotation);
                    }

                    if (count == 1199){
                        count = -1;
                    }
                }
                break;

            case XYZ:
                if (count < 600){

                    if(count == 0){
                        setX_axis(current_rotation);
                    }

                }else if (count < 1200){

                    if (count == 600){
                        setY_axis(current_rotation);
                    }

                }else {
                    if (count == 1200){
                        setZ_axis(current_rotation);
                    }

                    if (count == 1799){
                        count = -1;
                    }
                }
                break;

            case Y:
                if (count == 0){
                    setY_axis(current_rotation);
                }
                break;

            case YX:
                if (count < 600){

                    if(count == 0){
                        setY_axis(current_rotation);
                    }

                }else {

                    if (count == 600){
                        setX_axis(current_rotation);
                    }

                    if (count == 1199){
                        count = -1;
                    }
                }
                break;


        }


        count ++;
        Matrix.setRotateM(rotationMatrix, 0, speed, current_axis[0], current_axis[1], current_axis[2]);
        return rotationMatrix;

    }

    public void ResetAnimation(){

        Matrix.setIdentityM(rotationXMatrix, 0);
        Matrix.setIdentityM(rotationYMatrix, 0);
        Matrix.setIdentityM(rotationZMatrix, 0);
        count = 0;
    }

    private void setX_axis(float[] current_rotation){
        Matrix.multiplyMV(current_axis, 0, current_rotation, 0, new float[]{-1, 0, 0, 1}, 0);
//        for (int i = 0; i < 4; i++)
//            current_axis[i] = x_axis[i];
    }

    private void setY_axis(float[] current_rotation){
        Matrix.multiplyMV(current_axis, 0, current_rotation, 0, new float[]{0, 1, 0, 1}, 0);
    }

    private void setZ_axis(float[] current_rotation){
        Matrix.multiplyMV(current_axis, 0, current_rotation, 0, new float[]{0, 0, 1, 1}, 0);
    }



}

