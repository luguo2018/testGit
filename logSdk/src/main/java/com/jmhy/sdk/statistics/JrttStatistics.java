package com.jmhy.sdk.statistics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bytedance.applog.AppLog;
import com.bytedance.applog.GameReportHelper;
import com.bytedance.applog.ILogger;
import com.bytedance.applog.InitConfig;
import com.bytedance.applog.util.UriConstants;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.model.PayData;
import com.jmhy.sdk.model.PaymentInfo;
import com.jmhy.sdk.sdk.StatisticsSDK;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * create by yhz on 2018/8/31
 */
public class JrttStatistics implements StatisticsSDK {
    private final static String TAG = JrttStatistics.class.getSimpleName();
    private JrttApi jrttApi;
    static String appId;
    static String appName;
    private String userId;

    @Override
    public void initInterface(Context context, JSONObject config) {
        Log.i(TAG, "init");
        this.jrttApi = new JrttApi();
        appId = config.optString("app_id");
        appName = config.optString("app_name");
        int aid = 0;
        try {
            aid = Integer.parseInt(appId);
        } catch (Exception e) {
        }

        Log.i(TAG, "init packageName = " + context.getPackageName());
            Log.i(TAG, "init appName = " + appName + ", aid=" + aid);
        InitConfig initConfig = new InitConfig(aid + "", "jm");

        initConfig.setUriConfig (UriConstants.DEFAULT);
//        initConfig.setLogger ((msg, t) -> Log._d_ (TAG, msg, t)); // 是否在控制台输出日志，可用于观察用户行为日志上报情况，建议仅在调试时使用
        ILogger iLogger=new ILogger() {
            @Override
            public void log(String s, Throwable throwable) {
                Log.i("测试日志","msg:"+s+".throwable:"+throwable);
                if (throwable!=null){
                    throwable.printStackTrace();
                }
            }
        };
        initConfig.setLogger (iLogger); // 是否在控制台输出日志，可用于观察用户行为日志上报情况，建议仅在调试时使用
        initConfig.setEnablePlay(true); // 是否开启游戏模式，游戏APP建议设置为 true
        initConfig.setAbEnable(false); // 是否开启A/B Test功能
        // 游戏模式，YES会开始 playSession 上报，每隔一分钟上报心跳日志
        initConfig.setAutoStart(true);

        AppLog.init(context, initConfig);
        Log.i("头条5.2.4测试日志","开始初始化心跳");
        //start 补充/report/channel接口
        JrttApi.REPORT = WebApi.HOST + "/report/channel";
        WebApi.HttpTypeMap.put(JrttApi.REPORT, "post");
        //end

        this.jrttApi.reportInit(appId, appName);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            onResume(activity);
        }
    }

    @Override
    public void onLogin(String userId) {
        Log.i(TAG, "onLogin set userId = " + userId);
        this.userId = userId;
        this.jrttApi.reportLogin(userId);
        AppLog.setUserUniqueID(userId);

    }

    @Override
    public void onRegister(String method, boolean success) {
        Log.i(TAG, "onRegister method = " + method + ", success = " + success);
        //内置事件: “注册” ，属性：注册方式，是否成功，属性值为：wechat ，true
        GameReportHelper.onEventRegister(method, true);
        this.jrttApi.reportRegister(method, success);
    }

    @Override
    public void onCompleteOrder(PaymentInfo paymentInfo) {
        Log.i(TAG, "onCompleteOrder");
    }

    @Override
    public void onPay(PaymentInfo paymentInfo, PayData payData, String channel, boolean success) {
        Log.i(TAG, "onPay channel = " + channel + ",success=" + success);

        int money = Integer.valueOf(paymentInfo.getAmount());
        int moneyYuan = money / 100;
        if(success){
            GameReportHelper.onEventPurchase(paymentInfo.getOrdername(), paymentInfo.getOrdername(), paymentInfo.getCporderid(), 1, channel, "CNY", success, moneyYuan);
        }
        //内置事件 “支付”，属性：商品类型，商品名称，商品ID，商品数量，支付渠道，币种，是否成功（必传），金额（必传）
        this.jrttApi.reportPay(this.userId,paymentInfo,payData,success);
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
        Log.i(TAG, "onResume");
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put("activity", "onresume");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("onresume", paramsObj);
//        if (this.jrttApi!=null){
//            this.jrttApi.reportResume(this.userId,activity);
//        }
    }

    @Override
    public void onPause(Activity activity) {
        Log.i(TAG, "onPause");
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put("activity", "onpause");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("onpause", paramsObj);
//        if (this.jrttApi!=null){
//            this.jrttApi.reportPause(this.userId,activity);
//        }
    }

    @Override
    public void onRestart(Activity activity) {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(Activity activity, int i, int i1, Intent intent) {

    }

    @Override
    public void setExtData(Context context, String type, String roleid,
                           String rolename, String level, String gender, String serverno, String zoneName,
                           String balance, String power, String viplevel, String roleCTime, String roleLevelMTime, String ext) {
        Log.i(TAG, "setExtData");

        try {
            String typeStr = type;
            switch (type) {
                case "1":
                    typeStr = "create_gamerole";
                    GameReportHelper.onEventCreateGameRole(""); // 创建角色
                    break;
                case "2":
                    typeStr = "enter_server";
                    break;
                case "3":
                    typeStr = "update_level";
                    break;
            }

            JSONObject uploadParam = new JSONObject();
            uploadParam.put("roleid", roleid);
            uploadParam.put("rolename", rolename);
            uploadParam.put("level", level);
            uploadParam.put("gender", gender);
            uploadParam.put("serverno", serverno);
            uploadParam.put("zoneName", zoneName);
            uploadParam.put("balance", balance);
            uploadParam.put("power", power);
            uploadParam.put("viplevel", viplevel);
            uploadParam.put("roleCTime", roleCTime);
            uploadParam.put("roleLevelMTime", roleLevelMTime);
            uploadParam.put("ext", ext);
            AppLog.onEventV3(typeStr, uploadParam);




        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.jrttApi.reportInfo(userId, type, roleid, rolename, level, gender, serverno, zoneName, balance, power, viplevel, roleCTime, roleLevelMTime, ext);
    }

    @Override
    public void onSwitchAccount() {
        Log.i(TAG, "onSwitchAccount set userId = null");
        AppLog.setUserUniqueID(null);
        this.jrttApi.reportSwitchAccount(this.userId);
    }

    @Override
    public void onExit() {
        Log.i(TAG, "onExit set userId = null");
        AppLog.setUserUniqueID(null);
    }

    @Override
    public String getName() {
        return "jrtt";
    }
}
