package com.app.soundrecord.core.config.intf;

public interface IConfigMgrListener {
    void onDetectLocalInfoAsyncComplete();

    void onRequestConfigAsync(boolean bSuccess);
}
