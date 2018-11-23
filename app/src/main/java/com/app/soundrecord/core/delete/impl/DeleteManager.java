package com.app.soundrecord.core.delete.impl;




import com.app.soundrecord.core.delete.intf.IDeleteListener;
import com.app.soundrecord.core.delete.intf.IDeletetManager;

import ulric.li.xlib.impl.XObserver;

public class DeleteManager extends XObserver<IDeleteListener> implements IDeletetManager {

    public DeleteManager(){
    }

    @Override
    public void toDelete() {
        DeleteManager.this.notify(IDeleteListener::toDelete);
    }




}
