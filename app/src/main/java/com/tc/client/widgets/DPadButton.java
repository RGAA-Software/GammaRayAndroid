package com.tc.client.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.tc.client.R;

public class DPadButton extends ImageView {

    private String mText = "";
    private Paint mPaint;
    private int mCurrentEvent;
    private boolean mPressed;
    private int mBackgroundColor;
    private int mBorderColor;

    public DPadButton(Context context) {
        this(context, null);
    }

    public DPadButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DPadButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DpadButton);
        mBackgroundColor = ((ColorDrawable)typedArray.getDrawable(R.styleable.DpadButton_circleColor)).getColor();
        Drawable borderDrawable = typedArray.getDrawable(R.styleable.DpadButton_borderColor);
        if (borderDrawable == null) {
            mBorderColor = 0x999999;
        } else {
            mBorderColor = ((ColorDrawable)borderDrawable).getColor();
        }
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
        int borderWidthDp = 2;
        int borderWidthPixel = (int) (Resources.getSystem().getDisplayMetrics().density * borderWidthDp);
        if (isButtonPressed()) {
            mPaint.setColor(mBackgroundColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
        } else {
            mPaint.setStrokeWidth(borderWidthPixel);
            mPaint.setColor(mBorderColor);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - borderWidthPixel, mPaint);
        }

        mPaint.setStyle(Paint.Style.FILL);
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
