package com.app.soundrecord.out.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by WangYu on 2018/7/2.
 */
public class RoundProgressView extends View {
    private int mWidth, mHeight;
    private Paint mPaint;
    private float mStrokeWidth = 10;
    private int mPaintColor = Color.WHITE;//前景圆圈的颜色
    private int mBgColor = 0x7fffffff;//背景圆圈的颜色
    private RectF mRectF;
    private float mSweepAngle = 0;
    private float mMoveCircleRadius = 5;//移动圆圈的半径
    private float mPadding=0;

    public RoundProgressView(Context context) {
        super(context);
    }

    public RoundProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mPaintColor);
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    public void setStrokeWidth(float width) {
        mStrokeWidth = width;
        mMoveCircleRadius = mStrokeWidth / 2;
    }

    public void setBgCircleColor(int color) {
        mBgColor = color;
    }

    public void setColor(int color) {
        mPaintColor = color;
    }

    public void setProgress(float sweepAngle) {
        this.mSweepAngle = sweepAngle;
        postInvalidate();
    }

    public void setPadding(float padding) {
        mPadding=padding;
    }

    public void setMoveCircleRadius(float radius) {
        mMoveCircleRadius = radius;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mRectF = new RectF(mStrokeWidth / 2+mPadding, mStrokeWidth / 2+mPadding, mWidth - mStrokeWidth / 2-mPadding, mHeight - mStrokeWidth / 2-mPadding);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景园
        mPaint.setStrokeWidth(mStrokeWidth / 2);
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - mStrokeWidth / 2, mPaint);
        //起点园
        mPaint.setColor(mPaintColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mWidth / 2, mStrokeWidth / 2+mPadding, mStrokeWidth / 2, mPaint);

        //弧
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(mRectF, -90, mSweepAngle, false, mPaint);

        if (mSweepAngle < 360) {

            //终点园
            mPaint.setStyle(Paint.Style.FILL);
            float radius = mWidth / 2 - mStrokeWidth / 2-mPadding;
            float x = (float) (mWidth / 2 + radius * Math.cos((mSweepAngle - 90) * Math.PI / 180));
            float y = (float) (mHeight / 2 + radius * Math.sin((mSweepAngle - 90) * Math.PI / 180));
            canvas.drawCircle(x, y, mMoveCircleRadius, mPaint);
        }


    }
}
