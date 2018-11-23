package com.app.soundrecord.out.core;

import android.text.TextUtils;

import com.app.soundrecord.Constants;

import ulric.li.xout.core.scene.intf.IOutSceneMgr;

/**
 * Created by WangYu on 2018/9/5.
 */
public class UtilsOutScene {
    // 充电广告
    private static final String VALUE_STRING_CONFIG_NATIVE_BATTERY_CHARGING_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_battery_charging";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_BATTERY_CHARGING_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_battery_charging";
    private static final String VALUE_STRING_CONFIG_BANNER_BATTERY_CHARGING_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_battery_charging";
    // 充电完成
    private static final String VALUE_STRING_CONFIG_NATIVE_BATTERY_COMPLETE_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_battery_complete";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_BATTERY_COMPLETE_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_battery_complete";
    private static final String VALUE_STRING_CONFIG_BANNER_BATTERY_COMPLETE_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_battery_complete";
    // 网络加速页面
    private static final String VALUE_STRING_CONFIG_NATIVE_NETWORK_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_network";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_NETWORK_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_network";
    private static final String VALUE_STRING_CONFIG_BANNER_NETWORK_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_network";
    // 系统加速页面
    private static final String VALUE_STRING_CONFIG_NATIVE_BOOSTER_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_booster";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_BOOSTER_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_booster";
    private static final String VALUE_STRING_CONFIG_BANNER_BOOSTER_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_booster";
    // 垃圾清理页面
    private static final String VALUE_STRING_CONFIG_NATIVE_CLEAN_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_clean";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_CLEAN_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_clean";
    private static final String VALUE_STRING_CONFIG_BANNER_CLEAN_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_clean";
    // 系统降温页面
    private static final String VALUE_STRING_CONFIG_NATIVE_COOL_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_cool";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_COOL_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_cool";
    private static final String VALUE_STRING_CONFIG_BANNER_COOL_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_cool";
    // 未接电话页面
    private static final String VALUE_STRING_CONFIG_NATIVE_CALL_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_call";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_CALL_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_call";
    private static final String VALUE_STRING_CONFIG_BANNER_CALL_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_call";
    // wifi优化
    private static final String VALUE_STRING_CONFIG_NATIVE_WIFI_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_wifi";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_WIFI_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_wifi";
    private static final String VALUE_STRING_CONFIG_BANNER_WIFI_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_wifi";
    // 卸载
    private static final String VALUE_STRING_CONFIG_NATIVE_UNINSTALL_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_uninstall";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_UNINSTALL_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_uninstall";
    private static final String VALUE_STRING_CONFIG_BANNER_UNINSTALL_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_uninstall";
    // 更新
    private static final String VALUE_STRING_CONFIG_NATIVE_UPDATE_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_update";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_UPDATE_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_update";
    private static final String VALUE_STRING_CONFIG_BANNER_UPDATE_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_update";
    // 杀毒
    private static final String VALUE_STRING_CONFIG_NATIVE_ANTIVIRUS_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_antivirus";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_ANTIVIRUS_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_antivirus";
    private static final String VALUE_STRING_CONFIG_BANNER_ANTIVIRUS_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_antivirus";
    // 锁屏
    private static final String VALUE_STRING_CONFIG_NATIVE_LOCK_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_lock";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_LOCK_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_lock";
    private static final String VALUE_STRING_CONFIG_BANNER_LOCK_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_lock";

    //电池优化
    private static final String VALUE_STRING_CONFIG_NATIVE_BATTERY_OPT_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_native_battery_opt";
    private static final String VALUE_STRING_CONFIG_INTERSTITIAL_BATTERY_OPT_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_interstitial_battery_opt";
    private static final String VALUE_STRING_CONFIG_BANNER_BATTERY_OPT_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_out_banner_battery_opt";

