package com.app.soundrecord.application;

import android.app.Application;

import com.app.soundrecord.BuildConfig;
import com.app.soundrecord.Constants;
import com.app.soundrecord.core.XCoreFactory;


import ulric.li.XLibFactory;

import ulric.li.logic.alive.InstallReferrerReceiver;
import ulric.li.utils.UtilsEncrypt;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.utils.UtilsNetwork;


public class XConfig {
    private static final boolean VALUE_BOOLEAN_IS_NEED_SEND_LOG = !BuildConfig.DEBUG;
    private static final boolean VALUE_BOOLEAN_IS_NEED_LOCAL_LOG = BuildConfig.DEBUG;

    public static final String VALUE_STRING_REFERRER_URL = "/api/v3/referrer/"+Constants.VALUE_STRING_DB_NAME;
    public static final String VALUE_STRING_STATISTICS_LOG_URL = "/api/v3/statistics_log/"+Constants.VALUE_STRING_DB_NAME;
    public static final String VALUE_STRING_CRASH_LOG_URL = "/api/v3/crash_log/"+Constants.VALUE_STRING_DB_NAME;
    public static final String VALUE_STRING_CONFIG_URL = "/api/v3/config/"+Constants.VALUE_STRING_DB_NAME;
    public static final String VALUE_STRING_COUNTRY_URL = "/api/v3/country/"+Constants.VALUE_STRING_DB_NAME;

    public static void init(Application application) {
        XLibFactory.setApplication(application);
        XCoreFactory.setApplication(application);
    }
}
