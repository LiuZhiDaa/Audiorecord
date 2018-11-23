package com.app.soundrecord.out.core;


import com.app.soundrecord.application.XConfig;
import com.app.soundrecord.out.page.BatteryActivity;
import com.app.soundrecord.out.page.BatteryEndActivity;
import com.app.soundrecord.out.page.BatteryOptimizeActivity;
import com.app.soundrecord.out.page.BoosterActivity;
import com.app.soundrecord.out.page.CleanerActivity;
import com.app.soundrecord.out.page.CoolActivity;
import com.app.soundrecord.out.page.NetworkActivity;

import java.util.Random;

import ulric.li.utils.UtilsLog;
import ulric.li.xout.core.communication.IOutComponent;
import ulric.li.xout.core.scene.intf.IOutSceneMgr;

/**
 * Created by WangYu on 2018/9/5.
 */
public class OutComponentImpl implements IOutComponent {


    @Override
    public String getConfigUrl() {
        return XConfig.VALUE_STRING_CONFIG_URL;
    }

    @Override
    public String getNativeAdKey(String strScene) {
        return UtilsOutScene.getNativeAdKeyByScene(strScene);
    }

    @Override
    public String getBannerAdKey(String strScene) {
        return UtilsOutScene.getBannerAdKeyByScene(strScene);
    }

    @Override
    public String getInterstitialAdKey(String strScene) {
        return UtilsOutScene.getInterstitialAdKeyByScene(strScene);
    }

    @Override
    public String getSceneName(String adKey) {
        return UtilsOutScene.getSceneByAdKey(adKey);
    }

    @Override
    public Class<?> getOutPageClass(String strScene) {
        Class<?> cls = null;
        switch (strScene) {
            case IOutSceneMgr.VALUE_STRING_CHARGE_SCENE_TYPE://充电
                cls = BatteryActivity.class;
                break;
            case IOutSceneMgr.VALUE_STRING_CHARGE_COMPLETE_SCENE_TYPE://充电完成
                cls = BatteryEndActivity.class;
                break;
            case IOutSceneMgr.VALUE_STRING_NETWORK_SCENE_TYPE://网络
            case IOutSceneMgr.VALUE_STRING_WIFI_SCENE_TYPE://wifi
                cls = NetworkActivity.class;
                break;
            case IOutSceneMgr.VALUE_STRING_BOOST_SCENE_TYPE://加速
                cls = BoosterActivity.class;
                break;
            case IOutSceneMgr.VALUE_STRING_CLEAN_SCENE_TYPE://清理
            case IOutSceneMgr.VALUE_STRING_UNINSTALL_SCENE_TYPE://卸载
            case IOutSceneMgr.VALUE_STRING_UPDATE_SCENE_TYPE://更新
                cls = CleanerActivity.class;
                break;
            case IOutSceneMgr.VALUE_STRING_COOL_SCENE_TYPE://降温
                cls = CoolActivity.class;
                break;
            case IOutSceneMgr.VALUE_STRING_CALL_SCENE_TYPE://未接
            case IOutSceneMgr.VALUE_STRING_ANTIVIRUS_SCENE_TYPE://杀毒
                cls = CleanerActivity.class;
                break;
            case IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE://锁屏
                cls=getRandomActivityClass();
//                mWakeLockTool.release();
                UtilsLog.logI("UtilsLog", "release_lock");
                break;
            case IOutSceneMgr.VALUE_STRING_BATTERY_OPTIMIZE_SCENE_TYPE:
                cls = BatteryOptimizeActivity.class;
                break;
        }
        return cls;
    }

    private Class<?> getRandomActivityClass() {
        Random random = new Random();
        int i = random.nextInt(4);
        Class<?> cls = null;
        switch (i) {
            case 0:
                cls = BoosterActivity.class;
                break;
            case 1:
                cls = CoolActivity.class;
                break;
            case 2:
                cls = CleanerActivity.class;
                break;
            case 3:
                cls = CleanerActivity.class;
                break;
        }
        return cls;
    }
}
