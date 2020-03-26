package com.jmhy.sdk.common;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bun.miitmdid.core.JLibrary;
import com.jmhy.sdk.utils.MiitHelper;

public class JMApplication extends Application {


    private static String oaid;


    private static boolean isSupportOaid=true;
    private static int errorCode;


    public static String getOaid() {
        return oaid;
    }
    public static String getErrorCode() {
        return String.valueOf(errorCode);
    }

    public static boolean isSupportOaid() {
        return isSupportOaid;
    }

    public static void setIsSupportOaid(boolean isSupportOaid) {
        JMApplication.isSupportOaid = isSupportOaid;
    }

    public static void setIsSupportOaid(boolean isSupportOaid,int ErrorCode) {
        JMApplication.isSupportOaid = isSupportOaid;
        JMApplication.errorCode=ErrorCode;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //DealCrash crashHandler = DealCrash.getInstance();
        //crashHandler.init(this);
        Log.i("aaaaa", "onCreate");
        //获取OAID等设备标识符
        MiitHelper miitHelper = new MiitHelper(appIdsUpdater);
        miitHelper.getDeviceIds(getApplicationContext());


    }


    private MiitHelper.AppIdsUpdater appIdsUpdater = new MiitHelper.AppIdsUpdater() {
        @Override
        public void OnIdsAvalid(@NonNull String ids) {
            Log.e("aaaaa","++++++ids: " + ids);
            oaid = ids;
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.i("aaaaa", "attachBaseContext");
        try {
            JLibrary.InitEntry(base);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
