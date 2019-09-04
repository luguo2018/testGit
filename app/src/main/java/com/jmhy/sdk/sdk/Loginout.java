package com.jmhy.sdk.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;

public class Loginout {
	
	public static ApiAsyncTask loginouttask;
	public static Loginout mLoginout;
	public static Context mContext;
	public static  Loginout getInstatnce(Context context){
		if(mLoginout==null){
			mLoginout =new Loginout();
		}
		mContext = context;
		
       
		handler.sendEmptyMessage(AppConfig.GET_PAY);
		return mLoginout;
	}
	
	
	

	public static void ExitLoginout() {
		loginouttask = JmhyApi.get().starguserLoginout(mContext, 
				AppConfig.appKey,  new ApiRequestListener() {

					@Override
					public void onSuccess(Object obj) {
						// TODO Auto-generated method stub
						sendData(AppConfig.LOGINOUT_SUCCESS, obj, handler);
					}

					@Override
					public void onError(int statusCode) {
						// TODO Auto-generated method stub
						//JiMiSDK.userlistenerinfo.onLogout("logout");
						sendData(AppConfig.FLAG_FAIL,  AppConfig.getString(mContext, "http_rror_msg"),
								handler);
					}
				});

	}

	public static void sendData(int num, Object data, Handler callback) {

		Message msg = callback.obtainMessage();
		msg.what = num;
		msg.obj = data;
		msg.sendToTarget();
	}
	private static Handler handler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConfig.GET_PAY:
				 ExitLoginout() ;
				break;
				case AppConfig.LOGINOUT_SUCCESS:
					JiMiSDK.userlistenerinfo.onLogout("logout");
					JiMiSDK.getStatisticsSDK().onSwitchAccount();
					break;
			}
		}
	};

}