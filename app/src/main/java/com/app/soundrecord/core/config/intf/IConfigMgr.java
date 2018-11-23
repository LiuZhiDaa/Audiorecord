package com.app.soundrecord.core.config.intf;

import com.app.soundrecord.Constants;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface IConfigMgr extends IXManager, IXObserver<IConfigMgrListener> {
    boolean detectLocalInfoAsync();

    boolean requestConfigAsync();

    String VALUE_STRING_CONFIG_BANNER_MAIN_AD_KEY = Constants.VALUE_STRING_APP_NAME+"_banner_main";

}
