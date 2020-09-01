

package com.jmhy.sdk.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.utils.JsInterface;
import com.jmhy.sdk.utils.MimeType;
import com.jmhy.sdk.view.GifImageView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class JmUserInfoAddView extends FrameLayout {
    private final static String TAG = JmUserInfoAddView.class.getSimpleName();

    private final int HANDLER_TYPE_HIDE_LOGO = 150;// 隐藏LOGO
    private final int SHOW_KEFU_FLOAT = 160;// 显示客服小红点
    private LinearLayout back_view;
    private WebView mWebview;
    private GifImageView mGifImageView;
    private RelativeLayout right_back;
    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;
    private static Context mContext;
    private static Activity mActivity;
    private ObjectAnimator animator = null;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private ValueCallback<Uri> uploadMessage;
    private final static int FILE_CHOOSER_RESULT_CODE = 0x01;
    private static View rootFloatView;
    private ImageView mIvFloatLogo;
    private LinearLayout mLlFloatMenu;
    private View mTvAccount;
    private View mTvkefu, mTvkefuLine;
    private View mTvGift, mTvGiftLine;
    private View accountTip;
    private View giftTip;
    private View iconTip;
    private View kefuTip;
    private View view;
    private FrameLayout mFlFloatLogo;

    private boolean mIsRight = false;// logo是否在右边
    private boolean mCanHide;// 是否允许隐藏
    private float mTouchStartX;
    private float mTouchStartY;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean mDraging;
    private boolean mShowLoader = true;

    public final static int INDEX_ACCOUNT = 0x01;
    public final static int INDEX_GIFT = 0x02;
    public final static int INDEX_KEFU = 0x03;

    private final static int smallWidth = 30;
    private static int fullWidth = 56;

    private Timer mTimer, getStateTimer;
    private TimerTask mTimerTask;
    private static ApiAsyncTask loginouttask;
    WindowManager.LayoutParams floatUseWindowParams;
    private static int usableHeightPrevious2;//当前可用高度2 view专用
    int downScrollBy=0;
    int scrollByHeight=0;

    public JmUserInfoAddView(Context context, Activity activity, String url) {
        super(context);
        mContext = context;
        mActivity = activity;
        init(context, url);
    }


    private void init(Context context, String url) {
        this.mContext = context;
        view = this;
//        mWindowManager = ((Activity) mContext).getWindowManager();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        this.mWmParams = new WindowManager.LayoutParams();
        // 设置window type
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        mWmParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;
//        mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        //} else {
        //	mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //}
        // 设置图片格式，效果为背景透明
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
//        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWmParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        mWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;

//        Display display = mWindowManager.getDefaultDisplay();
//        Point p = new Point();
//        display.getRealSize(p);
//        mWmParams.width = p.x;
//        mWmParams.height = p.y;


        // 调整悬浮窗显示的停靠位置为左侧置?
//        mWmParams.gravity = Gravity.LEFT | Gravity.CENTER;
        mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        mWmParams.x = 0;
        mWmParams.y = 0;
//        mWmParams.y = mScreenHeight / 8;
//        mWmParams.y = mScreenHeight / 2;

        mWmParams.width = LayoutParams.WRAP_CONTENT;
        mWmParams.height = LayoutParams.MATCH_PARENT;
        mWmParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
//        mWmParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN| WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;

//        mWmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
//        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;


        try {
            addView(createView(mContext, url));
            mWindowManager.addView(createView(mContext, url), mWmParams);
        } catch (Exception e) {
            Log.w(TAG, e);
        }

    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        int oldX = mWmParams.x;
        int oldY = mWmParams.y;
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:// 横屏
		/*	if (mIsRight) {
				mWmParams.x = mScreenWidth;
				mWmParams.y = oldY;
			} else {*/
                mWmParams.x = oldX;
                mWmParams.y = oldY;
                //}
                break;
            case Configuration.ORIENTATION_PORTRAIT:// 竖屏
			/*if (mIsRight) {
				mWmParams.x = mScreenWidth;
				mWmParams.y = oldY;
			} else {*/
                mWmParams.x = oldX;
                mWmParams.y = oldY;
                //}
                break;
        }
        view = this;
        mWindowManager.updateViewLayout(this, mWmParams);
    }

    /**
     * 创建Float view
     *
     * @param context
     * @return
     */
    boolean isFirstResize=true;
    private View createView(final Context context, final String url) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // 从布局文件获取浮动窗口视图

        Log.i(TAG, "AppConfig.skin == " + AppConfig.skin);

        switch (AppConfig.skin) {
            case 9:
                rootFloatView = inflater.inflate(AppConfig.resourceId(context, "jmuserinfo_skin9", "layout"), null);
                break;

            default:
                rootFloatView = inflater.inflate(AppConfig.resourceId(context, "jmuserinfo_skin9", "layout"), null);
        }

//        AndroidBug5497Workaround.addOnGlobalLayoutListener(rootFloatView);
//        rootFloatView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            public void onGlobalLayout() {
//                Log.i("jimi测试","视图变化");
//            }
//        });
//        set5497Utils();


        mWebview = (WebView) rootFloatView.findViewById(AppConfig.resourceId(mContext, "webview", "id"));
        mWebview.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });
        mGifImageView = (GifImageView) rootFloatView.findViewById(AppConfig.resourceId(mContext, "gif", "id"));
        mGifImageView.setGifResource(AppConfig.resourceId(mContext, "jmloading_9", "drawable"));

        back_view = (LinearLayout) rootFloatView.findViewById(AppConfig.resourceId(mContext, "back_view", "id"));
        back_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("jimi", "点击背景");
                removeView();
            }
        });
        right_back = (RelativeLayout) rootFloatView.findViewById(AppConfig.resourceId(mContext, "right_back", "id"));
        right_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("jimi", "点击右侧返回按钮");
                removeView();
            }
        });


        DisplayMetrics outMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;

        FrameLayout.LayoutParams linearParams2 = (FrameLayout.LayoutParams) mWebview.getLayoutParams();
        linearParams2.width = (int) (heightPixels * 1.2);
        linearParams2.height = heightPixels;
