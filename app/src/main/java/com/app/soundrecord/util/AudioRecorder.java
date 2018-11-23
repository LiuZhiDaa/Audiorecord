package com.app.soundrecord.util;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.app.soundrecord.R;
import com.app.soundrecord.application.DBHelper;
import com.app.soundrecord.application.XRecordConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ulric.li.XLibFactory;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;


public class AudioRecorder {
    private static AudioRecorder audioRecorder;
    //音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
//    private final static int AUDIO_SAMPLE_RATE = XRecordConfig.VALUE_STRING_DOMAIN_NAME;
    //声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static Context mContext;
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;

    //录音时长
    private int recordDuration = 0;
    //录音对象
    private AudioRecord audioRecord;
    //录音最大时长
    private int mMaxDuration = 999999999;

    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;

    private boolean isDelete = false;




    //录音状态
    private Status status = Status.STATUS_NO_READY;

    //文件名
    private String fileName;

    private String mFilePath = null;

    private static boolean first = true;


    //用于计时
    private Handler mHandler = new Handler();

    //录音文件
    private List<String> filesName = new ArrayList<>();

    private int volumes;

    public int getVolumes() {
        return volumes;
    }

    private OnRecordingListener mListener;
    private IXThreadPool mIXThreadPool;

    private AudioRecorder() {
    }

    public void setmMaxDuration(int duration){
        mMaxDuration = duration;
    }
    public interface OnRecordingListener {
        void onRecording(int duration);
    }

    //单例模式
    public static AudioRecorder getInstance(Context context) {
        if (audioRecorder == null) {
            mContext=context;
            audioRecorder = new AudioRecorder();
        }
        return audioRecorder;

    }

    public void setOnRecordingListener(OnRecordingListener listener) {
        this.mListener = listener;
    }

