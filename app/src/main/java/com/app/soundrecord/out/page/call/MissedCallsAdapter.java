package com.app.soundrecord.out.page.call;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.soundrecord.R;

import java.util.List;


/**
 * Created by wanghailong on 2018/7/3.
 */

public class MissedCallsAdapter extends RecyclerView.Adapter<MissedCallsAdapter.MissedCallsViewHolder> {
    private List<String> mMissedCalls;

    public MissedCallsAdapter(List<String> mMissedCalls) {
        this.mMissedCalls = mMissedCalls;
    }

    @NonNull
    @Override
    public MissedCallsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_missed_calls, parent, false);
        return new MissedCallsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MissedCallsViewHolder holder, int position) {
        if (null != mMissedCalls && mMissedCalls.size() > 0 && mMissedCalls.get(position) != null) {
            String s = mMissedCalls.get(position);
            if (!TextUtils.isEmpty(s)) {
                String[] dateAndPhoneNum = s.split(",");
                if (dateAndPhoneNum.length == 2) {
                    holder.tv_time.setText(dateAndPhoneNum[0]);
                    holder.tv_phone_num.setText(dateAndPhoneNum[1]);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mMissedCalls == null) {
            return 0;
        }
        return mMissedCalls.size();
    }

    class MissedCallsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time;
        TextView tv_phone_num;

        public MissedCallsViewHolder(View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_phone_num = itemView.findViewById(R.id.tv_phone_num);
        }
    }


}
