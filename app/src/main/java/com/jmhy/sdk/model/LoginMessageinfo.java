package com.jmhy.sdk.model;

public class LoginMessageinfo {
	private String result;
	private String msg;
	private String uname;
	private String gametoken;
	private String openid;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getGametoken() {
		return gametoken;
	}
	public void setGametoken(String game_token) {
		this.gametoken = game_token;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}

	@Override
	public String toString() {
		return "LoginMessageinfo{" +
				"result='" + result + '\'' +
				", msg='" + msg + '\'' +
				", uname='" + uname + '\'' +
				", gametoken='" + gametoken + '\'' +
				", openid='" + openid + '\'' +
				'}';
	}
}
