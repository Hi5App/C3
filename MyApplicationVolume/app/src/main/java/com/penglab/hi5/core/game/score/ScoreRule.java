package com.penglab.hi5.core.game.score;

/**
 * Created by Yihang zhu 01/14/21
 */
public class ScoreRule {
    private static int scorePerPinPoint = 1;
    private static int scorePerCurve = 2;
    private static int scorePerImage = 20;

    public static int getScorePerPinPoint() {
        return scorePerPinPoint;
    }

    public static int getScorePerCurve() {
        return scorePerCurve;
    }

    public static int getScorePerImage() {
        return scorePerImage;
    }
}
