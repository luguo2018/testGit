package com.jmhy.sdk.sdk;

import com.jmhy.sdk.bean.InitInfo;
import com.jmhy.sdk.model.InitExt;
import com.jmhy.sdk.utils.DialogUtils;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.common.InitListener;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.push.PushService;
import com.jmhy.sdk.utils.FloatUtils;
import com.jmhy.sdk.utils.PackageUtils;
import com.jmhy.sdk.utils.Seference;
import com.jmhy.sdk.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class InitData {

    private InitListener listener;
    private Context context;

    private String ver_id;
    private Seference seference;

    public InitData(Context context, String ver_id, InitListener listener) {
        this.context = context;
        this.ver_id = ver_id;
        this.listener = listener;
        seference = new Seference(context);
        Init();
    }

    public void Init() {
        initHttp();
    }

    /**
     * 初始化接口
     */
    public void initHttp() {
        InitExt ext = new InitExt();
        ext.wechat = PackageUtils.isInstall(context, "com.tencent.mm");
        ext.qq = PackageUtils.isInstall(context, "com.tencent.mobileqq");
        ext.alipay = PackageUtils.isInstall(context, "com.eg.android.AlipayGphone");

        //判断是否模拟器
        ext.isEmu = Utils.isEmulator(context);
        ext.isEmu2 = Utils.checkIsRunningInEmulator(context);
        ext.isHasSimCard = Utils.ishasSimCard(context);
        JmhyApi.get().startInit(context,
                ver_id, ext, new ApiRequestListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        Log.i("jimi", "参数" + obj);
                        InitInfo result = (InitInfo) obj;
                        setInit(result);
                    }

                    @Override
                    public void onError(int statusCode) {
                        DialogUtils.showTip((Activity) context, statusCode + "");
                    }
                });
    }

    /**
     * 接口返回数据处理
     */
    public void sendData(int num, Object data, Handler callback) {
        Message msg = callback.obtainMessage();
        msg.what = num;
        msg.obj = data;
        msg.sendToTarget();
    }


    /**
     * 赋值初始化信息
     */
    public void setInit(InitInfo result) {
        AppConfig.Token = result.getAccess_token();
        AppConfig.iphoneidList = result.getCode_area_list();
        AppConfig.USERAGREEMENTURL = Utils.toBase64url(result.getUser_agreement_url());
        AppConfig.KEFU = Utils.toBase64url(result.getCustomer_service_url());
        AppConfig.is_user_float_on = result.getIs_user_float_on() + "";
        AppConfig.is_sdk_float_on = result.getIs_sdk_float_on() + "";
        AppConfig.is_service_float_on = result.getIs_service_float_on() + "";

        AppConfig.is_visitor_on = result.getIs_visitor_on() + "";
        AppConfig.is_visitor_on_phone = result.getIs_visitor_on_phone() + "";
        AppConfig.is_auto_login_on = result.getIs_auto_login_on() + "";
        AppConfig.is_log_on = result.getIs_log_on() + "";
        AppConfig.is_reg_login_on = result.getIs_reg_login_on() + "";
        AppConfig.FPWD = Utils.toBase64url(result.getForget_password_url());
        AppConfig.add_global_script_url = Utils.toBase64url(result.getAdd_global_script_url());
        AppConfig.switch_login = result.getSwitch_login() + "";
        AppConfig.skin = result.getSkin();
        AppConfig.sdkList = result.getChannel_sdk_list();
        AppConfig.h5GameUrl = Utils.toBase64url(result.getH5_game_url());
        if (result.getMoblie_direct_login() != null) {
            AppConfig.oneKeyLogin_SecretKey = result.getMoblie_direct_login().optString("clientSecret");
            Log.i("测试jimsdk", "一键登录key:" + AppConfig.oneKeyLogin_SecretKey);
        }
        seference.savePreferenceData("game", "token", result.getAccess_token());
        seference.savePreferenceData("game", "onlintiem", result.getOnline_report_interval() + "");
        seference.savePreferenceData("game", "userfloat", result.getIs_user_float_on() + "");
        seference.savePreferenceData("game", "servicefloat", result.getIs_service_float_on() + "");
        seference.savePreferenceData("game", "scripturl", Utils.toBase64url(result.getAdd_global_script_url()));

        if (result.getOnline_report_interval() != 0) {
            AppConfig.ONLIE_TIEM = Long.parseLong(result.getOnline_report_interval() + "");
            Intent pushIntent = new Intent(context, PushService.class);
            context.startService(pushIntent);
        }
        String url = Utils.toBase64url(result.getShow_url_after_init());
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("url", url);
            intent.putExtra("notice", true);
            intent.setClass(context, JmUserinfoActivity.class);
            context.startActivity(intent);
        }
        listener.Success("success");

    }

}
