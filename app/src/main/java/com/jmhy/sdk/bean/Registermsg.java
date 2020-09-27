package com.jmhy.sdk.bean;

public class Registermsg {
    private String auto_login_token;
    private String uname;


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

    @Override
    public String toString() {
        return "Registermsg{" +
                ", auto_login_token='" + auto_login_token + '\'' +
                ", uname='" + uname + '\'' +
                '}';
    }
}
