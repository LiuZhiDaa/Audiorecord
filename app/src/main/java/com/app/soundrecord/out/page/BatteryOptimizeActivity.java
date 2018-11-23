package com.app.soundrecord.out.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.soundrecord.R;

import org.json.JSONObject;

import java.util.Random;

import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.main.base.OutBaseActivity;

/**
 * @author gongguan
 * @time 2018/8/22 下午9:06
 */
public class BatteryOptimizeActivity extends OutBaseActivity {
    private static final int VALUE_INT_ROTATE_ANIM_DURATION = 600;
    private static final int VALUE_INT_TICK_ANIM_DURATION = 1000;
    private static final int VALUE_INT_RESULT_ANIM_DURATION = 500;

    private ImageView mIvRoate, mIvBattery;
    private ImageView mIvResult;
    private TextView mTvStatus;
    private ObjectAnimator mObjectAnimator;
    private AnimatorSet mAnimatorSet;
    private ImageView mIvCenterBg;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_battery_optimize;
    }

    @Override
    protected void initView() {
        setStatusBarColor(R.color.battery_optimize_bg);
        mIvRoate = findViewById(R.id.iv_battery_roate);
        mIvBattery = findViewById(R.id.iv_battery);
        mIvResult = findViewById(R.id.view_tick);
        mTvStatus = findViewById(R.id.tv_battery_optimize);
        mIvCenterBg = findViewById(R.id.battery_opt_iv);

        findViewById(R.id.iv_close).setOnClickListener(v -> {
            finish();
            JSONObject jsonObject = new JSONObject();
            UtilsJson.JsonSerialization(jsonObject, "close_button", "iv_close");
            UtilsLog.statisticsLog("battery", "close", jsonObject);
        });

        initAnimation();
    }

    private void initAnimation() {
        mObjectAnimator = ObjectAnimator.ofFloat(mIvRoate, "rotation", 0, 360);
        mObjectAnimator.setDuration(VALUE_INT_ROTATE_ANIM_DURATION);
        mObjectAnimator.setInterpolator(new LinearInterpolator());
        mObjectAnimator.setRepeatCount(8);
        mObjectAnimator.start();

        mObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showTickView();
            }
        });
    }

    @Override
    public ViewGroup getBannerAdLayout() {
        return findViewById(R.id.fl_banner_ad);
    }

    @Override
    public ViewGroup getNativeAdLayout() {
        return findViewById(R.id.fl_native_ad);
    }

    private void showTickView() {
        mIvRoate.setVisibility(View.GONE);
        mIvBattery.setVisibility(View.GONE);
        mIvCenterBg.setVisibility(View.GONE);
        mIvResult.setVisibility(View.VISIBLE);

        ObjectAnimator resultAnim = ObjectAnimator.ofFloat(mIvResult, "scaleX", 0, 1.f);
        ObjectAnimator resultAnim1 = ObjectAnimator.ofFloat(mIvResult, "scaleY", 0, 1.f);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(VALUE_INT_RESULT_ANIM_DURATION);
        mAnimatorSet.playTogether(resultAnim, resultAnim1);
        mAnimatorSet.start();

        mTvStatus.setText(String.format(getResources().getConfiguration().locale, getString(R.string.battery_optimize_after), getRandom()));
        ObjectAnimator tickAnimator = ObjectAnimator.ofFloat(mIvResult, "alpha", 0, 1.0f);
        tickAnimator.setDuration(VALUE_INT_TICK_ANIM_DURATION);
    }

    private Integer getRandom() {
        Random random = new Random();
        return random.nextInt(5) + 5;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mObjectAnimator != null) {
            mObjectAnimator.cancel();
            mObjectAnimator = null;
        }
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
        if (mIvResult != null) {
            mIvResult.clearAnimation();
        }
    }
}
