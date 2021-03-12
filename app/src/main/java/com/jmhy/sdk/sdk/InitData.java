package com.jmhy.sdk.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.common.InitListener;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.InitExt;
import com.jmhy.sdk.model.InitMsg;
import com.jmhy.sdk.push.PushService;
import com.jmhy.sdk.utils.DialogUtils;
import com.jmhy.sdk.utils.MediaUtils;
import com.jmhy.sdk.utils.PackageUtils;
import com.jmhy.sdk.utils.SecurityUtils;
import com.jmhy.sdk.utils.Seference;
import com.jmhy.sdk.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class InitData {

    private InitListener listener;
    private Context context;

    private String ver_id;
    private Seference seference;
    private File file;
    private static boolean isFirstInit=true;
    private String TAG="JimiInitData";

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
        final InitExt ext = new InitExt();
        ext.wechat = PackageUtils.isInstall(context, "com.tencent.mm");
        ext.qq = PackageUtils.isInstall(context, "com.tencent.mobileqq");
        ext.alipay = PackageUtils.isInstall(context, "com.eg.android.AlipayGphone");

        //判断是否模拟器
        ext.isEmu = Utils.isEmulator(context);
        ext.isEmu2 = Utils.checkIsRunningInEmulator(context);
        ext.isEmu3 = Utils.hasHardKey(context);
        ext.isHasSimCard = Utils.ishasSimCard(context);
        startInitRequest(ext);
     }

    private void startInitRequest(final InitExt ext) {
        JmhyApi.get().startInit(context, ver_id, ext, new ApiRequestListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        Log.i(TAG, "参数" + obj);
                        InitMsg result = (InitMsg) obj;
                        setInit(result);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                String md5_float_url_name = SecurityUtils.getMD5Str(AppConfig.float_icon_url);
                                String md5_loading_url_name = SecurityUtils.getMD5Str(AppConfig.web_loading_url);
                                String logo_url = SecurityUtils.getMD5Str(AppConfig.login_logo_url);
                                File icon_file,loading_file,logo_file;
                                if (file==null) {
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
                                    } else {
                                        file = JiMiSDK.mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                    }
                                }

                                icon_file = new File(file + "/" + md5_float_url_name + ".gif");
                                if (!icon_file.exists()){
                                    String path = MediaUtils.getImagePath(AppConfig.	float_icon_url, context);
                                    MediaUtils.copyFile(path, icon_file.toString());
                                }else{
                                    Log.i(TAG,"icon_file文件存在"+icon_file);
                                }

                                loading_file = new File(file + "/" + md5_loading_url_name + ".gif");
                                if (!loading_file.exists()){
                                    String path =MediaUtils.getImagePath(AppConfig.web_loading_url, context);
                                    MediaUtils.copyFile(path, loading_file.toString());
                                }else{
                                    Log.i(TAG,"loading_file文件存在"+loading_file);
                                }

                                if (AppConfig.login_logo_url != null && !AppConfig.login_logo_url.equals("")) {
                                    logo_file = new File(file + "/" + logo_url + ".png");
                                    if (!logo_file.exists()){
                                        Log.i(TAG,"logo_file文件存储到"+logo_file);
                                        String path =MediaUtils.getImagePath(AppConfig.login_logo_url, context);
                                        MediaUtils.copyFile(path, logo_file.toString());
                                    }else{
                                        Log.i(TAG,"logo_file文件存在"+logo_file);
                                    }
                                }

                            }
                        }).start();

                    }

                    @Override
                    public void onError(int statusCode,String msg) {
                        Log.i(TAG,(isFirstInit?"是":"非")+"第一次初始化异常,code:"+statusCode+",msg:"+msg);
                        if (isFirstInit){//初始化第一次失败，重发一次
                            isFirstInit=false;
                            startInitRequest(ext);
                        }else{
                            DialogUtils.showTip((Activity) context, msg);
                        }
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
    public void setInit(InitMsg result) {

        try {
            AppConfig.change_game_name = result.getChange_game_name();
            AppConfig.Token = result.getAccess_token();
            AppConfig.iphoneidList = result.getCode_area_list();
            AppConfig.USERAGREEMENTURL = Utils.toBase64url(result.getUseragreementurl());
            AppConfig.KEFU = Utils.toBase64url(result.getCustomerserviceurl());
            AppConfig.is_user_float_on = result.getUser_float();
            AppConfig.is_sdk_float_on = result.getSdk_float();
            AppConfig.is_service_float_on = result.getService_float();

            AppConfig.is_visitor_on = result.getVisitor();
            AppConfig.is_visitor_on_phone = result.getIsvisitoronphone();
            AppConfig.is_auto_login_on = result.getAuto_login();
            AppConfig.is_log_on = result.getLog_on();
            AppConfig.is_reg_login_on = result.getReg_login();
            AppConfig.FPWD = Utils.toBase64url(result.getForgetpasswordurl());
            AppConfig.add_global_script_url = Utils.toBase64url(result.getAddglobalscripturl());
            AppConfig.switch_login = result.getSwitch_login();
            AppConfig.skin = result.getSkin();
            AppConfig.sdkList = result.getChannel_sdk_list();
            AppConfig.h5GameUrl = Utils.toBase64url(result.getH5_game_url());
            if (result.getMoblie_direct_login() != null && !result.getMoblie_direct_login().isEmpty() && !result.getMoblie_direct_login().equals("")) {

                JSONObject jsonObject = null;
                try {
                    if (result.getMoblie_direct_login().length() > 2) {
                        jsonObject = new JSONObject(result.getMoblie_direct_login());
                        AppConfig.oneKeyLogin_SecretKey = jsonObject.getString("clientSecret");
                        Log.i("测试jimsdk", "一键登录key:" + AppConfig.oneKeyLogin_SecretKey);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            seference.savePreferenceData("game", "token", result.getAccess_token());
            seference.savePreferenceData("game", "onlintiem", result.getOnlinereportinterval());
            seference.savePreferenceData("game", "userfloat", result.getUser_float());
            seference.savePreferenceData("game", "servicefloat", result.getService_float());
            seference.savePreferenceData("game", "scripturl", Utils.toBase64url(result.getAddglobalscripturl()));

            if (!result.getOnlinereportinterval().equals("0")) {
                AppConfig.ONLIE_TIEM = Long.parseLong(result.getOnlinereportinterval());
                Intent pushIntent = new Intent(context, PushService.class);
                context.startService(pushIntent);
                //Log.i("kk", "间隔时间"+ AppConfig.ONLIE_TIEM);
            }
            //SeferenceGame.getInstance(context).savePreferenceData("gameuser", "time", System.currentTimeMillis()+"");
            String url = Utils.toBase64url(result.getShowurlafterint());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
