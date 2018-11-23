package com.app.soundrecord.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.soundrecord.application.DBHelper;
import com.app.soundrecord.R;
import com.app.soundrecord.application.XApplication;
import com.app.soundrecord.application.XRecordConfig;
import com.app.soundrecord.core.XCoreFactory;
import com.app.soundrecord.bean.DeleteItem;
import com.app.soundrecord.bean.RecordingItem;
import com.app.soundrecord.core.allselect.intf.IAllSelectListener;
import com.app.soundrecord.core.allselect.intf.IAllseclectManager;
import com.app.soundrecord.core.delete.intf.IDeleteListener;
import com.app.soundrecord.core.delete.intf.IDeletetManager;

import com.app.soundrecord.core.number.intf.INumberManager;
import com.app.soundrecord.core.play.intf.IPlayListener;
import com.app.soundrecord.core.play.intf.IPlayManager;
import com.app.soundrecord.core.size.intf.ISiizeManager;
import com.app.soundrecord.core.toobar.intf.IToolbarListener;
import com.app.soundrecord.core.toobar.intf.IToolbarManager;
import com.app.soundrecord.main.fragment.DetailFragment;

import com.app.soundrecord.core.audio.listeners.OnDatabaseChangedListener;
import com.app.soundrecord.util.SettingUtils;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ulric.li.permission.PermissionHelper;

import static com.app.soundrecord.util.FileUtils.AUDIO_WAV_BASEPATH;


