package com.jmhy.sdk.model;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 将视图添加在游戏的activity中，防止游戏暂停掉线。
 * 重写setViews方法然后在setViews中调用setContentView
 */
public abstract class BaseFloatActivity implements View.OnClickListener {
    protected View contentView;
    protected Activity activity;
    protected LinearLayout.LayoutParams lp;
    protected boolean isShow;

    /**
     * 设置布局动态添加
     *
     * @param layout_id 布局id
     */
    public void setContentView(@NonNull int layout_id) {
        View contentView = View.inflate(activity, layout_id, null);
        lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.contentView = contentView;
    }

    public void show() {
        if (activity != null && contentView != null && !isShow) {
            activity.addContentView(contentView, lp);
        } else {
            Toast.makeText(activity,"正在显示或者Activity为null",Toast.LENGTH_LONG).show();
        }
    }
    public boolean isShow(){
        return isShow;
    }
    /**
     * 获取根布局
     *
     * @return
     */
    public View getContentView() {
        if (contentView == null) {
            throw new NullPointerException("please call setContentView ");
        } else {
            return contentView;
        }
    }

    /**
     * 设置子View的各种事件
     *
     * @param url web链接
     */
    public abstract void setViews(Activity activity, @Nullable String url);

    /**
     * 移除布局
     */
    public  void removeContentView(){
            isShow=false;
    }

    /**
     * 子view点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

    }
}
