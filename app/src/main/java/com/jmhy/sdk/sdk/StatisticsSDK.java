package com.jmhy.sdk.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.jmhy.sdk.bean.PayData;
import com.jmhy.sdk.model.PaymentInfo;

import org.json.JSONObject;

/**
 * create by yhz on 2018/8/31
 */
public interface StatisticsSDK {
    void initInterface(Context context, JSONObject config);
    void onLogin(String userId);
    void onRegister(String method, boolean success);
    void onCompleteOrder(PaymentInfo payInfo);
    void onPay(PaymentInfo payInfo, PayData payData, String channel, boolean success);
    void onCreate(Activity activity);
    void onStop(Activity activity);
    void onDestroy(Activity activity);
    void onResume(Activity activity);
    void onPause(Activity activity);
    void onRestart(Activity activity);
    void onNewIntent(Intent intent);
    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);
    void setExtData(Context context, String type,String roleid,
                    String rolename,String level,String gender,String serverno,String zoneName,
                    String balance,String power,String viplevel,String roleCTime, String roleLevelMTime,String ext);
    void onSwitchAccount();
    void onExit();

    String getName();
}
