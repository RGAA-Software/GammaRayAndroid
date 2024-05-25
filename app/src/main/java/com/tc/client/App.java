package com.tc.client;

import android.app.Application;
import android.content.Context;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.tc.client.devices.UsbDeviceManager;

public class App extends Application {
    private static final String TAG = "Main";
    private AppContext appContext;
    public static SensorManager sm;
    private static App instance;
    //private UsbDeviceManager usbDeviceManager;

    private static int sDens = 0;

    public static App getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Settings.Companion.getInstance().loadConfig(this);
        Settings.Companion.getInstance().dump();

        instance = this;
        appContext = new AppContext(this);

        sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        sDens = dm.densityDpi;
        Log.d(TAG, "dens:" + sDens);

//        usbDeviceManager = new UsbDeviceManager(this);
//        usbDeviceManager.start();
    }

    public AppContext getAppContext() {
        return appContext;
    }

    public static int getDens(){
        return sDens;
    }
}
