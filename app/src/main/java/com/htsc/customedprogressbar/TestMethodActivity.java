package com.htsc.customedprogressbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.htsc.customedprogressbar.view.CircleProgressBar;
import com.htsc.customedprogressbar.view.HorizontalProgressBar;
import com.htsc.customedprogressbar.view.RippleDrawable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangxiaoting on 2017/3/21.
 */
public class TestMethodActivity extends MainActivity {
    private int mProgress = 0;
    private int mCircleProgress = 0;
    private HorizontalProgressBar horizontalProgressBar;
    private Timer timer;
    private CircleProgressBar mCircleProgressBar;

    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (mProgress > 100) {
                    timer.cancel();
                } else {
                    horizontalProgressBar.setProgress(mProgress);
                    horizontalProgressBar.invalidate();
                }
            } else if (msg.what == 2) {
                if (mCircleProgress <= 100) {
                    mCircleProgress++;
                    mCircleProgressBar.setProgress(mCircleProgress);
                    mHandler.sendEmptyMessageDelayed(2, 100);
                } else {
                    mHandler.removeMessages(2);
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        horizontalProgressBar = (HorizontalProgressBar) findViewById(R.id.horizontal_progress_bar);
        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerTask task = new TimerTask() {
                    public void run() {
                        mProgress++;
                        mHandler.sendEmptyMessage(1);
                    }
                };
                timer = new Timer(true);
                timer.schedule(task, 100, 100);
            }
        });
        mCircleProgressBar = (CircleProgressBar) findViewById(R.id.circle_progress_bar);
        findViewById(R.id.circle_progress_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(2);
            }
        });
        Button button = (Button) findViewById(R.id.button);
        final RippleDrawable drawable = new RippleDrawable();
        button.setBackgroundDrawable(drawable);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                drawable.onTouch(event);
                sendBroadcast(new Intent("test_method"));
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void testMethod() {
        super.testMethod();
        Toast.makeText(this, "ok, test", Toast.LENGTH_SHORT).show();
    }
}
