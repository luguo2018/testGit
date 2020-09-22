package com.jmhy.sdk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.http.Result;
import com.jmhy.sdk.bean.MobileUser;
import com.jmhy.sdk.bean.Registermsg;
import com.jmhy.sdk.sdk.JmhyApi;

public class JmSetpwd9Fragment extends JmBaseFragment implements OnClickListener {

    private Call mRegisterTask;

    // 吉米
    private ImageView mIvvisitor;
    private ImageView mIvdirect;
    private MobileUser mMobileUser;
    private EditText mTvusername;
    private EditText mEtpwd;
    private CheckBox checkBtn;
    private TextView tvurl;
    private Button mBtlogin;
    private LinearLayout rt_close;

    private String user;
    private String mobile;
    private String password;
    private String code;
    private String code_area;
    private boolean ischeck=true;
    private boolean isOnekeyLogin=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (getArguments() != null) {
            user = getArguments().getString("username");
            mobile = getArguments().getString("moblie");
            code = getArguments().getString("code");
            code_area = getArguments().getString("code_area");
            isOnekeyLogin = getArguments().getBoolean("isOneKeyLogin");
        }
        View view = inflater.inflate(
                AppConfig.resourceId(getActivity(), "jmsetpwd_9", "layout"),
                container, false);
        view.setClickable(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        initView();

        if (savedInstanceState != null) {
            String password = savedInstanceState.getString("password");
            mEtpwd.setText(password);
        }

        JiMiSDK.getStatisticsSDK().onRegister("JiMiSDK", true);
    }

    private void initView() {
        // TODO Auto-generated method stub

        rt_close = (LinearLayout) getView().findViewById(AppConfig.resourceId(getActivity(), "rt_close", "id"));
        rt_close.setOnClickListener(this);
        mTvusername = (EditText) getView().findViewById(AppConfig.resourceId(getActivity(), "tvmobile", "id"));
        mEtpwd = (EditText) getView().findViewById(AppConfig.resourceId(getActivity(), "edit_pwd", "id"));
        mBtlogin = (Button) getView().findViewById(AppConfig.resourceId(getActivity(), "iphonebtlg", "id"));
        checkBtn = (CheckBox) getView().findViewById(AppConfig.resourceId(getActivity(), "checkBtn", "id"));
        tvurl = (TextView) getView().findViewById(AppConfig.resourceId(getActivity(), "tvurl", "id"));
        tvurl.setOnClickListener(this);
        checkBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ischeck=!ischeck;
                checkBtn.setChecked(ischeck);
            }
        });
        mEtpwd.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mBtlogin.setOnClickListener(this);
        mTvusername.setText(user);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == AppConfig.resourceId(getActivity(), "iphonebtlg", "id")) {
            password = mEtpwd.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                showMsg(AppConfig.getString(getActivity(), "user_hintpwd_msg"));
                return;
            }

            if (ischeck){
                // 返回数据给游戏
                AppConfig.save_guest_end=true;
                getMobileRegister(password);
            }else{
                showMsg(AppConfig.getString(getActivity(), "jm_tip_user_text_deal_9"));
            }

        }
        else if (id == AppConfig.resourceId(getActivity(), "tvurl", "id")) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("url", AppConfig.USERAGREEMENTURL);
            intent.putExtra("protocol", true);
            intent.setClass(getActivity(), JmUserinfoActivity.class);
            startActivity(intent);
        }
        else if (id == AppConfig.resourceId(getActivity(), "rt_close", "id")) {
            getActivity().finish();
        }
    }

    //手机号注册
    public void getMobileRegister(final String password) {
        mRegisterTask = JmhyApi.get().startMobileRegister(
                user, password, mobile, code, code_area, new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        Result<Registermsg> registermsgResult = (Result<Registermsg>) obj;
                        Registermsg registermsg = registermsgResult.data;
                        //	Log.i("kk","Auto"+registermsg.getAuto_login_token());
                        mSeference.saveAccount(user, "~~test",
                                registermsg.getAuto_login_token());
                        AppConfig.saveMap(user, "~~test",
                                registermsg.getAuto_login_token());
                        toAutologin(registermsg);
                        getActivity().finish();
                    }

                    @Override
                    public void onError(int statusCode) {
                        showMsg(statusCode+"");

                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        String password = mEtpwd.getText().toString();
        outState.putString("password", password);

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel();
        }

        super.onDestroy();
    }
}
