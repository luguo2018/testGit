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
import com.jmhy.sdk.utils.SecurityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
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
                Map<String, String> newFormBodyParamMap = getSignParamMap("POST", uri.getPath(), formBodyParamMap);
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
        String sign = SecurityUtils.getMD5Str(paramStr.toLowerCase());
        paramMap.put("sign", sign);
        Log.e("TAG", "paramStr: ==="+paramStr);
        Log.e("TAG", "sign: ==="+sign);
        return paramMap;
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
