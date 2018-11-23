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
public class ImageProgressView extends View {
    private Context mContext;
    private Paint mPaint;//画两层图的画笔
    private int mWidth, mHeight;
    private PorterDuffXfermode xfermode;
    private Bitmap mDstBitmap;//底图
    private Bitmap mSrcBitmap;//上面的遮罩层
    private Canvas mSrcCanvas;
    private float mProgress;//进度
    private Paint mSrcPaint;//画上层图的画笔
    private RectF mRectF;
    private Drawable mOriginalDrawable;//原始图片
    private int mDuration = 10000;//动画时间
    private ValueAnimator mAnimator;
    private ProgressViewListener mListener;

    public ImageProgressView(Context context) {
        super(context);
    }

    public ImageProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public ImageProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mSrcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSrcPaint.setColor(Color.RED);
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
        mRectF = new RectF(0, 0, mWidth, mHeight);
        makeDst(mWidth, mHeight);
        makeSrc(mWidth, mHeight);
    }

    public void setColor(int color) {
        mSrcPaint.setColor(color);
    }

    public void setImageDrawable(int drawable) {
        mOriginalDrawable = mContext.getResources().getDrawable(drawable);
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

    private void initAnimator() {
        mAnimator = ValueAnimator.ofFloat(0, 360);
        mAnimator.setDuration(mDuration);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                if (mListener != null) {
                    mListener.onProgressChange(mProgress);
                }
                postInvalidate();
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onAnimationEnd();
                }
            }
        });
    }

    private void makeDst(int w, int h) {
        if (mOriginalDrawable == null || w <= 0 || h <= 0) {
            return;
        }
        mDstBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(this.mDstBitmap);
        mOriginalDrawable.setBounds(0, 0, w, h);
        mOriginalDrawable.draw(c);
    }

    private void makeSrc(int w, int h) {
        if (w <= 0 || h <= 0) {
            return;
        }
        if (mSrcBitmap == null) {
            mSrcBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mSrcCanvas = new Canvas(mSrcBitmap);
        }
        mSrcCanvas.drawArc(mRectF, 0, mProgress, true, mSrcPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDstBitmap==null||mSrcBitmap==null) {
            return;
        }
        canvas.saveLayer(0, 0, mWidth, mHeight, null);
        canvas.drawBitmap(mDstBitmap, 0, 0, mPaint);
        mPaint.setXfermode(xfermode);
        makeSrc(mWidth, mHeight);
        canvas.drawBitmap(mSrcBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.restore();
    }

    public interface ProgressViewListener {
        void onProgressChange(float degree);

        void onAnimationEnd();
    }
}
