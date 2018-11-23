package com.app.soundrecord.util;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.app.soundrecord.core.XCoreFactory;
import com.app.soundrecord.core.config.intf.ICloudConfig;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.HashMap;
import java.util.Map;

import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdConfig;
import ulric.li.ad.intf.IAdMgr;
import ulric.li.ad.utils.ProfitAdUtils;
import ulric.li.utils.UtilsLog;

public class UtilsAd {
    public static boolean isCanRequestAd(IAdConfig iAdConfig, String strScene) {
        if (null == iAdConfig)
            return false;

        ICloudConfig iCloudConfig = (ICloudConfig) XCoreFactory.getInstance().createInstance(ICloudConfig.class);
        if (null == iCloudConfig || !iCloudConfig.isAdEnable())
            return false;

        if (!TextUtils.isEmpty(strScene) && !iAdConfig.isSupportRequestScene(strScene))
            return false;

        return true;
    }

    public static boolean requestAd(IAdConfig iAdConfig, String strScene) {
        if (null == iAdConfig || !isCanRequestAd(iAdConfig, strScene))
            return false;

        return ProfitAdUtils.requestAd(iAdConfig);
    }

    /**
     * 展示原生或者banner广告
     *
     * @param container
     * @param iAdConfig
     * @param withAnimation
     * @return
     */
    public static Object showAdView(ViewGroup container, final IAdConfig iAdConfig, boolean withAnimation) {
        if (!UtilsAd.isCanShowAd(iAdConfig, null)) {
            UtilsLog.logI("ad", "view_ad_switch_of");
            return null;
        }

        if (null == container) {
            UtilsLog.logI("ad", "container_is_null");
            return null;
        }

        IAdMgr iAdMgr = (IAdMgr) XProfitFactory.getInstance().createInstance(IAdMgr.class);
        Object objAd = null;
        View adView = null;
        if (iAdConfig.getAdType().equals(IAdConfig.VALUE_STRING_NATIVE_AD_TYPE)) {
            objAd = iAdMgr.getNativeAdInfo(iAdConfig);
            if (objAd instanceof NativeAd) {
                adView = ProfitAdUtils.getDefaultFacebookNativeAdView((NativeAd) objAd);
            } else if (objAd instanceof UnifiedNativeAd) {
                adView = ProfitAdUtils.getDefaultAdmobNativeAdView((UnifiedNativeAd) objAd);
            }
        } else if (iAdConfig.getAdType().equals(IAdConfig.VALUE_STRING_BANNER_AD_TYPE)) {
            adView = iAdMgr.getBannerAdView(iAdConfig);
            objAd = adView;
        }
        ProfitAdUtils.showAdView(container, iAdConfig, adView,0, withAnimation);

        return objAd;
    }

    public static void showAdView(ViewGroup container, final IAdConfig iAdConfig, final View adView, boolean withAnimation){
        ProfitAdUtils.showAdView(container,iAdConfig,adView,0,withAnimation);
    }


    public static boolean isCanShowAd(IAdConfig iAdConfig, String strScene) {
        if (null == iAdConfig)
            return false;

        ICloudConfig iCloudConfig = (ICloudConfig) XCoreFactory.getInstance().createInstance(ICloudConfig.class);
        if (!iCloudConfig.isAdEnable())
            return false;

        if (!TextUtils.isEmpty(strScene) && !iAdConfig.isSupportShowScene(strScene))
            return false;

        return true;
    }

    public static boolean showInterstitialAd(IAdConfig iAdConfig, String strScene) {
        if (null == iAdConfig || !isCanShowAd(iAdConfig, strScene))
            return false;

        return ProfitAdUtils.showInterstitialAd(iAdConfig);
    }



    public static Map<IAdConfig, Object> mGlobalAdMap = new HashMap<>();

    public static void putAd(IAdConfig iAdConfig, Object ad) {
        if (mGlobalAdMap != null) {
            mGlobalAdMap.put(iAdConfig, ad);
        }
    }

    public static Object getAd(IAdConfig iAdConfig) {
        if (mGlobalAdMap != null) {
            return mGlobalAdMap.get(iAdConfig);
        }
        return null;
    }

    public static void releaseAdMap(Map<IAdConfig, Object> map) {
        ProfitAdUtils.releaseAdMap(map);
    }
}
