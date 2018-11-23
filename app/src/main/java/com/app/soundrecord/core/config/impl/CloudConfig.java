package com.app.soundrecord.core.config.impl;


import com.app.soundrecord.core.config.intf.ICloudConfig;

import org.json.JSONObject;

import ulric.li.utils.UtilsJson;

public class CloudConfig implements ICloudConfig {
    private boolean mIsAdEnable = false;

    @Override
    public JSONObject Serialization() {
        return null;
    }

    @Override
    public void Deserialization(JSONObject jsonObject) {
        if (null == jsonObject)
            return;

        mIsAdEnable = UtilsJson.JsonUnserialization(jsonObject, "ad_enable", false);
    }

    @Override
    public boolean isAdEnable() {
        return mIsAdEnable;
    }
}
