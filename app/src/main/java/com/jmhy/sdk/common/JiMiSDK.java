package com.jmhy.sdk.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bun.miitmdid.core.JLibrary;
import com.jmhy.sdk.activity.ForceActivity;
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
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.sdk.Logindata;
import com.jmhy.sdk.sdk.Loginout;
import com.jmhy.sdk.sdk.PayDataRequest;
import com.jmhy.sdk.sdk.RoleinfoRequest;
import com.jmhy.sdk.sdk.StatisticsSDK;
import com.jmhy.sdk.utils.ActivityStackManager;
import com.jmhy.sdk.utils.DealCrash;
import com.jmhy.sdk.utils.DeviceInfo;
import com.jmhy.sdk.utils.FloatUtils;
import com.jmhy.sdk.utils.MiitHelper;
import com.jmhy.sdk.utils.Seference;
import com.jmhy.sdk.utils.StatisticsSDKUtils;
import com.jmhy.sdk.utils.Utils;
import com.jmhy.sdk.view.Exitdialog;
import com.jmhy.sdk.view.Exitdialog.ExitDialogListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private static ApiAsyncTask RoleinfoTask;
	public static Context mContext;
	private static ExitListener mExitlistener;


	static{
		initMarkMap();
	}

    public static ActivityStackManager stackManager = new ActivityStackManager();

    private static StatisticsSDK statisticsSDK = new StatisticsSDKUtils();

	public static Handler handler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case 1:
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


		final Activity activity = (Activity)context;
		if(VERSION.SDK_INT < VERSION_CODES.Q){
			List<String> permission = new ArrayList<>();
			permission.add(Manifest.permission.READ_PHONE_STATE);
			permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
		}else{
			init(activity, appid, appkey, listener);
		}
	}

	public static String mOaid = "";


	private static MiitHelper.AppIdsUpdater appIdsUpdater = new MiitHelper.AppIdsUpdater() {
		@Override
		public void OnIdsAvalid(String ids) {
			Log.e(TAG,"++++++oaid: " + ids);
			mOaid = ids;
			//存储oaid
			Seference seference = new Seference(mContext);
			seference.savePreferenceData("game", "oaid_unique", mOaid);


		}
	};

	private static void init(final Context context, int appid, String appkey, final InitListener listener) {
		Log.i(TAG, "---init start---");
		JiMiSDK.mContext = context;
		//DealCrash.getInstance().init(JiMiSDK.context);
		//setStrictMode();

		//获取OAID等设备标识符
			Seference seference = new Seference(mContext);
			mOaid = seference.getPreferenceData("game", "oaid_unique");
			if (TextUtils.isEmpty(mOaid)){
				Log.i(TAG, "mOaid---null");

				try {
					JLibrary.InitEntry(context.getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
				}

				MiitHelper miitHelper = new MiitHelper(appIdsUpdater);
				miitHelper.getDeviceIds(context.getApplicationContext());

			}else{
				Log.i(TAG, "mOaid---" + mOaid);

			}


		try {
			SdkParams params = Utils.getSdkParams(context);

			//debug模式
			if (params.isDebugMode == null){
				params.isDebugMode = "false";
			}
			AppConfig.isDebugMode = params.isDebugMode.equals("true") ? true : false;
			doMark("init");
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
			Log.i(TAG, "isDebugMode = " + AppConfig.isDebugMode);
			new InitData(context, AppConfig.agent, new InitListener() {
				@Override
				public void Success(String msg) {
					Log.i(TAG, "初始化接口 version : " + AppConfig.sdk_version + ", skin : " + AppConfig.skin);
					Log.i(TAG, "init success");
					init = true;
					listener.Success(msg);

					statisticsSDK.initInterface(context, AppConfig.sdkList);
				}

				@Override
				public void fail(String msg) {
					Log.i(TAG, "init fail : " + msg);
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
		Log.i(TAG, "---login---");
		if(!init){
			Log.w(TAG, "sdk not initialized yet");
			return;
		}
		doMark("login");

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
		doMark("payment");
		String extData = "金额="+payInfo.getAmount()+",\n剩余元宝="+ payInfo.getBalance()+",\nCP订单号="+ payInfo.getCporderid()+",\n额外参数="+ payInfo.getExt()+",\ngender="+ payInfo.getGender()+",\n角色等级="+ payInfo.getLevel()+",\n商品订单名="+ payInfo.getOrdername()+",\n服务器ID="+ payInfo.getServerno()+",\n服务器名称="+payInfo.getZoneName()+
				"\n角色战力="+ payInfo.getPower()+",\nVIP等级=" +payInfo.getViplevel()+",\n角色ID="+payInfo.getRoleid() +",\n角色名="+payInfo.getRolename();
		Utils.showMsgToast(activity, extData);
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
		doMark("onCreate");
		Log.i(TAG, "activity = " + activity.toString());
		JiMiSDK.mContext = (Context)activity;
		stackManager.pushActivity(activity);
		statisticsSDK.onCreate(activity);
	}

	public static void onStop(Activity activity) {
		Log.i(TAG, "onStop");
		doMark("onStop");
		statisticsSDK.onStop(activity);
	}

	public static void onDestroy(Activity activity) {
		Log.i(TAG, "onDestroy");
		stackManager.removeActivity(activity);
		statisticsSDK.onDestroy(activity);
		PushService.closeSchedule();

	}

	public static void onResume(Activity activity) {
		Log.i(TAG, "onResume");
		doMark("onResume");
		statisticsSDK.onResume(activity);
		PushService.onResume();
	}

	public static void onPause(Activity activity) {
		Log.i(TAG, "onPause");
		doMark("onPause");
		statisticsSDK.onPause(activity);
		PushService.onPause();
	}

	public static void showFloat(){
		doMark("showFloat");

		if(!TextUtils.equals(AppConfig.is_sdk_float_on, "1")){
			return;
		}
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showFloatDelayed();
            }
        },1000);
	}

	private static void showFloatDelayed(){
/*        final Activity activity = stackManager.getTopActivity();
        if(activity == null){
            Log.i(TAG, "showFloat top activity is null");
            return;
        }
		Log.i(TAG, "showFloat top activity  == " + activity.toString());*/

		PermissionActivity.requestFloatPermission(mContext, new PermissionResultListener() {
            @Override
            public void onPermissionResult(boolean grant) {
				Log.i(TAG, "showFloat grant = " + grant);
                if(grant){
					FloatUtils.showFloat((Activity)mContext);
                }else{
                    permissionTip((Activity)mContext, "jm_permission_tip_float");
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
		doMark("setUserListener");
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
		JiMiSDK.mContext = context;
		doMark("setExtData");
		String extData = "type="+type+",\nroleid="+ roleid+",\nrolename="+ rolename+",\nlevel="+ level+",\ngender="+ gender+",\nserverno="+ serverno+",\nzoneName="+zoneName+
				",\nbalance="+ balance+",\npower="+ power+",\nviplevel=" +viplevel+",\next="+
				ext+",\nroleCTime="+roleCTime +",\nroleLevelMTime="+roleLevelMTime;
		switch (type) {
			case "1":
				extData = "调用创角接口\n" + extData;
				break;
			case "2":
				extData = "调用入服接口\n" + extData;
				break;
			case "3":
				extData = "调用升级接口\n" + extData;
				break;
			default:
				break;
		}
		Utils.showMsgToast(context, extData);
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
		mExitlistener = exitlistener;
		Log.i(TAG, "---exit--");
		doMark("exit");
		if (AppConfig.isDebugMode) {
			markTip(activity);
		} else {

			Exitdialog exitdialog = new Exitdialog(activity, AppConfig.resourceId(activity,
					"jm_MyDialog", "style"), new Exitdialog.ExitDialogListener() {
				@Override
				public void onExit() {
					PushService.closeSchedule();
					Intent intentFour = new Intent(activity, PushService.class);
					activity.stopService(intentFour);

					FloatUtils.destroyFloat();
					WebApi.shutdown();
					statisticsSDK.onExit();
					exitlistener.ExitSuccess("success");
				}

				@Override
				public void onCancel() {
					exitlistener.fail("fail");
				}
			});
			// exitdialog.setCancelable(false);
			exitdialog.show();
		}
	}

	/**
	 * 切换账号接口
	 */
	public static void switchAccount(Context context){
		Log.i(TAG,"触发切换账号");
		doMark("switchAccount");
		FloatUtils.destroyFloat();
		if(userlistenerinfo == null) {
			return;
		}
	    Loginout.getInstatnce(context);
		AppConfig.isShow = false;
		AppConfig.ismobillg=false;
		AppConfig.isswitch = false;
	}

	public static void clearAllActivity(){
		stackManager.clearAllActivity();
	}

	public static void forceLogout(final String message){
		if(mContext == null){
			return;
		}

		handler.post(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG,"forceLogout");

				Intent intent = new Intent(mContext, ForceActivity.class);
				intent.putExtra("message", message);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				mContext.startActivity(intent);
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

	public static void permissionTip(Activity activity, String tipRes){
		String tip = AppConfig.getString(activity, tipRes);
		String button = AppConfig.getString(activity, "jm_confirm");
		Dialog dialog = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT)
				.setMessage(tip)
				.setPositiveButton(button, null)
				.create();
		dialog.show();
	}

	private static void showToast(String content){
	    if(mContext == null){
	        return;
        }
		Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
	}

	public static String getUUID(){
		if(JmhyApi.get().getDeviceInfo() == null) {
			DeviceInfo deviceInfo = new DeviceInfo(mContext);
			return deviceInfo.getUuid();
		}
		return JmhyApi.get().getDeviceInfo().getUuid();
	}



	private static void initMarkMap() {
		AppConfig.markMap.put("init","调用  初始化: no, 主线程: null ");
		AppConfig.markMap.put("login","调用登录接口 : no, 主线程: null ");
		AppConfig.markMap.put("showFloat","调用显示浮点 : no, 主线程: null ");
		AppConfig.markMap.put("payment","调用充值接口 : no, 主线程: null ");
		AppConfig.markMap.put("setExtData","调用上报接口 : no, 主线程: null ");
		AppConfig.markMap.put("setUserListener","设置登出监听 : no, 主线程: null ");
		AppConfig.markMap.put("switchAccount","调用切换账号 : no, 主线程: null ");
		AppConfig.markMap.put("exit","调用退出接口 : no, 主线程: null ");
		AppConfig.markMap.put("onCreate","调用onCreate :  no");
		AppConfig.markMap.put("onResume","调用onResume :  no");
		AppConfig.markMap.put("onPause","调用onPause :  no");
		AppConfig.markMap.put("onStop","调用onStop :  no");

	}


	private static void doMark(String methodName) {

		if (AppConfig.isDebugMode){
			String value = AppConfig.markMap.get(methodName);
			value = value.replace("no", "yes");
			value = value.replace("null",Utils.isInMainThread()+ "");
			Log.e(TAG, value);
			AppConfig.markMap.put(methodName,value);
		}

	}

	public static void markTip(final Activity activity){
		StringBuilder sb = new StringBuilder();
		sb.append("接口及生命周期调用:\n");
		for (Map.Entry<String, String> entry : AppConfig.markMap.entrySet()) {
			sb.append(entry.getValue());
			sb.append("\n");
		}

		String button = AppConfig.getString(activity, "jm_confirm");
		Dialog dialog = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT)
				.setMessage(sb.toString())
				.setPositiveButton(button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						exit(activity,mExitlistener);
					}
				})
				.create();
		dialog.show();
		AppConfig.isDebugMode = false;
	}
}
