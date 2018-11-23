package com.app.soundrecord.out.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 对勾动画
 * Created by wangyu on 2018/1/20.
 */

public class TickView extends View {
    private Context context;
    private Paint mPaint;
    private int width, height;
    private float mSweepAngle, successAniValue;
    //    private Path circlePath;
    private Path successPath;
    private PathMeasure pathMeasure;
    private Path mPath;//截取到的真正用来画图的path
    private Paint mCirclePaint;
    //配置参数
    private int mCircleAnimDuration = 10000;
    private int mFastCircleAnimDuration = 1500;
    private int mTickAnimDuration = 500;
    private float mCircleStrokeWidth = 10, mTickStrokeWidth = 10;
    private float mBigCirRatio = 1.0f;//外圈园是整个view高度的多少
    private int mColor = Color.WHITE;
    private boolean mIsWorking = false;
    private OnAnimEndListener mListener;
    private ValueAnimator mCircleAnimator;
    private ValueAnimator mTickAnimator;
    private RectF mCircleRectF;


    public TickView(Context context) {
        this(context, null);
    }

    public TickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initPara();
    }

    /**
     * 初始化一些参数
     */
    private void initPara() {
//        if (mTypedArray != null) {
//            mCircleAnimDuration = mTypedArray.getInt(R.styleable.TickView_duration, mCircleAnimDuration);
//            mCircleStrokeWidth = mTypedArray.getDimension(R.styleable.TickView_tickStrokeWith, mCircleStrokeWidth);
//            mColor = mTypedArray.getColor(R.styleable.TickView_tickColor, mColor);
//            mBigCirRatio = mTypedArray.getFloat(R.styleable.TickView_tickCirRatio, mBigCirRatio);
//        }

        //画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleStrokeWidth);
        //圆点画笔
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mColor);
        // 外圈圆取值
        mCircleAnimator = ValueAnimator.ofFloat(0, 360);
        mCircleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSweepAngle = (float) animation.getAnimatedValue();
//                Log.i("wangyu", "onAnimationUpdate: " + mSweepAngle);
                postInvalidate();
            }
        });
        mCircleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                Log.i("wangyu","触发end" );
                if (mListener != null) {
                    mListener.onCircleAnimEnd();
                }
            }
        });
        mCircleAnimator.setDuration(mCircleAnimDuration);
        mTickAnimator = ValueAnimator.ofFloat(0, 1);
        mTickAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                successAniValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mTickAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mListener != null) {
                    mListener.onTickAnimEnd();
                }
            }
        });
        mTickAnimator.setDuration(mTickAnimDuration);
//        circlePath = new Path();
        successPath = new Path();
        mPath = new Path();
        pathMeasure = new PathMeasure();

//        animatorSet = new AnimatorSet();
////        animatorSet.setDuration(mCircleAnimDuration);
//        animatorSet.play(circleAnimator).before(successAnimator);
//        animatorSet.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                if (mListener != null) {
//                    mListener.onAnimEnd();
//                }
//            }
//
//        });
//        animatorSet.start();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算圆的半径和区域
        float r = height * mBigCirRatio / 2;
        mCircleRectF = new RectF(width / 2 - r, width / 2 - r, width / 2 + r, width / 2 + r);
        //画出对勾path
        successPath.moveTo(width * (260f / 700f), width * (344f / 700f));
        successPath.lineTo(width * (350f / 700f), width * (435f / 700f));
        successPath.lineTo(width * (480f / 700f), width * (304f / 700f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mIsWorking) {
            return;
        }
        //构建圆形path
//        circlePath.addCircle(width / 2, height / 2, height * mBigCirRatio / 2, Path.Direction.CW);
//        pathMeasure.setPath(circlePath, false);
        //截取片段
//        pathMeasure.getSegment(0, mSweepAngle * pathMeasure.getLength(), mPath, true);
        //画圆圈
//        mPaint.setStrokeWidth(mCircleStrokeWidth);
//        canvas.drawArc(mCircleRectF, -90, mSweepAngle, false, mPaint);
        if (successAniValue > 0f) {
            mPaint.setStrokeWidth(mTickStrokeWidth);
            pathMeasure.setPath(successPath, false);
            //起始位置的小圆点
            canvas.drawCircle(width * (260f / 700f), width * (344f / 700f), mPaint.getStrokeWidth() / 2, mCirclePaint);
            //对勾
            pathMeasure.getSegment(0, successAniValue * pathMeasure.getLength(), mPath, true);
            //跟随画笔的小圆点
            float x = 0, y = 0, tx = 0, ty = 0;
            float[] pos = {x, y};
            float[] tan = {tx, ty};
            pathMeasure.getPosTan(successAniValue * pathMeasure.getLength(), pos, tan);
            canvas.drawCircle(pos[0], pos[1], mPaint.getStrokeWidth() / 2, mCirclePaint);
            canvas.drawPath(mPath, mPaint);
        }

    }

    public void startCircleAnim() {
        mIsWorking = true;
        mCircleAnimator.start();
    }

    public void startFastCircleAnim() {
        mIsWorking = true;
        mCircleAnimator.pause();//停止慢速动画
        ValueAnimator fastAnimator = ValueAnimator.ofFloat(mSweepAngle, 360);
        fastAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSweepAngle = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        fastAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onCircleAnimEnd();
                }
            }
        });
        fastAnimator.setDuration(mFastCircleAnimDuration);
        fastAnimator.start();
    }

    public void startTickAnim() {
        mIsWorking = true;
        mTickAnimator.start();
    }

    public void setComplete() {
        mSweepAngle=360;
        successAniValue=1;
        mIsWorking=true;
        postInvalidate();
    }



    /**
     * 默认10秒
     * @param duration
     */
    public void setCircleAnimDuration(int duration) {
        this.mCircleAnimDuration = duration;
    }

    /**
     * 默认1.5秒
     * @param duration
     */
    public void setFastCircleAnimDuration(int duration) {
        this.mFastCircleAnimDuration=duration;
    }

    public float getSweepAngle() {
        return  mSweepAngle;
    }

    public void reset() {
        mIsWorking = false;
//        initPara();
        mPath.reset();
        mPath.moveTo(0, 0);
        postInvalidate();
    }

    public void setCircleStrokeWidth(float circleStrokeWidth) {
        this.mCircleStrokeWidth = circleStrokeWidth;
    }

    public void setTickStrokeWidth(float tickStrokeWidth) {
        this.mTickStrokeWidth = tickStrokeWidth;
    }

    public interface OnAnimEndListener {
        void onCircleAnimEnd();

        void onTickAnimEnd();
    }

    public void setOnAnimEndListener(OnAnimEndListener listener) {
        this.mListener = listener;
    }

}
