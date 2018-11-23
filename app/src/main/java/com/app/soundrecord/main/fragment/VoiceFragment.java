package com.app.soundrecord.main.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.soundrecord.R;
import com.app.soundrecord.application.DBHelper;
import com.app.soundrecord.application.XApplication;
import com.app.soundrecord.bean.RecordingItem;
import com.app.soundrecord.core.XCoreFactory;
import com.app.soundrecord.adapters.FileViewerAdapter;
import com.app.soundrecord.base.BaseFragment;
import com.app.soundrecord.core.delete.intf.IDeletetManager;

import com.app.soundrecord.core.number.intf.INumberListener;
import com.app.soundrecord.core.number.intf.INumberManager;
import com.app.soundrecord.core.play.intf.IPlayListener;
import com.app.soundrecord.core.play.intf.IPlayManager;
import com.app.soundrecord.core.size.intf.ISiizeManager;
import com.app.soundrecord.core.size.intf.ISizeListener;
import com.app.soundrecord.core.toobar.intf.IToolbarListener;
import com.app.soundrecord.core.toobar.intf.IToolbarManager;
import com.app.soundrecord.util.OnClickListener;
import com.app.soundrecord.util.SettingUtils;
import com.app.soundrecord.util.StorageUtil;
import com.app.soundrecord.view.HasNullDataViewRecyclerView;
import com.melnykov.fab.FloatingActionButton;


import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.app.soundrecord.util.FileUtils.AUDIO_WAV_BASEPATH;


