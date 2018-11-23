package com.app.soundrecord.core;

import android.content.Context;

import com.app.soundrecord.core.allselect.impl.AllSelectManager;
import com.app.soundrecord.core.allselect.intf.IAllseclectManager;
import com.app.soundrecord.core.config.impl.CloudConfig;
import com.app.soundrecord.core.config.impl.ConfigMgr;
import com.app.soundrecord.core.config.intf.ICloudConfig;
import com.app.soundrecord.core.config.intf.IConfigMgr;
import com.app.soundrecord.core.delete.impl.DeleteManager;
import com.app.soundrecord.core.delete.intf.IDeletetManager;
import com.app.soundrecord.core.drawer.impl.DialogManager;
import com.app.soundrecord.core.drawer.intf.IDialogManager;

import com.app.soundrecord.core.number.impl.NumberManager;
import com.app.soundrecord.core.number.intf.INumberManager;
import com.app.soundrecord.core.play.impl.PlayManager;
import com.app.soundrecord.core.play.intf.IPlayManager;
import com.app.soundrecord.core.size.impl.SizeManager;
import com.app.soundrecord.core.size.intf.ISiizeManager;
import com.app.soundrecord.core.toobar.impl.ToolbarManager;
import com.app.soundrecord.core.toobar.intf.IToolbarManager;

import java.util.HashMap;

import ulric.li.xlib.impl.XFactory;
import ulric.li.xlib.intf.IXFactory;
import ulric.li.xlib.intf.IXObject;

public class XCoreFactory extends XFactory {
    private static IXFactory sIXFactory = null;
    private static Context sContext = null;

    public static IXFactory getInstance() {
        if (sIXFactory == null) {
            synchronized (XCoreFactory.class) {
                if (sIXFactory == null)
                    sIXFactory = new XCoreFactory();
            }
        }

        return sIXFactory;
    }

    public static void setApplication(Context context) {
        XCoreFactory.sContext = context;
    }

    public static Context getApplication() {
        return sContext;
    }

    {
        mXFactoryInterfaceMap = new HashMap<>();
        mXFactoryInterfaceMap.put(IDialogManager.class, new XFactoryImplementMap(new Class<?>[]{DialogManager.class}, new IXObject[]{null}));
        mXFactoryInterfaceMap.put(IToolbarManager.class, new XFactoryImplementMap(new Class<?>[]{ToolbarManager.class}, new IXObject[]{null}));
        mXFactoryInterfaceMap.put(IAllseclectManager.class, new XFactoryImplementMap(new Class<?>[]{AllSelectManager.class}, new IXObject[]{null}));
        mXFactoryInterfaceMap.put(IDeletetManager.class, new XFactoryImplementMap(new Class<?>[]{DeleteManager.class}, new IXObject[]{null}));
        mXFactoryInterfaceMap.put(ISiizeManager.class, new XFactoryImplementMap(new Class<?>[]{SizeManager.class}, new IXObject[]{null}));
        mXFactoryInterfaceMap.put(IConfigMgr.class, new XFactoryImplementMap(new Class<?>[]{ConfigMgr.class}, new IXObject[]{null}));
        mXFactoryInterfaceMap.put(ICloudConfig.class, new XFactoryImplementMap(new Class<?>[]{CloudConfig.class}, new IXObject[]{null}));
        mXFactoryInterfaceMap.put(IPlayManager.class, new XFactoryImplementMap(new Class<?>[]{PlayManager.class}, new IXObject[]{null}));
        mXFactoryInterfaceMap.put(INumberManager.class, new XFactoryImplementMap(new Class<?>[]{NumberManager.class}, new IXObject[]{null}));

    }
}
