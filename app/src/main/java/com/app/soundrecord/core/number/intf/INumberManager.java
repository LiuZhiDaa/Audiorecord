package com.app.soundrecord.core.number.intf;


import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface INumberManager extends IXManager,IXObserver<INumberListener> {

    void toUpdate();
}
