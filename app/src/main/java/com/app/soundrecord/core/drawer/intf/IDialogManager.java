package com.app.soundrecord.core.drawer.intf;

import com.app.soundrecord.main.drawer.bean.DialogBean;

import java.util.List;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface IDialogManager extends IXManager,IXObserver<IDoalogListener> {
    List<DialogBean> setFormatData(List<String> list);
    List<DialogBean> setRecordData(List<String> list);
    void change();

}
