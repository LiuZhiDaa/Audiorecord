package com.app.soundrecord.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.soundrecord.application.XApplication;


public class SettingUtils {
    // 1: mkv  2:mp4  3:3gp
    //44100，16000，8000
    private static final String VALUE_STRING_SETTING_CODE = "setting_code";
    private static final String VALUE_STRING_SAMPLING_RATE = "sampling_rate";
    private static final String  positions = "settinng_";
    private static final String  nameNumber = "settinng_nameNumber";



    public static final int VALUE_INT_MKV = 1;
    public static final int VALUE_INT_MP4 = 2;
    public static final int VALUE_INT_3GP = 3;

    public static final int VALUE_INT_44100 = 44100;
    public static final int VALUE_INT_16000 = 16000;
    public static final int VALUE_INT_8000 = 8000;


    public static void putCode(int sex){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XApplication.getContext());
        sp.edit().putInt(VALUE_STRING_SETTING_CODE,sex).apply();
    }

    public static int getCode(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XApplication.getContext());
        return sp.getInt(VALUE_STRING_SETTING_CODE,1);
    }

    public static void putSampleRate(int sex){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XApplication.getContext());
        sp.edit().putInt(VALUE_STRING_SAMPLING_RATE,sex).apply();
    }

    public static int getSampleRate(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XApplication.getContext());
        return sp.getInt(VALUE_STRING_SAMPLING_RATE,16000);
    }

    public static void putpositions(int sex){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XApplication.getContext());
        sp.edit().putInt(positions,sex).apply();
    }

    public static int getpositions(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XApplication.getContext());
        return sp.getInt(positions,-1);
    }

    public static void putnameNumber(int num){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XApplication.getContext());
        sp.edit().putInt(nameNumber,num).apply();
    }
    public static int getnameNumber(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XApplication.getContext());
        return sp.getInt(nameNumber,0);
    }




}
