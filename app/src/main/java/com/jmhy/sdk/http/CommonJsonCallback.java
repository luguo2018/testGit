package com.jmhy.sdk.http;

import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.huosdk.gson.Gson;
import com.huosdk.gson.GsonBuilder;
import com.huosdk.huounion.sdk.okhttp3.Call;
import com.huosdk.huounion.sdk.okhttp3.Callback;
import com.huosdk.huounion.sdk.okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;

/**
 * 请求真正的实现类
 * @param <T>
 */
public class CommonJsonCallback<T> implements Callback {
    protected final String NETWORK_MSG = "请求失败";
    protected final String JSON_MSG = "解析失败";
    protected final String RESULT_CODE = "code";
    /**
     * 自定义异常类型
     */

    protected final int NETWORK_ERROR = -1; //网络失败
    protected final int ERROR = 1; //网络失败
    protected final int JSON_ERROR = -2; //解析失败
    protected final int OTHER_ERROR = 44000; //未知错误
    protected final int SUCCESS = 0; //成功
    private Handler mDeliveryHandler; //进行消息的转发
    private ResponseCallback<T> mListener;
    protected final String ERROR_MSG = "message";

    public CommonJsonCallback(Handler mDeliveryHandler, ResponseCallback<T> mListener) {
        this.mDeliveryHandler = mDeliveryHandler;
        this.mListener = mListener;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        Log.e("TAG", "请求失败=" + e.getMessage());
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(ERROR, e.getMessage()));
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String result = response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    private void handleResponse(Object responseObj) {
        if (responseObj == null && responseObj.toString().trim().equals("")) {
            mListener.onFailure(new OkHttpException(NETWORK_ERROR, NETWORK_MSG));
            return;
        }

        try {
            JSONObject result = new JSONObject(responseObj.toString());
            if (result.getInt(RESULT_CODE) == SUCCESS) {

                /**
                 * 判断是否需要解析成实体类还是json字符串
                 * class com.google.gson.internal.$Gson$Types$ParameterizedTypeImpl
                 */
                Gson gson = new GsonBuilder().serializeNulls().create();
                T obj = null;
                String classType = mListener.getClass().getGenericSuperclass()+"";
                Log.e("TAG", "handleResponse: classType"+classType);

                if (!classType.contains("java.lang.String")) {
                    obj = gson.fromJson((String) responseObj, mListener.mType);
                } else {
                    obj = (T) responseObj;
                }
                if (obj != null) {
                    mListener.onSuccess(obj);
                } else {
                    mListener.onFailure(new OkHttpException(JSON_ERROR, JSON_MSG));
                }
            } else { //将服务端返回的异常回调到应用层去处理
                mListener.onFailure(new OkHttpException(result.getInt(RESULT_CODE), result.get(ERROR_MSG) + ""));
                Log.e("TAG", "onResponse处理失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mListener.onFailure(new OkHttpException(OTHER_ERROR, e.getMessage()));
            Log.e("TAG", "onResponse处理失败" + e.getMessage());
        }
    }
}

