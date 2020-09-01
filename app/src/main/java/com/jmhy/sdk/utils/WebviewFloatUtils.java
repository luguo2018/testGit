package com.jmhy.sdk.utils;

import android.app.Activity;
import android.content.Context;

import com.jmhy.sdk.activity.JmUserInfoAddView;


public class WebviewFloatUtils {
    private static JmUserInfoAddView mFloatView;

    public static void showUserCentent(Context context, Activity activity, String url) {
        if (mFloatView==null){
            mFloatView = new JmUserInfoAddView(context,activity,url);
        }else{
            mFloatView.showWebFloatView();
        }
    }



    public static void showFloatView(Context context, Activity activity, String url){
        if (mFloatView==null){
            mFloatView = new JmUserInfoAddView(context,activity,url);
        }
        mFloatView.showWebFloatView();
    }

    public static void hideWebFloatView(){
        mFloatView.hideWebFloatView();
    }
}
