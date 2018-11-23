package com.app.soundrecord.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.soundrecord.R;


/**
 * on 2018/8/15-13:00
 */
public class MyToolbar extends FrameLayout {
    ImageView ivClose;
    TextView tvTitle;
    TextView tvRight;

    public MyToolbar(@NonNull Context context) {
        this(context, null);
    }

    public MyToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.layout_toolbar, this);
        ivClose = findViewById(R.id.iv_close);
        tvTitle = findViewById(R.id.tv_title);
        tvRight = findViewById(R.id.tv_right);
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClicked();
            }
        });
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyToolbar);
        String title = array.getString(R.styleable.MyToolbar_mytoolbar_title);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        String rightText = array.getString(R.styleable.MyToolbar_mytoolbar_right_text);
        if (!TextUtils.isEmpty(rightText)) {
            tvRight.setText(rightText);
        }
        Drawable icon = array.getDrawable(R.styleable.MyToolbar_mytoolbar_icon);
        if(icon!=null){
            ivClose.setImageDrawable(icon);
        }
        array.recycle();
    }
    public void  setIcon(Drawable icon){
        if (icon!=null){
            ivClose.setImageDrawable(icon);
        }

    }
    public void setOnRightClickListener(OnClickListener listener) {
        if (listener != null) {
            tvRight.setOnClickListener(listener);
        }
    }

    public void setTitle(String text){
        tvTitle.setText(text);
    }

    public void performRightTextViewClick() {
        tvRight.performClick();
    }

    public void setRightText(String text) {
        tvRight.setText(text);
    }

    public void setOnClickCloseListener(OnClickListener listener) {
        if (listener != null) {
            ivClose.setOnClickListener(listener);
        }
    }

    public void onViewClicked() {
        Context context = getContext();
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}
