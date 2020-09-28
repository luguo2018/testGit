package com.jmhy.sdk.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.Guest;
import com.jmhy.sdk.model.LoginMessage;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.FragmentUtils;
import com.jmhy.sdk.utils.Utils;

/**
 * create by yhz on 2018/8/13
 * 英文版邮箱登录
 */
public class EmailLoginFragment extends JmBaseFragment {
    private EditText email, password;
    private Call apiAsyncTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(AppConfig.resourceId(getActivity(),
                "jmlogin_main_en", "layout"), container, false);
        view.setClickable(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = (EditText)view.findViewById(AppConfig.resourceId(getActivity(), "email", "id"));
        password = (EditText)view.findViewById(AppConfig.resourceId(getActivity(), "password", "id"));
        View forget = view.findViewById(AppConfig.resourceId(getActivity(), "forget", "id"));
        View register = view.findViewById(AppConfig.resourceId(getActivity(), "register", "id"));
        View submit = view.findViewById(AppConfig.resourceId(getActivity(), "submit", "id"));
        password.setTypeface(Typeface.DEFAULT);

        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = Fragment.instantiate(getActivity(), EmailRegisterFragment.class.getName());
                addFragmentToActivity(getFragmentManager(),
                        fragment,
                        AppConfig.resourceId(getActivity(), "content", "id"));
            }
        });
        forget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("url", AppConfig.FPWD);
                intent.setClass(getActivity(), JmUserinfoActivity.class);
                startActivity(intent);
            }
        });
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    @Override
    public void onDestroy() {
        if(apiAsyncTask != null){
            apiAsyncTask.cancel();
        }

        super.onDestroy();
    }

    private void login(){
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        if (TextUtils.isEmpty(emailText)) {
            showMsg(AppConfig.getString(getActivity(), "user_hintuser_msg"));
            return;
        }

        if (TextUtils.isEmpty(passwordText)) {
            showMsg(AppConfig.getString(getActivity(), "user_hintpwd_msg"));
            return;
        }
        login(emailText, passwordText);
    }

    private void login(final String username, final String password){
        apiAsyncTask = JmhyApi.get().starusreLogin( username, password,
                new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        if (obj != null) {
                            LoginMessage loginMessage = (LoginMessage) obj;

                            if (loginMessage.getCode().equals("0")) {
                                mSeference.saveAccount(loginMessage.getUname(),
                                        "~~test", loginMessage.getLogin_token());
                                AppConfig.saveMap(loginMessage.getUname(),
                                        "~~test", loginMessage.getLogin_token());
                                Utils.saveUserToSd(getActivity());
                                wrapaLoginInfo("success",
                                        loginMessage.getMessage(),
                                        loginMessage.getUname(),
                                        loginMessage.getOpenid(),
                                        loginMessage.getGame_token());
                                sendData(AppConfig.LOGIN_SUCCESS, obj, handler);
                            } else {
                                sendData(AppConfig.FLAG_FAIL,
                                        loginMessage.getMessage(), handler);
                            }
                        } else {
                            sendData(AppConfig.FLAG_FAIL, AppConfig.getString(
                                    getActivity(), "http_rror_msg"), handler);
                        }
                    }

                    @Override
                    public void onError(int statusCode) {
                        sendData(
                                AppConfig.FLAG_FAIL,
                                AppConfig.getString(getActivity(),
                                        "http_rror_msg"), handler);
                    }
                });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.FLAG_FAIL:
                    String resultmsg = (String) msg.obj;
                    showMsg(resultmsg);
                    break;
                /*case AppConfig.FLAG_SHOW_POPWINDOW:
                    mRiphoen.getGlobalVisibleRect(mRectSrc);
                    int x = mRectSrc.centerX() - mRectSrc.width() / 2;
                    int y = mRectSrc.centerY() + mRectSrc.height() / 2;
                    popupWindow.showAtLocation(mLinearUl, Gravity.NO_GRAVITY, x, y);
                    break;*/
                case AppConfig.LOGIN_SUCCESS:
                    LoginMessage result = (LoginMessage) msg.obj;
                    showUserMsg(result.getUname());
                    AppConfig.USERURL = Utils.toBase64url(result
                            .getFloat_url_user_center());

                    String url = Utils.toBase64url(result.getShow_url_after_login());
                    turnToIntent(url);
                    getActivity().finish();
                    break;
                case AppConfig.GUEST_lOGIN_SUCCESS:
                    Guest guest = (Guest) msg.obj;
                    String murl = Utils
                            .toBase64url(guest.getShow_url_after_login());

                    if (!TextUtils.isEmpty(guest.getUpass())) {

                        Bundle args = new Bundle();
                        // Log.i("kk",mobileUser.getMoblie())
                        args.putString("username", guest.getUname());
                        args.putString("upass", guest.getUpass());
                        args.putString("msg", guest.getMessage());
                        args.putString("gametoken", guest.getGame_token());
                        args.putString("openid", guest.getOpenid());
                        args.putString("url", murl);
                        Fragment mJmSetUserFragment = FragmentUtils.getJmSetUserFragment(getActivity(), args);
                        addFragmentToActivity(getFragmentManager(),
                                mJmSetUserFragment, AppConfig.resourceId(
                                        getActivity(), "content", "id"));
                    } else {

                        wrapaLoginInfo("success", guest.getMessage(),
                                guest.getUname(), guest.getOpenid(),
                                guest.getGame_token());

                        turnToNotice(murl);
                        getActivity().finish();
                    }
                    break;
            }
        }
    };
}
