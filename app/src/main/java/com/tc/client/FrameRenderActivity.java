package com.tc.client;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tc.client.impl.ThunderApp;

public class FrameRenderActivity extends Activity {
    static {
        System.loadLibrary("client");
    }
    private FrameRenderView mFrameRenderView;
    private Thread mTickThread;
    private boolean mExitTickThread;
    private Handler mHandler;
    private ControlLayer mControlLayer;
    private ThunderApp mThunderApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.activity_frame_render);

        mThunderApp = new ThunderApp();

        mFrameRenderView = findViewById(R.id.id_frame_render_view);
        mFrameRenderView.init(mThunderApp);
        mFrameRenderView.onCreate();

        mControlLayer = findViewById(R.id.id_control_layer);
        mControlLayer.setThunderApp(mThunderApp);

        mHandler = new Handler(getMainLooper());

        mTickThread = new Thread(() -> {
            while(!mExitTickThread) {
                 mHandler.post(() -> {
                     mControlLayer.onEventTick();
                     mFrameRenderView.onEventTick();
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