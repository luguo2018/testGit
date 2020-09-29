package com.jmhy.sdk.utils;

import android.app.Activity;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.fragment.JmSwitchLogin9Fragment;
import com.jmhy.sdk.model.BaseFloatActivity;

public class changeAccountUtil {
    public static void changeAccount(Activity activity, BaseFloatActivity baseFloatActivity, boolean isJSChange, String oldAccount, String newAccount, String newPassword) {
        AppConfig.change_old_account = oldAccount;
        AppConfig.change_new_account = newAccount;
        AppConfig.change_new_password = newPassword;
        AppConfig.isChangeGuestAccount = true;
        JmSwitchLogin9Fragment.deleteAccount(activity, isJSChange, oldAccount);
        AppConfig.isShow = false;

        JiMiSDK.userlistenerinfo.onLogout("logout");
        FloatUtils.destroyFloat();
        if (baseFloatActivity == null) {
            activity.finish();
        } else {
            baseFloatActivity.removeContentView();
        }

    }
}
