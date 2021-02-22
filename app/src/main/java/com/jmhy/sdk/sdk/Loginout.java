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
        //2021-2-22新增，切换之后清空缓存角色数据
        RoleinfoRequest.getInstatnce(context, "", "", "", "", "", "", "", "", "", "", "");
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
                    public void onError(int statusCode,String msg) {
                        // TODO Auto-generated method stub
                    }
                });

    }

}