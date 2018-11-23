package com.app.soundrecord.main.drawer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.soundrecord.R;
import com.app.soundrecord.main.drawer.bean.DialogBean;

import java.util.List;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder> {

    public interface OnItemClickListener{
        void ItemClickListener(int position,DialogBean str,int type);
    }

    Context mContext;
    List<DialogBean> list;
    OnItemClickListener  onItemClickListener;
    int type;
    int width;
    public DialogAdapter(Context mContext,List<DialogBean> list){
        this.mContext = mContext;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    public void setList(List<DialogBean> list,int type) {
        if (list != null) {
            this.list.addAll(list);
        }
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list!=null && list.size() > 0){
            DialogBean bean = list.get(position);
            holder.mTvText.setText(bean.getmName());
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.mTvText.getLayoutParams();
            params.width = width;
            holder.mTvText.setLayoutParams(params);
            if (bean.ismIsChecked()){
                holder.mTvText.setTextColor(Color.parseColor("#ff3333"));
            }else{
                holder.mTvText.setTextColor(Color.parseColor("#000000"));
            }
            if (onItemClickListener != null){
                holder.mTvText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.ItemClickListener(position,list.get(position),type);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return list !=null ? list.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView mTvText;
        public ViewHolder(View itemView) {
            super(itemView);
            mTvText = itemView.findViewById(R.id.item_tv_text);
        }
    }
}
