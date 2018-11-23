package com.app.soundrecord.core.allselect.intf;



import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface IAllseclectManager extends IXManager,IXObserver<IAllSelectListener> {

    void toAllSelect();
}
