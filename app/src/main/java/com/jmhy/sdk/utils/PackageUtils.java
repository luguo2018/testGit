package com.jmhy.sdk.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * create by yhz on 2019/8/21
 */
public class PackageUtils {
    private final static String TAG = PackageUtils.class.getSimpleName();

    public static boolean isInstall(Context context, String packageName){
        if(TextUtils.isEmpty(packageName)){
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName, 0);
            Log.i(TAG, packageName + " installed");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, packageName + "packageName uninstall");
            return false;
        }
    }
}
