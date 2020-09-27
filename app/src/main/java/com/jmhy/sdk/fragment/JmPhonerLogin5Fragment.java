package com.jmhy.sdk.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.bean.LoginInfo;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.http.Result;
import com.jmhy.sdk.model.LoginMessage;
import com.jmhy.sdk.bean.Registermsg;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.FragmentUtils;
import com.jmhy.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JmPhonerLogin5Fragment extends JmBaseFragment implements
        OnClickListener {

    // 吉米
    private View mBtuser;
    private EditText mEdphone;
    private EditText mPassword;
    private String iphone;
    private String password;
    private View mBtmobilelg;
    private LinearLayout mLinearUl;
    private ImageView mIvkefu;

    private boolean flag = true;
    private int j = 0;

    private Call mSmsTask;
    private Call mobileCall;
    private ApiAsyncTask mGuestTask;

    List<String> moreCountList = new ArrayList<String>();
    List<String> morePwdList = new ArrayList<String>();
    List<String> moreUidList = new ArrayList<String>();
    List<HashMap<String, String>> contentList = new ArrayList<HashMap<String, String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        // 登录
        View view = null;
        view = inflater.inflate(AppConfig.resourceId(getActivity(),
                "jmlogin_main_5", "layout"), container, false);
        view.setClickable(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        intView();

    }

    private void intView() {
        mBtuser = getView().findViewById(
                AppConfig.resourceId(getActivity(), "userlgbt", "id"));
        mBtuser.setOnClickListener(this);
        mEdphone = (EditText) getView().findViewById(
                AppConfig.resourceId(getActivity(), "edit_iphone", "id"));
        mEdphone.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mPassword = (EditText) getView().findViewById(
                AppConfig.resourceId(getActivity(), "edit_password", "id"));
        mPassword.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        mBtmobilelg = getView().findViewById(
                AppConfig.resourceId(getActivity(), "mobilebt", "id"));
        mBtmobilelg.setOnClickListener(this);
        mLinearUl = (LinearLayout) getView().findViewById(
                AppConfig.resourceId(getActivity(), "linear", "id"));
        mIvkefu = (ImageView) getView().findViewById(
                AppConfig.resourceId(getActivity(), "ivkefu", "id"));
        mIvkefu.setOnClickListener(this);

        View agree = getView().findViewById(
                AppConfig.resourceId(getActivity(), "agree", "id"));
        agree.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToIntent(AppConfig.USERAGREEMENTURL);
            }
        });
    }

    private void loginFailed(String code) {
        showMsg(code);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            switch (msg.what) {
                case AppConfig.REGISTER_SUCCESS:

                    break;
                case AppConfig.AUTO_LOGIN_SUCCESS:
                case AppConfig.LOGIN_SUCCESS:
                    LoginMessage result = (LoginMessage) msg.obj;
                    showUserMsg(result.getUname());
                    AppConfig.USERURL = Utils.toBase64url(result
                            .getFloat_url_user_center());

                    String url = Utils
                            .toBase64url(result.getShow_url_after_login());
                    turnToNotice(url);
                    getActivity().finish();
                    break;
            }
        }
    };

    private void autologin(String token) {
        mobileCall = JmhyApi.get().starlAutoLogin(token,
                new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        LoginInfo loginInfo = (LoginInfo) obj;

                        mSeference.saveAccount(loginInfo.getUname(), "~~test",
                                loginInfo.getLogin_token());
                        AppConfig.saveMap(loginInfo.getUname(), "~~test",
                                loginInfo.getLogin_token());
                        Utils.saveUserToSd(getActivity());
                        wrapaLoginInfo("success", "登录成功",
                                loginInfo.getUname(), loginInfo.getOpenid(),
                                loginInfo.getGame_token());
                        sendData(AppConfig.AUTO_LOGIN_SUCCESS, obj,
                                handler);
                    }

                    @Override
                    public void onError(int statusCode) {
                        loginFailed(AppConfig.getString(getActivity(),
                                "http_rror_msg"));
                    }
                });
    }

    private void login(String username, String password) {
        mobileCall = JmhyApi.get().starusreLogin(username, password,
                new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        LoginInfo loginInfo = (LoginInfo) obj;

                        mSeference.saveAccount(loginInfo.getUname(),
                                "~~test", loginInfo.getLogin_token());
                        AppConfig.saveMap(loginInfo.getUname(),
                                "~~test", loginInfo.getLogin_token());
                        Utils.saveUserToSd(getActivity());
                        wrapaLoginInfo("success",
                                "登录成功",
                                loginInfo.getUname(),
                                loginInfo.getOpenid(),
                                loginInfo.getGame_token());
                        sendData(AppConfig.LOGIN_SUCCESS, obj, handler);

                    }

                    @Override
                    public void onError(int statusCode) {
                        loginFailed(AppConfig.getString(getActivity(),
                                "http_rror_msg"));
                    }
                });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == AppConfig.resourceId(getActivity(), "mobilebt", "id")) {
            // 登陆

            iphone = mEdphone.getText().toString();
            password = mPassword.getText().toString().trim();
            if (TextUtils.isEmpty(iphone)) {
                showMsg(AppConfig.getString(getActivity(), "jm_empty_email"));
                return;
            }
            if (TextUtils.isEmpty(password)) {
                showMsg(AppConfig.getString(getActivity(), "user_hintpwd_msg"));
                return;
            }
            startRegister(iphone, password);
        } else if (id == AppConfig.resourceId(getActivity(), "userlgbt", "id")) {
            // 用户登录
            Fragment mJmUserLoginFragment = FragmentUtils.getJmUserLoginFragment(getActivity());
            addFragmentToActivity(getFragmentManager(), mJmUserLoginFragment,
                    AppConfig.resourceId(getActivity(), "content", "id"));
        } else if (id == AppConfig.resourceId(getActivity(), "ivkefu", "id")) {
            // 客服

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("url", AppConfig.KEFU);
            intent.setClass(getActivity(), JmUserinfoActivity.class);
            startActivity(intent);

        }
    }

    /**
     * 从文件中获取数据
     */
    private void insertDataFromFile() {
        // TODO Auto-generated method stub
        moreCountList.clear();
        morePwdList.clear();
        moreUidList.clear();
        Map<String, String> map = new HashMap<String, String>();
        map = mUserinfo.userMap();
        // 判断由于程序出现什么异常导致某些信息没有写入文件系统
        for (int i = 0; i < map.size(); i++) {
            String tU = map.get("user" + i);
            String tempUser = ((tU != null && tU.split(":").length == 3) ? tU
                    .split(":")[0] : "empty");
            String tempPwd = ((tU != null && tU.split(":").length == 3) ? tU
                    .split(":")[1] : "empty");
            String tempUid = ((tU != null && tU.split(":").length == 3) ? tU
                    .split(":")[2] : "empty");

            if (!tempUid.equals("empty") && !tempUser.equals("empty")
                    && !tempPwd.equals("empty")) {
                moreCountList.add(tempUser);
                morePwdList.add(tempPwd);
                moreUidList.add(tempUid);
            }
        }
        for (int i = map.size() - 1; i >= 0; i--) {
            String tU = map.get("user" + i);
            String tempUser = ((tU != null && tU.split(":").length == 3) ? tU
                    .split(":")[0] : "empty");
            String tempPwd = ((tU != null && tU.split(":").length == 3) ? tU
                    .split(":")[1] : "empty");
            String tempUid = ((tU != null && tU.split(":").length == 3) ? tU
                    .split(":")[2] : "empty");
            if (!tempUser.equals("empty") && !tempPwd.equals("empty")
                    && !tempUid.equals("empty")) {
                mSeference.saveAccount(tempUser, tempPwd, tempUid);
            }
        }
    }

    /**
     * 从preference获取数据
     */
    public boolean insertDataFromSerference() {
        moreCountList.clear();
        morePwdList.clear();
        moreUidList.clear();
        contentList = mSeference.getContentList();
        if (contentList == null)
            return false;
        for (int i = 0; i < contentList.size(); i++) {
            moreCountList.add(contentList.get(i).get("account"));
            morePwdList.add(contentList.get(i).get("password"));
            moreUidList.add(contentList.get(i).get("uid"));
        }
        return true;
    }

    /**
     * 获取验证码
     *
     * @param username
     * @param password
     */
    public void startRegister(final String username, final String password) {
        mSmsTask = JmhyApi.get().startRegister(username, password,
                new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        if (obj != null) {
                            Result<Registermsg> registermsgResult = (Result<Registermsg>) obj;
                            if (registermsgResult.getCode()==0) {

                                autologin(registermsgResult.getData().getAuto_login_token());
                            } else if (registermsgResult.getCode()==44202) {
                                login(username, password);
                            } else {
                                loginFailed(registermsgResult.getCode()+"");

                            }
                        }
                    }

                    @Override
                    public void onError(int statusCode) {
                        loginFailed( AppConfig.getString(getActivity(),
                                "http_rror_msg"));
                    }
                });

    }

    @Override
    public void onDestroy() {
        if (mGuestTask != null) {
            mGuestTask.cancel(false);
        }
        if (mSmsTask != null) {
            mSmsTask.cancel();
        }
        if (mobileCall != null) {
            mobileCall.cancel();
        }

        super.onDestroy();
    }
}