public class FileViewerAdapter extends RecyclerView.Adapter<FileViewerAdapter.RecordingsViewHolder>
    implements OnDatabaseChangedListener {

    private static final String LOG_TAG = "FileViewerAdapter";

    private DBHelper mDatabase;

    RecordingItem item;
    Context mContext;
    LinearLayoutManager llm;
    List<DeleteItem> midList=new ArrayList<>();
    private Map<Integer,Boolean> map=new HashMap<>();// 存放已被选中的CheckBox
    private boolean isSelectedState =false;
    private IToolbarManager miToolbarManager;
    private IAllseclectManager miAllseclectManager;
    private IDeletetManager miDeletetManager;
    private ISiizeManager miSiizeManager;
    private IPlayManager miPlayManager;
    private INumberManager miNumberManager;
    private Context mActivity;



    public FileViewerAdapter(Context activity,Context context, LinearLayoutManager linearLayoutManager) {
        super();
//        timer.schedule(timerTask,
//                5 * 1000,//延迟5秒执行
//                1000);//周期为1秒
        mActivity=activity;
        mContext = context;
        mDatabase = new DBHelper(mContext);
        mDatabase.setOnDatabaseChangedListener(this);
        llm = linearLayoutManager;
        miSiizeManager=(ISiizeManager) XCoreFactory.getInstance().createInstance(ISiizeManager.class);
        miDeletetManager=(IDeletetManager) XCoreFactory.getInstance().createInstance(IDeletetManager.class);
        miDeletetManager.addListener(iDeleteListener);
        miToolbarManager=(IToolbarManager) XCoreFactory.getInstance().createInstance(IToolbarManager.class);
        miToolbarManager.addListener(iToolbarListener);
        miAllseclectManager=(IAllseclectManager) XCoreFactory.getInstance().createInstance(IAllseclectManager.class);
        miAllseclectManager.addListener(iAllSelectListener);
        miPlayManager=(IPlayManager) XCoreFactory.getInstance().createInstance(IPlayManager.class);
        miPlayManager.addListener(iPlayListener);
        miNumberManager=(INumberManager) XCoreFactory.getInstance().createInstance(INumberManager.class);


    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(final RecordingsViewHolder holder, int position) {

        item = getItem(position);
        if (!fileIsExists(item.getFilePath())){
            update(position);
            return;
        }
        long itemDuration = item.getLength();
        long hours =TimeUnit.MILLISECONDS.toHours(itemDuration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration)-TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)- TimeUnit.MINUTES.toSeconds(minutes)-TimeUnit.HOURS.toSeconds(hours);
        holder.vLength.setText(String.format("%02d:%02d:%02d",hours,minutes, seconds));
        if (getItem(position).getId()== SettingUtils.getpositions()){
            holder.ivHorn.setVisibility(View.VISIBLE);
        }else {
            holder.ivHorn.setVisibility(View.GONE);
        }

        if (isSelectedState){
            holder.file_check.setVisibility(View.VISIBLE);
            holder.voice_img_more.setVisibility(View.GONE);

            miSiizeManager.toUpdate(midList.size()+"");

        }else {
            holder.file_check.setVisibility(View.GONE);
            holder.voice_img_more.setVisibility(View.VISIBLE);
        }
//        holder.file_check.setChecked(false);
//        if (midList.size()==0){
//            holder.file_check.setChecked(false);
//        }else {
//            for (int i=0 ; i<midList.size();i++){
//                if (midList.get(i).getmId()==getItem(position).getId()){
//                    holder.file_check.setChecked(true);
//                    Log.d("ZZZ","position0  == "+position+ "   mId   ="+midList.get(i).getmId());
//                    break;
//                }
//            }
//        }
        if (midList.size()==mDatabase.getCount()){
            holder.file_check.setChecked(true);
        }else if(map!=null&&map.containsKey(getItem(position).getId())){
            holder.file_check.setChecked(true);
        }else {
            holder.file_check.setChecked(false);
        }
        try {
            String id = item.getName().substring(item.getName().indexOf("_"));
            String mid=id.substring(0, id.indexOf("."));
            holder.vName.setText(item.getName().replace(mid,""));
        }catch (Exception e){

        }
        holder.vDateAdded.setText(
            DateUtils.formatDateTime(
                mContext,
                item.getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
            )
        );
        holder.file_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSelectedState){
                    for (int i=0 ; i<midList.size();i++){
                        if (midList.get(i).getmId()==getItem(position).getId()){
                            midList.remove(midList.get(i));
                            map.remove(getItem(position).getId());
                            holder.file_check.setChecked(false);
                            miSiizeManager.toUpdate(midList.size()+"");
                            return;
                        }
                    }
                    holder.file_check.setChecked(true);
                    map.put(getItem(position).getId(),true);
                    Log.d("ZZZ","position1  == "+position+ "   mId   ="+getItem(position).getId());
                    DeleteItem deleteItem=new DeleteItem();
                    deleteItem.setmFilePath(getItem(position).getFilePath());
                    deleteItem.setmId(getItem(position).getId());
                    midList.add(deleteItem);
                    miSiizeManager.toUpdate(midList.size()+"");
                }else {
                    mHanlder.sendEmptyMessage(1);
                    SettingUtils.putpositions(getItem(position).getId());
                    holder.ivHorn.setVisibility(View.VISIBLE);
                    miPlayManager.toShow(item);
                }
            }
        });
        // define an on click listener to open PlaybackFragment
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionHelper.getInstance(mContext).requestPermission(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionHelper.PermissionGrantedCallback(){
                    @Override
                    public void onGranted(List<String> permissions) {
                        if (isSelectedState){
                            for (int i=0 ; i<midList.size();i++){
                                if (midList.get(i).getmId()==getItem(position).getId()){
                                    midList.remove(midList.get(i));
                                    map.remove(getItem(position).getId());
                                    holder.file_check.setChecked(false);
                                    miSiizeManager.toUpdate(midList.size()+"");
                                    return;
                                }
                            }
                            holder.file_check.setChecked(true);
                            map.put(getItem(position).getId(),true);
                            Log.d("ZZZ","position2  == "+position+ "   mId   ="+getItem(position).getId());
                            DeleteItem deleteItem=new DeleteItem();
                            deleteItem.setmFilePath(getItem(position).getFilePath());
                            deleteItem.setmId(getItem(position).getId());
                            midList.add(deleteItem);
                            miSiizeManager.toUpdate(midList.size()+"");
                        }else {
                            mHanlder.sendEmptyMessage(1);
                            SettingUtils.putpositions(getItem(position).getId());
                            holder.ivHorn.setVisibility(View.VISIBLE);
                            miPlayManager.toShow(getItem(position));
                        }
                    }
                });


            }
        });
        holder.voice_img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> entrys = new ArrayList<String>();
                entrys.add(mContext.getString(R.string.dialog_file_rename));
                entrys.add(mContext.getString(R.string.dialog_file_delete));
                entrys.add(mContext.getString(R.string.dialog_file_detail));

                final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);
