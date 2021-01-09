package com.jmhy.sdk.http;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.huosdk.huounion.sdk.okhttp3.OkHttpClient;
import com.huosdk.huounion.sdk.okhttp3.Request;
import com.jmhy.sdk.utils.OkHttpUtils;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
                .sslSocketFactory(createSSLSocketFactory())
                .connectTimeout(5000, TimeUnit.SECONDS)
                .writeTimeout(5000,TimeUnit.SECONDS)
                .readTimeout(5000,TimeUnit.SECONDS)
                .addInterceptor(new SignInterceptor())
                .build();

    }
    MyTrustManager mMyTrustManager;
    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            mMyTrustManager = new MyTrustManager();
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{mMyTrustManager}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return ssfFactory;
    }

    //实现X509TrustManager接口
    public static class MyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
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
    public Call postRequest(@NonNull String url, @NonNull HashMap<String, String> parameter, final ResponseCallback callback) {

        Request request = new Request.Builder()
                .url(url)
                .post(OkHttpUtils.hasMapToRequestBody(parameter))
                .build();
        Call  call=  okHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handler, callback));
        return call;
    }
}
