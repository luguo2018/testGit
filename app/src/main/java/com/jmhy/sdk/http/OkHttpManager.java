package com.jmhy.sdk.http;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.huosdk.huounion.sdk.okhttp3.OkHttpClient;
import com.huosdk.huounion.sdk.okhttp3.Request;
import com.jmhy.sdk.utils.OkHttpUtils;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * okHttp封装类,目前已经封装post表单请求，需要传入Result<T> T为返回实体类
 * 请求回调已经切换到主线程
 */
public class OkHttpManager {
    private static final String TAG = "OkHttpManager";
    Handler handler = new Handler(Looper.getMainLooper());
    OkHttpClient okHttpClient;

    private static class SingletonHolder {
        private static final OkHttpManager INSTANCE = new OkHttpManager();
    }

    private OkHttpManager() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.SECONDS)
                .writeTimeout(5000,TimeUnit.SECONDS)
                .readTimeout(5000,TimeUnit.SECONDS)
                .addInterceptor(new SignInterceptor())
                .build();
    }

    public static final OkHttpManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 异步post请求
     *
     * @param url
     * @param parameter
     * @param callback
     */
    public void postRequest(@NonNull String url, @NonNull HashMap<String, String> parameter, final ResponseCallback callback) {

        Request request = new Request.Builder()
                .url(url)
                .post(OkHttpUtils.hasMapToRequestBody(parameter))
                .build();
        okHttpClient.newCall(request).enqueue(new CommonJsonCallback(handler, callback));

    }
}
