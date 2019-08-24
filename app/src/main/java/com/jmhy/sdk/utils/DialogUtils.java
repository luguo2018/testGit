package com.jmhy.sdk.utils;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;

import com.jmhy.sdk.view.TipDialog;

/**
 * create by yhz on 2018/9/29
 */
public class DialogUtils {
    public static void showTip(Activity activity, String message){
        if(activity == null || activity.isFinishing()){
            Log.w("DialogUtils", "activity is null or finishing");
            return;
        }
        Dialog dialog = new TipDialog(activity, message);
        dialog.show();
    }
}
