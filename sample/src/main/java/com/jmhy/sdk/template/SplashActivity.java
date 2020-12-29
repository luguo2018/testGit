package com.jmhy.sdk.template;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.sample.MainActivity;

public class SplashActivity extends Activity {
    private TextView version_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(AppConfig.resourceId(this, "jm_health_notice", "layout"));
        version_tv=findViewById(AppConfig.resourceId(this, "version_tv", "id"));
        if (!getAppVersionName(this).equals("")){
            version_tv.setText("版本号："+getAppVersionName(this));
        }
        version_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                endSplash();
            }
        });
        startMain();
    }
    private Handler splashHandle = new Handler();

    private void startMain(){
        splashHandle.postDelayed(new Runnable(){
            public void run(){
                endSplash();
            }
        },2000);

    }

    private static String getAppVersionName(Context context) {
        String versionName="";
        try {
            PackageManager packageManager=context.getPackageManager();
            PackageInfo packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
            if (packageInfo.versionName!=null) {
                versionName = packageInfo.versionName;
            }
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private void endSplash() {
        splashHandle =null;
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

}
