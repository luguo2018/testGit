package com.jmhy.sdk.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jmhy.sdk.activity.FloatUserInfoActivity;
import com.jmhy.sdk.activity.JmCommunityActivity;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.utils.DisplayUtil;
import com.jmhy.sdk.utils.SecurityUtils;
import com.jmhy.sdk.utils.Utils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class FloatView extends FrameLayout implements OnTouchListener {
    private final static String TAG = FloatView.class.getSimpleName();

    private final int HANDLER_TYPE_HIDE_LOGO = 150;// 隐藏LOGO
    private final int SHOW_KEFU_FLOAT = 160;// 显示客服小红点

    private WindowManager.LayoutParams mWmParams,mWmParams2;
    private WindowManager mWindowManager;
    private static Context mContext;
    private ObjectAnimator animator = null;

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
    private View hideView;
    private View float_hide_top,float_hide;
    private FrameLayout mFlFloatLogo;
    private boolean mIsRight = false;// logo是否在右边
    private boolean mCanHide;// 是否允许隐藏
    private boolean showFloatLogo;// 显示浮标
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
    private final static int fullWidth = 56;

    private Timer mTimer, getStateTimer;
    private TimerTask mTimerTask;
    private static ApiAsyncTask loginouttask;
    private File file = null;
    private boolean canVibrate = true;
    private boolean showDialog = false;

    Handler mTimerHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_TYPE_HIDE_LOGO:
                    // 比如隐藏悬浮框
                    if (mCanHide) {
                        Log.i(TAG, "隐藏悬浮框");

                        mCanHide = false;
                        /*if (mIsRight) {
                            // 靠边隐藏图片
                            mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_on", "drawable"));
                        } else {
                            mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_on", "drawable"));
                        }*/

                        setFloatLogo("jm_float_on_9","jm_float_on_new", "jm_float_on_red", "jm_float_on_4", "jm_float_on_3", "jm_float_on");
                        if (AppConfig.skin!=9) {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFlFloatLogo.getLayoutParams();
                            layoutParams.width = DisplayUtil.dip2px(getContext(), smallWidth);
                            Log.i(TAG, "hidden");
                        }
                        refreshIconTip();

                        if (AppConfig.skin==7){
                            mWmParams.alpha = 1f;
                        }else{
                            mWmParams.alpha = 0.7f;
                        }

                        if (mContext == null) {
                            Log.i(TAG, "mContext is null");
                        } else {
                            Log.i(TAG, "mContext is not null");

                            mWindowManager.updateViewLayout(FloatView.this, mWmParams);
                            refreshFloatMenu(mIsRight);
                            mLlFloatMenu.setVisibility(View.GONE);
                        }

                    }
                    break;
                case SHOW_KEFU_FLOAT:
                    if (showFloatLogo){//打开浮窗web页面时  不抖动浮标
                        return;
                    }

                    show();
                    //显示客服小红点
                    if (AppConfig.skin==9){
                        iconTip.setVisibility(VISIBLE);
                    }else{
                        iconTip.setVisibility(VISIBLE);
                        kefuTip.setVisibility(VISIBLE);
                    }

                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFlFloatLogo.getLayoutParams();
                    layoutParams.width = DisplayUtil.dip2px(getContext(), fullWidth);

                    setFloatLogo("jm_float_9","jm_float_new", "jm_float_red", "jm_float_4", "jm_float_3", "jm_float");
                    mWmParams.alpha = 1f;
                    mWindowManager.updateViewLayout(view, mWmParams);
                    mCanHide=false;//有消息设置false不让悬浮窗隐藏到左边
                    mDraging = false;
                    if (AppConfig.skin==9){
                        startAnim();
                    }
                    removeTimerTask();
                    break;
            }
            super.handleMessage(msg);
        }

    };

    public FloatView(Context context) {
        super(context);
        init(context);
    }

    public void setRedDotState() {
        if (showFloatLogo){
            Message message = mTimerHandler.obtainMessage();
            message.what = SHOW_KEFU_FLOAT;
            mTimerHandler.sendMessage(message);
        }

    }

    private void init(Context context) {
        this.mContext = context;
        view = this;
        //mWindowManager = ((Activity) mContext).getWindowManager();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        this.mWmParams = new WindowManager.LayoutParams();
        //刘海屏延伸到刘海里面
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            mWmParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//        }
        // 设置window type
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        mWmParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;
//        mWmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//        mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        //} else {
//        	mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //}
        // 设置图片格式，效果为背景透明
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
//        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mWmParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        // 调整悬浮窗显示的停靠位置为左侧置?
        mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        mWmParams.x = 0;
        mWmParams.y = mScreenHeight / 8;
        //原本为屏幕左边中间   现改左上
//        mWmParams.y = mScreenHeight / 2;

        // 设置悬浮窗口长宽数据
        mWmParams.width = LayoutParams.WRAP_CONTENT;
        mWmParams.height = LayoutParams.WRAP_CONTENT;
        try {
            if (AppConfig.skin == 9) {
                mWmParams2 = new WindowManager.LayoutParams();
                mWmParams2.format = PixelFormat.RGBA_8888;
                mWmParams2.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;
                mWmParams2.width = LayoutParams.WRAP_CONTENT;
                mWmParams2.height = LayoutParams.WRAP_CONTENT;
                mWmParams2.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                mWindowManager.addView((createHideView(mContext)), mWmParams2);
                hideView.setVisibility(INVISIBLE);
            }
            addView(createView(mContext));
            mWindowManager.addView(this, mWmParams);
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        mTimer = new Timer();
//         hide();

        timerForHide();
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
    private View createHideView(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // 从布局文件获取浮动窗口视图
        hideView = inflater.inflate(AppConfig.resourceId(context, "jm_float_rb_hide_9", "layout"), null);
        float_hide_top = hideView.findViewById(AppConfig.resourceId(context, "float_hide_top", "id"));
        float_hide = hideView.findViewById(AppConfig.resourceId(context, "float_hide", "id"));
        return hideView;
    }

    /**
     * 创建Float view
     *
     * @param context
     * @return
     */
    private View createView(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // 从布局文件获取浮动窗口视图
        View rootFloatView;
        Log.i(TAG, "AppConfig.skin == " + AppConfig.skin);

        switch (AppConfig.skin) {
            case 9:
                if (gifExists()){
                    rootFloatView = inflater.inflate(AppConfig.resourceId(context, "jm_float_view_9_hongbao", "layout"), null);
                }else{
                    rootFloatView = inflater.inflate(AppConfig.resourceId(context, "jm_float_view_9", "layout"), null);
                }
                break;
            case 7:
                rootFloatView = inflater.inflate(
                        AppConfig.resourceId(context, "jm_float_view_red", "layout"), null);
                break;
            case 6:
            case 5:
            case 4:
                rootFloatView = inflater.inflate(
                        AppConfig.resourceId(context, "jm_float_view_4", "layout"), null);
                break;
            default:
                rootFloatView = inflater.inflate(
                        AppConfig.resourceId(context, "jm_float_view", "layout"), null);
        }
        mFlFloatLogo = (FrameLayout) rootFloatView.findViewById(AppConfig
                .resourceId(context, "float_view", "id"));
        // 浮点图标
        mIvFloatLogo = (ImageView) rootFloatView.findViewById(AppConfig
                .resourceId(context, "float_view_icon_imageView", "id"));

        mLlFloatMenu = (LinearLayout) rootFloatView.findViewById(AppConfig
                .resourceId(context, "ll_menu", "id"));
        mTvAccount = rootFloatView.findViewById(AppConfig
                .resourceId(context, "tv_account", "id"));
        mTvAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                turnTouserIntent(AppConfig.USERURL);
                mLlFloatMenu.setVisibility(View.GONE);
                hiddenTip(INDEX_ACCOUNT);
            }
        });

        mTvGift = rootFloatView.findViewById(AppConfig
                .resourceId(context, "tv_gift", "id"));
        mTvGift.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                turnToIntent(AppConfig.GIFT);
                mLlFloatMenu.setVisibility(View.GONE);
                hiddenTip(INDEX_GIFT);
            }
        });

        mTvkefu = rootFloatView.findViewById(AppConfig
                .resourceId(context, "tv_kefu", "id"));
        mTvkefu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                turnToIntent(AppConfig.KEFU);
                hiddenTip(INDEX_KEFU);
            }
        });

        accountTip = rootFloatView.findViewById(AppConfig
                .resourceId(context, "account_tip", "id"));
        giftTip = rootFloatView.findViewById(AppConfig
                .resourceId(context, "gift_tip", "id"));
        kefuTip = rootFloatView.findViewById(AppConfig
                .resourceId(context, "kefu_tip", "id"));
        iconTip = rootFloatView.findViewById(AppConfig
                .resourceId(context, "icon_tip", "id"));
        mTvkefuLine = rootFloatView.findViewById(AppConfig
                .resourceId(context, "tv_kefu_line", "id"));
        mTvGiftLine = rootFloatView.findViewById(AppConfig
                .resourceId(context, "tv_gift_line", "id"));

        rootFloatView.setOnTouchListener(this);
        rootFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mDraging) {
                    Utils.getSeferencegameuser(mContext);

                    if (AppConfig.skin == 9) {//点浮窗  允许悬浮窗隐藏到左边  开启计时器隐藏回左边   关闭动画 打开新客服url
                        mCanHide=true;
                        timerForHide();
                        endAnimator();
//                        turnToIntent(AppConfig.float_url_home_center);
//                        String url="http://test.172jm.com/go/to?access_token="+AppConfig.Token+"&redirect_url=http://test.172jm.com/sdk_new/dist/#/home?fromGame=1";
//                        String url="https://apisdk.5tc5.com/go/to?access_token="+AppConfig.Token+"&redirect_url=https%3A%2F%2Fapisdk.5tc5.com%2Fsdk_new%2Fdist%2F%23%2Fhome%3FfromGame%3D1";
//                        String url="https://apisdk.5tc5.com/go/to?access_token="+AppConfig.Token+"&redirect_url=https%3A%2F%2Fapisdk.5tc5.com%2Fcommunity%2Fdist%2Fhome.html%3FfromGame%3D1%26showSetPre%3D1";
//                        AppConfig.float_url_home_center=url;
                        Log.i("jimi测试url：",AppConfig.float_url_home_center);
                        turnToIntent(AppConfig.float_url_home_center);
                    } else {
                        if (mLlFloatMenu.getVisibility() == View.VISIBLE) {
                            mLlFloatMenu.setVisibility(View.GONE);
                        } else {
                            mLlFloatMenu.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        rootFloatView.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        /*if (AppConfig.is_user_float_on.equals("0")) {
            mTvAccount.setVisibility(View.INVISIBLE);
        }
        if (AppConfig.is_service_float_on.equals("0")) {
            mTvkefu.setVisibility(View.INVISIBLE);
        }*/

        if (TextUtils.isEmpty(AppConfig.USERURL)) {
            mTvAccount.setVisibility(GONE);
        }

        if (TextUtils.isEmpty(AppConfig.KEFU)) {
            mTvkefu.setVisibility(GONE);
            mTvkefuLine.setVisibility(GONE);
        } else {
            if (mTvAccount.getVisibility() == GONE) {
                mTvkefuLine.setVisibility(GONE);
            }
        }

        if (TextUtils.isEmpty(AppConfig.GIFT)) {
            mTvGift.setVisibility(GONE);
            mTvGiftLine.setVisibility(GONE);
        } else {
            if (mTvkefu.getVisibility() == GONE) {
                mTvGiftLine.setVisibility(GONE);
            }
        }
        setFloatLogo("jm_float_on_9","jm_float_on_new", "jm_float_on_red", "jm_float_on_4", "jm_float_on_3", "jm_float_on");

        if (AppConfig.showAccountTip) {
            accountTip.setVisibility(VISIBLE);
        }
        if (AppConfig.showGiftTip) {
            giftTip.setVisibility(VISIBLE);
        }
        if (AppConfig.showKefuTip) {
            kefuTip.setVisibility(VISIBLE);
        }
        refreshIconTip();

        return rootFloatView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mContext == null) {
            Log.e("JiMiSDK", "floatview mcontext is null");
        }
        removeTimerTask();
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFlFloatLogo.getLayoutParams();
        layoutParams.width = DisplayUtil.dip2px(getContext(), fullWidth);
        Log.i(TAG, "onTouch " + event.getAction());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();

                setFloatLogo("jm_float_9","jm_float_new", "jm_float_red", "jm_float_4", "jm_float_3", "jm_float");

                iconTip.setVisibility(GONE);

                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);
                mDraging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 3 || Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    if (hideView!=null){
                        hideView.setVisibility(VISIBLE);
                    }
                    mDraging = true;
                    // 更新浮动窗口位置参数
                    mWmParams.x = (int) (x - mTouchStartX);
                    mWmParams.y = (int) (y - mTouchStartY);

                    setFloatLogo("jm_float_9","jm_float_new", "jm_float_red", "jm_float_4", "jm_float_3", "jm_float");

                    iconTip.setVisibility(GONE);
                    if (AppConfig.skin==9){
                        mWmParams.alpha = 1f;
                    }else{
                        mWmParams.alpha = 0.5f;
                    }

                    if (AppConfig.skin == 9) {
                        int cententFloatWidth = mFlFloatLogo.getWidth();
                        int cententFloatHeight = mFlFloatLogo.getHeight();
                        int minWidth = mScreenWidth - float_hide.getWidth()+cententFloatWidth;
                        int minHeight = mScreenHeight - float_hide.getHeight()+cententFloatHeight;
                        if ((mWmParams.x + cententFloatWidth) >= minWidth && (mWmParams.y + cententFloatHeight) >= minHeight && canVibrate) {
                            canVibrate = false;
                            showDialog = true;
                            playVibrate(getContext());
                            float_hide_top.setVisibility(VISIBLE);
                        } else if (((mWmParams.x + cententFloatWidth) < minWidth || (mWmParams.y + cententFloatHeight) < minHeight) && !canVibrate) {
                            canVibrate = true;
                            showDialog = false;
                            float_hide_top.setVisibility(INVISIBLE);
                        }
                    }

                    mWindowManager.updateViewLayout(this, mWmParams);
                    mLlFloatMenu.setVisibility(View.GONE);
                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
                canVibrate=true;

            case MotionEvent.ACTION_CANCEL:
                if (hideView!=null){
                    hideView.setVisibility(INVISIBLE);
                }
                if (showDialog){
                    showDialog=false;
                    if (float_hide_top!=null){
                        float_hide_top.setVisibility(INVISIBLE);
                    }
                    HideDialog exitdialog = new HideDialog(getContext(), AppConfig.resourceId(getContext(), "jm_MyDialog", "style"), new HideDialog.HideDialogListener() {


                        @Override
                        public void onConfirm() {
                            hide();
                        }

                        @Override
                        public void onCancel() {
                            mCanHide = true;
                            cancelMethod();
                        }
                    });
                    exitdialog.show();
                }else{
                    mCanHide = true;
                    cancelMethod();
                }

                break;
        }
        return false;
    }

    void cancelMethod() {
        if (mWmParams.x >= mScreenWidth / 2) {
            mWmParams.x = 0;
            mIsRight = false;
        } else if (mWmParams.x < mScreenWidth / 2) {
            mIsRight = false;
            mWmParams.x = 0;
        }

        setFloatLogo("jm_float_9", "jm_float_new", "jm_float_red", "jm_float_4", "jm_float_3", "jm_float");

        iconTip.setVisibility(GONE);

        mWmParams.alpha = 1f;
        refreshFloatMenu(mIsRight);
        if (mCanHide) {
            timerForHide();
        }
        mWindowManager.updateViewLayout(this, mWmParams);
        // 初始化
        mTouchStartX = mTouchStartY = 0;
    }

    void setFloatLogo(String jm_float_9,String jm_float_new, String jm_float_red, String jm_float_4, String jm_float_3, String jm_float) {
        switch (AppConfig.skin) {
            case 9:
                if (gifExists()){//文件存在读gif（皮肤9红包版）  否则默认皮肤9的图
                    Glide.with(getContext()).load(file).into(mIvFloatLogo);
                }else{
                    mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, jm_float_9, "drawable"));
                }
                break;
            case 8:
                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, jm_float_new, "drawable"));
                break;
            case 7:
                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, jm_float_red, "drawable"));
                break;
            case 6:
            case 5:
            case 4:
                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, jm_float_4, "drawable"));
                break;
            case 3:
                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, jm_float_3, "drawable"));
                break;
            case 2:
                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, jm_float_new, "drawable"));
                break;
            default:
                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, jm_float, "drawable"));
        }
    }

    private void removeTimerTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void removeFloatView() {
        try {
      /*      mWmParams.alpha = 0;
            mWindowManager.updateViewLayout(this, mWmParams);*/
            Log.e("JiMiSDK", "myfloatview removeFloatView");

            mWindowManager.removeView(this);
            mWindowManager.removeView(hideView);
            mWindowManager = null;
            removeAllViews();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 隐藏悬浮窗
     */
    public void hide() {
        showFloatLogo = false;
        setVisibility(View.GONE);
        Message message = mTimerHandler.obtainMessage();
        message.what = HANDLER_TYPE_HIDE_LOGO;
        mTimerHandler.sendMessage(message);
        removeTimerTask();
    }

    /**
     * 显示悬浮窗
     */
    public void show() {
        Log.i(TAG, "显示悬浮窗...");
        showFloatLogo = true;
        try {
            if (getVisibility() != View.VISIBLE) {
                setVisibility(View.VISIBLE);

                if (mShowLoader) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFlFloatLogo.getLayoutParams();
                    layoutParams.width = DisplayUtil.dip2px(getContext(), fullWidth);
                    Log.i(TAG, "show");

                    setFloatLogo("jm_float_9","jm_float_new", "jm_float_red", "jm_float_4", "jm_float_3", "jm_float");

                    iconTip.setVisibility(GONE);

                    mWmParams.alpha = 1f;
                    mWindowManager.updateViewLayout(this, mWmParams);

                    timerForHide();

                    mShowLoader = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新float view menu
     *
     * @param right
     */
    private void refreshFloatMenu(boolean right) {

        if (right) {
            FrameLayout.LayoutParams paramsFloatImage = (FrameLayout.LayoutParams) mIvFloatLogo.getLayoutParams();
            paramsFloatImage.gravity = Gravity.RIGHT;
            FrameLayout.LayoutParams paramsFlFloat = (FrameLayout.LayoutParams) mFlFloatLogo.getLayoutParams();
            paramsFlFloat.gravity = Gravity.RIGHT;
            //mFlFloatLogo.setLayoutParams(paramsFlFloat);

            /*int padding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources()
                            .getDisplayMetrics());
            int padding52 = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 45, mContext.getResources()
                            .getDisplayMetrics());

            LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams) mTvAccount
                    .getLayoutParams();
            paramsMenuAccount.rightMargin = padding;
            paramsMenuAccount.leftMargin = padding;
            mTvAccount.setLayoutParams(paramsMenuAccount);

            LinearLayout.LayoutParams paramsMenuFb = (LinearLayout.LayoutParams) mTvkefu
                    .getLayoutParams();
            paramsMenuFb.rightMargin = padding52;
            paramsMenuFb.leftMargin = padding;
            mTvkefu.setLayoutParams(paramsMenuFb);*/


        } else {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mIvFloatLogo
                    .getLayoutParams();
            //params.setMargins(0, 0, 0, 0);
            params.gravity = Gravity.LEFT;
            //mIvFloatLogo.setLayoutParams(params);
            FrameLayout.LayoutParams paramsFlFloat = (FrameLayout.LayoutParams) mFlFloatLogo
                    .getLayoutParams();
            paramsFlFloat.gravity = Gravity.LEFT;
            //mFlFloatLogo.setLayoutParams(paramsFlFloat);

            /*int padding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10, mContext.getResources()
                            .getDisplayMetrics());
            int padding52 = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 60, mContext.getResources()
                            .getDisplayMetrics());

            LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams) mTvAccount
                    .getLayoutParams();
            paramsMenuAccount.rightMargin = padding;
            paramsMenuAccount.leftMargin = padding52;
            mTvAccount.setLayoutParams(paramsMenuAccount);

            LinearLayout.LayoutParams paramsMenuFb = (LinearLayout.LayoutParams) mTvkefu
                    .getLayoutParams();
            paramsMenuFb.rightMargin = padding;
            paramsMenuFb.leftMargin = padding;
            mTvkefu.setLayoutParams(paramsMenuFb);*/


        }
    }

    /**
     * 定时隐藏float view
     */
    private void timerForHide() {
        Log.i(TAG, "timerForHide");

        mCanHide = true;

        // 结束任务
        if (mTimerTask != null) {
            try {
                mTimerTask.cancel();
                mTimerTask = null;
            } catch (Exception e) {
            }

        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = mTimerHandler.obtainMessage();
                message.what = HANDLER_TYPE_HIDE_LOGO;
                mTimerHandler.sendMessage(message);
            }
        };
        if (mCanHide) {
            mTimer.schedule(mTimerTask, 6000, 3000);
        }
    }

    public void destroy() {
        Log.e("JiMiSDK", "floatview destroy");
        mContext = null;
        removeTimerTask();
        mTimer.cancel();
        mTimer = null;

        if (getStateTimer != null) {
            getStateTimer.cancel();
            getStateTimer = null;
        }

        mTimerHandler.removeCallbacksAndMessages(null);
        mTimerHandler = null;


        removeFloatView();
    }
    FloatUserInfoActivity floatUserInfoActivity;
    private void turnToIntent(String url) {
        if (TextUtils.isEmpty(url)) {
            String tip = AppConfig.getString(mContext, "function_not_open");
            Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
            return;
        }
//        if (AppConfig.skin==9){
//        }else {

//            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            intent.putExtra("url", url);
//            intent.setClass(mContext, JmUserinfoActivity.class);
//            mContext.startActivity(intent);
//        }
        if(floatUserInfoActivity==null){
            floatUserInfoActivity = new FloatUserInfoActivity((Activity) mContext, new FloatUserInfoActivity.CloseFloatListener() {
                @Override
                public void closeFloat() {
                    show();
                }
            });
            floatUserInfoActivity.setViews(url);
            floatUserInfoActivity.show();
        }else {
            floatUserInfoActivity.setViews(url);
            floatUserInfoActivity.show();
        }
        hide();
    }
    private PopupWindow popupWindow;


    private void turnTouserIntent(String url) {
        if (TextUtils.isEmpty(url)) {
            String tip = AppConfig.getString(mContext, "function_not_open");
            Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
		/*intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);*/
        intent.putExtra("url", url);
        intent.setClass(mContext, JmCommunityActivity.class);
        mContext.startActivity(intent);

    }

    public void hiddenTip(int index) {
        switch (index) {
            case INDEX_ACCOUNT:
                accountTip.setVisibility(GONE);
                break;
            case INDEX_GIFT:
                giftTip.setVisibility(GONE);
                break;
            case INDEX_KEFU:
                kefuTip.setVisibility(GONE);
                break;
        }

        refreshIconTip();
    }

    private void refreshIconTip() {
        if (accountTip.getVisibility() == View.VISIBLE || giftTip.getVisibility() == View.VISIBLE || kefuTip.getVisibility() == View.VISIBLE) {
            if (AppConfig.skin!=9) {
                iconTip.setVisibility(VISIBLE);
            }
        } else {
            iconTip.setVisibility(GONE);
        }
    }

    public void showTip(int index) {
        switch (index) {
            case INDEX_ACCOUNT:
                accountTip.setVisibility(VISIBLE);
                break;
            case INDEX_GIFT:
                giftTip.setVisibility(VISIBLE);
                break;
            case INDEX_KEFU:
                kefuTip.setVisibility(VISIBLE);
                break;
        }

        refreshIconTip();
    }


    private boolean gifExists() {
        if (AppConfig.float_icon_url==null || AppConfig.float_icon_url.equals("")){
            return false;
        }
        String md5ResultString = SecurityUtils.getMD5Str(AppConfig.float_icon_url);
        if (file==null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
            } else {
                file = JiMiSDK.mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            }
            file= new File(file + "/" + md5ResultString + ".gif");
        }
        Log.i("jimi","查看文件"+file);
        if (file.exists()){
            return true;
        }else{
            return false;
        }

    }

    /**
     * 震动
     */
    public static void playVibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        long[] vibrationPattern = new long[]{0, 180};
        // 第一个参数为开关开关的时间，第二个参数是重复次数，振动需要添加权限
        vibrator.vibrate(vibrationPattern, -1);
    }

    //实现先顺时针360度旋转然后逆时针360度旋转动画功能
    private void startAnim() {
        if (animator == null) {
            animator = ObjectAnimator.ofFloat(mIvFloatLogo, "rotation", -30F, 30F, -30F);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setDuration(700);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        }

    }

    private void endAnimator() {
        if (animator != null) {
            animator.cancel();
            animator = ObjectAnimator.ofFloat(mIvFloatLogo, "rotation", 0F, 0F);
            animator.setDuration(100);
            animator.start();
            animator.cancel();
            animator = null;
        }

    }
}

