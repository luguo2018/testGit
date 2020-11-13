package com.jmhy.sdk.ad.http;

import android.util.Log;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.http.OkHttpException;
import com.jmhy.sdk.http.OkHttpManager;
import com.jmhy.sdk.http.ResponseCallback;

import java.util.HashMap;

public class ReportApi {
    private static final String TAG = ReportApi.class.getSimpleName();
    static String REPORT_AD= WebApi.HOST + "/report/ad";//    "https://apisdk.5tc5.com/v1/report/ad";
    public ReportApi() {
    }


    public static void reportAd(String appId, String adId, String type, String action,String gameId) {
        long time = System.currentTimeMillis() / 1000L;
        final HashMap<String, String> params = new HashMap<>();
        params.put("access_token", AppConfig.Token);
        params.put("tk", AppConfig.Token);
        params.put("time", String.valueOf(time));

        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        paramsdata.put("a_appId", appId);//应用id
        paramsdata.put("a_unitId", adId);//广告id
        paramsdata.put("a_gameId", gameId);//研发代码位，暂无
        paramsdata.put("a_type", type);//1,激励广告   2,全屏广告   3,横幅广告
        paramsdata.put("a_c", "1"); //1,穿山甲   2,Google   3,广点通   4,自家渠道
        paramsdata.put("a_action", action);//1, 广告点击   2, 广告展示完成、包含播放N秒的   3, 广告展示完成、取消跳过   4, 申请加载广告   5, 广告展示

        params.put("context", paramsdata.toString());

        Log.i(ReportApi.TAG,"ad上报请求1:"+REPORT_AD+"\n 数据"+params.toString());
        AppConfig.is_ad_sign=true;
        OkHttpManager.getInstance().postRequest(REPORT_AD, params, new ResponseCallback<String>() {
            public void onSuccess(String o) {
                Log.i(ReportApi.TAG, "report onSuccess");
            }

            public void onFailure(OkHttpException e) {
                Log.i(ReportApi.TAG, "report onError " + e.getEmsg());
            }
        });
    }
}

