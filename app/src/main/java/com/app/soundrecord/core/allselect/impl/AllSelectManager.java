package com.app.soundrecord.core.allselect.impl;



import com.app.soundrecord.core.allselect.intf.IAllSelectListener;
import com.app.soundrecord.core.allselect.intf.IAllseclectManager;

import ulric.li.xlib.impl.XObserver;

public class AllSelectManager extends XObserver<IAllSelectListener> implements IAllseclectManager {

    public AllSelectManager(){
    }

    @Override
    public void toAllSelect() {
        AllSelectManager.this.notify(iAllSelectListener -> iAllSelectListener.toAllSelect());
    }




}
