package com.jmhy.sdk.sdk;

import com.jmhy.sdk.activity.JmAutoLoginActivity;
import com.jmhy.sdk.activity.JmLoginActivity;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.utils.Seference;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Logindata {
    private static Seference mSeference;

    public static void selectLogin(Activity context) {
        mSeference = new Seference(context);
//        AppConfig.showOneKeyLogin=true;
        if (AppConfig.is_reg_login_on.equals("0")) {
            return;
        }
        if (AppConfig.isChangeGuestAccount){//修改游客号后登录
            if (AppConfig.change_new_password!=null&&!AppConfig.change_new_password.equals("")){//有密码的情况  过去自动页面（XXX欢迎进入游戏）  拉登录接口
                Intent intent = new Intent(context, JmAutoLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }else{//如果没密码的情况  到常规登录页面
                Intent intent = new Intent(context, JmLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        }
        else if (mSeference.isExitData()) {
            if (AppConfig.is_auto_login_on.equals("1")) {
                if (AppConfig.isswitch) {
                    //自动登录界面
                    Intent intent = new Intent(context, JmAutoLoginActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                } else {
                    AppConfig.isswitch = true;
                    Intent intent = new Intent(context, JmLoginActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                }
            } else {
                Intent intent = new Intent(context, JmLoginActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        } else {
            switch (AppConfig.skin) {
                case 8:
                    AppConfig.isRegister = true;
                    break;
                default:
                    break;
            }
            //登录界面
            Intent intent = new Intent(context, JmLoginActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }
    }
}
