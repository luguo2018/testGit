package com.jmhy.sdk.sdk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.jmhy.sdk.BuildConfig;
import com.jmhy.sdk.common.JMApplication;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.http.JSONParse;
import com.jmhy.sdk.http.OkHttpException;
import com.jmhy.sdk.http.ResponseCallback;
import com.jmhy.sdk.http.OkHttpManager;
import com.jmhy.sdk.model.Guest;
import com.jmhy.sdk.model.InitExt;
import com.jmhy.sdk.model.InitMsg;
import com.jmhy.sdk.model.LoginMessage;
import com.jmhy.sdk.model.MobileUser;
import com.jmhy.sdk.model.Msg;
import com.jmhy.sdk.model.OnlineMessage;
import com.jmhy.sdk.model.PayData;
import com.jmhy.sdk.model.Registermsg;
import com.jmhy.sdk.utils.DeviceInfo;
import com.jmhy.sdk.utils.Utils;

import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class JmhyApi {
    private static JmhyApi instance;

    private final static int DEVICE = 1;// 安卓设备
    private final static String TAG = "JimiApi";

    private DeviceInfo deviceInfo;
    private String signValidString;

    private JmhyApi() {

    }

    public static JmhyApi get() {

        if (instance == null) {
            instance = new JmhyApi();
        }
        return instance;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    private String getSignValidString(byte[] paramArrayOfByte) throws NoSuchAlgorithmException {
        MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
        localMessageDigest.update(paramArrayOfByte);
        return toHexString(localMessageDigest.digest());
    }

    public String toHexString(byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null) {
            return null;
        }
        StringBuilder localStringBuilder = new StringBuilder(2 * paramArrayOfByte.length);
        for (int i = 0; ; i++) {
            if (i >= paramArrayOfByte.length) {
                return localStringBuilder.toString();
            }
            String str = Integer.toString(0xFF & paramArrayOfByte[i], 16);
            if (str.length() == 1) {
                str = "0" + str;
            }
            localStringBuilder.append(str);
        }
    }

    /**
     * 初始化接口
     *
     * @param context
     * @param ver
     * @param listener
     * @return
     */
    public void startInit(Context context,
                          String ver, InitExt ext, final ApiRequestListener listener) {

        if (deviceInfo == null) {
            deviceInfo = new DeviceInfo(context);
        }
        try {   //BuildConfig.APPLICATION_ID   当前应用包名
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            signValidString = getSignValidString(packageInfo.signatures[0].toByteArray());
            Log.e("获取应用签名", BuildConfig.APPLICATION_ID + "---:" + signValidString);
        } catch (Exception e) {
            Log.e("获取应用签名", "异常:" + e);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", "");
        params.put("apkSign", signValidString);
        params.put("time", System.currentTimeMillis() / 1000 + "");

        paramsdata.put("appid", AppConfig.appId + "");
        paramsdata.put("campaign_id", ver + "");
        paramsdata.put("package_name", deviceInfo.getPackagename() + "");
        paramsdata.put("device", DEVICE + "");
        paramsdata.put("sdk_version", AppConfig.SDK_VER + "");
        paramsdata.put("package_version", AppConfig.version + "");
        paramsdata.put("game_version", Utils.getVersion(context));
        paramsdata.put("uuid", deviceInfo.getUuid() + "");
        paramsdata.put("idfa", "");
        paramsdata.put("idfv", "");
        paramsdata.put("imei", deviceInfo.getImei() + "");
        paramsdata.put("androidid", deviceInfo.getSystemId() + "");
        paramsdata.put("manufacturer", deviceInfo.getDevicebrand() + "");
        paramsdata.put("version", deviceInfo.getSystemVer() + "");
        paramsdata.put("device_type", deviceInfo.getModel() + "");
        paramsdata.put("network", deviceInfo.getNetwork() + "");
        paramsdata.put("resolution", deviceInfo.getDeviceScreen() + "");
        paramsdata.put("operator", deviceInfo.getOperator() + "");
        paramsdata.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("is_charged", deviceInfo.getIs_charged() + "");
        paramsdata.put("oaid", JiMiSDK.mOaid);
        paramsdata.put("push_token", AppConfig.push_token);

        paramsdata.put("isApplication", JMApplication.isApplication);
        Log.i("JiMiSDK", "JMApplication.isApplication  -->  " + JMApplication.isApplication);

        String ext_data = "q=" + (ext.qq ? 1 : 0) +
                "&wc=" + (ext.wechat ? 1 : 0) +
                "&ali=" + (ext.alipay ? 1 : 0) +
                "&mn=" + (ext.isEmu ? 1 : 0) +
                "&mn2=" + (ext.isEmu2 ? 1 : 0) +
                "&sim=" + (ext.isHasSimCard ? 1 : 0);

        Log.i("JiMiSDK", "ext_data  -->  " + ext_data);
        paramsdata.put("ext_data", ext_data);
        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        OkHttpManager.getInstance().postRequest(WebApi.ACTION_INIT, params, new ResponseCallback<String>() {

            @Override
            public void onSuccess(String infoString) {
                Log.d(TAG, "onSuccess() called with: initInfoString = [" + infoString.toString() + "]");
                InitMsg initMsg = null;
                try {
                    initMsg = JSONParse.parseInitMsg(infoString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(initMsg);
            }

            @Override
            public void onFailure(OkHttpException e) {
                Log.d(TAG, "OkHttpException e"+e.getEmsg()+"---"+e.getEcode());
                listener.onError(e.getEcode(),e.getEmsg());

            }
        });
    }

    /**
     * 获取验证码
     *
     * @param context
     * @param appKey
     * @param codearea 区号
     * @param type     1注册2登陆3找回密码
     * @param listener
     * @return
     */
    public Call startRequestSMS(Context context, String appKey,
                                String mobile, String codearea, String type,
                                final ApiRequestListener listener) {

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("mobile", mobile + "");
        paramsdata.put("code_area", codearea + "");
        paramsdata.put("type", type);
        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_SMS, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Msg msg = null;
                try {
                    msg = JSONParse.parseRequestSMS(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(msg);
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    /**
     * 手机号码登陆
     *
     * @param context
     * @param appKey
     * @param codearea 区号
     * @param code     验证码
     * @param listener
     * @return
     */
    public Call startloginMoblie(Context context, String appKey,
                                 final String mobile, String codearea, String code,
                                 final ApiRequestListener listener) {

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("mobile", mobile + "");
        paramsdata.put("code_area", codearea + "");
        paramsdata.put("code", code);
        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_PHONE_LOGIN, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String String) {
                MobileUser mobileUser = null;
                try {
                    mobileUser = JSONParse.parseMobilelogin(String);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(mobileUser);

            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    /**
     * 手机号码登陆
     *
     * @param context
     * @param appKey
     * @param codearea 区号
     * @param code     验证码
     * @param listener
     * @return
     */
    public Call startloginMoblie(Context context, String appKey,
                                 String mobile, String codearea, String code, String autoReg,
                                 final ApiRequestListener listener) {

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("mobile", mobile + "");
        paramsdata.put("code_area", codearea + "");
        paramsdata.put("auto_reg", autoReg);
        paramsdata.put("code", code);
        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_PHONE_LOGIN, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String String) {
                MobileUser mobileUser = null;
                try {
                    mobileUser = JSONParse.parseMobilelogin(String);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(mobileUser);

            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    /**
     * 用户注册
     *
     * @param username
     * @param password
     * @param listener
     * @return
     */
    public Call startRegister(
            String username, String password, final ApiRequestListener listener) {

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("uname", username + "");
        paramsdata.put("upass", password + "");

        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_USERREGISTER, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Registermsg registermsg = null;
                try {
                    registermsg = JSONParse.parseuserRegister(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(registermsg);
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;

    }

    /**
     * 手机号码注册
     *
     * @param username
     * @param password
     * @param mobile
     * @param code
     * @param code_area
     * @param listener
     * @return
     */
    public Call startMobileRegister(
            String username, String password, String mobile, String code,
            String code_area, final ApiRequestListener listener) {

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("mobile", mobile + "");
        paramsdata.put("uname", username + "");
        paramsdata.put("upass", password + "");
        paramsdata.put("code_area", code_area + "");
        paramsdata.put("code", code + "");
        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_PHONE_REGISTER, params, new ResponseCallback<String>() {

            @Override
            public void onSuccess(String data) {
                Registermsg registermsg = null;
                try {
                    registermsg = JSONParse.parseuserRegister(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(registermsg);
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    /**
     * 用户自动登录
     *
     * @param logintoken
     * @param listener
     * @return
     */
    public Call starlAutoLogin(String logintoken, final ApiRequestListener listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("login_token", logintoken + "");

        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_AUTOLOGIN, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String loginInfoString) {
                LoginMessage loginMessage = null;
                try {
                    loginMessage = JSONParse.parseAutologin(loginInfoString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(loginMessage);

            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    /**
     * 用户登录
     *
     * @param username
     * @param pwd
     * @param listener
     * @return
     */
    public Call starusreLogin(
            String username, String pwd, final ApiRequestListener listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("uname", username + "");

        paramsdata.put("upass", pwd + "");

        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_USRRLOGIN, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String data) {
                LoginMessage loginMessage = null;
                try {
                    loginMessage = JSONParse.parseAutologin(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(loginMessage);
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    /**
     * 修改用户账号
     */
    public Call startSetAccount(Context context, String appkey, String account, String password, String confirm_password, final ApiRequestListener listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        params.put("uname", account + "");
        params.put("upass", password + "");
        params.put("reupass", confirm_password + "");
        params.put("context", "");
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_SET_ACCOUNT, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Msg msg = null;
                try {
                    msg = JSONParse.parseRequestSMS(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(msg);
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }


    /**
     * 游客登录
     *
     * @param context
     * @param appkey
     * @param listener
     * @return
     */
    public Call starguestLogin(Context context, String appkey,
                               final ApiRequestListener listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        params.put("context", "");
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_GUEST, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Guest  guest = null;
                try {
                    guest = JSONParse.parseGuestlogin(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(guest);
            }

            @Override
            public void onFailure(OkHttpException e) {
                e.printStackTrace();
                Log.i("jimi","异常"+e);
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    /**
     * 创建订单
     *
     * @param context
     * @param appkey
     * @param opendid
     * @param cporderid
     * @param ordername
     * @param amount
     * @param roleid
     * @param rolename
     * @param level
     * @param gender
     * @param serverno
     * @param balance
     * @param power
     * @param viplevel
     * @param ext
     * @param listener
     * @return
     */
    public Call starCreate(Context context, String appkey,
                           String opendid, String cporderid, String ordername, String amount,
                           String roleid, String rolename, String level, String gender,
                           String serverno, String servername, String balance, String power, String viplevel,
                           String ext, final ApiRequestListener listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");

        paramsdata.put("openid", opendid + "");
        paramsdata.put("cp_order_id", cporderid + "");
        paramsdata.put("order_name", ordername + "");
        paramsdata.put("amount", amount + "");
        paramsdata.put("role_id", roleid + "");
        paramsdata.put("role_name", rolename + "");
        paramsdata.put("level", level + "");
        paramsdata.put("gender", gender + "");
        paramsdata.put("server_no", serverno + "");
        paramsdata.put("server_name", servername);
        paramsdata.put("balance", balance + "");
        paramsdata.put("power", power + "");
        paramsdata.put("vip_level", viplevel + "");
        paramsdata.put("ext", ext + "");

        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_CREATE, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String data) {
                PayData payData = null;
                try {
                    payData = JSONParse.parseCreate(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(payData);
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    /***
     * 角色信息提交
     *
     * @param context
     * @param appkey
     * @param opendid
     * @param type
     * @param roleid
     * @param rolename
     * @param level
     * @param gender
     * @param serverno
     * @param balance
     * @param power
     * @param viplevel
     * @param ext
     * @param listener
     * @return
     */
    public Call starRole(Context context, String appkey,
                         String opendid, String type, String roleid, String rolename,
                         String level, String gender, String serverno, String servername, String balance,
                         String power, String viplevel, String ext,
                         final ApiRequestListener listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("openid", opendid + "");
        paramsdata.put("type", type + "");
        paramsdata.put("role_id", roleid + "");
        paramsdata.put("role_name", rolename + "");
        paramsdata.put("level", level + "");
        paramsdata.put("gender", gender + "");
        paramsdata.put("server_no", serverno + "");
        paramsdata.put("server_name", servername);
        paramsdata.put("balance", balance + "");
        paramsdata.put("power", power + "");
        paramsdata.put("vip_level", viplevel + "");
        paramsdata.put("ext", ext + "");

        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_REPORT, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String o) {
                listener.onSuccess(o);
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }


    /**
     * 切换账号
     *
     * @param context
     * @param appkey
     * @param listener
     * @return
     */
    public Call starguserLoginout(Context context, String appkey,
                                  final ApiRequestListener listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        params.put("context", "");
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_LOGINOUT, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String o) {
                listener.onSuccess(o);
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    public Call starbug(Context context, String appkey, String openid,
                        String content, ApiRequestListener listener) {
        if (deviceInfo == null) {
            deviceInfo = new DeviceInfo(context);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("openid", openid + "");
        paramsdata.put("network", deviceInfo.getNetwork() + "");
        paramsdata.put("content", content + "");
        paramsdata.put("error_level", "1");
        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_BUG, params, new ResponseCallback() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onFailure(OkHttpException e) {

            }
        });
        return call;
    }

    /**
     * 在线上报
     *
     * @param context
     * @param appkey
     * @param opendid
     * @param type
     * @param roleid
     * @param rolename
     * @param level
     * @param gender
     * @param serverno
     * @param servername
     * @param balance
     * @param power
     * @param viplevel
     * @param ext
     * @param listener
     * @return
     */
    public Call starOnline(Context context, String appkey,
                           String opendid, String type, String roleid, String rolename, String seconds,
                           String level, String gender, String serverno, String servername, String balance,
                           String power, String viplevel, String ext,
                           final ApiRequestListener listener) {
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("openid", opendid + "");
        paramsdata.put("role_id", roleid + "");
        paramsdata.put("role_name", rolename + "");
        paramsdata.put("online_seconds", seconds + "");
        paramsdata.put("level", level + "");
        paramsdata.put("gender", gender + "");
        paramsdata.put("server_no", serverno + "");
        paramsdata.put("server_name", servername);
        paramsdata.put("balance", balance + "");
        paramsdata.put("power", power + "");
        paramsdata.put("vip_level", viplevel + "");
        paramsdata.put("ext", ext + "");
        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        String url="";
        try {
            if(WebApi.ACTION_ONLINE==null){
                url="https://apisdk.5tc5.com/v1/role/online";
                Log.i("url为空","固定地址");
            }else{
                url=WebApi.ACTION_ONLINE;
            }
        }catch (Exception e){
            e.printStackTrace();
            url="https://apisdk.5tc5.com/v1/role/online";
            Log.i("url异常","固定地址");
        }

        Call call = OkHttpManager.getInstance().postRequest(url, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String String) {
                OnlineMessage onlineMessage = null;
                try {
                    onlineMessage = JSONParse.parseOnlineNotify(String);
                    listener.onSuccess(onlineMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    public Call startOneKeylogin(String oneKeyToken, String appkey, final ApiRequestListener listener) {

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("login_token", oneKeyToken + "");

        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_ONEKEYLOGIN, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String data) {
                LoginMessage loginMessage = null;
                try {
                    loginMessage = JSONParse.parseOneKeylogin(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onSuccess(loginMessage);
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    /******************分割线**********************************************************************************************
     *
     *
     *
     *
     */
    public Call getWebSocketToken(Context context, String access_token, String appKey, final ApiRequestListener listener) {

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", access_token);
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("ext_data", "");
        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_GETTOKEN, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String String) {
                listener.onSuccess(String);
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    public Call getFloatState(Context context, String jm_customer_token, String appKey, final ApiRequestListener listener) {

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("jm_customer_token", jm_customer_token);
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("ext_data", "");
        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_FLOATSTATE, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String String) {
                listener.onSuccess(String);
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }

    public Call clearNotice(Context context, String jm_customer_token, String appKey, final ApiRequestListener listener) {

        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, Object> paramsdata = new HashMap<String, Object>();
        params.put("access_token", AppConfig.Token + "");
        params.put("jm_customer_token", jm_customer_token);
        params.put("time", System.currentTimeMillis() / 1000 + "");
        paramsdata.put("ext_data", "");
        HashmapToJson toJson = new HashmapToJson();
        params.put("context", toJson.toJson(paramsdata));
        Call call = OkHttpManager.getInstance().postRequest(WebApi.ACTION_CLEANNOTICE, params, new ResponseCallback<String>() {
            @Override
            public void onSuccess(String String) {
                listener.onSuccess(String);
            }

            @Override
            public void onFailure(OkHttpException e) {
                listener.onError(e.getEcode(),e.getEmsg());
            }
        });
        return call;
    }


}
