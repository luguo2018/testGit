package com.jmhy.sdk.http;


public interface OneKeyLoginListener {


    void onSuccess(String obj);


    void onError(String statusCode);
}
