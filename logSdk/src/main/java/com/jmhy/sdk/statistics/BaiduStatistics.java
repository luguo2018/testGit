package com.jmhy.sdk.statistics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.mobads.action.ActionParam;
import com.baidu.mobads.action.ActionType;
import com.baidu.mobads.action.BaiduAction;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.model.PayData;
import com.jmhy.sdk.model.PaymentInfo;
import com.jmhy.sdk.sdk.StatisticsSDK;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * create by yhz on 2018/8/31
 */
public class BaiduStatistics implements StatisticsSDK {
    private final static String TAG = BaiduStatistics.class.getSimpleName();
    private BaiduApi api;
    static String appId;
    static String appKey;
    private String userId;

    @Override
    public void initInterface(Context context, JSONObject config) {
        Log.i(TAG, "init");
        this.api = new BaiduApi();

        Log.i(TAG, "init packageName = " + context.getPackageName());
        try {
            appId = config.optString("baiduAppId");
            appKey = config.optString("baiduAppKey");
        }catch (Exception e){
            e.printStackTrace();
        }

        BaiduApi.REPORT = WebApi.HOST + "/report/channel";
        WebApi.HttpTypeMap.put(BaiduApi.REPORT, "post");
        //end

        this.api.reportInit(appId,appKey);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            onResume(activity);
        }
    }

    @Override
    public void onLogin(String userId) {
        Log.i(TAG, "onLogin set userId = " + userId);
        this.userId = userId;

        JSONObject actionParam = new JSONObject();
        try {
            actionParam.put("userId", userId);
            BaiduAction.logAction(ActionType.LOGIN,actionParam);
            Log.i(TAG, "onRegister");
            this.api.reportLogin(userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRegister(String method, boolean success) {
        Log.i(TAG, "onRegister method = " + method + ", success = " + success);
        //内置事件: “注册” ，属性：注册方式，是否成功，属性值为：wechat ，true
        BaiduAction.logAction(ActionType.REGISTER);
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
        if(success){
            JSONObject actionParam = new JSONObject();
            try {
                actionParam.put(ActionParam.Key.PURCHASE_MONEY, money);//上报支付单位为分
                actionParam.put("orderName", paymentInfo.getOrdername());
                actionParam.put("cpOrderId", paymentInfo.getCporderid());
                actionParam.put("orderId", payData.getOrderid());
                actionParam.put("channel", channel);
                actionParam.put("amount", money);
                BaiduAction.logAction(ActionType.PURCHASE,actionParam);
                Log.i(TAG, "onPay"+actionParam);
                //内置事件 “支付”，属性：商品类型，商品名称，商品ID，商品数量，支付渠道，币种，是否成功（必传），金额（必传）
                this.api.reportPay(this.userId,paymentInfo,payData,success);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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


    @Override
    public void setExtData(Context context, String type, String roleid,
                           String rolename, String level, String gender, String serverno, String zoneName,
                           String balance, String power, String viplevel, String roleCTime, String roleLevelMTime, String ext) {
        Log.i(TAG, "setExtData");

        try {
            String typeStr = type;
            switch (type) {
                case "1"://创建角色
                    typeStr = ActionType.CREATE_ROLE;
                    break;
                case "2"://进入游戏
                    typeStr = "enter_server";
                    break;
                case "3"://升级
                    typeStr = ActionType.UPGRADE ;
                    break;
            }

            JSONObject uploadParam = new JSONObject();
            uploadParam.put("type", typeStr);
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
            BaiduAction.logAction(typeStr, uploadParam);
            Log.i(TAG, "setExtData"+uploadParam);
            this.api.reportInfo(userId, type, roleid, rolename, level, gender, serverno, zoneName, balance, power, viplevel, roleCTime, roleLevelMTime, ext);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSwitchAccount() {
        Log.i(TAG, "onSwitchAccount set userId = null");
    }

    @Override
    public void onExit() {
        Log.i(TAG, "onExit set userId = null");
    }

    @Override
    public String getName() {
        return "bd";
    }
}
