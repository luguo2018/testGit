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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.view.PermissionDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * create by yhz on 2018/9/21
 */
public class PermissionActivity extends Activity {
    private static PermissionResultListener listener;
    private static List<String> permissionList;

    private final static int REQUEST_PERMISSION = 0x01;
    private final static int REQUEST_FLOAT = 0x02;
    List<String> list;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("JiMiSDK","PermissionActivity oncreate");
        if(VERSION.SDK_INT < VERSION_CODES.M){
            onPermissionRequestResult(true);
            return;
        }

        if(permissionList == null){
            requestFloatPermission();
            return;
        }

        list = new ArrayList<>();
        for(String permission : permissionList){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                list.add(permission);
            }
        }
        Log.i("jimi",list+"chakan查看"+list.toString());
        if(list.isEmpty()){
            onPermissionRequestResult(true);
            return;
        }
//        ActivityCompat.requestPermissions(PermissionActivity.this, toStringFormat(list), REQUEST_PERMISSION);
        ShowDialog(list);
    }

    private void ShowDialog(final List<String> list) {
        PermissionDialog exitdialog = new PermissionDialog(this, AppConfig.resourceId(this, "jm_MyDialog", "style"),list, new PermissionDialog.PermissionDialogListener() {
            @Override
            public void onAllow() {
                ActivityCompat.requestPermissions(PermissionActivity.this, toStringFormat(list), REQUEST_PERMISSION);
            }
        });
        exitdialog.setCancelable(false);
        exitdialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
//                    onPermissionRequestResult(false);
//                    ShowDialog(list);
                    Intent intent = new Intent(this, PermissionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(intent);
                    return;
                }
            }

            onPermissionRequestResult(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_FLOAT){
            boolean over = true;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                over = Settings.canDrawOverlays(this);
            }
            onPermissionRequestResult(over);
        }
    }

    private String[] toStringFormat(List<String> list){
        String[] arr = new String[list.size()];
        return list.toArray(arr);
    }

    private void onPermissionRequestResult(boolean grant){
        if (listener != null){
            listener.onPermissionResult(grant);

        }

        listener = null;
        permissionList = null;
        finish();
    }

    public static void requestPermission(Context context, List<String> permissionList, PermissionResultListener listener){
        if(VERSION.SDK_INT < VERSION_CODES.M){
            listener.onPermissionResult(true);
            return;
        }

        /*int sdkVersion = context.getApplicationInfo().targetSdkVersion;
        if(sdkVersion < VERSION_CODES.M) {
            listener.onPermissionResult(true);
            return;
        }*/
        if (context == null){
            context = JiMiSDK.mContext;
        }
        PermissionActivity.listener = listener;
        PermissionActivity.permissionList = permissionList;
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void requestFloatPermission(Context context, PermissionResultListener listener){
        if(VERSION.SDK_INT < VERSION_CODES.M){
            listener.onPermissionResult(true);
            return;
        }

        /*int sdkVersion = context.getApplicationInfo().targetSdkVersion;
        if(sdkVersion < VERSION_CODES.M) {
            listener.onPermissionResult(true);
            return;
        }*/

        if(Settings.canDrawOverlays(context)){
            listener.onPermissionResult(true);
            return;
        }

        PermissionActivity.listener = listener;
        if (context == null){
            context = JiMiSDK.mContext;
        }
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void requestFloatPermission(){
        /*int sdkVersion = getApplicationInfo().targetSdkVersion;
        if(sdkVersion < VERSION_CODES.M) {
            onPermissionResult(true);
            return;
        }*/

        Log.e("JiMiSDK","requestFloatPermission--------");
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this) && this.getPackageName() != null) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, REQUEST_FLOAT);
            }else {
                onPermissionRequestResult(true);
            }
        }else{
            onPermissionRequestResult(true);
        }
    }

    public interface PermissionResultListener{
        void onPermissionResult(boolean grant);
    }
}