//        linearParams2.topMargin -= fullWidth;
//        fullWidth -=fullWidth;
//            if (utils.hasNotchInScreen(this)){
//                int higth=utils.getNotchHigth();
//                linearParams2.leftMargin = higth;
//            }
//            延伸到刘海屏内
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                        WindowManager.LayoutParams mWmParams = new WindowManager.LayoutParams();
//                        mWmParams.layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//                    }
//
//                    mWmParams.layoutInDisplayCutoutMode(LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES);
        mWebview.setLayoutParams(linearParams2);
        mWebview.setVerticalScrollBarEnabled(false);
        mWebview.getSettings().setSupportZoom(false);
        mWebview.getSettings().setSaveFormData(false);
        mWebview.getSettings().setSavePassword(false);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setBuiltInZoomControls(false);//设置内置缩放控件
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 默认不使用缓存！
        mWebview.addJavascriptInterface(new JsInterface((Activity) mContext), "jimiJS");

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

        mWebview.loadUrl(url);
//        mWebview.loadUrl("https://api.kingoo.com.cn/web2/profile?uid=13461923463271286466&token=50807554098a1dc9ce8571a0fb73ba21&appid=997773064");

        return rootFloatView;
    }

    private void set5497Utils() {
        rootFloatView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootFloatView.getWindowVisibleDisplayFrame(r);
                int usableHeightNow = r.bottom - r.top;//计算键盘高度
                //当前可见高度和上一次可见高度不一致 布局变动
                Log.i("jimi测试","触发高度变动,计量高度:"+usableHeightNow+"---上次高度"+usableHeightPrevious2);
                //当前可见高度和上一次可见高度不一致 布局变动
                if (usableHeightNow != usableHeightPrevious2) {
                    int usableHeightSansKeyboard = rootFloatView.getRootView().getHeight(); //屏幕可用高度
                    int heightDifference = usableHeightSansKeyboard - usableHeightNow;  //高度差=屏幕可用高度-键盘高
                    Log.i("jimi测试","屏幕可用高:"+usableHeightSansKeyboard+"---高度差"+heightDifference);
                    floatUseWindowParams =(WindowManager.LayoutParams) rootFloatView.getLayoutParams(); //取控件textView当前的布局参数
                    if (heightDifference > (usableHeightSansKeyboard / 4)) {
                        //界面的高度变化超过1/4的屏幕高度，才会进行重新设置高度，能保证响应软键盘的弹出
//                        downScrollBy = usableHeightNow ;//向上偏移 键盘的高度（411）
//                        scrollByHeight += downScrollBy;  //偏移量
//                        if ( scrollByHeight < (downScrollBy*2) ){
//                            rootFloatView.scrollBy(0, downScrollBy);
//                            Log.i("测试偏移量","向上偏移量"+ scrollByHeight +"---限值"+(downScrollBy*2));
//                        }else{
//                            scrollByHeight =0;
//                        }
                        floatUseWindowParams.height = usableHeightSansKeyboard - heightDifference;
                        isFirstResize=true;
                    }else{
                        //隐藏键盘
//                        if (downScrollBy == 0) {//第一次进来走到这回调不做处理  后续如弹出键盘有偏移值  收起键盘时往回偏移
//                            Log.i("测试日志5", "首次收起键盘屏幕高" + usableHeightSansKeyboard + "偏移usableHeightNow: " + (-usableHeightNow));
//                        } else {
//                            scrollByHeight -= downScrollBy;
//                            Log.i("测试偏移量","向下偏移量"+ scrollByHeight +"---限值"+(-downScrollBy));
//                            if ( scrollByHeight > (-downScrollBy) ){
//                                rootFloatView.scrollBy(0, -downScrollBy);
//                            }else{
//                                scrollByHeight =0;
//                            }
//                        }
//                        if (downScrollBy == 0) {//第一次进来走到这回调不做处理  后续如弹出键盘有偏移值  收起键盘时往回偏移
//                            downScrollBy+=1;
//                        }else{
//                        }
                        isFirstResize=false;
                        floatUseWindowParams.height = usableHeightSansKeyboard - heightDifference;
                    }
                        rootFloatView.setLayoutParams(floatUseWindowParams);
                        rootFloatView.requestLayout();//请求布局
                        mWindowManager.updateViewLayout(rootFloatView, floatUseWindowParams);
                        usableHeightPrevious2 = usableHeightNow;
                }
            }
        });
    }


    private void removeView() {
        try {
            Log.e("JiMiSDK", mWindowManager+"addView removeView"+this);
//            mWindowManager.removeView(this);
//            mWindowManager = null;
//            removeAllViews();
//            this.removeView(view);
//            view.setVisibility(GONE);
            mWindowManager.removeView(rootFloatView);
//            mWindowManager.removeViewImmediate(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 显示悬浮窗
     */
    public void showWebFloatView() {
//        addView(createView(context, url));
        mWindowManager.addView(rootFloatView, mWmParams);
    }

    /**
     * 显示悬浮窗
     */
    public void hideWebFloatView() {
        mWindowManager.removeView(rootFloatView);
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

        mActivity.startActivityForResult(Intent.createChooser(i, "选择图片"), FILE_CHOOSER_RESULT_CODE);
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

}

