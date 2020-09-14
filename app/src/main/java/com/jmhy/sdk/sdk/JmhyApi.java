package com.jmhy.sdk.sdk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.jmhy.sdk.BuildConfig;
import com.jmhy.sdk.bean.InitInfo;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.http.OkHttpException;
import com.jmhy.sdk.http.ResponseCallback;
import com.jmhy.sdk.http.OkHttpManager;
import com.jmhy.sdk.http.Result;
import com.jmhy.sdk.model.InitExt;
import com.jmhy.sdk.utils.DeviceInfo;
import com.jmhy.sdk.utils.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class JmhyApi {
	private static JmhyApi instance;

	private final static int DEVICE = 1;// 安卓设备

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

	public DeviceInfo getDeviceInfo(){
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
	 * @param appId
	 * @param appKey
	 * @param ver
	 * @param listener
	 * @return
	 */
	public ApiAsyncTask startInit(Context context, int appId, String appKey,
								  String ver, InitExt ext, ApiRequestListener listener) {

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
		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", "");
		params.put("apkSign", signValidString);
		params.put("time", System.currentTimeMillis() / 1000 + "");

		paramsdata.put("appid", appId + "");
		paramsdata.put("campaign_id", ver + "");
		paramsdata.put("package_name", deviceInfo.getPackagename() + "");
		paramsdata.put("device", DEVICE + "");
		paramsdata.put("sdk_version", AppConfig.sdk_version + "");
		paramsdata.put("package_version", AppConfig.version + "");
		paramsdata.put("game_version", Utils.getVersion(context));
		paramsdata.put("uuid", deviceInfo.getUuid() + "");
		paramsdata.put("idfa", "");
		paramsdata.put("idfv", "");
		paramsdata.put("imei", deviceInfo.getImei() + "");
		paramsdata.put("androidid", deviceInfo.getSystemId() + "");
		paramsdata.put("manufacturer",deviceInfo.getDevicebrand()+ "");
		paramsdata.put("version", deviceInfo.getSystemVer() + "");
		paramsdata.put("device_type", deviceInfo.getModel() + "");
		paramsdata.put("network", deviceInfo.getNetwork() + "");
		paramsdata.put("resolution", deviceInfo.getDeviceScreen() + "");
		paramsdata.put("operator", deviceInfo.getOperator() + "");
		paramsdata.put("time", System.currentTimeMillis() / 1000 + "");
		paramsdata.put("is_charged", deviceInfo.getIs_charged() + "");
		paramsdata.put("oaid", JiMiSDK.mOaid);

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
		HashMap<String, String> p = new HashMap<>();
		p.put("access_token", "");
		p.put("apkSign", signValidString);
		p.put("time", (String) params.get("time"));
		p.put("context",toJson.toJson(paramsdata));
		OkHttpManager.getInstance().postRequest(WebApi.ACTION_INIT, p, new ResponseCallback<Result<InitInfo>>() {

			@Override
			public void onSuccess(Result<InitInfo> initInfoResult) {
				Log.d("TAG", "onSuccess() called with: initInfoResult = [" + initInfoResult.toString() + "]");
			}

			@Override
			public void onFailure(OkHttpException e) {

			}
		});
		return null;
//
//		return WebApi.startThreadRequest(WebApi.ACTION_INIT, listener, params,
//				appKey);
	}

	/**
	 * 获取验证码
	 * 
	 * @param context
	 * @param appKey
	 * @param mobile手机号码
	 * @param codearea
	 *            区号
	 * @param type
	 *            1注册2登陆3找回密码
	 * @param listener
	 * @return
	 */
	public ApiAsyncTask startRequestSMS(Context context, String appKey,
			String mobile, String codearea, String type,
			ApiRequestListener listener) {

		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token + "");
		params.put("time", System.currentTimeMillis() / 1000 + "");
		paramsdata.put("mobile", mobile + "");
		paramsdata.put("code_area", codearea + "");
		paramsdata.put("type", type);
		HashmapToJson toJson = new HashmapToJson();
		params.put("context", toJson.toJson(paramsdata));
		return WebApi.startThreadRequest(WebApi.ACTION_SMS, listener, params,
				appKey);
	}

	/**
	 * 手机号码登陆
	 * 
	 * @param context
	 * @param appKey
	 * @param mobile手机号码
	 * @param codearea
	 *            区号
	 * @param code
	 *            验证码
	 * @param listener
	 * @return
	 */
	public ApiAsyncTask startloginMoblie(Context context, String appKey,
											  String mobile, String codearea, String code,
											  ApiRequestListener listener) {

		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token + "");
		params.put("time", System.currentTimeMillis() / 1000 + "");
		paramsdata.put("mobile", mobile + "");
		paramsdata.put("code_area", codearea + "");
		paramsdata.put("code", code);
		HashmapToJson toJson = new HashmapToJson();
		params.put("context", toJson.toJson(paramsdata));
		return WebApi.startThreadRequest(WebApi.ACTION_PHONE_LOGIN, listener,
				params, appKey);
	}

	/**
	 * 手机号码登陆
	 *
	 * @param context
	 * @param appKey
	 * @param mobile手机号码
	 * @param codearea
	 *            区号
	 * @param code
	 *            验证码
	 * @param listener
	 * @return
	 */
	public ApiAsyncTask startloginMoblie(Context context, String appKey,
										 String mobile, String codearea, String code, String autoReg,
										 ApiRequestListener listener) {

		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token + "");
		params.put("time", System.currentTimeMillis() / 1000 + "");
		paramsdata.put("mobile", mobile + "");
		paramsdata.put("code_area", codearea + "");
		paramsdata.put("auto_reg", autoReg);
		paramsdata.put("code", code);
		HashmapToJson toJson = new HashmapToJson();
		params.put("context", toJson.toJson(paramsdata));
		return WebApi.startThreadRequest(WebApi.ACTION_PHONE_LOGIN, listener,
				params, appKey);
	}

	/**
	 * 用户注册
	 * 
	 * @param context
	 * @param appKey
	 * @param username
	 * @param password
	 * @param listener
	 * @return
	 */
	public ApiAsyncTask startRegister(Context context, String appKey,
			String username, String password, ApiRequestListener listener) {

		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token + "");
		params.put("time", System.currentTimeMillis() / 1000 + "");
		paramsdata.put("uname", username + "");
		paramsdata.put("upass", password + "");

		HashmapToJson toJson = new HashmapToJson();
		params.put("context", toJson.toJson(paramsdata));
		return WebApi.startThreadRequest(WebApi.ACTION_USERREGISTER, listener,
				params, appKey);

	}

	/**
	 * 手机号码注册
	 * 
	 * @param context
	 * @param appKey
	 * @param username
	 * @param password
	 * @param mobile
	 * @param code
	 * @param code_area
	 * @param listener
	 * @return
	 */
	public ApiAsyncTask startMobileRegister(Context context, String appKey,
			String username, String password, String mobile, String code,
			String code_area, ApiRequestListener listener) {

		HashMap<String, Object> params = new HashMap<String, Object>();
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
		return WebApi.startThreadRequest(WebApi.ACTION_PHONE_REGISTER,
				listener, params, appKey);

	}

	/**
	 * 用户自动登录
	 * 
	 * @param context
	 * @param appkey
	 * @param logintoken
	 * @param listener
	 * @return
	 */
	public ApiAsyncTask starlAutoLogin(Context context, String appkey,
			String logintoken, ApiRequestListener listener) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token + "");
		params.put("time", System.currentTimeMillis() / 1000 + "");
		paramsdata.put("login_token", logintoken + "");

		HashmapToJson toJson = new HashmapToJson();
		params.put("context", toJson.toJson(paramsdata));
		return WebApi.startThreadRequest(WebApi.ACTION_AUTOLOGIN, listener,
				params, appkey);
	}

	/**
	 * 用户登录
	 * 
	 * @param context
	 * @param appkey
	 * @param username
	 * @param pwd
	 * @param listener
	 * @return
	 */
	public ApiAsyncTask starusreLogin(Context context, String appkey,
			String username, String pwd, ApiRequestListener listener) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token + "");
		params.put("time", System.currentTimeMillis() / 1000 + "");
		paramsdata.put("uname", username + "");

		paramsdata.put("upass", pwd + "");

		HashmapToJson toJson = new HashmapToJson();
		params.put("context", toJson.toJson(paramsdata));
		return WebApi.startThreadRequest(WebApi.ACTION_USRRLOGIN, listener,
				params, appkey);
	}

	/**
	 * 修改用户账号
	 */
	public ApiAsyncTask startSetAccount(Context context, String appkey, String account, String password, String confirm_password, ApiRequestListener listener) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token + "");
		params.put("time", System.currentTimeMillis() / 1000 + "");
		params.put("uname", account + "");
		params.put("upass", password + "");
		params.put("reupass", confirm_password + "");
