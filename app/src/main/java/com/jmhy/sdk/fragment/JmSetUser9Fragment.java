package com.jmhy.sdk.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmhy.sdk.activity.JmTopLoginTipActivity;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.activity.PermissionActivity;
import com.jmhy.sdk.activity.PermissionActivity.PermissionResultListener;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.utils.MediaUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JmSetUser9Fragment extends JmBaseFragment implements
        OnClickListener {
    private EditText mEtusername;
    private EditText mEtpwd;
    private View mBtgetgame;
    private TextView mTvmsg;
    private ImageView kefu_help;
    private View contentView;
    private ImageView back;
    private CheckBox checkBtn;
    private boolean ischeck =true;
    private String path;
    private File file;
    private String url;

    private String password;
    private String result;
    private String msg;
    private String uname;
    private String gametoken;
    private String openid;
    private TimeCount time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (getArguments() != null) {
            uname = getArguments().getString("username");
            password = getArguments().getString("upass");
            msg = getArguments().getString("msg");
            gametoken = getArguments().getString("gametoken");
            openid = getArguments().getString("openid");
            url = getArguments().getString("url");
        }
        View view = null;
        view = inflater.inflate(AppConfig.resourceId(getActivity(), "jmsetuser_9", "layout"), container, false);
        view.setClickable(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        mEtusername = (EditText) getView().findViewById(AppConfig.resourceId(getActivity(), "edit_user", "id"));
        mEtusername.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mEtusername.setFocusable(false);
        mEtusername.setFocusableInTouchMode(false);
        mEtpwd = (EditText) getView().findViewById(AppConfig.resourceId(getActivity(), "edit_pwd", "id"));
        mEtpwd.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mEtpwd.setFocusable(false);
        mEtpwd.setFocusableInTouchMode(false);
        mBtgetgame = getView().findViewById(AppConfig.resourceId(getActivity(), "btgetgame", "id"));

        contentView = getView().findViewById(AppConfig.resourceId(getActivity(), "content_view", "id"));
        kefu_help = getView().findViewById(AppConfig.resourceId(getActivity(), "kefu_help", "id"));

        mTvmsg = (TextView) getView().findViewById(AppConfig.resourceId(getActivity(), "tvurl", "id"));
        back = (ImageView) getView().findViewById(AppConfig.resourceId(getActivity(), "back", "id"));
        checkBtn = (CheckBox) getView().findViewById(AppConfig.resourceId(getActivity(), "checkBtn", "id"));
        checkBtn.setOnClickListener(this);
        back.setOnClickListener(this);
        mTvmsg.setOnClickListener(this);
        mBtgetgame.setOnClickListener(this);
        kefu_help.setOnClickListener(this);
        mEtpwd.setText(password);
        mEtusername.setText(uname);
        time = new TimeCount(100, 100);//控制弹窗显示时间
        time.start();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == AppConfig.resourceId(getActivity(), "btgetgame", "id")) {
            String edit_password = mEtpwd.getText().toString().trim();
            if (TextUtils.isEmpty(edit_password)) {
                showMsg(AppConfig.getString(getActivity(), "user_hintpwd_msg"));
                return;
            }
            if (ischeck){//设置完账号密码
                AppConfig.save_guest_end=true;
//                Fragment setAccount_fragment = Fragment.instantiate(getActivity(), JmSetAccountActivity9.class.getName());
//                replaceFragmentToActivity(getFragmentManager(), setAccount_fragment, AppConfig.resourceId(getActivity(), "content", "id"));
//
                Intent intent = new Intent(getActivity(), JmTopLoginTipActivity.class);
                intent.putExtra("message",msg);
                intent.putExtra("uName",uname);
                intent.putExtra("openId",openid);
                intent.putExtra("token",gametoken);
                intent.putExtra("noticeUrl",url);
                intent.putExtra("type", AppConfig.GUEST_lOGIN_SUCCESS);
                startActivity(intent);
                getActivity().finish();
            }else{
                showMsg(AppConfig.getString(getActivity(), "jm_tip_user_text_deal_9"));
            }

        } else if (id == AppConfig.resourceId(getActivity(), "kefu_help", "id")) {
            callKefu();
        } else if (id == AppConfig.resourceId(getActivity(), "tvurl", "id")) {
//            turnToIntent(AppConfig.USERAGREEMENTURL);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("url", AppConfig.USERAGREEMENTURL);
            intent.putExtra("protocol", true);
            intent.setClass(getActivity(), JmUserinfoActivity.class);
            startActivity(intent);
        } else if (id == AppConfig.resourceId(getActivity(), "checkBtn", "id")) {
            ischeck= !ischeck;
            checkBtn.setChecked(ischeck);
        } else if (id == AppConfig.resourceId(getActivity(), "back", "id")) {
            Fragment mJmUserLoginFragment = Fragment.instantiate(getActivity(), JmLoginHomePage9Fragment.class.getName());
            replaceFragmentToActivity(getFragmentManager(), mJmUserLoginFragment, AppConfig.resourceId(getActivity(), "content", "id"));
        }

    }

    private void saveCurrentImage() {
        if (VERSION.SDK_INT < VERSION_CODES.Q) {
            List<String> permission = new ArrayList<>();
            permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            PermissionActivity.requestPermission(getActivity(), permission, new PermissionResultListener() {
                @Override
                public void onPermissionResult(boolean grant) {
                    if (!grant) {
                        JiMiSDK.permissionTip(getActivity(), "jm_permission_tip_init");
                    } else {
                        saveCurrentSnapshot();
                    }
                }
            });
        } else {
            saveCurrentSnapshot();
        }
    }

    /**
     * 截屏
     */
    private void saveCurrentSnapshot() {
        if (contentView == null) {
            return;
        }

        // 找到当前页面的跟布局
        contentView.setDrawingCacheEnabled(true);
        contentView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap temBitmap = Bitmap.createBitmap(contentView.getWidth(), contentView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temBitmap);
        contentView.draw(canvas);
        contentView.destroyDrawingCache();

        MediaUtils.saveImage(getActivity(), temBitmap);
        String snapshot = AppConfig.getString(getActivity(), "snapshot_save");
        showMsg(snapshot);
    }

    class TimeCount extends CountDownTimer {
        /**
         * 构造方法
         *
         * @param millisInFuture    总倒计时长 毫秒
         * @param countDownInterval 倒计时间隔
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {// 计时结束
            saveCurrentImage();
        }
    }

}
