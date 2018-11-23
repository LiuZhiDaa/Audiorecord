package com.app.soundrecord.main.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.soundrecord.application.DBHelper;
import com.app.soundrecord.R;
import com.app.soundrecord.application.XApplication;
import com.app.soundrecord.core.XCoreFactory;
import com.app.soundrecord.base.BaseFragment;
import com.app.soundrecord.core.config.intf.IConfigMgr;
import com.app.soundrecord.core.config.intf.IConfigMgrListener;
import com.app.soundrecord.core.audio.handlers.MyHandler;
import com.app.soundrecord.core.audio.interfaces.IAudioCallback;
import com.app.soundrecord.core.audio.interfaces.IPhoneState;
import com.app.soundrecord.core.drawer.intf.IDialogManager;
import com.app.soundrecord.core.drawer.intf.IDoalogListener;


import com.app.soundrecord.core.number.intf.INumberListener;

import com.app.soundrecord.core.number.intf.INumberManager;
import com.app.soundrecord.util.AudioRecorder;
import com.app.soundrecord.util.AudioRecorder2AAC;

import com.app.soundrecord.util.InputUtil;
import com.app.soundrecord.util.MediaRecorders;
import com.app.soundrecord.util.OnClickListener;
import com.app.soundrecord.util.SettingUtils;
import com.app.soundrecord.util.StorageUtil;
import com.app.soundrecord.util.TimeUtil;
import com.carlos.voiceline.mylibrary.VoiceLineView;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import ulric.li.permission.PermissionHelper;

import static com.app.soundrecord.util.FileUtils.AUDIO_WAV_BASEPATH;


public class RecordFragment extends BaseFragment implements IAudioCallback ,IPhoneState,Runnable{
    @BindView(R.id.Load)
    RelativeLayout Load;
    @BindView(R.id.main_start)
    ImageView imageView_start;
    @BindView(R.id.main_record)
    RelativeLayout relativeLayout_record;
    @BindView(R.id.main_cancel)
    ImageView main_cancel;
    @BindView(R.id.main_play)
    ImageView main_play;
    @BindView(R.id.main_sure)
    ImageView main_sure;
    @BindView(R.id.voicLine)
    VoiceLineView voiceLineView;
    @BindView(R.id.tv_record_time)
    TextView tvRecordTime;
    @BindView(R.id.main_rl_name)
    LinearLayout main_rl_name;
    @BindView(R.id.main_rl_record)
    RelativeLayout main_rl_record;
    @BindView(R.id.main_edit_name)
    EditText main_edit_name;
    @BindView(R.id.main_tv_code)
    TextView main_tv_code;
    @BindView(R.id.main_tv_count)
    TextView main_tv_count;
    @BindView(R.id.main_tv_named)
    TextView main_tv_named;
    @BindView(R.id.main_tv_memory)
    TextView main_tv_memory;

    /**
     * 支持定时和周期性执行的线程池
     */
    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);

    private MyHandler myHandler = new MyHandler(this);
    private static final int INITIAL_DELAY = 0;
    private static final int PERIOD = 1000;
    private static int MAX_DURATION = 240000;

    private AudioRecorder maudioRecorder;
    private AudioRecorder2AAC mAudioRecorder2AAC;

    private boolean isRecording=false;
    private boolean isAlive = true;
    private String name=null;
    private  String mName;
    private Context mContext;
    File audioFile; //录音保存的文件
    boolean isRecoding=false;// true 表示正在录音

    private String mFilePath = null;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private IDialogManager mDialogManager;
    private INumberManager miNumberManager;


    private DBHelper dbHelper;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int ratio = 0;
            if (SettingUtils.getCode()==1){
                if(maudioRecorder==null) return;
                 ratio = maudioRecorder.getVolumes();
            }else if (SettingUtils.getCode()==2){
                if(mAudioRecorder2AAC==null) return;
                ratio = mAudioRecorder2AAC.getVolumes();
            }else if (SettingUtils.getCode()==3){
                if(mAudioRecorder2AAC==null) return;
                ratio = mAudioRecorder2AAC.getVolumes();
            }

//            double db = 0;// 分贝
            //默认的最大音量是100,可以修改，但其实默认的，在测试过程中就有不错的表现
            //你可以传自定义的数字进去，但需要在一定的范围内，比如0-200，就需要在xml文件中配置maxVolume
            //同时，也可以配置灵敏度sensibility
