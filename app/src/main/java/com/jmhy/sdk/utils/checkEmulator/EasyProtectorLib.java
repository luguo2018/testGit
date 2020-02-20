package com.jmhy.sdk.utils.checkEmulator;

import android.content.Context;

/**
 * Project Name:EasyProtector
 * Package Name:com.lahm.library
 * Created by lahm on 2018/5/14 下午9:38 .
 */
public class EasyProtectorLib {


    public static boolean checkIsRunningInEmulator(Context context, EmulatorCheckCallback callback) {
        return EmulatorCheckUtil.getSingleInstance().readSysProperty(context, callback);
    }


}
