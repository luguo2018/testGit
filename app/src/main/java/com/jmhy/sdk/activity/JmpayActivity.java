package com.jmhy.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.sdk.PayDataRequest;
import com.jmhy.sdk.utils.DialogUtils;
import com.jmhy.sdk.utils.JspayInterface;
//import com.jmhy.sdk.utils.thirdPlatform.AlipayUtils;
//import com.jmhy.sdk.utils.thirdPlatform.PayListener;
//import com.jmhy.sdk.utils.thirdPlatform.WechatPlatform;
import com.jmhy.sdk.view.GifImageView;

public class JmpayActivity extends JmBaseActivity implements OnClickListener {
	private final static String TAG = JmpayActivity.class.getSimpleName();

	private WebView mWebview;
	private ImageView mIvback;
	private GifImageView mGifImageView;
	private String murl;

	private JspayInterface jspayInterface;
	//private WechatPlatform platform;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(AppConfig.resourceId(this, "jmuserp", "layout"));
		murl = getIntent().getStringExtra("url");
		//platform = new WechatPlatform(payListener);
		intView();
		// mDialog=DialogUtils.ShowDialog(this);
	}

	private void intView() {
		// TODO Auto-generated method stub
		mWebview = (WebView) findViewById(AppConfig.resourceId(this, "webview",
				"id"));
		mIvback = (ImageView) findViewById(AppConfig.resourceId(this, "ivback",
				"id"));
		mIvback.setOnClickListener(this);
		mGifImageView = (GifImageView) findViewById(AppConfig.resourceId(this,
				"gif", "id"));

		switch (AppConfig.skin) {
			case 6:
				mGifImageView.setGifResource(AppConfig.resourceId(this, "jmloading_new",
						"drawable"));
				break;
			case 5:
			case 4:
				mGifImageView.setGifResource(AppConfig.resourceId(this, "jmloading_4",
						"drawable"));
				break;
			case 3:
				mGifImageView.setGifResource(AppConfig.resourceId(this, "jmloading_3",
						"drawable"));
				break;
			case 2:
				mGifImageView.setGifResource(AppConfig.resourceId(this, "jmloading_new",
						"drawable"));
				break;
			default:
				mGifImageView.setGifResource(AppConfig.resourceId(this, "jmloading",
						"drawable"));
		}

		mWebview.setVerticalScrollBarEnabled(false);
		mWebview.getSettings().setSupportZoom(false);
		mWebview.getSettings().setSaveFormData(false);
		mWebview.getSettings().setSavePassword(false);
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.getSettings().setBuiltInZoomControls(false);
		mWebview.getSettings().setSupportZoom(false);
		mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 默认不使用缓存！
		jspayInterface = new JspayInterface(this);
		mWebview.addJavascriptInterface(jspayInterface, "jimiJS");
		mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		// 修复一些机型webview无法点击****/
		mWebview.requestFocus(View.FOCUS_DOWN);
		mWebview.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					if (!v.hasFocus()) {
						v.requestFocus();
					}
					break;
				}
				return false;
			}
		});
		// ************************//
		mWebview.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				// Log.i("kk","加"+js);
				hiddenLoading();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return shouldOverrideUrl(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				return shouldOverrideUrl(view, request.getUrl().toString());
			}

			private boolean shouldOverrideUrl(WebView view, String url){
				Log.i("JiMiSDK","shouldOverrideUrlLoading "+url);

				if (url.startsWith("weixin://wap/pay")) {
					try {
						Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
						view.getContext().startActivity(intent);

						refreshPay();
					} catch (Exception e) {
						e.printStackTrace();

						finish();

						Activity activity = JiMiSDK.stackManager.getBottomActivity();
						if (activity != null) {
							Log.i(TAG, "getBottomactivity = " + activity.toString());
							DialogUtils.showTip(activity, AppConfig.getString(activity, "jm_no_install_wechat"));
						}
					}

					return true;
				} else if (url.startsWith("alipays://platformapi/startApp")){
					try {
						Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
						view.getContext().startActivity(intent);

						refreshPay();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				} else {

					return false;
				}
			}
		});

		mWebview.setWebChromeClient(new WebChromeClient());

		mWebview.loadUrl(murl);
	}

	private void refreshPay(){
		String url = WebApi.BASE_HOST + "/pay_back/info?access_token={access_token}&billno={billno}";
		url = url.replace("{access_token}", AppConfig.Token);
		url = url.replace("{billno}", PayDataRequest.getmPayInfo().getCporderid());
		mWebview.loadUrl(url);

		Log.i(TAG, "url = " + url);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == AppConfig.resourceId(this, "ivback", "id")) {
			// if (mWebview.canGoBack()) {
			// mWebview.goBack();

			// } else {
			if (JiMiSDK.apiListenerInfo != null) {
				JiMiSDK.apiListenerInfo.onSuccess("close");
			}
			finish();

			// }
		} else if (v.getId() == AppConfig.resourceId(this, "ivclose", "id")) {
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebview.canGoBack()) {
			mWebview.goBack(); // goBack()表示返回WebView的上一页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		mWebview.onResume();
		mWebview.resumeTimers();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mWebview.onPause();
		mWebview.pauseTimers();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if(mGifImageView != null){
			mGifImageView.pause();
		}
		if (mWebview != null) {
			((ViewGroup) mWebview.getParent()).removeView(mWebview);
			mWebview.removeAllViews();
			mWebview.destroy();
			mWebview = null;
		}
		super.onDestroy();

	}

	@Override
	public void finish() {
		if(jspayInterface != null && jspayInterface.isPayResult() == null){
			Log.i(JspayInterface.TAG, "pay cancel");
			Log.i("JrttStatistics", "------> pay cancel");
			JiMiSDK.getStatisticsSDK().onPay(PayDataRequest.getmPayInfo(), PayDataRequest.getPayData(), JiMiSDK.payChannel, false);

			if(JiMiSDK.apiListenerInfo != null){
				JiMiSDK.apiListenerInfo.onSuccess("cancel");
			}
		}

		super.finish();
	}

	public void hiddenLoading() {
		Log.i(TAG, "loadingamimation");
		if(mGifImageView == null){
			Log.i(TAG, "loadingamimation mGifImageView = null");
			return;
		}
		mGifImageView.pause();
		mGifImageView.setVisibility(View.GONE);
	}

	/*public void alipay(String orderInfo){
		AlipayUtils.pay(this, orderInfo, payListener);
	}

	public void wechatPay(String appId, String partnerId, String prepayId, String nonceStr, String sign, String timeStamp){
		WechatPlatform.WECHAT_APP_ID = appId;
		if(platform.isWXAppInstalled(this)){
			String tip = AppConfig.getString(this, "jm_no_install_wechat");
			DialogUtils.showTip(this, tip);
			return;
		}

		platform.pay(this, partnerId, prepayId, nonceStr, sign, timeStamp);
	}

	private PayListener payListener = new PayListener() {
		@Override
		public void onSuccess() {
			Log.i(TAG, "pay success");
			jspayInterface.setPayResult(true);

			if(JiMiSDK.apiListenerInfo != null){
				JiMiSDK.apiListenerInfo.onSuccess("success");
			}

			JiMiSDK.getStatisticsSDK().onPay(PayDataRequest.getmPayInfo(), PayDataRequest.getPayData(), JiMiSDK.payChannel, true);
		}

		@Override
		public void onFailure() {
			Log.i(TAG, "pay failure");
			jspayInterface.setPayResult(false);

			if(JiMiSDK.apiListenerInfo != null){
				JiMiSDK.apiListenerInfo.onSuccess("close");
			}

			JiMiSDK.getStatisticsSDK().onPay(PayDataRequest.getmPayInfo(), PayDataRequest.getPayData(), JiMiSDK.payChannel, false);
		}

		@Override
		public void onCancel() {
			Log.i(TAG, "pay cancel");
			jspayInterface.setPayResult(false);

			if(JiMiSDK.apiListenerInfo != null){
				JiMiSDK.apiListenerInfo.onSuccess("cancel");
			}

			JiMiSDK.getStatisticsSDK().onPay(PayDataRequest.getmPayInfo(), PayDataRequest.getPayData(), JiMiSDK.payChannel, false);
		}
	};*/
}
