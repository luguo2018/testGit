package com.jmhy.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jmhy.sdk.model.PayData;
import com.jmhy.sdk.model.PaymentInfo;
import com.jmhy.sdk.sdk.StatisticsSDK;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * create by yhz on 2018/9/13
 */
public class StatisticsSDKUtils implements StatisticsSDK{
    private final static String TAG = StatisticsSDKUtils.class.getSimpleName();

    private List<StatisticsSDK> list = new ArrayList<>();

    @Override
    public void initInterface(Context context, JSONObject config) {
        JSONObject logConfig = ConfigUtils.getConfigData(context);
        if(logConfig == null){
            Log.i(TAG, "no log config in config.json");
            return;
        }

        JSONArray array = logConfig.optJSONArray("jm_log_sdk");
        if(array == null || array.length() == 0){
            Log.i(TAG, "no log config in config.json");
            return;
        }

        if(config == null){
            Log.i(TAG, "no log support in web api");
            return;
        }

        List<String> logClass = new ArrayList<>();
        for(int i=0; i<array.length(); i++){
            logClass.add(array.optString(i));
        }

        for(String className : logClass) {
            try {

                Class<?> clazz = Class.forName(className);
                Log.i(TAG, "class = " + clazz);
                StatisticsSDK statisticsSDK = (StatisticsSDK) clazz.newInstance();

                if(config.has(statisticsSDK.getName())){
                    Log.i(TAG, "class = " + clazz + " is support");
                    JSONObject sdkConfig = config.optJSONObject(statisticsSDK.getName());
                    statisticsSDK.initInterface(context, sdkConfig);

                    if (!isHasSDK(statisticsSDK))
                    {
                        list.add(statisticsSDK);

                    }else {
                        Log.e(TAG,"已经添加过该log sdk");
                    }

                    }else{
                    Log.i(TAG, "class = " + clazz + " is not support");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isHasSDK(StatisticsSDK statisticsSDK) {
        boolean hasSDK = false;
        for(StatisticsSDK sdk : list) {
            hasSDK = sdk.getName() == statisticsSDK.getName();
            if (hasSDK){
                return true;
            }

        }
        return hasSDK;
    }

    @Override
    public void onLogin(String userId) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onLogin(userId);
        }
    }

    @Override
    public void onRegister(String method, boolean success) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onRegister(method, success);
        }
    }

    @Override
    public void onCompleteOrder(PaymentInfo payInfo) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onCompleteOrder(payInfo);
        }
    }

    @Override
    public void onPay(PaymentInfo payInfo, PayData payData, String channel, boolean success) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onPay(payInfo, payData, channel, success);
        }
    }

    @Override
    public void onCreate(Activity activity) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onCreate(activity);
        }
    }

    @Override
    public void onStop(Activity activity) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onStop(activity);
        }
    }

    @Override
    public void onDestroy(Activity activity) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onDestroy(activity);
        }
    }

    @Override
    public void onResume(Activity activity) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onResume(activity);
        }
    }

    @Override
    public void onPause(Activity activity) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onPause(activity);
        }
    }

    @Override
    public void onRestart(Activity activity) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onRestart(activity);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onNewIntent(intent);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onActivityResult(activity, requestCode, resultCode, data);
        }
    }

    @Override
    public void setExtData(Context context, String type, String roleid, String rolename, String level, String gender, String serverno, String zoneName, String balance, String power, String viplevel, String roleCTime, String roleLevelMTime, String ext) {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.setExtData(context, type, roleid, rolename, level, gender, serverno, zoneName, balance, power, viplevel, roleCTime, roleLevelMTime, ext);
        }
    }

    @Override
    public void onSwitchAccount() {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onSwitchAccount();
        }
    }

    @Override
    public void onExit() {
        for(StatisticsSDK statisticsSDK : list){
            statisticsSDK.onExit();
        }
    }

    @Override
    public String getName() {
        return null;
    }

    /*private String getMetaData(Context context){
        String log = "";
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if(info.metaData == null){
                return log;
            }
            log = info.metaData.getString("jm_log_sdk", "");

            Log.i(TAG, "get meta log = " + log);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return log;
    }*/
}
