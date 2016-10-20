package com.htsc.customedprogressbar;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.htsc.customedprogressbar.view.CircleProgressBar;
import com.htsc.customedprogressbar.view.HorizontalProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
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
        findViewById(R.id.start_button_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(2);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
