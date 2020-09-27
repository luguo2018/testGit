package com.jmhy.sdk.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;

public class Loginout {
    public static Loginout mLoginout;
    public static Context mContext;

    public static Loginout getInstatnce(Context context) {
        if (mLoginout == null) {
            mLoginout = new Loginout();
        }
        mContext = context;
        ExitLoginout();
        return mLoginout;
    }

    public static void ExitLoginout() {
        JmhyApi.get().starguserLoginout(mContext,
                AppConfig.appKey, new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        JiMiSDK.userlistenerinfo.onLogout("logout");
                        JiMiSDK.getStatisticsSDK().onSwitchAccount();
                        AppConfig.skin9_is_switch = true;
                    }

                    @Override
                    public void onError(int statusCode) {
                        // TODO Auto-generated method stub
                    }
                });

    }

}