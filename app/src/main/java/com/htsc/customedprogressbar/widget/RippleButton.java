package com.htsc.customedprogressbar.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.htsc.customedprogressbar.view.RippleDrawable;

/**
 * Created by zhangxiaoting on 2017/3/16.
 */
public class RippleButton extends Button implements View.OnTouchListener{
    private RippleDrawable mRippleDrawable;

    public RippleButton(Context context) {
        this(context, null);
    }

    public RippleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRippleDrawable = new RippleDrawable();
        mRippleDrawable.setCallback(this);
        setOnTouchListener(this);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mRippleDrawable || super.verifyDrawable(who);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mRippleDrawable.onTouch(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRippleDrawable.draw(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRippleDrawable.setBounds(0, 0, getWidth(), getHeight());
    }
}
