package com.app.soundrecord.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.app.soundrecord.BuildConfig;
import com.app.soundrecord.core.XCoreFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import ulric.li.XLibFactory;
import ulric.li.logic.alive.CrashTool;
import ulric.li.tool.intf.IHttpTool;
import ulric.li.tool.intf.IHttpToolListener;
import ulric.li.tool.intf.IHttpToolResult;
import ulric.li.utils.UtilsAlive;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.utils.UtilsNetwork;
import ulric.li.xout.core.status.IAppStatusMgr;

public class XApplication extends Application {
    private static Context sInstance;
    private boolean isRecording=false;
    private boolean isPlaying = false;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        // 全局异常钩子
       if (!BuildConfig.DEBUG) {
            CrashTool.init();
        }
        // 初始化配置
        XConfig.init(this);

        UtilsAlive.init(this);
        // 逻辑初始化
        initLogic();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private void initLogic() {
        initCountry();
        IAppStatusMgr appStatusMgr = (IAppStatusMgr) XCoreFactory.getInstance().createInstance(IAppStatusMgr.class);
//        appStatusMgr.init();
    }

    private void initCountry() {
        String strCountry = UtilsEnv.getCountry();
        if (TextUtils.isEmpty(strCountry)) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            strCountry = sp.getString("country_code", "");
            if (TextUtils.isEmpty(strCountry)) {
                IHttpTool iHttpTool = (IHttpTool) XLibFactory.getInstance().createInstance(IHttpTool.class);
                iHttpTool.requestToBufferByPostAsync(UtilsNetwork.getURL(XConfig.VALUE_STRING_COUNTRY_URL), null, null, null, new IHttpToolListener() {
                    @Override
                    public void onRequestToBufferByPostAsyncComplete(String strURL, Map<String, String> mapParam, Object objectTag, IHttpToolResult iHttpToolResult) {
                        if (null == iHttpToolResult || !iHttpToolResult.isSuccess() || null == iHttpToolResult.getBuffer()) {
                            UtilsLog.statisticsLog("application", "get_country_code_fail", null);
                            return;
                        }

                        int nCode = UtilsNetwork.VALUE_INT_FAIL_CODE;
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(new String(iHttpToolResult.getBuffer()));
                            nCode = UtilsJson.JsonUnserialization(jsonObject, "code", nCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String strCountry = null;
                        if (UtilsNetwork.VALUE_INT_SUCCESS_CODE == nCode) {
                            try {
                                strCountry = jsonObject.getString("country_code");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (!TextUtils.isEmpty(strCountry)) {
                            UtilsLog.statisticsLog("application", "get_country_code_success", jsonObject);
                            sp.edit().putString("country_code", strCountry).apply();
                            UtilsLog.addUserInfo("country_code", strCountry);
                            UtilsEnv.setCountry(strCountry);
                        } else {
                            UtilsLog.statisticsLog("application", "get_country_code_empty", null);
                        }
                    }
                });

                return;
            }
        }

        UtilsLog.addUserInfo("country_code", strCountry);
        UtilsEnv.setCountry(strCountry);
    }

    public static Context getContext(){
        return sInstance;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }
    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
