package com.jmhy.sdk.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.jmhy.sdk.activity.JmTopLoginTipActivity;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.adapter.UserAdapter;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.LoginMessage;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class JmUserLogin9Fragment extends JmBaseFragment implements
        OnClickListener {
    //
    private TextView mIvregister;
    private TextView tvforgot;
    private EditText mNameEt;
    private EditText mPasswordEt;
    private ImageView back;
    private ImageView eye_psw;
    private ImageView clean_account;
    private View mBtuserlogin;
    private Call mLoginTask;
    private String username = "";
    private String pwd;
    private String logintoke;
    private boolean isclear = true;
    private Rect mRectSrc;
    private PopupWindow popupWindow;
    private ImageView kefu_help;

    private Timer timer;
    List<String> moreCountList = new ArrayList<String>();
    List<String> morePwdList = new ArrayList<String>();
    List<String> moreUidList = new ArrayList<String>();
    private Call mautoLoginTask;
    List<HashMap<String, String>> contentList = new ArrayList<HashMap<String, String>>();
    private UserAdapter mUserAdapter;

    private Call mGuestTask;
    private View view = null;
    private final static int oneKeyLoginFail = 1;
    private final static int oneKeyLoginCheckFail = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        view = inflater.inflate(AppConfig.resourceId(getActivity(),
                "jmuserlogin_9", "layout"), container, false);
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
        timer = new Timer();
        mIvregister = (TextView) getView().findViewById(AppConfig.resourceId(getActivity(), "ivregister", "id"));
        mIvregister.setOnClickListener(this);
        tvforgot = (TextView) getView().findViewById(AppConfig.resourceId(getActivity(), "tvforgot", "id"));
        tvforgot.setOnClickListener(this);
        eye_psw = (ImageView) getView().findViewById(AppConfig.resourceId(getActivity(), "eye_2", "id"));
        eye_psw.setOnClickListener(this);
        clean_account = (ImageView) getView().findViewById(AppConfig.resourceId(getActivity(), "clean_account", "id"));
        clean_account.setOnClickListener(this);
        mNameEt = (EditText) getView().findViewById(AppConfig.resourceId(getActivity(), "edit_user", "id"));
        mNameEt.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mPasswordEt = (EditText) getView().findViewById(AppConfig.resourceId(getActivity(), "edit_pwd", "id"));
        mPasswordEt.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mBtuserlogin = getView().findViewById(AppConfig.resourceId(getActivity(), "userloginbt", "id"));
        mBtuserlogin.setOnClickListener(this);

        kefu_help = (ImageView) getView().findViewById(AppConfig.resourceId(getActivity(), "kefu_help", "id"));
        kefu_help.setOnClickListener(this);
        back = (ImageView) getView().findViewById(AppConfig.resourceId(getActivity(), "back", "id"));
        back.setOnClickListener(this);
        //从缓存里读取数据显示账号密码  皮肤9去除
//        if (mSeference.isExitData()) {
//            String temUser = mSeference.getPreferenceData(
//                    Seference.ACCOUNT_FILE_NAME, Seference.ACCOUNT_1);
//            String temPwd = mSeference.getContentPW(Seference.PASSWORD_1);
//            String temUid = mSeference.getPreferenceData(
//                    Seference.ACCOUNT_FILE_NAME, Seference.UID_1);
////            mNameEt.setText(temUser);
////            mPasswordEt.setText(temPwd);
//            logintoke = temUid;
//            // AppConfig.saveMap(temUser, temPwd, temUid);
//        } else if (mUserinfo.isFile()) {
//            insertDataFromFile();
//            String temUser = moreAccountList.get(0);
//            String temPwd = morePwdList.get(0);
//            String temUid = moreUidList.get(0);
////            mNameEt.setText(temUser);
////            mPasswordEt.setText(temPwd);
//            logintoke = temUid;
//        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            switch (msg.what) {
                case AppConfig.FLAG_FAIL:
                    String resultmsg = (String) msg.obj;
                    showMsg(resultmsg);
                    mPasswordEt.setText("");
                    break;

                case AppConfig.LOGIN_SUCCESS:
                    LoginMessage result = (LoginMessage) msg.obj;
//                    showUserMsg(result.getUname());
                    AppConfig.USERURL = Utils.toBase64url(result
                            .getFloat_url_user_center());
//                    AppConfig.float_url_home_center = Utils.toBase64url(result
//                            .getFloat_url_home_center());
//                    String url = Utils
//                            .toBase64url(result.getShow_url_after_login());
//                    turnToNotice(url);

                    Intent oneKeyLoginIntent = new Intent(getActivity(), JmTopLoginTipActivity.class);
                    oneKeyLoginIntent.putExtra("message", result.getMessage());
                    oneKeyLoginIntent.putExtra("uName", result.getUname());
                    oneKeyLoginIntent.putExtra("openId", result.getOpenid());
                    oneKeyLoginIntent.putExtra("token", result.getGame_token());
                    oneKeyLoginIntent.putExtra("noticeUrl", Utils.toBase64url(result.getShow_url_after_login()));
                    oneKeyLoginIntent.putExtra("type", AppConfig.LOGIN_SUCCESS);
                    startActivity(oneKeyLoginIntent);

                    getActivity().finish();
                    break;
            }
        }
    };

    private void login(final String userName, final String passWord) {
        Log.i("登录---", "login");
        mLoginTask = JmhyApi.get().starusreLogin(userName, passWord, new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        // TODO Auto-generated method stub
                        if (obj != null) {
                            LoginMessage loginMessage = (LoginMessage) obj;
                            if (loginMessage.getCode().equals("0")) {
                                mSeference.saveTimeAndType(loginMessage.getUname(), new SimpleDateFormat("MM月dd日 HH:mm:ss").format(new Date()), "帐号登录");
                                mSeference.saveAccount(loginMessage.getUname(), "~~test", loginMessage.getLogin_token());
                                AppConfig.saveMap(loginMessage.getUname(), "~~test", loginMessage.getLogin_token());
                                Utils.saveUserToSd(getActivity());

                                Utils.saveTimeAndTypeToSd(getActivity());

//                                wrapaLoginInfo("success",
//                                        loginMessage.getMessage(),
//                                        loginMessage.getUname(),
//                                        loginMessage.getOpenid(),
//                                        loginMessage.getGame_token());
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
        if (id == AppConfig.resourceId(getActivity(), "userloginbt", "id")) {
            // 登陆
            username = mNameEt.getText().toString().trim();
            pwd = mPasswordEt.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                showMsg(AppConfig.getString(getActivity(), "user_hintuser_msg"));
                return;

            }
            if (TextUtils.isEmpty(pwd)) {
                showMsg(AppConfig.getString(getActivity(), "user_hintpwd_msg"));
                return;
            }
            //Log.i("kk",pwd);
            if (pwd.equals("~~test")) {
                if (judgeUser(username)) {
                    autoLogin(logintoke);
                } else {
                    login(username, pwd);
                }
            } else {
                login(username, pwd);
            }
        } else if (id == AppConfig.resourceId(getActivity(), "back", "id")) {
            AppConfig.skin9_is_switch=false;
            Fragment mJmUserLoginFragment = Fragment.instantiate(getActivity(), JmLoginHomePage9Fragment.class.getName());
            replaceFragmentToActivity(getFragmentManager(), mJmUserLoginFragment, AppConfig.resourceId(getActivity(), "content", "id"));
        } else if (id == AppConfig.resourceId(getActivity(), "clean_account", "id")) {
            mNameEt.setText("");
            mPasswordEt.setText("");
        } else if (id == AppConfig.resourceId(getActivity(), "kefu_help", "id")) {
            callKefu();
        } else if (id == AppConfig.resourceId(getActivity(), "tvforgot", "id")) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("url", AppConfig.FPWD);
            intent.putExtra("notice", true);
            intent.setClass(getActivity(), JmUserinfoActivity.class);
            startActivity(intent);
        } else if (id == AppConfig.resourceId(getActivity(), "eye_2", "id")) {
            if (mPasswordEt.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                eye_psw.setImageResource(AppConfig.resourceId(getActivity(), "jm_eye_open_9", "drawable"));
            } else {
                mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eye_psw.setImageResource(AppConfig.resourceId(getActivity(), "jm_eye_close_9", "drawable"));
            }
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


    public void autoLogin(String logintoken) {
        mautoLoginTask = JmhyApi.get().starlAutoLogin( logintoken, new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                        // TODO Auto-generated method stub
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
                        // TODO Auto-generated method stub
                        sendData(
                                AppConfig.FLAG_FAIL,
                                AppConfig.getString(getActivity(),
                                        "http_rror_msg"), handler);
                    }
                });
    }

    public boolean judgeUser(String name) {
        if (mSeference.isExitData()) {
            insertDataFromSerference();
        }
        boolean msg = false;
        for (int i = 0; i < moreCountList.size(); i++) {
            if (name.equals(moreCountList.get(i))) {
                msg = true;

            }
        }

        return msg;
    }

    @Override
    public void onDestroy() {
        if (mGuestTask != null) {
            mGuestTask.cancel();
        }
        if (mLoginTask != null) {
            mLoginTask.cancel();
        }
        if (mautoLoginTask != null) {
            mautoLoginTask.cancel();
        }

        super.onDestroy();
    }
}