//            if (ratio > 1)
//                db = 10 * Math.log10(ratio);
//            Log.d("zzz","db  =="+db);
//            Log.d("zzz","ratio  =="+ratio);
            //只要有一个线程，不断调用这个方法，就可以使波形变化
            //主要，这个方法必须在ui线程中调用
            if (ratio<52)
                ratio=0;
            if (isRecording){
                if (voiceLineView!=null)
                voiceLineView.setVolume(ratio);
                Log.d("zzz","音量的值   =   "+ratio);
            }

        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void init(View rootView) {
        dbHelper = new DBHelper(getActivity());
        main_tv_count.setText((dbHelper.getCount()+1)+"");
        main_edit_name.setText(Objects.requireNonNull(getActivity()).getResources().getString(R.string.myvoice)+(SettingUtils.getnameNumber()+1));
        setClickListener(imageView_start, main_cancel, main_play,main_sure);
        scheduledThreadPool.scheduleAtFixedRate(() -> {
            if (isRecording) {
                ++time;
                myHandler.sendEmptyMessage(HANDLER_CODE);
            }
        }, INITIAL_DELAY, PERIOD, TimeUnit.MILLISECONDS);
        main_tv_memory.setText(StorageUtil.getAvailableInternalMemorySize(getContext()));
        if (SettingUtils.getCode()==1){
            main_tv_code.setText(".wav");
        }else if (SettingUtils.getCode()==2){
            main_tv_code.setText(".mp4");
        }else if (SettingUtils.getCode()==3){
            main_tv_code.setText(".3gp");
        }
        main_edit_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int s, int i1, int i2) {
                String editable = main_edit_name.getText().toString();
                String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";//正则表达式
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(editable);
                String str = m.replaceAll("").trim();    //删掉不是字母或数字的字符
                if(!editable.equals(str)){
                    main_edit_name.setText(str);  //设置EditText的字符
                    main_edit_name.setSelection(str.length()); //因为删除了字符，要重写设置新的光标所在位置
                }
                int mTextMaxlenght = 0;
                Editable editables = main_edit_name.getText();
                String strs = editables.toString().trim();
                //得到最初字段的长度大小，用于光标位置的判断
                int selEndIndex = Selection.getSelectionEnd(editables);
                // 取出每个字符进行判断，如果是字母数字和标点符号则为一个字符加1，如果是汉字则为两个字符
                for (int i = 0; i < strs.length(); i++) {
                    char charAt = strs.charAt(i);
                    //32-122包含了空格，大小写字母，数字和一些常用的符号，如果在这个范围内则算一个字符，
                    //如果不在这个范围比如是汉字的话就是两个字符
                    if (charAt >= 32 && charAt <= 122) {
                        mTextMaxlenght++;
                    } else {
                        mTextMaxlenght += 2;
                    }
                    // 当最大字符大于40时，进行字段的截取，并进行提示字段的大小
                    if (mTextMaxlenght > 16) {
                        // 截取最大的字段
                        String newStr = strs.substring(0, i);
                        main_edit_name.setText(newStr);
                        // 得到新字段的长度值
                        editables = main_edit_name.getText();
                        int newLen = editables.length();
                        if (selEndIndex > newLen) {
                            selEndIndex=editables.length();
                        }
                        // 设置新光标所在的位置
                        Selection.setSelection(editables, selEndIndex);

                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mDialogManager = (IDialogManager) XCoreFactory.getInstance().createInstance(IDialogManager.class);
        mDialogManager.addListener(iDoalogListener);
        miNumberManager=(INumberManager) XCoreFactory.getInstance().createInstance(INumberManager.class);
        miNumberManager.addListener(iNumberListener);

        imageView_start.setOnClickListener(new OnClickListener() {
            @Override
            protected void myOnClickListener(View v) {
                if (main_edit_name.getText().toString().equals("")){
                    Toast.makeText(getContext(), R.string.string_inputname,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (main_edit_name.getText()==null){
                    Toast.makeText(getContext(), R.string.string_inputname,Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getActivity()!=null) {
                    InputUtil.closeInput(getActivity(), main_edit_name);
                }
                if (getActivity()!=null){
                    PermissionHelper.getInstance(getActivity()).requestPermission(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO}, new PermissionHelper.PermissionGrantedCallback() {
                        @Override
                        public void onGranted(List<String> permissions) {
                            for (int nIndex = 0; nIndex < dbHelper.getCount(); nIndex++){
                                if (main_edit_name.getText().toString().equals(dbHelper.getItemAt(nIndex).getName().substring(0, dbHelper.getItemAt(nIndex).getName().indexOf("_")))) {
                                    Toast.makeText(getContext(), R.string.string_named,Toast.LENGTH_SHORT).show();
                                    dbHelper.close();
                                    return;
                                }else {
                                    continue;
                                }
                            }
                            main_rl_name.setVisibility(View.GONE);
                            main_rl_record.setVisibility(View.VISIBLE);
                            main_play.setImageResource(R.drawable.icon_bofang);
                            imageView_start.setVisibility(View.GONE);
                            relativeLayout_record.setVisibility(View.VISIBLE);
                            isRecording=true;
                            if (getActivity() != null) {
                                ((XApplication)getActivity().getApplication()).setRecording(true);
                            }
                            if (SettingUtils.getCode()==1){
                                main_play.setEnabled(true);
                                maudioRecorder = AudioRecorder.getInstance(getContext());
                                maudioRecorder.setmMaxDuration(MAX_DURATION);
                                if (maudioRecorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY){
                                    name=main_edit_name.getText().toString();

                                    maudioRecorder.createDefaultAudio(name);
                                    main_tv_named.setText(name+getString(R.string.string_wav));
                                    maudioRecorder.startRecord(null);
                                    Thread thread = new Thread(RecordFragment.this);
                                    thread.start();
                                }
                            }else if (SettingUtils.getCode()==2){
                                name=main_edit_name.getText().toString();
                                mAudioRecorder2AAC=new AudioRecorder2AAC(name,getContext());
                                mAudioRecorder2AAC.startAudioRecording();
                                main_play.setImageResource(R.drawable.icon_prohibit);
                                main_play.setEnabled(false);
                                main_tv_named.setText(name+getString(R.string.string_mp4));
                                Thread thread = new Thread(RecordFragment.this);
                                thread.start();
                            }else if (SettingUtils.getCode()==3){
                                name=main_edit_name.getText().toString();
                                mName=name;
                                mAudioRecorder2AAC=new AudioRecorder2AAC(name,getContext());
                                mAudioRecorder2AAC.startAudioRecording();
                                main_tv_named.setText(name+getString(R.string.string_3gp));
                                main_play.setImageResource(R.drawable.icon_prohibit);
                                main_play.setEnabled(false);
                                Thread thread = new Thread(RecordFragment.this);
                                thread.start();
                            }

                        }
                    });
                }
            }
        });
        main_cancel.setOnClickListener(new OnClickListener() {
            @Override
            protected void myOnClickListener(View v) {
                AlertDialog.Builder confirmDelete = new AlertDialog.Builder(getContext());
                confirmDelete.setTitle(getContext().getString(R.string.dialog_action_cancel));
                confirmDelete.setMessage(getContext().getString(R.string.dialog_text_cancel));
                confirmDelete.setCancelable(true);
                confirmDelete.setPositiveButton(getContext().getString(R.string.dialog_action_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (getActivity() != null) {
                                    ((XApplication)getActivity().getApplication()).setRecording(false);
                                }
                                isRecording=false;
                                main_rl_name.setVisibility(View.VISIBLE);
                                main_rl_record.setVisibility(View.GONE);
                                imageView_start.setVisibility(View.VISIBLE);
                                relativeLayout_record.setVisibility(View.GONE);
                                time = 0;
                                tvRecordTime.setText("00:00:00");
                                if (SettingUtils.getCode()==1){
                                    maudioRecorder.canel();
                                }else if (SettingUtils.getCode()==2){
                                    mAudioRecorder2AAC.canel();
                                }else if (SettingUtils.getCode()==3){
//                   stop();
//                    mMediaRecorders.stop();
                                    mAudioRecorder2AAC.canel();
                                }
                                dialog.cancel();
                            }
                        });
                confirmDelete.setNegativeButton(getContext().getString(R.string.dialog_action_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = confirmDelete.create();
                alert.show();
            }
        });
        main_play.setOnClickListener(new OnClickListener() {
            @Override
            protected void myOnClickListener(View v) {
                if (isRecording){
                    if (maudioRecorder.getStatus() == AudioRecorder.Status.STATUS_START) {
                        phoneToPause();
                    }else {
                        maudioRecorder.startRecord(null);
                    }
                    isRecording=false;

                    main_play.setImageResource(R.drawable.icon_luyin);
                }else {
                    maudioRecorder.startRecord(null);
                    isRecording=true;
                    main_play.setImageResource(R.drawable.icon_bofang);
                }
            }
        });
        main_sure.setOnClickListener(new OnClickListener() {
            @Override
            protected void myOnClickListener(View v) {

                if (getActivity() != null) {
                    ((XApplication)getActivity().getApplication()).setRecording(false);
                }
                isRecording = false;
                main_rl_name.setVisibility(View.VISIBLE);
                main_rl_record.setVisibility(View.GONE);
                tvRecordTime.setText("00:00:00");
                imageView_start.setVisibility(View.VISIBLE);
                relativeLayout_record.setVisibility(View.GONE);
                if (SettingUtils.getCode()==1){
                    if (maudioRecorder!=null) {
                        maudioRecorder.stopRecord(time);
                        time = 0;
                    }
                }else if (SettingUtils.getCode()==2){
                    if (mAudioRecorder2AAC!=null) {
                        mAudioRecorder2AAC.stopAudioRecording(time);
                        time = 0;
                    }
                }else if (SettingUtils.getCode()==3){
//                    stop();
//                    mMediaRecorders.stop();
                    if (mAudioRecorder2AAC!=null) {
                        mAudioRecorder2AAC.stopAudioRecording(time);
                        time = 0;
                    }
                }
                main_tv_memory.setText(StorageUtil.getAvailableInternalMemorySize(getContext()));
                Log.d("ZZZ","getnameNumber ="+SettingUtils.getnameNumber());
                Load.setVisibility(View.GONE);
            }
        });
    }
    IConfigMgrListener mIConfigMgrListener = new IConfigMgrListener() {
        @Override
        public void onDetectLocalInfoAsyncComplete() {

        }

        @Override
        public void onRequestConfigAsync(boolean bSuccess) {
            if (!mHasBannerAdShow) {

            }
        }
    };
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_record;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    public void showPlay(String filePath) {

    }

    @Override
    public void phone() {

    }
    /**
     * 遍历设置监听
     *
     * @param views
     */
    private void setClickListener(View... views) {
        for (View view : views) {
            view.setOnClickListener(new OnClickListener() {
                @Override
                protected void myOnClickListener(View v) {
                    onClick(v);
                }
            });
        }
    }
    @SuppressLint("SetTextI18n")
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_start:
                break;
            case R.id.main_cancel:
                break;
            case R.id.main_play:


                break;
            case R.id.main_sure:

                break;
        }
    }

    private IDoalogListener iDoalogListener =new IDoalogListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onChange() {
            super.onChange();
            if (main_tv_code == null){
                return;
            }
            if (SettingUtils.getCode()==1){
                main_tv_code.setText(R.string.string_wav);
            }else if (SettingUtils.getCode()==2){
                main_tv_code.setText(R.string.string_mp4);
            }else if (SettingUtils.getCode()==3){
                main_tv_code.setText(R.string.string_3gp);
            }


        }
    };

    private INumberListener iNumberListener = new INumberListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void toUpdate() {
            super.toUpdate();
            if (main_edit_name == null || main_tv_memory == null)
                return;
            main_edit_name.setText(Objects.requireNonNull(getActivity()).getResources().getString(R.string.myvoice) + (SettingUtils.getnameNumber() + 1));
            main_tv_memory.setText(StorageUtil.getAvailableInternalMemorySize(getContext()));
            Load.setVisibility(View.GONE);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (maudioRecorder == null) {
            return;
        }
    }

    @Override
    public void onDestroy() {
        if (maudioRecorder != null) {
            maudioRecorder.release();
        }
        isAlive = false;
        scheduledThreadPool.shutdown();
        super.onDestroy();
        if (mDialogManager != null) {
            mDialogManager.removeListener(iDoalogListener);
        }
        if (miNumberManager != null) {
            miNumberManager.removeListener(iNumberListener);
        }

    }

    @Override
    public void run() {
        while (isAlive) {
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private final static int HANDLER_CODE = 0x0249;
    private int time;

    public void requestOver(Message msg) {
        switch (msg.what) {
            case HANDLER_CODE:
                tvRecordTime.setText(TimeUtil.formatLongToTimeStr(time));
                break;
        }
    }

    /**
     * 暂停录音和状态修改
     */
    private void phoneToPause() {
        maudioRecorder.pauseRecord();
        main_play.setImageResource(R.drawable.icon_zanting);
        isRecording = false;

    }
}
