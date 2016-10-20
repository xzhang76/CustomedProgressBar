package com.htsc.customedprogressbar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.htsc.customedprogressbar.R;

/**
 * 圆形进度条
 * Created by zhangxiaoting on 2016/10/20.
 */
public class CircleProgress extends View {
    private Paint mPaint;
    private int mUnreachColor;
    private int mReachColor;
    private int mTextColor;
    private float mTextSize;
    private float mUnreachHeight;
    private float mReachHeight;
    private int max = 100;
    private int mProgress = 50;
    private boolean textIsDisplayable;
    private String progressStr;
    private int mCenter; // 圆心
    private float mRadius;

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        // 获取自定义属性和默认值
        mUnreachHeight = mTypedArray.getDimension(R.styleable.CircleProgress_progress_unreach_height, 1);
        mUnreachColor = mTypedArray.getColor(R.styleable.CircleProgress_progress_unreach_color, Color.RED);
        mReachHeight = mTypedArray.getDimension(R.styleable.CircleProgress_progress_reach_height, 3);
        mReachColor = mTypedArray.getColor(R.styleable.CircleProgress_progress_reach_color, Color.GREEN);
        mTextColor = mTypedArray.getColor(R.styleable.CircleProgress_progress_text_color, Color.GREEN);
        mTextSize = mTypedArray.getDimension(R.styleable.CircleProgress_progress_text_size, 15);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.CircleProgress_text_is_show, true);
        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
        if (textIsDisplayable) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mTextColor);
            mPaint.setTextSize(mTextSize);
            mPaint.setStrokeWidth(0);
            String text = mProgress + "%";
            int textWidth = (int) mPaint.measureText(text);
            canvas.drawText(mProgress + "%", mCenter - textWidth / 2, mCenter + textWidth / 6, mPaint);
        }
    }

    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return mProgress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(String progress) {
        int mProgress = (int) Float.parseFloat(progress);
        if (mProgress < 0) {
            this.mProgress = 0;
        }
        else if (mProgress > max) {
            this.mProgress = max;
        }
        postInvalidate();

    }

    public int getCricleColor() {
        return mUnreachColor;
    }

    public void setCricleColor(int cricleColor) {
        this.mUnreachColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return mReachColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.mReachColor = cricleProgressColor;
    }

    public int getmTextColor() {
        return mTextColor;
    }

    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
    }

    public float getmUnreachHeight() {
        return mUnreachHeight;
    }

    public void setmUnreachHeight(float mUnreachHeight) {
        this.mUnreachHeight = mUnreachHeight;
    }

}

