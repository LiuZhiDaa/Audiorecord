package com.app.soundrecord.core.size.impl;




import com.app.soundrecord.core.delete.intf.IDeleteListener;
import com.app.soundrecord.core.delete.intf.IDeletetManager;
import com.app.soundrecord.core.size.intf.ISiizeManager;
import com.app.soundrecord.core.size.intf.ISizeListener;

import ulric.li.xlib.impl.XObserver;

public class SizeManager extends XObserver<ISizeListener> implements ISiizeManager {

    public SizeManager(){
    }


    @Override
    public void toUpdate(String siize) {
        SizeManager.this.notify(iSizeListener -> iSizeListener.toUpdate(siize));
    }
}
