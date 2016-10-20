package com.htsc.customedprogressbar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.htsc.customedprogressbar.R;

/**
 * Created by zhangxiaoting on 2016/10/13.
 * 自定义的水平进度条
 */
public class HorizontalProgressBar extends ProgressBar {
    /**
     * 几个默认的属性值
     */
    private static final int DEFAULT_TEXT_SIZE = 10; // sp
    private static final int DEFAULT_TEXT_COLOR = 0xFC00D1;
    private static final int DEFAULT_UNREACH_COLOR = 0x4f5994;
    private static final int DEFAULT_UNREACH_HEIGHT = 2; // dp
    private static final int DEFAULT_REACH_COLOR = 0xFC00D1;
    private static final int DEFAULT_REACH_HEIGHT = 2; // dp
    private static final int DEFAULT_TEXT_OFFSET = 10; // dp
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mUnreachColor = DEFAULT_UNREACH_COLOR;
    protected int mUnreachHeight = dp2px(DEFAULT_UNREACH_HEIGHT);
    protected int mReachColor = DEFAULT_REACH_COLOR;
    protected int mReachHeight = dp2px(DEFAULT_REACH_HEIGHT);
    private int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);

    protected Paint mPaint = new Paint();
    private int mRealWidth; // 控件宽度 在onMeasure()中确定 在onDraw()中使用
    protected int mProgress;

    public HorizontalProgressBar(Context context) {
        this(context, null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar);
        mTextSize = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_progress_text_size, mTextSize);
        mTextColor = typedArray.getColor(R.styleable.HorizontalProgressBar_progress_text_color, mTextColor);
        mUnreachColor = typedArray.getColor(R.styleable.HorizontalProgressBar_progress_unreach_color, mUnreachColor);
        mUnreachHeight = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_progress_unreach_height, mUnreachHeight);
        mReachColor = typedArray.getColor(R.styleable.HorizontalProgressBar_progress_reach_color, mReachColor);
        mReachHeight = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_progress_reach_height, mReachHeight);
        mTextOffset = (int) typedArray.getDimension(R.styleable.HorizontalProgressBar_progress_text_offset, mTextOffset);
        typedArray.recycle();
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2); // 从paddingLeft开始绘制
        boolean noNeedUnreach = false; // 当reachWidth+textWidth+offset/2 >= view的宽度时，不需要绘制unreach部分

        // draw reached part
        String text = mProgress + "%";
        int textWidth = (int) mPaint.measureText(text); // 字体宽度
        float radio = mProgress * 1.0f / getMax();
        float progressX = radio * mRealWidth;
        float reachX = progressX - mTextOffset / 2 - textWidth / 2;

        if ((progressX + textWidth / 2) >= mRealWidth) {
            progressX = mRealWidth - textWidth /2;
            reachX = mRealWidth - textWidth - mTextOffset / 2;
            noNeedUnreach = true;
        }
        if (reachX > 0) {
            // 开始绘制
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, reachX, 0, mPaint);
        } else {
            progressX = textWidth / 2;
        }

        // draw text
        mPaint.setColor(mTextColor);
        int textY = (int) (-(mPaint.descent() + mPaint.ascent()) / 2);
        canvas.drawText(text, progressX - textWidth / 2, textY, mPaint);

        // draw unreached part
        if (!noNeedUnreach) {
            mPaint.setColor(mUnreachColor);
            mPaint.setStrokeWidth(mUnreachHeight);
            canvas.drawLine(progressX + textWidth / 2 + mTextOffset / 2, 0, mRealWidth, 0, mPaint);
        }
        canvas.restore();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 对于宽度来说，因为必须由用户指定，要么是固定值或match_parent
         */
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(widthVal, height);
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 下面就是三种模式的判断
        if (heightMode == MeasureSpec.EXACTLY) {
            result = heightSize;
        } else {
            // 可能是MeasureSpec.AT_MOST, 也可能是MeasureSpec.UNSPECIFIED
            /**
             * 如果是UNSPECIFIED 高度值应该是左边进度条高度，右边进度条高度，文字高度三者最大值
             * 加上上下的padding
             */
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            result = getPaddingTop() + getPaddingBottom() + Math.max(Math.max(mReachHeight, mUnreachHeight), Math.abs(textHeight));

            if (heightMode == MeasureSpec.AT_MOST) {
                /**
                 * 如果是AT_MOST 高度值应该是进度条本身高度和父布局测量的高度值两者的较小值
                 */
                result = Math.min(result, heightSize);
            }
        }
        return result;
    }

    @Override
    public synchronized void setProgress(int progress) {
        if (progress > getMax()) {
            progress = getMax();
        }
        if (progress <= getMax()) {
            this.mProgress = progress;
            postInvalidate();
        }
    }

    /**
     * 两个工具方法
     */
    public int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    public int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
    }
}