//		paramsdata.put("uname", account + "");
//		paramsdata.put("upass", password + "");
//		paramsdata.put("reupass", confirm_password + "");


		HashmapToJson toJson = new HashmapToJson();
		params.put("context", "");
		return WebApi.startThreadRequest(WebApi.ACTION_SET_ACCOUNT, listener,
				params, appkey);
	}



	/**
	 * 游客登录
	 * 
	 * @param context
	 * @param appkey
	 * @param username
	 * @param pwd
	 * @param listener
	 * @return
	 */
	public ApiAsyncTask starguestLogin(Context context, String appkey,
			ApiRequestListener listener) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token + "");
		params.put("time", System.currentTimeMillis() / 1000 + "");

		HashmapToJson toJson = new HashmapToJson();
		params.put("context", "");
		return WebApi.startThreadRequest(WebApi.ACTION_GUEST, listener, params,
				appkey);
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
	public ApiAsyncTask starCreate(Context context, String appkey,
			String opendid, String cporderid, String ordername, String amount,
			String roleid, String rolename, String level, String gender,
			String serverno, String servername,String balance, String power, String viplevel,
			String ext, ApiRequestListener listener) {
		HashMap<String, Object> params = new HashMap<String, Object>();
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

		return WebApi.startThreadRequest(WebApi.ACTION_CREATE, listener,
				params, appkey);
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
	public ApiAsyncTask starRole(Context context, String appkey,
			String opendid, String type, String roleid, String rolename,
			String level, String gender, String serverno, String servername,String balance,
			String power, String viplevel, String ext,
			ApiRequestListener listener) {
		HashMap<String, Object> params = new HashMap<String, Object>();
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

		return WebApi.startThreadRequest(WebApi.ACTION_REPORT, listener,
				params, appkey);
	}

	/**
	 * 切换账号
	 * 
	 * @param context
	 * @param appkey
	 * @param listener
	 * @return
	 */
	public ApiAsyncTask starguserLoginout(Context context, String appkey,
			ApiRequestListener listener) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token + "");
		params.put("time", System.currentTimeMillis() / 1000 + "");

		HashmapToJson toJson = new HashmapToJson();
		params.put("context", "");
		return WebApi.startThreadRequest(WebApi.ACTION_LOGINOUT, listener,
				params, appkey);
	}
	public ApiAsyncTask starbug(Context context, String appkey,String openid,
			String content,ApiRequestListener listener) {
		if (deviceInfo == null) {
			deviceInfo = new DeviceInfo(context);
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token + "");
		params.put("time", System.currentTimeMillis() / 1000 + "");
		paramsdata.put("openid", openid + "");
		paramsdata.put("network", deviceInfo.getNetwork() + "");
		paramsdata.put("content", content + "");
		paramsdata.put("error_level",   "1");
		HashmapToJson toJson = new HashmapToJson();
		params.put("context", toJson.toJson(paramsdata));
		return WebApi.startThreadRequest(WebApi.ACTION_BUG, listener,
				params, appkey);
	}

	/**
	 * 在线上报
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
	public ApiAsyncTask starOnline(Context context, String appkey,
			String opendid, String type, String roleid, String rolename,String seconds,
			String level, String gender, String serverno, String servername,String balance,
			String power, String viplevel, String ext,
			ApiRequestListener listener) {
		HashMap<String, Object> params = new HashMap<String, Object>();
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

		return WebApi.startThreadRequest(WebApi.ACTION_ONLINE, listener,
				params, appkey);
	}

	public ApiAsyncTask getWebSocketToken(Context context, String access_token, String appKey, ApiRequestListener listener) {

		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", access_token);
		params.put("time", System.currentTimeMillis() / 1000 + "");
		paramsdata.put("ext_data", "");
		HashmapToJson toJson = new HashmapToJson();
		params.put("context", toJson.toJson(paramsdata));
		return WebApi.startThreadRequest(WebApi.ACTION_GETTOKEN, listener, params,
				appKey);
	}

	public ApiAsyncTask getFloatState(Context context, String jm_customer_token, String appKey, ApiRequestListener listener) {

		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token+"");
		params.put("jm_customer_token", jm_customer_token);
		params.put("time", System.currentTimeMillis() / 1000 + "");
		paramsdata.put("ext_data", "");
		HashmapToJson toJson = new HashmapToJson();
		params.put("context", toJson.toJson(paramsdata));
		return WebApi.startThreadRequest(WebApi.ACTION_FLOATSTATE, listener, params, appKey);
	}

	public ApiAsyncTask clearNotice(Context context, String jm_customer_token, String appKey, ApiRequestListener listener) {

		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token+"");
		params.put("jm_customer_token", jm_customer_token);
		params.put("time", System.currentTimeMillis() / 1000 + "");
		paramsdata.put("ext_data", "");
		HashmapToJson toJson = new HashmapToJson();
		params.put("context", toJson.toJson(paramsdata));
		return WebApi.startThreadRequest(WebApi.ACTION_CLEANNOTICE, listener, params, appKey);
	}

	public ApiAsyncTask startOneKeylogin(String oneKeyToken,String appkey, ApiRequestListener listener) {

		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> paramsdata = new HashMap<String, Object>();
		params.put("access_token", AppConfig.Token + "");
		params.put("time", System.currentTimeMillis() / 1000 + "");
		paramsdata.put("login_token", oneKeyToken + "");

		HashmapToJson toJson = new HashmapToJson();
		params.put("context", toJson.toJson(paramsdata));
		return WebApi.startThreadRequest(WebApi.ACTION_ONEKEYLOGIN, listener, params, appkey);
	}
}
