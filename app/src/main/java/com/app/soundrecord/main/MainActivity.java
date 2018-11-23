package com.app.soundrecord.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.soundrecord.Constants;
import com.app.soundrecord.R;
import com.app.soundrecord.application.XApplication;
import com.app.soundrecord.base.BaseActivity;
import com.app.soundrecord.core.XCoreFactory;
import com.app.soundrecord.core.allselect.intf.IAllseclectManager;
import com.app.soundrecord.core.config.intf.IConfigMgr;
import com.app.soundrecord.core.config.intf.IConfigMgrListener;
import com.app.soundrecord.core.toobar.intf.IToolbarListener;
import com.app.soundrecord.core.toobar.intf.IToolbarManager;
import com.app.soundrecord.main.drawer.AboutActivity;
import com.app.soundrecord.main.drawer.view.DrawerDialog;
import com.app.soundrecord.main.fragment.RecordFragment;
import com.app.soundrecord.main.fragment.VoiceFragment;
import com.app.soundrecord.util.InputUtil;
import com.app.soundrecord.view.MyToolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ulric.li.ad.intf.IAdConfig;
import ulric.li.utils.UtilsApp;

public class MainActivity extends BaseActivity {
    private static final String EMAIL = "";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.tool_bar)
    MyToolbar toolBar;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.tv_recording_quality)
    AppCompatTextView tvRecordingQuality;
    @BindView(R.id.tv_encoding_format)
    AppCompatTextView tvEncodingFormat;
    @BindView(R.id.tv_privacy)
    AppCompatTextView tvPrivacy;
    @BindView(R.id.tv_feed_back)
    AppCompatTextView tvFeedBack;
    @BindView(R.id.tv_about)
    AppCompatTextView tvAbout;
    @BindView(R.id.main_toolbar_ln)
    LinearLayout main_toolbar_ln;
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.main_tv_select)
    TextView main_tv_select;
    @BindView(R.id.fl_banner_ad)
    FrameLayout flBannerAd;
    @BindView(R.id.lin_null)
    LinearLayout linNull;
    private String[] mTitleArr;
    public List<Fragment> mFragmentList;
    private List<String> mPermissionList = new ArrayList<>();
    private final static int ACCESS_FINE_ERROR_CODE = 0x0245;
    private String[] recordingItem = null;
    private String[] formatItem = null;
    private boolean isSelectedState = false;
    private IToolbarManager miToolbarManager;
    private IAllseclectManager miAllseclectManager;
    private IConfigMgr mIConfigMgr;

    private boolean mIsNativeAdShowing = false;

    public static void launch(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
//        setPermissions(new String[]{
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_SETTINGS,
//                        Manifest.permission.RECORD_AUDIO},
//                ACCESS_FINE_ERROR_CODE);

        miAllseclectManager = (IAllseclectManager) XCoreFactory.getInstance().createInstance(IAllseclectManager.class);
        miToolbarManager = (IToolbarManager) XCoreFactory.getInstance().createInstance(IToolbarManager.class);
        miToolbarManager.addListener(iToolbarListener);
        mTitleArr = getResources().getStringArray(R.array.tabTitles);
        toolBar.setOnClickCloseListener(v -> {
            if (isSelectedState) {
                isSelectedState = false;
                main_tv_select.setText(R.string.main_tv_select);
                toolBar.setIcon(getResources().getDrawable(R.drawable.icon_drawer));

                miToolbarManager.toClose();
            } else {
                drawerLayout.openDrawer(Gravity.START);
                InputUtil.closeInput(MainActivity.this, v);
            }

        });
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new RecordFragment());
        mFragmentList.add(new VoiceFragment());
        viewpager.setAdapter(new MainAdapter(getSupportFragmentManager()));
        viewpager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewpager);
        viewpager.setCurrentItem(0);
        recordingItem = new String[]{getString(R.string.high), getString(R.string.normal), getString(R.string.lower)};
        formatItem = new String[]{getString(R.string.format_mkv), getString(R.string.format_mp4), getString(R.string.format_3gp)};

        mTvVersion.setText(UtilsApp.getMyAppVersionName(this));
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    main_toolbar_ln.setVisibility(View.VISIBLE);
                } else {
                    main_toolbar_ln.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (isSelectedState) {
                    isSelectedState = false;
                    main_tv_select.setText(R.string.main_tv_select);
                    toolBar.setIcon(getResources().getDrawable(R.drawable.icon_drawer));
                    miToolbarManager.toClose();
                }
            }
        });

        requestBannerAd();
        showBannerAd(false);
        mIConfigMgr = (IConfigMgr) XCoreFactory.getInstance().createInstance(IConfigMgr.class);
        mIConfigMgr.addListener(mIConfigMgrListener);
    }

    IConfigMgrListener mIConfigMgrListener = new IConfigMgrListener() {
        @Override
        public void onDetectLocalInfoAsyncComplete() {

        }

        @Override
        public void onRequestConfigAsync(boolean bSuccess) {
            if (!mHasBannerAdShow) {
                requestBannerAd();
            }
        }
    };


    @Override
    public ViewGroup getBannerAdLayout() {
        return flBannerAd;
    }

    @Override
    public String getBannerAdRequestScene() {
        return "main_create";
    }

    @Override
    public IAdConfig getBannerAdConfig() {
        return mIAdMgr.getAdConfig(IConfigMgr.VALUE_STRING_CONFIG_BANNER_MAIN_AD_KEY);
    }

    @OnClick({R.id.tv_recording_quality, R.id.tv_encoding_format, R.id.tv_privacy, R.id.tv_feed_back, R.id.tv_about, R.id.main_toolbar_ln, R.id.lin_null})
    public void onViewClicked(View view) {
        drawerLayout.closeDrawer(Gravity.START);
        DrawerDialog drawerDialog = null;
        switch (view.getId()) {
            case R.id.tv_recording_quality:
                if (drawerDialog == null) {
                    drawerDialog = new DrawerDialog(this, R.style.dialog);
                }
                drawerDialog.setmTitle("Recording Quality");
                drawerDialog.setData(Arrays.asList(recordingItem), DrawerDialog.VALUE_INT_SAMPLE);
                drawerDialog.show();
                break;
            case R.id.tv_encoding_format:
                if (drawerDialog == null) {
                    drawerDialog = new DrawerDialog(this, R.style.dialog);
                }
                drawerDialog.setmTitle("Eincoming Format");
                drawerDialog.setData(Arrays.asList(formatItem), DrawerDialog.VALUE_INT_FORMAT);
                drawerDialog.show();
                break;
            case R.id.tv_privacy:
                privacy();
                break;
            case R.id.tv_feed_back:
                feedback();
                break;
            case R.id.tv_about:
                AboutActivity.launch(this);
                break;
            case R.id.main_toolbar_ln:
                if (isSelectedState) {
                    //todo  全选
                    miAllseclectManager.toAllSelect();
                } else {
                    isSelectedState = true;

                    main_tv_select.setText(R.string.main_tv_selectall);
                    toolBar.setIcon(getResources().getDrawable(R.drawable.icon_back));
                    miToolbarManager.toOpen();
                }

                break;
            case R.id.lin_null:
                break;
        }
    }

    private int currPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    class MainAdapter extends FragmentPagerAdapter {
        public VoiceFragment currentFragment;

        public MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleArr[position];
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            currPosition = position;
            if (position == 2) {
                this.currentFragment = (VoiceFragment) object;
            }
            super.setPrimaryItem(container, position, object);
        }
    }
    private IToolbarListener iToolbarListener = new IToolbarListener() {
        @Override
        public void toClose() {
            super.toClose();
            isSelectedState = false;
            if (main_tv_select != null) {
                main_tv_select.setText(R.string.main_tv_select);
            }
            if (toolBar != null) {
                toolBar.setIcon(getResources().getDrawable(R.drawable.icon_drawer));
            }
        }

    };

    protected void showToast(String toastInfo) {
        Toast.makeText(this, toastInfo, Toast.LENGTH_LONG).show();
    }

    private static boolean mBackKeyPressed = false;//记录是否有首次按键

    @Override
    public void onBackPressed() {
        if (isSelectedState) {
            isSelectedState = true;
            miToolbarManager.toClose();
        } else if (((XApplication)getApplication()).isPlaying()){
            Toast.makeText(getApplicationContext(), R.string.string_stopplay,Toast.LENGTH_SHORT).show();
        }else if (((XApplication)getApplication()).isRecording()){
            Toast.makeText(getApplicationContext(), R.string.string_stoprecord,Toast.LENGTH_SHORT).show();
        } else {
            if (!mBackKeyPressed) {
                Toast.makeText(this, getResources().getText(R.string.main_exit), Toast.LENGTH_SHORT).show();
                mBackKeyPressed = true;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mBackKeyPressed = false;
                    }
                }, 2000);
            } else {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                   super.onBackPressed();
                }
            }
        }


    }

    private void feedback() {
        try {
            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:" + EMAIL));
            startActivity(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void privacy() {
        try {
            Uri uri = Uri.parse(Constants.VALUE_STRING_PRIVACY_POLICY_URL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIConfigMgr != null) {
            mIConfigMgr.removeListener(mIConfigMgrListener);
        }
        if (miToolbarManager != null) {
            miToolbarManager.removeListener(iToolbarListener);
        }
    }
}
