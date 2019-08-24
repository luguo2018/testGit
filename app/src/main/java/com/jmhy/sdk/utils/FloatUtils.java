package com.jmhy.sdk.utils;

import android.app.Activity;
import android.content.Context;

import com.jmhy.sdk.view.FloatView;

public class FloatUtils {
    private static FloatView mFloatView;

    public static void showFloat(Activity activity) {
        if(mFloatView != null){
            mFloatView.destroy();
            mFloatView = null;
        }
        mFloatView = new FloatView(activity);
        mFloatView.show();
    }

    /*public static void hideFloat() {
        if (mFloatView != null) {
            mFloatView.hide();
        }
    }*/

    public static void destroyFloat() {
        if (mFloatView != null) {
            mFloatView.destroy();
        }
        mFloatView = null;
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
