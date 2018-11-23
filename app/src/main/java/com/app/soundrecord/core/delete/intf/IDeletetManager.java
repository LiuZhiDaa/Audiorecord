package com.app.soundrecord.core.delete.intf;





import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface IDeletetManager extends IXManager,IXObserver<IDeleteListener> {

    void toDelete();
}
