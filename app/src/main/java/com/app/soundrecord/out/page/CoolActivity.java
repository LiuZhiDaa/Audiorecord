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
import com.app.soundrecord.out.view.ScanView;

import org.json.JSONObject;

import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.main.base.OutBaseActivity;

/**
 * 冷却降温
 */
public class CoolActivity extends OutBaseActivity {

    private static final int VALUE_INT_RESULT_ANIM_DURATION = 500;
    private static final int VALUE_INT_ROTATE_ANIM_DURATION = 3600;

    private FrameLayout mFlNativeAd;
    private FrameLayout mFlBannerAd;
    private ScanView mScanView;
    private TextView mTvStatus;
    private ImageView mIvCircleBg, mIvResult;
    private ValueAnimator mProgressAnimator;
    private AnimatorSet mAnimatorSet;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_out_cool;
    }

    @Override
    protected void initView() {
        setStatusBarColor(R.color.cool_page_bg_color);
        initId();
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

    private void initId() {
        mIvResult = findViewById(R.id.iv_result_view);
        mScanView = findViewById(R.id.view_scan);
        mFlNativeAd = findViewById(R.id.fl_native_ad);
        mFlBannerAd = findViewById(R.id.fl_banner_ad);
        mTvStatus = findViewById(R.id.tv_status);
        mIvCircleBg = findViewById(R.id.iv_cool_circle_bg);


        findViewById(R.id.iv_close).setOnClickListener(v -> {
            finish();
            JSONObject jsonObject = new JSONObject();
            UtilsJson.JsonSerialization(jsonObject, "close_button", "iv_close");
            UtilsLog.statisticsLog("cool", "close", jsonObject);
        });
    }

    private void initAnimation() {
        mScanView.setSnowDrawable(R.drawable.out_icon_phone_out);
        mScanView.setDstDrawable(R.drawable.out_icon_phone_inner);
        mScanView.setScanLineDrawable(R.drawable.icon_scan_line, R.drawable.icon_scan_line_up);
        mScanView.setDuration(1200);
        mScanView.start();

        mProgressAnimator = ValueAnimator.ofFloat(0, 360);
        mProgressAnimator.setDuration(VALUE_INT_ROTATE_ANIM_DURATION);
        mProgressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showSnowView();
            }
        });
        mProgressAnimator.start();
    }

    private void showSnowView() {
        mScanView.stop();
        mScanView.setVisibility(View.GONE);
        mIvResult.setVisibility(View.VISIBLE);
        mIvCircleBg.setVisibility(View.GONE);

        ObjectAnimator resultAnim = ObjectAnimator.ofFloat(mIvResult, "scaleX", 0, 1.f);
        ObjectAnimator resultAnim1 = ObjectAnimator.ofFloat(mIvResult, "scaleY", 0, 1.f);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(VALUE_INT_RESULT_ANIM_DURATION);
        mAnimatorSet.playTogether(resultAnim, resultAnim1);
        mAnimatorSet.start();

        int i = (int) (1 + Math.random() * (4 + 1));
        mTvStatus.setText(String.format(getResources().getConfiguration().locale, getResources().getString(R.string.cool_result), i));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressAnimator != null) {
            mProgressAnimator.cancel();
        }
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
        if (mIvResult != null) {
            mIvResult.clearAnimation();
        }
        if (mScanView!=null) {
            mScanView.stop();
        }
    }
}
