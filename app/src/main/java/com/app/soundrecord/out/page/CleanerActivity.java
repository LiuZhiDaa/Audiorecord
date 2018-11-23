package com.app.soundrecord.out.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.soundrecord.R;
import com.app.soundrecord.out.view.RoundProgressView;

import org.json.JSONObject;

import java.text.DecimalFormat;

import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.core.scene.intf.IOutSceneMgr;
import ulric.li.xout.main.base.OutBaseActivity;

/**
 * 垃圾清理
 */
public class CleanerActivity extends OutBaseActivity {

    private TextView mTvStatus;
    private ImageView mIvClean, mIvBroom;
    private RoundProgressView mRoundView;
    private ImageView mIvResult;
    private FrameLayout mFlNativeAd;
    private FrameLayout mFlBannerAd;
    private ImageView mIvCircleBg;

    private static final int VALUE_INT_ROTATE_ANIM_DURATION = 5000;
    private static final int VALUE_INT_TICK_ANIM_DURATION = 600;
    private ObjectAnimator mCleanAnimation;
    private ValueAnimator mProgressAnimator;
    private AnimatorSet mResultAnimSet;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_out_clean;
    }

    @Override
    protected void initView() {
        setStatusBarColor(R.color.clean_page_bg_color);
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
        mIvClean = findViewById(R.id.iv_clean);
        mRoundView = findViewById(R.id.view_progress);
        mIvBroom = findViewById(R.id.clean_center_broom_iv);
        mIvResult = findViewById(R.id.view_tick);
        mFlNativeAd = findViewById(R.id.fl_native_ad);
        mFlBannerAd = findViewById(R.id.fl_banner_ad);
        mTvStatus = findViewById(R.id.tv_status);
        mIvCircleBg = findViewById(R.id.iv_clean_circle_bg);

        mRoundView.setBgCircleColor(getResources().getColor(R.color.white2));
        mRoundView.setColor(getResources().getColor(R.color.white1));
        mRoundView.setStrokeWidth(getResources().getDimension(R.dimen.garbage_progress_stroke));

        mIvResult.setVisibility(View.GONE);

        findViewById(R.id.iv_close).setOnClickListener(v -> {
            finish();
            JSONObject jsonObject = new JSONObject();
            UtilsJson.JsonSerialization(jsonObject, "close_button", "iv_close");
            UtilsLog.statisticsLog("clean", "close", jsonObject);
        });
    }

    private void initAnimation() {
        //中间的图片
        mCleanAnimation = ObjectAnimator.ofFloat(mIvClean, "rotation", 0, 360 * 6);
        mCleanAnimation.setDuration(VALUE_INT_ROTATE_ANIM_DURATION);
        mCleanAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mCleanAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showTickView();
            }
        });
        mCleanAnimation.start();
        //进度条
        mProgressAnimator = ValueAnimator.ofFloat(0, 360);
        mProgressAnimator.setDuration(VALUE_INT_ROTATE_ANIM_DURATION);
        mProgressAnimator.addUpdateListener(animation -> {
            float angle = (float) animation.getAnimatedValue();
            mRoundView.setProgress(angle);
        });
        mProgressAnimator.start();
    }

    private void showTickView() {
        mIvClean.setVisibility(View.GONE);
        mIvResult.setVisibility(View.VISIBLE);
        mIvBroom.setVisibility(View.GONE);
        mIvCircleBg.setVisibility(View.GONE);

        ObjectAnimator tickAnimator = ObjectAnimator.ofFloat(mIvResult, "scaleX", 0, 1.0f);
        ObjectAnimator tickAnimator1 = ObjectAnimator.ofFloat(mIvResult, "scaleY", 0, 1.0f);

        mResultAnimSet = new AnimatorSet();
        mResultAnimSet.setDuration(VALUE_INT_TICK_ANIM_DURATION);
        mResultAnimSet.playTogether(tickAnimator, tickAnimator1);
        mResultAnimSet.start();

        double cleanSize;
        String result;
        if (IOutSceneMgr.VALUE_STRING_UNINSTALL_SCENE_TYPE.equals(getSceneType())) {
            cleanSize = (5 + Math.random() * (10 + 1));
            result = getResources().getString(R.string.clean_uninstall);
        } else if (IOutSceneMgr.VALUE_STRING_UPDATE_SCENE_TYPE.equals(getSceneType())) {
            cleanSize = (2 + Math.random() * (2 + 1));
            result = getResources().getString(R.string.clean_update);
        } else if (IOutSceneMgr.VALUE_STRING_CALL_SCENE_TYPE.equals(getSceneType())
                || IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE.equals(getSceneType())) {
            cleanSize = (2 + Math.random() * (2 + 1));
            result = getResources().getString(R.string.clean_result);
        } else {
            cleanSize = (80 + Math.random() * (300 + 1));
            result = getResources().getString(R.string.clean_result);
        }
        mTvStatus.setText(String.format(getResources().getConfiguration().locale, result, formatData(cleanSize)));
    }


    public String formatData(double data) {
        String pattern = "##0.#";
        DecimalFormat df = new DecimalFormat();
        df.applyPattern(pattern);
        return df.format(data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCleanAnimation != null) {
            mCleanAnimation.cancel();
        }
        if (mProgressAnimator != null) {
            mProgressAnimator.cancel();
        }
        if (mResultAnimSet != null) {
            mResultAnimSet.cancel();
        }
        if (mIvResult != null) {
            mIvResult.clearAnimation();
        }
    }
}
