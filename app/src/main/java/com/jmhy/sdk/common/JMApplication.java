package com.jmhy.sdk.common;

import android.app.Application;

public class JMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //DealCrash crashHandler = DealCrash.getInstance();
        //crashHandler.init(this);
        JiMiSDK.onApplicationOnCreate(this);

    }

}
