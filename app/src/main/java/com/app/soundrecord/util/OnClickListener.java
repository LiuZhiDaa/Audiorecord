package com.app.soundrecord.util;

import android.view.View;


public abstract class OnClickListener implements View.OnClickListener {
    private long mExitTime = 0;

    @Override
    public void onClick(View view) {
        if ((System.currentTimeMillis() - mExitTime) > 1000) {
            mExitTime = System.currentTimeMillis();
            myOnClickListener(view);
        }
    }

    protected abstract void myOnClickListener(View v);
}
