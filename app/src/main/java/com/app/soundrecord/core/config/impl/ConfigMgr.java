package com.app.soundrecord.core.config.impl;

import android.content.Context;
import android.os.Message;

import com.app.soundrecord.application.XConfig;
import com.app.soundrecord.core.XCoreFactory;
import com.app.soundrecord.core.config.intf.ICloudConfig;
import com.app.soundrecord.core.config.intf.IConfigMgr;
import com.app.soundrecord.core.config.intf.IConfigMgrListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ulric.li.XLibFactory;
import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdMgr;
import ulric.li.tool.intf.IHttpTool;
import ulric.li.tool.intf.IHttpToolResult;
import ulric.li.utils.UtilsApp;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.utils.UtilsNetwork;
import ulric.li.utils.UtilsTime;
import ulric.li.xlib.impl.XObserverAutoRemove;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;
import ulric.li.xout.core.config.impl.OutConfig;
import ulric.li.xout.core.config.intf.IOutConfig;

public class ConfigMgr extends XObserverAutoRemove<IConfigMgrListener> implements IConfigMgr {
    private Context mContext = null;
    private IXThreadPool mIXThreadPool = null;
    private IHttpTool mIHttpTool = null;

    private IAdMgr mIAdMgr = null;

    private String VALUE_STRING_CONFIG_FILE = "config.dat";

    public ConfigMgr() {
        mContext = XCoreFactory.getApplication();
        _init();
    }

    private void _init() {
        mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        mIHttpTool = (IHttpTool) XLibFactory.getInstance().createInstance(IHttpTool.class);
        mIAdMgr = (IAdMgr) XProfitFactory.getInstance().createInstance(IAdMgr.class);
    }

    @Override
    public boolean detectLocalInfoAsync() {
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                if (mIAdMgr.isNeedLoadConfig()) {
                    JSONObject jsonObject = UtilsJson.loadJsonFromFileWithDecrypt(mContext, VALUE_STRING_CONFIG_FILE);
                    loadConfig(jsonObject);
                }
            }

            @Override
            public void onTaskComplete() {
                for (IConfigMgrListener listener : getListenerList())
                    listener.onDetectLocalInfoAsyncComplete();
            }

            @Override
            public void onMessage(Message msg) {
            }
        });

        return true;
    }

    @Override
    public boolean requestConfigAsync() {
        final int[] nCode = new int[]{UtilsNetwork.VALUE_INT_FAIL_CODE};
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                JSONObject jsonObjectParam = new JSONObject();
                UtilsJson.JsonSerialization(jsonObjectParam, "country_code", UtilsEnv.getCountry());
                UtilsJson.JsonSerialization(jsonObjectParam, "time", UtilsTime.getDateStringHh(System.currentTimeMillis()));
                UtilsJson.JsonSerialization(jsonObjectParam, "app_version", String.valueOf(UtilsApp.getMyAppVersionCode(mContext)));

                Map<String, String> mapParam = new HashMap<>();
                mapParam.put("data", jsonObjectParam.toString());
                IHttpToolResult iHttpToolResult = mIHttpTool.requestToBufferByPostSync(UtilsNetwork.getURL(XConfig.VALUE_STRING_CONFIG_URL), mapParam, null);
                if (null == iHttpToolResult || !iHttpToolResult.isSuccess() || null == iHttpToolResult.getBuffer()) {
                    JSONObject jsonObjectDebug = new JSONObject();
                    try {
                        jsonObjectDebug.put("success", false);
                        jsonObjectDebug.put("exception", iHttpToolResult.getException());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    UtilsLog.statisticsLog("debug", "request_config", jsonObjectDebug);
                    return;
                }

                JSONObject jsonObject = null;
                JSONObject jsonObjectData = null;
                try {
//                    Log.i("wangyu", new String(iHttpToolResult.getBuffer()));
                    jsonObject = new JSONObject(new String(iHttpToolResult.getBuffer()));
                    nCode[0] = UtilsJson.JsonUnserialization(jsonObject, "code", nCode[0]);
                    jsonObjectData = jsonObject.getJSONObject("data");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (UtilsNetwork.VALUE_INT_SUCCESS_CODE == nCode[0]) {
                    loadConfig(jsonObjectData);

                    UtilsJson.saveJsonToFileWithEncrypt(mContext, VALUE_STRING_CONFIG_FILE, jsonObjectData);
                }

                JSONObject jsonObjectDebug = new JSONObject();
                try {
                    jsonObjectDebug.put("success", iHttpToolResult.isSuccess());
                    jsonObjectDebug.put("exception", iHttpToolResult.getException());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("debug", "request_config", jsonObjectDebug);
            }

            @Override
            public void onTaskComplete() {
                for (IConfigMgrListener listener : getListenerList()) {
                    listener.onRequestConfigAsync(UtilsNetwork.VALUE_INT_SUCCESS_CODE == nCode[0]);
                }
            }

            @Override
            public void onMessage(Message msg) {
            }
        });

        return true;
    }

    private boolean loadConfig(JSONObject jsonObject) {
        if (null == jsonObject)
            return false;

        try {
            if (jsonObject.has("cloud")) {
                UtilsJson.JsonUnserialization(jsonObject, "cloud", ICloudConfig.class, CloudConfig.class);
            }

            if (jsonObject.has("ad")) {
                mIAdMgr.loadConfig(jsonObject.getJSONObject("ad"));
            }

            if (jsonObject.has("out")) {
                UtilsJson.JsonUnserialization(jsonObject, "out", IOutConfig.class, OutConfig.class);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
