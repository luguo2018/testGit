package com.jmhy.sdk.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.config.WebApi;
import com.jmhy.sdk.model.BaseFloatActivity;
import com.jmhy.sdk.sdk.PayDataRequest;
import com.jmhy.sdk.utils.AndroidBug5497Workaround;
import com.jmhy.sdk.utils.HasNotchInScreenUtil;
import com.jmhy.sdk.utils.MimeType;
import com.jmhy.sdk.utils.checkEmulator.FloatJsInterface;
import com.jmhy.sdk.view.GifImageView;

import java.util.ArrayList;

public class FloatUserInfoActivity extends BaseFloatActivity {
    private static final String TAG = "FloatUserInfoActivity";
    private WebView mWebview;
    private GifImageView mGifImageView;
    private View parent;
    private View right_view;
    private String mUrl;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private ValueCallback<Uri> uploadMessage;
    private final static int FILE_CHOOSER_RESULT_CODE = 0x01;
    private int mWbeHeight;
    public boolean notice;
    public boolean protocol;
    private View right_back,back_view,right_close;
    private boolean isShowKeyboard;
    private int reduceHeight;
    private CloseFloatListener closeFloatListener;

    public FloatUserInfoActivity(Activity activity,CloseFloatListener listener) {
        super(activity);
        this.closeFloatListener=listener;
    }
    @Override
    public void setContentView(@NonNull int layout_id) {
        super.setContentView(layout_id);
    }


