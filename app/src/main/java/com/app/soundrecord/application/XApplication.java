package com.app.soundrecord.application;

import android.app.Application;
import android.content.Context;

import com.app.soundrecord.BuildConfig;

import ulric.li.logic.alive.CrashTool;



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
