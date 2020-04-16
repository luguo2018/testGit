package com.jmhy.sdk.statistics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bytedance.applog.AppLog;
import com.bytedance.applog.GameReportHelper;
import com.bytedance.applog.InitConfig;
import com.bytedance.applog.util.UriConfig;
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
    private String mOridId;

    @Override
    public void initInterface(Context context, JSONObject config) {
        Log.i(TAG, "init");
        String appId = config.optString("app_id");
        String appName = config.optString("app_name");
        int aid = 0;
        try{
            aid = Integer.parseInt(appId);
        }catch (Exception e){}

        Log.i(TAG, "init packageName = " + context.getPackageName());
        Log.i(TAG, "init appName = " + appName + ", aid=" + aid);
        InitConfig initConfig = new InitConfig(aid + "", "jm");
        initConfig.setUriConfig(UriConfig.DEFAULT);
        initConfig.setEnablePlay(true);
        AppLog.setEnableLog(false);  //true 调试  false 正式
        AppLog.init(context,initConfig);

        if(context instanceof Activity){
            Activity activity = (Activity)context;
            onResume(activity);
        }
    }

    @Override
    public void onLogin(String userId) {
        Log.i(TAG, "onLogin set userId = " + userId);
        AppLog.setUserUniqueID(userId);

    }

    @Override
    public void onRegister(String method, boolean success) {
        Log.i(TAG, "onRegister method = " + method + ", success = " + success);
        //内置事件: “注册” ，属性：注册方式，是否成功，属性值为：wechat ，true
        GameReportHelper.onEventRegister(method,true);
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
        if (success){
            if (mOridId
                    != payData.getOrderid()){
                GameReportHelper.onEventPurchase(paymentInfo.getOrdername(), paymentInfo.getOrdername(), paymentInfo.getCporderid(), 1, channel,"CNY", success, moneyYuan);
                mOridId = payData.getOrderid();
            }


        }else {
            GameReportHelper.onEventPurchase(paymentInfo.getOrdername(), paymentInfo.getOrdername(), paymentInfo.getCporderid(), 1, channel,"CNY", success, moneyYuan);

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
        Log.i(TAG, "onResume");
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put("activity","onresume");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("onresume", paramsObj);

    }

    @Override
    public void onPause(Activity activity) {
        Log.i(TAG, "onPause");
        JSONObject paramsObj = new JSONObject();
        try {
            paramsObj.put("activity","onpause");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("onpause",paramsObj);
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
            AppLog.onEventV3(typeStr , uploadParam);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSwitchAccount() {
        Log.i(TAG, "onSwitchAccount set userId = null");
        AppLog.setUserUniqueID(null);
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
