package com.jmhy.sdk.model;

public class PayData {
	
	private String code;
	private String message;
	private String otype;
	private String ocontent;
	private String callbackUrl;
	private String realnameneedeed;
	private String orderid;
	private String user_reg_date;
	private String ext;
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
	public String getOtype() {
		return otype;
	}
	public void setOtype(String otype) {
		this.otype = otype;
	}
	public String getOcontent() {
		return ocontent;
	}
	public void setOcontent(String ocontent) {
		this.ocontent = ocontent;
	}
	public String getRealnameneedeed() {
		return realnameneedeed;
	}
	public void setRealnameneedeed(String realnameneedeed) {
		this.realnameneedeed = realnameneedeed;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getUser_reg_date() {
		return user_reg_date;
	}

	public void setUser_reg_date(String user_reg_date) {
		this.user_reg_date = user_reg_date;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	@Override
	public String toString() {
		return "PayData{" +
				"code='" + code + '\'' +
				", message='" + message + '\'' +
				", otype='" + otype + '\'' +
				", ocontent='" + ocontent + '\'' +
				", callbackUrl='" + callbackUrl + '\'' +
				", realnameneedeed='" + realnameneedeed + '\'' +
				", orderid='" + orderid + '\'' +
				", user_reg_date='" + user_reg_date + '\'' +
				", ext='" + ext + '\'' +
				'}';
	}
}