package com.penglab.hi5.game;

import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_unit;

public class GameMapPoint {
    public float x;
    public float y;
    public float z;

    public int type;

    public float radius;
    public int score;

    public int nearPoints;
    public float proportion;

    public GameMapPoint(float _x, float _y, float _z, int t, float r){
        x = _x;
        y = _y;
        z = _z;
        type = t;
        radius = r;
        score = 10;
        nearPoints = 0;
        proportion = 1.0f;
    }

    public GameMapPoint(float _x, float _y, float _z){
        x = _x;
        y = _y;
        z = _z;
        type = 6;
        radius = 0.002f;
        score = 10;
        nearPoints = 0;
        proportion = 1.0f;
    }

    public GameMapPoint(float [] pos){
        x = pos[0];
        y = pos[1];
        z = pos[2];
        type = 6;
        radius = 0.002f;
        score = 10;
        nearPoints = 0;
        proportion = 1.0f;
    }

    public static float distance(GameMapPoint a, GameMapPoint b){
        return (float) Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z));
    }

    public void reduceRadius(float d){
        radius = radius * d;
    }

    public void setPos(float [] pos){
        x = pos[0];
        y = pos[1];
        z = pos[2];
    }

    public void moveTo(GameMapPoint g, float r){
        float disX = g.x - x;
        float disY = g.y - y;
        float disZ = g.z - z;

        x += disX * (1 - r);
        y += disY * (1 - r);
        z += disZ * (1 - r);
    }

    public void updateScore(V_NeuronSWC list){
        int count = 0;

        for (int i = 0; i < list.nrows(); i++){
            V_NeuronSWC_unit unit = list.row.get(i);
            if (((unit.x - x) * (unit.x - x) + (unit.y - y) * (unit.y - y) + (unit.z - z) * (unit.z - z)) < 400) {
                count++;
            }
        }

        if (count > 10){

        }
    }

    public void updateScore(){
        nearPoints++;

        if (nearPoints > 10 && proportion > 0){
            proportion -= 0.05;
        }
    }
}
