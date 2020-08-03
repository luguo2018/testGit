package com.jmhy.sdk.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.jmhy.sdk.activity.JmBaseActivity;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.OneKeyLoginListener;
import com.jmhy.sdk.utils.AppUtils;
import com.mobile.auth.gatewayauth.AuthRegisterViewConfig;
import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.model.TokenRet;
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate;

import static com.jmhy.sdk.utils.AppUtils.dp2px;


public class JmOneKeyLogin extends JmBaseActivity {
    private String TAG = "jimisdk";
    private PhoneNumberAuthHelper mAlicomAuthHelper;
    private TokenResultListener mTokenListener;
    private String token;
//    private String key = "dYG3R2817Btq+mHwklkVO30+xhyxc8ZWQh5wIFYe9ebf6I4wltzmh7KVlSIpG5n5s1QHvsTFM1TEHpQYSWaRgmQMSpsyDQAUEBx3CZb0QABmNRe71rw/Q5DFzJBuoxXo4c2R3vPVIB961dTvNCgfR6VZtGQLgkovX7vYAlnWhc+C0bv4/leKaZyW5jgogmEwX6aHTprM/HsqB3dzs+dqt3M+n7ZejUb2ZevVlc31hizQm2sP7J+Jggg9vRFATHrnb+dNIYFAoIctj3ho9sJ8BkTz2Qr/1s7Q";
    private String key = AppConfig.oneKeyLogin_SecretKey;

    private int mScreenWidthDp;
    private int mScreenHeightDp;
    private final static int oneKeyLoginFail = 250;
    private Activity mContext;
    private OneKeyLoginListener listener;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    public void invoke(Context context, final OneKeyLoginListener listener) {
        this.listener = listener;
        this.mContext = (Activity) context;
        mTokenListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String ret) {
                Log.i(TAG, "成功+onTokenSuccess" + ret);

                /*
                 *   setText just show the result for get token。
                 *   use ret to verfiy number。
                 */
                TokenRet tokenRet = null;
                try {
                    tokenRet = JSON.parseObject(ret, TokenRet.class);
                    Log.e(TAG, "获取的json:" + ret);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (tokenRet != null && ("600024").equals(tokenRet.getCode())) {
                    Log.i(TAG, "终端自检成功:\n" + ret);
                }

                if (tokenRet != null && ("600001").equals(tokenRet.getCode())) {
                    Log.i(TAG, "唤起授权页成功:\n" + ret);
                }

                if (tokenRet != null && ("600000").equals(tokenRet.getCode())) {
                    token = tokenRet.getToken();
                    mAlicomAuthHelper.quitLoginPage();
                    Log.i(TAG, "获取token成功:\n" + token);
                    listener.onSuccess(token);
                    mContext.finish();
                }
            }

            @Override
            public void onTokenFailed(String s) {
                Log.i(TAG, "获取失败，或选择了切换其他方式" + s);
                AppConfig.showOneKeyLogin = false;
                closeLogin(mAlicomAuthHelper,s);
            }
        };

        mAlicomAuthHelper = PhoneNumberAuthHelper.getInstance(mContext, mTokenListener);
        mAlicomAuthHelper.setLoggerEnable(true);//设置日志输出是否开启 true则输出关键步骤运行日志
        mAlicomAuthHelper.setAuthSDKInfo(key);
        // SDK环境检查函数，检查终端是否支持号码认证，通过TokenResultListener返回code type 1：本机号码校验 2: 一键登录  600024 终端支持认证   600013 系统维护，功能不可用
        Log.i(TAG, "支持手机" + mAlicomAuthHelper.checkEnvAvailable());
        if (mAlicomAuthHelper.checkEnvAvailable()) {

            Log.i("jimisdk","屏幕方向"+mContext.getResources().getConfiguration().orientation);
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                //竖屏
                portraitConfigLoginTokenLandDialog();//竖屏
            } else {
                //横屏
                landscapeConfigLoginTokenLandDialog();//横屏
            }

