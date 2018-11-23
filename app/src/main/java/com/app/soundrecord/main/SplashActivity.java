package com.app.soundrecord.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.soundrecord.R;
import com.app.soundrecord.core.XCoreFactory;
import com.app.soundrecord.core.config.intf.IConfigMgr;
import com.app.soundrecord.core.config.intf.IConfigMgrListener;

import ulric.li.XLibFactory;

import ulric.li.logic.alive.UtilsBroadcast;
import ulric.li.utils.UtilsLog;
import ulric.li.xlib.intf.IXTimer;
import ulric.li.xlib.intf.IXTimerListener;

public class SplashActivity extends AppCompatActivity {
    private static final long VALUE_LONG_WAIT_TIME = 2500;
    private IConfigMgr mIConfigMgr = null;
    private IConfigMgrListener mIConfigMgrListener = null;

    private int mCheckLoadingCompleteCount = 0;
    private IXTimer mIXTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        UtilsLog.startLog("splash", null);
        UtilsBroadcast.sendAliveBroadcast(this);
        // ConfigMgr读取本地配置 预加载广告
        mIConfigMgr = (IConfigMgr) XCoreFactory.getInstance().createInstance(IConfigMgr.class);
        mIConfigMgrListener = new IConfigMgrListener() {
            @Override
            public void onDetectLocalInfoAsyncComplete() {

                checkLoadingComplete();
            }

            @Override
            public void onRequestConfigAsync(boolean bSuccess) {
            }
        };
        mIConfigMgr.addListener(mIConfigMgrListener);
        if (mIConfigMgr.detectLocalInfoAsync()) {
            mCheckLoadingCompleteCount++;
        }
        mIConfigMgr.requestConfigAsync();



        // 启动定时器等待结束界面
        mIXTimer = (IXTimer) XLibFactory.getInstance().createInstance(IXTimer.class);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIXTimer!=null&&mIXTimer.start(VALUE_LONG_WAIT_TIME, new IXTimerListener() {
            @Override
            public void onTimerComplete() {
                checkLoadingComplete();
            }

            @Override
            public void onTimerRepeatComplete() {
            }
        })) {
            mCheckLoadingCompleteCount++;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIXTimer != null) {
            mIXTimer.stop();
            mCheckLoadingCompleteCount--;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mIConfigMgr) {
            mIConfigMgr.removeListener(mIConfigMgrListener);
        }
        if (mIXTimer != null) {
            mIXTimer.stop();
        }
    }



    private void checkLoadingComplete() {
        mCheckLoadingCompleteCount--;
        if (mCheckLoadingCompleteCount > 0)
            return;

        MainActivity.launch(this);
        finish();
    }
}
