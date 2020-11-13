package com.jmhy.sdk.sdk;

import com.jmhy.sdk.activity.JmpayActivity;
import com.jmhy.sdk.common.ApiListenerInfo;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.utils.SeferenceGame;
import com.jmhy.sdk.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class RoleinfoRequest {

    private ApiListenerInfo listener;
    private static Context mContext;
    private static RoleinfoRequest mPayData;

    private static ApiListenerInfo mListener;
    //private static String mtype;
    private static String mroleid;
    private static String mrolename;
    private static String mlevel;
    private static String mgender;
    private static String mserverno;
    private static String mbalance;
    private static String mpower;
    private static String mviplevel;
    private static String mext;
    private static String mservername;

    public static RoleinfoRequest getInstatnce(Context context, String type, String roleid,
                                               String rolename, String level, String gender, String serverno, String servername,
                                               String balance, String power, String viplevel, String ext) {
        if (mPayData == null) {
            mPayData = new RoleinfoRequest();
        }
        mContext = context;
        //mtype=type;
        mroleid = roleid;
        mrolename = rolename;
        mlevel = level;
        mgender = gender;
        mserverno = serverno;
        mservername = servername;
        mbalance = balance;
        mpower = power;
        mviplevel = viplevel;
        mext = ext;
        Utils.getSeferencegame(context);
        payHttp(type);
        saveGameuser(type, mroleid,
                mrolename, mlevel, mgender, mserverno, mservername, mbalance,
                mpower, mviplevel, mext);
        return mPayData;
    }


    /**
     * http请求，初始化接口
     */
    public static void payHttp(String mtype) {
        JmhyApi.get().starRole(mContext, AppConfig.appKey,
                AppConfig.openid, mtype, mroleid,
                mrolename, mlevel, mgender, mserverno, mservername, mbalance,
                mpower, mviplevel, mext, new ApiRequestListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onError(int statusCode,String msg) {
                        // TODO Auto-generated method stub

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

    public static void turnToIntent(String url) {
        if (TextUtils.isEmpty(url)) {
            // Toast.makeText(mContext, "此功能暂未开通", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("url", url);
        intent.setClass(mContext, JmpayActivity.class);
        mContext.startActivity(intent);

    }

    public static void saveGameuser(String type, String roleid,
                                    String rolename, String level, String gender, String serverno, String servername,
                                    String balance, String power, String viplevel, String ext) {
        SeferenceGame.getInstance(mContext).savePreferenceData("gameuser", "type", type);
        SeferenceGame.getInstance(mContext).savePreferenceData("gameuser", "roleid", roleid);
        SeferenceGame.getInstance(mContext).savePreferenceData("gameuser", "rolename", rolename);
        SeferenceGame.getInstance(mContext).savePreferenceData("gameuser", "level", level);
        SeferenceGame.getInstance(mContext).savePreferenceData("gameuser", "gender", gender);
        SeferenceGame.getInstance(mContext).savePreferenceData("gameuser", "serverno", serverno);
        SeferenceGame.getInstance(mContext).savePreferenceData("gameuser", "servername", servername);
        SeferenceGame.getInstance(mContext).savePreferenceData("gameuser", "balance", balance);
        SeferenceGame.getInstance(mContext).savePreferenceData("gameuser", "power", power);
        SeferenceGame.getInstance(mContext).savePreferenceData("gameuser", "viplevel", viplevel);
        SeferenceGame.getInstance(mContext).savePreferenceData("gameuser", "ext", ext);
    }

}