package com.penglab.hi5.core.game.score;

/**
 * Created by Yihang zhu 01/14/21
 */
public class ScoreRule {
    private static int scorePerPinPoint = 1;
    private static int scorePerCurve = 2;
    private static int scorePerSoma = 1;
    private static int scorePerImage = 20;
    private static int scorePerRewardLevel = 30;
    private static int scorePerGuessMusic = 50;

    public static int getScorePerPinPoint() {
        return scorePerPinPoint;
    }

    public static int getScorePerSoma(int somaNum) {return scorePerSoma*somaNum;}

    public static int getScorePerCurve() {
        return scorePerCurve;
    }

    public static int getScorePerImage() {
        return scorePerImage;
    }

    public static int getScorePerRewardLevel() {return scorePerRewardLevel;}

    public static int getScorePerGuessMusic(){return scorePerGuessMusic;}

}
