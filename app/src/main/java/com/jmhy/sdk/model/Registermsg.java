package com.jmhy.sdk.model;

public class Registermsg {
    private String code;
    private String auto_login_token;
    private String uname;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAuto_login_token() {
        return auto_login_token;
    }

    public void setAuto_login_token(String auto_login_token) {
        this.auto_login_token = auto_login_token;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Registermsg{" +
                "code='" + code + '\'' +
                ", auto_login_token='" + auto_login_token + '\'' +
                ", uname='" + uname + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
