package com.tc.client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import com.tc.client.impl.ThunderSdk;

public class FrameRenderActivity extends Activity {

    private FrameRenderView mFrameRenderView;
    private Thread mTickThread;
    private boolean mExitTickThread;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_render);

        mFrameRenderView = findViewById(R.id.id_frame_render_view);
        mFrameRenderView.onCreate();

        mHandler = new Handler(getMainLooper());

        mTickThread = new Thread(() -> {
            while(!mExitTickThread) {
                mHandler.postAtFrontOfQueue(() -> {
                    //mFrameRenderView.onRenderTick();
                });
                SystemClock.sleep(17);
            }
        });
        mTickThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFrameRenderView.onResume();
    }

    @Override
    protected void onPause() {
        //mFrameRenderView.setVisibility(View.GONE);
        super.onPause();
        mFrameRenderView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExitTickThread = true;
        try {
            if (mTickThread != null) {
                mTickThread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        mFrameRenderView.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && mFrameRenderView.getVisibility() == View.GONE) {
            //mFrameRenderView.setVisibility(View.VISIBLE);
        }
    }
}