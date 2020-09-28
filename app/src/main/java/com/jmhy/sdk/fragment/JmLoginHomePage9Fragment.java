package com.jmhy.sdk.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.jmhy.sdk.activity.JmTopLoginTipActivity;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.http.OneKeyLoginListener;
import com.jmhy.sdk.model.Guest;
import com.jmhy.sdk.model.LoginMessage;
import com.jmhy.sdk.model.Msg;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.FragmentUtils;
import com.jmhy.sdk.utils.Utils;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.TokenResultListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class JmLoginHomePage9Fragment extends JmBaseFragment implements
        OnClickListener {

    // 吉米
    private String TAG = "jimisdk";
    private View mBtuser;
    private TextView mIvregister;
    private ImageView kefu_help, back;
    private EditText mEdphone;
    private EditText mEdcode;
    private String iphone;
    private String code;
    private TextView mBtmobilelg;
    private LinearLayout mLinearUl, jm_skin9_user_login, jm_skin9_quick_login, jm_skin9_guest_login, jm_skin9_kefu;
    private TextView mTvistor, jm_skin9_phone_login_btn;
    private ImageView mIvkefu;
    private TextView mTvagreement;


    private boolean flag = true;
    private int j = 0;

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
    View sms_view = null;
    private EditText editText;
    private TextView[] TextViews;
    private StringBuffer stringBuffer = new StringBuffer();
    private int count = 6;
    private ViewGroup container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        // 登录
        this.container = container;
        view = inflater.inflate(AppConfig.resourceId(getActivity(),
                "jmlogin_main_9", "layout"), container, false);
        view.setClickable(true);//设置可点击的
        view.setVisibility(View.VISIBLE);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
//        boolean xxx= new HasNotchInScreenUtil().hasNotchInScreen(getActivity());
//        Log.i("jimi测试", "刘海屏: xxx"+xxx);
        if (AppConfig.skin9_is_switch) {//如果是从顶部点切换账号来的     替换页面过去  首页显示返回按钮
            Fragment switch_fragment = Fragment.instantiate(getActivity(), JmSwitchLogin9Fragment.class.getName());
            replaceFragmentToActivity(getFragmentManager(), switch_fragment, AppConfig.resourceId(getActivity(), "content", "id"));
        } else {
            newIntView();
        }
    }

    private void newIntView() {

//        mLinearUl = (LinearLayout) getView().findViewById(AppConfig.resourceId(getActivity(), "linear", "id"));
        jm_skin9_user_login = (LinearLayout) getView().findViewById(AppConfig.resourceId(getActivity(), "jm_skin9_user_login", "id"));
        jm_skin9_quick_login = (LinearLayout) getView().findViewById(AppConfig.resourceId(getActivity(), "jm_skin9_quick_login", "id"));
        jm_skin9_guest_login = (LinearLayout) getView().findViewById(AppConfig.resourceId(getActivity(), "jm_skin9_guest_login", "id"));
        jm_skin9_phone_login_btn = (TextView) getView().findViewById(AppConfig.resourceId(getActivity(), "jm_skin9_phone_login_btn", "id"));
        mEdphone = (EditText) getView().findViewById(AppConfig.resourceId(getActivity(), "edit_iphone", "id"));
        mEdphone.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        kefu_help = (ImageView) getView().findViewById(AppConfig.resourceId(getActivity(), "kefu_help", "id"));
        back = (ImageView) getView().findViewById(AppConfig.resourceId(getActivity(), "back", "id"));
        if (mSeference.isExitData()) {
            back.setVisibility(View.VISIBLE);
        } else {
            back.setVisibility(View.GONE);
        }
//        mLinearUl.setOnClickListener(this);
        back.setOnClickListener(this);
        kefu_help.setOnClickListener(this);
        mEdphone.setOnClickListener(this);
        jm_skin9_user_login.setOnClickListener(this);
        jm_skin9_quick_login.setOnClickListener(this);
        jm_skin9_guest_login.setOnClickListener(this);
        jm_skin9_phone_login_btn.setOnClickListener(this);
        if (AppConfig.oneKeyLogin_SecretKey != null && !AppConfig.oneKeyLogin_SecretKey.isEmpty() && !AppConfig.oneKeyLogin_SecretKey.equals("")) {
            jm_skin9_quick_login.setVisibility(View.VISIBLE);
        } else {
            jm_skin9_quick_login.setVisibility(View.GONE);
        }
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            switch (msg.what) {
                case oneKeyLoginFail:
                    Log.i("jimi测试查看", "" + msg.obj);
                    Toast.makeText(getActivity(), msg.obj + "", Toast.LENGTH_SHORT).show();
//                    view.setVisibility(View.VISIBLE);
                    break;
                case AppConfig.FLAG_FAIL:
                    String resultmsg = (String) msg.obj;
                    showMsg(resultmsg);
                    break;
                case AppConfig.GUEST_lOGIN_SUCCESS:

                    Guest guest = (Guest) msg.obj;
                    String noticeUrl = Utils.toBase64url(guest.getShow_url_after_login());

                    if (!TextUtils.isEmpty(guest.getUpass())) {
//                    if (true) {
                        Bundle args = new Bundle();
                        args.putString("username", guest.getUname());
                        args.putString("upass", guest.getUpass());
                        args.putString("msg", guest.getMessage());
                        args.putString("gametoken", guest.getGame_token());
                        args.putString("openid", guest.getOpenid());
                        args.putString("url", noticeUrl);
                        AppConfig.save_guest_end=false;
                        Fragment mJmSetUserFragment = FragmentUtils.getJmSetUserFragment(getActivity(), args);
                        replaceFragmentToActivity(getFragmentManager(), mJmSetUserFragment, AppConfig.resourceId(getActivity(), "content", "id"));
                    } else {
                        Intent intent = new Intent(getActivity(), JmTopLoginTipActivity.class);
                        intent.putExtra("message", guest.getMessage());
                        intent.putExtra("uName", guest.getUname());
                        intent.putExtra("openId", guest.getOpenid());
                        intent.putExtra("token", guest.getGame_token());
                        intent.putExtra("noticeUrl", noticeUrl);
                        intent.putExtra("type", AppConfig.GUEST_lOGIN_SUCCESS);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    break;
                case AppConfig.ONEKEY_lOGIN_SUCCESS:
                    LoginMessage loginMessage = (LoginMessage) msg.obj;
                    Log.i("jimi","测试"+loginMessage.getSetPwdCode());
                    if (loginMessage.getSetPwdCode()!=null&&loginMessage.getSetPwdCode()!=""){//有code验证码   跳到设置密码页
                        Bundle args = new Bundle();
                        // Log.i("kk",mobileUser.getMoblie());
                        AppConfig.save_guest_end=false;
                        args.putString("username", loginMessage.getUname());
                        args.putString("moblie", loginMessage.getMobile());
                        args.putString("code_area", "86");
                        args.putString("code", loginMessage.getSetPwdCode());
                        args.putBoolean("isOneKeyLogin", true);
                        Fragment mJmSetpwdFragment = FragmentUtils.getJmSetpwdFragment(getActivity(), args);
                        replaceFragmentToActivity(getFragmentManager(), mJmSetpwdFragment, AppConfig.resourceId(getActivity(), "content", "id"));
                    } else {
                        Intent oneKeyLoginIntent = new Intent(getActivity(), JmTopLoginTipActivity.class);
                        oneKeyLoginIntent.putExtra("message", loginMessage.getMessage());
                        oneKeyLoginIntent.putExtra("uName", loginMessage.getUname());
                        oneKeyLoginIntent.putExtra("openId", loginMessage.getOpenid());
                        oneKeyLoginIntent.putExtra("token", loginMessage.getGame_token());
                        oneKeyLoginIntent.putExtra("noticeUrl", Utils.toBase64url(loginMessage.getShow_url_after_login()));
                        oneKeyLoginIntent.putExtra("type", AppConfig.ONEKEY_lOGIN_SUCCESS);

                        startActivity(oneKeyLoginIntent);
                        getActivity().finish();
                    }
                    break;
                case AppConfig.CODE_SUCCESS://输手机号  点登录按钮  发请求验证码，发完跳转
                    Fragment mJmUserLoginFragment = Fragment.instantiate(getActivity(), JmSmsLogin9Fragment.class.getName());
                    replaceFragmentToActivity(getFragmentManager(), mJmUserLoginFragment, AppConfig.resourceId(getActivity(), "content", "id"));
                    break;
                case AppConfig.CODE_FAIL:

                    flag = false;
                    j = 0;
                    String result = (String) msg.obj;
                    showMsg(result);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();

        if (id == AppConfig.resourceId(getActivity(), "jm_skin9_user_login", "id")) {//用户登录
            Fragment mJmUserLoginFragment = FragmentUtils.getJmUserLoginFragment(getActivity());
            replaceFragmentToActivity(getFragmentManager(), mJmUserLoginFragment, AppConfig.resourceId(getActivity(), "content", "id"));
        } else if (id == AppConfig.resourceId(getActivity(), "jm_skin9_quick_login", "id")) {//快速(一键)登录
            oneKeyLogin();
        } else if (id == AppConfig.resourceId(getActivity(), "back", "id")) {
            Fragment mJmUserLoginFragment = Fragment.instantiate(getActivity(), JmSwitchLogin9Fragment.class.getName());
            replaceFragmentToActivity(getFragmentManager(), mJmUserLoginFragment, AppConfig.resourceId(getActivity(), "content", "id"));
        } else if (id == AppConfig.resourceId(getActivity(), "kefu_help", "id")) {
            callKefu();
        } else if (id == AppConfig.resourceId(getActivity(), "jm_skin9_guest_login", "id")) {//游客登录
            guestLogin();
        } else if (id == AppConfig.resourceId(getActivity(), "jm_skin9_phone_login_btn", "id")) {//手机号登录
            //透明样式，采用replace替换  正常添加add会因透明显示原本那层fragment
            AppConfig.phone_number = mEdphone.getText().toString();
            startRequestSMS(AppConfig.phone_number, "+86", "1");
        }
    }

    private void oneKeyLogin() {
        view.setVisibility(View.GONE);
        try {

            JmOneKeyLogin jmOneKeyLogin = new JmOneKeyLogin();
            jmOneKeyLogin.invoke(getActivity(), new OneKeyLoginListener() {

                @Override
                public void onSuccess(String obj) {
                    //一键登录成功
                    Log.i("jimisdk", getActivity() + "一键登录成功: " + obj);
                    AppConfig.oneKey_access_token = obj;

                    JmhyApi.get().startOneKeylogin(AppConfig.oneKey_access_token, AppConfig.appKey, new ApiRequestListener() {

                        @Override
                        public void onSuccess(Object obj) {
                            // TODO Auto-generated method stub
                            try {
                                Log.i("jimisdk", "一键登录校验成功" + obj.toString());
                                LoginMessage loginMessage = (LoginMessage) obj;
                                mSeference.saveTimeAndType(loginMessage.getUname(), new SimpleDateFormat("MM月dd日 HH:mm:ss").format(new Date()), "一键登录");
                                mSeference.saveAccount(loginMessage.getUname(), "~~test", loginMessage.getLogin_token());
                                AppConfig.saveMap(loginMessage.getUname(), "~~test", loginMessage.getLogin_token());
                                Utils.saveUserToSd(getActivity());
                                Utils.saveTimeAndTypeToSd(getActivity());
                                sendData(AppConfig.ONEKEY_lOGIN_SUCCESS, obj, handler);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.i("jimi", "一键登录返回数据错误" + e);
                            }
                        }

                        @Override
                        public void onError(int statusCode) {
                            // TODO Auto-generated method stub
                            Log.i("jimisdk", "一键登录校验失败" + statusCode);

                            Message message = handler.obtainMessage();
                            message.what = oneKeyLoginFail;
                            message.obj = "登录校验失败";
                            handler.sendMessage(message);
                        }
                    });
                }

                @Override
                public void showAutoLoginSuccess() {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onError(String msg) {
                    //一键登录失败
                    view.setVisibility(View.VISIBLE);
                    Log.i("jimisdk", "一键登录失败onError: " + msg);
                    Message message = handler.obtainMessage();
                    message.what = oneKeyLoginFail;
                    message.obj = msg;
                    handler.sendMessage(message);
                }

                @Override
                public void onCancle(String msg) {
                    view.setVisibility(View.VISIBLE);
                    Log.i("jimisdk", "一键登录取消: " + msg);
                }
            });
        }catch (Exception e) {
            view.setVisibility(View.GONE);
            e.printStackTrace();
            Log.i("jimi","异常："+e);
            Toast.makeText(getActivity(), "异常："+e, Toast.LENGTH_SHORT).show();
            getActivity().finish();
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

    /**
     * 游客登录
     */
    public void guestLogin() {
//        Fragment mJmSetUserFragment = FragmentUtils.getJmSetUserFragment(getActivity(), args);
//        replaceFragmentToActivity(getFragmentManager(), mJmSetUserFragment, AppConfig.resourceId(getActivity(), "content", "id"));

        mGuestTask = JmhyApi.get().starguestLogin(getActivity(), AppConfig.appKey, new ApiRequestListener() {

            @Override
            public void onSuccess(Object obj) {
                // TODO Auto-generated method stub
                Log.i("测试日志1", "obj" + obj.toString());
                if (obj != null) {
                    Guest guest = (Guest) obj;
                    if (guest.getUpass() != null) {
                        if (!guest.getUpass().equals("")) {
                            JiMiSDK.getStatisticsSDK().onRegister(TAG, true);
                        }
                    }
                    if (guest.getCode().equals("0")) {
                        mSeference.saveTimeAndType(guest.getUname(), new SimpleDateFormat("MM月dd日 HH:mm:ss").format(new Date()), "游客登录");
                        mSeference.saveAccount(guest.getUname(), "~~test", guest.getLogin_token());
                        AppConfig.saveMap(guest.getUname(), "~~test", guest.getLogin_token());
                        Utils.saveUserToSd(getActivity());

                        Utils.saveTimeAndTypeToSd(getActivity());
                        sendData(AppConfig.GUEST_lOGIN_SUCCESS, obj, handler);
                    } else {
                        sendData(AppConfig.FLAG_FAIL, guest.getMessage(), handler);
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
    }


}
