package com.app.soundrecord.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class HasNullDataViewRecyclerView extends RecyclerView {

    View nullView;

    public HasNullDataViewRecyclerView(Context context) {
        super(context);
    }

    public HasNullDataViewRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HasNullDataViewRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            checkIfEmpty();
        }
    };

    public void setNullView(View view){
        this.nullView = view;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null){
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter!=null){
            adapter.registerAdapterDataObserver(observer);
        }
        checkIfEmpty();
    }
    private void checkIfEmpty(){
        if (nullView != null && getAdapter() != null){
            if (getAdapter().getItemCount() == 0){
                nullView.setVisibility(VISIBLE);
                this.setVisibility(GONE);
            }else{
                nullView.setVisibility(GONE);
                this.setVisibility(VISIBLE);
            }
        }
    }

}
