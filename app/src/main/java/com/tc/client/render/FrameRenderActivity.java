package com.tc.client.render;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tc.client.App;
import com.tc.client.AppContext;
import com.tc.client.ControlLayer;
import com.tc.client.R;
import com.tc.client.Settings;
import com.tc.client.impl.ThunderApp;
import com.tc.client.util.ViewUtil;

public class FrameRenderActivity extends Activity {

    private static final String TAG = "Main";
    private FrameRenderView mFrameRenderView;
    private Thread mTickThread;
    private boolean mExitTickThread;
    private Handler mHandler;
    private ControlLayer mControlLayer;
    private ThunderApp mThunderApp;
    private String mIp;
    private int mPort;
    private String mStreamId;
    private String mRemoteDeviceId;
    private AppContext appContext;
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.activity_frame_render);
        mIp = getIntent().getStringExtra("ip");
        mPort = getIntent().getIntExtra("port", 20371);
        mStreamId = getIntent().getStringExtra("streamId");
        mRemoteDeviceId = getIntent().getStringExtra("remoteDeviceId");

        appContext = ((App)getApplication()).getAppContext();

        mThunderApp = new ThunderApp(mIp, mPort, true, true, true, mStreamId, mRemoteDeviceId);

        mFrameRenderView = findViewById(R.id.id_frame_render_view);
        mFrameRenderView.init(mThunderApp);
        mFrameRenderView.onCreate();

        mControlLayer = findViewById(R.id.id_control_layer);
        mControlLayer.setVisibility(Settings.Companion.getInstance().getShowVirtualGamepad() ? View.VISIBLE : View.GONE);
        mControlLayer.setThunderApp(mThunderApp);

        mHandler = new Handler(getMainLooper());

        mTickThread = new Thread(() -> {
            while(!mExitTickThread) {
                 mHandler.post(() -> {
                     if (Settings.Companion.getInstance().getShowVirtualGamepad()) {
                         mControlLayer.onEventTick();
                     } else {
                         mFrameRenderView.onEventTick();
                     }
                 });
                SystemClock.sleep(10);
            }
        });
        mTickThread.start();

        mThunderApp.registerFrameChangedCallback(((width, height) -> {
            runOnUiThread(() -> {
                resizeFrameView(width, height);
            });
        }));

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "GammaRay:Render");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFrameRenderView.onResume();
        if (mWakeLock != null) {
            mWakeLock.acquire(120 * 60 * 1000L);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFrameRenderView.onPause();
        if (mWakeLock != null) {
            mWakeLock.release();
        }
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

    private void resizeFrameView(int width, int height) {
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        if (ViewUtil.checkDeviceHasNavigationBar(this)) {
            screenWidth += ViewUtil.getNavigationBarHeight(this);
        }

        float xScale = screenHeight*1.0f / height;
        float yScale = screenWidth*1.0f / width;
        float scale = Math.min(xScale, yScale);
        int targetWidth = (int) (scale * width);
        int offsetX = (int) ((screenWidth - targetWidth)/2);
        mFrameRenderView.getLayoutParams().width = (int) targetWidth;
        mFrameRenderView.getLayoutParams().height = screenHeight;
        mFrameRenderView.layout(offsetX, 0, (int) (offsetX+targetWidth), screenHeight);
        mFrameRenderView.postInvalidate();
        Log.i(TAG, "After resize view, width: " + targetWidth + ", height: " + screenHeight);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        return super.dispatchGenericMotionEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }
}