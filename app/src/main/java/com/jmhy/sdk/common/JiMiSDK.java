package com.jmhy.sdk.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jmhy.sdk.activity.ForceActivity;
import com.jmhy.sdk.activity.JmLoginActivity;
import com.jmhy.sdk.activity.PermissionActivity;
import com.jmhy.sdk.activity.PermissionActivity.PermissionResultListener;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.model.PayData;
import com.jmhy.sdk.model.PaymentInfo;
import com.jmhy.sdk.model.SdkParams;
import com.jmhy.sdk.push.PushService;
import com.jmhy.sdk.sdk.InitData;
import com.jmhy.sdk.sdk.Logindata;
import com.jmhy.sdk.sdk.Loginout;
import com.jmhy.sdk.sdk.PayDataRequest;
import com.jmhy.sdk.sdk.RoleinfoRequest;
import com.jmhy.sdk.sdk.StatisticsSDK;
import com.jmhy.sdk.utils.ActivityStackManager;
import com.jmhy.sdk.utils.DeviceInfo;
import com.jmhy.sdk.utils.FloatUtils;
import com.jmhy.sdk.utils.StatisticsSDKUtils;
import com.jmhy.sdk.utils.Utils;
import com.jmhy.sdk.view.Exitdialog;
import com.jmhy.sdk.view.Exitdialog.Exitdialoglistener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class JiMiSDK {
	private final static String TAG = JiMiSDK.class.getSimpleName();
	
	public static boolean isShow = true;
	public static boolean iswelcom = true;
	public static Timer timer;

	public final static String payChannel = "jm";

	private static boolean init;

	public static ApiListenerInfo apiListenerInfo;
	public static UserApiListenerInfo userlistenerinfo;
	private static Exitdialog exitdialog;
	
    private static ApiAsyncTask RoleinfoTask;
	public static Context context;

    public static ActivityStackManager stackManager = new ActivityStackManager();

    private static StatisticsSDK statisticsSDK = new StatisticsSDKUtils();

	public static Handler handler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case 1:
                    JmLoginActivity.closeActivity();
					apiListenerInfo.onSuccess(msg.obj);
					break;
				case 2:
                    apiListenerInfo.onSuccess(msg.obj);
					break;
				case 3:
					try {
						userlistenerinfo.onLogout(msg.obj);
					} catch (Exception e) {
						// TODO: handle exception
					}
					break;
				}
                
                	
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	public static StatisticsSDK getStatisticsSDK() {
		return statisticsSDK;
	}

	/**
	 * 初始化接口
	 */
	public static void initInterface(final Context context, final int appid, final String appkey, final InitListener listener) {
		if(init){
			Log.w(TAG, "sdk already init");
			return;
		}

        Log.i(TAG, "version : " + AppConfig.sdk_version);

		final Activity activity = (Activity)context;
		List<String> permission = new ArrayList<>();
		permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		permission.add(Manifest.permission.READ_PHONE_STATE);
		PermissionActivity.requestPermission(context, permission, new PermissionResultListener() {
			@Override
			public void onPermissionResult(boolean grant) {
				Log.i(TAG, "onPermissionResult " + grant);
				if(!grant) {
					permissionTip(activity, "jm_permission_tip_init");

					listener.fail("fail");
				}else{
					init(activity, appid, appkey, listener);
				}
			}
		});
	}

	private static void init(final Context context, int appid, String appkey, final InitListener listener) {
		JiMiSDK.context = context.getApplicationContext();

		//DealCrash.getInstance().init(JiMiSDK.context);
		//setStrictMode();

		try {
			SdkParams params = Utils.getSdkParams(context);
			WebApi.init(params.host);

			AppConfig.agent = params.agent;
			AppConfig.version = params.version;

			if(TextUtils.isEmpty(params.appid)) {
				AppConfig.appId = appid;
			}else{
				AppConfig.appId = Integer.valueOf(params.appid);
			}
			if(TextUtils.isEmpty(params.appkey)) {
				AppConfig.appKey = appkey;
			}else{
				AppConfig.appKey = params.appkey;
			}
			AppConfig.supportEnglish = params.supportEnglish;

			Log.i(TAG, "host = " + params.host);
			Log.i(TAG, "appId = " + AppConfig.appId);
			Log.i(TAG, "appKey = " + AppConfig.appKey);
			Log.i(TAG, "agent = " + AppConfig.agent);

			new InitData(context, AppConfig.agent, new InitListener() {
				@Override
				public void Success(String msg) {
					init = true;
					listener.Success(msg);

					statisticsSDK.initInterface(context, AppConfig.sdkList);
				}

				@Override
				public void fail(String msg) {
					listener.fail(msg);
				}
			});// point浮点的显示

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 登录接口
	 * 
	 * @param context
	 * @param listener
	 */
	public static void login(Activity context, int appid, String appkey, final ApiListenerInfo listener) {
		if(!init){
			Log.w(TAG, "sdk not initialized yet");
			return;
		}
		try {
			apiListenerInfo = listener;
			Logindata.selectLogin(context);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 充值接口
	 * 
	 * @param activity
	 * @param payInfo
	 * @param listener
	 */

	public static void payment(Activity activity, PaymentInfo payInfo,
			ApiListenerInfo listener) {
		try {
			apiListenerInfo = listener;
			PayDataRequest.getInstatnce(activity, payInfo, listener);

			JiMiSDK.getStatisticsSDK().onCompleteOrder(payInfo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void payment(Activity activity, PayData payData,
							   ApiListenerInfo listener) {
		apiListenerInfo = listener;
		String url = Utils.toBase64url(payData.getOcontent());
		PayDataRequest.turnToIntent(activity, url);
	}

	public static void onCreate(Activity activity) {
		Log.i(TAG, "onCreate");
		stackManager.pushActivity(activity);
		statisticsSDK.onCreate(activity);
	}

	public static void onStop(Activity activity) {
		Log.i(TAG, "onStop");
		statisticsSDK.onStop(activity);
	}

	public static void onDestroy(Activity activity) {
		Log.i(TAG, "onDestroy");
		stackManager.removeActivity(activity);
		statisticsSDK.onDestroy(activity);
	}

	public static void onResume(Activity activity) {
		Log.i(TAG, "onResume");
		statisticsSDK.onResume(activity);
		PushService.onResume();
	}

	public static void onPause(Activity activity) {
		Log.i(TAG, "onPause");
		statisticsSDK.onPause(activity);
		PushService.onPause();
	}

	public static void showFloat(){
		if(!TextUtils.equals(AppConfig.is_sdk_float_on, "1")){
			return;
		}
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showFloatDelayed();
            }
        }, 300);
	}

	private static void showFloatDelayed(){
        final Activity activity = stackManager.getTopActivity();
        if(activity == null){
            Log.i(TAG, "showFloat top activity is null");
            return;
        }
        PermissionActivity.requestFloatPermission(activity, new PermissionResultListener() {
            @Override
            public void onPermissionResult(boolean grant) {
				Log.i(TAG, "showFloat grant = " + grant);
                if(grant){
                    FloatUtils.showFloat(activity);
                }else{
                    permissionTip(activity, "jm_permission_tip_float");
                }
            }
        });
    }

	public static void hideFloat(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                FloatUtils.destroyFloat();
            }
        });
	}

	public static void onRestart(Activity activity) {
		Log.i(TAG, "onRestart");
		statisticsSDK.onPause(activity);
	}

	public static void onNewIntent(final Intent intent) {
		Log.i(TAG, "onNewIntent");
		statisticsSDK.onNewIntent(intent);
	}

	public static void onActivityResult(Activity activity, int requestCode,
			int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		statisticsSDK.onActivityResult(activity, requestCode, resultCode, data);
	}

	/*
	 * 切换账号回调
	 */
	public static void setUserListener(UserApiListenerInfo listener) {
		userlistenerinfo = listener;
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
	public static void setExtData(Context context, String type,String roleid,
			String rolename,String level,String gender,String serverno,String zoneName,
			String balance,String power,String viplevel,String roleCTime, String roleLevelMTime,String ext) {
		Log.i(TAG,"type="+type+",roleid="+ roleid+",rolename="+ rolename+",level="+
				level+",gender="+ gender+",serverno="+ serverno+",zoneName="+zoneName+
				",balance="+ balance+",power="+ power+",viplevel=" +viplevel+",ext="+
				ext+",roleCTime="+roleCTime +",roleLevelMTime="+roleLevelMTime);
		if(TextUtils.isEmpty(roleid) || "0".equals(roleid)){
			return;
		}
		RoleinfoRequest.getInstatnce(context, type, roleid, rolename,
				level, gender, serverno,zoneName, balance, power, viplevel, ext);
		statisticsSDK.setExtData(context, type, roleid, rolename, level, gender, serverno, zoneName, balance, power, viplevel, roleCTime, roleLevelMTime, ext);
	}


	/**
	 * 退出接口
	 */
	public static void exit(final Activity activity,
			final ExitListener exitlistener) {

		Log.i(TAG, "---exit--");
	
		exitdialog = new Exitdialog(activity, AppConfig.resourceId(activity,
				"jm_MyDialog", "style"), new Exitdialoglistener() {

			public void onClick(View v) {
				if (v.getId() == AppConfig.resourceId(activity, "dialog_exit",
						"id")) {
					PushService.closeSchedule();
					Intent intentFour = new Intent(activity, PushService.class);
					activity.stopService(intentFour);

					FloatUtils.destroyFloat();
					WebApi.shutdown();
					statisticsSDK.onExit();
					exitlistener.ExitSuccess("success");

					exitdialog.dismiss();
				} else if (v.getId() == AppConfig.resourceId(activity,
						"dialog_cancel", "id")) {

					exitlistener.fail("fail");
					exitdialog.dismiss();
				}
			}
		});
		// exitdialog.setCancelable(false);
		exitdialog.show();
	}

	/**
	 * 切换账号接口
	 */
	public static void switchAccount(Context context){
		Log.i(TAG,"触发切换账号");
		if(userlistenerinfo == null) {
			return;
		}
	    Loginout.getInstatnce(context);
		AppConfig.isShow = false;
		AppConfig.ismobillg=false;
		AppConfig.isswitch = false;
		FloatUtils.destroyFloat();
	}

	public static void clearAllActivity(){
		stackManager.clearAllActivity();
	}

	public static void forceLogout(final String message){
		if(context == null){
			return;
		}

		handler.post(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG,"forceLogout");

				Intent intent = new Intent(context, ForceActivity.class);
				intent.putExtra("message", message);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				context.startActivity(intent);
			}
		});
	}

	public static void showErrorToast(final String message){
		handler.post(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG,"showErrorToast");
				showToast(message);
			}
		});
	}

	private static void permissionTip(Activity activity, String tipRes){
		String tip = AppConfig.getString(activity, tipRes);
		String button = AppConfig.getString(activity, "jm_confirm");
		Dialog dialog = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT)
				.setMessage(tip)
				.setPositiveButton(button, null)
				.create();
		dialog.show();
	}

	private static void showToast(String content){
	    if(context == null){
	        return;
        }
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	private static DeviceInfo deviceInfo;
	public static String getUUID(){
		if(deviceInfo == null) {
			deviceInfo = new DeviceInfo(context);
		}
        return deviceInfo.getUuid();
    }
}
