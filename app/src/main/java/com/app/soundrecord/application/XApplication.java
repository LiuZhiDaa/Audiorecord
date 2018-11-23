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


        // 逻辑初始化
        initLogic();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private void initLogic() {


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
