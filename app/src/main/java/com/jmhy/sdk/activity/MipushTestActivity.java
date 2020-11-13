package com.jmhy.sdk.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;

public class MipushTestActivity extends UmengNotifyClickActivity {

    private static String TAG = MipushTestActivity.class.getName();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
//        setContentView(AppConfig.resourceId(this, "jmlogin_main_9", "layout"));
        Log.i("启动测试", "启动类准备");
        Intent intent = this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        this.startActivity(intent);
        finish();
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Log.i(TAG, body);
    }
}