package com.jmhy.sdk.http;

import android.util.Log;

import com.huosdk.huounion.sdk.okhttp3.FormBody;
import com.huosdk.huounion.sdk.okhttp3.Headers;
import com.huosdk.huounion.sdk.okhttp3.Interceptor;
import com.huosdk.huounion.sdk.okhttp3.MultipartBody;
import com.huosdk.huounion.sdk.okhttp3.Request;
import com.huosdk.huounion.sdk.okhttp3.RequestBody;
import com.huosdk.huounion.sdk.okhttp3.Response;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.utils.MD5Utils;
import com.jmhy.sdk.utils.SecurityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * okHttp重写定向拦截器，用于参数签名与添加
 */
public class SignInterceptor implements Interceptor {
    private static final String TAG = "SignInterceptor";
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        Request.Builder newRequest = originRequest.newBuilder();

        // Header
        Headers.Builder newHeaderBuilder = originRequest.headers().newBuilder();
        // Query Param
        if ("POST".equals(originRequest.method())) {
            RequestBody body = originRequest.body();
            if (body != null && body instanceof FormBody) {
                // POST Param x-www-form-urlencoded
                FormBody formBody = (FormBody) body;
                TreeMap<String, String> formBodyParamMap = new TreeMap<String, String>();
                int bodySize = formBody.size();
                for (int i = 0; i < bodySize; i++) {
                    formBodyParamMap.put(formBody.name(i), formBody.value(i));
                }
                URI uri = originRequest.url().uri();
                //合并新老参数
                Map<String, String> newFormBodyParamMap = AppConfig.is_ad_sign?getEncryStr(AppConfig.agent,  formBodyParamMap):getSignParamMap("POST", uri.getPath(), formBodyParamMap);
                if (newFormBodyParamMap != null) {
                    FormBody.Builder bodyBuilder = new FormBody.Builder();
                    for (Map.Entry<String, String> entry : newFormBodyParamMap.entrySet()) {
                        bodyBuilder.add(entry.getKey(), null == entry.getValue() ? "" : entry.getValue());
                    }
                    newRequest.method(originRequest.method(), bodyBuilder.build());
                }
            } else if (body != null && body instanceof MultipartBody) {
                // POST Param form-data
            }
        }

        return chain.proceed(newRequest.build());
    }
    /**
     * 对原有的参数进行签名
     *
     * @param mothed   GET or POST
     * @param api      请求的路径
     * @param paramMap 原有的参数
     * @return 添加签名后的参数
     */
    public static Map<String, String> getSignParamMap(String mothed, String api, TreeMap<String, String> paramMap) {
        StringBuilder paramBuffer = new StringBuilder();
        paramBuffer.append("access_token=").append(URLEncoder.encode(paramMap.get("access_token").toString())).
                append("&").append("time=").append(URLEncoder.encode(paramMap.get("time").toString())).append("&").
                append("context=").append(paramMap.get("context").toString()).append("&");
        paramBuffer.append("appkey=").append(AppConfig.appKey);
        String paramStr = paramBuffer.toString();
//        String sign = SecurityUtils.getMD5Str(paramStr.toLowerCase());
        String sign = SecurityUtils.getMD5Str(paramStr.toLowerCase());
        paramMap.put("sign", sign);
        Log.e("TAG", "paramStr: ==="+paramStr);
        Log.e("TAG", "sign: ==="+sign);
        return paramMap;
    }

    public static Map<String, String> getEncryStr(String appkey, TreeMap<String, String> treeMap) {
        AppConfig.is_ad_sign=false;
        StringBuffer buf = new StringBuffer(appkey);
        buf = buf.reverse();
        String md5Key=MD5Utils.md5(buf.toString()).toLowerCase();
        StringBuilder encodeSB = new StringBuilder();
        StringBuilder sortSB = new StringBuilder();

        try {
            Iterator var4 = treeMap.entrySet().iterator();

            while(var4.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry)var4.next();
                String key = (String)entry.getKey();
                String value = entry.getValue() == null ? "" : (String)entry.getValue();
                sortSB.append(key).append("=").append(value).append("&");
                encodeSB.append(key).append("=").append(URLEncoder.encode(value, "UTF-8")).append("&");
            }
        } catch (UnsupportedEncodingException var8) {
            var8.printStackTrace();
        }

        String sign = MD5Utils.md5(sortSB.append(md5Key).toString().toLowerCase());
        treeMap.put("sign", sign);
        return treeMap;
    }

    public static String urlencode(String data) {
        try {
            return URLEncoder.encode(data, "utf-8")
                    .replace("+", "%20")
                    .replace("*", "%2A");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
