package com.tc.client;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

public class DpadButtonGroup extends RelativeLayout {

    private Paint mPaint;

    public DpadButtonGroup(Context context) {
        this(context, null);
    }

    public DpadButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
    }

}
