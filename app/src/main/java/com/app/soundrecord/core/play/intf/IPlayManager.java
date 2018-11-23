package com.app.soundrecord.core.play.intf;



import com.app.soundrecord.bean.RecordingItem;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface IPlayManager extends IXManager,IXObserver<IPlayListener> {

    void toShow(RecordingItem item);
    void toClose();
}
