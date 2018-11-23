package com.app.soundrecord.out.page;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.app.soundrecord.R;

import ulric.li.xout.core.scene.intf.IOutSceneMgr;
import ulric.li.xout.main.base.OutBaseActivity;

/**
 * Created by WangYu on 2018/7/2.
 */
public class TestActivity extends OutBaseActivity {
    public static void start(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, TestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoosterActivity.start(TestActivity.this, IOutSceneMgr.VALUE_STRING_BOOST_SCENE_TYPE,BoosterActivity.class);
            }
        });
        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkActivity.start(TestActivity.this, IOutSceneMgr.VALUE_STRING_NETWORK_SCENE_TYPE,NetworkActivity.class);
            }
        });
        findViewById(R.id.bt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoolActivity.start(TestActivity.this,IOutSceneMgr.VALUE_STRING_COOL_SCENE_TYPE,CoolActivity.class);
            }
        });
        findViewById(R.id.bt4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CleanerActivity.start(TestActivity.this,IOutSceneMgr.VALUE_STRING_CLEAN_SCENE_TYPE,CleanerActivity.class);
            }
        });
        findViewById(R.id.bt5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                OutNotifyActivity.start(TestActivity.this);
            }
        });
        findViewById(R.id.bt6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BatteryActivity.start(TestActivity.this,"charge",BatteryActivity.class);
            }
        });
        findViewById(R.id.bt7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BatteryEndActivity.start(TestActivity.this,"charge_complete",BatteryEndActivity.class);
            }
        });
        findViewById(R.id.bt8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MissCallActivity.start(TestActivity.this);

            }
        });
        findViewById(R.id.bt9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BatteryOptimizeActivity.start(TestActivity.this,getSceneType(),BatteryOptimizeActivity.class);
            }
        });
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected String getSceneType() {
        return null;
    }

}
