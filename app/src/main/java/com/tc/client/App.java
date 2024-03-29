package com.tc.client;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Map;

public class App extends Application {
    private static final String TAG = "Main";
    @Override
    public void onCreate() {
        super.onCreate();

        Settings.Companion.getInstance().loadConfig(this);
        Settings.Companion.getInstance().dump();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.i(TAG, "onInitializationComplete");
                Map<String, AdapterStatus> status = initializationStatus.getAdapterStatusMap();
                for (String key: status.keySet()) {
                    Log.i(TAG, "==> Ad init status: " + key + ", " + status.get(key).getInitializationState().name());
                }
            }
        });
    }
}
