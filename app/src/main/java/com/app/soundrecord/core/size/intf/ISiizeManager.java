package com.app.soundrecord.core.size.intf;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface ISiizeManager extends IXManager,IXObserver<ISizeListener> {

    void toUpdate(String siize);
}
