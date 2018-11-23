package com.app.soundrecord.util;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.app.soundrecord.application.DBHelper;
import com.app.soundrecord.application.XRecordConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import ulric.li.XLibFactory;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;

import static com.app.soundrecord.util.FileUtils.AUDIO_WAV_BASEPATH;


public class AudioRecorder2AAC {
    private static final String TAG = "xiao";
    private int SAMPLE_RATE = 16000; //采样率 8K或16K
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO; //音频通道(单声道)
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT; //音频格式
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;  //音频源（麦克风）
    private String encodeType = MediaFormat.MIMETYPE_AUDIO_AAC;
    private boolean isRecording = false;
    private boolean isDelete = false;
    private static final int samples_per_frame = 2048;

    public static AudioRecord audioRecord;
    private Thread recorderThread;

    private static AcousticEchoCanceler canceler;//回声消除

    private MediaCodec mediaEncode;
    private MediaCodec.BufferInfo encodeBufferInfo;
    private ByteBuffer[] encodeInputBuffers;
    private ByteBuffer[] encodeOutputBuffers;
    private byte[] chunkAudio = new byte[0];
    private int volumes;

    public int getVolumes() {
        return volumes;
    }

    private BufferedOutputStream out;
//    private RecorderTask recorderTask;
    private  Context mContext;

    //文件名
    private String fileName;
    private String mFilePath = null;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    Object mLock;
    private IXThreadPool mIXThreadPool;
    private IXThreadPoolListener mIXThreadPoolListener;
    //录音状态
    private AudioRecorder.Status status = AudioRecorder.Status.STATUS_NO_READY;

    public AudioRecorder2AAC(String fileName,Context context) {
        initAACMediaEncode();
        this.fileName = fileName;
        this.mContext=context;
//        recorderTask = new RecorderTask();
        mLock = new Object();
        mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        mIXThreadPoolListener = new MyThreadListener();
    }

    /*
        开始录音
     */
    public void startAudioRecording() {
//        recorderThread = new Thread(recorderTask);
//        if (!recorderThread.isAlive()) {
//            recorderThread.start();
//        }
        if (isRecording)return;

        if (!isRecording) {

            isRecording=true;
            isDelete=false;
            mIXThreadPool.addTask(mIXThreadPoolListener);
        }
        mStartingTimeMillis = System.currentTimeMillis();

    }

