package com.tc.client.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.tc.client.R;

public class FunctionButton extends ImageView {

    private String mText = "";
    private Paint mPaint;
    private int mCurrentEvent;
    private boolean mPressed;
    private int mBackgroundColor;

    public FunctionButton(Context context) {
        this(context, null);
    }

    public FunctionButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunctionButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DpadButton);
        mBackgroundColor = ((ColorDrawable)typedArray.getDrawable(R.styleable.DpadButton_circleColor)).getColor();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(Resources.getSystem().getDisplayMetrics().density * 25);
        setOnClickListener(v -> {

        });
    }

    public void setText(String text) {
        mText = text;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setTextAlign(Paint.Align.CENTER);

        mPaint.setColor(mBackgroundColor);
        if (isButtonPressed()) {
            mPaint.setAlpha(255);
            canvas.drawRoundRect(0, 0, getWidth(), getHeight(), getHeight()/2, getHeight()/2, mPaint);
        }
        mPaint.setAlpha(128);
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), getHeight()/2, getHeight()/2, mPaint);

        mPaint.setColor(Color.WHITE);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((mPaint.descent() + mPaint.ascent()) / 2)) ;

        canvas.drawText(mText, xPos, yPos, mPaint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:{
                mPressed = true;
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPressed = false;
                invalidate();
                break;
        }
        return true;//super.onTouchEvent(event);
    }

    public boolean isButtonPressed() {
        return mPressed;
    }
}
