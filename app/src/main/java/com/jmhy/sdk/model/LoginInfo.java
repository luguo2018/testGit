package com.jmhy.sdk.model;

public class LoginInfo {
    String uname;
    String openid;
    String login_token;
    String game_token;
    String h5_game_url;
    String show_url_after_login;
    String float_url_user_center;
    String float_url_gift_center;
    String float_url_home_center;
    int  float_red_recommend;
    int show_set_account;
    String hgu;
    int is_package_new;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
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

    public String getH5_game_url() {
        return h5_game_url;
    }

    public void setH5_game_url(String h5_game_url) {
        this.h5_game_url = h5_game_url;
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

    public String getFloat_url_gift_center() {
        return float_url_gift_center;
    }

    public void setFloat_url_gift_center(String float_url_gift_center) {
        this.float_url_gift_center = float_url_gift_center;
    }

    public String getFloat_url_home_center() {
        return float_url_home_center;
    }

    public void setFloat_url_home_center(String float_url_home_center) {
        this.float_url_home_center = float_url_home_center;
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

    public String getHgu() {
        return hgu;
    }

    public void setHgu(String hgu) {
        this.hgu = hgu;
    }

    public int getIs_package_new() {
        return is_package_new;
    }

    public void setIs_package_new(int is_package_new) {
        this.is_package_new = is_package_new;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "uname='" + uname + '\'' +
                ", openid='" + openid + '\'' +
                ", login_token='" + login_token + '\'' +
                ", game_token='" + game_token + '\'' +
                ", h5_game_url='" + h5_game_url + '\'' +
                ", show_url_after_login='" + show_url_after_login + '\'' +
                ", float_url_user_center='" + float_url_user_center + '\'' +
                ", float_url_gift_center='" + float_url_gift_center + '\'' +
                ", float_url_home_center='" + float_url_home_center + '\'' +
                ", float_red_recommend=" + float_red_recommend +
                ", show_set_account=" + show_set_account +
                ", hgu='" + hgu + '\'' +
                ", is_package_new=" + is_package_new +
                '}';
    }
}
