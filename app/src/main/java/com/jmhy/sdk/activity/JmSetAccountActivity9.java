package com.jmhy.sdk.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huosdk.huounion.sdk.okhttp3.Call;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.fragment.JmSwitchLogin9Fragment;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.http.ApiRequestListener;
import com.jmhy.sdk.model.Msg;
import com.jmhy.sdk.sdk.JmhyApi;
import com.jmhy.sdk.utils.FloatUtils;
import com.jmhy.sdk.utils.changeAccountUtil;

import java.io.File;

public class JmSetAccountActivity9 extends JmBaseActivity implements
        OnClickListener {
    private EditText mEtusername;
    private EditText mEtpwd;
    private EditText mEt_confirm_pwd;
    private View mBtgetgame;
    private TextView mTvmsg;
    private ImageView kefu_help;
    private View contentView;
    private ImageView back;
    private CheckBox checkBtn;
    private boolean ischeck = true;
    private String path;
    private File file;
    private String url;
    Call setGuestAccountTask;
    Activity mActivity;
    private String password;
    private String result;
    private String msg;
    private String uname;
    private String gametoken;
    private String openid;
    private final static int SetAccountSuccess = 999;
    String edit_accpunt,edit_password,edit_confirm_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(AppConfig.resourceId(this, "jm_set_account_9", "layout"));
        mActivity=this;
        initView();
    }
    


    private void initView() {
        // TODO Auto-generated method stub
        mEtusername = (EditText) findViewById(AppConfig.resourceId(this, "edit_user", "id"));
        mEtusername.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mEtpwd = (EditText) findViewById(AppConfig.resourceId(this, "edit_pwd", "id"));
        mEtpwd.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
//        mEt_confirm_pwd = (EditText) findViewById(AppConfig.resourceId(this, "edit_confirm_pwd", "id"));
//        mEt_confirm_pwd.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mBtgetgame = findViewById(AppConfig.resourceId(this, "btgetgame", "id"));
        kefu_help = findViewById(AppConfig.resourceId(this, "kefu_help", "id"));
        back = (ImageView) findViewById(AppConfig.resourceId(this, "back", "id"));

        back.setOnClickListener(this);
        mBtgetgame.setOnClickListener(this);
        kefu_help.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == AppConfig.resourceId(this, "btgetgame", "id")) {
             edit_accpunt = mEtusername.getText().toString().trim();
              edit_password = mEtpwd.getText().toString().trim();
//              edit_confirm_password = mEt_confirm_pwd.getText().toString().trim();
            //不用确认密码
              edit_confirm_password = edit_password;
            if (TextUtils.isEmpty(edit_accpunt)) {
                showMsg(AppConfig.getString(this, "user_edit_hint"));
                return;
            }
            if (TextUtils.isEmpty(edit_password)) {
                showMsg(AppConfig.getString(this, "user_edit_pwdhint"));
                return;
            }
            if (TextUtils.isEmpty(edit_confirm_password)) {
                showMsg(AppConfig.getString(this, "user_edit_pwdhint"));
                return;
            }
            setGuestAccount(edit_accpunt, edit_password, edit_confirm_password);
        } else if (id == AppConfig.resourceId(this, "kefu_help", "id")) {
            this.finish();
        } else if (id == AppConfig.resourceId(this, "back", "id")) {
            this.finish();
        }

    }

    /**
     * 保存游客账号
     */
    public void setGuestAccount(String account, String password, String confirm_password) {

        setGuestAccountTask = JmhyApi.get().startSetAccount(this, AppConfig.appKey, account, password, confirm_password, new ApiRequestListener() {

            @Override
            public void onSuccess(Object obj) {
                Log.i("jimi","修改游客账号成功");
                Toast.makeText(mActivity,"设置成功,请重新登录",Toast.LENGTH_SHORT).show();
                changeAccountUtil.changeAccount(mActivity,null,false,"",edit_accpunt,edit_password);
            }

            @Override
            public void onError(int statusCode) {
                showMsg(AppConfig.getString(mActivity, "http_rror_msg"));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (setGuestAccountTask != null) {
            setGuestAccountTask.cancel();
        }
    }
}
