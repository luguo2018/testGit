package com.jmhy.sdk.activity;


import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.utils.JsInterface;
import com.jmhy.sdk.view.CornerCompatView;
import com.jmhy.sdk.view.GifImageView;

public class JmRealNameActivity extends JmBaseActivity implements OnClickListener {
	private final static String TAG = JmRealNameActivity.class.getSimpleName();

	private WebView mWebview;
	private GifImageView mGifImageView;

	private String murl;

	private ValueCallback<Uri[]> uploadMessageAboveL;
	private ValueCallback<Uri> uploadMessage;
	private final static int FILE_CHOOSER_RESULT_CODE = 0x01;
	private CornerCompatView mCornerCompatView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(AppConfig.resourceId(this, "jmuser_realname", "layout"));
		murl = getIntent().getStringExtra("url");
		JiMiSDK.stackManager.pushActivity(this);
		intView();
	}

	private void intView() {
		Log.e("kk","JmuserinfoActivity intView");

		// TODO Auto-generated method stub
		mWebview = (WebView) findViewById(AppConfig.resourceId(this,
				"webview", "id"));
		mGifImageView = (GifImageView) findViewById(AppConfig.resourceId(this,
				"gif", "id"));
		switch (AppConfig.skin){
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

		View view =  findViewById(AppConfig.resourceId(this,
				"content", "id"));
		//测试view是否为空
		if (view == null){
			return;
		}
//		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)view.getLayoutParams();
//		switch (AppConfig.skin){
//			case 5:
//			case 4:
//				layoutParams.width = (int)getResources().getDimension(AppConfig.resourceId(this, "jm_login_width_4", "dimen"));
//				layoutParams.height = (int)getResources().getDimension(AppConfig.resourceId(this, "jm_login_height_4", "dimen"));;
//				break;
//		}

		mWebview.setVerticalScrollBarEnabled(false);
		mWebview.getSettings().setSupportZoom(false);
		mWebview.getSettings().setSaveFormData(false);
		mWebview.getSettings().setSavePassword(false);
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.getSettings().setBuiltInZoomControls(false);
		mWebview.getSettings().setSupportZoom(false);
		mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 默认不使用缓存！
		mWebview.addJavascriptInterface(new JsInterface(this), "jimiJS");
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
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.i(TAG, "onPageStarted " + url);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				Log.i(TAG, "onPageFinished " + url);
				super.onPageFinished(view, url);
				hiddenLoading();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i(TAG, "shouldOverrideUrlLoading " + url);
				
			
				if (url.startsWith("weixin://wap/pay")) {
					Intent intent = null;

					try {
						intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
						view.getContext().startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}

					return true;
				} else {

					return super.shouldOverrideUrlLoading(view, url);
				}
			}

			/*@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				// super.onReceivedSslError(view, handler, error);
				handler.proceed();// 接受https所有网站的证书
				Log.i(TAG, "onReceivedSslError");
			}*/
		});

		mWebview.setWebChromeClient(new WebChromeClient(){
			public void openFileChooser(ValueCallback<Uri> valueCallback) {
				uploadMessage = valueCallback;
				openImageChooserActivity();
			}

			public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
				uploadMessage = valueCallback;
				openImageChooserActivity();
			}

			@Override
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
				uploadMessageAboveL = filePathCallback;
				openImageChooserActivity();
				return true;
			}
		});

		mWebview.loadUrl(murl);

		/*mWebview.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int width = mWebview.getWidth();
				int px = DisplayUtil.dip2px(JmUserinfoActivity.this, 471);
				Log.i(TAG, "width = " + width + ",px = " + px);
			}
		});*/
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == AppConfig.resourceId(this, "iphoneback", "id")) {
			if (mWebview.canGoBack()) {
				mWebview.goBack();
				
			} else {
				finish();
				
			}
		}else if (v.getId()==AppConfig.resourceId(this, "ivclose", "id")){
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mWebview.canGoBack()) {
				mWebview.goBack(); // goBack()表示返回WebView的上一页面
				return true;
			}else{
				return true;
			}

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

	public void hiddenLoading() {
		Log.i(TAG, "loadingamimation a");
		if(mGifImageView == null){
			return;
		}
		Log.i(TAG, "loadingamimation b");
		mGifImageView.pause();
		mGifImageView.setVisibility(View.GONE);
	}

	private void openImageChooserActivity() {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("image/*");
		startActivityForResult(Intent.createChooser(i, "选择图片"), FILE_CHOOSER_RESULT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == FILE_CHOOSER_RESULT_CODE) {
			if (null == uploadMessage && null == uploadMessageAboveL)
				return;
			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
			if (uploadMessageAboveL != null) {
				onActivityResultAboveL(requestCode, resultCode, data);
			} else if (uploadMessage != null) {
				uploadMessage.onReceiveValue(result);
				uploadMessage = null;
			}
		}
	}

	private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
		if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
			return;
		Uri[] results = null;
		if (resultCode == Activity.RESULT_OK) {
			if (intent != null) {
				String dataString = intent.getDataString();
				ClipData clipData = intent.getClipData();
				if (clipData != null) {
					results = new Uri[clipData.getItemCount()];
					for (int i = 0; i < clipData.getItemCount(); i++) {
						ClipData.Item item = clipData.getItemAt(i);
						results[i] = item.getUri();
					}
				}
				if (dataString != null)
					results = new Uri[]{Uri.parse(dataString)};
			}
		}
		uploadMessageAboveL.onReceiveValue(results);
		uploadMessageAboveL = null;
	}
}
