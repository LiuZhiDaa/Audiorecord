package com.app.soundrecord.core.config.intf;

import ulric.li.mode.intf.IXJsonSerialization;
import ulric.li.xlib.intf.IXManager;

public interface ICloudConfig extends IXManager, IXJsonSerialization {
    boolean isAdEnable();
}
