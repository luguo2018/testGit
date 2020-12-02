package com.jmhy.sdk.statistics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.model.PayData;
import com.jmhy.sdk.model.PaymentInfo;
import com.jmhy.sdk.sdk.StatisticsSDK;
import com.qq.gdt.action.ActionParam;
import com.qq.gdt.action.ActionType;
import com.qq.gdt.action.GDTAction;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * create by yhz on 2018/8/31
 */
public class GdtStatistics implements StatisticsSDK {
    private final static String TAG = GdtStatistics.class.getSimpleName();
    private GdtApi api;
    /*private final static String ACTION_LOGIN = "login";
    private final static String ACTION_PAY = "pay";
    private final static String ACTION_ROLE_DATA = "roleData";
    private final static String ACTION_SWITCH_ACCOUNT = "switchAccount";
    private final static String ACTION_EXIT = "exit";*/

    private boolean init;
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    public static String trackId;
    public static String trackKey;
    public static String userId;

    @Override
    public void initInterface(final Context context, JSONObject config) {
        Log.i(TAG, "init");
        trackId = config.optString("trackId");
        trackKey = config.optString("trackKey");
        WebApi.HttpTypeMap.put(GdtApi.REPORT, "post");
        this.api = new GdtApi();
        Log.i(TAG, "init gdtInfo trackId = " + trackId + ", trackKey=" + trackKey);

        final Activity activity = (Activity)context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GDTAction.init(context.getApplicationContext(), trackId, trackKey);
                init = true;
                GdtApi.REPORT = WebApi.HOST + "/report/channel";
                Log.i(TAG, "init success"+GdtApi.REPORT);
                onResume(null);
            }
        });
        this.api.reportInit(GdtStatistics.trackId, trackKey);
    }

    @Override
    public void onLogin(String userId) {
        Log.i(TAG, "onLogin set userId = " + userId);
        this.userId = userId;
        /*if(!init){
            Log.w(TAG, "gdt not init");
            return;
        }

        try {
            JSONObject actionParam = new JSONObject();
            actionParam.put("userId", userId);

            GDTAction.logAction(ACTION_LOGIN, actionParam);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        Log.i(TAG, " 用户软ID onLogin set userId = " + userId);
        if (!this.init)
        {
            Log.w(TAG, "gdt not init");
            return;
        }
//        GDTAction.setUserUniqueId(userId);
        try
        {
            JSONObject actionParam = new JSONObject();
            actionParam.put("userId", userId);

            GDTAction.logAction("login", actionParam);

            this.api.reportLogin(userId);
            Log.i(TAG,"gdt上报: reportLogin");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRegister(String method, boolean success) {
        Log.i(TAG, "onRegister method = " + method + ", success = " + success);

        if(!init){
            Log.w(TAG, "gdt not init");
            return;
        }

        JSONObject actionParam = new JSONObject();
        try {
            actionParam.put(ActionParam.Key.OUTER_ACTION_ID, getActionId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDTAction.logAction(ActionType.REGISTER, actionParam);
        Log.i(TAG, "ActionType.REGISTER");
        this.api.reportRegister(method, success);
        Log.i(TAG, "gdt上报: reportRegister");
    }

    @Override
    public void onCompleteOrder(PaymentInfo paymentInfo) {
        Log.i(TAG, "onCompleteOrder");

        /*if(!init){
            Log.w(TAG, "gdt not init");
            return;
        }

        GDTAction.logAction(ActionType.COMPLETE_ORDER);*/
    }

    @Override
    public void onPay(PaymentInfo paymentInfo, PayData payData, String channel, boolean success) {
        Log.i(TAG, "onPay channel = " + channel + ",success=" + success);
        Log.i(TAG, "onPay paymentInfo = " + paymentInfo.toString());
        Log.i(TAG, "onPay payData = " + payData.toString());

        if(!init){
            Log.w(TAG, "gdt not init");
            return;
        }

        /*int money = Integer.valueOf(paymentInfo.getAmount());
        String moneyYuan = String.valueOf(money / 100f);

        try {
            JSONObject actionParam = new JSONObject();
            actionParam.put("productName", paymentInfo.getOrdername());
            actionParam.put("orderId", paymentInfo.getCporderid());
            actionParam.put("channel", channel);
            actionParam.put("money", moneyYuan);
            actionParam.put("success", success);

            GDTAction.logAction(ActionType.PURCHASE, actionParam);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        if(!success){
            Log.w(TAG, "pay not success");
            return;
        }

        String date = payData.getUser_reg_date();
        String current = format.format(new Date());
//        if(!TextUtils.equals(current, date)){
//            Log.w(TAG, "date not equals date = " + date + ",current = " + current);
//            return;
//        }

        JSONObject actionParam = new JSONObject();
        try {
            int money = Integer.valueOf(paymentInfo.getAmount());
            actionParam.put("value", money);

            Log.i(TAG, "price = " + money);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GDTAction.logAction(ActionType.PURCHASE, actionParam);
        Log.w(TAG, "date = " + date + ",current = " + current);
        Log.i(TAG, "ActionType.PURCHASE");
        this.api.reportPay(userId,paymentInfo,payData,success);
        Log.i(TAG, "gdt上报: reportPay");
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

        if(!init){
            Log.w(TAG, "gdt not init");
            return;
        }
        GDTAction.logAction(ActionType.START_APP);
        Log.i(TAG, "ActionType.START_APP");
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
    public void onActivityResult(Activity activity, int i, int i1, Intent intent) {

    }

    @Override
    public void setExtData(Context context, String type, String roleid,
                           String rolename, String level, String gender, String serverno, String zoneName,
                           String balance, String power, String viplevel, String roleCTime, String roleLevelMTime, String ext) {
        Log.i(TAG, "setExtData");

        /*if(!init){
            Log.w(TAG, "gdt not init");
            return;
        }

        String typeStr = type;
        switch (type){
            case "1":
                typeStr = "create_gamerole";
                break;
            case "2":
                typeStr = "enter_server";
                break;
            case "3":
                typeStr = "update_level";
                break;
        }

        try {
            JSONObject actionParam = new JSONObject();
            actionParam.put("type", typeStr);
            actionParam.put("roleid", roleid);
            actionParam.put("rolename", rolename);
            actionParam.put("level", level);
            actionParam.put("gender", gender);
            actionParam.put("serverno", serverno);
            actionParam.put("zoneName", zoneName);
            actionParam.put("balance", balance);
            actionParam.put("power", power);
            actionParam.put("viplevel", viplevel);
            actionParam.put("roleCTime", roleCTime);
            actionParam.put("roleLevelMTime", roleLevelMTime);
            actionParam.put("ext", ext);

            GDTAction.logAction(ACTION_ROLE_DATA, actionParam);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onSwitchAccount() {
        Log.i(TAG, "onSwitchAccount");

        /*if(!init){
            Log.w(TAG, "gdt not init");
            return;
        }

        GDTAction.logAction(ACTION_SWITCH_ACCOUNT);*/
    }

    @Override
    public void onExit() {
        Log.i(TAG, "onExit");

        /*if(!init){
            Log.w(TAG, "gdt not init");
            return;
        }

        GDTAction.logAction(ACTION_EXIT);*/
    }

    @Override
    public String getName() {
        return "gdt";
    }

    private String getActionId(){
        return JiMiSDK.getUUID().replaceAll("-", "");
    }
}
