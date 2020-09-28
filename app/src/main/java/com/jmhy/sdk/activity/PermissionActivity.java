package com.jmhy.sdk.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * create by yhz on 2018/9/21
 */
public class PermissionActivity extends Activity {
    private static PermissionResultListener listener;
    private static List<String> permissionList;

    private final static int REQUEST_PERMISSION = 0x01;
    private final static int REQUEST_FLOAT = 0x02;
    private List<String> list;
    private TextView confirm_tv;
    private ImageView state1,state2;
    private View mView;
    private Timer mTimer;
    private TimerTask mTimerTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mView = LayoutInflater.from(this).inflate(AppConfig.resourceId(this, "jm_permission_dialog", "layout"), null);
        setContentView(mView);
        Log.e("JiMiSDK", "PermissionActivity oncreate");
        if (VERSION.SDK_INT < VERSION_CODES.M) {
            onPermissionRequestResult(true);
            return;
        }

        if (permissionList == null) {
            requestFloatPermission();
            return;
        }

        list = new ArrayList<>();
        for (String permission : permissionList) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                list.add(permission);
            }
        }
        Log.i("jimi", list + "chakan查看" + list.toString());
        if (list.isEmpty()) {
            onPermissionRequestResult(true);
            return;
        }
        init(list);

        refreshState();
    }

    private void refreshState() {
        if (mTimerTask==null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    list.clear();
                    for (String permission : permissionList) {
                        if (ContextCompat.checkSelfPermission(PermissionActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                            list.add(permission);
                        }
                    }
                    Log.i("jimi","权限测试:"+list);
                    show(list);

                    if (list.size()>0){

                    }else{
                        removeTimer();
                        onPermissionRequestResult(true);
                    }

                }
            };
        }
        if (mTimer==null){
            mTimer=new Timer();
        }

        mTimer.schedule(mTimerTask, 500, 500);
    }

    private void show(final List<String> list) {
        state1.setImageResource(AppConfig.resourceId(this, "jm_permission_on2", "drawable"));
        state2.setImageResource(AppConfig.resourceId(this, "jm_permission_on2", "drawable"));
        for (int i=0;i<list.size();i++){
            if (list.get(i).equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                state1.setImageResource(AppConfig.resourceId(this, "jm_permission_off2", "drawable"));
            }

            if (list.get(i).equals(Manifest.permission.READ_PHONE_STATE)) {
                state2.setImageResource(AppConfig.resourceId(this, "jm_permission_off2", "drawable"));
            }
        }

        mView.setVisibility(View.VISIBLE);
    }
    private void init(final List<String> list) {
        state1 = (ImageView) findViewById(AppConfig.resourceId(this, "state1", "id"));
        state2 = (ImageView) findViewById(AppConfig.resourceId(this, "state2", "id"));
        confirm_tv = (TextView) findViewById(AppConfig.resourceId(this, "confirm_tv", "id"));
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() > 0) {
                    ActivityCompat.requestPermissions(PermissionActivity.this, toStringFormat(list), REQUEST_PERMISSION);
                }
                mView.setVisibility(View.GONE);
                removeTimer();
            }
        });
        for (int i=0;i<list.size();i++){
            if (list.get(i).equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                state1.setImageResource(AppConfig.resourceId(this, "jm_permission_off2", "drawable"));
            }

            if (list.get(i).equals(Manifest.permission.READ_PHONE_STATE)) {
                state2.setImageResource(AppConfig.resourceId(this, "jm_permission_off2", "drawable"));
            }
        }

    }

    void removeTimer() {
        if (mTimerTask!=null){
            mTimerTask.cancel();
            mTimerTask=null;
        }
        if (mTimer!=null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("jimi","权限测试"+requestCode+"\n"+permissions+"\n"+grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {

                    list.clear();
                    for (String permission : permissionList) {
                        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                            list.add(permission);
                        }
                    }
                    show(list);

                    return;
                }
            }

            onPermissionRequestResult(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FLOAT) {
            boolean over = true;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                over = Settings.canDrawOverlays(this);
            }
            onPermissionRequestResult(over);
        }
    }

    private String[] toStringFormat(List<String> list) {
        String[] arr = new String[list.size()];
        return list.toArray(arr);
    }

    private void onPermissionRequestResult(boolean grant) {
        if (listener != null) {
            listener.onPermissionResult(grant);

        }

        listener = null;
        permissionList = null;
        removeTimer();
        finish();
    }

    public static void requestPermission(Context context, List<String> permissionList, PermissionResultListener listener) {
        if (VERSION.SDK_INT < VERSION_CODES.M) {
            listener.onPermissionResult(true);
            return;
        }

        /*int sdkVersion = context.getApplicationInfo().targetSdkVersion;
        if(sdkVersion < VERSION_CODES.M) {
            listener.onPermissionResult(true);
            return;
        }*/
        if (context == null) {
            context = JiMiSDK.mContext;
        }
        PermissionActivity.listener = listener;
        PermissionActivity.permissionList = permissionList;
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void requestFloatPermission(Context context, PermissionResultListener listener) {
        if (VERSION.SDK_INT < VERSION_CODES.M) {
            listener.onPermissionResult(true);
            return;
        }

        /*int sdkVersion = context.getApplicationInfo().targetSdkVersion;
        if(sdkVersion < VERSION_CODES.M) {
            listener.onPermissionResult(true);
            return;
        }*/

        if (Settings.canDrawOverlays(context)) {
            listener.onPermissionResult(true);
            return;
        }

        PermissionActivity.listener = listener;
        if (context == null) {
            context = JiMiSDK.mContext;
        }
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void requestFloatPermission() {
        /*int sdkVersion = getApplicationInfo().targetSdkVersion;
        if(sdkVersion < VERSION_CODES.M) {
            onPermissionResult(true);
            return;
        }*/

        Log.e("JiMiSDK", "requestFloatPermission--------");
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this) && this.getPackageName() != null) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, REQUEST_FLOAT);
            } else {
                onPermissionRequestResult(true);
            }
        } else {
            onPermissionRequestResult(true);
        }
    }

    public interface PermissionResultListener {
        void onPermissionResult(boolean grant);
    }
}
