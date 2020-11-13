package com.jmhy.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.model.BaseFloatActivity;
import com.jmhy.sdk.sdk.PayDataRequest;
import com.jmhy.sdk.utils.DialogUtils;
import com.jmhy.sdk.utils.FloatJspayInterface;
import com.jmhy.sdk.utils.FloatUtils;
import com.jmhy.sdk.view.GifImageView;

public class FloatPayActivity extends BaseFloatActivity {
    private static final String TAG = "FloatPayActivity";

    private WebView mWebview;
    private ImageView mIvback;
    private GifImageView mGifImageView;
    private String murl;
    private View parent;
    private FloatJspayInterface jspayInterface;

    public FloatPayActivity(Activity activity) {
        super(activity);
    }

    @Override
    public void setViews( @Nullable String url) {
        if (contentView != null) {
            return;
        }
        murl = url;
        setContentView(AppConfig.resourceId(activity, "jmuserp", "layout"));
        intView();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public boolean isShow() {
        return super.isShow();
    }

    @Override
    public void setContentView(@NonNull int layout_id) {
        super.setContentView(layout_id);
    }

    private void intView() {
        // TODO Auto-generated method stub
        mWebview = (WebView) contentView.findViewById(AppConfig.resourceId(activity, "webview",
                "id"));
        mIvback = (ImageView) contentView.findViewById(AppConfig.resourceId(activity, "ivback",
                "id"));
        parent = contentView.findViewById(AppConfig.resourceId(activity, "parent",
                "id"));
        mGifImageView = (GifImageView) contentView.findViewById(AppConfig.resourceId(activity,
                "gif", "id"));
        mIvback.setOnClickListener(this);
        parent.setOnClickListener(this);
        switch (AppConfig.skin) {
            case 9:
                mGifImageView.setGifResource(AppConfig.resourceId(activity, "jmloading_9",
                        "drawable"));
                break;
            case 8:
                mGifImageView.setGifResource(AppConfig.resourceId(activity, "jmloading_new",
                        "drawable"));
                break;
            case 7:
                mGifImageView.setGifResource(AppConfig.resourceId(activity, "jmloading_red",
                        "drawable"));
                break;
            case 6:
                mGifImageView.setGifResource(AppConfig.resourceId(activity, "jmloading_new",
                        "drawable"));
                break;
            case 5:
            case 4:
                mGifImageView.setGifResource(AppConfig.resourceId(activity, "jmloading_4",
                        "drawable"));
                break;
            case 3:
                mGifImageView.setGifResource(AppConfig.resourceId(activity, "jmloading_3",
                        "drawable"));
                break;
            case 2:
                mGifImageView.setGifResource(AppConfig.resourceId(activity, "jmloading_new",
                        "drawable"));
                break;
            default:
                mGifImageView.setGifResource(AppConfig.resourceId(activity, "jmloading",
                        "drawable"));
        }
        mWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebview.setVerticalScrollBarEnabled(false);
        mWebview.getSettings().setSupportZoom(false);
        mWebview.getSettings().setSaveFormData(false);
        mWebview.getSettings().setSavePassword(false);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setBuiltInZoomControls(false);
        mWebview.getSettings().setSupportZoom(false);
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 默认不使用缓存！
        jspayInterface = new FloatJspayInterface(activity, this);
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

            private boolean shouldOverrideUrl(WebView view, String url) {
                Log.i("JiMiSDK", "shouldOverrideUrlLoading " + url);
                String lowcaseUrl = url.toLowerCase();
                if (lowcaseUrl.startsWith("weixin://wap/pay")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        view.getContext().startActivity(intent);
                        refreshPay();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Activity activity = JiMiSDK.stackManager.getBottomActivity();
                        if (activity != null) {
                            Log.i(TAG, "getBottomactivity = " + activity.toString());
                            DialogUtils.showTip(activity, AppConfig.getString(activity, "jm_no_install_wechat"));
                        }
                    }
                    return true;
                } else if (lowcaseUrl.startsWith("alipays://platformapi/startapp")) {
                    try {
                        Log.i("JiMiSDK", "uuuuuuuuuuuuu");

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

    @Override
    public View getContentView() {
        return super.getContentView();
    }

    @Override
    public void removeContentView() {
        super.removeContentView();
        if (contentView != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "removeContentView: threadName" + Thread.currentThread().getName());
                    ViewParent parent = contentView.getParent();
                    ViewGroup viewGroup = (ViewGroup) parent;
                    ViewGroup viewGroup1 = (ViewGroup) contentView;
                    int count = viewGroup1.getChildCount();
                    viewGroup1.removeAllViews();
                    Log.d(TAG, "removeContentView: childCount=" + count);
                    viewGroup.removeView(contentView);
                    mWebview = null;
                    mGifImageView = null;
                    mIvback = null;
                    FloatPayActivity.this.parent = null;
                    jspayInterface = null;
                    contentView = null;
                    FloatUtils.showFloat(activity);
                }
            });

        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == AppConfig.resourceId(activity, "ivback",
                "id")) {
            removeContentView();
        }

    }

    private void refreshPay() {
        String url = WebApi.BASE_HOST + "/pay_back/info?access_token={access_token}&billno={billno}";
        url = url.replace("{access_token}", AppConfig.Token);
        url = url.replace("{billno}", PayDataRequest.getmPayInfo().getCporderid());
        mWebview.loadUrl(url);

        Log.i(TAG, "url = " + url);
    }

    public void hiddenLoading() {
        Log.i(TAG, "loadingamimation");
        if (mGifImageView == null) {
            Log.i(TAG, "loadingamimation mGifImageView = null");
            return;
        }
        mGifImageView.pause();
        mGifImageView.setVisibility(View.GONE);
    }
}
