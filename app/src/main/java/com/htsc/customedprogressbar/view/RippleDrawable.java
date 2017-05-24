package com.htsc.customedprogressbar.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 水波纹drawable
 */
public class RippleDrawable extends Drawable {

    private static final int INIT_ALPHA = 200;

    private int mAlpha = INIT_ALPHA; // 透明度 0~255
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
    private float mEnterProgress = 0;
    /**
     * 360是指希望360ms停止 16对应16ms 每次变化16的进度
     */
    private static final float INCREMENT = 16f / 500;
    private int mMaxRadius = 0;
    // 插值器
    private Interpolator mEnterInterpolator = new DecelerateInterpolator(1.5f);
    // 按钮的背景色
    private int mBackgroundColor;

    // 退出动画
    private float mExitProgress = 0;
    private static final float EXIT_INCREMENT = 16f / 500;
    private Interpolator mExitInterpolator = new AccelerateInterpolator(3f);
    /**
     * 两个flag 分别表示进入动画已退出和手指已抬起
     */
    private boolean mEnterDrawableDoneFlag;
    private boolean mTouchReleaseFlag;

    private Runnable mEnterRunnable = new Runnable() {
        @Override
        public void run() {
            mEnterProgress = mEnterProgress + INCREMENT;
            if (mEnterProgress > 1) {
                Log.d("zxt", "enter runnable done");
                // 开启退出动画
                mEnterDrawableDoneFlag = true;
                onEnterProgressChanged(1);
                /**
                 * 如果点一下很快松开
                 * 这样touch up先调用, mTouchReleaseFlag = true;
                 * 就会直接直接退出动画
                 *
                 * 如果按住不动
                 * 这样进入动画先执行完, 这时mTouchReleaseFlag = false;
                 * 直接退出 当手指抬起时会调用开启退出动画
                 */
                if (mTouchReleaseFlag) {
                    startExitRunnable();
                }
                return;
            }
            float realProgress = mEnterInterpolator.getInterpolation(mEnterProgress);
            onEnterProgressChanged(realProgress);
            // 延迟16毫秒再重绘, 保证洁面刷新频率接近60fps
            scheduleSelf(mEnterRunnable, SystemClock.uptimeMillis() + 16);
        }
    };

    private Runnable mExitRunnable = new Runnable() {
        @Override
        public void run() {
            mExitProgress = mExitProgress + EXIT_INCREMENT;
            if (mExitProgress > 1) {
                Log.d("zxt", "exit runnable done");
                onExitProgressChanged(1);
                return;
            }
            float realProgress = mExitInterpolator.getInterpolation(mExitProgress);
            onExitProgressChanged(realProgress);
            // 延迟16毫秒再重绘, 保证洁面刷新频率接近60fps
            scheduleSelf(mExitRunnable, SystemClock.uptimeMillis() + 16);
        }
    };

    /**
     * 进入动画
     * <p/>
     * 进度变化时 需要重新确定涟漪效果的圆心和半径
     * 使圆心不断向控件中心靠拢 半径也随进度变大
     *
     * @param realProgress 进度值
     */
    private void onEnterProgressChanged(float realProgress) {
        mRadius = mMaxRadius * realProgress;
        mDrawableX = mDownCenterX + (mCenterX - mDownCenterX) * realProgress;
        mDrawableY = mDownCenterY + (mCenterY - mDownCenterY) * realProgress;
        // 第一个参数是初始值, 变深
        mBackgroundColor = changeColorAlpha(0x30000000, realProgress);
        invalidateSelf();
    }

    /**
     * 退出动画
     * <p/>
     * 进度变化时 realprogress [0, 1)
     * (1)背景色由深变淡
     * (2)涟漪的圆减淡
     *
     * @param realProgress 进度值
     */
    private void onExitProgressChanged(float realProgress) {
        // 第一个参数是初始值, 背景色减淡
        mBackgroundColor = changeColorAlpha(0x30000000, 1 - realProgress);
        // 涟漪的圆减淡
        mAlpha = (int) (INIT_ALPHA * (1 - realProgress));
        onColorOrAlphaChanged();
        invalidateSelf();
    }

    /**
     * 启动进入动画
     */
    private void startEnterRunnable() {
        mEnterProgress = 0;
        mRadius = 0;
        mAlpha = INIT_ALPHA;
        onColorOrAlphaChanged();
        mEnterDrawableDoneFlag = false;
        unscheduleSelf(mEnterRunnable);
        scheduleSelf(mEnterRunnable, SystemClock.uptimeMillis());
    }

    /**
     * 启动退出动画
     */
    private void startExitRunnable() {
        mExitProgress = 0;
        unscheduleSelf(mEnterRunnable);
        unscheduleSelf(mExitRunnable);
        scheduleSelf(mExitRunnable, SystemClock.uptimeMillis());
    }

    // 改变颜色的alpha值
    private int changeColorAlpha(int color, float progress) {
        int alpha = (color >> 24) & 0xFF;
        alpha = (int) (alpha * progress);
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
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
        canvas.drawColor(mBackgroundColor);
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
                mTouchReleaseFlag = false;
                mDownCenterX = event.getX();
                mDownCenterY = event.getY();
                startEnterRunnable();
                break;
            case MotionEvent.ACTION_UP:
                Log.d("zxt", "action up");
                /**
                 * 如果点一下很快松开
                 * 这样touch up先调用, 但mEnterDrawableDoneFlag = false;
                 * 就会直接返回 当进入动画执行完会开启退出动画
                 *
                 * 如果按住不动
                 * 这样进入动画先执行完, 这时mEnterDrawableDoneFlag = true;
                 * 就会直接调用开启退出动画
                 */
                mTouchReleaseFlag = true;
                if (mEnterDrawableDoneFlag) {
                    startExitRunnable();
                }
                break;
            default:
                break;

        }
    }
}
