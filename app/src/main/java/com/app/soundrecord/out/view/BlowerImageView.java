package com.app.soundrecord.out.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by WangYu on 2018/7/4.
 */
public class BlowerImageView extends android.support.v7.widget.AppCompatImageView {
    private Context mContext;
    private Paint mPaint;

    public BlowerImageView(Context context) {
        this(context, null);
    }

    public BlowerImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BlowerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(getWidth()/2f,getHeight()/2f,getHeight()/6.0f,mPaint);
    }
}