//                int position=holder.getPosition();

                // File delete confirm
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                String id = getItem(position).getName().substring(getItem(position).getName().indexOf("_"));
                String mid=id.substring(0, id.indexOf("."));
                builder.setTitle(getItem(position).getName().replace(mid,""));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            if (((XApplication)((Activity)mContext).getApplication()).isRecording()){
                                Toast.makeText(mContext, mContext.getString(R.string.do_not_update_name), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            renameFileDialog(position,getItem(position).getName());
                        } if (item == 1) {
                            deleteFileDialog(position);
                        } else if (item == 2) {
//                            shareFileDialog(holder.getPosition());
                            try {
                                DetailFragment playbackFragment =
                                        new DetailFragment().newInstance(getItem(position));

                                FragmentTransaction transaction = ((FragmentActivity) mContext)
                                        .getSupportFragmentManager()
                                        .beginTransaction();

                                playbackFragment.show(transaction, "dialog_detail");

                            } catch (Exception e) {
                                Log.e(LOG_TAG, "exception", e);
                            }
                        }
                    }
                });
                builder.setCancelable(true);
                builder.setNegativeButton(mContext.getString(R.string.dialog_action_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
    }
    private final Timer timer = new Timer();
    /**
     * 定义并初始化定时器任务
     * */
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            /**
             * 此处执行任务
             * */
            mHanlder.sendEmptyMessage(1);//通知UI更新
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public RecordingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_view, parent, false);

        mContext = parent.getContext();

        return new RecordingsViewHolder(itemView);
    }

    public static class RecordingsViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vLength;
        protected TextView vDateAdded;
        protected View cardView;
        protected ImageView voice_img_more;
        protected CheckBox file_check;
        protected ImageView ivHorn;
        public RecordingsViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.file_name_text);
            vLength = (TextView) v.findViewById(R.id.file_length_text);
            vDateAdded = (TextView) v.findViewById(R.id.file_date_added_text);
            voice_img_more=v.findViewById(R.id.voice_img_more);
            cardView = v.findViewById(R.id.card_view);
            file_check=v.findViewById(R.id.file_check);
            ivHorn = v.findViewById(R.id.iv_horn);
        }
    }

    @Override
    public int getItemCount() {

        return mDatabase.getCount();
    }

    public RecordingItem getItem(int position) {
        return mDatabase.getItemAt(position);
    }

    @Override
    public void onNewDatabaseEntryAdded() {
        //item added to top of the list
        notifyItemInserted(getItemCount() - 1);
        llm.scrollToPosition(getItemCount() - 1);
        SettingUtils.putnameNumber((SettingUtils.getnameNumber()+1));
        miNumberManager.toUpdate();

        Toast.makeText(mContext, R.string.sring_preservation,Toast.LENGTH_LONG).show();
        Log.d("ZZZ","getnameNumber ="+SettingUtils.getnameNumber());
        Log.d("ZZZ","getnameNumber+1 ="+(SettingUtils.getnameNumber()+1));


    }

    private IDeleteListener iDeleteListener=new IDeleteListener() {
        @Override
        public void toDelete() {
            super.toDelete();
            if (midList.size()==0){
                return;
            }
            for (int i=0 ; i<midList.size();i++){
                File file = new File(midList.get(i).getmFilePath());
                file.delete();
                mDatabase.removeItemWithId(midList.get(i).getmId());
            }
            isSelectedState=false;
            midList.clear();
            miToolbarManager.toClose();
            notifyDataSetChanged();
            miNumberManager.toUpdate();


        }
    };
    private IToolbarListener iToolbarListener =new IToolbarListener() {
        @Override
        public void toClose() {
            super.toClose();
            midList.clear();
            map.clear();
            isSelectedState=false;
            notifyDataSetChanged();
        }

        @Override
        public void toOpen() {
            super.toOpen();
            midList.clear();
            map.clear();
            isSelectedState=true;
            notifyDataSetChanged();
        }
    };
    private IAllSelectListener iAllSelectListener=new IAllSelectListener() {
        @Override
        public void toAllSelect() {
            super.toAllSelect();
            onAllSelect();
        }
    };
    private IPlayListener iPlayListener =new IPlayListener() {


        @Override
        public void toClose() {
            super.toClose();
            notifyDataSetChanged();
        }
    };

    @Override
    //TODO
    public void onDatabaseEntryRenamed() {

    }

    public void update(int position){
        mDatabase.removeItemWithId(getItem(position).getId());
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                notifyDataSetChanged();
            }
        };
        handler.post(r);


    }
    public void remove(int position) {
        //remove item from database, recyclerview and storage
        //delete file from storage
        File file = new File(getItem(position).getFilePath());
        file.delete();
        mDatabase.removeItemWithId(getItem(position).getId());
        notifyDataSetChanged();
        miNumberManager.toUpdate();


    }
    //TODO
    public void removeOutOfApp(String filePath) {
        //user deletes a saved recording out of the application through another application
    }
    public void rename(int position, String name) {
        //rename a file

        String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH + name;
        File f = new File(mFilePath);

        if (f.exists() && !f.isDirectory()) {
            //file name is not unique, cannot rename file.
            Toast.makeText(mContext,
                    String.format(mContext.getString(R.string.toast_file_exists), name),
                    Toast.LENGTH_SHORT).show();

        } else {
            //file name is unique, rename file
            File oldFilePath = new File(getItem(position).getFilePath());
            oldFilePath.renameTo(f);
            mDatabase.renameItem(getItem(position), name, mFilePath);
            notifyItemChanged(position);
        }
    }
    public void renameFileDialog (final int position,String name) {
        String id = name.substring(name.indexOf("_"));
        String mid=id.substring(0, id.indexOf("."));
        String names =name.substring(0, name.indexOf("_"));
        // File rename dialog
        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_rename_file, null);

        final EditText input = (EditText) view.findViewById(R.id.new_name);
        final TextView textView = (TextView) view.findViewById(R.id.textView);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int s, int i1, int i2) {
                String editable = input.getText().toString();
                String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";//正则表达式
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(editable);
                String str = m.replaceAll("").trim();    //删掉不是字母或数字的字符
                if(!editable.equals(str)){
                    input.setText(str);  //设置EditText的字符
                }
                int mTextMaxlenght = 0;
                Editable editables = input.getText();
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
                        input.setText(newStr);
                        // 得到新字段的长度值
                        editables = input.getText();
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

        if (getItem(position).getCode()==1){
            textView.setText(".wav");
        }else if (getItem(position).getCode()==2){
            textView.setText(".mp4");
        }else if (getItem(position).getCode()==3){
            textView.setText(".3gp");
        }

        renameFileBuilder.setTitle(mContext.getString(R.string.dialog_title_rename));
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton(mContext.getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (input.getText().toString().trim().equals("")){
                            Toast.makeText(mContext,R.string.string_notempty,Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (input.getText().toString().trim().equals(names)){
                            Toast.makeText(mContext, R.string.string_not_same,Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (int nIndex = 0; nIndex < new DBHelper(mContext).getCount(); nIndex++){
                            if (input.getText().toString()
                                    .equals(new DBHelper(mContext).getItemAt(nIndex).getName().substring(0, new DBHelper(mContext).getItemAt(nIndex).getName().indexOf("_"))))
                            {
                                Toast.makeText(mContext, R.string.string_already,Toast.LENGTH_SHORT).show();
                                return;
                            }else {
                                continue;
                            }
                        }
                        try {
                            if (getItem(position).getCode()==1){
                                String value = input.getText().toString().trim()+ mid+ ".wav";
                                rename(position, value);
                            }else if (getItem(position).getCode()==2){
                                String value = input.getText().toString().trim()+ mid + ".mp4";
                                rename(position, value);
                            }else if (getItem(position).getCode()==3){
                                String value = input.getText().toString().trim()+ mid + ".3gp";
                                rename(position, value);
                            }


                        } catch (Exception e) {
                            Log.e(LOG_TAG, "exception", e);
                        }

                        dialog.cancel();
                    }
                });
        renameFileBuilder.setNegativeButton(mContext.getString(R.string.dialog_action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        renameFileBuilder.setView(view);
        AlertDialog alert = renameFileBuilder.create();
        alert.show();
    }
    public void deleteFileDialog (final int position) {
        // File delete confirm
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(mContext);
        confirmDelete.setTitle(mContext.getString(R.string.dialog_title_delete));
        confirmDelete.setMessage(mContext.getString(R.string.dialog_text_delete));
        confirmDelete.setCancelable(true);
        confirmDelete.setPositiveButton(mContext.getString(R.string.dialog_action_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            //remove item from database, recyclerview, and storage
                            remove(position);

                        } catch (Exception e) {
                            Log.e(LOG_TAG, "exception", e);
                        }

                        dialog.cancel();
                    }
                });
        confirmDelete.setNegativeButton(mContext.getString(R.string.dialog_action_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

    AlertDialog alert = confirmDelete.create();
        alert.show();
}
    public void onAllSelect(){
        if (midList.size()==mDatabase.getCount()){
            midList.clear();
            map.clear();
            notifyDataSetChanged();
        }else {
            midList.clear();
            map.clear();
            for (int i=0 ; i<mDatabase.getCount();i++){
                DeleteItem deleteItem=new DeleteItem();
                deleteItem.setmFilePath(getItem(i).getFilePath());
                deleteItem.setmId(getItem(i).getId());
                midList.add(deleteItem);
                map.put(getItem(i).getId(),true);
            }
            notifyDataSetChanged();
        }


    }

    //判断文件是否存在
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

    public void onstop(){
        timer.cancel();
        if (miToolbarManager != null) {
            miToolbarManager.removeListener(iToolbarListener);
        }
        if (miAllseclectManager != null) {
            miAllseclectManager.removeListener(iAllSelectListener);
        }
        if (miDeletetManager != null) {
            miDeletetManager.removeListener(iDeleteListener);
        }
        if (miPlayManager != null) {
            miPlayManager.removeListener(iPlayListener);
        }
    }


}
