package com.app.soundrecord.out.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.soundrecord.R;
import com.app.soundrecord.out.view.ImageProgressView2;
import com.app.soundrecord.out.view.RoundProgressBar;

import org.json.JSONObject;

import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.main.base.OutBaseActivity;

/**
 * 网络和wifi优化页面
 * Created by WangYu on 2018/7/2.
 */
public class NetworkActivity extends OutBaseActivity {

    private RoundProgressBar mRoundViewBig;
    private ImageProgressView2 mRoundViewSmall;
    private ImageView mIvResult, mIvWifi, mIvWifiBg;
    private TextView mTvStatus;
    private FrameLayout mFlNativeAd;
    private FrameLayout mFlBannerAd;
    private static final int VALUE_INT_ROTATE_ANIM_DURATION = 2000;
    private static final int VALUE_INT_RESULT_ANIM_DURATION = 500;
    private ValueAnimator mProgressAnimator;
    private AnimatorSet mAnimatorSet;

    protected int getLayoutRes() {
        return R.layout.activity_out_network;
    }

    @Override
    protected void initView() {
        setStatusBarColor(R.color.network_change_page_bg);
        initId();
        initAnim();
    }


    private void initId() {
        mIvWifi = findViewById(R.id.iv_progress_wifi);
        mIvWifiBg = findViewById(R.id.iv_progress_wifi_bg);
        mRoundViewSmall = findViewById(R.id.view_progress_small);
        mRoundViewBig = findViewById(R.id.view_progress_big);

        mRoundViewSmall.setDuration(VALUE_INT_ROTATE_ANIM_DURATION);
        mRoundViewSmall.setColor(getResources().getColor(R.color.white1));
        mRoundViewSmall.setImageDrawable(R.drawable.faster_seek);
        mRoundViewSmall.start();

        mRoundViewBig.setColor(Color.WHITE);

        mIvResult = findViewById(R.id.iv_result);
        mTvStatus = findViewById(R.id.tv_status);
        mFlNativeAd = findViewById(R.id.fl_native_ad);
        mFlBannerAd = findViewById(R.id.fl_banner_ad);
        findViewById(R.id.iv_close).setOnClickListener(v -> {
            finish();
            JSONObject jsonObject = new JSONObject();
            UtilsJson.JsonSerialization(jsonObject, "close_button", "iv_close");
            UtilsLog.statisticsLog("network", "close", jsonObject);
        });
    }

    private void initAnim() {
        //进度条动画
        mProgressAnimator = ValueAnimator.ofFloat(0, 360);
        mProgressAnimator.setDuration(VALUE_INT_ROTATE_ANIM_DURATION);

        mProgressAnimator.addUpdateListener(animation -> {
            float angle = (float) animation.getAnimatedValue();
            mRoundViewBig.setProgress((int) angle);
        });
        mProgressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showResultAnim();
            }
        });
        mProgressAnimator.start();
    }

    private void showResultAnim() {
        mIvWifi.setVisibility(View.GONE);
        mIvWifiBg.setVisibility(View.GONE);
        mRoundViewSmall.setVisibility(View.GONE);
        mRoundViewBig.setVisibility(View.GONE);
        mIvResult.setVisibility(View.VISIBLE);

        ObjectAnimator resultAnim = ObjectAnimator.ofFloat(mIvResult, "scaleX", 0, 1.0f);
        ObjectAnimator resultAnim1 = ObjectAnimator.ofFloat(mIvResult, "scaleY", 0, 1.0f);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(VALUE_INT_RESULT_ANIM_DURATION);
        mAnimatorSet.playTogether(resultAnim, resultAnim1);
        mAnimatorSet.start();

        int i = (int) (10 + Math.random() * (20 + 1));
        mTvStatus.setText(String.format(getResources().getConfiguration().locale, "%s %d%%", getResources().getString(R.string.network_speeding), i));
    }

    @Override
    public ViewGroup getNativeAdLayout() {
        return mFlNativeAd;
    }

    @Override
    public ViewGroup getBannerAdLayout() {
        return mFlBannerAd;
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
    }
}
