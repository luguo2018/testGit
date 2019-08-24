package com.jmhy.sdk.model;

public class MobileUser {
    private String code;
    private String code_area;
    private String moblie_code;
    private String unname;
    private String phone_register;
    private String message;
    private String moblie;

    private String openid;
    private String login_token;
    private String game_token;
    private String show_url_after_login;
    private String float_url_user_center;
    private String channel_user_info;
    private String float_url_gift_center;
    private int float_red_recommend;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode_area() {
        return code_area;
    }

    public void setCode_area(String code_area) {
        this.code_area = code_area;
    }

    public String getMoblie_code() {
        return moblie_code;
    }

    public void setMoblie_code(String moblie_code) {
        this.moblie_code = moblie_code;
    }

    public String getUnname() {
        return unname;
    }

    public void setUnname(String unname) {
        this.unname = unname;
    }

    public String getPhone_register() {
        return phone_register;
    }

    public void setPhone_register(String phone_register) {
        this.phone_register = phone_register;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMoblie() {
        return moblie;
    }

    public void setMoblie(String moblie) {
        this.moblie = moblie;
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

    @Override
    public String toString() {
        return "MobileUser{" +
                "code='" + code + '\'' +
                ", code_area='" + code_area + '\'' +
                ", moblie_code='" + moblie_code + '\'' +
                ", unname='" + unname + '\'' +
                ", phone_register='" + phone_register + '\'' +
                ", message='" + message + '\'' +
                ", moblie='" + moblie + '\'' +
                ", openid='" + openid + '\'' +
                ", login_token='" + login_token + '\'' +
                ", game_token='" + game_token + '\'' +
                ", show_url_after_login='" + show_url_after_login + '\'' +
                ", float_url_user_center='" + float_url_user_center + '\'' +
                ", channel_user_info='" + channel_user_info + '\'' +
                ", float_url_gift_center='" + float_url_gift_center + '\'' +
                ", float_red_recommend=" + float_red_recommend +
                '}';
    }
}
