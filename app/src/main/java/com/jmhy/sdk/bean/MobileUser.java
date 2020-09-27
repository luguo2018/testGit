package com.jmhy.sdk.bean;

import android.service.autofill.UserData;
import android.util.Log;

import com.jmhy.sdk.common.JiMiSDK;

public class MobileUser {
    private String code_area;
    private String code;
    private String uname;
    private String is_package_new;
    private String h5_game_url;
    private String hgu;
    private String phone_register;
    private String openid;
    private String login_token;
    private String game_token;
    private String show_url_after_login;
    private String float_url_user_center;
    private String float_url_home_center;
    private String channel_user_info;
    private String float_url_gift_center;
    private int float_red_recommend;
    private int show_set_account;
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone_register() {
        return phone_register;
    }

    public void setPhone_register(String phone_register) {
        this.phone_register = phone_register;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getH5_game_url() {
        return h5_game_url;
    }

    public void setH5_game_url(String h5_game_url) {
        this.h5_game_url = h5_game_url;
    }

    public String getHgu() {
        return hgu;
    }

    public void setHgu(String hgu) {
        this.hgu = hgu;
    }

    public String getCode_area() {
        return code_area;
    }

    public String getFloat_url_home_center() {
        return float_url_home_center;
    }

    public void setFloat_url_home_center(String float_url_home_center) {
        this.float_url_home_center = float_url_home_center;
    }

    public void setCode_area(String code_area) {
        this.code_area = code_area;
    }



    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getLogin_token() {
        return login_token;
    }

    public void setLogin_token(String login_token) {
        this.login_token = login_token;
    }

    public String getGame_token() {
        return game_token;
    }

    public void setGame_token(String game_token) {
        this.game_token = game_token;
    }

    public String getShow_url_after_login() {
        return show_url_after_login;
    }

    public void setShow_url_after_login(String show_url_after_login) {
        this.show_url_after_login = show_url_after_login;
    }

    public String getFloat_url_user_center() {
        return float_url_user_center;
    }

    public void setFloat_url_user_center(String float_url_user_center) {
        this.float_url_user_center = float_url_user_center;
    }

    public String getChannel_user_info() {
        return channel_user_info;
    }

    public void setChannel_user_info(String channel_user_info) {
        this.channel_user_info = channel_user_info;
    }

    public String getFloat_url_gift_center() {
        return float_url_gift_center;
    }

    public void setFloat_url_gift_center(String float_url_gift_center) {
        this.float_url_gift_center = float_url_gift_center;
    }

    public int getFloat_red_recommend() {
        return float_red_recommend;
    }

    public void setFloat_red_recommend(int float_red_recommend) {
        this.float_red_recommend = float_red_recommend;
    }


    public int getShow_set_account() {
        return show_set_account;
    }

    public void setShow_set_account(int show_set_account) {
        this.show_set_account = show_set_account;
    }

    public String getIs_package_new() {
        return is_package_new;
    }

    public void setIs_package_new(String is_package_new) {
        this.is_package_new = is_package_new;
        Log.i("测试mobile", "is_package_new" + is_package_new);
        if (is_package_new.equals("1")) {
            JiMiSDK.getStatisticsSDK().onRegister("JiMiSDK", true);
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "MobileUser{" +
                "code_area='" + code_area + '\'' +
                ", uname='" + uname + '\'' +
                ", is_package_new='" + is_package_new + '\'' +
                ", h5_game_url='" + h5_game_url + '\'' +
                ", hgu='" + hgu + '\'' +
                ", openid='" + openid + '\'' +
                ", login_token='" + login_token + '\'' +
                ", game_token='" + game_token + '\'' +
                ", show_url_after_login='" + show_url_after_login + '\'' +
                ", float_url_user_center='" + float_url_user_center + '\'' +
                ", float_url_home_center='" + float_url_home_center + '\'' +
                ", channel_user_info='" + channel_user_info + '\'' +
                ", float_url_gift_center='" + float_url_gift_center + '\'' +
                ", float_red_recommend=" + float_red_recommend +
                ", show_set_account=" + show_set_account +
                '}';
    }
}
