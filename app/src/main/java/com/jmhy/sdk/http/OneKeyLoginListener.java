package com.jmhy.sdk.http;


public interface OneKeyLoginListener {


    void onSuccess(String obj);

    void showAutoLoginSuccess();

    void onError(String msg );

    void onCancle(String msg );
}
