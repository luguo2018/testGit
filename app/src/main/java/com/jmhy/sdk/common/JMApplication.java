package com.jmhy.sdk.common;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;


import com.baidu.mobads.action.BaiduAction;
import com.baidu.mobads.action.PrivacyStatus;
import com.bun.miitmdid.core.JLibrary;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.utils.Utils;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import java.util.List;

public class JMApplication extends Application {
    private static final String TAG = "JMApplication";
    public static Class<JMApplication> clazz = JMApplication.class;
    //    private static String xiaomi_id="2882303761518747722";
//    private static String xiaomi_key="5871874773722";
//    private static String oppo_key="5f9d639e101341fa87412c1f92979876";
//    private static String oppo_secret="180af2819310488b8e2f352caf8aed23";
//    private static String meizu_appId ="135767";
//    private static String meizu_appKey ="f4d34244253b4be5b10eaf11c11b4745";
//    private static String umeng_appKey ="5f810b5d80455950e4a453b3";
//    private static String umeng_messageSecret ="93a1996ee6e514cce6655eac0da83342";
    private static String umeng_appKey, umeng_messageSecret, xiaomi_id, xiaomi_key, oppo_key, oppo_secret, meizu_appId, meizu_appKey, huawei_appId, vivo_apiKey;

    public static String baiduAppId;
    public static String baiduAppKey;
    public static boolean isApplication=true;


    @Override
    public void onCreate() {
        super.onCreate();
//        MultiDex.install(this);
//        DealCrash crashHandler = DealCrash.getInstance();
//        crashHandler.init(this);
        baiduInit(this);

        //因为项目集成了友盟推送，友盟推送自己会创建一个进程，加上app默认的进程，一共就有2个进程了，导致Application的onCreate执行2次，线程也就创建了2次。
        //判断当前进程为自己的进程才走一次init取参初始化数据sdk
        String processName = getProcessName(this,android.os.Process.myPid());
        Log.e(TAG, "全局初始化"+processName+"当前包名："+this.getPackageName());
        if(processName != null){
            boolean defaultProcess = processName.equals(this.getPackageName());
            if(defaultProcess){
                //当前应用的初始化
                isApplication=true;
                JiMiSDK.onApplicationOnCreate(this);
            }
        }

        Log.e(TAG, "onCreate: JMApplicationOnCreate" + this.getPackageName());
        if (getPushParam()) {
            initUmengSDK(this);
        } else {
            UMConfigure.init(this, "5f810b5d80455950e4a453b3", "JM", UMConfigure.DEVICE_TYPE_PHONE, null);
        }
        manufacturerPush();//厂商推送
    }

    private void baiduInit(final Context context) {
        try {
            baiduAppId = Utils.getMetaValue(this, "baiduAppId").replaceAll("baiduAppId=", "");
            baiduAppKey = Utils.getMetaValue(this, "baiduAppKey").replaceAll("baiduAppKey=", "");

            Log.i("测试", baiduAppId + "-------" + baiduAppKey);
            if (baiduAppKey != null && !baiduAppKey.equals("")) {
                JLibrary.InitEntry(this);
                Log.i("测试", "开始百度");
                // 调用SDK初始化接口：
                // 1. 调用位置：必须在Application的onCreate方法中调用。
                // 2. 必须在其他的数据上报接口调用之前调用，否则其他接口都将无法使用。
                // USER_ACTION_SET_ID：SDK应用ID,需要在百度申请; Constants.APP_SECRET_KEY:SDK应用密钥，需要在百度申请
                // 是否在控制台输出⽇志，可⽤于观察⽤⼾⾏为⽇志上报情况，建议仅在调试时使⽤，release版 请设置为false ！
                BaiduAction.setPrintLog(false);
                BaiduAction.init(context, Long.parseLong(baiduAppId), baiduAppKey);
                // 设置应用激活的间隔（默认30天）
                BaiduAction.setActivateInterval(context, 7);
                BaiduAction.setPrivacyStatus(PrivacyStatus.AGREE);
                Log.i(TAG, "百度初始化完毕");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "bd异常" + e);
        }
    }

