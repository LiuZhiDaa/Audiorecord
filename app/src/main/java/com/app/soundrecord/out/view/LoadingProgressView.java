package com.app.soundrecord.out.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


/**
 * 弧形进度条
 *
 * @author Wangyu
 */
public class LoadingProgressView extends View {
    private Context context;
    private float barStrokeWidth = 10;
    private int barColor = Color.WHITE;
    private Paint mPaintCircle = null;
    private RectF rectBg = null;
    private int width;
    private int height;
    private float mBigRatio = 1.0f;
    private float mSmallRatio = 0.5f;
    private TypedArray mTypedArray;
    private boolean mIsWorking = false;
    private CircleControler circleControler = new CircleControler();
    private RoundCompleteListener mRoundCompleteListener;
    private Control bigControl = new Control(0);
    private Control smallControl = new Control(100);
    private Paint mPaintBar;
    public static final int VALUE_INT_TYPE_DOUBLECIRCLE = 0X11;
    public static final int VALUE_INT_TYPE_SINGLECIRCLE = 0X22;
    public static final int VALUE_INT_TYPE_CONNECTING = 0X33;

    private int mCircleType = VALUE_INT_TYPE_SINGLECIRCLE;

    public LoadingProgressView(Context context) {
        super(context);
        this.context = context;
    }

