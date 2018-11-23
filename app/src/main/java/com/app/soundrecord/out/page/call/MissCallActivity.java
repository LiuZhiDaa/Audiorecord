package com.app.soundrecord.out.page.call;

import android.annotation.SuppressLint;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.soundrecord.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.main.base.OutBaseActivity;

/**
 * Created by wanghailong on 2018/7/3.
 */

public class MissCallActivity extends OutBaseActivity implements View.OnClickListener {


    private TextView mTvMissedCallNum;//missed call number
    private MissedCallsAdapter mAdapter;
    private ArrayList<String> mContactList;
    private ImageView mIvClose;
    private FrameLayout mFlNativeAd;
    private String mMissedCallNum;
    private FrameLayout mFlBannerAd;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_out_miss_call;
    }

    private ContentObserver mMissedCallsObserver = new ContentObserver(new Handler()) {
        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri.equals(CallLog.Calls.CONTENT_URI)) {
                getMissedCallsData();
            }
        }
    };


    private void initData() {
        //等到查出所有的未接电话到设置值
//        mMissedCallNum = this.getResources().getString(R.string.miss_call_activity_miss_call_num);
//        mContactList = new ArrayList<>();
//        mAdapter = new MissedCallsAdapter(mContactList);
//        mRv.setAdapter(mAdapter);
//        initContentReceiver();
//        getMissedCallsData();
    }

    @Override
    protected void initView() {
        setStatusBarColor(R.color.miss_call_activity_bg);
        mTvMissedCallNum = findViewById(R.id.tv_miss_call_num);
        mIvClose = findViewById(R.id.iv_close);
        mIvClose.setOnClickListener(this);
//        mRv = findViewById(R.id.rv);
//        mRv.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRv.setLayoutManager(linearLayoutManager);
        mFlNativeAd = findViewById(R.id.fl_native_ad);
        mFlBannerAd = findViewById(R.id.fl_banner_ad);
        initData();
        UtilsLog.statisticsLog("miss_call", "show", null);
    }

    @Override
    public ViewGroup getNativeAdLayout() {
        return mFlNativeAd;
    }

    @Override
    public ViewGroup getBannerAdLayout() {
        return mFlBannerAd;
    }

    /*****注册ContentObserver*****/
    private void initContentReceiver() {
        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, mMissedCallsObserver);
    }

    /*****获得未接来电数据****/
    private void getMissedCallsData() {
        mContactList.clear();
        getMissedCalls();
        mMissedCallNum = MissCallActivity.this.getResources().getString(R.string.miss_call_activity_miss_call_num);
        mTvMissedCallNum.setText(mContactList.size() > 0 ? String.format(mMissedCallNum, mContactList.size()) : "");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            finish();
            JSONObject jsonObject = new JSONObject();
            UtilsJson.JsonSerialization(jsonObject, "close_button", "iv_close");
            UtilsLog.statisticsLog("miss_call", "close", jsonObject);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "close_button", "back");
        UtilsLog.statisticsLog("charge_over", "close", jsonObject);
    }

    @SuppressLint("MissingPermission")
    private void getMissedCalls() {
        String callName;
        String callNumber;
        String callDate;
        final String[] projection = null;
        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = CallLog.Calls.DATE + " DESC";
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI, projection,
                    selection, selectionArgs, sortOrder);
            while (cursor.moveToNext()) {
                callName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String callLogID = cursor.getString(cursor
                        .getColumnIndex(CallLog.Calls._ID));
                callNumber = cursor.getString(cursor
                        .getColumnIndex(CallLog.Calls.NUMBER));
                //需要对时间进行一定的处理
                callDate = cursor.getString(cursor
                        .getColumnIndex(CallLog.Calls.DATE));
                long callTime = Long.parseLong(callDate);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                callDate = sdf.format(new Date(callTime));

                String callType = cursor.getString(cursor
                        .getColumnIndex(CallLog.Calls.TYPE));
                String isCallNew = cursor.getString(cursor
                        .getColumnIndex(CallLog.Calls.NEW));
                if (Integer.parseInt(callType) == (CallLog.Calls.MISSED_TYPE)
                        && Integer.parseInt(isCallNew) > 0)  //isCallNew  0表示已读，1表示未读
                    if (Integer.parseInt(callType) == (CallLog.Calls.MISSED_TYPE)) {
                        mContactList.add(callDate + "," + callNumber);
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        getContentResolver().unregisterContentObserver(mMissedCallsObserver);
    }
}
