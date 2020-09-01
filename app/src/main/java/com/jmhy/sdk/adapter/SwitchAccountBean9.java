package com.jmhy.sdk.adapter;

public class SwitchAccountBean9 {
    private String account;
    private String time;
    private String loginType;
    private boolean isLastLogin;

    public SwitchAccountBean9(String account, String time, String loginType, boolean isLastLogin) {
        this.account = account;
        this.time = time;
        this.loginType = loginType;
        this.isLastLogin = isLastLogin;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public boolean isLastLogin() {
        return isLastLogin;
    }

    public void setLastLogin(boolean lastLogin) {
        isLastLogin = lastLogin;
    }
}
