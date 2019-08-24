package com.jmhy.sdk.push;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.SeferenceGame;
import com.jmhy.sdk.utils.Utils;

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

	public static long startTime = 3000 * 60;// 3分钟之后请求一次
	private static Timer timer;
	private Task task;
	private Handler handler;
	private ApiAsyncTask roletask;

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
		if (timer == null) {
			timer = new Timer();
		}
		if (task == null) {
			task = new Task();
		}
		handler = new Handler();
		lastTime = 0;
		onLineTime = 0;
		timer.schedule(task, startTime, AppConfig.ONLIE_TIEM * 1000);

		Log.i(TAG, "定时任务初始化成功...");
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
						// TODO Auto-generated method stub

					}

					@Override
					public void onError(int statusCode) {
						// TODO Auto-generated method stub

					}
				});
	}
}