public class VoiceFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    HasNullDataViewRecyclerView mRecyclerView;
    @BindView(R.id.voice_tv_size)
    TextView voice_tv_size;
    @BindView(R.id.aaa)
    RelativeLayout aaa;
    @BindView(R.id.null_data_view)
    View emptyDataView;
    @BindView(R.id.file_name_text_view)
    TextView mFileNameTextView;
    @BindView(R.id.file_length_text_view)
    TextView mFileLengthTextView;
    @BindView(R.id.current_progress_text_view)
    TextView mCurrentProgressTextView;
    @BindView(R.id.seekbar)
    SeekBar mSeekBar;
    @BindView(R.id.fab_play)
    ImageView mPlayButton;
    @BindView(R.id.mediaplayer_view)
    RelativeLayout mediaplayer_view;



    private FileViewerAdapter mFileViewerAdapter;
    private IToolbarManager miToolbarManager;
    private IDeletetManager miDeletetManager;
    private ISiizeManager miSiizeManager;
    private INumberManager miNumberManager;

    //播放器
    private RecordingItem item;

    private Handler mHandler = new Handler();

    private MediaPlayer mMediaPlayer = null;


    private IPlayManager miPlayManager;

    //stores whether or not the mediaplayer is currently playing audio


    //stores minutes and seconds of the length of the file.
    long minutes = 0;
    long seconds = 0;
    long hours=0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observer.startWatching();

    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void init(View rootView) {
        miNumberManager=(INumberManager) XCoreFactory.getInstance().createInstance(INumberManager.class);
        miNumberManager.addListener(iNumberListener);
        miPlayManager=(IPlayManager) XCoreFactory.getInstance().createInstance(IPlayManager.class);
        miPlayManager.addListener(iPlayListener);
        miSiizeManager=(ISiizeManager) XCoreFactory.getInstance().createInstance(ISiizeManager.class);
        miDeletetManager=(IDeletetManager) XCoreFactory.getInstance().createInstance(IDeletetManager.class);
        miToolbarManager=(IToolbarManager) XCoreFactory.getInstance().createInstance(IToolbarManager.class);
        miToolbarManager.addListener(iToolbarListener);
        miSiizeManager.addListener(iSizeListener);
        try {
            voice_tv_size.setText("In total "+new DBHelper(getActivity()).getCount()+" voice files");
        }catch (Exception e){

        }
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.item_divider));
        mRecyclerView.setNullView(emptyDataView);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mFileViewerAdapter = new FileViewerAdapter(getActivity().getBaseContext(),getActivity(), llm);
        mRecyclerView.setAdapter(mFileViewerAdapter);
        aaa.setOnClickListener(new OnClickListener() {
            @Override
            protected void myOnClickListener(View v) {

                AlertDialog.Builder confirmDelete = new AlertDialog.Builder(getActivity());
                confirmDelete.setTitle(getActivity().getString(R.string.dialog_title_delete));
                confirmDelete.setMessage(getActivity().getString(R.string.dialog_text_deletes));
                confirmDelete.setCancelable(true);
                confirmDelete.setPositiveButton(getActivity().getString(R.string.dialog_action_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                miDeletetManager.toDelete();
                                dialog.cancel();
                            }
                        });
                confirmDelete.setNegativeButton(getActivity().getString(R.string.dialog_action_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = confirmDelete.create();
                alert.show();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 1000);

            }
        });
        ColorFilter filter = new LightingColorFilter
                (getResources().getColor(R.color.colorRed), getResources().getColor(R.color.colorRed));
        mSeekBar.getProgressDrawable().setColorFilter(filter);
        mSeekBar.getThumb().setColorFilter(filter);


        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item==null){
                    return;
                }
                onPlay(((XApplication)(getActivity()).getApplication()).isPlaying());
                ((XApplication)(getActivity()).getApplication()).setPlaying(!((XApplication)(getActivity()).getApplication()).isPlaying());
            }
        });





    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_voice;
    }

    @Override
    protected void onLazyLoad() {

    }

    private ISizeListener iSizeListener=new ISizeListener() {
        @Override
        public void toUpdate(String size) {
            super.toUpdate(size);
            if (voice_tv_size!=null) {
                voice_tv_size.setText(size + " " + getResources().getString(R.string.fragment_select));
            }
        }
    };
    private IToolbarListener iToolbarListener =new IToolbarListener() {
        @Override
        public void toClose() {
            super.toClose();
            if (voice_tv_size!=null && aaa!=null) {
                aaa.setVisibility(View.GONE);
                mediaplayer_view.setVisibility(View.VISIBLE);
                try {
                    voice_tv_size.setText("In total "+new DBHelper(getActivity()).getCount()+" voice files");
                }catch (Exception e){

                }
            }
        }

        @Override
        public void toOpen() {
            super.toOpen();
            if (voice_tv_size!=null && aaa!=null) {
                voice_tv_size.setText("0" + " " + getResources().getString(R.string.fragment_select));
                Log.d("zzz", "VoiceFragment选中状态");
                aaa.setVisibility(View.VISIBLE);
                mediaplayer_view.setVisibility(View.GONE);
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFileViewerAdapter!=null){
            mFileViewerAdapter.onstop();
            SettingUtils.putpositions(-1);
        }
        miPlayManager.toClose();
//        if (mMediaPlayer != null) {
//            stopPlaying();
//        }
        if (miToolbarManager != null) {
            miToolbarManager.removeListener(iToolbarListener);
        }
        if (miNumberManager != null) {
            miNumberManager.removeListener(iNumberListener);
        }
        if (miSiizeManager != null) {
            miSiizeManager.removeListener(iSizeListener);
        }
        if (miPlayManager != null) {
            miPlayManager.removeListener(iPlayListener);
        }



    }

    FileObserver observer =
            new FileObserver(Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH ) {
                // set up a file observer to watch this directory on sd card
                @Override
                public void onEvent(int event, String file) {
                    if(event == FileObserver.DELETE){
                        // user deletes a recording file out of the app

                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH + file + "]";

                        // remove file from database and recyclerview
                        mFileViewerAdapter.removeOutOfApp(filePath);
                    }
                }
            };
    //updating mSeekBar
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mMediaPlayer != null){

                int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                mSeekBar.setProgress(mCurrentPosition);
                long hours =TimeUnit.MILLISECONDS.toHours(mCurrentPosition);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition)-TimeUnit.HOURS.toMinutes(hours);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition)- TimeUnit.MINUTES.toSeconds(minutes)-TimeUnit.HOURS.toSeconds(hours);
                if (mCurrentProgressTextView!=null)
                    mCurrentProgressTextView.setText(String.format("%02d:%02d:%02d",hours,minutes, seconds));
                updateSeekBar();
            }
        }
    };

    private void updateSeekBar() {
        mHandler.postDelayed(mRunnable, 1000);
    }

    private void prepareMediaPlayerFromPoint(int progress) {
        //set mediaPlayer to start from middle of the audio file

        mMediaPlayer = new MediaPlayer();

        try {
            if (item==null){
                return;
            }
            mMediaPlayer.setDataSource(item.getFilePath());
            mMediaPlayer.prepare();
            mSeekBar.setMax(mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(progress);

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });

        } catch (IOException e) {

        }

        //keep screen on while playing audio
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    private void stopPlaying() {
        mPlayButton.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;

        mSeekBar.setProgress(mSeekBar.getMax());
        ((XApplication)(getActivity()).getApplication()).setPlaying(!((XApplication)(getActivity()).getApplication()).isPlaying());
        if (mCurrentProgressTextView!=null)
            mCurrentProgressTextView.setText(mFileLengthTextView.getText());

        mSeekBar.setProgress(mSeekBar.getMax());

        //allow the screen to turn off again once audio is finished playing
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void onPlay(boolean isPlaying){
        if (!isPlaying) {
            //currently MediaPlayer is not playing audio
            if(mMediaPlayer == null) {
                startPlaying(); //start from beginning
            } else {
                resumePlaying(); //resume the currently paused MediaPlayer
            }

        } else {
            //pause the MediaPlayer
            pausePlaying();
        }
    }
    private void startPlaying() {
        mPlayButton.setImageResource(R.drawable.ic_media_pause);
        mMediaPlayer = new MediaPlayer();

        try {
            if (item==null){
                return;
            }
            mMediaPlayer.setDataSource(item.getFilePath());
            mMediaPlayer.prepare();
            mSeekBar.setMax(mMediaPlayer.getDuration());

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {

        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });

        updateSeekBar();

        //keep screen on while playing audio
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    private void resumePlaying() {
        mPlayButton.setImageResource(R.drawable.ic_media_pause);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.start();
        updateSeekBar();
    }
    private void pausePlaying() {
        mPlayButton.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        if (mMediaPlayer==null)
            return;
        mMediaPlayer.pause();
    }
    private INumberListener iNumberListener=new INumberListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void toUpdate() {
            super.toUpdate();
            try {
                voice_tv_size.setText("In total "+new DBHelper(getActivity()).getCount()+" voice files");
            }catch (Exception e){

            }

        }
    };

    private IPlayListener iPlayListener =new IPlayListener() {
        @SuppressLint("DefaultLocale")
        @Override
        public void toShow(RecordingItem recordingItem) {
            super.toShow(recordingItem);
            if (recordingItem==null)
                return;
            if (mMediaPlayer!=null){
                stopPlaying();
            }
            if (mCurrentProgressTextView!=null)
            mCurrentProgressTextView.setText("00:00:00");
            item=recordingItem;
            long itemDuration = item.getLength();
            long hours =TimeUnit.MILLISECONDS.toHours(itemDuration);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration)-TimeUnit.HOURS.toMinutes(hours);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)- TimeUnit.MINUTES.toSeconds(minutes)-TimeUnit.HOURS.toSeconds(hours);
            if (mFileLengthTextView!=null)
                mFileLengthTextView.setText(String.format("%02d:%02d:%02d",hours,minutes, seconds));
            String id = item.getName().substring(item.getName().indexOf("_"));
            String mid=id.substring(0, id.indexOf("."));
            if (mFileNameTextView!=null)
                mFileNameTextView.setText(item.getName().replace(mid,""));
            if (mSeekBar!=null){
                mSeekBar.setProgress(0);
                mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(mMediaPlayer != null && fromUser) {
                            mMediaPlayer.seekTo(progress);
                            mHandler.removeCallbacks(mRunnable);

                            long hours =TimeUnit.MILLISECONDS.toHours(mMediaPlayer.getCurrentPosition());
                            long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition())-TimeUnit.HOURS.toMinutes(hours);
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition())- TimeUnit.MINUTES.toSeconds(minutes)-TimeUnit.HOURS.toSeconds(hours);
                            if (mCurrentProgressTextView!=null)
                                mCurrentProgressTextView.setText(String.format("%02d:%02d:%02d",hours,minutes, seconds));


                            updateSeekBar();

                        } else if (mMediaPlayer == null && fromUser) {
                            prepareMediaPlayerFromPoint(progress);
                            updateSeekBar();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        if(mMediaPlayer != null) {
                            // remove message Handler from updating progress bar
                            mHandler.removeCallbacks(mRunnable);
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (mMediaPlayer != null) {
                            mHandler.removeCallbacks(mRunnable);
                            mMediaPlayer.seekTo(seekBar.getProgress());
                            long hours =TimeUnit.MILLISECONDS.toHours(mMediaPlayer.getCurrentPosition());
                            long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition())-TimeUnit.HOURS.toMinutes(hours);
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition())- TimeUnit.MINUTES.toSeconds(minutes)-TimeUnit.HOURS.toSeconds(hours);
                            if (mCurrentProgressTextView!=null)
                                mCurrentProgressTextView.setText(String.format("%02d:%02d:%02d",hours,minutes, seconds));mCurrentProgressTextView.setText(String.format("%02d:%02d:%02d",hours,minutes, seconds));
                            updateSeekBar();
                        }
                    }
                });
            }


//            onPlay(isPlaying);
//            isPlaying = !isPlaying;
        }

    };
}
