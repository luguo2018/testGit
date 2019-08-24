package com.jmhy.sdk.model;

/**
 * create by yhz on 2018/8/3
 */
public class BaseResponse {
    public String message;
    public String code;

    public final static String SUCCESS = "0";
    public final static String DEVICE_DENY_ACCESS = "44103";
    public final static String USER_DENY_ACCESS = "44104";
}