            mAlicomAuthHelper.getLoginToken(mContext, 5000);//timeout时间
        } else {
            Log.i(TAG, "暂时无法使用，手机卡未打开或者已损坏");
            AppConfig.showOneKeyLogin = false;
            closeLogin(mAlicomAuthHelper,"暂时无法使用，手机卡未打开或者已损坏");
        }
    }

    private void landscapeConfigLoginTokenLandDialog() {
        mAlicomAuthHelper.removeAuthRegisterXmlConfig();
        mAlicomAuthHelper.removeAuthRegisterViewConfig();
        int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
        }
        updateScreenSize(authPageOrientation);
        final int dialogWidth = (int) (mScreenWidthDp * 0.63);
        final int dialogHeight = (int) (mScreenHeightDp * 0.6);

        final int logBtnOffsetY = dialogHeight / 2 - 50;


        mAlicomAuthHelper.addAuthRegistViewConfig("image_icon", new AuthRegisterViewConfig.Builder()
                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_NUMBER)
                .setView(createLandDialogPhoneNumberIcon(dp2px(mContext, 30)))
                .build());

        mAlicomAuthHelper.addAuthRegisterXmlConfig(new AuthRegisterXmlConfig.Builder()
                .setLayout(AppConfig.resourceId(mContext, "custom_land_dialog", "layout"), new AbstractPnsViewDelegate() {
                    @Override
                    public void onViewCreated(View view) {
                        findViewById(AppConfig.resourceId(mContext, "btn_close", "id")).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i(TAG, "X按钮关闭");
//                                closeLogin(mAlicomAuthHelper,"关闭一键登录");
                                mAlicomAuthHelper.quitLoginPage();
                                mAlicomAuthHelper.hideLoginLoading();
                                mContext.finish();
                                AppConfig.showOneKeyLogin = false;
                            }
                        });
                    }
                })
                .build());


        mAlicomAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()
                .setPrivacyState(true)
                .setCheckboxHidden(true)
                .setNavHidden(true)
                .setNavReturnHidden(true)
                .setNumFieldOffsetY(logBtnOffsetY - 50)
                .setSloganHidden(true)
                .setSwitchAccTextSize(12)
                .setSwitchOffsetY_B(55)


                //LogBtn 一键登录
                .setLogBtnOffsetY(logBtnOffsetY)
                .setLogBtnWidth(dialogWidth - 50)
                .setLogBtnMarginLeftAndRight(15)
                .setLogBtnHeight(30)
                .setLogBtnTextSize(12)

                .setDialogWidth(dialogWidth)
                .setDialogHeight(dialogHeight)
                .setDialogBottom(false)//dialog局底false则为居中
                .setScreenOrientation(authPageOrientation)
                .setAuthPageActIn("in_activity", "out_activity")
                .setAuthPageActOut("in_activity", "out_activity")
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                .setPrivacyOffsetY_B(20)

                .create());

    }

    private void portraitConfigLoginTokenLandDialog() {
        mAlicomAuthHelper.removeAuthRegisterXmlConfig();
        mAlicomAuthHelper.removeAuthRegisterViewConfig();
        int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
        }
        updateScreenSize(authPageOrientation);
        int dialogWidth = (int) (mScreenWidthDp * 0.8f);
        int dialogHeight = (int) (mScreenHeightDp * 0.65f);
        mAlicomAuthHelper.addAuthRegisterXmlConfig(new AuthRegisterXmlConfig.Builder()
                .setLayout(AppConfig.resourceId(mContext, "custom_land_dialog", "layout"), new AbstractPnsViewDelegate() {
                    @Override
                    public void onViewCreated(View view) {
                        findViewById(AppConfig.resourceId(mContext, "btn_close", "id")).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAlicomAuthHelper.quitLoginPage();
                                mAlicomAuthHelper.hideLoginLoading();
                                mContext.finish();
                                AppConfig.showOneKeyLogin = false;
                            }
                        });
                    }
                })
                .build());
        int logBtnOffset = dialogHeight / 2;
        mAlicomAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()
                .setPrivacyState(true)
                .setCheckboxHidden(true)
                .setNavHidden(true)
                .setNavColor(Color.TRANSPARENT)
                .setWebNavColor(Color.BLUE)
                .setAuthPageActIn("in_activity", "out_activity")
                .setAuthPageActOut("in_activity", "out_activity")
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                .setLogoImgPath("jm_logo_new")
//                .setLogBtnWidth(dialogWidth - 30)
                .setLogBtnMarginLeftAndRight(15)
                .setNavReturnHidden(true)
                .setLogoOffsetY(48)
                .setLogoWidth(30)
                .setLogoHeight(41)
                .setLogBtnOffsetY(logBtnOffset)
                .setSloganText("为了您的账号安全，请先绑定手机号")
                .setSloganOffsetY(logBtnOffset - 100)
                .setSloganTextSize(11)
                .setNumFieldOffsetY(logBtnOffset - 50)
                .setSwitchOffsetY(logBtnOffset + 50)
                .setSwitchAccTextSize(11)
                .setPageBackgroundPath("dialog_page_background")
                .setNumberSize(17)
                .setLogBtnHeight(28)
                .setLogBtnTextSize(16)
                .setDialogWidth(dialogWidth)
                .setDialogHeight(dialogHeight)
                .setDialogBottom(false)
                .setScreenOrientation(authPageOrientation)
                .create());


    }
    private void updateScreenSize(int authPageScreenOrientation) {
        int screenHeightDp = AppUtils.px2dp(mContext, AppUtils.getPhoneHeightPixels(mContext));
        int screenWidthDp = AppUtils.px2dp(mContext, AppUtils.getPhoneWidthPixels(mContext));
        int rotation = 0;
        if (authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_BEHIND) {
            authPageScreenOrientation = mContext.getRequestedOrientation();
        }
        if (authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                || authPageScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE) {
            rotation = Surface.ROTATION_90;
        }else{
            rotation = Surface.ROTATION_180;
        }
        switch (rotation) {
            case Surface.ROTATION_90:
                mScreenWidthDp = screenWidthDp;
                mScreenHeightDp = screenHeightDp;
                break;
            case Surface.ROTATION_180:
                mScreenWidthDp = screenWidthDp;
                mScreenHeightDp = screenHeightDp;
                break;
        }
    }

    private ImageView createLandDialogPhoneNumberIcon(int leftMargin) {
        ImageView imageView = new ImageView(mContext);
        int size = dp2px(mContext, 23);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size, size);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        layoutParams.leftMargin = leftMargin;
        imageView.setLayoutParams(layoutParams);
//        imageView.setBackgroundResource(AppConfig.resourceId(mContext, "phone", "drawable"));
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        return imageView;
    }

    private void closeLogin(PhoneNumberAuthHelper mAlicomAuthHelper,String msg) {
        mAlicomAuthHelper.quitLoginPage();
        mAlicomAuthHelper.hideLoginLoading();
        listener.onError(msg);
    }

}
