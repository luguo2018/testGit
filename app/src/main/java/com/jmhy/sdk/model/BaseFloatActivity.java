package com.jmhy.sdk.model;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 将视图添加在游戏的activity中，防止游戏暂停掉线。
 * 先调用setContentView方法，然后重写setViews方法
 */
public abstract class BaseFloatActivity implements View.OnClickListener {
    protected View contentView;
    protected Activity activity;

    public BaseFloatActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * 设置布局动态添加
     *
     * @param layout_id 布局id
     */
    public void setContentView(@NonNull int layout_id) {
        View contentView = View.inflate(activity, layout_id, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.contentView = contentView;
        activity.addContentView(contentView, lp);

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
    public abstract void setViews(@Nullable String url);

    /**
     * 移除布局
     */
    public abstract void removeContentView();

    /**
     * 子view点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

    }
}
