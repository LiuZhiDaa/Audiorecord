package com.app.soundrecord.out.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 圆形进度view  通过xfermode图片混合方式实现
 * Created by WangYu on 2018/5/31.
 */
public class ScanView extends View {
    private Context mContext;
    private Paint mPaint;//画两层图的画笔
    private int mHeight, mWidth;
    private PorterDuffXfermode xfermode;
    private Bitmap mDstBitmap, mSnowBitmap, mScanLineBitmap, mScanLineBitmapUp;//上面的遮罩层
    private float mProgress;//进度
    private Paint mSrcPaint;//画上层图的画笔
    private Drawable mDstDrawable, mSnowDrawable,
            mScanLineDrawable, mScanLineDrawableUp;//原始图片
    private int mDstBgColor = Color.TRANSPARENT;
    private int mDuration = 10000;//动画时间
    private ValueAnimator mAnimator;
    private ProgressViewListener mListener;
    private Paint mCirclePaint;
    private Bitmap mBitmap;
    private boolean isFirst = true;

    public ScanView(Context context) {
        super(context);
    }

    public ScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public ScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mSrcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSrcPaint.setColor(Color.WHITE);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(0x33ffffff);
        mCirclePaint.setStyle(Paint.Style.FILL);
        initAnimator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mWidth <= 0 || mHeight <= 0) {
            return;
        }
        mScanLineBitmap = getBitmap(mScanLineDrawable, mWidth, mScanLineDrawable.getIntrinsicHeight());
        mScanLineBitmapUp = getBitmap(mScanLineDrawableUp, mWidth, mScanLineDrawableUp.getIntrinsicHeight());
        mSnowBitmap = getBitmap(mSnowDrawable);
        mDstBitmap = getBitmap(mDstDrawable, mDstBgColor);
    }

    public void setColor(int color) {
        mSrcPaint.setColor(color);
    }

    public void setSnowDrawable(int drawable) {
        mSnowDrawable = mContext.getResources().getDrawable(drawable);
    }

    public void setDstDrawable(int drawable) {
        mDstDrawable = mContext.getResources().getDrawable(drawable);
    }

    public void setDstBgColor(int color) {
        mDstBgColor = color;
    }

    public void setScanLineDrawable(int drawable, int drawableUp) {
        mScanLineDrawable = mContext.getResources().getDrawable(drawable);
        mScanLineDrawableUp = mContext.getResources().getDrawable(drawableUp);
        mBitmap = mScanLineBitmapUp;
    }

    public void setDuration(int duration) {
        mDuration = duration;
        initAnimator();
    }

    public void setListener(ProgressViewListener listener) {
        this.mListener = listener;
    }

    public void start() {
        mAnimator.start();
    }

    public void stop() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    private void initAnimator() {
        mAnimator = ValueAnimator.ofFloat(0, 1f);
        mAnimator.setDuration(mDuration);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                if (!isFirst) {
                    isFirst = true;
                    mBitmap = mScanLineBitmapUp;
                } else {
                    isFirst = false;
                    mBitmap = mScanLineBitmap;
                }
            }
        });
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
    }


    private Bitmap getBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(c);
        return bitmap;
    }

    private Bitmap getBitmap(Drawable drawable, int w, int h) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(c);
        return bitmap;
    }

    private Bitmap getBitmap(Drawable drawable, int color) {
        if (drawable == null) {
            return null;
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        RectF rectF = new RectF(0, 0, w, h * 2);
        c.drawRoundRect(rectF, 10, 10, paint);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(c);
        return bitmap;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float dividerHeight = mScanLineBitmapUp.getHeight();
        float lineH = (dividerHeight + mHeight) * mProgress - dividerHeight;

        canvas.saveLayer(0, 0, mWidth, mHeight, null);
        drawBitmapInCenter(canvas, mSnowBitmap);
        mPaint.setXfermode(xfermode);
        canvas.drawRect(0, 0, mWidth, lineH + (isFirst ? dividerHeight : 0), mPaint);
        mPaint.setXfermode(null);
        canvas.restore();
        canvas.saveLayer(0, 0, mWidth, mHeight, null);
        drawBitmapInCenter(canvas, mDstBitmap);
        mPaint.setXfermode(xfermode);
        canvas.drawRect(0, lineH + (isFirst ? dividerHeight : 0), mWidth, mHeight, mPaint);
        mPaint.setXfermode(null);
        canvas.restore();

        if (mBitmap == null) {
            canvas.drawBitmap(mScanLineBitmapUp, 0, lineH, mPaint);
            return;
        }
        canvas.drawBitmap(mBitmap, 0, lineH, mPaint);
    }

    private void drawBitmapInCenter(Canvas canvas, Bitmap bitmap) {
        if (bitmap != null) {
            float snowTop = (mHeight - bitmap.getHeight()) / 2;
            float snowLeft = (mWidth - bitmap.getWidth()) / 2;
            canvas.drawBitmap(bitmap, snowLeft, snowTop, mPaint);
        }
    }

    public interface ProgressViewListener {
        void onProgressChange(float degree);

        void onAnimationEnd();
    }
}
