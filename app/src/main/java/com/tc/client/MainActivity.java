package com.tc.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tc.client.impl.ThunderSdk;

public class MainActivity extends Activity {

    static {
        System.loadLibrary("client");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.id_start).setOnClickListener(v -> {

            startActivity(new Intent(this, FrameRenderActivity.class));

        });

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, FrameRenderActivity.class));
        }, 300);

    }

}
