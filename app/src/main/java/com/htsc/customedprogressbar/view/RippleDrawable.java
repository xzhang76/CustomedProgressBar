package com.htsc.customedprogressbar.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 水波纹drawable
 */
public class RippleDrawable extends Drawable {

    private int mAlpha = 200; // 透明度 0~255
    private int mRippleColor; // 整体颜色

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 画笔 去锯齿
    /**
     * 涟漪效果的圆心
     * 根据按下位置和控件中心位置 再结合进度来计算
     * 需要注意：
     * 控件中心位置一直不变 按下的位置也是按下之后就不会再变(除非再按)
     */
    private float mDrawableX, mDrawableY;
    private float mDownCenterX, mDownCenterY; // 按下时的点
    private float mCenterX, mCenterY; // 控件的中心位置
    private float mRadius = 0; //半径
    private float mProgress = 0;
    /**
     * 360是指希望360ms停止 16对应16ms 每次变化16的进度
     */
    private static final float INCREMENT = 16f/360;
    private int mMaxRadius = 0;
    // 插值器
    private Interpolator mEnterInterpolator = new DecelerateInterpolator(1.2f);

    private Runnable mEnterRunnable = new Runnable() {
        @Override
        public void run() {
            mProgress = mProgress + INCREMENT;
            if (mProgress > 1) {
                return;
            }
            float realProgress = mEnterInterpolator.getInterpolation(mProgress);
            onProgressChanged(realProgress);
            // 延迟16毫秒再重绘, 保证洁面刷新频率接近60fps
            scheduleSelf(mEnterRunnable, SystemClock.uptimeMillis() + 16);
        }
    };

    /**
     * 进度变化时 需要重新确定涟漪效果的圆心和半径
     * 使圆心不断向控件中心靠拢 半径也随进度变大
     * @param realProgress 进度值
     */
    private void onProgressChanged(float realProgress) {
        mRadius = mMaxRadius * realProgress;
        mDrawableX = mDownCenterX + (mCenterX - mDownCenterX) * realProgress;
        mDrawableY = mDownCenterY + (mCenterY - mDownCenterY) * realProgress;
        invalidateSelf();
    }

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
        canvas.drawCircle(mDrawableX, mDrawableY, mRadius, mPaint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        // 控件中心位置
        mCenterX = bounds.centerX();
        mCenterY = bounds.centerY();
        mMaxRadius = Math.max(bounds.width(), bounds.height()) / 2 + 5;
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
                mDownCenterX = event.getX();
                mDownCenterY = event.getY();
                startEnterRunnable();
                break;
        }
    }

    private void startEnterRunnable() {
        mProgress = 0;
        mRadius = 0;
        unscheduleSelf(mEnterRunnable);
        scheduleSelf(mEnterRunnable, SystemClock.uptimeMillis());
    }
}
