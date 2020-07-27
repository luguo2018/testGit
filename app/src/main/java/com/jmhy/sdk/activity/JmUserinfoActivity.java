package com.jmhy.sdk.activity;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.utils.AndroidBug5497Workaround;
import com.jmhy.sdk.utils.DisplayUtil;
import com.jmhy.sdk.utils.JsInterface;
import com.jmhy.sdk.utils.MimeType;
import com.jmhy.sdk.view.GifImageView;

import java.util.ArrayList;

public class JmUserinfoActivity extends JmBaseActivity implements OnClickListener {
	private final static String TAG = JmUserinfoActivity.class.getSimpleName();

	private WebView mWebview;
	private GifImageView mGifImageView;

	private String murl;

	private ValueCallback<Uri[]> uploadMessageAboveL;
	private ValueCallback<Uri> uploadMessage;
	private final static int FILE_CHOOSER_RESULT_CODE = 0x01;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            return true;
        } else {
            Log.i(TAG, "外部点击,收起软键盘" + event.getAction());
            View v = getCurrentFocus();
            hideSoftInput(v.getWindowToken());
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //设置之后可监听到外部点击，收起软键盘
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		boolean notice = getIntent().getBooleanExtra("notice", false);
		if(notice) {
			setContentView(AppConfig.resourceId(this, "jmnotice", "layout"));
		}else {
			setContentView(AppConfig.resourceId(this, "jmuserinfo", "layout"));
		}

		//仅客服页的webview才做外部点击收起软键盘及软键盘弹出收起，其他webview先不干预
        boolean isKefu = getIntent().getBooleanExtra("isKefu", false);
        if (isKefu) {
            AndroidBug5497Workaround.assistActivity(this);
        }

		murl = getIntent().getStringExtra("url");
		JiMiSDK.stackManager.pushActivity(this);
		intView();
	}

	private void intView() {
		// TODO Auto-generated method stub
		mWebview = (WebView) findViewById(AppConfig.resourceId(this,
				"webview", "id"));
		mGifImageView = (GifImageView) findViewById(AppConfig.resourceId(this,
				"gif", "id"));
		switch (AppConfig.skin){
			case 8:
				mGifImageView.setGifResource(AppConfig.resourceId(this, "jmloading_new",
						"drawable"));
				break;
			case 7:
				mGifImageView.setGifResource(AppConfig.resourceId(this, "jmloading_red",
						"drawable"));
				break;
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
			Log.e("JiMiSDK","JmuserinfoActivity view == null");
			return;
		}
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)view.getLayoutParams();
		switch (AppConfig.skin){
			case 5:
			case 4:
				//layoutParams.width = (int)getResources().getDimension(AppConfig.resourceId(this, "jm_login_width_4", "dimen"));
				//layoutParams.height = (int)getResources().getDimension(AppConfig.resourceId(this, "jm_login_height_4", "dimen"));
				break;
		}

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
				ArrayList<String> allType = new ArrayList<>();
				openImageChooserActivity(allType);
			}

			public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
				uploadMessage = valueCallback;
				ArrayList<String> allType = new ArrayList<>();
				openImageChooserActivity(allType);
			}

			@Override
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
				uploadMessageAboveL = filePathCallback;

				//h5文件选择回调，监听类型传对应格式打开文件选择器
				ArrayList fileType = new ArrayList();
				for (int i = 0; i < fileChooserParams.getAcceptTypes().length; i++) {
					Log.i(TAG,"查看webview打开文件选择器的文件类型："+ fileChooserParams.getAcceptTypes()[i]);
					if (fileChooserParams.getAcceptTypes()[i].equals("image/png")) {
						fileType.add(MimeType.IMAGE);
					} else if (fileChooserParams.getAcceptTypes()[i].equals("video/mp4")) {
						fileType.add(MimeType.VEDIO);
					} else if (fileChooserParams.getAcceptTypes()[i].equals("application/msword")) {
						fileType.add(MimeType.DOC);
						fileType.add(MimeType.DOCX);
						fileType.add(MimeType.PDF);
						fileType.add(MimeType.PPT);
						fileType.add(MimeType.PPTX);
						fileType.add(MimeType.XLS);
						fileType.add(MimeType.XLSX);
					}
				}
				Log.i("测试webV文件打开格式", "类型" + fileType);

				openImageChooserActivity(fileType);
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
			if(mWebview.canGoBack()){
				mWebview.goBack(); // goBack()表示返回WebView的上一页面
				return true;
			}else {
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
		JiMiSDK.stackManager.removeActivity(this);

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

	private void openImageChooserActivity(ArrayList mimeTypes) {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		//2020-07-27 本日之前的旧版  固定只打开文件格式文件选择器
//		i.setType("image/*");
		if (mimeTypes != null || !mimeTypes.isEmpty()) {
			i.setType("*/*");
            i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
		} else {//全部类型
			i.setType("*/*");
		}

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
