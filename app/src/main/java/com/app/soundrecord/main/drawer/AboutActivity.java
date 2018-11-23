package com.app.soundrecord.main.drawer;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.soundrecord.R;
import com.app.soundrecord.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import ulric.li.utils.UtilsApp;

/**
 * @author gongguan
 * @time 2018/7/5 上午12:32
 */
public class AboutActivity extends BaseActivity {

    private Unbinder mUnbind;

    public static void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.about_app_name)
    TextView mTvAppName;
    @BindView(R.id.setting_title)
    TextView mTvSettingTitle;
    @BindView(R.id.about_app_version)
    TextView mTvAboutAPPVersion;
    @BindView(R.id.setting_back)
    ImageView mIvBack;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        String strVersion = UtilsApp.getMyAppVersionName(this);
        mTvAppName.setText(getString(R.string.app_name));
        mTvAboutAPPVersion.setText("v" + strVersion);
        mTvSettingTitle.setText(getResources().getString(R.string.about));
        mIvBack.setImageResource(R.drawable.icon_back);
    }

    @OnClick({ R.id.about_page_protocol_tv, R.id.setting_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbind != null) {
            mUnbind.unbind();
        }
    }

}