    private boolean getPushParam() {
        try {
            Log.i(TAG,"--------"+Utils.getMetaValue(this, "umeng_appKey"));
            if(Utils.getMetaValue(this, "umeng_appKey")==null){
                return false;
            }
            umeng_appKey = Utils.getMetaValue(this, "umeng_appKey").replaceAll("umeng_appKey=", "");
            umeng_messageSecret = Utils.getMetaValue(this, "umeng_messageSecret").replaceAll("umeng_messageSecret=", "");
            xiaomi_id = Utils.getMetaValue(this, "xiaomi_id").replaceAll("xiaomi_id=", "");
            xiaomi_key = Utils.getMetaValue(this, "xiaomi_key").replaceAll("xiaomi_key=", "");
            oppo_key = Utils.getMetaValue(this, "oppo_key").replaceAll("oppo_key=", "");
            oppo_secret = Utils.getMetaValue(this, "oppo_secret").replaceAll("oppo_secret=", "");
            meizu_appId = Utils.getMetaValue(this, "meizu_appId").replaceAll("meizu_appId=", "");
            meizu_appKey = Utils.getMetaValue(this, "meizu_appKey").replaceAll("meizu_appKey=", "");

            huawei_appId = Utils.getMetaValue(this, "com.huawei.hms.client.appid").replaceAll("appid=", "");
            vivo_apiKey = Utils.getMetaValue(this, "com.vivo.push.api_key");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "error:" + e);
            return false;
        }

    }

    private void initUmengSDK(final Context context) {
        UMConfigure.setLogEnabled(false);
        UMConfigure.init(context, umeng_appKey, "jm", UMConfigure.DEVICE_TYPE_PHONE, umeng_messageSecret);
        PushAgent mPushAgent = PushAgent.getInstance(this);
//        mPushAgent.setResourcePackageName("com.jmhy.sdk.common");
        mPushAgent.setResourcePackageName("com.jmhy.sdk");
//        mPushAgent.setResourcePackageName(this.getPackageName());
//        mPushAgent.setResourcePackageName(context.getPacConnectionCrosedExceptionkageName());
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String s) {
                Log.i(TAG, getOpPackageName() + "---" + getPackageName() + "--->>> 注册成功,token:" + s);
                AppConfig.push_token = s;
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.i(TAG, "--->>> 注册失败,code:" + s + ",msg:" + s1);
            }
        });
        mPushAgent.onAppStart();

    }

    private void manufacturerPush() {
        try {
            if (xiaomi_id != null && xiaomi_key != null && !xiaomi_id.equals("") && !xiaomi_key.equals("")) {
                Log.i("查看", "xiaomi_id" + xiaomi_id);
                MiPushRegistar.register(this, xiaomi_id, xiaomi_key);
            }
            if (oppo_key != null && oppo_secret != null && !oppo_key.equals("") && !oppo_secret.equals("")) {
                Log.i("查看", "oppo_key" + oppo_key + "oppo_secret" + oppo_secret);
                OppoRegister.register(this, oppo_key, oppo_secret);
            }
            if (meizu_appId != null && meizu_appKey != null && !meizu_appId.equals("") && !meizu_appKey.equals("")) {
                Log.i("查看", "meizu_appId" + meizu_appId);
                MeizuRegister.register(this, meizu_appId, meizu_appKey);
            }
            if (huawei_appId != null && !huawei_appId.equals("")) {
                Log.i("查看", "huawei_appId" + huawei_appId);
                HuaWeiRegister.register(this);
            }
            if (vivo_apiKey != null && !vivo_apiKey.equals("")) {
                Log.i("查看", "vivo_apiKey" + vivo_apiKey);
                VivoRegister.register(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "push error:" + e);
        }
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

}
