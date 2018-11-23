package com.app.soundrecord.out.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.soundrecord.R;
import com.app.soundrecord.out.view.RoundProgressView;

import org.json.JSONObject;

import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.main.base.OutBaseActivity;

/**
 * 加速
 */
public class BoosterActivity extends OutBaseActivity {
    private static final int VALUE_INT_RESULT_ANIM_DURATION = 500;
    private ImageView mIvResult;
    private TextView mTvStatus;
    private FrameLayout mFlNativeAd;
    private FrameLayout mFlBannerAd;
    private FrameLayout mFlBoosterBg;
    private AnimatorSet mAnimatorSet;
    private RoundProgressView mRoundView;
    private ImageView mIvRocket;
    private ImageView mIvCirclePoint;
    private ValueAnimator roundAnim;
    private ObjectAnimator translateAnim;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_out_booster;
    }

    @Override
    protected void initView() {
        setStatusBarColor(R.color.booster_page_bg_color);
        initId();
    }

    @Override
    public ViewGroup getNativeAdLayout() {
        return mFlNativeAd;
    }

    @Override
    public ViewGroup getBannerAdLayout() {
        return mFlBannerAd;
    }

    private void initId() {

        mIvResult = findViewById(R.id.tick_view);
        mFlBoosterBg = findViewById(R.id.fl_content);
        mFlNativeAd = findViewById(R.id.fl_native_ad);
        mFlBannerAd = findViewById(R.id.fl_banner_ad);
        mTvStatus = findViewById(R.id.tv_status);
        mRoundView = findViewById(R.id.booster_roundview);
        mIvRocket = findViewById(R.id.booster_socker_img);
        mIvCirclePoint = findViewById(R.id.booster_circle_point_iv);

        mRoundView.setStrokeWidth(5);
        mRoundView.setMoveCircleRadius(6);

        findViewById(R.id.iv_close).setOnClickListener(v -> {
            finish();
            JSONObject jsonObject = new JSONObject();
            UtilsJson.JsonSerialization(jsonObject, "close_button", "iv_close");
            UtilsLog.statisticsLog("boost", "close", jsonObject);
        });

        roundAnim = ValueAnimator.ofFloat(0, 360);
        roundAnim.setDuration(2000);
        roundAnim.addUpdateListener(animation -> {
            float angle = (float) animation.getAnimatedValue();
            mRoundView.setProgress(angle);
        });
        roundAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rocketUpAnim();
            }
        });
        roundAnim.start();
    }

    private void rocketUpAnim() {
        translateAnim = ObjectAnimator.ofFloat(mIvRocket, "translationY", 0, -800);
        translateAnim.setDuration(1000);
        translateAnim.start();

        translateAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showTickView();
            }
        });
    }

    private void showTickView() {
        mFlBoosterBg.setBackground(null);
        mIvRocket.setVisibility(View.GONE);
        mRoundView.setVisibility(View.GONE);
        mIvResult.setVisibility(View.VISIBLE);
        mIvCirclePoint.setVisibility(View.GONE);

        ObjectAnimator resultAnim = ObjectAnimator.ofFloat(mIvResult, "scaleX", 0, 1.0f);
        ObjectAnimator resultAnim1 = ObjectAnimator.ofFloat(mIvResult, "scaleY", 0, 1.0f);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(VALUE_INT_RESULT_ANIM_DURATION);
        mAnimatorSet.playTogether(resultAnim, resultAnim1);
        mAnimatorSet.start();

        int i = (int) (10 + Math.random() * (20 + 1));
        mTvStatus.setText(String.format(getResources().getConfiguration().locale, "%s %d%%", getResources().getString(R.string.booster_text), i));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
        if (translateAnim != null) {
            translateAnim.cancel();
        }
        if (roundAnim != null) {
            roundAnim.cancel();
        }
        if (mIvRocket != null) {
            mIvRocket.clearAnimation();
        }
        if (mIvResult != null) {
            mIvResult.clearAnimation();
        }
    }

}
