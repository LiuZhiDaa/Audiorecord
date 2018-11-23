package com.app.soundrecord.core.number.impl;




import com.app.soundrecord.core.number.intf.INumberListener;
import com.app.soundrecord.core.number.intf.INumberManager;


import ulric.li.xlib.impl.XObserver;

public class NumberManager extends XObserver<INumberListener> implements INumberManager {
    @Override
    public void toUpdate() {
        NumberManager.this.notify(iNumberListener -> iNumberListener.toUpdate());
    }
}
