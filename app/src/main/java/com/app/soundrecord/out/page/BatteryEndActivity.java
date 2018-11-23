package com.app.soundrecord.out.page;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.soundrecord.R;

import org.json.JSONObject;

import java.util.Locale;

import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.trigger.intf.IOutTriggerMgr;
import ulric.li.xout.main.base.OutBaseActivity;

/**
 * Created by wanghailong on 2018/7/2.
 * <p>
 * 电池状态页面
 */

public class BatteryEndActivity extends OutBaseActivity implements View.OnClickListener {

    private ImageView mIvClose;
    private FrameLayout mFlNativeAd;
    private FrameLayout mFlBannerAd;
    private BatteryBroadCastReceiver mReceiver;
    private TextView mTvBatteryStatus;
    private ImageView mIvBatteryLogo;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_battery_end;
    }

    @Override
    protected void initView() {
        setStatusBarColor(R.color.battery_end_bg);
        mTvBatteryStatus = findViewById(R.id.tv_battery_status);
        mIvBatteryLogo = findViewById(R.id.iv_battery_status);
        mIvClose = findViewById(R.id.iv_close);
        mFlNativeAd = findViewById(R.id.fl_native_ad);
        mFlBannerAd = findViewById(R.id.fl_banner_ad);
        mIvClose.setOnClickListener(this);
        initData();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
    }

    private void initData() {
        IOutTriggerMgr iOutTriggerMgr = (IOutTriggerMgr) XOutFactory.getInstance().createInstance(IOutTriggerMgr.class);
        long time = iOutTriggerMgr.getChargeTime();

        mTvBatteryStatus.setText(convertTime(time));
    }

    @Override
    public ViewGroup getNativeAdLayout() {
        return mFlNativeAd;
    }

    @Override
    public ViewGroup getBannerAdLayout() {
        return mFlBannerAd;
    }

    public void onResume() {
        super.onResume();
        mReceiver = new BatteryBroadCastReceiver();
        // 监听电量变化
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mReceiver, filter);
    }


    @Override
    public void onClick(View v) {
        finish();
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "close_button", "iv_close");
        UtilsLog.statisticsLog("charge_over", "close", jsonObject);
    }

    private SpannableStringBuilder convertTime(long time) {
        String chargeText = getResources().getString(R.string.battery_charge_time);
        String chargeComplete = getResources().getString(R.string.battery_charge_complete);


        time /= 1000;
        long hour = time / 3600;
        long minute = (time % 3600) / 60;
        long second = time % 3600 % 60;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(time == 0 ? chargeComplete : chargeText + " ");
        if (hour != 0) {
            spannableStringBuilder.append(getTextSpan(hour));
            spannableStringBuilder.append("h");
        }
        if (minute != 0) {
            spannableStringBuilder.append(getTextSpan(minute));
            spannableStringBuilder.append("min");
        }
        if (second != 0) {
            spannableStringBuilder.append(getTextSpan(second));
            spannableStringBuilder.append("second");
        }
        return spannableStringBuilder;
    }

    private SpannableString getTextSpan(long time) {
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, this.getResources().getDisplayMetrics());
        SpannableString spannableString = new SpannableString(time + "");
        spannableString.setSpan(new AbsoluteSizeSpan(dp), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }


    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public class BatteryBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            String action = intent.getAction();
            /*****处理电池电量*****/
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                // 获取当前电量
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                // 电量的总刻度
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {//充电
                    mIvBatteryLogo.setImageResource(R.drawable.out_icon_charging);
                    mTvBatteryStatus.setText(R.string.battery_charging);
                } else {//不充电了
                    mIvBatteryLogo.setImageResource(R.drawable.out_icon_chaging_complete);
                    mTvBatteryStatus.setText(R.string.battery_charge_complete);
                }
                int batteryChargingProgress = (level * 100) / scale;
                mTvBatteryStatus.append(String.format(Locale.getDefault()," %d%%", batteryChargingProgress));
            }
        }


    }
}