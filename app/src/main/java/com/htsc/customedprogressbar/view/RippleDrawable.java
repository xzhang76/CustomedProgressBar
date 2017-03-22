package com.htsc.customedprogressbar.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

/**
 * 水波纹drawable
 */
public class RippleDrawable extends Drawable {

    private int mAlpha = 200; // 透明度 0~255
    private int mRippleColor; // 整体颜色

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 画笔 去锯齿
    private float mCenterX, mCenterY; //圆心坐标
    private float mRadius = 200; //半径

    public RippleDrawable() {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        setRippleColor(0x30000000);
    }

    private void setRippleColor(int color) {
        mRippleColor = color;
        onColorOrAlphaChanged();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        onColorOrAlphaChanged();
    }

    private void onColorOrAlphaChanged() {
        mPaint.setColor(mRippleColor);
        if (mAlpha != 255) {
            int alpha = mPaint.getAlpha();
            int realAlpha = (int) (alpha * (mAlpha / 255f));
            mPaint.setAlpha(realAlpha);
        }
    }

    // 滤镜 主要用来处理图片
    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (mPaint.getColorFilter() != colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        int alpha = mPaint.getAlpha();

        if (alpha == 255) {
            return PixelFormat.OPAQUE;
        } else if (alpha == 0) {
            return PixelFormat.TRANSPARENT;
        } else {
            return PixelFormat.TRANSLUCENT;
        }
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    public void onTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mCenterX = event.getX();
                mCenterY = event.getY();
                mRadius = mRadius + 40;
                invalidateSelf();
                break;
        }
    }
}
