package com.jmhy.sdk.model;

public class OnlineMessage {
	private String code;
	private String message;
	private String showUrl;
	private String showMsg;
	private String application_notice ="";// 状态0正常   状态1有新消息需要显示悬浮窗客服红点
	private String channel_event = "";

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

	public String getChannel_event() {
		return channel_event;
	}

	public void setChannel_event(String channel_event) {
		this.channel_event = channel_event;
	}

	public String getApplication_notice() {
		return application_notice;
	}

	public void setApplication_notice(String application_notice) {
		this.application_notice = application_notice;
	}

	@Override
	public String toString() {
		return "OnlineMessage{" +
				"code='" + code + '\'' +
				", message='" + message + '\'' +
				", showUrl='" + showUrl + '\'' +
				", showMsg='" + showMsg + '\'' +
				", exit=" + exit +
				", channel_event=" + channel_event +
				", application_notice=" + application_notice +
				'}';
	}

}
