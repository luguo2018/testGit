package com.jmhy.sdk.common;

import android.app.Application;
import android.util.Log;

public class JMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //DealCrash crashHandler = DealCrash.getInstance();
        //crashHandler.init(this);
        Log.i("JMApplication", "onCreate");
    }
}
