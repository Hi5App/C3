package com.penglab.hi5.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.penglab.hi5.R;

public class MyRockerView extends View {


    private float mRockerBg_X;
    private float mRockerBg_Y;
    private float mRockerBg_R;

    private float mRockerBtn_X;
    private float mRockerBtn_Y;
    private float mRockerBtn_R;
    private Bitmap mBmpRockerBg;
    private Bitmap mBmpRockerBtn;

    private PointF mCenterPoint;

    private Thread thread;

    public MyRockerView(Context context, AttributeSet attrs) {
//        super(context)
        super(context, attrs);
        // TODO Auto-generated constructor stub

        mBmpRockerBg = BitmapFactory.decodeResource(context.getResources(), R.drawable.rocker_bg);
        mBmpRockerBtn = BitmapFactory.decodeResource(context.getResources(), R.drawable.rocker_btn);

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {


            @Override
            public boolean onPreDraw() {
                // TODO Auto-generated method stub
                getViewTreeObserver().removeOnPreDrawListener(this);

                Log.e("RockerView", getWidth() + "/" +  getHeight());
                mCenterPoint = new PointF(getWidth() / 2, getHeight() / 2);
                mRockerBg_X = mCenterPoint.x;
                mRockerBg_Y = mCenterPoint.y;

                mRockerBtn_X = mCenterPoint.x;
                mRockerBtn_Y = mCenterPoint.y;

                float tmp_f = mBmpRockerBg.getWidth() / (float)(mBmpRockerBg.getWidth() + mBmpRockerBtn.getWidth());
                mRockerBg_R = tmp_f * getWidth() / 2;
                mRockerBtn_R = (1.0f - tmp_f)* getWidth() / 2;

                return true;
            }
        });


        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while(!Thread.currentThread().isInterrupted()){

                    //ϵͳ����onDraw����ˢ�»���


                    try {
                        MyRockerView.this.postInvalidate();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.drawBitmap(mBmpRockerBg, null,
                new Rect((int)(mRockerBg_X - mRockerBg_R),
                        (int)(mRockerBg_Y - mRockerBg_R),
                        (int)(mRockerBg_X + mRockerBg_R),
                        (int)(mRockerBg_Y + mRockerBg_R)),
                null);
        canvas.drawBitmap(mBmpRockerBtn, null,
                new Rect((int)(mRockerBtn_X - mRockerBtn_R),
                        (int)(mRockerBtn_Y - mRockerBtn_R),
                        (int)(mRockerBtn_X + mRockerBtn_R),
                        (int)(mRockerBtn_Y + mRockerBtn_R)),
                null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {

            if (Math.sqrt(Math.pow((mRockerBg_X - (int) event.getX()), 2) + Math.pow((mRockerBg_Y - (int) event.getY()), 2)) >= mRockerBg_R) {

                double tempRad = getRad(mRockerBg_X, mRockerBg_Y, event.getX(), event.getY());

                getXY(mRockerBg_X, mRockerBg_Y, mRockerBg_R, tempRad);
            } else {
                mRockerBtn_X = (int) event.getX();
                mRockerBtn_Y = (int) event.getY();
            }
            if(mRockerChangeListener != null) {
                mRockerChangeListener.report(mRockerBtn_X - mCenterPoint.x, mRockerBtn_Y - mCenterPoint.y);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            mRockerBtn_X = mCenterPoint.x;
            mRockerBtn_Y = mCenterPoint.y;
            if(mRockerChangeListener != null) {
                mRockerChangeListener.report(0, 0);
            }
        }
        return true;
    }

    public double getRad(float px1, float py1, float px2, float py2) {

        float x = px2 - px1;

        float y = py1 - py2;

        float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        float cosAngle = x / xie;

        float rad = (float) Math.acos(cosAngle);

        if (py2 < py1) {
            rad = -rad;
        }
        return rad;
    }

    /**
     *
     * @param R
     * @param centerX
     * @param centerY
     * @param rad
     */
    public void getXY(float centerX, float centerY, float R, double rad) {

        mRockerBtn_X = (float) (R * Math.cos(rad)) + centerX;

        mRockerBtn_Y = (float) (R * Math.sin(rad)) + centerY;
    }

    RockerChangeListener mRockerChangeListener = null;
    public void setRockerChangeListener(RockerChangeListener rockerChangeListener) {
        mRockerChangeListener = rockerChangeListener;
    }
    public interface RockerChangeListener {
        public void report(float x, float y);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        thread.interrupt();
    }
}
