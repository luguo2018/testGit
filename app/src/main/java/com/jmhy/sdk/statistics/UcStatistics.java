package com.jmhy.sdk.statistics;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gism.sdk.GismConfig;
import com.gism.sdk.GismEventBuilder;
import com.gism.sdk.GismSDK;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.model.PayData;
import com.jmhy.sdk.model.PaymentInfo;
import com.jmhy.sdk.sdk.StatisticsSDK;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * create by xq on 2021/02/23
 */
public class UcStatistics implements StatisticsSDK {
    private final static String TAG = UcStatistics.class.getSimpleName();
    private UcApi api;
    static String appId;
    static String appName;
    private String userId;

    @Override
    public void initInterface(Context context, JSONObject config) {
        Log.i(TAG, "init");
        this.api = new UcApi();
        GismSDK.debug();
        try {
            appId = config.optString("ucAppId");
            appName = config.optString("ucAppName");
            Log.i(TAG, "appId = " + appId + ",appName:" + appName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebApi.HttpTypeMap.put(UcApi.REPORT, "post");
        //end

        //首次全局初始化  第二次初始化有权限上报启动
        if (context == context.getApplicationContext()) {//是application Context 初始化
            Log.i(TAG, "全局初始化"+context);
            GismSDK.init(GismConfig.newBuilder((Application) context)
                    .appID(appId)
                    .appName(appName)
                    .appChannel("jm")
                    .build()
            );
        } else {
            Log.i(TAG, "普通初始化，已有权限。上报启动"+context);
            GismSDK.onLaunchApp();//启动游戏回调 必选。每次 APP 启动时必须回调，否则会漏报激活和启动，导致汇川平台数据偏少。
        }


        this.api.reportInit(appId, appName,context == context.getApplicationContext());
    }

    @Override
    public void onLogin(String userId) {
        Log.i(TAG, "onLogin set userId = " + userId);
        this.userId = userId;

        JSONObject actionParam = new JSONObject();
        try {
            actionParam.put("userId", userId);
            GismSDK.onEvent(GismEventBuilder.onCustomEvent().action("login").putKeyValue("userId", userId).build());
            Log.i(TAG, "onLogin");
            this.api.reportLogin(userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRegister(String method, boolean success) {
        Log.i(TAG, "onRegister method = " + method + ", success = " + success);
        //内置事件: “注册” ，属性：注册方式，是否成功，属性值为：wechat ，true
        GismSDK.onEvent(GismEventBuilder.onRegisterEvent().isRegisterSuccess(success).registerType(method).build());
        Log.i(TAG, "onRegister");
        this.api.reportRegister(method, success);
    }

    @Override
    public void onCompleteOrder(PaymentInfo paymentInfo) {
        Log.i(TAG, "onCompleteOrder");
    }

    @Override
    public void onPay(PaymentInfo paymentInfo, PayData payData, String channel, boolean success) {
        Log.i(TAG, "onPay channel = " + channel + ",success=" + success);

        int money = Integer.valueOf(paymentInfo.getAmount());
        if (success) {
            GismSDK.onEvent(GismEventBuilder.onPayEvent().isPaySuccess(success).payAmount(money).contentName(paymentInfo.getOrdername()).build());
            //内置事件 “支付”，属性：商品类型，商品名称，商品ID，商品数量，支付渠道，币种，是否成功（必传），金额（必传）
            this.api.reportPay(this.userId, paymentInfo, payData, success);
            Log.i(TAG, "pay report success");
        }
    }

    @Override
    public void setExtData(Context context, String type, String roleid,
                           String rolename, String level, String gender, String serverno, String zoneName,
                           String balance, String power, String viplevel, String roleCTime, String roleLevelMTime, String ext) {
        Log.i(TAG, "uc setExtData" + type);
        switch (type) {
            case "1"://创建角色
                GismSDK.onEvent(GismEventBuilder.onRoleEVent().build());
                break;
            case "2"://进入游戏
                String typeStr = "enter_server";
                GismSDK.onEvent(GismEventBuilder.onCustomEvent().action(typeStr)
                        .putKeyValue("type", typeStr)
                        .putKeyValue("roleid", roleid)
                        .putKeyValue("rolename", rolename)
                        .putKeyValue("level", level)
                        .putKeyValue("gender", gender)
                        .putKeyValue("serverno", serverno)
                        .putKeyValue("zoneName", zoneName)
                        .putKeyValue("balance", balance)
                        .putKeyValue("power", power)
                        .putKeyValue("viplevel", viplevel)
                        .putKeyValue("roleCTime", roleCTime)
                        .putKeyValue("roleLevelMTime", roleLevelMTime)
                        .putKeyValue("ext", ext)
                        .build());
                break;
            case "3"://升级
                GismSDK.onEvent(GismEventBuilder.onUpgradeEvent().level(Integer.parseInt(level)).build());
                break;
        }

        this.api.reportInfo(userId, type, roleid, rolename, level, gender, serverno, zoneName, balance, power, viplevel, roleCTime, roleLevelMTime, ext);
    }

    @Override
    public void onExit() {
        Log.i(TAG, "onExit set userId = null");
        GismSDK.onExitApp();
    }

    @Override
    public String getName() {
        return "uc";
    }


    @Override
    public void onSwitchAccount() {
    }

    @Override
    public void onCreate(Activity activity) {

    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {

    }

    @Override
    public void onResume(Activity activity) {

    }

    @Override
    public void onPause(Activity activity) {

    }

    @Override
    public void onRestart(Activity activity) {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }
}
