package com.jmhy.sdk.activity;


import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.utils.JsInterface;
import com.jmhy.sdk.view.GifImageView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class JmCommunityActivity extends Activity implements OnClickListener {
	private final static String TAG = JmCommunityActivity.class.getSimpleName();

	private WebView mWebview;
	//private GifImageView mGifImageView;
	
	private String murl;

	private ValueCallback<Uri[]> uploadMessageAboveL;
	private ValueCallback<Uri> uploadMessage;
	private final static int FILE_CHOOSER_RESULT_CODE = 0x01;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(AppConfig.resourceId(this, "jmcommunity", "layout"));
		murl = getIntent().getStringExtra("url");
		intView();
	}

	private void intView() {
		/*mGifImageView = (GifImageView) findViewById(AppConfig.resourceId(this,
				"gif", "id"));
		if(AppConfig.skin == 2) {
			mGifImageView.setGifResource(AppConfig.resourceId(this, "jmloading_new",
					"drawable"));
		}else{
			mGifImageView.setGifResource(AppConfig.resourceId(this, "jmloading",
					"drawable"));
		}*/

		mWebview = (WebView) findViewById(AppConfig.resourceId(this,
				"webview", "id"));
		mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 默认不使用缓存！
		mWebview.setVerticalScrollBarEnabled(false);
		mWebview.getSettings().setSupportZoom(false);
		mWebview.getSettings().setSaveFormData(false);
		mWebview.getSettings().setSavePassword(false);
		mWebview.getSettings().setBuiltInZoomControls(false);
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.getSettings().setSupportZoom(false);
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

				showLoading();
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.i(TAG, "onPageFinished " + url);
				super.onPageFinished(view, url);
				hiddenLoading();
			}

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				Log.i(TAG, "onReceivedError");
				super.onReceivedError(view, request, error);
			}

			@Override
			public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
				Log.i(TAG, "onReceivedHttpError");
				super.onReceivedHttpError(view, request, errorResponse);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i(TAG, "shouldOverrideUrlLoading url = " + url);
				
			
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

					return false;
				}
			}

			/*@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				Log.i(TAG, "onReceivedSslError ");
				// super.onReceivedSslError(view, handler, error);
				handler.proceed();// 接受https所有网站的证书
			}*/
		});

		mWebview.setWebChromeClient(new WebChromeClient(){
			public void openFileChooser(ValueCallback<Uri> valueCallback) {
				uploadMessage = valueCallback;
				openImageChooserActivity();
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				Log.i(TAG, "onProgressChanged " + newProgress);
				super.onProgressChanged(view, newProgress);
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
		Log.i(TAG, "loadUrl = " + murl);
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
//		mWebview.pauseTimers();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		/*if(mGifImageView != null){
			mGifImageView.pause();
		}*/
		if (mWebview != null) {
			((ViewGroup) mWebview.getParent()).removeView(mWebview);
			mWebview.removeAllViews();
			mWebview.destroy();
			mWebview = null;
		}

		super.onDestroy();
	}

	public void showLoading(){
		Log.i(TAG, "showLoading");
		/*if(mGifImageView == null){
			return;
		}

		mGifImageView.setVisibility(View.VISIBLE);
        mGifImageView.play();*/
	}

	public void hiddenLoading() {
		Log.i(TAG, "hiddenLoading");
		/*if(mGifImageView == null){
			return;
		}
		mGifImageView.pause();
		mGifImageView.setVisibility(View.GONE);*/
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