    public static String getNativeAdKeyByScene(String strScene) {
        if (TextUtils.isEmpty(strScene))
            return null;

        String strAdKey = null;
        switch (strScene) {
            case IOutSceneMgr.VALUE_STRING_CHARGE_SCENE_TYPE://充电
                strAdKey = VALUE_STRING_CONFIG_NATIVE_BATTERY_CHARGING_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_CHARGE_COMPLETE_SCENE_TYPE://充电完成
                strAdKey = VALUE_STRING_CONFIG_NATIVE_BATTERY_COMPLETE_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_NETWORK_SCENE_TYPE://网络
                strAdKey = VALUE_STRING_CONFIG_NATIVE_NETWORK_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_BOOST_SCENE_TYPE://加速
                strAdKey = VALUE_STRING_CONFIG_NATIVE_BOOSTER_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_CLEAN_SCENE_TYPE://清理
                strAdKey = VALUE_STRING_CONFIG_NATIVE_CLEAN_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_COOL_SCENE_TYPE://降温
                strAdKey = VALUE_STRING_CONFIG_NATIVE_COOL_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_CALL_SCENE_TYPE://未接
                strAdKey = VALUE_STRING_CONFIG_NATIVE_CALL_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_WIFI_SCENE_TYPE://wifi
                strAdKey = VALUE_STRING_CONFIG_NATIVE_WIFI_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_UNINSTALL_SCENE_TYPE://卸载
                strAdKey = VALUE_STRING_CONFIG_NATIVE_UNINSTALL_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_UPDATE_SCENE_TYPE://更新
                strAdKey = VALUE_STRING_CONFIG_NATIVE_UPDATE_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_ANTIVIRUS_SCENE_TYPE://杀毒
                strAdKey = VALUE_STRING_CONFIG_NATIVE_ANTIVIRUS_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE://锁屏
                strAdKey = VALUE_STRING_CONFIG_NATIVE_LOCK_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_BATTERY_OPTIMIZE_SCENE_TYPE://电池优化
                strAdKey = VALUE_STRING_CONFIG_NATIVE_BATTERY_OPT_AD_KEY;
                break;
        }

        return strAdKey;
    }

    public static String getBannerAdKeyByScene(String strScene) {
        if (TextUtils.isEmpty(strScene))
            return null;

        String strAdKey = null;
        switch (strScene) {
            case IOutSceneMgr.VALUE_STRING_CHARGE_SCENE_TYPE://充电
                strAdKey = VALUE_STRING_CONFIG_BANNER_BATTERY_CHARGING_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_CHARGE_COMPLETE_SCENE_TYPE://充电完成
                strAdKey = VALUE_STRING_CONFIG_BANNER_BATTERY_COMPLETE_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_NETWORK_SCENE_TYPE://网络
                strAdKey = VALUE_STRING_CONFIG_BANNER_NETWORK_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_BOOST_SCENE_TYPE://加速
                strAdKey = VALUE_STRING_CONFIG_BANNER_BOOSTER_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_CLEAN_SCENE_TYPE://清理
                strAdKey = VALUE_STRING_CONFIG_BANNER_CLEAN_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_COOL_SCENE_TYPE://降温
                strAdKey = VALUE_STRING_CONFIG_BANNER_COOL_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_CALL_SCENE_TYPE://未接
                strAdKey = VALUE_STRING_CONFIG_BANNER_CALL_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_WIFI_SCENE_TYPE://wifi
                strAdKey = VALUE_STRING_CONFIG_BANNER_WIFI_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_UNINSTALL_SCENE_TYPE://卸载
                strAdKey = VALUE_STRING_CONFIG_BANNER_UNINSTALL_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_UPDATE_SCENE_TYPE://更新
                strAdKey = VALUE_STRING_CONFIG_BANNER_UPDATE_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_ANTIVIRUS_SCENE_TYPE://杀毒
                strAdKey = VALUE_STRING_CONFIG_BANNER_ANTIVIRUS_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE://锁屏
                strAdKey = VALUE_STRING_CONFIG_BANNER_LOCK_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_BATTERY_OPTIMIZE_SCENE_TYPE://电池优化
                strAdKey = VALUE_STRING_CONFIG_BANNER_BATTERY_OPT_AD_KEY;
                break;
        }

        return strAdKey;
    }

    public static String getInterstitialAdKeyByScene(String strScene) {
        if (TextUtils.isEmpty(strScene))
            return null;

        String strAdKey = null;
        switch (strScene) {
            case IOutSceneMgr.VALUE_STRING_CHARGE_SCENE_TYPE://充电
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_BATTERY_CHARGING_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_CHARGE_COMPLETE_SCENE_TYPE://充电完成
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_BATTERY_COMPLETE_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_NETWORK_SCENE_TYPE://网络
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_NETWORK_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_BOOST_SCENE_TYPE:
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_BOOSTER_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_CLEAN_SCENE_TYPE:
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_CLEAN_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_COOL_SCENE_TYPE:
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_COOL_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_CALL_SCENE_TYPE:
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_CALL_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_WIFI_SCENE_TYPE://wifi
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_WIFI_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_UNINSTALL_SCENE_TYPE://卸载
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_UNINSTALL_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_UPDATE_SCENE_TYPE://更新
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_UPDATE_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_ANTIVIRUS_SCENE_TYPE://杀毒
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_ANTIVIRUS_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE://锁屏
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_LOCK_AD_KEY;
                break;
            case IOutSceneMgr.VALUE_STRING_BATTERY_OPTIMIZE_SCENE_TYPE://电池优化
                strAdKey = VALUE_STRING_CONFIG_INTERSTITIAL_BATTERY_OPT_AD_KEY;
                break;
        }

        return strAdKey;
    }

