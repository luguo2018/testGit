package com.jmhy.sdk.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jmhy.sdk.config.AppConfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

public class Seference {

    public Context mcontext;
    public static final String ACCOUNT_1 = "account_1";
    public static final String PASSWORD_1 = "password_1";
    public static final String UID_1 = "uid_1";
    public static final String TIME_1 = "time_1";
    public static final String LOGIN_TYPE_1 = "login_type_1";

    public static final String ACCOUNT_2 = "account_2";
    public static final String PASSWORD_2 = "password_2";
    public static final String UID_2 = "uid_2";
    public static final String TIME_2 = "time_2";
    public static final String LOGIN_TYPE_2 = "login_type_2";

    public static final String ACCOUNT_3 = "account_3";
    public static final String PASSWORD_3 = "password_3";
    public static final String UID_3 = "uid_3";
    public static final String TIME_3 = "time_3";
    public static final String LOGIN_TYPE_3 = "login_type_3";
    public static final String ACCOUNT_FILE_NAME = "account_file_name";

    public Seference(Context context) {
        this.mcontext = context;
    }

    public void savePreferenceData(String filename, String key, String value) {
        SharedPreferences.Editor sharedata = mcontext.getSharedPreferences(
                filename, 0).edit();
        sharedata.putString(key, value);
        sharedata.commit();
    }

    public void removePreferenceData(String filename, String key) {
        SharedPreferences.Editor sharedata = mcontext.getSharedPreferences(
                filename, 0).edit();
        sharedata.remove(key);

        sharedata.commit();
    }

    public String getPreferenceData(String filename, String key) {
        String temp = "";
        SharedPreferences sharedata = mcontext
                .getSharedPreferences(filename, 0);
        temp = sharedata.getString(key, "");
        return temp;
    }

    public String getContentPW(String key) {
        String temp = "";
        temp = Base64.decode(getPreferenceData(ACCOUNT_FILE_NAME, key));
        return temp;
    }

