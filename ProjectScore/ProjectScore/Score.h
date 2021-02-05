#pragma once
#ifndef SCORE_H
#define SCORE_H

/*
基本：
Curve 2
Marker 1

成就：
1.	首次登陆       100
2.	首次打开图像   100
3.	首次划线       100
4.	首次点点       100
5.	划线100次      500
6.	点点100次      300
7.

每日任务：
1.	首次登陆       20
2.	首次打开图像   20
3.	首次划线       20
4.	首次点点       20
5.

*/

class Score {
private:
	int id;
	int score;
	int curveNum;
	int markerNum;
	int lastLoginYear;
	int lastLoginDay;
	int curveNumToday;
	int markerNumToday;
	int editImageNum;
	int editImageNumToday;

	int achievementsScore[7] = { -1, 100, 100, 100, 100, 500, 300 };
	int dailyQuestsScore[5] = { -1, 20, 20, 20, 20 };

public:
	void drawACurve();
	void pinpoint();
	void openImage();
	void achievementFinished(int i);
	void dailyQuestFinished(int i);
	Score();
	~Score();
	
	int getScore() { return score; }
	int getCurveNum() { return curveNum; }
	int getMarkerNum() { return markerNum; }
	int getLastLoginYear() { return lastLoginYear; }
	int getLastLoginDay() { return lastLoginDay; }
	int getCurveNumToday() { return curveNumToday; }
	int getMarkerNumToday() { return markerNumToday; }
	int getEditImageNum() { return editImageNum; }
	int getEditImageNumToday() { return editImageNumToday; }
};
#endif