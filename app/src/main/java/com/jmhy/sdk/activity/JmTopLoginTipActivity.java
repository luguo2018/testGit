package com.jmhy.sdk.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.http.ApiAsyncTask;
import com.jmhy.sdk.model.LoginMessage;
import com.jmhy.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
 *皮肤9用，登录成功顶部显示提示布局，提供切换帐号功能
 *  */
public class JmTopLoginTipActivity extends JmBaseActivity {

    private TextView mTvname;
    private View mBtback;
    TimerTask task;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        AppConfig.skin9_is_switch = false;
        setContentView(AppConfig.resourceId(this, "jmautologin_9", "layout"));
        initView();
    }


    @Override
    public void onBackPressed() {
        backListener.onClick(null);
    }

    private OnClickListener backListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            AppConfig.ismobillg = true;
            timer.cancel();
            AppConfig.skin9_is_switch = true;
            Intent intent = new Intent(JmTopLoginTipActivity.this, JmLoginActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private void initView() {
        mTvname =  findViewById(AppConfig.resourceId(this, "tvusername", "id"));
        mBtback = findViewById(AppConfig.resourceId(this, "btbacklogin", "id"));
        mBtback.setOnClickListener(backListener);
        String uName = getIntent().getStringExtra("uName");
        if (!TextUtils.isEmpty(uName)) {
            mTvname.setText(uName);
        }

        task = new TimerTask() {
            @Override
            public void run() {

                int type = getIntent().getIntExtra("type", 0);
                String message = getIntent().getStringExtra("message");
                String uName = getIntent().getStringExtra("uName");
                String openId = getIntent().getStringExtra("openId");
                String token = getIntent().getStringExtra("token");
                String noticeUrl = getIntent().getStringExtra("noticeUrl");
                wrapaLoginInfo("success", message, uName, openId, token);
                if (AppConfig.skin9_show_setAccount) {
                    turnToSetAccount();
                }else{
                    turnToNotice(noticeUrl);
                }
                finish();

            }
        };
        timer = new Timer();
        timer.schedule(task, 3000);//设置延迟3秒访问
    }


}
