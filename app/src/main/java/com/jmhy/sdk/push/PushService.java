package com.jmhy.sdk.push;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.OnlineMessage;
import com.jmhy.sdk.model.PayData;
import com.jmhy.sdk.model.PaymentInfo;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.sdk.PayDataRequest;
import com.jmhy.sdk.statistics.JrttStatistics;
import com.jmhy.sdk.utils.FloatUtils;
import com.jmhy.sdk.utils.SeferenceGame;
import com.jmhy.sdk.utils.Utils;
import com.jmhy.sdk.view.FloatView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 推送服务 使用轮询实现 每隔一段时间发起pull请求， 返回数据后跟本地id匹配，如果是没有接收过的，加载详细数据，显示通知栏消息
 *
 * @author
 *
 */

public class PushService extends Service {
	private final static String TAG = PushService.class.getSimpleName();

	public static long startTime = 1000;// 3分钟之后请求一次
	private static Timer timer;
	private Task task;
	private Handler handler;
	private ApiAsyncTask roletask;
	private Context mContext;
	private static long lastTime;
	private static long onLineTime;
	private static boolean pause;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//Log.i("kk", "onCreate = "+ AppConfig.ONLIE_TIEM);
		long period = AppConfig.ONLIE_TIEM * 1000;

		if (timer == null) {
			timer = new Timer();
		}
		if (task == null) {
			task = new Task();
		}
		handler = new Handler();
		lastTime = 0;
		onLineTime = 0;
		timer.schedule(task, startTime, period);

		Log.i(TAG, "定时任务初始化成功...");
		Log.i(TAG, "时间间隔..." + period);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public static void onResume(){
		Log.i(TAG, "onResume");
		pause = false;
		lastTime = System.currentTimeMillis();
	}

	public static void onPause(){
		Log.i(TAG, "onPause");
		pause = true;
		if(lastTime == 0){
			return;
		}
		long currentTime = System.currentTimeMillis();
		long thisOnLineTime = currentTime - lastTime;
		onLineTime += thisOnLineTime;
		lastTime = 0;
	}

	/** * TimerTask对象，主要用于定时拉去服务器信息 */
	public class Task extends TimerTask {
		@Override
		public void run() {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());// HH:mm:ss
			// 获取当前时间
			Date date = new Date(System.currentTimeMillis());
			Log.i(TAG, "开始执行执行timer定时任务..." + simpleDateFormat.format(date));
			Utils.getSeferencegame(PushService.this);
			getRole();
		}
	}

	public static void closeSchedule() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void getRole() {
		int seconds;
		long currentTime = System.currentTimeMillis();
		if(onLineTime == 0){
			if(lastTime == 0) {
				seconds = 0;
			}else{
				seconds = (int)((currentTime - lastTime) / 1000);
			}
		}else{
			seconds = (int)(onLineTime / 1000);
		}
		onLineTime = 0;
		lastTime = pause ? 0 : currentTime;
		Log.i(TAG, "onLineTime = " + seconds + "秒");

		if(seconds == 0){
			return;
		}
		mContext =this;
		roletask = JmhyApi.get().starOnline(
				this,
				AppConfig.appKey,
				AppConfig.openid,
				SeferenceGame.getInstance(this).getPreferenceData("gameuser",
						"type"),
				SeferenceGame.getInstance(this).getPreferenceData("gameuser",
						"roleid"),
				SeferenceGame.getInstance(this).getPreferenceData("gameuser",
						"rolename"),
				String.valueOf(seconds),
				SeferenceGame.getInstance(this).getPreferenceData("gameuser",
						"level"),
				SeferenceGame.getInstance(this).getPreferenceData("gameuser",
						"gender"),
				SeferenceGame.getInstance(this).getPreferenceData("gameuser",
						"serverno"),
				SeferenceGame.getInstance(this).getPreferenceData("gameuser",
						"servername"),
				SeferenceGame.getInstance(this).getPreferenceData("gameuser",
						"balance"),
				SeferenceGame.getInstance(this).getPreferenceData("gameuser",
						"power"),
				SeferenceGame.getInstance(this).getPreferenceData("gameuser",
						"viplevel"),
				SeferenceGame.getInstance(this).getPreferenceData("gameuser",
						"ext"), new ApiRequestListener() {

					@Override
					public void onSuccess(Object obj) {
						Log.i(TAG, "onSuccess = " + obj);

						Log.i("jimisdk测试","返回数据:"+obj);
						if (obj != null) {
							final OnlineMessage msg = (OnlineMessage)obj;
							String murl = Utils.toBase64url(msg.getShowUrl());
							int exit = msg.getExit();
							//exit = 1;
							if (!TextUtils.isEmpty(murl)){
								if (AppConfig.save_guest_end){//游客或者手机号设置帐密结束才跳转实名url
									turnToNotice(murl);
								}
							}
							//如果不是0，强制关闭
							if (exit != 0 ){
								JiMiSDK.handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										closeSchedule();
										JiMiSDK.forceLogout(msg.getShowMsg());
									}
								},exit * 1000);

							}
                            if (!msg.getApplication_notice().equals("") && msg.getApplication_notice() != null && !msg.getApplication_notice().isEmpty()) {
                                if (msg.getApplication_notice().equals("1")) {
                                    Log.i("jimisdk测试", "有新消息，即将显示悬浮窗红点tip");
                                    FloatUtils.showFloatRedDot();
                                }
                            }
                            try {
                                JSONObject data=new JSONObject(msg.getChannel_event());
                                if (!msg.getChannel_event().equals("")) {
									JSONObject pay = new JSONObject(data.getString("pay"));

									PaymentInfo paymentInfo = new PaymentInfo();

									paymentInfo.setCporderid(pay.getString("cpOrderId"));
									paymentInfo.setOrdername(pay.getString("orderName"));
									paymentInfo.setAmount(pay.getString("amountCNY"));
									paymentInfo.setCporderid(pay.getString("cpOrderId"));

									paymentInfo.setRoleid(pay.getString("roleId"));
									paymentInfo.setRolename(pay.getString("roleName"));
									paymentInfo.setLevel(pay.getString("level"));
									paymentInfo.setServerno(pay.getString("serverNo"));
									paymentInfo.setZoneName(pay.getString("serverName"));
									paymentInfo.setBalance(pay.getString("balance"));
									paymentInfo.setPower(pay.getString("power"));
									paymentInfo.setViplevel(pay.getString("vipLevel"));

									PayData payData = new PayData();
									payData.setOrderid(pay.getString("orderId"));

									Log.i("jimsdk测试信息", "支付上报数据1" + paymentInfo);
									Log.i("jimsdk测试信息", "支付上报数据2" + payData);
									JiMiSDK.getStatisticsSDK().onPay(paymentInfo, payData, JiMiSDK.payChannel, true);
								}
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

					}

					@Override
					public void onError(int statusCode) {
						Log.i(TAG, "onError = " + statusCode);

					}
				});
	}


	public void turnToNotice(final String url) {
		if (TextUtils.isEmpty(url)) {
			// Toast.makeText(mContext, "此功能暂未开通", Toast.LENGTH_SHORT).show();
			return;
		}

		JiMiSDK.handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("url", url);
				intent.putExtra("notice", true);
				intent.setClass(JiMiSDK.mContext, JmUserinfoActivity.class);
				JiMiSDK.mContext.startActivity(intent);
			}
		}, 1000);
	}
}
