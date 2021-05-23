package com.penglab.hi5.basic;

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
    ZYX
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

    public float[] Rotation(){
        float[] rotationMatrix = new float[16];
        Matrix.setIdentityM(rotationMatrix,0);

        switch (rotationType){

            case X:
                if (count == 0){
                    setX_axis();
                }
                break;

            case XY:
                if (count < 600){

                    if(count == 0){
                        setX_axis();
                    }

                }else {

                    if (count == 600){
                        setY_axis();
                    }

                    if (count == 1199){
                        count = -1;
                    }
                }
                break;

            case XYZ:
                if (count < 600){

                    if(count == 0){
                        setX_axis();
                    }

                }else if (count < 1200){

                    if (count == 600){
                        setY_axis();
                    }

                }else {
                    if (count == 1200){
                        setZ_axis();
                    }

                    if (count == 1799){
                        count = -1;
                    }
                }
                break;



            case Y:
                if (count == 0){
                    setY_axis();
                }
                break;

            case YX:
                if (count < 600){

                    if(count == 0){
                        setY_axis();
                    }

                }else {

                    if (count == 600){
                        setX_axis();
                    }

                    if (count == 1199){
                        count = -1;
                    }
                }
                break;

            case YXZ:
                if (count < 600){

                    if(count == 0){
                        setY_axis();
                    }

                }else if (count < 1200){

                    if (count == 600){
                        setX_axis();
                    }

                }else {
                    if (count == 1200){
                        setZ_axis();
                    }

                    if (count == 1799){
                        count = -1;
                    }
                }
                break;



            case Z:
                if (count == 0){
                    setZ_axis();
                }
                break;

            case ZXY:
                if (count < 600){

                    if(count == 0){
                        setZ_axis();
                    }

                }else if (count < 1200){

                    if (count == 600){
                        setX_axis();
                    }

                }else {
                    if (count == 1200){
                        setY_axis();
                    }

                    if (count == 1799){
                        count = -1;
                    }
                }
                break;

            case ZYX:
                if (count < 600){

                    if(count == 0){
                        setZ_axis();
                    }

                }else if (count < 1200){

                    if (count == 600){
                        setY_axis();
                    }

                }else {
                    if (count == 1200){
                        setY_axis();
                    }

                    if (count == 1799){
                        count = -1;
                    }
                }
                break;


        }

        count ++;
        Matrix.setRotateM(rotationMatrix, 0, speed, current_axis[0], current_axis[1], current_axis[2]);
        return rotationMatrix;

    }

    public void Start(){
        status = true;
    }

    public void Pause(){
        status = false;
    }

    public void Resume(){
        status = true;
        count = 0;
    }

    public void Stop(){
        status = false;
        count = 0;
        speed = 36/60f;
        rotationType = RotationType.XYZ;
    }

    public void setAnimation(boolean status, float speed, String type){
        this.status = status;
        this.speed = speed/60f;
        this.setRotationType(type);
        this.ResetAnimation();
        if (status == false){
            this.setRotationType("X");
        }
    }

    public void ResetAnimation(){

        Matrix.setIdentityM(rotationXMatrix, 0);
        Matrix.setIdentityM(rotationYMatrix, 0);
        Matrix.setIdentityM(rotationZMatrix, 0);
        count = 0;
    }

    public void quickStart(){

        Matrix.setIdentityM(rotationXMatrix, 0);
        Matrix.setIdentityM(rotationYMatrix, 0);
        Matrix.setIdentityM(rotationZMatrix, 0);
        this.status = true;
        this.speed = 36/60f;
        this.rotationType = RotationType.XYZ;
        count = 0;
    }

    public void quickStop(){
        this.status = false;
        count = 0;
    }

    public void setRotationType(String type){
        RotationType cur_type = null;
        switch (type){
            case "X":
                cur_type = RotationType.X;
                break;
            case "XY":
                cur_type = RotationType.XY;
                break;
            case "XYZ":
                cur_type = RotationType.XYZ;
                break;
            case "Y":
                cur_type = RotationType.Y;
                break;
            case "YX":
                cur_type = RotationType.YX;
                break;
            case "YXZ":
                cur_type = RotationType.YXZ;
                break;
            case "Z":
                cur_type = RotationType.Z;
                break;
            case "ZXY":
                cur_type = RotationType.ZXY;
                break;
            case "ZYX":
                cur_type = RotationType.ZYX;
                break;

            default:
                return;
        }
        if (cur_type != rotationType){
            rotationType = cur_type;
            ResetAnimation();
        }
    }


    private void setX_axis(){
//        System.out.println("-----------setX_axis------------");
        current_axis = new float[]{ -1, 0 ,0 ,1};
    }

    private void setY_axis(){
//        System.out.println("-----------setY_axis------------");
        current_axis = new float[]{0, -1, 0, 1};
    }

    private void setZ_axis(){
//        System.out.println("-----------setZ_axis------------");
        current_axis = new float[]{0, 0, 1, 1};
    }



}

