package com.jmhy.sdk.utils;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.fragment.JmLoginHomePage9Fragment;
import com.jmhy.sdk.fragment.JmPhonerLogin2Fragment;
import com.jmhy.sdk.fragment.JmPhonerLogin3Fragment;
import com.jmhy.sdk.fragment.JmPhonerLogin4Fragment;
import com.jmhy.sdk.fragment.JmPhonerLogin5Fragment;
import com.jmhy.sdk.fragment.JmPhonerLogin6Fragment;
import com.jmhy.sdk.fragment.JmPhonerLogin8Fragment;
import com.jmhy.sdk.fragment.JmPhonerLoginFragment;
import com.jmhy.sdk.fragment.JmSetUser2Fragment;
import com.jmhy.sdk.fragment.JmSetUser3Fragment;
import com.jmhy.sdk.fragment.JmSetUser8Fragment;
import com.jmhy.sdk.fragment.JmSetUser9Fragment;
import com.jmhy.sdk.fragment.JmSetUserFragment;
import com.jmhy.sdk.fragment.JmSetpwd2Fragment;
import com.jmhy.sdk.fragment.JmSetpwd3Fragment;
import com.jmhy.sdk.fragment.JmSetpwd4Fragment;
import com.jmhy.sdk.fragment.JmSetpwd9Fragment;
import com.jmhy.sdk.fragment.JmSetpwdFragment;
import com.jmhy.sdk.fragment.JmUserLogin2Fragment;
import com.jmhy.sdk.fragment.JmUserLogin3Fragment;
import com.jmhy.sdk.fragment.JmUserLogin4Fragment;
import com.jmhy.sdk.fragment.JmUserLogin8Fragment;
import com.jmhy.sdk.fragment.JmUserLogin9Fragment;
import com.jmhy.sdk.fragment.JmUserLoginFragment;
import com.jmhy.sdk.fragment.JmUserRegister2Fragment;
import com.jmhy.sdk.fragment.JmUserRegister3Fragment;
import com.jmhy.sdk.fragment.JmUserRegister8Fragment;
import com.jmhy.sdk.fragment.JmUserRegisterFragment;

/**
 * create by yhz on 2018/8/21
 */
public class FragmentUtils {
    public static Fragment getJmPhonerLoginFragment(Context context){
        switch (AppConfig.skin){
            case 9:
                return Fragment.instantiate(context, JmLoginHomePage9Fragment.class.getName());
            case 8:
                return Fragment.instantiate(context, JmPhonerLogin8Fragment.class.getName());
            case 7:
                return Fragment.instantiate(context, JmPhonerLogin2Fragment.class.getName());
             case 6:
                return Fragment.instantiate(context, JmPhonerLogin6Fragment.class.getName());
            case 5:
                return Fragment.instantiate(context, JmPhonerLogin5Fragment.class.getName());
            case 4:
                return Fragment.instantiate(context, JmPhonerLogin4Fragment.class.getName());
            case 3:
                return Fragment.instantiate(context, JmPhonerLogin3Fragment.class.getName());
            case 2:
                return Fragment.instantiate(context, JmPhonerLogin2Fragment.class.getName());
            default:
                return Fragment.instantiate(context, JmPhonerLoginFragment.class.getName());
        }
    }

    public static Fragment getJmSetpwdFragment(Context context, Bundle args){
        switch (AppConfig.skin){
            case 9:
                return Fragment.instantiate(context, JmSetpwd9Fragment.class.getName(), args);
            case 8:
                return Fragment.instantiate(context, JmSetpwd2Fragment.class.getName(), args);
            case 7:
                return Fragment.instantiate(context, JmSetpwd2Fragment.class.getName(), args);
            case 6:
            case 5:
            case 4:
                return Fragment.instantiate(context, JmSetpwd4Fragment.class.getName(), args);
            case 3:
                return Fragment.instantiate(context, JmSetpwd3Fragment.class.getName(), args);
            case 2:
                return Fragment.instantiate(context, JmSetpwd2Fragment.class.getName(), args);
            default:
                return Fragment.instantiate(context, JmSetpwdFragment.class.getName(), args);
        }
    }

    public static Fragment getJmUserLoginFragment(Context context){
        switch (AppConfig.skin){
            case 9:
                return Fragment.instantiate(context, JmUserLogin9Fragment.class.getName());
            case 8:
                Log.e("jimisdk","8888 JmUserLogin8Fragment");
                return Fragment.instantiate(context, JmUserLogin8Fragment.class.getName());
            case 7:
                return Fragment.instantiate(context, JmUserLogin2Fragment.class.getName());
            case 6:
                return Fragment.instantiate(context, JmPhonerLogin6Fragment.class.getName());
            case 5:
            case 4:
                return Fragment.instantiate(context, JmUserLogin4Fragment.class.getName());
            case 3:
                return Fragment.instantiate(context, JmUserLogin3Fragment.class.getName());
            case 2:
                return Fragment.instantiate(context, JmUserLogin2Fragment.class.getName());
            default:
                return Fragment.instantiate(context, JmUserLoginFragment.class.getName());
        }
    }

    public static Fragment getJmSetUserFragment(Context context, Bundle args){
        switch (AppConfig.skin){
            case 9:
                return Fragment.instantiate(context, JmSetUser9Fragment.class.getName(), args);
            case 8:
                Log.e("jimisdk","8888 getJmSetUserFragment");
                return Fragment.instantiate(context, JmSetUser8Fragment.class.getName(), args);
            case 7:
                return Fragment.instantiate(context, JmSetUser2Fragment.class.getName(), args);

            case 3:
                return Fragment.instantiate(context, JmSetUser3Fragment.class.getName(), args);
            case 2:
                return Fragment.instantiate(context, JmSetUser2Fragment.class.getName(), args);
            default:
                return Fragment.instantiate(context, JmSetUserFragment.class.getName(), args);
        }
    }

    public static Fragment getJmUserRegisterFragment(Context context){
        switch (AppConfig.skin){
            case 9:
                Log.e("jimisdk","8888 getJmUserRegisterFragment");
                return Fragment.instantiate(context, JmUserRegister8Fragment.class.getName());
            case 8:
                Log.e("jimisdk","8888 getJmUserRegisterFragment");
                return Fragment.instantiate(context, JmUserRegister8Fragment.class.getName());
            case 7:
                return Fragment.instantiate(context, JmUserRegister2Fragment.class.getName());
            case 5:
                return Fragment.instantiate(context, JmPhonerLogin5Fragment.class.getName());
            case 4:
                return Fragment.instantiate(context, JmPhonerLogin4Fragment.class.getName());
            case 3:
                return Fragment.instantiate(context, JmUserRegister3Fragment.class.getName());
            case 2:
                return Fragment.instantiate(context, JmUserRegister2Fragment.class.getName());
            default:
                return Fragment.instantiate(context, JmUserRegisterFragment.class.getName());
        }
    }
}