    public static String getSceneByAdKey(String strAdKey) {
        if (TextUtils.isEmpty(strAdKey))
            return null;

        String strScene = null;
        switch (strAdKey) {
            case VALUE_STRING_CONFIG_NATIVE_BATTERY_CHARGING_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_BATTERY_CHARGING_AD_KEY:
            case VALUE_STRING_CONFIG_BANNER_BATTERY_CHARGING_AD_KEY:
                //充电
                strScene = IOutSceneMgr.VALUE_STRING_CHARGE_SCENE_TYPE;
                break;
            case VALUE_STRING_CONFIG_BANNER_BATTERY_COMPLETE_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_BATTERY_COMPLETE_AD_KEY:
            case VALUE_STRING_CONFIG_NATIVE_BATTERY_COMPLETE_AD_KEY:
                //充电完成
                strScene = IOutSceneMgr.VALUE_STRING_CHARGE_COMPLETE_SCENE_TYPE;
                break;
            case VALUE_STRING_CONFIG_NATIVE_NETWORK_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_NETWORK_AD_KEY:
            case VALUE_STRING_CONFIG_BANNER_NETWORK_AD_KEY:
                //网络
                strScene = IOutSceneMgr.VALUE_STRING_NETWORK_SCENE_TYPE;
                break;
            case VALUE_STRING_CONFIG_NATIVE_BOOSTER_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_BOOSTER_AD_KEY:
            case VALUE_STRING_CONFIG_BANNER_BOOSTER_AD_KEY:
                //加速
                strScene = IOutSceneMgr.VALUE_STRING_BOOST_SCENE_TYPE;
                break;
            case VALUE_STRING_CONFIG_NATIVE_CLEAN_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_CLEAN_AD_KEY:
            case VALUE_STRING_CONFIG_BANNER_CLEAN_AD_KEY:
                //清理
                strScene = IOutSceneMgr.VALUE_STRING_CLEAN_SCENE_TYPE;
                break;
            case VALUE_STRING_CONFIG_NATIVE_COOL_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_COOL_AD_KEY:
            case VALUE_STRING_CONFIG_BANNER_COOL_AD_KEY:
                //降温
                strScene = IOutSceneMgr.VALUE_STRING_COOL_SCENE_TYPE;
                break;
            case VALUE_STRING_CONFIG_NATIVE_CALL_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_CALL_AD_KEY:
            case VALUE_STRING_CONFIG_BANNER_CALL_AD_KEY:
                //未接
                strScene = IOutSceneMgr.VALUE_STRING_CALL_SCENE_TYPE;
                break;
            case VALUE_STRING_CONFIG_NATIVE_WIFI_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_WIFI_AD_KEY:
            case VALUE_STRING_CONFIG_BANNER_WIFI_AD_KEY:
                //wifi
                strScene = IOutSceneMgr.VALUE_STRING_WIFI_SCENE_TYPE;
                break;
            case VALUE_STRING_CONFIG_NATIVE_UNINSTALL_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_UNINSTALL_AD_KEY:
            case VALUE_STRING_CONFIG_BANNER_UNINSTALL_AD_KEY:
                //卸载
                strScene = IOutSceneMgr.VALUE_STRING_UNINSTALL_SCENE_TYPE;
                break;
            case VALUE_STRING_CONFIG_NATIVE_UPDATE_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_UPDATE_AD_KEY:
            case VALUE_STRING_CONFIG_BANNER_UPDATE_AD_KEY:
                //更新
                strScene = IOutSceneMgr.VALUE_STRING_UPDATE_SCENE_TYPE;
                break;
            case VALUE_STRING_CONFIG_NATIVE_ANTIVIRUS_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_ANTIVIRUS_AD_KEY:
            case VALUE_STRING_CONFIG_BANNER_ANTIVIRUS_AD_KEY:
                //杀毒
                strScene = IOutSceneMgr.VALUE_STRING_ANTIVIRUS_SCENE_TYPE;
                break;
            case VALUE_STRING_CONFIG_NATIVE_LOCK_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_LOCK_AD_KEY:
            case VALUE_STRING_CONFIG_BANNER_LOCK_AD_KEY:
                //锁屏
                strScene = IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE;
                break;
            case VALUE_STRING_CONFIG_NATIVE_BATTERY_OPT_AD_KEY:
            case VALUE_STRING_CONFIG_INTERSTITIAL_BATTERY_OPT_AD_KEY:
            case VALUE_STRING_CONFIG_BANNER_BATTERY_OPT_AD_KEY:
                //电池优化
                strScene = IOutSceneMgr.VALUE_STRING_BATTERY_OPTIMIZE_SCENE_TYPE;
                break;

        }

        return strScene;
    }
}
