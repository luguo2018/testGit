package com.jmhy.sdk.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.LoginMessageinfo;
import com.jmhy.sdk.model.PaymentInfo;

public class JMSDK {
    private static final String TAG = JMSDK.class.getSimpleName();
    private static Handler handler;

    /**
     * 初始化接口
     */
    public static void initInterface(final Context context, int appid, String appkey, final InitListener listener) {
        Log.i(TAG, "initInterface");
        handler = new Handler();
        JiMiSDK.initInterface(context, appid, appkey, listener);
    }


    /**
     * 登录接口
     *
     * @param context
     * @param listener
     */
    public static void login(final Activity context, int appid, String appkey, final ApiListenerInfo listener) {
        Log.i(TAG, "login");
        JiMiSDK.login(context, appid, appkey, new ApiListenerInfo() {
            @Override
            public void onSuccess(final Object obj) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {


                        if (obj != null) {
                            LoginMessageinfo data = (LoginMessageinfo) obj;
                            String result = data.getResult();

                            if (TextUtils.equals("success", result)) {
                                if (TextUtils.equals(AppConfig.is_sdk_float_on, "1")) {
                                    JiMiSDK.showFloat();
                                }
                            }
                        }
                        listener.onSuccess(obj);
                    }
                });
            }
        });

    }

    /**
     * 充值接口
     *
     * @param activity
     * @param payInfo
     * @param listener
     */
    public static void payment(final Activity activity, final PaymentInfo payInfo, final ApiListenerInfo listener) {
        Log.i(TAG, "payment " + payInfo.toString());
        //创建订单
        JiMiSDK.payment(activity, payInfo, listener);
    }


    public static void onCreate(Activity activity) {
        Log.i(TAG, "onCreate");
        JiMiSDK.onCreate(activity);
    }


    public static void onStop(Activity activity) {
        Log.i(TAG, "onStop");
        JiMiSDK.onStop(activity);
    }

    public static void onDestroy(Activity activity) {
        Log.i(TAG, "onDestroy");
        JiMiSDK.onDestroy(activity);
    }

    public static void onResume(Activity activity) {
        Log.i(TAG, "onResume");
        JiMiSDK.onResume(activity);
    }

    public static void onPause(Activity activity) {
        Log.i(TAG, "onPause");
        JiMiSDK.onPause(activity);
    }

    public static void onRestart(Activity activity) {
        Log.i(TAG, "onRestart");
        JiMiSDK.onRestart(activity);
    }

    public static void onNewIntent(final Intent intent) {
        Log.i(TAG, "onNewIntent");
        JiMiSDK.onNewIntent(intent);
    }

    public static void onActivityResult(Activity activity, int requestCode,
                                        int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        JiMiSDK.onActivityResult(activity, requestCode, resultCode, data);
    }

    /*
     * 切换账号回调
     */
    public static void setUserListener(UserApiListenerInfo listener) {
        Log.i(TAG, "setUserListener");
        JiMiSDK.setUserListener(listener);
    }

    /***
     *
     * @param context
     * @param type 分别为玩家创建用户角色(1) 进入服务器(2)、、玩家升级(3)
     * @param roleid
     * @param rolename
     * @param level
     * @param gender
     * @param serverno
     * @param zoneName
     * @param balance
     * @param power
     * @param viplevel
     * @param roleCTime
     * @param roleLevelMTime
     * @param ext
     */
    public static void setExtData(Context context, String type, String roleid,
                                  String rolename, String level, String gender, String serverno, String zoneName,
                                  String balance, String power, String viplevel, String roleCTime, String roleLevelMTime, String ext) {
        Log.i(TAG, "setExtData type=" + type + ",roleid=" + roleid + ",rolename=" + rolename + ",level=" + level + ",gender=" + gender + ",serverno=" + serverno +
                ",zoneName=" + zoneName + ",balance=" + balance + ",power=" + power + ",viplevel=" + viplevel + ",roleCTime=" + roleCTime + ",roleLevelMTime=" + roleLevelMTime +
                ",ext=" + ext);

        JiMiSDK.setExtData(context, type, roleid, rolename, level, gender, serverno, zoneName, balance, power, viplevel, roleCTime, roleLevelMTime, ext);
    }

    /**
     * 退出接口
     */
    public static void exit(final Activity activity,
                            final ExitListener exitlistener) {
        Log.i(TAG, "exit");
        JiMiSDK.exit(activity, new ExitListener() {
            @Override
            public void ExitSuccess(String s) {
                exitlistener.ExitSuccess(s);

                activity.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }

            @Override
            public void fail(String s) {
                exitlistener.fail(s);
            }
        });


    }

    /**
     * 切换账号接口
     */
    public static void switchAccount(Context context) {
        JiMiSDK.switchAccount(context);

    }

}
