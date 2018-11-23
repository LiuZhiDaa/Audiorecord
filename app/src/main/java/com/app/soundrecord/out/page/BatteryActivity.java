package com.app.soundrecord.out.page;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import ulric.li.xout.main.base.OutBaseActivity;

/**
 * Created by wanghailong on 2018/7/2.
 * <p>
 * 电池状态页面
 */

public class BatteryActivity extends OutBaseActivity implements View.OnClickListener {

    private ImageView mIvBatteryChargingLogo;
    private FrameLayout mFlNativeAd;
    private FrameLayout mFlBannerAd;
    private BatteryBroadCastReceiver mReceiver;
    private ImageView mWaveView, mWaveViewMid;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    mWaveView.setVisibility(View.VISIBLE);

                    Message msg1 = new Message();
                    msg1.what = 1000;
                    mHandler.sendEmptyMessageDelayed(msg1.what, 500);

                    Message msg2 = new Message();
                    msg2.what = 0;
                    mHandler.sendEmptyMessageDelayed(msg2.what, 1000);

                    Message msg3 = new Message();
                    msg3.what = 1;
                    mHandler.sendEmptyMessageDelayed(msg3.what, 1500);
                    break;
                case 0:
                    mWaveViewMid.setVisibility(View.VISIBLE);
                    break;
                default:
                    mWaveView.setVisibility(View.INVISIBLE);
                    mWaveViewMid.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };
    private TextView mTvBatteryStatus;
    private ImageView mIvBatteryStatus;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_battery;
    }

    @Override
    protected void initView() {
        setStatusBarColor(R.color.battery_charging_bg);
        mTvBatteryStatus = findViewById(R.id.tv_battery_status);
        mIvBatteryStatus = findViewById(R.id.iv_battery_status);
        findViewById(R.id.iv_close).setOnClickListener(this);
        mFlNativeAd = findViewById(R.id.fl_native_ad);
        mFlBannerAd = findViewById(R.id.fl_banner_ad);
        mWaveView = findViewById(R.id.battery_waveview);
        mWaveViewMid = findViewById(R.id.battery_waveview_middle);

        Message msg1 = new Message();
        msg1.what = 0;
        mHandler.sendEmptyMessageDelayed(msg1.what,500);

        Message msg2 = new Message();
        msg2.what = 1;
        mHandler.sendEmptyMessageDelayed(msg2.what, 1000);
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
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeMessages(0);
            mHandler.removeMessages(1);
            mHandler.removeMessages(1000);
        }
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
    public void onClick(View v) {
        finish();
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
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {//充电
                    mIvBatteryStatus.setImageResource(R.drawable.out_icon_charging);
                } else {//不充电了
                    mIvBatteryStatus.setImageResource(R.drawable.out_icon_chaging_complete);

                    JSONObject jsonObject = new JSONObject();
                    UtilsJson.JsonSerialization(jsonObject, "close_button", "charge_complete");
                    UtilsLog.statisticsLog("charge", "close", jsonObject);
                }
                // 获取当前电量
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                // 电量的总刻度
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
                int batteryChargingProgress = (level * 100) / scale;
                mTvBatteryStatus.setText(String.format(Locale.getDefault(),
                        "%s %d%%",
                        getResources().getString(R.string.battery_charging),
                        batteryChargingProgress));

            }
        }
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}