    public LoadingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
//        mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressbar, -1, 0);
        init();
    }

    private void init() {
//        if (mTypedArray != null) {
//            barColor = mTypedArray.getColor(R.styleable.CircleProgressbar_barColor, Color.WHITE);
//            barStrokeWidth = mTypedArray.getDimension(R.styleable.CircleProgressbar_barStrokeWidth, barStrokeWidth);
//            mSmallRatio = mTypedArray.getFloat(R.styleable.CircleProgressbar_barSmallRatio, mSmallRatio);
//            mBigRatio = mTypedArray.getFloat(R.styleable.CircleProgressbar_barBigRatio, mBigRatio);
//        }

        smallControl.setFastShortRunCircle(540);
        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setColor(barColor);
        mPaintCircle.setStrokeWidth(barStrokeWidth);
        mPaintBar = new Paint();
        mPaintBar.setAntiAlias(true);
        mPaintBar.setStyle(Paint.Style.STROKE);
        mPaintBar.setStrokeWidth(barStrokeWidth);
        mPaintBar.setColor(barColor);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCircleType == VALUE_INT_TYPE_CONNECTING) {
            drawCircle(canvas, width * mBigRatio / 2, circleControler);
            circleControler.upDateUi();
            if (mIsWorking) {
                postInvalidate();
            }
        } else {
            drawCircle(canvas, width * mBigRatio / 2, bigControl);
            if (mCircleType == VALUE_INT_TYPE_DOUBLECIRCLE) {
                drawCircle(canvas, width * mBigRatio * mSmallRatio / 2, smallControl);
            }
            getCurrentStartAngle();
            postInvalidate();
        }
    }

    private void getCurrentStartAngle() {
        bigControl.upDateUi();
        smallControl.upDateUi();
    }

    public void start() {
        circleControler = new CircleControler();
        mIsWorking = true;
        postInvalidate();
    }


    public void stop() {
        mIsWorking = false;
    }

    public interface RoundCompleteListener {
        void onRoundComplete(float angle);
    }

    public void setOnRoundCompleteListener(RoundCompleteListener listener) {
        this.mRoundCompleteListener = listener;
    }


    public interface Controler {
        float getStartAngle();

        float getSweepAngle();

        void upDateUi();
    }

    public class CircleControler implements Controler {
        private final float VALUE_FLOAT_SWEEP_ANGLE = 360;
        private float mPoint = -90;
        private float mStartAngle = -90;
        private float mEndAngle = mStartAngle + VALUE_FLOAT_SWEEP_ANGLE;
        private float mStep = 8;
        private float mPointStep = 0.2f;
        private int status = 0;

        public void upDateUi() {
            mPoint += mPointStep;
            if (status == 0) {
                reduce();
            } else if (status == 1) {
                grow();
            }
        }

        private void grow() {
            mStartAngle = mPoint;
            if (mEndAngle < mStartAngle + VALUE_FLOAT_SWEEP_ANGLE) {
                mEndAngle += mStep;
                if (mEndAngle > mStartAngle + VALUE_FLOAT_SWEEP_ANGLE) {
                    mEndAngle = mStartAngle + VALUE_FLOAT_SWEEP_ANGLE;
                    mStartAngle = mPoint;
                    status = 0;
                }
            }
        }

        private void reduce() {
            mEndAngle = mPoint + VALUE_FLOAT_SWEEP_ANGLE;
            if (mStartAngle < mEndAngle) {
                mStartAngle += mStep;
                if (mStartAngle > mEndAngle) {
                    mStartAngle = mPoint;
                    mEndAngle = mPoint;
                    status = 1;
                    if (mRoundCompleteListener != null) {
                        mRoundCompleteListener.onRoundComplete(mPoint);
                    }
                }
            }
        }


        public float getStartAngle() {
            return mStartAngle;
        }

        public float getSweepAngle() {
            return mEndAngle - mStartAngle;
        }
    }


    class Control implements Controler {
        //起点弧度
        private float startAngle = 0;
        //默认的弧长
        private float sweepAngleOrignal = -10;
        private float sweepAngle = sweepAngleOrignal;


        //第一阶段参数
        private float fastStep = 6;
        private float fastRunAngle = 20;

        //第二阶段参数
        private float growStep = 5;
        private float growLength = 270;

        //缩小参数
        private float smallStep = 5;
        private float smallLength = sweepAngleOrignal;

        private int status = 0;
        private float totalAngle = 0;

        public void setFastShortRunCircle(int circleAngle) {
            fastRunAngle = circleAngle;
        }

        /**
         * 获取开始弧度位置
         *
         * @return
         */
        public float getStartAngle() {
            return startAngle;
        }

        /**
         * 获取弧度长度
         *
         * @return
         */
        public float getSweepAngle() {
            return sweepAngle;
        }

        public Control(float startAngle) {
            this.startAngle = startAngle;
        }

        /**
         * status == 0 进入
         * 短快速转动一圈
         */
        private void firstStage() {
            if (startAngle > 360) {
                startAngle -= 360;
            }

            if (totalAngle < fastRunAngle) {
                startAngle += fastStep;
                totalAngle += fastStep;
            } else {
                totalAngle = 0;
                status = 1;
            }
        }

        /**
         * status == 1 进入
         */
        private void toGrowUp() {
            if (startAngle > 360) {
                startAngle -= 360;
            }
            if (-sweepAngle < growLength) {
                startAngle += growStep;
                sweepAngle -= growStep * 0.67;
            } else {
                totalAngle = 0;
                status = 2;
            }

        }

        /**
         * status == 2 进入
         * 长的 很快转动 转2圈
         */
        private void keepLongFast() {
            if (startAngle > 360) {
                startAngle -= 360;
            }
            if (totalAngle < 20) {
                startAngle += fastStep;
                totalAngle += fastStep;
            } else {
                totalAngle = 0;
                status = 3;
            }
        }

        /**
         * status == 3 进入
         * 缩小
         */
        private void toSmall() {
            if (startAngle > 360) {
                startAngle -= 360;
            }

            if (-sweepAngle > -smallLength) {
                startAngle += smallStep;
                sweepAngle += smallStep;
            } else {
                totalAngle = 0;
                status = 0;
            }
        }

        /**
         * 更新参数
         *
         * @return
         */
        public void upDateUi() {
            if (status == 0) {
                //短小 快速转动
                firstStage();
            } else if (status == 1) {
                //基本保持尾部不动增长
                toGrowUp();
            } else if (status == 2) {
                keepLongFast();
            } else if (status == 3) {
                toSmall();
            }
        }
    }


    /**
     * 画圆
     *
     * @param canvas
     * @param radius
     * @param control
     */
    private void drawCircle(Canvas canvas, float radius, Controler control) {
        //中心
        int centerX = width / 2;
        int centerY = height / 2;
        //弧线宽度
        //内弧线直径
        float diameter = radius * 2;
        //内弧线半径
        float radiusInCircle = diameter / 2 - barStrokeWidth / 2;

        // 画弧形的矩阵区域。
        float l = centerX - radius + barStrokeWidth / 2;
        float t = centerY - radius + barStrokeWidth / 2;
        float r = centerX + radius - barStrokeWidth / 2;
        float b = centerY + radius - barStrokeWidth / 2;
        rectBg = new RectF(l, t, r, b);


        // 弧形ProgressBar。
        canvas.drawArc(rectBg, (float) control.getStartAngle(), control.getSweepAngle(), false, mPaintBar);


        //画起始的小圆
        // 计算圆心和半径。
        float cx_start = (float) (centerX + (radiusInCircle) * Math.cos(control.getStartAngle() * Math.PI / 180)); //圆心的x坐标
        float cy_start = (float) (centerY + radiusInCircle * Math.sin(control.getStartAngle() * Math.PI / 180)); //圆心y坐标  一样

        canvas.drawCircle(cx_start, cy_start, barStrokeWidth / 2, mPaintCircle);

        //画尾巴
        float x = (float) (centerX + radiusInCircle * Math.cos((control.getSweepAngle() + control.getStartAngle()) * Math.PI / 180));
        float y = (float) (centerY + radiusInCircle * Math.sin((control.getSweepAngle() + control.getStartAngle()) * Math.PI / 180));
        canvas.drawCircle(x, y, barStrokeWidth / 2, mPaintCircle);// 小圆

    }

    public void setmCircleType(int mCircleType) {
        this.mCircleType = mCircleType;
    }

}

