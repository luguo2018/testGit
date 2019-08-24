package com.jmhy.sdk.model;

public class Msg {
	//data{"code":44004,"data":[],"message":"参数缺失"}
	private String message;
	private String code;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}
