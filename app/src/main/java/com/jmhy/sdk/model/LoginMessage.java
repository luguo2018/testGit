package com.jmhy.sdk.model;

public class LoginMessage {
	private String code;
	private String uname;
	private String login_token;
	private String game_token;
	private String show_url_after_login;
	private String openid;
	private String message;
    private String float_url_user_center;
	private String float_url_gift_center;
	private int float_red_recommend;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
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

	public int getFloat_red_recommend() {
		return float_red_recommend;
	}

	public void setFloat_red_recommend(int float_red_recommend) {
		this.float_red_recommend = float_red_recommend;
	}

	@Override
	public String toString() {
		return "LoginMessage{" +
				"code='" + code + '\'' +
				", uname='" + uname + '\'' +
				", login_token='" + login_token + '\'' +
				", game_token='" + game_token + '\'' +
				", show_url_after_login='" + show_url_after_login + '\'' +
				", openid='" + openid + '\'' +
				", message='" + message + '\'' +
				", float_url_user_center='" + float_url_user_center + '\'' +
				", float_url_gift_center='" + float_url_gift_center + '\'' +
				", float_red_recommend=" + float_red_recommend +
				'}';
	}
}
