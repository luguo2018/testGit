package com.jmhy.sdk.hotfix;

import android.util.Log;

import com.jmhy.sdk.config.AppConfig;
import com.taobao.sophix.SophixManager;

public class HotFixManager {
    private static long oldTime = 0;
    private static final String TAG = "HotFixManager";
    public static void inquire() {
        long nowTime = System.currentTimeMillis();
        if (AppConfig.ali_hot_fix == 1 && nowTime - oldTime > 3000) {
            Log.e(TAG, "inquire:");
            SophixManager.getInstance().queryAndLoadNewPatch();
        }
        oldTime = nowTime;
    }
}