    public void saveAccount(String account, String password, String uid) {
        // System.out.println("--account--" + account + "--password--" +
        // password
        // + "--uid--" + uid);
        if (account.equals(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1))) {
            changeAccount_1(account, password, uid);
            return;
        }
        if (account.equals(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2))) {
            changeAccount_2(account, password, uid);
            return;
        }
        if (account.equals(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3))) {
            changeAccount_3(account, password, uid);
            return;
        }
        String pwtemp = Base64.encode(password.getBytes());
        if (AppConfig.userType == 1) {
            if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME,
                    ACCOUNT_1))) {
                savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1, account);
                savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_1, pwtemp);
                savePreferenceData(ACCOUNT_FILE_NAME, UID_1, uid);
            } else {
                changeAccount_1(account, password, uid);
            }
            return;
        }
        if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1))) {
            savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1, account);
            savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_1, pwtemp);
            savePreferenceData(ACCOUNT_FILE_NAME, UID_1, uid);

        } else if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME,
                ACCOUNT_2))) {
            // 号码存储，之前的第一个账号移到第ACCOUNT_2位置，新账号放到ACCOUNT_1位置
            savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1));
            savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, PASSWORD_1));
            savePreferenceData(ACCOUNT_FILE_NAME, UID_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, UID_1));
            savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1, account);
            savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_1, pwtemp);
            savePreferenceData(ACCOUNT_FILE_NAME, UID_1, uid);

        } else {
            savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3,
                    getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2));
            savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_3,
                    getPreferenceData(ACCOUNT_FILE_NAME, PASSWORD_2));
            savePreferenceData(ACCOUNT_FILE_NAME, UID_3,
                    getPreferenceData(ACCOUNT_FILE_NAME, UID_2));
            savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1));
            savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, PASSWORD_1));
            savePreferenceData(ACCOUNT_FILE_NAME, UID_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, UID_1));
            savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1, account);
            savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_1, pwtemp);
            savePreferenceData(ACCOUNT_FILE_NAME, UID_1, uid);
        }
    }

    public void saveTimeAndType(String account,String time, String type) {
        Log.i("jimi",time+"比较"+getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1)+":----"+getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2)+"---"+getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3));
        if (AppConfig.skin!=9){
            return;
        }
        if (account.equals(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1))) {
            changeTimeAndType_1(time, type);
            return;
        }
        if (account.equals(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2))) {
            changeTimeAndType_2(time, type);
            return;
        }
        if (account.equals(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3))) {
            changeTimeAndType_3(time, type);
            return;
        }

        if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME, TIME_1))) {
            savePreferenceData(ACCOUNT_FILE_NAME, TIME_1, time);
            savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_1, type);

        } else if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME,
                TIME_2))) {
            // 号码存储，之前的第一个账号移到第ACCOUNT_2位置，新账号放到ACCOUNT_1位置
            savePreferenceData(ACCOUNT_FILE_NAME, TIME_2, getPreferenceData(ACCOUNT_FILE_NAME, TIME_1));
            savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_2, getPreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_1));

            savePreferenceData(ACCOUNT_FILE_NAME, TIME_1, time);
            savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_1, type);

        } else {
            savePreferenceData(ACCOUNT_FILE_NAME, TIME_3, getPreferenceData(ACCOUNT_FILE_NAME, TIME_2));
            savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_3, getPreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_2));

            savePreferenceData(ACCOUNT_FILE_NAME, TIME_2, getPreferenceData(ACCOUNT_FILE_NAME, TIME_1));
            savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_2, getPreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_1));

            savePreferenceData(ACCOUNT_FILE_NAME, TIME_1, time);
            savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_1, type);
        }
    }

    /**
     * 移动账号的位置，user1
     */
    public void changeAccount_1(String account, String pwd, String uid) {
        savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1, account);
        savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_1,
                Base64.encode(pwd.getBytes()));
        savePreferenceData(ACCOUNT_FILE_NAME, UID_1, uid);
    }

    /**
     * 移动账号的位置，user2----user1
     */
    public void changeAccount_2(String account, String pwd, String uid) {
        savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2,
                getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1));
        savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_2,
                getPreferenceData(ACCOUNT_FILE_NAME, PASSWORD_1));
        savePreferenceData(ACCOUNT_FILE_NAME, UID_2,
                getPreferenceData(ACCOUNT_FILE_NAME, UID_1));
        savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1, account);
        savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_1,
                Base64.encode(pwd.getBytes()));
        savePreferenceData(ACCOUNT_FILE_NAME, UID_1, uid);
    }

    /**
     * 移动账号位置 user3---1
     */
    public void changeAccount_3(String account, String pwd, String uid) {
        savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3,
                getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2));
        savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_3,
                getPreferenceData(ACCOUNT_FILE_NAME, PASSWORD_2));
        savePreferenceData(ACCOUNT_FILE_NAME, UID_3,
                getPreferenceData(ACCOUNT_FILE_NAME, UID_2));
        savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2,
                getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1));
        savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_2,
                getPreferenceData(ACCOUNT_FILE_NAME, PASSWORD_1));
        savePreferenceData(ACCOUNT_FILE_NAME, UID_2,
                getPreferenceData(ACCOUNT_FILE_NAME, UID_1));
        savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1, account);
        savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_1,
                Base64.encode(pwd.getBytes()));
        savePreferenceData(ACCOUNT_FILE_NAME, UID_1, uid);
    }


    public void changeTimeAndType_1(String time, String type) {
        savePreferenceData(ACCOUNT_FILE_NAME, TIME_1, time);
        savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_1, type);
    }

    public void changeTimeAndType_2(String time, String type) {
        Log.i("jimi","类型断点"+type);
        savePreferenceData(ACCOUNT_FILE_NAME, TIME_2, getPreferenceData(ACCOUNT_FILE_NAME, TIME_1));
        savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_2, getPreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_1));

        savePreferenceData(ACCOUNT_FILE_NAME, TIME_1, time);
        savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_1, type);
    }

    public void changeTimeAndType_3(String time, String type) {

        savePreferenceData(ACCOUNT_FILE_NAME, TIME_3, getPreferenceData(ACCOUNT_FILE_NAME, TIME_2));
        savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_3, getPreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_2));

        savePreferenceData(ACCOUNT_FILE_NAME, TIME_2, getPreferenceData(ACCOUNT_FILE_NAME, TIME_1));
        savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_2, getPreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_1));

        savePreferenceData(ACCOUNT_FILE_NAME, TIME_1, time);
        savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_1, type);
    }


    /**
     * 判断账号的个数
     */
    public int numberUser() {
        int num = 0;
        if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1))) {
            num = 0;
        }
        if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2))) {
            num = 1;
        }
        if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3))) {
            num = 2;
        } else {
            num = 3;
        }
        return num;
    }

    /**
     * 判断账号是什么位置 for example xxx------account_1
     *
     * @return
     */
    public String JusticePosition(String account) {
        if (account.equals(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1)))
            return ACCOUNT_1;
        if (account.equals(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2)))
            return ACCOUNT_2;
        if (account.equals(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3)))
            return ACCOUNT_3;
        return "";
    }

    /**
     * 判断preference是否存在数据
     */
    public boolean isExitData() {
        if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1))) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 单独修改密码
     *
     * @return
     */
    public void ChangePWD(String pwd, String position) {
        String pwtemp = Base64.encode(pwd.getBytes());
        savePreferenceData(ACCOUNT_FILE_NAME, position, pwtemp);
    }

    public List<HashMap<String, String>> getContentList() {

        List<HashMap<String, String>> accountList = new ArrayList<HashMap<String, String>>();

        if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1))) {
            return null;
        } else if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME,
                ACCOUNT_2))) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("account", getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1));
            hm.put("password", getContentPW(PASSWORD_1));
            hm.put("uid", getPreferenceData(ACCOUNT_FILE_NAME, UID_1));
            accountList.add(hm);
            return accountList;
        } else if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME,
                ACCOUNT_3))) {
            HashMap<String, String> hm1 = new HashMap<String, String>();
            hm1.put("account", getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1));
            hm1.put("password", getContentPW(PASSWORD_1));
            hm1.put("uid", getPreferenceData(ACCOUNT_FILE_NAME, UID_1));
            accountList.add(hm1);
            HashMap<String, String> hm2 = new HashMap<String, String>();
            hm2.put("account", getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2));
            hm2.put("password", getContentPW(PASSWORD_2));
            hm2.put("uid", getPreferenceData(ACCOUNT_FILE_NAME, UID_2));
            accountList.add(hm2);
            return accountList;
        } else {
            HashMap<String, String> hm1 = new HashMap<String, String>();
            hm1.put("account", getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1));
            hm1.put("password", getContentPW(PASSWORD_1));
            hm1.put("uid", getPreferenceData(ACCOUNT_FILE_NAME, UID_1));
            accountList.add(hm1);
            HashMap<String, String> hm2 = new HashMap<String, String>();
            hm2.put("account", getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2));
            hm2.put("password", getContentPW(PASSWORD_2));
            hm2.put("uid", getPreferenceData(ACCOUNT_FILE_NAME, UID_2));
            accountList.add(hm2);
            HashMap<String, String> hm3 = new HashMap<String, String>();
            hm3.put("account", getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3));
            hm3.put("password", getContentPW(PASSWORD_3));
            hm3.put("uid", getPreferenceData(ACCOUNT_FILE_NAME, UID_3));
            accountList.add(hm3);
            return accountList;
        }
    }

    public List<HashMap<String, String>> getTimeAndTypeContentList() {

        List<HashMap<String, String>> timeAndTypeList = new ArrayList<HashMap<String, String>>();

        if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME, TIME_1))) {
            return null;
        } else if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME, TIME_2))) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("time", getPreferenceData(ACCOUNT_FILE_NAME, TIME_1));
            hm.put("loginType", getPreferenceData(ACCOUNT_FILE_NAME,LOGIN_TYPE_1));
            timeAndTypeList.add(hm);
            return timeAndTypeList;
        } else if (TextUtils.isEmpty(getPreferenceData(ACCOUNT_FILE_NAME, TIME_3))) {
            HashMap<String, String> hm1 = new HashMap<String, String>();
            hm1.put("time", getPreferenceData(ACCOUNT_FILE_NAME, TIME_1));
            hm1.put("loginType", getPreferenceData(ACCOUNT_FILE_NAME,LOGIN_TYPE_1));
            timeAndTypeList.add(hm1);
            HashMap<String, String> hm2 = new HashMap<String, String>();
            hm2.put("time", getPreferenceData(ACCOUNT_FILE_NAME, TIME_2));
            hm2.put("loginType", getPreferenceData(ACCOUNT_FILE_NAME,LOGIN_TYPE_2));
            timeAndTypeList.add(hm2);
            return timeAndTypeList;
        } else {
            HashMap<String, String> hm1 = new HashMap<String, String>();
            hm1.put("time", getPreferenceData(ACCOUNT_FILE_NAME, TIME_1));
            hm1.put("loginType", getPreferenceData(ACCOUNT_FILE_NAME,LOGIN_TYPE_1));
            timeAndTypeList.add(hm1);
            HashMap<String, String> hm2 = new HashMap<String, String>();
            hm2.put("time", getPreferenceData(ACCOUNT_FILE_NAME, TIME_2));
            hm2.put("loginType", getPreferenceData(ACCOUNT_FILE_NAME,LOGIN_TYPE_2));
            timeAndTypeList.add(hm2);
            HashMap<String, String> hm3 = new HashMap<String, String>();
            hm3.put("time", getPreferenceData(ACCOUNT_FILE_NAME, TIME_3));
            hm3.put("loginType", getPreferenceData(ACCOUNT_FILE_NAME,LOGIN_TYPE_3));
            timeAndTypeList.add(hm3);
            return timeAndTypeList;
        }
    }

    public void clearingAccount(String name) {

        if (JusticePosition(name).equals(ACCOUNT_1)) {

            // 2存在->1
            savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_1,
                    getPreferenceData(ACCOUNT_FILE_NAME, PASSWORD_2));
            savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_1,
                    getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2));
            savePreferenceData(ACCOUNT_FILE_NAME, UID_1,
                    getPreferenceData(ACCOUNT_FILE_NAME, UID_2));

            // 3-》2
            savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3));
            savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, PASSWORD_3));
            savePreferenceData(ACCOUNT_FILE_NAME, UID_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, UID_3));
            savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3, "");
            savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_3, "");
            savePreferenceData(ACCOUNT_FILE_NAME, UID_3, "");
        } else if (JusticePosition(name).equals(ACCOUNT_2)) {

            // 3-》2
            //Log.i("kk", "3->2");
            savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3));
            savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, PASSWORD_3));
            savePreferenceData(ACCOUNT_FILE_NAME, UID_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, UID_3));
            savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3, "");
            savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_3, "");
            savePreferenceData(ACCOUNT_FILE_NAME, UID_3, "");

        } else if (JusticePosition(name).equals(ACCOUNT_3)) {

            savePreferenceData(ACCOUNT_FILE_NAME, ACCOUNT_3, "");
            savePreferenceData(ACCOUNT_FILE_NAME, PASSWORD_3, "");
            savePreferenceData(ACCOUNT_FILE_NAME, UID_3, "");
        }
    }
    public void clearingTimeAndType(String name) {

        if (JusticePosition(name).equals(ACCOUNT_1)) {

            savePreferenceData(ACCOUNT_FILE_NAME, TIME_1,
                    getPreferenceData(ACCOUNT_FILE_NAME, TIME_2));
            savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_1,
                    getPreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_2));

            // 3-》2
            savePreferenceData(ACCOUNT_FILE_NAME, TIME_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, TIME_3));
            savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_3));

            savePreferenceData(ACCOUNT_FILE_NAME, TIME_3, "");
            savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_3, "");
        } else if (JusticePosition(name).equals(ACCOUNT_2)) {

            // 3-》2
            savePreferenceData(ACCOUNT_FILE_NAME, TIME_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, TIME_3));
            savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_2,
                    getPreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_3));

            savePreferenceData(ACCOUNT_FILE_NAME, TIME_3, "");
            savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_3, "");

        } else if (JusticePosition(name).equals(ACCOUNT_3)) {

            savePreferenceData(ACCOUNT_FILE_NAME, TIME_3, "");
            savePreferenceData(ACCOUNT_FILE_NAME, LOGIN_TYPE_3, "");
        }
    }

}
