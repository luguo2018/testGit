package com.jmhy.sdk.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.jmhy.sdk.activity.JmCommunityActivity;
import com.jmhy.sdk.activity.JmpayActivity;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.BaseFloatActivity;
import com.jmhy.sdk.sdk.PayDataRequest;

public class FloatJspayInterface {
	public final static String TAG = FloatJspayInterface.class.getSimpleName();

	private Activity activity;

	private Seference mSeference;

	private Boolean payResult;
	private BaseFloatActivity floatActivity;
	public FloatJspayInterface(Activity activity, BaseFloatActivity floatActivity) {
		this.activity = activity;
		this.floatActivity =floatActivity;
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
			Log.d(TAG, "closeWebView: ");
			JiMiSDK.apiListenerInfo.onSuccess("close");
			floatActivity.removeContentView();
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
				floatActivity.removeContentView();
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
			Log.i("JrttStatistics", "------> pay success");
			Log.i(TAG, "pay success---PayDataRequest.getmPayInfo()"+PayDataRequest.getmPayInfo().getCporderid());
			if (!AppConfig.cache_orderId.equals(PayDataRequest.getmPayInfo().getCporderid())) {//支付成功：缓存订单号  不等于本次上报的订单号  上报
                AppConfig.cache_orderId = PayDataRequest.getmPayInfo().getCporderid();
                JiMiSDK.getStatisticsSDK().onPay(PayDataRequest.getmPayInfo(), PayDataRequest.getPayData(), JiMiSDK.payChannel, true);
            }else{//支付成功：缓存订单号 = 本次上报订单号  订单重复
                Log.i("jimi测试","************订单重复上报************");
                Log.i("jimi测试","************缓存订单************"+AppConfig.cache_orderId);
                Log.i("jimi测试","************上报订单************"+PayDataRequest.getmPayInfo().getCporderid());
            }


		}else{
			if(JiMiSDK.apiListenerInfo!=null){
				JiMiSDK.apiListenerInfo.onSuccess("close");
			}

			payResult = false;
			Log.i(TAG, "pay failure");
			Log.i("JrttStatistics", "------> pay failure");
			JiMiSDK.getStatisticsSDK().onPay(PayDataRequest.getmPayInfo(), PayDataRequest.getPayData(), JiMiSDK.payChannel, false);
		}
		if(activity!=null){
			floatActivity.removeContentView();
    	}
	}
	@JavascriptInterface
	public void closeAnimation(){
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				activity.hiddenLoading();
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
