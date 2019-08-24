package com.jmhy.sdk.sdk;

import com.jmhy.sdk.activity.JmAutoLoginActivity;
import com.jmhy.sdk.activity.JmLoginActivity;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.utils.Seference;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Logindata {
    private static Seference mSeference;

    public static void selectLogin(Activity context) {
        mSeference = new Seference(context);
        if (AppConfig.is_reg_login_on.equals("0")) {
            return;
        }
        if (mSeference.isExitData()) {
            if (AppConfig.is_auto_login_on.equals("1")) {
                if (AppConfig.isswitch) {
                    //自动登录界面
                    Intent intent = new Intent(context, JmAutoLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                } else {
                    AppConfig.isswitch = true;
                    Intent intent = new Intent(context, JmLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                }
            } else {
                Intent intent = new Intent(context, JmLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        } else {
            //登录界面
            Intent intent = new Intent(context, JmLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }
    }
}
