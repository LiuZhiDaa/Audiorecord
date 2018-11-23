package com.app.soundrecord.out.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.app.soundrecord.R;


/**
 * Created by wanghailong on 2018/7/2.
 * 电池充电view
 */

public class BatteryImageView extends android.support.v7.widget.AppCompatImageView {

    private int mPercent = -1;//当前充电电量比例
    private Paint mBatteryBodyPaint;//电池电量比例
    private Paint mOutCirclePaint;//外圆
    private Paint mBackgroundCirclePaint;//外圆
    private Paint mRenderPaint;//扩散圆
    private RectF mBatteryOuter;//电池外边框
    private RectF mBatteryTop;//电池最上面
    private int mWidth;//控件宽度
    private int mHeight;//控件高度
    public static final int OUT_STROKE_WIDTH = 3;//外圆宽度
    public static final int ROUNDX = 22;//圆度
    public static final int ROUNDY = 22;//圆度
    private int mSize;
    private int mRadius;
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;
//    private RectF mRectBatteryCharging;


    public BatteryImageView(Context context) {
        super(context);
        init();
    }

    public BatteryImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BatteryImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBatteryBodyPaint = new Paint();
        mBatteryBodyPaint.setColor(ContextCompat.getColor(getContext(), R.color.white1));
        mBatteryBodyPaint.setAntiAlias(true);
        mBatteryBodyPaint.setDither(true);

        mOutCirclePaint = new Paint();
        mOutCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.white1));
        mOutCirclePaint.setStyle(Paint.Style.STROKE);
        mOutCirclePaint.setStrokeWidth(OUT_STROKE_WIDTH);
        mOutCirclePaint.setAntiAlias(true);
        mOutCirclePaint.setDither(true);


        mBackgroundCirclePaint = new Paint();
        mBackgroundCirclePaint.setColor(0x20ffffff);
        mBackgroundCirclePaint.setStyle(Paint.Style.FILL);
        mBackgroundCirclePaint.setAntiAlias(true);
        mBackgroundCirclePaint.setDither(true);

        mRenderPaint = new Paint();
        mRenderPaint.setColor(0x40ffffff);
        mRenderPaint.setStyle(Paint.Style.FILL);
        mRenderPaint.setAntiAlias(true);
        mRenderPaint.setDither(true);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mWidth = resolveSize(mWidth, widthMeasureSpec);
        mHeight = resolveSize(mHeight, heightMeasureSpec);
        mSize = Math.min(mWidth, mHeight);
        mRadius = mSize / 2 - OUT_STROKE_WIDTH;
        setMeasuredDimension(mSize, mSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBatteryOuter==null) {
            mBatteryOuter = new RectF(mRadius - mRadius / 5 + OUT_STROKE_WIDTH * 2, mRadius - mRadius / 4, mRadius + mRadius / 5, mRadius + mRadius / 4);
        }
        if (mBatteryTop==null) {
            mBatteryTop = new RectF(mBatteryOuter.left + mBatteryOuter.width() / 4 + OUT_STROKE_WIDTH * 2, mBatteryOuter.top - mBatteryOuter.height() / 10, mBatteryOuter.left + mBatteryOuter.width() * 3 / 4 - OUT_STROKE_WIDTH * 2, mBatteryOuter.top);
        }

        if (mPaintFlagsDrawFilter==null) {
            mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        }

        canvas.setDrawFilter(mPaintFlagsDrawFilter);
//        canvas.drawCircle(mRadius, mRadius, mRadius - OUT_STROKE_WIDTH, mOutCirclePaint);
        canvas.drawCircle(mRadius, mRadius, mRadius - OUT_STROKE_WIDTH * 2, mBackgroundCirclePaint);

        canvas.drawCircle(mRadius, mRadius, mRadius / 3 * 2, mRenderPaint);

        mOutCirclePaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(mBatteryOuter, ROUNDX, ROUNDY, mOutCirclePaint);
        mOutCirclePaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(mBatteryTop, ROUNDX, ROUNDY, mOutCirclePaint);


        if (mPercent < 0) return;
        if (mPercent > 100) {
            mPercent = 100;
        }
        /***绘制充电电量***/
        int maxHeight = (int) mBatteryOuter.height();
        drawBatteryBody(canvas, maxHeight);

        super.onDraw(canvas);
    }

    /****绘制充电电量*****/
    private void drawBatteryBody(Canvas canvas, int maxHeight) {
        int percentHeight = (int) (maxHeight * (1 - mPercent / 100f));

        float left = mBatteryOuter.left + OUT_STROKE_WIDTH * 2;
        float top=mBatteryOuter.top + percentHeight+ OUT_STROKE_WIDTH * 2;
        float right=mBatteryOuter.right-OUT_STROKE_WIDTH * 2;
        float bottom = mBatteryOuter.bottom -OUT_STROKE_WIDTH * 2;
//        mRectBatteryCharging.inset(OUT_STROKE_WIDTH * 2, OUT_STROKE_WIDTH * 2);
        canvas.drawRoundRect(left,top,right,bottom, 16, 16, mBatteryBodyPaint);
    }

    public int getUpPercent() {
        return mPercent;
    }

    public void setBatteryPercent(int percent) {
        this.mPercent = percent;
        invalidate();
    }


}