    /*
        停止录音
     */
    public void stopAudioRecording(int data) {
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        //释放回声消除器
        setAECEnabled(false);

        isRecording = false;
        if (data<2)
            isDelete=true;

        try {
            mediaEncode.stop();
            mediaEncode.release();
            initAACMediaEncode();
        } catch (IllegalStateException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消录音
     */
    public void canel() {

        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        isDelete=true;
        status = AudioRecorder.Status.STATUS_NO_READY;


    }

    /**
     * 添加AEC回声消除
     *
     * @return
     */
    public static boolean chkNewDev() {
        return android.os.Build.VERSION.SDK_INT >= 16;
    }


    public static boolean isDeviceSupport() {
        return AcousticEchoCanceler.isAvailable();
    }


    public static boolean initAEC(int audioSession) {
        if (canceler != null) {
            return false;
        }
        canceler = AcousticEchoCanceler.create(audioSession);
        if (canceler != null) {
            canceler.setEnabled(true);
        }
        return canceler.getEnabled();
    }

    public static boolean setAECEnabled(boolean enable) {
        if (null == canceler) {
            return false;
        }
        canceler.setEnabled(enable);
        return canceler.getEnabled();
    }

    class MyThreadListener implements IXThreadPoolListener{
        int bufferReadResult = 0;
        public MyThreadListener(){
            try {
                if (SettingUtils.getCode()==2){
                    String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;
                    File file = new File(fileBasePath);
                    //创建目录
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH , fileName+".mp4");
                    out = new BufferedOutputStream(new FileOutputStream(f, false));
                }else if (SettingUtils.getCode()==3){
                    String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;
                    File file = new File(fileBasePath);
                    //创建目录
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH , fileName+".3gp");
                    out = new BufferedOutputStream(new FileOutputStream(f, false));
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTaskRun() {
            //获取最小缓冲区大小
            int bufferSizeInBytes = android.media.AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
            Log.e("bufferSizeInBytes*****", bufferSizeInBytes + "");
            audioRecord = new AudioRecord(
                    AUDIO_SOURCE,   //音频源
                    SAMPLE_RATE,    //采样率
                    CHANNEL_CONFIG,  //音频通道
                    AUDIO_FORMAT,    //音频格式\采样精度
                    bufferSizeInBytes * 4//缓冲区
            );
            audioRecord.startRecording();

//            isRecording = true;
            while (isRecording) {

                byte[] buffer = new byte[samples_per_frame];
                if (null==audioRecord)
                    return;
                if (null==buffer)
                    return;
                //从缓冲区中读取数据，存入到buffer字节数组数组中
                bufferReadResult = audioRecord.read(buffer, 0, buffer.length);
                Log.e(TAG,"============="+bufferReadResult+"===="+audioRecord);
                //判断是否读取成功
                if (bufferReadResult == android.media.AudioRecord.ERROR_BAD_VALUE || bufferReadResult == android.media.AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.e(TAG, "Read error");
                }
                if (audioRecord != null&&bufferReadResult>0) {
                    Log.i(TAG,"===================="+bufferReadResult);
                    try {
                        dstAudioFormatFromPCM(buffer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                double sum = 0;
                for (int i = 0; i < bufferReadResult; i++) {
                    sum += buffer[i] * buffer[i];
                }
                if (bufferReadResult > 0) {
                    double amplitude = sum / bufferReadResult;
                    volumes = (int) Math.sqrt(amplitude);
                }
//                final double volume = 10 * mean;
//                volumes=(int)volume;
//                Log.d(TAG, "分贝值:" + volume);
        }
            if (audioRecord != null) {
                audioRecord.setRecordPositionUpdateListener(null);
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }

        }

        @Override
        public void onTaskComplete() {

            if (audioRecord != null) {
                audioRecord.setRecordPositionUpdateListener(null);
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }

            try {
                if (SettingUtils.getCode()==2){
                    mFilePath=Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH+fileName+".mp4";
                    fileName=fileName+ "_" + (new DBHelper(mContext).getCount() + 1) +".mp4";
                }else if (SettingUtils.getCode()==3){
                    mFilePath=Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH+fileName+".3gp";
                    fileName=fileName+ "_" + (new DBHelper(mContext).getCount() + 1) +".3gp";
                }
                if (!isDelete)
                new DBHelper(mContext).addRecording(fileName, mFilePath, mElapsedMillis,SettingUtils.getSampleRate(),SettingUtils.getCode());
            } catch (Exception e){

            }
            fileName = null;

        }

        @Override
        public void onMessage(Message msg) {

        }
    }

//    class RecorderTask implements Runnable {
//        int bufferReadResult = 0;
//
//
//        public RecorderTask() {
//            try {
//                if (SettingUtils.getCode()==2){
//                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH , fileName+".mp4");
//                    out = new BufferedOutputStream(new FileOutputStream(f, false));
//                }else if (SettingUtils.getCode()==3){
//                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH , fileName+".3gp");
//                    out = new BufferedOutputStream(new FileOutputStream(f, false));
//                }
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//        @Override
//        public void run() {
//            //获取最小缓冲区大小
//            int bufferSizeInBytes = android.media.AudioRecord.getMinBufferSize(XRecordConfig.VALUE_STRING_DOMAIN_NAME, CHANNEL_CONFIG, AUDIO_FORMAT);
//            Log.e("bufferSizeInBytes*****", bufferSizeInBytes + "");
//                audioRecord = new AudioRecord(
//                        AUDIO_SOURCE,   //音频源
//                        XRecordConfig.VALUE_STRING_DOMAIN_NAME,    //采样率
//                        CHANNEL_CONFIG,  //音频通道
//                        AUDIO_FORMAT,    //音频格式\采样精度
//                        bufferSizeInBytes * 4//缓冲区
//                );
//            audioRecord.startRecording();
//
//            isRecording = true;
//            while (isRecording) {
//                short[] buffers = new short[bufferSizeInBytes * 4];
//                //r是实际读取的数据长度，一般而言r会小于buffersize
//                int r = audioRecord.read(buffers, 0, bufferSizeInBytes * 4);
//                long v = 0;
//                // 将 buffer 内容取出，进行平方和运算
//                for (int i = 0; i < buffers.length; i++) {
//                    v += buffers[i] * buffers[i];
//                }
//                // 平方和除以数据总长度，得到音量大小。
//                double mean = v / (double) r;
//                double volume = 10 * Math.log10(mean);
//                volumes=(int)volume;
//                Log.d(TAG, "分贝值:" + volume);
//                byte[] buffer = new byte[samples_per_frame];
//                if (null==audioRecord)
//                    return;
//                if (null==buffer)
//                    return;
//                //从缓冲区中读取数据，存入到buffer字节数组数组中
//                bufferReadResult = audioRecord.read(buffer, 0, buffer.length);
//                //判断是否读取成功
//                if (bufferReadResult == android.media.AudioRecord.ERROR_BAD_VALUE || bufferReadResult == android.media.AudioRecord.ERROR_INVALID_OPERATION)
//                    Log.e(TAG, "Read error");
//                if (audioRecord != null&&bufferReadResult>0) {
//                    Log.i("bufferReadResult----->", bufferReadResult + "");
//                    try {
//                        dstAudioFormatFromPCM(buffer);
//                    } catch (IllegalStateException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//
//            try {
//                if (SettingUtils.getCode()==2){
//                    mFilePath=Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH+fileName+".mp4";
//                    fileName=fileName+ "_" + (new DBHelper(mContext).getCount() + 1) +".mp4";
//                }else if (SettingUtils.getCode()==3){
//                    mFilePath=Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH+fileName+".3gp";
//                    fileName=fileName+ "_" + (new DBHelper(mContext).getCount() + 1) +".3gp";
//                }
//
//                new DBHelper(mContext).addRecording(fileName, mFilePath, mElapsedMillis,XRecordConfig.VALUE_STRING_DOMAIN_NAME,2);
//            } catch (Exception e){
//
//            }
//            fileName = null;
//            if (audioRecord != null) {
//
//                audioRecord.setRecordPositionUpdateListener(null);
//                audioRecord.stop();
//                audioRecord.release();
//                audioRecord = null;
//
//            }
//
//
//        }
//    }


    /**
     * 初始化AAC编码器
     */
    private void initAACMediaEncode() {
        try {
            //参数对应-> mime type、采样率、声道数
            MediaFormat encodeFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 16000, 1);
            encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64000);//比特率
            encodeFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
            encodeFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
            encodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, samples_per_frame);//作用于inputBuffer的大小
            mediaEncode = MediaCodec.createEncoderByType(encodeType);
            mediaEncode.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaEncode == null) {
            Log.e(TAG, "create mediaEncode failed");
            return;
        }
        mediaEncode.start();
        encodeInputBuffers = mediaEncode.getInputBuffers();
        encodeOutputBuffers = mediaEncode.getOutputBuffers();
        encodeBufferInfo = new MediaCodec.BufferInfo();
    }


    /**
     * 编码PCM数据 得到AAC格式的音频文件
     */
    private void dstAudioFormatFromPCM(byte[] pcmData) {

        int inputIndex;
        ByteBuffer inputBuffer;
        int outputIndex;
        ByteBuffer outputBuffer;

        int outBitSize;
        int outPacketSize;
        byte[] PCMAudio;
        PCMAudio = pcmData;

        encodeInputBuffers = mediaEncode.getInputBuffers();
        encodeOutputBuffers = mediaEncode.getOutputBuffers();
        encodeBufferInfo = new MediaCodec.BufferInfo();


        inputIndex = mediaEncode.dequeueInputBuffer(0);
        inputBuffer = encodeInputBuffers[inputIndex];
        inputBuffer.clear();
        inputBuffer.limit(PCMAudio.length);
        inputBuffer.put(PCMAudio);//PCM数据填充给inputBuffer
        mediaEncode.queueInputBuffer(inputIndex, 0, PCMAudio.length, 0, 0);//通知编码器 编码


        outputIndex = mediaEncode.dequeueOutputBuffer(encodeBufferInfo, 0);
        while (outputIndex >0) {

            outBitSize = encodeBufferInfo.size;
            outPacketSize = outBitSize + 7;//7为ADT头部的大小
            outputBuffer = encodeOutputBuffers[outputIndex];//拿到输出Buffer
            outputBuffer.position(encodeBufferInfo.offset);
            outputBuffer.limit(encodeBufferInfo.offset + outBitSize);
            chunkAudio = new byte[outPacketSize];
            addADTStoPacket(chunkAudio, outPacketSize);//添加ADTS
            outputBuffer.get(chunkAudio, 7, outBitSize);//将编码得到的AAC数据 取出到byte[]中

            try {
                //录制aac音频文件，保存在手机内存中
                out.write(chunkAudio, 0, chunkAudio.length);
                out.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputBuffer.position(encodeBufferInfo.offset);
            mediaEncode.releaseOutputBuffer(outputIndex, false);
            outputIndex = mediaEncode.dequeueOutputBuffer(encodeBufferInfo, 0);

        }

    }


    /**
     * 添加ADTS头
     *
     * @param packet
     * @param packetLen
     */
    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2; // AAC LC
        int freqIdx = 8; // 16KHz
        int chanCfg = 1; // CPE

        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF1;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;

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
