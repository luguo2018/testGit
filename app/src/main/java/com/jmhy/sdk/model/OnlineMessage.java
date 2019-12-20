package com.jmhy.sdk.model;

public class OnlineMessage {
	private String code;
	private String message;
	private String showUrl;
	private String showMsg;
	private int exit;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getShowUrl() {
		return showUrl;
	}

	public void setShowUrl(String showUrl) {
		this.showUrl = showUrl;
	}

	public String getShowMsg() {
		return showMsg;
	}

	public void setShowMsg(String showMsg) {
		this.showMsg = showMsg;
	}

	public int getExit() {
		return exit;
	}

	public void setExit(int exit) {
		this.exit = exit;
	}

	@Override
	public String toString() {
		return "OnlineMessage{" +
				"code='" + code + '\'' +
				", message='" + message + '\'' +
				", showUrl='" + showUrl + '\'' +
				", showMsg='" + showMsg + '\'' +
				", exit=" + exit +
				'}';
	}
}
