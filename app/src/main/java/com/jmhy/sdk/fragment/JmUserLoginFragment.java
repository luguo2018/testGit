package com.jmhy.sdk.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.jmhy.sdk.activity.JmCommunityActivity;
import com.jmhy.sdk.activity.JmLoginActivity;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.adapter.UserAdapter;
import com.jmhy.sdk.adapter.UserAdapter.InnerItemOnclickListener;
import com.jmhy.sdk.bean.LoginInfo;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.bean.Guest;
import com.jmhy.sdk.model.LoginMessage;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.FragmentUtils;
import com.jmhy.sdk.utils.Seference;
import com.jmhy.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class JmUserLoginFragment extends JmBaseFragment implements
        OnClickListener {
    // 吉米
    private View mBtiphoe;
    private TextView mIvregister;
    private EditText mNameEt;
    private EditText mPasswordEt;
    private ImageButton mIuserlist;
    private Button mBtuserlogin;
    private Call mLoginTask;
    private String username = "";
    private String pwd;
    private String logintoke;
    private boolean isclear = true;
    private Rect mRectSrc;
    private PopupWindow popupWindow;
    private RelativeLayout mRelative;
    private ImageView mIvkefu;
    private TextView mTvforgot;
    private TextView mTvversion;
    private View contentView;

    private Timer timer;
    List<String> moreCountList = new ArrayList<String>();
    List<String> morePwdList = new ArrayList<String>();
    List<String> moreUidList = new ArrayList<String>();
    private Call mautoLoginTask;
    private TextView mTvistor;
    List<HashMap<String, String>> contentList = new ArrayList<HashMap<String, String>>();
    private UserAdapter mUserAdapter;

    private Call mGuestTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        // 登录
        View view = null;
        view = inflater.inflate(AppConfig.resourceId(getActivity(),
                "jmuserlogin", "layout"), container, false);
        view.setClickable(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        intView();

        if (savedInstanceState != null) {
            String account = savedInstanceState.getString("account");
            String password = savedInstanceState.getString("password");
            mNameEt.setText(account);
            mPasswordEt.setText(password);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (contentView == null || contentView.getLayoutParams().height <= 0) {
            return;
        }

        JmLoginActivity activity = (JmLoginActivity) getActivity();
        activity.resetContentViewSize(contentView.getLayoutParams().height);
    }

    private void intView() {
        timer = new Timer();
        mBtiphoe = getView().findViewById(
                AppConfig.resourceId(getActivity(), "iphonebtlg", "id"));
        mBtiphoe.setOnClickListener(this);
        mIvregister = (TextView) getView().findViewById(
                AppConfig.resourceId(getActivity(), "ivregister", "id"));
        mIvregister.setOnClickListener(this);
        mNameEt = (EditText) getView().findViewById(
                AppConfig.resourceId(getActivity(), "edit_user", "id"));
        mNameEt.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mPasswordEt = (EditText) getView().findViewById(
                AppConfig.resourceId(getActivity(), "edit_pwd", "id"));
        mPasswordEt.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mIuserlist = (ImageButton) getView().findViewById(
                AppConfig.resourceId(getActivity(), "ibpulldown", "id"));
        mIuserlist.setOnClickListener(this);
        mBtuserlogin = (Button) getView().findViewById(
                AppConfig.resourceId(getActivity(), "userloginbt", "id"));
        mBtuserlogin.setOnClickListener(this);
        mRelative = (RelativeLayout) getView().findViewById(
                AppConfig.resourceId(getActivity(), "userpd", "id"));
        mIvkefu = (ImageView) getView().findViewById(
                AppConfig.resourceId(getActivity(), "ivkefu", "id"));
        mIvkefu.setOnClickListener(this);

        mTvistor = (TextView) getView().findViewById(
                AppConfig.resourceId(getActivity(), "tvvistor", "id"));
        mTvistor.setOnClickListener(this);
        mTvforgot = (TextView) getView().findViewById(
                AppConfig.resourceId(getActivity(), "tvforgot", "id"));
        mTvforgot.setOnClickListener(this);
        if (AppConfig.is_visitor_on.equals("0")) {
            mTvistor.setVisibility(View.INVISIBLE);
        }
        mTvversion = (TextView) getView().findViewById(AppConfig.resourceId(getActivity(),
                "tvversion", "id"));
        contentView = getView().findViewById(
                AppConfig.resourceId(getActivity(), "content_view", "id"));

        mTvversion.setText("v" + AppConfig.SDK_VER);
        if (mSeference.isExitData()) {
            String temUser = mSeference.getPreferenceData(
                    Seference.ACCOUNT_FILE_NAME, Seference.ACCOUNT_1);
            String temPwd = mSeference.getContentPW(Seference.PASSWORD_1);
            String temUid = mSeference.getPreferenceData(
                    Seference.ACCOUNT_FILE_NAME, Seference.UID_1);
            mNameEt.setText(temUser);
            mPasswordEt.setText(temPwd);
            logintoke = temUid;
            // AppConfig.saveMap(temUser, temPwd, temUid);

        } else if (mUserinfo.isFile()) {
            insertDataFromFile();
            String temUser = moreCountList.get(0);
            String temPwd = morePwdList.get(0);
            String temUid = moreUidList.get(0);
            mNameEt.setText(temUser);
            mPasswordEt.setText(temPwd);
            logintoke = temUid;
        }

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
                    showUserMsg(result.getUname());

                    String url = Utils
                            .toBase64url(result.getShow_url_after_login());
                    turnToNotice(url);
                    getActivity().finish();
                    break;
                case AppConfig.FLAG_SHOW_POPWINDOW:
                    mNameEt.getGlobalVisibleRect(mRectSrc);
                    int x = mRectSrc.centerX() - mRectSrc.width() / 2;
                    int y = mRectSrc.centerY() + mRectSrc.height() / 2;
                    popupWindow.showAtLocation(mRelative, Gravity.NO_GRAVITY, x, y);
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
                    break;
            }
        }
    };

    private void login(final String userName, final String passWord) {

        mLoginTask = JmhyApi.get().starusreLogin(userName, passWord, new ApiRequestListener() {

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
                showUserMsg(loginInfo.getUname());
                AppConfig.USERURL = Utils.toBase64url(loginInfo
                        .getFloat_url_user_center());
                String url = Utils
                        .toBase64url(loginInfo.getShow_url_after_login());
                turnToNotice(url);
                getActivity().finish();
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
        } else if (id == AppConfig
                .resourceId(getActivity(), "iphonebtlg", "id")) {
            Fragment mJmPhonerLoginFragment = FragmentUtils.getJmPhonerLoginFragment(getActivity());
            addFragmentToActivity(getFragmentManager(), mJmPhonerLoginFragment,
                    AppConfig.resourceId(getActivity(), "content", "id"));

        } else if (id == AppConfig
                .resourceId(getActivity(), "ivregister", "id")) {
            // 用户注册
            Fragment mJmUserRegisterFragment = FragmentUtils.getJmUserRegisterFragment(getActivity());
            addFragmentToActivity(getFragmentManager(),
                    mJmUserRegisterFragment,
                    AppConfig.resourceId(getActivity(), "content", "id"));
            AppConfig.isgoto = false;

        } else if (id == AppConfig.resourceId(getActivity(), "ivkefu", "id")) {
            // 客服

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("url", AppConfig.KEFU);
            intent.setClass(getActivity(), JmUserinfoActivity.class);
            startActivity(intent);

        } else if (id == AppConfig
                .resourceId(getActivity(), "ibpulldown", "id")) {

            if (mSeference.isExitData()) {
                inipopWindow();
                timer.schedule(new myPopupWindow(), 5);
            } else {
                String tip = AppConfig.getString(getActivity(), "login_tip");
                if (mUserinfo.isFile()) {
                    insertDataFromSerference();
                    if (moreCountList.size() > 0) {
                        inipopWindow();
                        timer.schedule(new myPopupWindow(), 5);
                    }
                    showMsg(tip);
                } else {
                    showMsg(tip);
                }
            }

        } else if (id == AppConfig.resourceId(getActivity(), "tvvistor", "id")) {
            // 游客账号
            getGuest();

        } else if (id == AppConfig.resourceId(getActivity(), "tvforgot", "id")) {
            // 找回密码
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("url", AppConfig.FPWD);
            intent.setClass(getActivity(), JmCommunityActivity.class);
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

    private void inipopWindow() {
        // 加载popupWindow的布局文件
        mRectSrc = new Rect();
        mNameEt.getGlobalVisibleRect(mRectSrc);
        final View contentView = LayoutInflater.from(getActivity()).inflate(
                AppConfig.resourceId(getActivity(), "jmpopwindow", "layout"),
                null);
        contentView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                return false;
            }
        });
        if (mSeference.isExitData()) {
            insertDataFromSerference();
        } else {
            if (isclear) {
                insertDataFromFile();
            }
        }
        ListView myListView = (ListView) contentView.findViewById(AppConfig
                .resourceId(getActivity(), "poplist", "id"));
        /*
         * myListView.setAdapter(new ArrayAdapter<String>(getActivity(),
         * AppConfig .resourceId(getActivity(), "jmitemcountlist", "layout"),
         * AppConfig.resourceId(getActivity(), "TextView", "id"),
         * moreCountList));
         */
        mUserAdapter = new UserAdapter(getActivity(), moreCountList);
        myListView.setAdapter(mUserAdapter);
        mUserAdapter
                .setOnInnerItemOnClickListener(new InnerItemOnclickListener() {

                    @Override
                    public void itemClick(int position) {
                        // TODO Auto-generated method stub
                        String name = mNameEt.getText().toString().trim();
                        if (name.equals(moreCountList.get(position))) {
                            mNameEt.setText("");
                            mPasswordEt.setText("");
                        }
                        mSeference.clearingAccount(moreCountList.get(position));
                        moreCountList.remove(position);
                        moreUidList.remove(position);
                        mUserAdapter.notifyDataSetChanged();
                        isclear = false;
                        Utils.saveUserToSd(getActivity());
                    }
                });
        myListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mNameEt.setText(moreCountList.get(position));
                mPasswordEt.setText(morePwdList.get(position));
                logintoke = moreUidList.get(position);
                popupWindow.dismiss();
            }

        });
        popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(AppConfig.resourceId(getActivity(),
                "jm_popwindow_anim_style", "style"));
        popupWindow.setWidth(mRectSrc.width());
    }

    /**
     * 弹出一个窗口
     */
    private class myPopupWindow extends TimerTask {

        @Override
        public void run() {
            Message message = new Message();
            message.what = AppConfig.FLAG_SHOW_POPWINDOW;
            handler.sendMessage(message);
        }
    }

    public void autoLogin(String logintoken) {
        mautoLoginTask = JmhyApi.get().starlAutoLogin(logintoken, new ApiRequestListener() {
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
                showUserMsg(loginInfo.getUname());
                AppConfig.USERURL = Utils.toBase64url(loginInfo
                        .getFloat_url_user_center());
                String url = Utils
                        .toBase64url(loginInfo.getShow_url_after_login());
                turnToNotice(url);
                getActivity().finish();
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
    public void getGuest() {
        mGuestTask = JmhyApi.get().starguestLogin(getActivity(),
                AppConfig.appKey, new ApiRequestListener() {

                    @Override
                    public void onSuccess(Object obj) {
                            Guest guest = (Guest) obj;
                            mSeference.saveAccount(guest.getUname(),
                                    "~~test", guest.getLogin_token());
                            AppConfig.saveMap(guest.getUname(), "~~test",
                                    guest.getLogin_token());
                            Utils.saveUserToSd(getActivity());
                            sendData(AppConfig.GUEST_lOGIN_SUCCESS, obj,
                                    handler);
                        }

                        @Override
                        public void onError ( int statusCode){
                            // TODO Auto-generated method stub
                            sendData(
                                    AppConfig.FLAG_FAIL,
                                    AppConfig.getString(getActivity(),
                                            "http_rror_msg"), handler);
                        }
                    });

                }

        public boolean judgeUser (String name){
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
        public void onSaveInstanceState (Bundle outState){
            String account = mNameEt.getText().toString();
            String password = mPasswordEt.getText().toString();
            outState.putString("account", account);
            outState.putString("password", password);

            super.onSaveInstanceState(outState);
        }

        @Override
        public void onDestroy () {
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
