package com.app.soundrecord.main.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.soundrecord.R;
import com.app.soundrecord.bean.RecordingItem;

import java.util.concurrent.TimeUnit;

public class DetailFragment extends DialogFragment {
    private RecordingItem item;

    private TextView dialog_detail_name = null;
    private TextView dialog_detail_time = null;
    private TextView dialog_detail_length = null;
    private TextView dialog_detail_samping = null;
    private TextView dialog_detail_encoming = null;

    private static final String ARG_ITEM = "detail_item";

    public DetailFragment newInstance(RecordingItem item) {
        DetailFragment f = new DetailFragment();
        Bundle b = new Bundle();
        b.putParcelable(ARG_ITEM, item);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = getArguments().getParcelable(ARG_ITEM);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_detail, null);
        dialog_detail_encoming=view.findViewById(R.id.dialog_detail_encoming);
        dialog_detail_samping=view.findViewById(R.id.dialog_detail_samping);
        dialog_detail_length=view.findViewById(R.id.dialog_detail_length);
        dialog_detail_time=view.findViewById(R.id.dialog_detail_time);
        dialog_detail_name=view.findViewById(R.id.dialog_detail_name);
        String id = item.getName().substring(item.getName().indexOf("_"));
        String mid=id.substring(0, id.indexOf("."));
        dialog_detail_name.setText(item.getName().replace(mid,""));
        dialog_detail_time.setText( DateUtils.formatDateTime(
                getContext(),
                item.getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
        ));
        long itemDuration = item.getLength();
        long hours =TimeUnit.MILLISECONDS.toHours(itemDuration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration)-TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)- TimeUnit.MINUTES.toSeconds(minutes)-TimeUnit.HOURS.toSeconds(hours);
        dialog_detail_length.setText(String.format("%02d:%02d:%02d",hours,minutes, seconds));

        if (item.getmSampling()==44100){
            dialog_detail_samping.setText("High");
        }else if (item.getmSampling()==16000){
            dialog_detail_samping.setText("Normal");
        }else if (item.getmSampling()==8000){
            dialog_detail_samping.setText("Lower");
        }
        if (item.getCode()==1){
            dialog_detail_encoming.setText(".WAV");
        }else  if (item.getCode()==2){
            dialog_detail_encoming.setText(".MP4");
        }else if (item.getCode()==3){
            dialog_detail_encoming.setText(".3GP");
        }
        builder.setView(view);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM; // 显示在底部
        params.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度填充满屏
        window.setAttributes(params);
        window.setBackgroundDrawableResource(android.R.color.transparent);

        //disable buttons from dialog
        AlertDialog alertDialog = (AlertDialog) getDialog();
        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_NEUTRAL).setEnabled(false);
    }
}
