package com.jmhy.sdk.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.model.BaseResponse;


import android.text.TextUtils;
import android.util.Log;

/**
 * API 响应结果解析工厂类，所有的API响应结果解析需要在此完成。
 * 
 */
public class ApiResponseFactory {

	public static final boolean DEBUG = false;
	public static final String LOGTAG = "ApiResponseFactory";

	/**
	 * 处理response
	 * @param webApi
	 * @param response
	 * @return
	 */
	public static Object handleResponse(String webApi, HttpResponse response) {

		String data = inputStreamToString(HttpUtils
				.getInputStreamResponse(response));
		data = clearBom(data);

		Log.d("JiMiSDK", "response = " + data);

		try {
			BaseResponse baseResponse = JSONParse.parseBaseResponse(data);
			if(forceCode(baseResponse.code)){
				JiMiSDK.forceLogout(baseResponse.message);
				return null;
			}else if(!TextUtils.equals(baseResponse.code, BaseResponse.SUCCESS)){
				//JiMiSDK.showErrorToast(baseResponse.message);
				Log.e("JiMiSDK", "error = " + baseResponse.code + "," + baseResponse.message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Object result = data;

		try {
			Log.i("kk", "data"+data);
			if (webApi.equals(WebApi.ACTION_INIT)) {
//				result = JSONParse.parseInitMsg(data);
			}else if (webApi.equals(WebApi.ACTION_USERREGISTER)) {
//				result = JSONParse.parseuserRegister(data);
			}else if (webApi.equals(WebApi.ACTION_SMS)) {
				result = JSONParse.parseRequestSMS(data);
			}else if (webApi.equals(WebApi.ACTION_PHONE_LOGIN)) {
				result = JSONParse.parseMobilelogin(data);
			}else if (webApi.equals(WebApi.ACTION_PHONE_REGISTER)) {
//				result = JSONParse.parseuserRegister(data);
			}else if (webApi.equals(WebApi.ACTION_AUTOLOGIN)) {
				result = JSONParse.parseAutologin(data);
			}else if (webApi.equals(WebApi.ACTION_USRRLOGIN)) {
				result = JSONParse.parseAutologin(data);
			}else if(webApi.equals(WebApi.ACTION_GUEST)){
				result = JSONParse.parseGuestlogin(data);	
			}else if(webApi.equals(WebApi.ACTION_CREATE)){
				result = JSONParse.parseCreate(data);
			}else if(webApi.equals(WebApi.THIRD_PLATFORM_LOGIN)){
				result = JSONParse.parseThirdPlatformLogin(data);
			}else if(webApi.equals(WebApi.RECHARGE_NOTIFY)){
				result = JSONParse.parseRechargeNotify(data);
			}else if(webApi.equals(WebApi.ACTION_ONLINE)){
				result = JSONParse.parseOnlineNotify(data);
			}else if(webApi.equals(WebApi.ACTION_ONEKEYLOGIN)){
				result = JSONParse.parseOneKeylogin(data);
			}else if(webApi.equals(WebApi.ACTION_SET_ACCOUNT)){
				result = JSONParse.parseSetAccount(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	private static String inputStreamToString(InputStream in) {

		try {
			StringBuilder builder = new StringBuilder();
			BufferedReader bufferedReader2 = new BufferedReader(
					new InputStreamReader(in));

			for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2
					.readLine()) {
				builder.append(s);
			}

			return builder.toString();

		} catch (IOException e) {

			e.printStackTrace();

		}

		return "";
	}
	
	private static String clearBom(String data){
		if(data.startsWith("\ufeff")){
			return data.substring(1);
		}
		return data;
	}

	private static boolean forceCode(String code){
		return TextUtils.equals(code, BaseResponse.DEVICE_DENY_ACCESS) ||
				TextUtils.equals(code, BaseResponse.USER_DENY_ACCESS);
	}

	/*private static boolean forceUrl(String url){
		return TextUtils.equals(url, WebApi.ACTION_REPORT) ||
				TextUtils.equals(url, WebApi.ACTION_ONLINE) ||
				TextUtils.equals(url, WebApi.ACTION_CREATE);
	}*/
}
