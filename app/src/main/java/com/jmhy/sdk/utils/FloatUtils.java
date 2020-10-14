package com.jmhy.sdk.utils;

import android.app.Activity;
import android.util.Log;

import com.jmhy.sdk.view.FloatView;

public class FloatUtils {
    private static FloatView mFloatView;

    public static void showFloat(Activity activity) {
        if(mFloatView == null){
            mFloatView = new FloatView(activity);
            mFloatView.show();
        }
    }

    public static void showFloatRedDot() {
        if(mFloatView != null){
            Log.i("jimisdk测试","悬浮窗不为空，展示红点"+mFloatView);
            mFloatView.setRedDotState();
        }else{
            Log.i("jimisdk测试","悬浮窗为空");
        }
    }
    /*public static void hideFloat() {
        if (mFloatView != null) {
            mFloatView.hide();
        }
    }*/

    public static void destroyFloat() {
        if (mFloatView != null) {
            mFloatView.destroy();
            mFloatView = null;
        }
    }

    public static void hiddenTip(int index){
        if (mFloatView != null) {
            mFloatView.hiddenTip(index);
        }
    }

    public static void showTip(int index){
        if (mFloatView != null) {
            mFloatView.showTip(index);
        }
    }
}
