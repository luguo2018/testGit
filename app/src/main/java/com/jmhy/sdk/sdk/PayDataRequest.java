package com.jmhy.sdk.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.jmhy.sdk.activity.FloatPayActivity;
import com.jmhy.sdk.activity.JmpayActivity;
import com.jmhy.sdk.common.ApiListenerInfo;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.PayData;
import com.jmhy.sdk.model.PaymentInfo;
import com.jmhy.sdk.utils.DialogUtils;
import com.jmhy.sdk.utils.Utils;

public class PayDataRequest {

    private ApiListenerInfo listener;
    private static Context mContext;
    private static ApiAsyncTask payTask; // 初始化
    private static PayDataRequest mPayData;
    private static PaymentInfo mPayInfo;
    private static ApiListenerInfo mListener;
    //private static PayData payData;

    public static PaymentInfo getmPayInfo() {
        return mPayInfo;
    }

    public static PayData getPayData() {
        return AppConfig.payData;
    }

    public static void setmPayInfo(PaymentInfo mPayInfo) {
        PayDataRequest.mPayInfo = mPayInfo;
    }

    public static PayDataRequest getInstatnce(Context context, PaymentInfo payInfo,
                                              ApiListenerInfo listener) {
        if (mPayData == null) {
            mPayData = new PayDataRequest();
        }
        mContext = context;
        mListener = listener;
        mPayInfo = payInfo;
        Utils.getSeferencegame(context);
        handler.sendEmptyMessage(AppConfig.GET_PAY);
        return mPayData;
    }


    /**
     * http请求，初始化接口
     */
    public static void payHttp() {
        payTask = JmhyApi.get().starCreate(mContext, AppConfig.appKey, AppConfig.openid,
                mPayInfo.getCporderid(), mPayInfo.getOrdername(), mPayInfo.getAmount(), mPayInfo.getRoleid(),
                mPayInfo.getRolename(), mPayInfo.getLevel(), mPayInfo.getGender(), mPayInfo.getServerno(), mPayInfo.getZoneName(), mPayInfo.getBalance(),
                mPayInfo.getPower(), mPayInfo.getViplevel(), mPayInfo.getExt(), new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        // TODO Auto-generated method stub
                        if (obj != null) {
                            PayData payData = (PayData) obj;
                            if (payData.getCode().equals("0")) {
                                sendData(AppConfig.PAY_SUCCESS, obj,
                                        handler);

                            } else {
                                sendData(AppConfig.FLAG_FAIL,
                                        payData.getMessage(), handler);
                            }
                        } else {
                            sendData(AppConfig.FLAG_FAIL,
                                    AppConfig.getString(mContext, "http_rror_msg"), handler);
                        }

                    }

                    @Override
                    public void onError(int statusCode) {
                        // TODO Auto-generated method stub
                        sendData(AppConfig.FLAG_FAIL,
                                AppConfig.getString(mContext, "http_rror_msg"), handler);
                    }
                });
    }


    /*
     * 接口返回数据处理
     */
    public static void sendData(int num, Object data, Handler callback) {
        Message msg = callback.obtainMessage();
        msg.what = num;
        msg.obj = data;
        msg.sendToTarget();
    }

    private static Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.GET_PAY:
                    payHttp();
                    break;
                case AppConfig.PAY_SUCCESS:
                    PayData payData = (PayData) msg.obj;
                    String url = Utils
                            .toBase64url(payData.getOcontent());
                    turnToIntent(url);
                    break;
                case AppConfig.FLAG_FAIL:
                    Activity activity = (Activity)mContext;
                    String message = (String) msg.obj;
                    DialogUtils.showTip(activity, message);
                    break;
            }
        }
    };
    static FloatPayActivity floatPayActivity;
    public static void turnToIntent(String url) {
        if (TextUtils.isEmpty(url)) {
            // Toast.makeText(mContext, "此功能暂未开通", Toast.LENGTH_SHORT).show();
            return;
        }

//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtra("url", url);
//        intent.setClass(mContext, JmpayActivity.class);
//        mContext.startActivity(intent);
        if(floatPayActivity==null){
            floatPayActivity = new FloatPayActivity((Activity) mContext);
            floatPayActivity.setViews(url);
        }else {
            floatPayActivity.setViews(url);
        }
    }

    public static void turnToIntent(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            // Toast.makeText(mContext, "此功能暂未开通", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("url", url);
        intent.setClass(context, JmpayActivity.class);
        context.startActivity(intent);
    }
}