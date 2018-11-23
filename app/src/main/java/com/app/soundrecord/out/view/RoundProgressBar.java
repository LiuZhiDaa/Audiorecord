package com.app.soundrecord.out.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.app.soundrecord.R;


/**
 * Project name flightProject
 * Created by gongguan on 2018/8/10.
 */

public class RoundProgressBar extends View {
    private int mWidth, mHeight;
    private Paint mPaint;
    private float mStrokeWidth = 20;
    private int mPaintColor = Color.WHITE;//前景圆圈的颜色
    private float mSweepAngle = 90;
    private float mPadding = 50;
    private Bitmap mSanjiao;

    public RoundProgressBar(Context context) {
        super(context);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mPaintColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mSanjiao = BitmapFactory.decodeResource(getResources(), R.drawable.ui_wifiyouhua_sanjiao);
    }

    public void setStrokeWidth(float width) {
        mStrokeWidth = width;
    }

    public void setColor(int color) {
        mPaintColor = color;
    }

    public void setProgress(float sweepAngle) {
        this.mSweepAngle = sweepAngle;
        postInvalidate();
    }

    public void setPadding(float padding) {
        mPadding = padding;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.rotate(mSweepAngle, mWidth / 2, mHeight / 2);
        mPaint.setColor(Color.WHITE);
        canvas.drawBitmap(mSanjiao, mWidth / 2 - mSanjiao.getWidth() / 2, mPadding - mStrokeWidth / 2, mPaint);
    }
}
