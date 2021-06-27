package com.penglab.hi5.basic.learning.objectclassification;

import android.graphics.Color;

public class Binary {
    public static int[] getBinaryImg(int w, int h, int[] inputs) {
        int[] gray = new int[w * h];
        int[] newpixel = new int[w * h];
        for (int index = 0; index < w * h; index++) {
            int red = (inputs[index] & 0x00FF0000) >> 16;
            int green = (inputs[index] & 0x0000FF00) >> 8;
            int blue = inputs[index] & 0x000000FF;
            gray[index] = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
        }
        //求出最大灰度值zmax和最小灰度值zmin
        int Gmax = gray[0], Gmin = gray[0];
        for (int index = 0; index < w * h; index++) {
            if (gray[index] > Gmax) {
                Gmax = gray[index];
            }
            if (gray[index] < Gmin) {
                Gmin = gray[index];
            }
        }

        //获取灰度直方图
        int i, j, t, count1 = 0, count2 = 0, sum1 = 0, sum2 = 0;
        int bp, fp;
        int[] histogram = new int[256];
        for (t = Gmin; t <= Gmax; t++) {
            for (int index = 0; index < w * h; index++) {
                if (gray[index] == t)
                    histogram[t]++;
            }
        }


        int T = 0;
        int newT = (Gmax + Gmin) / 2;//初始阈值
        while (T != newT)
        //求出背景和前景的平均灰度值bp和fp
        {
            for (i = 0; i < T; i++) {
                count1 += histogram[i];//背景像素点的总个数
                sum1 += histogram[i] * i;//背景像素点的灰度总值
            }
            bp = (count1 == 0) ? 0 : (sum1 / count1);//背景像素点的平均灰度值

            for (j = i; j < histogram.length; j++) {
                count2 += histogram[j];//前景像素点的总个数
                sum2 += histogram[j] * j;//前景像素点的灰度总值
            }
            fp = (count2 == 0) ? 0 : (sum2 / count2);//前景像素点的平均灰度值
            T = newT;
            newT = (bp + fp) / 2;
        }
        int finestYzt = newT; //最佳阈值

        //二值化
        for (int index = 0; index < w * h; index++) {
            if (gray[index] > finestYzt)
                newpixel[index] = Color.WHITE;
            else newpixel[index] = Color.BLACK;
        }
        return newpixel;
    }
}
