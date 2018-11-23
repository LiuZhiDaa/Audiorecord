package com.app.soundrecord.core.play.intf;

import com.app.soundrecord.bean.RecordingItem;

/**
 * Created by WangYu on 2018/10/12.
 */
public abstract class IPlayListener {

   public void toShow(RecordingItem item){}
   public void toClose(){}


}
