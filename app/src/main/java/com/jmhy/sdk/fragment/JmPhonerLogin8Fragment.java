package com.jmhy.sdk.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.bean.Guest;
import com.jmhy.sdk.bean.MobileUser;
import com.jmhy.sdk.http.Result;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.FragmentUtils;
import com.jmhy.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JmPhonerLogin8Fragment extends JmBaseFragment implements
        OnClickListener {

    // 吉米
    private View mBtuser;
    private TextView mIvregister;
    private TextView mTitleTv;
    private EditText mEdphone;
    private EditText mEdcode;
    private String iphone;
    private String code;
    private TextView mIbcode;
    private TextView mBtmobilelg;
    private LinearLayout mLinearUl;
    private TextView mTvistor;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        // 登录
        View view = null;
        view = inflater.inflate(AppConfig.resourceId(getActivity(),
                "jmlogin_main_8", "layout"), container, false);
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

        mTvagreement = (TextView) getView().findViewById(
                AppConfig.resourceId(getActivity(), "tvurl", "id"));
        mTvagreement.setOnClickListener(this);

        mTitleTv = (TextView) getView().findViewById(
                AppConfig.resourceId(getActivity(), "title_tv", "id"));
        mBtuser = getView().findViewById(
                AppConfig.resourceId(getActivity(), "userlgbt", "id"));
        mBtuser.setOnClickListener(this);
        mIvregister = (TextView) getView().findViewById(
                AppConfig.resourceId(getActivity(), "ivregister", "id"));
        mIvregister.setOnClickListener(this);
        mEdphone = (EditText) getView().findViewById(
                AppConfig.resourceId(getActivity(), "edit_iphone", "id"));
        mEdphone.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mEdcode = (EditText) getView().findViewById(
                AppConfig.resourceId(getActivity(), "edit_code", "id"));
        mEdcode.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mIbcode = (TextView) getView().findViewById(
                AppConfig.resourceId(getActivity(), "ibcode", "id"));
        mIbcode.setOnClickListener(this);

        mBtmobilelg = (TextView) getView().findViewById(
                AppConfig.resourceId(getActivity(), "mobilebt", "id"));
        mBtmobilelg.setOnClickListener(this);
        mLinearUl = (LinearLayout) getView().findViewById(
                AppConfig.resourceId(getActivity(), "linear", "id"));
        mTvistor = (TextView) getView().findViewById(
                AppConfig.resourceId(getActivity(), "tvvistor", "id"));
        mTvistor.setOnClickListener(this);

        if (AppConfig.isRegister) {
            Log.e("jimisdk", "isRegister = true");

            //手机注册
            mTitleTv.setText(AppConfig.getString(getActivity(), "moblie_bt_register"));
            //手机登录
            mIvregister.setText(AppConfig.getString(getActivity(), "moblie_login_title"));
            //注册
            mBtmobilelg.setText(AppConfig.getString(getActivity(), "moblie_bt_register"));
        } else {
            Log.e("jimisdk", "isRegister = false");
            mIvregister.setVisibility(View.GONE);

        }


        if (AppConfig.is_visitor_on_phone.equals("0")) {
            mTvistor.setVisibility(View.INVISIBLE);
        }
        mIvkefu = (ImageView) getView().findViewById(
                AppConfig.resourceId(getActivity(), "ivkefu", "id"));
        mIvkefu.setOnClickListener(this);

        TextView mTvversion = (TextView) getView().findViewById(AppConfig.resourceId(getActivity(),
                "tvversion", "id"));
        mTvversion.setText(String.format("v%s", AppConfig.SDK_VER));
    }
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
                        MobileUser mobileUser = (MobileUser) obj;
						flag = false;
						if (mobileUser.getPhone_register().equals("1")) {
							// 跳转设置密码
							Bundle args = new Bundle();
							// Log.i("kk",mobileUser.getMoblie());
							args.putString("username", mobileUser.getUname());
							args.putString("moblie", mobileUser.getMobile());
							args.putString("code_area", mobileUser.getCode_area());
							args.putString("code", mobileUser.getCode());
							Fragment mJmSetpwdFragment = FragmentUtils.getJmSetpwdFragment(getActivity(), args);
							addFragmentToActivity(getFragmentManager(),
									mJmSetpwdFragment, AppConfig.resourceId(
											getActivity(), "content", "id"));

						} else {
							// 直接登录成功，返回数据
							mSeference.saveAccount(mobileUser.getUname(), "~~test",
									mobileUser.getLogin_token());
							AppConfig.saveMap(mobileUser.getUname(), "~~test",
									mobileUser.getLogin_token());
							Utils.saveUserToSd(getActivity());
							wrapaLoginInfo("success", "登录成功",
									mobileUser.getUname(), mobileUser.getOpenid(),
									mobileUser.getGame_token());
							showUserMsg(mobileUser.getUname());
							AppConfig.USERURL = Utils.toBase64url(mobileUser
									.getFloat_url_user_center());
							String url = Utils.toBase64url(mobileUser
									.getShow_url_after_login());
							turnToIntent(url);
							getActivity().finish();

						}
                    }

                    public void onError(int statusCode) {
						showMsg(AppConfig.getString(getActivity(),
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
            code = mEdcode.getText().toString().trim();
            if (TextUtils.isEmpty(iphone)) {
                showMsg(AppConfig.getString(getActivity(), "moblie_edit_hint"));
                return;
            }
            if (TextUtils.isEmpty(code)) {
                showMsg(AppConfig.getString(getActivity(), "user_hintcode_msg"));
                return;
            }
            login(iphone, "+86", code);
        } else if (id == AppConfig.resourceId(getActivity(), "ibcode", "id")) {
            // 获取验证码
            iphone = mEdphone.getText().toString();
            if (TextUtils.isEmpty(iphone)) {
                showMsg(AppConfig.getString(getActivity(), "moblie_edit_hint"));
                return;
            }

            startRequestSMS(iphone, "+86", "1");

            flag = true;
            mIbcode.setClickable(false);


        } else if (id == AppConfig.resourceId(getActivity(), "userlgbt", "id")) {
            // 用户登录
            Fragment mJmUserLoginFragment = FragmentUtils.getJmUserLoginFragment(getActivity());
            addFragmentToActivity(getFragmentManager(), mJmUserLoginFragment,
                    AppConfig.resourceId(getActivity(), "content", "id"));
        } else if (id == AppConfig
                .resourceId(getActivity(), "ivregister", "id")) {
            // 用户注册
            Fragment mJmUserRegisterFragment = FragmentUtils.getJmUserLoginFragment(getActivity());
            addFragmentToActivity(getFragmentManager(),
                    mJmUserRegisterFragment,
                    AppConfig.resourceId(getActivity(), "content", "id"));
            AppConfig.isgoto = true;
            AppConfig.isRegister = false;

        } else if (id == AppConfig.resourceId(getActivity(), "tvvistor", "id")) {
            // 游客账号
            getGuest();
        } else if (id == AppConfig.resourceId(getActivity(), "ivkefu", "id")) {
            // 客服

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("url", AppConfig.KEFU);
            intent.setClass(getActivity(), JmUserinfoActivity.class);
            startActivity(intent);

        } else if (id == AppConfig.resourceId(getActivity(), "tvurl", "id")) {
            // 用户协议
            // Log.i("kk","用户协议"+AppConfig.USERAGREEMENTURL);
            turnToIntent(AppConfig.USERAGREEMENTURL);
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
						Result result = (Result) obj;
						mEdcode.requestFocus();
						showMsg(result.getMessage());
                    }

                    @Override
                    public void onError(int statusCode) {
						showMsg(AppConfig.getString(getActivity(),
								"http_rror_msg"));
                    }
                });

    }

    /**
     * 游客登录
     */
    public void getGuest() {
        mGuestTask = JmhyApi.get().starguestLogin(getActivity(),
                AppConfig.appKey, new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        Guest guest = (Guest) obj;
                        Log.i("测试日志", "guest.getUpass():" + guest.getUpass() + "----getIs_package_new：" + guest.getIs_package_new());
//							if (!guest.getUpass().equals("")||guest.getIs_package_new().equals("1")){
                        if (!guest.getUpass().equals("")) {
                            JiMiSDK.getStatisticsSDK().onRegister("JiMiSDK", true);
                        }
                        mSeference.saveAccount(guest.getUname(),
                                "~~test", guest.getLogin_token());
                        AppConfig.saveMap(guest.getUname(), "~~test",
                                guest.getLogin_token());
                        Utils.saveUserToSd(getActivity());
                        String murl = Utils
                                .toBase64url(guest.getShow_url_after_login());

                        if (!TextUtils.isEmpty(guest.getUpass())) {
                            Bundle args = new Bundle();
                            // Log.i("kk",mobileUser.getMoblie())
                            args.putString("username", guest.getUname());
                            args.putString("upass", guest.getUpass());
                            args.putString("msg", "登录成功");
                            args.putString("gametoken", guest.getGame_token());
                            args.putString("openid", guest.getOpenid());
                            args.putString("url", murl);
                            Fragment mJmSetUserFragment = FragmentUtils.getJmSetUserFragment(getActivity(), args);
                            addFragmentToActivity(getFragmentManager(),
                                    mJmSetUserFragment, AppConfig.resourceId(
                                            getActivity(), "content", "id"));
                        } else {

                            wrapaLoginInfo("success", "登录成功",
                                    guest.getUname(), guest.getOpenid(),
                                    guest.getGame_token());

                            turnToNotice(murl);
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onError(int statusCode) {
						showMsg(AppConfig.getString(getActivity(),
								"http_rror_msg"));
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

        j = 0;
    }

}
