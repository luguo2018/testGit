package com.jmhy.sdk.fragment;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.Guest;
import com.jmhy.sdk.model.Registermsg;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.Utils;

/**
 * create by yhz on 2018/8/13
 * 英文版邮箱注册
 */
public class EmailRegisterFragment extends JmBaseFragment {
    private EditText email, password;
    private Call apiAsyncTask, mGuestTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(AppConfig.resourceId(getActivity(),
                "jmuserregister_en", "layout"), container, false);
        view.setClickable(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = (EditText) view.findViewById(AppConfig.resourceId(getActivity(), "email", "id"));
        password = (EditText) view.findViewById(AppConfig.resourceId(getActivity(), "password", "id"));
        View visitorLogin = view.findViewById(AppConfig.resourceId(getActivity(), "visitor_login", "id"));
        View autoLogin = view.findViewById(AppConfig.resourceId(getActivity(), "auto_login", "id"));
        View submit = view.findViewById(AppConfig.resourceId(getActivity(), "submit", "id"));
        password.setTypeface(Typeface.DEFAULT);

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        visitorLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                guestLogin();
            }
        });
        autoLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (apiAsyncTask != null) {
            apiAsyncTask.cancel();
        }

        if (mGuestTask != null) {
            mGuestTask.cancel();
        }

        super.onDestroy();
    }

    public void guestLogin() {
        mGuestTask = JmhyApi.get().starguestLogin(getActivity(),
                AppConfig.appKey, new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        // TODO Auto-generated method stub
                        if (obj != null) {
                            Guest guest = (Guest) obj;
                            if (guest.getCode().equals("0")) {
                                mSeference.saveAccount(guest.getUname(),
                                        "~~test", guest.getLogin_token());
                                AppConfig.saveMap(guest.getUname(), "~~test",
                                        guest.getLogin_token());
                                Utils.saveUserToSd(getActivity());
                                sendData(AppConfig.GUEST_lOGIN_SUCCESS, obj,
                                        handler);
                            } else {
                                sendData(AppConfig.FLAG_FAIL,
                                        guest.getMessage(), handler);
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

    private void register() {
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
        register(emailText, passwordText);
    }

    private void register(final String username, final String password) {
        apiAsyncTask = JmhyApi.get().startRegister(
                username, password, new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        if (obj != null) {
                            Registermsg registermsg = (Registermsg) obj;
                            if (registermsg.getCode().equals("0")) {
                                mSeference.saveAccount(username, "~~test",
                                        registermsg.getAuto_login_token());
                                AppConfig.saveMap(username, "~~test",
                                        registermsg.getAuto_login_token());
                                sendData(AppConfig.REGISTER_SUCCESS, obj,
                                        handler);
                            } else {
                                sendData(AppConfig.FLAG_FAIL,
                                        registermsg.getMessage(), handler);
                            }
                        } else {
                            sendData(AppConfig.FLAG_FAIL, AppConfig.getString(
                                    getActivity(), "http_rror_msg"), handler);
                        }
                    }

                    @Override
                    public void onError(int statusCode) {
                        sendData(AppConfig.FLAG_FAIL,
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
                case AppConfig.REGISTER_SUCCESS:
                    Registermsg registermsg = (Registermsg) msg.obj;

                    toAutologin(registermsg);

                    getActivity().finish();
                    break;
                case AppConfig.GUEST_lOGIN_SUCCESS:
                    Guest guest = (Guest) msg.obj;
                    String murl = Utils
                            .toBase64url(guest.getShow_url_after_login());

                    if (!TextUtils.isEmpty(guest.getUpass())) {

                        Fragment fragment = Fragment.instantiate(getActivity(), VisitorFragment.class.getName());
                        Bundle args = new Bundle();
                        // Log.i("kk",mobileUser.getMoblie())
                        args.putString("username", guest.getUname());
                        args.putString("upass", guest.getUpass());
                        args.putString("msg", guest.getMessage());
                        args.putString("gametoken", guest.getGame_token());
                        args.putString("openid", guest.getOpenid());
                        args.putString("url", murl);
                        fragment.setArguments(args);
                        addFragmentToActivity(getFragmentManager(),
                                fragment, AppConfig.resourceId(
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