    @Override
    public void setViews( String url) {
        if(contentView!=null){
            return;
        }
        mUrl = url;
        intView();

        //防止软键盘覆盖webview
        mWebview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout: 软键盘状态改变");
                if(mWebview!=null){
                    mWbeHeight = mWebview.getHeight();
                    Rect r = new Rect();
                    //获取当前界面可视部分
                    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                    //获取屏幕的高度
                    int screenHeight =  activity.getWindow().getDecorView().getRootView().getHeight();
                    //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                    int heightDifference = screenHeight - r.bottom;
                    Log.d("Keyboard Size", "Size: " + heightDifference);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mWebview.getLayoutParams();
                    if (heightDifference>150&&!isShowKeyboard){
                        reduceHeight =mWbeHeight-(mWbeHeight-heightDifference) ;
                        layoutParams.height = mWbeHeight-heightDifference;
                        mWebview.setLayoutParams(layoutParams);
                        isShowKeyboard = true;
                    }else if (heightDifference<150&&isShowKeyboard){
                        isShowKeyboard =false;
                        layoutParams.height = mWbeHeight+reduceHeight;
                        mWebview.setLayoutParams(layoutParams);
                    }
                }
            }
        });
    }
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL)
                return;
            Uri result = data == null || resultCode != Activity.RESULT_OK ? null : data.getData();
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
    @Override
    public void removeContentView() {
        super.removeContentView();

        if (contentView != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "removeContentView: threadName" + Thread.currentThread().getName());
                    Log.i("jimi","断点错误查看："+contentView);
                    Log.i("jimi","断点错误查看2："+contentView.getParent());
                    ViewParent parent = contentView.getParent();
                    ViewGroup viewGroup = (ViewGroup) parent;
                    ViewGroup viewGroup1 = (ViewGroup) contentView;
                    int count = viewGroup1.getChildCount();
                    viewGroup1.removeAllViews();
                    Log.d(TAG, "removeContentView: childCount=" + count);
                    viewGroup.removeView(contentView);
                    mWebview.clearCache(true);
                    mWebview=null;
                    mGifImageView=null;
                    FloatUserInfoActivity.this.parent=null;
                    uploadMessage=null;
                    uploadMessageAboveL=null;
                    contentView=null;
                    closeFloatListener.closeFloat();
                }
            });

        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public boolean isShow() {
        return super.isShow();
    }

    private void intView() {
        //仅客服页的webview才做外部点击收起软键盘及软键盘弹出收起，其他webview先不干预
//        boolean isKefu = getIntent().getBooleanExtra("isKefu", false);
//        notice = false;
//        protocol =false;
        int layout_id = 0;
        if (protocol) {
            if (AppConfig.skin == 9) {
                Log.i("jimi", "加载全屏协议protocol");
                layout_id=AppConfig.resourceId(activity, "jm_protocol_skin9", "layout");
            }
        } else if (notice) {
            Log.i("jimi","float加载notice"+AppConfig.skin);
            if (AppConfig.skin == 9) {
                layout_id=AppConfig.resourceId(activity, "jmnotice_skin9", "layout");
            } else {
                layout_id=AppConfig.resourceId(activity, "jmnotice", "layout");
            }

        } else {
            if (AppConfig.skin == 9) {
                layout_id=AppConfig.resourceId(activity, "jmuserinfo_skin9", "layout");
            } else {
                layout_id=AppConfig.resourceId(activity, "jmuserinfo", "layout");
            }
        }

        setContentView(layout_id);
        if (true) {
            AndroidBug5497Workaround.assistActivity(activity);
        }
        // TODO Auto-generated method stub
        right_view = contentView.findViewById((AppConfig.resourceId(activity, "right_view", "id")));
        if (right_view!=null&&activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            right_view.setVisibility(View.GONE);
        }
        parent = contentView.findViewById((AppConfig.resourceId(activity, "parent", "id")));
        right_back =contentView.findViewById((AppConfig.resourceId(activity, "right_back", "id")));
        right_close =contentView.findViewById((AppConfig.resourceId(activity, "right_close", "id")));
        back_view =contentView.findViewById((AppConfig.resourceId(activity, "back_view", "id")));
        if (back_view!=null){
            back_view.setOnClickListener(this);
        }
        if(parent!=null){
            parent.setOnClickListener(this);
        }
        if (AppConfig.skin == 9 && right_back != null) {
            right_back.setOnClickListener(this);
        }
        if (AppConfig.skin == 9 && right_close != null) {
            right_close.setOnClickListener(this);
        }

        mWebview = (WebView) contentView.findViewById(AppConfig.resourceId(activity,
                "webview", "id"));
        mGifImageView = (GifImageView) contentView.findViewById(AppConfig.resourceId(activity,
                "gif", "id"));

        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;
        HasNotchInScreenUtil utils=new HasNotchInScreenUtil();
        if (utils.hasNotchInScreen(activity)){
            int higth=utils.getNotchHigth();
        }
        Log.i("jimi测试","是否存在刘海屏："+utils.hasNotchInScreen(activity)+"刘海屏高度："+utils.getNotchHigth());

        FrameLayout.LayoutParams linearParams2 = (FrameLayout.LayoutParams) mWebview.getLayoutParams();

        if (AppConfig.skin==9){
            //服务端字段控制弹窗、浮窗宽高，有值才设置
            if (notice){
                if (!AppConfig.notice_screen_scale.equals("") && !AppConfig.notice_wh_scale.equals("")) {
                    if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏 高度占满 宽度为屏幕* （服务端返回0.8）
                        linearParams2.width = (int) (widthPixels * Float.parseFloat(AppConfig.notice_screen_scale));
                        linearParams2.height = (int) (linearParams2.width * Float.parseFloat(AppConfig.notice_wh_scale));
                    } else {//横屏弹窗  高度为屏幕高的* （例服务端返0.8）； 宽度为高度的 *  （例服务端返1.0 宽高相同正方形显示）
                        linearParams2.width = (int) (linearParams2.height * Float.parseFloat(AppConfig.notice_wh_scale));
                        linearParams2.height = (int) (heightPixels * Float.parseFloat(AppConfig.notice_screen_scale));
                    }
                    mWebview.setLayoutParams(linearParams2);
                    mGifImageView.setLayoutParams(linearParams2);
                }
            }
            else{

                if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏  有值设置服务端的数值  无返回时设置默认0.7宽度
                    if (!AppConfig.float_portrait_w.equals("")) {
                        linearParams2.width = (int) (widthPixels * Float.parseFloat(AppConfig.float_portrait_w));
                    }else{
                        linearParams2.width = (int) (widthPixels * 0.85);
                    }
                    linearParams2.height = heightPixels;
                    mWebview.setLayoutParams(linearParams2);
                    mGifImageView.setLayoutParams(linearParams2);
                } else {//横屏  有值才设置
                    if (!AppConfig.float_landscape_w.equals("")) {
                        linearParams2.width = (int) (heightPixels * Float.parseFloat(AppConfig.float_landscape_w));
                        linearParams2.height = heightPixels;
                        mWebview.setLayoutParams(linearParams2);
                        mGifImageView.setLayoutParams(linearParams2);
                    }
                }
            }
        }
        else{
            if (notice){
                if (!AppConfig.notice_screen_scale.equals("") && !AppConfig.notice_wh_scale.equals("")) {
                    if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏 高度占满 宽度为屏幕* （服务端返回0.8）
                        linearParams2.width = (int) (widthPixels * Float.parseFloat(AppConfig.notice_screen_scale));
                        linearParams2.height = (int) (linearParams2.width * Float.parseFloat(AppConfig.notice_wh_scale));
                    } else {//横屏弹窗  高度为屏幕高的* （例服务端返0.8）； 宽度为高度的 *  （例服务端返1.0 宽高相同正方形显示）
                        linearParams2.width = (int) (linearParams2.height * Float.parseFloat(AppConfig.notice_wh_scale));
                        linearParams2.height = (int) (heightPixels * Float.parseFloat(AppConfig.notice_screen_scale));
                    }
                    mWebview.setLayoutParams(linearParams2);
                    mGifImageView.setLayoutParams(linearParams2);
                }
            }
            else{

                if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏  有值设置服务端的数值  无返回时设置默认0.7宽度
                    if (!AppConfig.float_portrait_w.equals("")) {
                        linearParams2.width = (int) (widthPixels * Float.parseFloat(AppConfig.float_portrait_w));
                    }else{
                        linearParams2.width = (int) (widthPixels * 0.75);
                    }
                    linearParams2.height = linearParams2.width;
                    mWebview.setLayoutParams(linearParams2);
                    mGifImageView.setLayoutParams(linearParams2);
                } else {//横屏  有值才设置
                    if (!AppConfig.float_landscape_w.equals("")) {
                        linearParams2.width = (int) (heightPixels * Float.parseFloat(AppConfig.float_landscape_w));
                        linearParams2.height = heightPixels;
                        mWebview.setLayoutParams(linearParams2);
                        mGifImageView.setLayoutParams(linearParams2);
                    }
                }
            }
        }


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

        View view = contentView.findViewById(AppConfig.resourceId(activity,
                "content", "id"));
        //测试view是否为空
        if (view == null) {
            Log.e("JiMiSDK", "JmuserinfoActivity view == null");
            return;
        }
        switch (AppConfig.skin) {
            case 5:
            case 4:
                //layoutParams.width = (int)getResources().getDimension(AppConfig.resourceId(activity, "jm_login_width_4", "dimen"));
                //layoutParams.height = (int)getResources().getDimension(AppConfig.resourceId(activity, "jm_login_height_4", "dimen"));
                break;
        }
        mWebview.setBackgroundColor(0); // 设置背景色
        mWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebview.setVerticalScrollBarEnabled(false);
        mWebview.getSettings().setSupportZoom(false);
        mWebview.getSettings().setSaveFormData(false);
        mWebview.getSettings().setSavePassword(false);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setBuiltInZoomControls(false);
        mWebview.getSettings().setSupportZoom(false);
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 默认不使用缓存！
        mWebview.addJavascriptInterface(new FloatJsInterface(activity, this, new FloatJsInterface.WebAdListener() {
            @Override
            public void end(String var1) {
                Log.i("jimi","加载广告回调成功");
                mWebview.loadUrl("javascript:adEndCallBack()");
            }

            @Override
            public void error(String var1) {
                mWebview.loadUrl("javascript:adEndCallBack(" + var1 + ")");
            }

            @Override
            public void close() {
//                removeContentView();
            }
        }), "jimiJS");
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 修复一些机型webview无法点击****/
        mWebview.requestFocus(View.FOCUS_DOWN);
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
                Log.i(TAG, "shouldOverrideUrlLoading 123 " + url+"\n"+url.startsWith("alipays://platformapi/startApp"));
                if (url.startsWith("weixin://wap/pay")) {
                    Intent intent = null;

                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }else if (url.startsWith("alipays://platformapi/startApp")) {
                    
                    try {
                        
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        view.getContext().startActivity(intent);

                        refreshPay();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });
        mWebview.setWebChromeClient(new WebChromeClient() {
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    for (int i = 0; i < fileChooserParams.getAcceptTypes().length; i++) {
                        Log.i(TAG, "查看webview打开文件选择器的文件类型：" + fileChooserParams.getAcceptTypes()[i]);
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
                }
                Log.i("测试webV文件打开格式", "类型" + fileType);

                openImageChooserActivity(fileType);
                return true;
            }
        });
        mWebview.loadUrl(mUrl);
    }

    public void hiddenLoading() {
        Log.i(TAG, "loadingamimation a");
        if (mGifImageView == null) {
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
        activity.startActivityForResult(Intent.createChooser(i, "选择图片"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == AppConfig.resourceId(activity, "iphoneback", "id")) {
            if (mWebview.canGoBack()) {
                mWebview.goBack();
            } else {
                // TODO Auto-generated method stub
            }
        } else if (v.getId() ==AppConfig.resourceId(activity, "right_back", "id")) {
            removeContentView();
            closeFloatListener.closeFloat();
        } else if (v.getId() ==AppConfig.resourceId(activity, "right_close", "id")) {
            removeContentView();
            closeFloatListener.closeFloat();
        } else if (v.getId() ==AppConfig.resourceId(activity, "parent", "id") ) {
            if (AppConfig.skin==9&&activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){//皮肤9 竖屏  点背景层关闭浮窗
                removeContentView();
                closeFloatListener.closeFloat();
            }
        } else if (v.getId() ==AppConfig.resourceId(activity, "back_view", "id") ) {
//            if (AppConfig.skin==9&&activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){//皮肤9 竖屏  点背景层关闭浮窗
//                removeContentView();
//                closeFloatListener.closeFloat();
//            }
        }
    }

    private void refreshPay() {
        String url = WebApi.BASE_HOST + "/pay_back/info?access_token={access_token}&billno={billno}";
        url = url.replace("{access_token}", AppConfig.Token);
        url = url.replace("{billno}", PayDataRequest.getmPayInfo().getCporderid());
        mWebview.loadUrl(url);

        Log.i(TAG, "url = " + url);
    }

    public interface CloseFloatListener {

        void closeFloat();

    }
}
