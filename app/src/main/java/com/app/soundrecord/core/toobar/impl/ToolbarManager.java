package com.app.soundrecord.core.toobar.impl;

import com.app.soundrecord.core.toobar.intf.IToolbarListener;
import com.app.soundrecord.core.toobar.intf.IToolbarManager;

import ulric.li.xlib.impl.XObserver;

public class ToolbarManager extends XObserver<IToolbarListener> implements IToolbarManager {

    public ToolbarManager(){
    }

    @Override
    public void toClose() {
        ToolbarManager.this.notify(IToolbarListener::toClose);
    }

    @Override
    public void toOpen() {
        ToolbarManager.this.notify(IToolbarListener::toOpen);
    }


}
