package com.jmhy.sdk.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmhy.sdk.ad.BuildConfig;
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
    private TextView confirm_tv,centent_tv;
    private ImageView state1,state2;
    private View mView;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean toSetting=false;
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
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            show(list);
                        }
                    };

//                    show(list);
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
        if (mView != null) {
            state1.setImageResource(AppConfig.resourceId(this, "jm_permission_on2", "drawable"));
            state2.setImageResource(AppConfig.resourceId(this, "jm_permission_on2", "drawable"));
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    state1.setImageResource(AppConfig.resourceId(this, "jm_permission_off2", "drawable"));
                }

                if (list.get(i).equals(Manifest.permission.READ_PHONE_STATE)) {
                    state2.setImageResource(AppConfig.resourceId(this, "jm_permission_off2", "drawable"));
                }
            }

            mView.setVisibility(View.VISIBLE);
        }
    }
    private void init(final List<String> list) {
        state1 = (ImageView) findViewById(AppConfig.resourceId(this, "state1", "id"));
        state2 = (ImageView) findViewById(AppConfig.resourceId(this, "state2", "id"));
        centent_tv = (TextView) findViewById(AppConfig.resourceId(this, "centent_tv", "id"));
        confirm_tv = (TextView) findViewById(AppConfig.resourceId(this, "confirm_tv", "id"));
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toSetting){
//                    Intent intent = new Intent();
//                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                    intent.setData(Uri.fromParts("package", getPackageName(), null));
//                    startActivity(intent);


                    String brand = Build.BRAND;//手机厂商
                    if (TextUtils.equals(brand.toLowerCase(), "redmi") || TextUtils.equals(brand.toLowerCase(), "xiaomi")) {
                        gotoMiuiPermission();//小米
                    } else if (TextUtils.equals(brand.toLowerCase(), "meizu")) {
                        gotoMeizuPermission();
                    } else if (TextUtils.equals(brand.toLowerCase(), "huawei") || TextUtils.equals(brand.toLowerCase(), "honor")) {
                        gotoHuaweiPermission();
                    } else if (TextUtils.equals(brand.toLowerCase(), "Sony")) {
                        gotoSuoNiPermission();
                    } else if (TextUtils.equals(brand.toLowerCase(), "OPPO") ) {
                        gotoOPPOPermission();
                    } else if (TextUtils.equals(brand.toLowerCase(), "LG")) {
                        gotoLGPermission();
                    } else if (TextUtils.equals(brand.toLowerCase(), "Letv")) {
                        gotoLeShiPermission();
                    } else {
                        startActivity(getAppDetailSettingIntent());
                    }


                }else{
                    toSetting=true;
                    if (list.size() > 0) {
                        ActivityCompat.requestPermissions(PermissionActivity.this, toStringFormat(list), REQUEST_PERMISSION);
                    }
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
        Log.i("jimi","权限测试"+requestCode+"\n"+permissions[0]+"\n"+grantResults[0]);
        if (requestCode == REQUEST_PERMISSION) {
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    centent_tv.setTextColor(Color.RED);
                    centent_tv.setText("为确保您能正常使用,需开启以下权限,请点击授权前往设置界面授予下列权限:");

                    list.clear();
                    for (String permission : permissionList) {
//                        Intent intent = new Intent();
//                        intent.setAction("android.permission.READ_PHONE_STATE");
//                        intent.setData(Uri.fromParts("package", getPackageName(), null));
//                        this.startActivity(intent);
                        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                            list.add(permission);
                        }
                    }
                    show(list);
//

//                    finish();
//                    Intent intent = new Intent();
//                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                    intent.setData(Uri.fromParts("package", getPackageName(), null));
//                    startActivity(intent);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        onPermissionRequestResult(true);
    }


    /**
     * 跳转到miui的权限管理页面
     */
    private void gotoMiuiPermission() {
        try { // MIUI 8
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", getPackageName());
            startActivity(localIntent);
        } catch (Exception e) {
            try { // MIUI 5/6/7
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", getPackageName());
                startActivity(localIntent);
            } catch (Exception e1) { // 否则跳转到应用详情
                startActivity(getAppDetailSettingIntent());
            }
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private void gotoMeizuPermission() {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", getPackageName());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(getAppDetailSettingIntent());
        }
    }

    /**
     * 华为的权限管理页面
     */
    private void gotoHuaweiPermission() {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(getAppDetailSettingIntent());
        }

    }
    /**
     * 索尼的权限管理页面
     */
    private void gotoSuoNiPermission() {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
            intent.setComponent(comp);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(getAppDetailSettingIntent());
        }

    }

    /**
     * OPPO的权限管理页面
     */
    private void gotoOPPOPermission() {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
            intent.setComponent(comp);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(getAppDetailSettingIntent());
        }

    }

    /**
     * LG的权限管理页面
     */
    private void gotoLGPermission() {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
            intent.setComponent(comp);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(getAppDetailSettingIntent());
        }

    }

    /**
     * 乐视的权限管理页面
     */
    private void gotoLeShiPermission() {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
            intent.setComponent(comp);
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(getAppDetailSettingIntent());
        }

    }

    /**
     * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
     *
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        return localIntent;
    }

}
