package com.app.soundrecord.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.app.soundrecord.util.UtilsAd;
import com.facebook.ads.AdView;
import com.facebook.ads.NativeAd;

import java.util.HashMap;
import java.util.Map;

import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdConfig;
import ulric.li.ad.intf.IAdMgr;
import ulric.li.ad.intf.IAdMgrListener;
import ulric.li.ad.intf.IFastAdInterface;
import ulric.li.utils.UtilsLog;

/**
 * Created by WangYu on 2018/11/19.
 */
public class BaseAdFragment extends Fragment implements IFastAdInterface {

    protected IAdMgr mIAdMgr;
    private Map<IAdConfig, Object> mAdMap;
    protected boolean mHasBannerAdShow=false;
    protected boolean mHasNativeAdShow=false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIAdMgr = (IAdMgr) XProfitFactory.getInstance().createInstance(IAdMgr.class);
        mIAdMgr.addListener(mAdMgrListener);
        mAdMap = new HashMap<>();
    }

    protected void requestBannerAd() {
        UtilsAd.requestAd(getBannerAdConfig(), getBannerAdRequestScene());
    }

    protected void requestNativeAd() {
        UtilsAd.requestAd(getNativeAdConfig(), getNativeAdRequestScene());
    }

    protected void requestInterstitialAd() {
        UtilsAd.requestAd(getInterstitialAdConfig(), getInterstitialAdRequestScene());
    }

    protected void showInterstitialAd() {
        UtilsAd.showInterstitialAd(getInterstitialAdConfig(), getInterstitialAdShowScene());
    }

    @Override
    public IAdConfig getNativeAdConfig() {
        return null;
    }

    @Override
    public ViewGroup getNativeAdLayout() {
        return null;
    }

    @Override
    public String getNativeAdRequestScene() {
        return "";
    }

    @Override
    public boolean showNativeAd(boolean withAnimation) {
        if (getNativeAdLayout() == null) {
            return false;
        }
        if (mIAdMgr.isNativeAdLoaded(getNativeAdConfig())) {
            Object nativeAd = mAdMap.get(getNativeAdConfig());
            if (nativeAd instanceof NativeAd) {
                ((NativeAd) nativeAd).destroy();
                mAdMap.remove(getNativeAdConfig());
            }
            Object newNativeAd = UtilsAd.showAdView(getNativeAdLayout(), getNativeAdConfig(), withAnimation);
            if (newNativeAd != null) {
                mAdMap.put(getNativeAdConfig(), newNativeAd);
                mHasNativeAdShow=true;
            }
            return newNativeAd!=null;
        }
        return false;
    }

    @Override
    public IAdConfig getBannerAdConfig() {
        return null;
    }

    @Override
    public ViewGroup getBannerAdLayout() {
        return null;
    }

    @Override
    public String getBannerAdRequestScene() {
        return null;
    }

    @Override
    public boolean showBannerAd(boolean withAnimation) {
        if (getBannerAdLayout() == null) {
            return false;
        }
        if (mIAdMgr.isBannerAdLoaded(getBannerAdConfig())) {
            Object bannerAd = mAdMap.get(getBannerAdConfig());
            Object newBannerAd = UtilsAd.showAdView(getBannerAdLayout(), getBannerAdConfig(), withAnimation);
            if (bannerAd != null&&bannerAd!=newBannerAd) {
                if (bannerAd instanceof AdView) {
                    ((AdView) bannerAd).destroy();
                } else if (bannerAd instanceof com.google.android.gms.ads.AdView) {
                    ((com.google.android.gms.ads.AdView) bannerAd).destroy();
                }
                mAdMap.remove(getBannerAdConfig());
            }
            if (newBannerAd != null) {
                mAdMap.put(getBannerAdConfig(), newBannerAd);
                mHasBannerAdShow=true;
            }
            return newBannerAd!=null;
        }
        return false;
    }

    private IAdMgrListener mAdMgrListener = new IAdMgrListener() {
        @Override
        public void onAdLoaded(IAdConfig iAdConfig) {
            super.onAdLoaded(iAdConfig);
            if (iAdConfig == null || iAdConfig.getAdKey() == null) {
                return;
            }
            BaseAdFragment.this.onAdLoaded(iAdConfig);
        }

        @Override
        public void onAdOpened(IAdConfig iAdConfig) {
            super.onAdOpened(iAdConfig);
            if (iAdConfig==null) {
                return;
            }
            BaseAdFragment.this.onAdOpened(iAdConfig);
        }
    };

    protected void onAdLoaded(IAdConfig iAdConfig) {
        if (iAdConfig.equals(getNativeAdConfig())) {
            showNativeAd(true);
        } else if (iAdConfig.equals(getBannerAdConfig())) {
            showBannerAd(true);
        }
    }

    protected void onAdOpened(IAdConfig adConfig) {

    }

    @Override
    public IAdConfig getInterstitialAdConfig() {
        return null;
    }

    @Override
    public String getInterstitialAdShowScene() {
        return "";
    }

    @Override
    public String getInterstitialAdRequestScene() {
        return "";
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mIAdMgr != null) {
            mIAdMgr.removeListener(mAdMgrListener);
        }
        UtilsAd.releaseAdMap(mAdMap);
        UtilsLog.sendLog();
    }
}
