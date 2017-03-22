package com.htsc.customedprogressbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                testMethod();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("test_method");
        registerReceiver(receiver, intentFilter);
    }

    protected void testMethod() {

    }
}
