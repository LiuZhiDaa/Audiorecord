package com.app.soundrecord.core.toobar.intf;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface IToolbarManager extends IXManager,IXObserver<IToolbarListener> {

    void toClose();
    void toOpen();
}
