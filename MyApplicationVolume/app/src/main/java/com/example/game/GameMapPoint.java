package com.example.game;

public class GameMapPoint {
    public float x;
    public float y;
    public float z;

    public int type;

    public float radius;
    public int score;

    public GameMapPoint(float _x, float _y, float _z, int t, float r){
        x = _x;
        y = _y;
        z = _z;
        type = t;
        radius = r;
    }

    public GameMapPoint(float _x, float _y, float _z){
        x = _x;
        y = _y;
        z = _z;
        type = 6;
        radius = 0.002f;
    }

    public GameMapPoint(float [] pos){
        x = pos[0];
        y = pos[1];
        z = pos[2];
        type = 6;
        radius = 0.002f;
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

    public void updateScore(){

    }
}
