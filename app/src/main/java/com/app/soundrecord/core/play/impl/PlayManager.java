package com.app.soundrecord.core.play.impl;






import com.app.soundrecord.bean.RecordingItem;
import com.app.soundrecord.core.play.intf.IPlayListener;
import com.app.soundrecord.core.play.intf.IPlayManager;

import ulric.li.xlib.impl.XObserver;

public class PlayManager extends XObserver<IPlayListener> implements IPlayManager {

    public PlayManager(){}


    @Override
    public void toShow(RecordingItem item) {
        PlayManager.this.notify(iPlayListener -> iPlayListener.toShow(item));
    }

    @Override
    public void toClose() {
        PlayManager.this.notify(IPlayListener::toClose);
    }
}
