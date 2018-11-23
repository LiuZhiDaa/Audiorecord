package com.app.soundrecord.out.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.soundrecord.R;
import com.app.soundrecord.out.view.RoundProgressView;
import com.app.soundrecord.out.view.TickView;

import org.json.JSONObject;

import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.main.base.OutBaseActivity;

/**
 * @author gongguan  暂时未用，废弃中
 * @time 2018/7/18 下午5:35
 */
@Deprecated
public class AntivirusActivity extends OutBaseActivity {
    private static final int VALUE_INT_ROTATE_ANIM_DURATION = 5000;
    private static final int VALUE_INT_TICK_ANIM_DURATION = 600;

    private ImageView mIvAntivirusAnim, mIvCenterBg;
    private RoundProgressView mRoundView;
    private TickView mTickView;
    private TextView mTvStatus;
    private FrameLayout mFlNativeAd;
    private FrameLayout mFlBannerAd;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_antivirus;
    }

    @Override
    protected void initView() {
        setStatusBarColor(R.color.anivirus_page_bg_color);
        mIvAntivirusAnim = findViewById(R.id.iv_clean);
        mRoundView = findViewById(R.id.view_progress);
        mTickView = findViewById(R.id.view_tick);
        mTvStatus = findViewById(R.id.tv_status);
        mIvCenterBg = findViewById(R.id.iv_circle);
        mFlNativeAd = findViewById(R.id.fl_native_ad);
        mFlBannerAd = findViewById(R.id.fl_banner_ad);
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                JSONObject jsonObject = new JSONObject();
                UtilsJson.JsonSerialization(jsonObject, "close_button", "iv_close");
                UtilsLog.statisticsLog(getSceneType(), "close", jsonObject);
            }
        });
        initEvents();
        initAnimation();
    }

    @Override
    public ViewGroup getNativeAdLayout() {
        return mFlNativeAd;
    }

    @Override
    public ViewGroup getBannerAdLayout() {
        return mFlBannerAd;
    }

    private void initEvents() {
        mTickView = findViewById(R.id.view_tick);
        mTickView.setTickStrokeWidth(getResources().getDimension(R.dimen.garbage_progress_stroke));
        mTickView.setComplete();
    }

    private void initAnimation() {
        //中间的图片
        ObjectAnimator cleanAnimation = ObjectAnimator.ofFloat(mIvAntivirusAnim, "rotation", 0, 360 * 6);
        cleanAnimation.setDuration(VALUE_INT_ROTATE_ANIM_DURATION);
        LinearInterpolator ll = new LinearInterpolator();
        cleanAnimation.setInterpolator(ll);
        cleanAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIvCenterBg.setVisibility(View.INVISIBLE);
                showTickView();
            }
        });
        cleanAnimation.start();
        //进度条
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 360);
        progressAnimator.setDuration(VALUE_INT_ROTATE_ANIM_DURATION);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float angle = (float) animation.getAnimatedValue();
                mRoundView.setProgress(angle);
            }
        });
        progressAnimator.start();
    }

    private void showTickView() {
        mTickView.setVisibility(View.VISIBLE);
        ObjectAnimator tickAnimator = ObjectAnimator.ofFloat(mTickView, "alpha", 0, 1.0f);
        tickAnimator.setDuration(VALUE_INT_TICK_ANIM_DURATION);
        ObjectAnimator iconAnimator = ObjectAnimator.ofFloat(mIvAntivirusAnim, "alpha", 1.0f, 0);
        iconAnimator.setDuration(VALUE_INT_TICK_ANIM_DURATION);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(tickAnimator, iconAnimator);
        set.start();

        mTvStatus.setText(getResources().getString(R.string.antivirus_text_scanning_result));
    }

}
