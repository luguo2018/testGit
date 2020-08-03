package com.jmhy.sdk.model;

import org.json.JSONObject;

import java.util.List;

public class InitMsg {
	/**
	 * 	"is_sdk_float_on": 1,
		"is_user_float_on": 1,
		"is_pay_float_on": 1,
		"is_gift_float_on": 1,
		"is_logout_float_on": 1,
		"is_service_float_on": 1,
		"is_recommend_float_on": 1,
		"is_reg_login_on": 1,
		"is_visitor_on": 1,
		"is_auto_login_on": 1,
		"is_log_on": 0,
		"show_url_after_init": "",
		"code_area_list": ["+86", "+85", "+84", "+83", "+82"],
		"reg_flow_type": 1,
		"access_token": "100001_1529666020ec6997fa1c50341246984f24261230ac584b5ae75746ccb2",
		"expired": 7200,
		"user_agreement_url": "aHR0cDovL3N0YXRpYy5pam02LmNvbS9wdWJsaWMvdmVuZG9yL2FncmVlbWVudC9hZ3JlZW1lbnQuaHRtbA==",
		"customer_service_url": "aHR0cDovL2FwaS51LmlqbTYuY29tL2NlbnRlci9zZXJ2aWNl",
		"online_report_interval": 300
	 */
	private String code;
	private String message;
	private String sdk_float;
	private String user_float;
	private String pay_float;
	private String gift_float;
	private String logout_float;
	private String service_float;
	private String recommend_float;
	private String reg_login;
	private String visitor;
	private String auto_login;
	private String log_on;
	private String switch_login;
	private int skin;
	private String h5_game_url;
	
	private String access_token;
	private String expired;
	private List<String> code_area_list;
	private JSONObject channel_sdk_list;
	
	private String showurlafterint;
	private String regflowtype;
	private String useragreementurl;
	private String customerserviceurl;
	private String onlinereportinterval;
	private String isvisitoronphone;
	private String forgetpasswordurl;
	private String addglobalscripturl;
	private String moblie_direct_login="";

	public String getMoblie_direct_login() {
		return moblie_direct_login;
	}

	public void setMoblie_direct_login(String moblie_direct_login) {
		this.moblie_direct_login = moblie_direct_login;
	}

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
	public String getSdk_float() {
		return sdk_float;
	}
	public void setSdk_float(String sdk_float) {
		this.sdk_float = sdk_float;
	}
	public String getUser_float() {
		return user_float;
	}
	public void setUser_float(String user_float) {
		this.user_float = user_float;
	}
	public String getPay_float() {
		return pay_float;
	}
	public void setPay_float(String pay_float) {
		this.pay_float = pay_float;
	}
	public String getGift_float() {
		return gift_float;
	}
	public void setGift_float(String gift_float) {
		this.gift_float = gift_float;
	}
	public String getLogout_float() {
		return logout_float;
	}
	public void setLogout_float(String logout_float) {
		this.logout_float = logout_float;
	}
	public String getService_float() {
		return service_float;
	}
	public void setService_float(String service_float) {
		this.service_float = service_float;
	}
	public String getRecommend_float() {
		return recommend_float;
	}
	public void setRecommend_float(String recommend_float) {
		this.recommend_float = recommend_float;
	}
	public String getReg_login() {
		return reg_login;
	}
	public void setReg_login(String reg_login) {
		this.reg_login = reg_login;
	}
	public String getVisitor() {
		return visitor;
	}
	public void setVisitor(String visitor) {
		this.visitor = visitor;
	}
	public String getAuto_login() {
		return auto_login;
	}
	public void setAuto_login(String auto_login) {
		this.auto_login = auto_login;
	}
	public String getLog_on() {
		return log_on;
	}
	public void setLog_on(String log_on) {
		this.log_on = log_on;
	}
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getExpired() {
		return expired;
	}
	public void setExpired(String expired) {
		this.expired = expired;
	}
	