    /**
     * 获取录音时长
     */
    public int getRecordDuration(){
        return recordDuration;
    }
    /**
     * 创建录音对象
     */
    public void createAudio(String fileName, int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, channelConfig);
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        this.fileName = fileName;
    }

    /**
     * 创建默认的录音对象
     *
     * @param fileName 文件名
     */
    public void createDefaultAudio(String fileName) {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(SettingUtils.getSampleRate(), AUDIO_CHANNEL, AUDIO_ENCODING);
        audioRecord = new AudioRecord(AUDIO_INPUT, SettingUtils.getSampleRate(), AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
        this.fileName = fileName;
        status = Status.STATUS_READY;
        mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
    }


    /**
     * 开始录音
     *
     * @param listener 音频流的监听
     */
    public void startRecord(final RecordStreamListener listener) {
        first = true;
        if (status == Status.STATUS_NO_READY || TextUtils.isEmpty(fileName)) {
//            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限~");
            Toast.makeText(mContext, R.string.string_noinit,Toast.LENGTH_SHORT).show();
        }
        if (status == Status.STATUS_START) {
//            throw new IllegalStateException("正在录音");
            Toast.makeText(mContext, R.string.string_recording,Toast.LENGTH_SHORT).show();
        }
        if (audioRecord==null){
            return;
        }
        isDelete=false;
        audioRecord.startRecording();
        mStartingTimeMillis = System.currentTimeMillis();

        new Thread(new Runnable() {
            @Override
            public void run() {
                writeDataTOFile(listener);
            }
        }).start();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recordDuration++;
                if(recordDuration >= mMaxDuration){
                    stopRecord(mMaxDuration);
                }
                if (mListener != null) {
                    mListener.onRecording(recordDuration);
                }
                mHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    /**
     * 暂停录音
     */
    public void pauseRecord() {
        Log.d("AudioRecorder", "===pauseRecord===");
        if (status != Status.STATUS_START) {
            Toast.makeText(mContext, R.string.string_norecord,Toast.LENGTH_SHORT).show();
        } else {
            audioRecord.stop();
            status = Status.STATUS_PAUSE;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 停止录音
     */
    public void stopRecord(int data) {
        Log.d("AudioRecorder", "===stopRecord===");
        if (status == Status.STATUS_NO_READY || status == Status.STATUS_READY) {
//            throw new IllegalStateException("录音尚未开始");
            Toast.makeText(mContext, R.string.string_notstart,Toast.LENGTH_SHORT).show();
        } else {
            if (data<2)
                isDelete=true;
            audioRecord.stop();
            status = Status.STATUS_STOP;
            mElapsedMillis = data*1000;
            recordDuration  = 0 ;
            release();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        Log.d("AudioRecorder", "===release===");
        //假如有暂停录音
        try {
            if (filesName.size() > 0) {
                List<String> filePaths = new ArrayList<>();
                for (String fileName : filesName) {
                    if (TextUtils.isEmpty(fileName)){
                        Toast.makeText(mContext, R.string.string_notempty,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    filePaths.add(FileUtils.getPcmFileAbsolutePath(fileName));
                }
                //清除
                filesName.clear();
                for (int i=0 ; i<filePaths.size();i++){
                    if (!fileIsExists(filePaths.get(i))){
                        filePaths.clear();
                        status = Status.STATUS_NO_READY;
                        return;
                    }
                }
                //将多个pcm文件转化为wav文件
                mergePCMFilesToWAVFile(filePaths);

            } else {
                //这里由于只要录音过filesName.size都会大于0,没录音时fileName为null
                //会报空指针 NullPointerException
                // 将单个pcm文件转化为wav文件
                //Log.d("AudioRecorder", "=====makePCMFileToWAVFile======");
                //makePCMFileToWAVFile();
            }
        } catch (Exception e) {

        }

        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }

        status = Status.STATUS_NO_READY;
    }

    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    /**
     * 取消录音
     */
    public void canel() {
        filesName.clear();
        fileName = null;
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        status = Status.STATUS_NO_READY;
//        File file = new File(mFilePath);
//        file.delete();
    }
    /**
     * 将音频信息写入文件
     *
     * @param listener 音频流的监听
     */
    private void writeDataTOFile(RecordStreamListener listener) {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        byte[] audiodata = new byte[bufferSizeInBytes];

        FileOutputStream fos = null;

        try {
            String currentFileName = fileName;
            if (status == Status.STATUS_PAUSE) {
                //假如是暂停录音 将文件名后面加个数字,防止重名文件内容被覆盖
                currentFileName += filesName.size();

            }
            filesName.add(currentFileName);
            if (TextUtils.isEmpty(fileName)){
                Toast.makeText(mContext,R.string.string_notempty,Toast.LENGTH_SHORT).show();
                return;
            }

            File file = new File(FileUtils.getPcmFileAbsolutePath(currentFileName));
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
        } catch (IllegalStateException e) {
            Log.e("AudioRecorder", e.getMessage());
            throw new IllegalStateException(e.getMessage());
        } catch (FileNotFoundException e) {
            Log.e("AudioRecorder", e.getMessage());

        }
        //将录音状态设置成正在录音状态
        status = Status.STATUS_START;
        while (status == Status.STATUS_START) {
            try {
                final int readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
                if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
                    try {
                        fos.write(audiodata);
                        if (listener != null) {
                            //用于拓展业务
                            listener.recordOfByte(audiodata, 0, audiodata.length);
                        }
                    } catch (IOException e) {
                        Log.e("AudioRecorder", e.getMessage());
                    }
                }
                Timer timer = new Timer();
                if (first)
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            double sum = 0;
                            for (int i = 0; i < readsize; i++) {
                                sum += audiodata[i] * audiodata[i];
                            }
                            if (readsize > 0) {
                                double amplitude = sum / readsize;
                                volumes = (int) Math.sqrt(amplitude);
                            }
//                            long v = 0;
//                            // 将 buffer 内容取出，进行平方和运算
//                            for (int i = 0; i < audiodata.length; i++) {
//                                v += audiodata[i] * audiodata[i];
//                            }
//                            // 平方和除以数据总长度，得到音量大小。
//                             volumes = v / (double) readsize;

                        }
                    }, 0, 1000);
                first = false;
            }catch (Exception e){}


        }
        try {
            if (fos != null) {
                fos.close();// 关闭写入流
            }
        } catch (IOException e) {
            Log.e("AudioRecorder", e.getMessage());
        }
    }

    /**
     * 将pcm合并成wav
     *
     * @param filePaths
     */
    private void mergePCMFilesToWAVFile(final List<String> filePaths) {

        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                if (TextUtils.isEmpty(fileName)){

                    return;
                }
                if (PcmToWav.mergePCMFilesToWAVFile(filePaths, FileUtils.getWavFileAbsolutePath(fileName))) {
                    try {
                        if (TextUtils.isEmpty(fileName)){

                            return;
                        }
                        mFilePath=FileUtils.getWavFileAbsolutePath(fileName);
                        fileName=fileName+ "_" + (new DBHelper(mContext).getCount() + 1) +".wav";

                    } catch (Exception e){

                    }
                    //操作成功
                } else {
                    //操作失败
                    Log.e("AudioRecorder", "mergePCMFilesToWAVFile fail");
                    return;
                }
            }

            @Override
            public void onTaskComplete() {
                if (!isDelete)
                    new DBHelper(mContext).addRecording(fileName, mFilePath, mElapsedMillis,SettingUtils.getSampleRate(),1);

                fileName = null;
            }

            @Override
            public void onMessage(Message msg) {

            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (PcmToWav.mergePCMFilesToWAVFile(filePaths, FileUtils.getWavFileAbsolutePath(fileName))) {
//                    try {
//                        mFilePath=FileUtils.getWavFileAbsolutePath(fileName);
//                        fileName=fileName+ "_" + (new DBHelper(mContext).getCount() + 1) +".wav";
//                        new DBHelper(mContext).addRecording(fileName, mFilePath, mElapsedMillis,XRecordConfig.VALUE_STRING_DOMAIN_NAME,1);
//                    } catch (Exception e){
//
//                    }
//                    //操作成功
//                } else {
//                    //操作失败
//                    Log.e("AudioRecorder", "mergePCMFilesToWAVFile fail");
//                    throw new IllegalStateException("mergePCMFilesToWAVFile fail");
//                }
//                fileName = null;
//            }
//        }).start();
    }



    /**
     * 获取录音对象的状态
     *
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * 获取本次录音文件的个数
     *
     * @return
     */
    public int getPcmFilesCount() {
        return filesName.size();
    }

    /**
     * 录音对象的状态
     */
    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE,
        //停止
        STATUS_STOP
    }

}
