package com.jmhy.sdk.sdk;

import com.jmhy.sdk.model.InitExt;
import com.jmhy.sdk.utils.DialogUtils;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.common.InitListener;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.InitMsg;
import com.jmhy.sdk.push.PushService;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class InitData {

	private InitListener listener;
	private Context context;
	//private ApiAsyncTask iniTask; // 初始化

	private String ver_id;
	private Seference seference;

	public InitData(Context context, String ver_id, InitListener listener) {
		this.context = context;

		this.ver_id = ver_id;
		this.listener = listener;
		seference = new Seference(context);
		handler.sendEmptyMessage(AppConfig.INIT);
	}

	public void Init() {
		initHttp();
	}

	/**
	 * http请求，初始化接口
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
//		iniTask =
				JmhyApi.get().startInit(context, AppConfig.appId,
				AppConfig.appKey, ver_id, ext, new ApiRequestListener() {
					@Override
					public void onSuccess(Object obj) {
						Log.i("jimi","参数"+obj);
						if (obj != null) {
							try {
								InitMsg result = (InitMsg) obj;
								if (result.getCode().equals("0")) {
									sendData(AppConfig.INIT_SUCCESS, obj, handler);
								} else {
									sendData(AppConfig.FLAG_FAIL,
											result.getMessage(), handler);
								}
							} catch (Exception e) {
								e.printStackTrace();
								Log.i("jimi", "初始化回调信息异常" + e);
								sendData(AppConfig.FLAG_FAIL,"初始化回调信息异常"+obj.toString(), handler);
							}
						} else {
							sendData(AppConfig.FLAG_FAIL, AppConfig.getString(context, "http_rror_msg"),
									handler);
						}
					}

					@Override
					public void onError(int statusCode) {
						// TODO Auto-generated method stub
						sendData(AppConfig.FLAG_FAIL, AppConfig.getString(context, "http_rror_msg"),
								handler);
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

	private Handler handler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case AppConfig.INIT:
				Init();
				break;

			case AppConfig.INIT_SUCCESS:
				InitMsg init = (InitMsg) msg.obj;
				 Log.i("kk",init.toString());
				setInit(init);
				break;
			case AppConfig.FLAG_FAIL:
				Activity activity = (Activity)context;
				String message = (String) msg.obj;
				DialogUtils.showTip(activity, message);

				listener.fail("fail");
				break;
			case AppConfig.FLAG_PUSH:
				// 初始化浮点
				
				//FloatUtils.intview(context);
				break;
			}
		}
	};

	/**
	 * 赋值初始化信息
	 */
	public void setInit(InitMsg result) {

		try {
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
			if (result.getMoblie_direct_login()!=null&&!result.getMoblie_direct_login().isEmpty()&&!result.getMoblie_direct_login().equals("")){

				JSONObject jsonObject= null;
				try {
					jsonObject = new JSONObject(result.getMoblie_direct_login());
					AppConfig.oneKeyLogin_SecretKey = jsonObject.getString("clientSecret");
					Log.i("测试jimsdk","一键登录key:"+AppConfig.oneKeyLogin_SecretKey);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		    seference.savePreferenceData("game", "token", result.getAccess_token());
		    seference.savePreferenceData("game", "onlintiem", result.getOnlinereportinterval());
		    seference.savePreferenceData("game", "userfloat",result.getUser_float());
		    seference.savePreferenceData("game", "servicefloat", result.getService_float());
		    seference.savePreferenceData("game", "scripturl", Utils.toBase64url(result.getAddglobalscripturl()));

			if (result.getSdk_float().equals("1")) {
				handler.sendEmptyMessage(AppConfig.FLAG_PUSH);
			}
			if(!result.getOnlinereportinterval().equals("0")){
			   AppConfig.ONLIE_TIEM = Long.parseLong(result.getOnlinereportinterval());
			   Intent pushIntent = new Intent(context, PushService.class);
				context.startService(pushIntent);
				//Log.i("kk", "间隔时间"+ AppConfig.ONLIE_TIEM);
			}
			//SeferenceGame.getInstance(context).savePreferenceData("gameuser", "time", System.currentTimeMillis()+"");
			String url= Utils.toBase64url(result.getShowurlafterint());
			if(!TextUtils.isEmpty(url)){
				Intent intent = new Intent();
				 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				  Intent.FLAG_ACTIVITY_SINGLE_TOP); intent.putExtra("url", url);
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
