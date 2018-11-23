package com.app.soundrecord.main.drawer.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.soundrecord.R;
import com.app.soundrecord.application.XApplication;
import com.app.soundrecord.core.XCoreFactory;
import com.app.soundrecord.main.drawer.adapter.DialogAdapter;
import com.app.soundrecord.core.drawer.intf.IDialogManager;
import com.app.soundrecord.main.drawer.bean.DialogBean;
import com.app.soundrecord.util.SettingUtils;



import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DrawerDialog extends Dialog {

    public static final int VALUE_INT_SAMPLE = 1;
    public static final int VALUE_INT_FORMAT = 2;

    Context mContext;
    @BindView(R.id.dialog_tv_title)
    TextView mTvDialogTitle;
    @BindView(R.id.iv_close)
    ImageView mIvClose;
    @BindView(R.id.dialog_recycle_view)
    RecyclerView mDialogRecycleView;

    DialogAdapter mAdapter;
    IDialogManager mDialogManager;


    public DrawerDialog(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DrawerDialog(Context context, int nResThemeID) {
        super(context, nResThemeID);
        this.mContext = context;
        init();
    }


    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_drawer, null);
        setContentView(view);
        mTvDialogTitle = view.findViewById(R.id.dialog_tv_title);
        mIvClose = view.findViewById(R.id.iv_close);
        mDialogRecycleView = view.findViewById(R.id.dialog_recycle_view);
        mDialogManager = (IDialogManager) XCoreFactory.getInstance().createInstance(IDialogManager.class);
        mAdapter = new DialogAdapter(mContext,new ArrayList<DialogBean>());
        mDialogRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mDialogRecycleView.setAdapter(mAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(mContext,R.drawable.drawer_dialog_divider_item_decoration));
        mDialogRecycleView.addItemDecoration(decoration);
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mAdapter.setWidth(getWindow().getDecorView().getWidth());
                mAdapter.notifyDataSetChanged();
            }
        });

        mAdapter.setOnItemClickListener(new DialogAdapter.OnItemClickListener() {
            @Override
            public void ItemClickListener(int position, DialogBean bean,int type) {
                DrawerDialog.this.dismiss();
                switch (type){
                    case VALUE_INT_SAMPLE:
                        if (((XApplication)((Activity)mContext).getApplication()).isRecording()){
                            Toast.makeText(mContext, mContext.getString(R.string.do_not_update_sampling), Toast.LENGTH_SHORT).show();
                            break;
                        }
                        SettingUtils.putSampleRate(bean.getValue());
                        Toast.makeText(mContext, mContext.getString(R.string.toast_update_samle), Toast.LENGTH_SHORT).show();
                        break;
                    case VALUE_INT_FORMAT:
                        if (((XApplication)((Activity)mContext).getApplication()).isRecording()){
                            Toast.makeText(mContext, mContext.getString(R.string.do_not_update_format), Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if (bean.getValue() == SettingUtils.VALUE_INT_MP4 || bean.getValue() == SettingUtils.VALUE_INT_3GP){
                            Toast.makeText(mContext, mContext.getString(R.string.toast_update_format), Toast.LENGTH_SHORT).show();
                        }
                        SettingUtils.putCode(bean.getValue());
                        mDialogManager.change();
                        break;
                }

            }
        });
    }

    public void setmTitle(String title){
        if (TextUtils.isEmpty(title))
            return;
        mTvDialogTitle.setText(title);
    }

    public void setData(List<String> list,int type){
        if (list == null){
            return;
        }
        if (type == VALUE_INT_SAMPLE){
            mAdapter.setList(mDialogManager.setRecordData(list),type);
        }else{
            mAdapter.setList(mDialogManager.setFormatData(list),type);
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.iv_close)
    public void onViewClicked() {
        if (this.isShowing()) {
            this.dismiss();
        }
    }

    @Override
    public void dismiss() {
        if (this.isShowing()) {
            super.dismiss();
        }
    }
}