	public List<String> getCode_area_list() {
		return code_area_list;
	}
	public void setCode_area_list(List<String> code_area_list) {
		this.code_area_list = code_area_list;
	}
	
	public String getShowurlafterint() {
		return showurlafterint;
	}
	public void setShowurlafterint(String showurlafterint) {
		this.showurlafterint = showurlafterint;
	}
	public String getRegflowtype() {
		return regflowtype;
	}
	public void setRegflowtype(String regflowtype) {
		this.regflowtype = regflowtype;
	}
	public String getUseragreementurl() {
		return useragreementurl;
	}
	public void setUseragreementurl(String useragreementurl) {
		this.useragreementurl = useragreementurl;
	}
	public String getCustomerserviceurl() {
		return customerserviceurl;
	}
	public void setCustomerserviceurl(String customerserviceurl) {
		this.customerserviceurl = customerserviceurl;
	}
	public String getOnlinereportinterval() {
		return onlinereportinterval;
	}
	public void setOnlinereportinterval(String onlinereportinterval) {
		this.onlinereportinterval = onlinereportinterval;
	}
	
	
	public String getIsvisitoronphone() {
		return isvisitoronphone;
	}
	public void setIsvisitoronphone(String isvisitoronphone) {
		this.isvisitoronphone = isvisitoronphone;
	}
	
	public String getForgetpasswordurl() {
		return forgetpasswordurl;
	}
	public void setForgetpasswordurl(String forgetpasswordurl) {
		this.forgetpasswordurl = forgetpasswordurl;
	}

	public String getSwitch_login() {
		return switch_login;
	}

	public void setSwitch_login(String switch_login) {
		this.switch_login = switch_login;
	}

	public String getAddglobalscripturl() {
		return addglobalscripturl;
	}
	public void setAddglobalscripturl(String addglobalscripturl) {
		this.addglobalscripturl = addglobalscripturl;
	}

	public int getSkin() {
		return skin;
	}

	public void setSkin(int skin) {
		this.skin = skin;
	}

	public JSONObject getChannel_sdk_list() {
		return channel_sdk_list;
	}

	public void setChannel_sdk_list(JSONObject channel_sdk_list) {
		this.channel_sdk_list = channel_sdk_list;
	}

	public String getH5_game_url() {
		return h5_game_url;
	}

	public void setH5_game_url(String h5_game_url) {
		this.h5_game_url = h5_game_url;
	}

	@Override
	public String toString() {
		return "InitMsg{" +
				"code='" + code + '\'' +
				", message='" + message + '\'' +
				", sdk_float='" + sdk_float + '\'' +
				", user_float='" + user_float + '\'' +
				", pay_float='" + pay_float + '\'' +
				", gift_float='" + gift_float + '\'' +
				", logout_float='" + logout_float + '\'' +
				", service_float='" + service_float + '\'' +
				", recommend_float='" + recommend_float + '\'' +
				", reg_login='" + reg_login + '\'' +
				", visitor='" + visitor + '\'' +
				", auto_login='" + auto_login + '\'' +
				", log_on='" + log_on + '\'' +
				", switch_login='" + switch_login + '\'' +
				", skin=" + skin +
				", h5_game_url='" + h5_game_url + '\'' +
				", access_token='" + access_token + '\'' +
				", expired='" + expired + '\'' +
				", code_area_list=" + code_area_list +
				", channel_sdk_list=" + channel_sdk_list +
				", showurlafterint='" + showurlafterint + '\'' +
				", regflowtype='" + regflowtype + '\'' +
				", useragreementurl='" + useragreementurl + '\'' +
				", customerserviceurl='" + customerserviceurl + '\'' +
				", onlinereportinterval='" + onlinereportinterval + '\'' +
				", isvisitoronphone='" + isvisitoronphone + '\'' +
				", forgetpasswordurl='" + forgetpasswordurl + '\'' +
				", addglobalscripturl='" + addglobalscripturl + '\'' +
				", moblie_direct_login='" + moblie_direct_login + '\'' +
				'}';
	}
}
