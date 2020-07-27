package com.jmhy.sdk.config;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.http.JMApiTask;

public class WebApi {

	private static final String LOGTAG = "WebApi";

	public static String BASE_HOST;// = "https://apisdk.5tc5.com/v1"
	public static String HOST;// = "https://apisdk.5tc5.com/v1"
//	public static String WEBSOCKET_HOST = "http://swim.t52f.top:9501";
//	public static String WEBSOCKET_HOST = "https://swim.t52f.top";
	public static String WEBSOCKET_HOST = "https://jmkf.jmhy.com";
	//private static final String HOST = "https://test.172jm.com/v1";//测试用
	//private final String HOST = "https://apisdk.wfg3.com/v1";

	public static Map<String, String> HttpTypeMap = new HashMap<String, String>();
	/**
	 * 接口名称配置信息
	 */

	public static void init(String host){
		if(TextUtils.isEmpty(host)){
			host = "https://apisdk.5tc5.com";
		}
		BASE_HOST = host;
		HOST = host + "/v1";
		// 初始化接口
		ACTION_INIT = HOST + "/app/init";
		// 请求短信
		ACTION_SMS = HOST + "/sms/send";
		//手机登录接口
		ACTION_PHONE_LOGIN = HOST+"/user/phone_login";
		//账号注册
		ACTION_USERREGISTER = HOST+"/user/account_register";
		//账号登录
		ACTION_USRRLOGIN = HOST+"/user/account_login";
		//自动登录
		ACTION_AUTOLOGIN = HOST+"/user/auto_login";
		//手机注册
		ACTION_PHONE_REGISTER =HOST+"/user/phone_register";
		//游客账号
		ACTION_GUEST =HOST+"/user/guest";
		//订单创建
		ACTION_CREATE =HOST+ "/od/create";
		//角色上报接口
		ACTION_REPORT=HOST+"/role/report";
		//用户登出
		ACTION_LOGINOUT = HOST+"/user/loginout";
		//异常上报
		ACTION_BUG =HOST+ "/report/bug";
		//在线上报
		ACTION_ONLINE =HOST+"/role/online";
		//第三方登录
		THIRD_PLATFORM_LOGIN = HOST + "/user/channel_login";
		//第三方支付回调
		RECHARGE_NOTIFY = HOST + "/od/ysdkNotify";
		//第三方支付签名或者获取其他信息
		RECHARGE_GETINFO = HOST + "/channel/get_info";
		//获取websocket的token接口
		ACTION_GETTOKEN = WEBSOCKET_HOST + "/client/login";
        //获取悬浮窗红点状态
        ACTION_FLOATSTATE =WEBSOCKET_HOST+"/client/applicationNotice";
        //清除悬浮窗红点状态
		ACTION_CLEANNOTICE =WEBSOCKET_HOST+"/client/clearNotice";

		/*
		 * 接口请求方式配置
		 */
		HttpTypeMap.put(ACTION_INIT, "post");
		HttpTypeMap.put(ACTION_SMS, "post");
		HttpTypeMap.put(ACTION_PHONE_LOGIN, "post");
		HttpTypeMap.put(ACTION_USERREGISTER, "post");
		HttpTypeMap.put(ACTION_USRRLOGIN, "post");
		HttpTypeMap.put(ACTION_AUTOLOGIN, "post");
		HttpTypeMap.put(ACTION_PHONE_REGISTER, "post");
		HttpTypeMap.put(ACTION_GUEST, "post");
		HttpTypeMap.put(ACTION_LOGINOUT, "post");
		HttpTypeMap.put(ACTION_CREATE, "post");
		HttpTypeMap.put(ACTION_REPORT, "post");
		HttpTypeMap.put(ACTION_BUG, "post");
		HttpTypeMap.put(ACTION_ONLINE, "post");
		HttpTypeMap.put(THIRD_PLATFORM_LOGIN, "post");
		HttpTypeMap.put(RECHARGE_NOTIFY, "post");
		HttpTypeMap.put(RECHARGE_GETINFO, "post");
		HttpTypeMap.put(ACTION_GETTOKEN, "post");
		HttpTypeMap.put(ACTION_FLOATSTATE, "post");
		HttpTypeMap.put(ACTION_CLEANNOTICE, "post");
	}

	// 初始化接口
   public static String ACTION_INIT;
   // 请求短信
   public static String ACTION_SMS;
	//手机登录接口
   public static String ACTION_PHONE_LOGIN;
   //账号注册
   public static String ACTION_USERREGISTER;
   //账号登录
   public static String ACTION_USRRLOGIN;
   //自动登录
   public static String ACTION_AUTOLOGIN;
   //手机注册
   public static String ACTION_PHONE_REGISTER;
   //游客账号
   public static String ACTION_GUEST;
   //订单创建
   public static String ACTION_CREATE;
   //角色上报接口
   public static String ACTION_REPORT;
   //用户登出
   public static String ACTION_LOGINOUT;
   //异常上报
   public static String ACTION_BUG;
	//在线上报
   public static String ACTION_ONLINE;
	//第三方登录
	public static String THIRD_PLATFORM_LOGIN;
	//第三方支付回调
	public static String RECHARGE_NOTIFY;
	//第三方支付签名或者获取其他信息
	public static String RECHARGE_GETINFO;
	//获取websocket的token接口
	public static String ACTION_GETTOKEN;
	//轮询接口，获取是否有新消息，展示悬浮窗小红点
	public static String ACTION_FLOATSTATE;
	//点开客服浮标通知服务端接口,轮询不在获得显示浮标状态
	public static String ACTION_CLEANNOTICE;

	/**
	 * 后台启动http连接，使用Thread实现
	 *
	 * @param webApi
	 * @param listener
	 * @param params
	 * @param appKey
	 */
	public static ApiAsyncTask startThreadRequest(String webApi,
			ApiRequestListener listener, HashMap<String, Object> params,
			String appKey) {

		ApiAsyncTask task = new JMApiTask(webApi, listener, params, appKey);
		threadPool.execute(task);

		return task;
	}

	private static ExecutorService threadPool;

	static {
		threadPool = Executors.newCachedThreadPool();
	}

	public static void shutdown(){
		threadPool.shutdown();
	}
}
