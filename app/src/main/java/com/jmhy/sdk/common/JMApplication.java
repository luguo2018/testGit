package com.jmhy.sdk.common;

import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

public class JMApplication extends Application {
    private static final String TAG = "JMApplication";
    public static Class<JMApplication> clazz = JMApplication.class;
    @Override
    public void onCreate() {
        super.onCreate();
        //DealCrash crashHandler = DealCrash.getInstance();
        //crashHandler.init(this);
        JiMiSDK.onApplicationOnCreate(this);
        Log.e(TAG, "onCreate: JMApplicationOnCreate");
    }
}
