package com.jmhy.sdk.utils;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.jmhy.sdk.activity.JmCommunityActivity;
import com.jmhy.sdk.activity.JmpayActivity;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.sdk.PayDataRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class JspayInterface {
	public final static String TAG = JspayInterface.class.getSimpleName();

	private JmpayActivity activity;
	
	private Seference mSeference;

	private Boolean payResult;

	public JspayInterface(JmpayActivity activity) {
		this.activity = activity;
		mSeference = new Seference(activity);
	}

	public void setPayResult(Boolean payResult) {
		this.payResult = payResult;
	}

	public Boolean isPayResult() {
		return payResult;
	}

	@JavascriptInterface
	public void JavaScriptSavepassword(String user, String pwd, String uid) {
		mSeference.saveAccount(user, pwd, uid);
		AppConfig.saveMap(user, pwd, uid);
		Utils.saveUserToSd(activity);
	}
	
	@JavascriptInterface
    public  void closeWebView(){
		if(activity!=null){
			JiMiSDK.apiListenerInfo.onSuccess("close");
			activity.finish();
    	}
	}
	
	/**
	 * 切换账号
	 */
	@JavascriptInterface
	public void JavascriptSwitchUser() {
		//Log.i("kk","切换");

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				JiMiSDK.userlistenerinfo.onLogout("logout");
				activity.finish();
				AppConfig.isShow = false;
				FloatUtils.destroyFloat();

				JiMiSDK.getStatisticsSDK().onSwitchAccount();
			}
		});
	}
	/**
	 * 跳转浏览器
	 */
	@JavascriptInterface
	public void loadUrlByBrowser(String url){
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(url));
		activity.startActivity(intent);
	}
	
	@JavascriptInterface
	public void payNotify(String result){
		//result=0 失败，1 成功
		if(result.equals("1")){
			if(JiMiSDK.apiListenerInfo!=null){
				JiMiSDK.apiListenerInfo.onSuccess("success");
			}

			payResult = true;
			Log.i(TAG, "pay success");
			JiMiSDK.getStatisticsSDK().onPay(PayDataRequest.getmPayInfo(), PayDataRequest.getPayData(), JiMiSDK.payChannel, true);
		}else{
			if(JiMiSDK.apiListenerInfo!=null){
				JiMiSDK.apiListenerInfo.onSuccess("close");
			}

			payResult = false;
			Log.i(TAG, "pay failure");
			JiMiSDK.getStatisticsSDK().onPay(PayDataRequest.getmPayInfo(), PayDataRequest.getPayData(), JiMiSDK.payChannel, false);
		}
		if(activity!=null){
			activity.finish();
    	}
	}
	@JavascriptInterface
	public void closeAnimation(){
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				activity.hiddenLoading();
			}
		});
	}

	@JavascriptInterface
	public void openWebView(String url, int type){
		Intent intent = new Intent(activity, JmCommunityActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("type", type);
		activity.startActivity(intent);
	}

	/*@JavascriptInterface
	public void pay(final String data){
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject jsonObject = new JSONObject(data);
					int type = jsonObject.optInt("type");
					switch (type){
						case 0:
							String orderInfo = jsonObject.optString("orderInfo");
							activity.alipay(orderInfo);
							break;
						case 1:
							String partnerId = jsonObject.optString("partnerId");
							String prepayId = jsonObject.optString("prepayId");
							String nonceStr = jsonObject.optString("nonceStr");
							String sign = jsonObject.optString("sign");
							String timeStamp = jsonObject.optString("timeStamp");
							String appId = jsonObject.optString("appId");
							activity.wechatPay(appId, partnerId, prepayId, nonceStr, sign, timeStamp);
							break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}*/
}
