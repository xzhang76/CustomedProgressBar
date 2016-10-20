package com.htsc.customedprogressbar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * 圆形进度条
 * Created by zhangxiaoting on 2016/10/20.
 */
public class CircleProgressBar extends HorizontalProgressBar {
    private int mCenter; // 圆心
    private int mRadius; // 半径

    public CircleProgressBar(Context context) {
        super(context);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mReachHeight = (mUnreachHeight * 2);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        mCenter = getWidth() / 2;
        mRadius = mCenter - mReachHeight / 2;
        // 画最大的圆环
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mUnreachColor); // 设置圆环的颜色
        mPaint.setStrokeWidth(mUnreachHeight); // 设置圆环的宽度
        canvas.drawCircle(mCenter, mCenter, mRadius, mPaint); // 画出圆环

        // 画进度条
        mPaint.setStrokeWidth(mReachHeight); // 设置圆环的宽度
        mPaint.setColor(mReachColor); // 设置进度的颜色
        RectF oval = new RectF(mCenter - mRadius, mCenter - mRadius, mCenter + mRadius, mCenter + mRadius); // 用于定义的圆弧的形状和大小的界限
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(oval, -90, 360 * mProgress / getMax(), false, mPaint); // 根据进度画圆弧

        // 画中间进度条字体
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setStrokeWidth(0);
        String text = mProgress + "%";
        int textWidth = (int) mPaint.measureText(text);
        canvas.drawText(mProgress + "%", mCenter - textWidth / 2, mCenter + textWidth / 6, mPaint);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
    }
}
