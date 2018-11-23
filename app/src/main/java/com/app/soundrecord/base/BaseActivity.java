package com.app.soundrecord.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.app.soundrecord.core.XCoreFactory;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseActivity extends BaseAdActivity {
    private Unbinder mBind;
    protected Context mApplicationContext= XCoreFactory.getApplication();
    private long mTimeOpen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        mBind = ButterKnife.bind(this);
        initView();
    }

    protected abstract @LayoutRes int getLayoutResId();

    protected abstract void initView();

    protected void initToolbar(Toolbar toolbar){
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTimeOpen = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        long duration = System.currentTimeMillis() - mTimeOpen;
        onRemainDurationCalculated(duration);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBind != null) {
            mBind.unbind();
        }
    }

    /**
     * 页面停留时间
     * @param duration
     */
    protected void onRemainDurationCalculated(long duration){

    }

    protected void goActivity(Class<? extends Activity> clazz){
        startActivity(new Intent(this,clazz));
    }

}
