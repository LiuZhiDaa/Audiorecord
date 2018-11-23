package com.app.soundrecord.util;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import com.app.soundrecord.application.DBHelper;

import java.io.File;
import java.io.IOException;

import static com.app.soundrecord.util.FileUtils.AUDIO_WAV_BASEPATH;

public class MediaRecorders  {
    public MediaRecorder getMediaRecorders() {
        return mediaRecorders;
    }

    private  MediaRecorder mediaRecorders;
    private  String mName;
    private  Context mContext;
    File audioFile; //录音保存的文件
    boolean isRecoding=false;// true 表示正在录音

    private String mFilePath = null;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    public MediaRecorders(String name,Context context){
        mName=name;
        mContext=context;
        mediaRecorders = new MediaRecorder();
        mediaRecorders.setAudioSource(MediaRecorder.AudioSource.MIC);//设置播放源 麦克风
        mediaRecorders.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //设置输入格式 3gp
        mediaRecorders.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); //设置编码 AMR
    }

    public void recod(){
        mStartingTimeMillis = System.currentTimeMillis();
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH);
        if(!path.exists())
        {
            path.mkdirs();
        }
        try {
            audioFile=new File(path,mName+".3gp");
            if(audioFile.exists())
            {
                audioFile.delete();
            }
            audioFile.createNewFile();//创建文件

        } catch (Exception e) {
            throw new RuntimeException("Couldn't create recording audio file", e);
        }

        mediaRecorders.setOutputFile(audioFile.getAbsolutePath()); //设置输出文件

        try {
            mediaRecorders.prepare();
        } catch (IllegalStateException e) {
            throw new RuntimeException("IllegalStateException on MediaRecorder.prepare", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException on MediaRecorder.prepare", e);
        }
        isRecoding=true;
        mediaRecorders.start();
    }
    public void stop(){
        if(isRecoding) {
            mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
            isRecoding=false;
            mediaRecorders.stop();
            mediaRecorders.release();
            try {
                mFilePath=Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH+mName+".3gp";
                mName=mName+ "_" + (new DBHelper(mContext).getCount() + 1) +".3gp";
                new DBHelper(mContext).addRecording(mName, mFilePath, mElapsedMillis,0,3);
            } catch (Exception e){

            }
        }
    }

}
