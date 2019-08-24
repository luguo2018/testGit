package com.jmhy.sdk.model;

public class PaymentInfo{
	private int appid;
	private String appKey;

	private	String cporderid;
	private String ordername;
	private String amount;
	private String roleid;
	private String rolename;
	private String level;
	private String gender;
	private String serverno;
	private String zoneName;
	private String balance;
	private String power;
	private String viplevel;
	private String ext;

	public String getCporderid() {
		return cporderid;
	}
	public void setCporderid(String cporderid) {
		this.cporderid = cporderid;
	}
	public String getOrdername() {
		return ordername;
	}
	public void setOrdername(String ordername) {
		this.ordername = ordername;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getRoleid() {
		return roleid;
	}
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getServerno() {
		return serverno;
	}
	public void setServerno(String serverno) {
		this.serverno = serverno;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	public String getViplevel() {
		return viplevel;
	}
	public void setViplevel(String viplevel) {
		this.viplevel = viplevel;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	@Override
	public String toString() {
		return "PaymentInfo{" +
				"appid=" + appid +
				", appKey='" + appKey + '\'' +
				", cporderid='" + cporderid + '\'' +
				", ordername='" + ordername + '\'' +
				", amount='" + amount + '\'' +
				", roleid='" + roleid + '\'' +
				", rolename='" + rolename + '\'' +
				", level='" + level + '\'' +
				", gender='" + gender + '\'' +
				", serverno='" + serverno + '\'' +
				", zoneName='" + zoneName + '\'' +
				", balance='" + balance + '\'' +
				", power='" + power + '\'' +
				", viplevel='" + viplevel + '\'' +
				", ext='" + ext + '\'' +
				'}';
	}
}
