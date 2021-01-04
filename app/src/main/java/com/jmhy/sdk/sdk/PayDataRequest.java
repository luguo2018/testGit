package com.jmhy.sdk.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.jmhy.sdk.activity.FloatPayActivity;
import com.jmhy.sdk.activity.JmpayActivity;
import com.jmhy.sdk.common.ApiListenerInfo;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.PayData;
import com.jmhy.sdk.model.PaymentInfo;
import com.jmhy.sdk.utils.DialogUtils;
import com.jmhy.sdk.utils.FloatUtils;
import com.jmhy.sdk.utils.Utils;

public class PayDataRequest {

    private ApiListenerInfo listener;
    private static Context mContext;
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
        payHttp();
        return mPayData;
    }


    /**
     * http请求，初始化接口
     */
    public static void payHttp() {
        JmhyApi.get().starCreate(mContext, AppConfig.appKey, AppConfig.openid,
                mPayInfo.getCporderid(), mPayInfo.getOrdername(), mPayInfo.getAmount(), mPayInfo.getRoleid(),
                mPayInfo.getRolename(), mPayInfo.getLevel(), mPayInfo.getGender(), mPayInfo.getServerno(), mPayInfo.getZoneName(), mPayInfo.getBalance(),
                mPayInfo.getPower(), mPayInfo.getViplevel(), mPayInfo.getExt(), new ApiRequestListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        // TODO Auto-generated method stub
                        PayData payData = (PayData) obj;
                        String url = Utils
                                .toBase64url(payData.getOcontent());
                        turnToIntent(url);
                    }

                    @Override
                    public void onError(int statusCode,String msg) {
                        Activity activity = (Activity) mContext;
                        DialogUtils.showTip(activity, msg+"");
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
        if (floatPayActivity == null) {
            floatPayActivity = new FloatPayActivity((Activity) mContext);
            floatPayActivity.setViews(url);
            floatPayActivity.show();
        } else {
            floatPayActivity.setViews(url);
            floatPayActivity.show();
        }
        FloatUtils.destroyFloat();
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