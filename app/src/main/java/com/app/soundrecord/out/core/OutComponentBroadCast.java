package com.app.soundrecord.out.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import ulric.li.utils.UtilsLog;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.communication.IOutComponent;

/**
 * Created by WangYu on 2018/9/6.
 */
public class OutComponentBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        UtilsLog.statisticsLog("out", "setOutComponent", null);
        XOutFactory.setComponentImpl(new OutComponentImpl());
    }


    public static void register(Context context) {
        try {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(IOutComponent.BROADCAST_ACTION);
            localBroadcastManager.registerReceiver(new OutComponentBroadCast(), intentFilter);
        } catch (Exception e) {
            UtilsLog.crashLog(e);
        }
    }
}
