

package com.jmhy.sdk.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import com.jmhy.sdk.activity.JmCommunityActivity;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.utils.DisplayUtil;
import com.jmhy.sdk.utils.Utils;

public class FloatView extends FrameLayout implements OnTouchListener {
    private final static String TAG = FloatView.class.getSimpleName();

    private final int HANDLER_TYPE_HIDE_LOGO = 150;// 隐藏LOGO

    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;
    private Context mContext;

    private ImageView mIvFloatLogo;
    private LinearLayout mLlFloatMenu;
    private View mTvAccount;
    private View mTvkefu, mTvkefuLine;
    private View mTvGift, mTvGiftLine;
    private View accountTip;
    private View giftTip;
    private View iconTip;

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

    private final static int smallWidth = 30;
    private final static int fullWidth = 56;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private static ApiAsyncTask loginouttask;
    Handler mTimerHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_TYPE_HIDE_LOGO:
                    // 比如隐藏悬浮框
                    if (mCanHide) {
                        mCanHide = false;
                        /*if (mIsRight) {
                            // 靠边隐藏图片
                            mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_on", "drawable"));
                        } else {
                            mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_on", "drawable"));
                        }*/

                        switch (AppConfig.skin){
                            case 5:
                            case 4:
                                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext,
                                        "jm_float_on_4", "drawable"));
                                break;
                            case 3:
                                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext,
                                        "jm_float_on_3", "drawable"));
                                break;
                            case 2:
                                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext,
                                        "jm_float_on_new", "drawable"));
                                break;
                            default:
                                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext,
                                        "jm_float_on", "drawable"));
                        }
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)mFlFloatLogo.getLayoutParams();
                        layoutParams.width = DisplayUtil.dip2px(getContext(), smallWidth);
                        Log.i(TAG, "hidden");

                        refreshIconTip();

                        mWmParams.alpha = 0.7f;
                        mWindowManager.updateViewLayout(FloatView.this, mWmParams);
                        refreshFloatMenu(mIsRight);
                        mLlFloatMenu.setVisibility(View.GONE);

                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public FloatView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context mContext) {
        this.mContext = mContext;

        //mWindowManager = ((Activity) mContext).getWindowManager();
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
        //} else {
        //	mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //}
        // 设置图片格式，效果为背景透明
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置为左侧置?
        mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        mWmParams.x = 0;
        mWmParams.y = mScreenHeight / 2;

        // 设置悬浮窗口长宽数据
        mWmParams.width = LayoutParams.WRAP_CONTENT;
        mWmParams.height = LayoutParams.WRAP_CONTENT;
        try {
            addView(createView(mContext));
            mWindowManager.addView(this, mWmParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTimer = new Timer();
        // hide();

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
        mWindowManager.updateViewLayout(this, mWmParams);
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
        switch (AppConfig.skin){
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

                mLlFloatMenu.setVisibility(View.GONE);
            }
        });

        accountTip = rootFloatView.findViewById(AppConfig
                .resourceId(context, "account_tip", "id"));
        giftTip = rootFloatView.findViewById(AppConfig
                .resourceId(context, "gift_tip", "id"));
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

                    if (mLlFloatMenu.getVisibility() == View.VISIBLE) {
                        mLlFloatMenu.setVisibility(View.GONE);
                    } else {
                        mLlFloatMenu.setVisibility(View.VISIBLE);
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

        if(TextUtils.isEmpty(AppConfig.USERURL)){
            mTvAccount.setVisibility(GONE);
        }

        if(TextUtils.isEmpty(AppConfig.KEFU)){
            mTvkefu.setVisibility(GONE);
            mTvkefuLine.setVisibility(GONE);
        }else{
            if(mTvAccount.getVisibility() == GONE){
                mTvkefuLine.setVisibility(GONE);
            }
        }

        if(TextUtils.isEmpty(AppConfig.GIFT)){
            mTvGift.setVisibility(GONE);
            mTvGiftLine.setVisibility(GONE);
        }else{
            if(mTvkefu.getVisibility() == GONE){
                mTvGiftLine.setVisibility(GONE);
            }
        }
        switch (AppConfig.skin){
            case 5:
            case 4:
                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_on_4", "drawable"));
                break;
            case 3:
                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_on_3", "drawable"));
                break;
            case 2:
                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_on_new", "drawable"));
                break;
            default:
                mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_on", "drawable"));
        }

        if(AppConfig.showAccountTip){
            accountTip.setVisibility(VISIBLE);
        }
        if(AppConfig.showGiftTip){
            giftTip.setVisibility(VISIBLE);
        }
        refreshIconTip();

        return rootFloatView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        removeTimerTask();
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)mFlFloatLogo.getLayoutParams();
        layoutParams.width = DisplayUtil.dip2px(getContext(), fullWidth);
        Log.i(TAG, "onTouch " + event.getAction());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();

                switch (AppConfig.skin){
                    case 5:
                    case 4:
                        mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_4", "drawable"));
                        break;
                    case 3:
                        mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_3", "drawable"));
                        break;
                    case 2:
                        mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_new", "drawable"));
                        break;
                    default:
                        mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float", "drawable"));
                }

                iconTip.setVisibility(GONE);

                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);
                mDraging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 3
                        && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    mDraging = true;
                    // 更新浮动窗口位置参数
                    mWmParams.x = (int) (x - mTouchStartX);
                    mWmParams.y = (int) (y - mTouchStartY);

                    switch (AppConfig.skin){
                        case 5:
                        case 4:
                            mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_4", "drawable"));
                            break;
                        case 3:
                            mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_3", "drawable"));
                            break;
                        case 2:
                            mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_new", "drawable"));
                            break;
                        default:
                            mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float", "drawable"));
                    }

                    iconTip.setVisibility(GONE);

                    mWmParams.alpha = 0.5f;
                    mWindowManager.updateViewLayout(this, mWmParams);
                    mLlFloatMenu.setVisibility(View.GONE);
                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (mWmParams.x >= mScreenWidth / 2) {
                    mWmParams.x = 0;
                    mIsRight = false;
                } else if (mWmParams.x < mScreenWidth / 2) {
                    mIsRight = false;
                    mWmParams.x = 0;
                }

                switch (AppConfig.skin){
                    case 5:
                    case 4:
                        mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_4", "drawable"));
                        break;
                    case 3:
                        mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_3", "drawable"));
                        break;
                    case 2:
                        mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_new", "drawable"));
                        break;
                    default:
                        mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float", "drawable"));
                }

                iconTip.setVisibility(GONE);

                mWmParams.alpha = 1f;
                refreshFloatMenu(mIsRight);
                timerForHide();
                mWindowManager.updateViewLayout(this, mWmParams);
                // 初始化
                mTouchStartX = mTouchStartY = 0;
                break;
        }
        return false;
    }

    private void removeTimerTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void removeFloatView() {
        try {
            mWmParams.alpha = 0;
            mWindowManager.updateViewLayout(this, mWmParams);
            mWindowManager.removeView(this);
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
        try {
            if (getVisibility() != View.VISIBLE) {
                setVisibility(View.VISIBLE);

                if (mShowLoader) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)mFlFloatLogo.getLayoutParams();
                    layoutParams.width = DisplayUtil.dip2px(getContext(), fullWidth);
                    Log.i(TAG, "show");

                    switch (AppConfig.skin){
                        case 5:
                        case 4:
                            mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_4", "drawable"));
                            break;
                        case 3:
                            mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_3", "drawable"));
                            break;
                        case 2:
                            mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float_new", "drawable"));
                            break;
                        default:
                            mIvFloatLogo.setImageResource(AppConfig.resourceId(mContext, "jm_float", "drawable"));
                    }

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
            FrameLayout.LayoutParams paramsFloatImage = (FrameLayout.LayoutParams) mIvFloatLogo
                    .getLayoutParams();
            paramsFloatImage.gravity = Gravity.RIGHT;
            //mIvFloatLogo.setLayoutParams(paramsFloatImage);
            FrameLayout.LayoutParams paramsFlFloat = (FrameLayout.LayoutParams) mFlFloatLogo
                    .getLayoutParams();
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
        mContext = null;
        removeTimerTask();
        mTimer.cancel();
        mTimer = null;
        mTimerHandler.removeCallbacksAndMessages(null);
        mTimerHandler = null;
        removeFloatView();
    }

    private void turnToIntent(String url) {
        if (TextUtils.isEmpty(url)) {
            String tip = AppConfig.getString(mContext, "function_not_open");
            Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("url", url);
        intent.setClass(mContext, JmUserinfoActivity.class);
        mContext.startActivity(intent);

    }

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

    public void hiddenTip(int index){
        switch (index){
            case INDEX_ACCOUNT:
                accountTip.setVisibility(GONE);
                break;
            case INDEX_GIFT:
                giftTip.setVisibility(GONE);
                break;
        }

        refreshIconTip();
    }

    private void refreshIconTip(){
        if(accountTip.getVisibility() == View.VISIBLE || giftTip.getVisibility() == View.VISIBLE) {
            iconTip.setVisibility(VISIBLE);
        }else{
            iconTip.setVisibility(GONE);
        }
    }

    public void showTip(int index){
        switch (index){
            case INDEX_ACCOUNT:
                accountTip.setVisibility(VISIBLE);
                break;
            case INDEX_GIFT:
                giftTip.setVisibility(VISIBLE);
                break;
        }

        refreshIconTip();
    }
}

