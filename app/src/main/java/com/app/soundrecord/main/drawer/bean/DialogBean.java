package com.app.soundrecord.main.drawer.bean;

import ulric.li.xlib.intf.IXObject;

public class DialogBean implements IXObject {
    private String mName;
    private int value;
    private boolean mIsChecked;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public boolean ismIsChecked() {
        return mIsChecked;
    }

    public void setmIsChecked(boolean mIsChecked) {
        this.mIsChecked = mIsChecked;
    }
}
