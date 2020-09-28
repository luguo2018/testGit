package com.jmhy.sdk.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.jmhy.sdk.activity.JmTopLoginTipActivity;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.MobileUser;
import com.jmhy.sdk.model.Msg;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.FragmentUtils;
import com.jmhy.sdk.utils.Utils;
import com.jmhy.sdk.view.CustomerCodeView;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.TokenResultListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class JmSmsLogin9Fragment extends JmBaseFragment implements
        OnClickListener {

    // 吉米
    private String TAG = "jimisdk";
    private View mBtuser;
    private TextView mIvregister;
    private TextView mTitleTv;
    private EditText mEdphone;
    private String phoneNumber;
    private String code;
    private TextView mIbcode, call_kefu, gray_phone_tv;
    private TextView mBtmobilelg;
    private LinearLayout mLinearUl;
    private TextView mTvistor;
    private ImageView mIvkefu, back;
    private TextView mTvagreement;

    private boolean flag = true;
    private int j = 0;
    private int count_down = 60;

    private Call mSmsTask;
    private Call mLoginmobileTask;
    private Call mGuestTask;

    List<String> moreCountList = new ArrayList<String>();
    List<String> morePwdList = new ArrayList<String>();
    List<String> moreUidList = new ArrayList<String>();
    List<HashMap<String, String>> contentList = new ArrayList<HashMap<String, String>>();
    private PhoneNumberAuthHelper mAlicomAuthHelper;
    private TokenResultListener mTokenListener;
    private String token;
    private final static int oneKeyLoginFail = 250;
    private int mScreenWidthDp;
    private int mScreenHeightDp;
    private boolean showJimiLogin = false;
    View view = null;
    private EditText editText;
    private TextView[] TextViews;
    private StringBuffer stringBuffer = new StringBuffer();
    private int count = 6;
    private ViewGroup container;
    private CustomerCodeView mySmsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        // 登录
        this.container = container;
        view = inflater.inflate(AppConfig.resourceId(getActivity(), "jmlogin_sms_code", "layout"), container, false);
        view.setClickable(true);//设置可点击的
        view.setVisibility(View.VISIBLE);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        intView();

        mySmsView = (CustomerCodeView) Objects.requireNonNull(getView()).findViewById(AppConfig.resourceId(getActivity(), "sms_code_edit", "id"));
        mySmsView.setInputCompleteListener(new CustomerCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                Log.i(TAG, "输入完毕,收起软键盘,输入内容：" + mySmsView.getEditContent());
                InputMethodManager manager = ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
                if (manager != null) {
                    manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                login(phoneNumber, "+86", mySmsView.getEditContent());
            }

            @Override
            public void deleteContent(boolean isDelete) {

            }
        });
    }


    private void intView() {

        mIbcode = (TextView) getView().findViewById(AppConfig.resourceId(getActivity(), "ibcode", "id"));
        call_kefu = (TextView) getView().findViewById(AppConfig.resourceId(getActivity(), "kefu_help", "id"));
        gray_phone_tv = (TextView) getView().findViewById(AppConfig.resourceId(getActivity(), "gray_phone_tv", "id"));
        back = (ImageView) getView().findViewById(AppConfig.resourceId(getActivity(), "back", "id"));
        mIbcode.setOnClickListener(this);
        call_kefu.setOnClickListener(this);
        back.setOnClickListener(this);

        mIbcode.setClickable(false);
        mIbcode.setBackgroundResource(AppConfig.resourceId(getActivity(), "jm_gray_btn_style", "drawable"));

        phoneNumber = AppConfig.phone_number;
        gray_phone_tv.setText(phoneNumber);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (flag) {
                    handler.sendEmptyMessage(AppConfig.CODE_BUTTON);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            mIbcode.setClickable(true);
            switch (msg.what) {

                case AppConfig.FLAG_FAIL:

                    String resultmsg = (String) msg.obj;
                    showMsg(resultmsg);
                    break;
                case AppConfig.CODE_BUTTON:
                    if ((count_down - j) == 0) {
                        mIbcode.setClickable(true);
                        flag = false;
                        mIbcode.setText("" + AppConfig.getString(getActivity(), "moblie_bt_code"));
                        mIbcode.setBackgroundResource(AppConfig.resourceId(getActivity(), "jm_orange_btn_style", "drawable"));
                        j = 0;
                    } else {
                        mIbcode.setClickable(false);
                        mIbcode.setText((count_down - j) + "s");
                    }
                    j++;
                    break;
                case AppConfig.MOBILELOGIN_SUCCESS:
                    flag = false;
                    MobileUser mobileUser = (MobileUser) msg.obj;
                    if (mobileUser.getPhone_register().equals("1")) {
                        // 跳转设置密码
                        Bundle args = new Bundle();
                        // Log.i("kk",mobileUser.getMoblie());
                        AppConfig.save_guest_end=false;
                        args.putString("username", mobileUser.getUnname());
                        args.putString("moblie", mobileUser.getMoblie());
                        args.putString("code_area", mobileUser.getCode_area());
                        args.putString("code", mobileUser.getMoblie_code());
                        args.putBoolean("isOneKeyLogin",false);
                        Fragment mJmSetpwdFragment = FragmentUtils.getJmSetpwdFragment(getActivity(), args);
                        replaceFragmentToActivity(getFragmentManager(),
                                mJmSetpwdFragment, AppConfig.resourceId(
                                        getActivity(), "content", "id"));

                    } else {
                        // 直接登录成功，返回数据
                        mSeference.saveTimeAndType(mobileUser.getUnname(),new SimpleDateFormat("MM月dd日 HH:mm:ss").format(new Date()), "手机号登录");
                        mSeference.saveAccount(mobileUser.getUnname(), "~~test", mobileUser.getLogin_token());
                        AppConfig.saveMap(mobileUser.getUnname(), "~~test", mobileUser.getLogin_token());
                        Utils.saveUserToSd(getActivity());

                        Utils.saveTimeAndTypeToSd(getActivity());

//                        wrapaLoginInfo("success", mobileUser.getMessage(),
//                                mobileUser.getUnname(), mobileUser.getOpenid(),
//                                mobileUser.getGame_token());
//                        showUserMsg(mobileUser.getUnname());
//                        AppConfig.USERURL = Utils.toBase64url(mobileUser
//                                .getFloat_url_user_center());
//                        String url = Utils.toBase64url(mobileUser
//                                .getShow_url_after_login());
//                        turnToIntent(url);
//                        AppConfig.float_url_home_center = Utils.toBase64url(mobileUser
//                                .getFloat_url_home_center());
                        Intent oneKeyLoginIntent = new Intent(getActivity(), JmTopLoginTipActivity.class);
                        oneKeyLoginIntent.putExtra("message",mobileUser.getMessage());
                        oneKeyLoginIntent.putExtra("uName",mobileUser.getUnname());
                        oneKeyLoginIntent.putExtra("openId",mobileUser.getOpenid());
                        oneKeyLoginIntent.putExtra("token",mobileUser.getGame_token());
                        oneKeyLoginIntent.putExtra("noticeUrl", Utils.toBase64url(mobileUser.getShow_url_after_login()));
                        oneKeyLoginIntent.putExtra("type", AppConfig.MOBILELOGIN_SUCCESS);
                        startActivity(oneKeyLoginIntent);

                        getActivity().finish();
                    }
                    break;

                case AppConfig.CODE_SUCCESS:
                    flag = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            while (flag) {

                                handler.sendEmptyMessage(AppConfig.CODE_BUTTON);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
//                                     TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    Msg msg2 = (Msg) msg.obj;
                    showMsg(msg2.getMessage());
                    break;
                case AppConfig.CODE_FAIL:

                    mIbcode.setClickable(true);
                    flag = false;
                    mIbcode.setText(""
                            + AppConfig.getString(getActivity(),
                            "moblie_bt_code"));
                    mIbcode.setBackgroundResource(AppConfig.resourceId(getActivity(), "jm_orange_btn_style", "drawable"));
                    j = 0;
                    String result = (String) msg.obj;
                    showMsg(result);
                    break;
            }
        }
    };

    /**
     * 手机登录
     *
     * @param mobile
     * @param codearea
     * @param code
     */

    private void login(String mobile, String codearea, String code) {

        mLoginmobileTask = JmhyApi.get().startloginMoblie(getActivity(),
                AppConfig.appKey, mobile, codearea, code,
                new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        // TODO Auto-generated method stub
                        if (obj != null) {
                            MobileUser mobileUser = (MobileUser) obj;
                            if (mobileUser.getCode().equals("0")) {

                                sendData(AppConfig.MOBILELOGIN_SUCCESS, obj,
                                        handler);

                            } else {
                                sendData(AppConfig.FLAG_FAIL,
                                        mobileUser.getMessage(), handler);
                            }
                        } else {
                            sendData(AppConfig.FLAG_FAIL, AppConfig.getString(
                                    getActivity(), "http_rror_msg"), handler);
                        }
                    }

                    @Override
                    public void onError(int statusCode) {
                        // TODO Auto-generated method stub
                        sendData(
                                AppConfig.FLAG_FAIL,
                                AppConfig.getString(getActivity(),
                                        "http_rror_msg"), handler);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == AppConfig.resourceId(getActivity(), "back", "id")) {//返回
            Fragment mJmUserLoginFragment = Fragment.instantiate(getActivity(), JmLoginHomePage9Fragment.class.getName());
            replaceFragmentToActivity(getFragmentManager(), mJmUserLoginFragment, AppConfig.resourceId(getActivity(), "content", "id"));
        } else if (id == AppConfig.resourceId(getActivity(), "ibcode", "id")) {
            mIbcode.setClickable(false);
            // 禁止点击  获取验证码请求发送才开点击
            startRequestSMS(phoneNumber, "+86", "1");
            flag = true;
            mIbcode.setClickable(false);
        } else if (id == AppConfig.resourceId(getActivity(), "kefu_help", "id")) {
            callKefu();
        }
    }


    /**
     * 获取验证码
     *
     * @param mobile
     * @param codearea
     * @param type     1注册2登陆3找回密码
     */
    public void startRequestSMS(String mobile, String codearea, String type) {
        mSmsTask = JmhyApi.get().startRequestSMS(getActivity(),
                AppConfig.appKey, mobile, codearea, type,
                new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        // TODO Auto-generated method stub
                        if (obj != null) {
                            Msg msg = (Msg) obj;

                            if (msg.getCode().equals("0")) {

                                sendData(AppConfig.CODE_SUCCESS,

                                        obj, handler);
                            } else if (msg.getCode().equals("44010")) {

                                sendData(AppConfig.FLAG_FAIL,

                                        msg.getMessage(), handler);
                            } else {

                                sendData(AppConfig.CODE_FAIL,

                                        msg.getMessage(), handler);
                            }
                        }
                    }

                    @Override
                    public void onError(int statusCode) {
                        // TODO Auto-generated method stub
                        sendData(
                                AppConfig.FLAG_FAIL,
                                AppConfig.getString(getActivity(),
                                        "http_rror_msg"), handler);
                    }
                });

    }

    @Override
    public void onDestroy() {
        if (mGuestTask != null) {
            mGuestTask.cancel();
        }
        if (mSmsTask != null) {
            mSmsTask.cancel();
        }
        if (mLoginmobileTask != null) {
            mLoginmobileTask.cancel();
        }
        super.onDestroy();
        mIbcode.setClickable(true);
        flag = false;
        mIbcode.setText(""
                + AppConfig.getString(getActivity(),
                "moblie_bt_code"));
        mIbcode.setBackgroundResource(AppConfig.resourceId(getActivity(), "jm_orange_btn_style", "drawable"));
        j = 0;
    }


}
