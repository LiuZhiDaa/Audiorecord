package com.app.soundrecord.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;


import ulric.li.utils.UtilsLog;

/**
 * Created by WangYu on 2018/11/19.
 */
public class BaseAdFragment extends Fragment  {


    protected boolean mHasBannerAdShow=false;
    protected boolean mHasNativeAdShow=false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();

        UtilsLog.sendLog();
    }
}
