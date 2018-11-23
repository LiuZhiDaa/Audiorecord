package com.app.soundrecord.core.drawer.impl;

import android.content.Context;
import com.app.soundrecord.R;
import com.app.soundrecord.core.XCoreFactory;
import com.app.soundrecord.core.drawer.intf.IDoalogListener;
import com.app.soundrecord.main.drawer.bean.DialogBean;
import com.app.soundrecord.core.drawer.intf.IDialogManager;
import com.app.soundrecord.util.SettingUtils;
import java.util.ArrayList;
import java.util.List;
import ulric.li.xlib.impl.XObserver;

public class DialogManager extends XObserver<IDoalogListener> implements IDialogManager {
    Context mContext;
    public DialogManager(){
        this.mContext = XCoreFactory.getApplication();
    }

    //转换格式
    @Override
    public List<DialogBean> setFormatData(List<String> list) {
        List<DialogBean> mList = new ArrayList<>();

        if (list != null && list.size() > 0) {
            for(String str : list){
                DialogBean bean = new DialogBean();
                bean.setmName(str);
                if(list.indexOf(str) == SettingUtils.getCode()-1){
                    bean.setmIsChecked(true);
                }else{
                    bean.setmIsChecked(false);
                }
                bean.setValue(list.indexOf(str)+1);
                mList.add(bean);
            }
        }

        return mList;
    }

    //转换采样率数据
    @Override
    public List<DialogBean> setRecordData(List<String> list) {
        List<DialogBean> mList = new ArrayList<>();

        if (list != null && list.size() > 0) {
            for(String str : list){
                DialogBean bean = new DialogBean();
                bean.setmName(str);
                switch (list.indexOf(str)){
                    case 0:
                        bean.setValue(SettingUtils.VALUE_INT_44100);
                        break;
                    case 1:
                        bean.setValue(SettingUtils.VALUE_INT_16000);
                        break;
                    case 2:
                        bean.setValue(SettingUtils.VALUE_INT_8000);
                        break;
                }
                if(SettingUtils.getSampleRate() == SettingUtils.VALUE_INT_8000 && str.equals(mContext.getResources().getString(R.string.lower))){
                    bean.setmIsChecked(true);
                }else if(SettingUtils.getSampleRate() == SettingUtils.VALUE_INT_16000 && str.equals(mContext.getResources().getString(R.string.normal))){
                    bean.setmIsChecked(true);
                }else if(SettingUtils.getSampleRate() == SettingUtils.VALUE_INT_44100 && str.equals(mContext.getResources().getString(R.string.high))){
                    bean.setmIsChecked(true);
                }else{
                    bean.setmIsChecked(false);
                }
                mList.add(bean);
            }
        }

        return mList;
    }

    @Override
    public void change() {
        DialogManager.this.notify(iDoalogListener -> iDoalogListener.onChange());
    }

}
