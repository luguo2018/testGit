package com.jmhy.sdk.statistics;

import android.app.Activity;
import android.util.Log;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.OkHttpException;
import com.jmhy.sdk.http.OkHttpManager;
import com.jmhy.sdk.http.ResponseCallback;
import com.jmhy.sdk.model.PayData;
import com.jmhy.sdk.model.PaymentInfo;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.DeviceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Api {
    private static final String TAG = Api.class.getSimpleName();
    static String REPORT;
    public void reportPay(String openid, PaymentInfo paymentInfo, PayData payData, boolean success)
    {
        JSONObject event = new JSONObject();
        JSONObject account = new JSONObject();
        try
        {
            event.put("cpOrderId", paymentInfo.getCporderid());
            event.put("orderId", payData.getOrderid());
            event.put("orderName", paymentInfo.getOrdername());
            int money = Integer.valueOf(paymentInfo.getAmount()).intValue();
            int moneyYuan = money / 100;
            event.put("amountCNY", String.valueOf(moneyYuan));
            if (success){
                event.put("success", 1);
//                event.put("success", success ? 1 : 0);
            }else {
                return;
            }
            event.put("appKey", BaiduStatistics.appKey);
            event.put("appId", BaiduStatistics.appId);

            account.put("roleId", paymentInfo.getRoleid());
            account.put("roleName", paymentInfo.getRolename());
            account.put("level", paymentInfo.getLevel());
            account.put("gender", paymentInfo.getGender());
            account.put("serverNo", paymentInfo.getServerno());
            account.put("serverName", paymentInfo.getZoneName());
            account.put("balance", paymentInfo.getBalance());
            account.put("power", paymentInfo.getPower());
            account.put("vipLevel", paymentInfo.getViplevel());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        report(openid, "pay", event, account);
    }

    public void reportInfo(String openid, String type, String roleid, String rolename, String level, String gender, String serverno, String zoneName, String balance, String power, String viplevel, String roleCTime, String roleLevelMTime, String ext)
    {
        JSONObject event = new JSONObject();
        JSONObject account = new JSONObject();
        String typeStr = "";
        try
        {
            switch (type)
            {
                case "1":
                    typeStr = "create_gamerole";
                    break;
                case "2":
                    typeStr = "enter_server";
                    break;
                case "3":
                    typeStr = "update_level";
            }
            event.put("roleId", roleid);
            event.put("roleName", rolename);
            event.put("level", level);
            event.put("gender", gender);
            event.put("serverNo", serverno);
            event.put("serverName", zoneName);
            event.put("balance", balance);
            event.put("power", power);
            event.put("vipLevel", viplevel);
            event.put("roleCTime", roleCTime);
            event.put("roleLevelMTime", roleLevelMTime);
            event.put("ext", ext);
            event.put("appKey", BaiduStatistics.appKey);
            event.put("appId", BaiduStatistics.appId);

            account.put("roleId", roleid);
            account.put("roleName", rolename);
            account.put("level", level);
            account.put("gender", gender);
            account.put("serverNo", serverno);
            account.put("serverName", zoneName);
            account.put("balance", balance);
            account.put("power", power);
            account.put("vipLevel", viplevel);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        report(openid, typeStr, event, account);
    }

    public void reportLogin(String openid)
    {
        JSONObject event = new JSONObject();
        try
        {
            event.put("appKey", BaiduStatistics.appKey);
            event.put("appId", BaiduStatistics.appId);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        report(openid, "login", event, null);
    }

    public void reportRegister(String method, boolean success)
    {
        JSONObject event = new JSONObject();
        try
        {
            event.put("method", method);
            event.put("success", success ? 1 : 0);
            event.put("appKey", BaiduStatistics.appKey);
            event.put("appId", BaiduStatistics.appId);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        report(null, "register", event, null);
    }

    public void reportSwitchAccount(String openid)
    {
//        JSONObject event = new JSONObject();
//        try
//        {
//            event.put("appKey", BaiduStatistics.appKey);
//            event.put("appId", BaiduStatistics.appId);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//        report(openid, "switchAccount", event, null);
    }

    public void reportExit(String openid)
    {
//        JSONObject event = new JSONObject();
//        try
//        {
//            event.put("appKey", BaiduStatistics.appKey);
//            event.put("appId", BaiduStatistics.appId);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//        report(openid, "exit", event, null);
    }

    public void reportResume(String openid, Activity activity)
    {
//        JSONObject event = new JSONObject();
//        try
//        {
//            event.put("activity", activity.getClass().getName());
//            event.put("appKey", BaiduStatistics.appKey);
//            event.put("appId", BaiduStatistics.appId);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//        report(openid, "resume", event, null);
    }

    public void reportPause(String openid, Activity activity)
    {
//        JSONObject event = new JSONObject();
//        try
//        {
//            event.put("activity", activity.getClass().getName());
//            event.put("appKey", BaiduStatistics.appKey);
//            event.put("appId", BaiduStatistics.appId);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//        report(openid, "pause", event, null);
    }

    public void reportInit(String appId, String appkey)
    {
        JSONObject event = new JSONObject();
        try
        {
            event.put("channel", "baidu");
            event.put("baiduAppId", appId);
            event.put("baiduAppKey", appkey);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        report(null, "init", event, null);
    }

    private  void report(String openid, String type, JSONObject event, JSONObject account)
    {
        long time = System.currentTimeMillis() / 1000L;

        HashMap<String, String> params = new HashMap();

        JSONObject paramsdata = new JSONObject();

        params.put("access_token", AppConfig.Token);
        params.put("time", String.valueOf(time));
        try
        {
            DeviceInfo deviceInfo = JmhyApi.get().getDeviceInfo();
            if (deviceInfo != null) {
                paramsdata.put("device", deviceInfo.getImei());
            }
            paramsdata.put("who", openid == null ? "" : openid);
            paramsdata.put("event", type);
            paramsdata.put("campaign_id", AppConfig.agent);
            if (account != null) {
                paramsdata.put("account_info", account.toString());
            } else {
                paramsdata.put("account_info", "");
            }
            if (event != null) {
                paramsdata.put("event_detail", event.toString());
            } else {
                paramsdata.put("event_detail", "");
            }
            params.put("context", paramsdata.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        OkHttpManager.getInstance().postRequest(REPORT, params, new ResponseCallback<String>() {
            public void onSuccess(String o) {
                Log.i(Api.TAG, "report onSuccess");
            }

            public void onFailure(OkHttpException e) {
                Log.i(Api.TAG, "report onError " + e.getEmsg());
            }
        });
    }
}
