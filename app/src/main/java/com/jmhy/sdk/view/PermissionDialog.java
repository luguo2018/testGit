package com.jmhy.sdk.view;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmhy.sdk.config.AppConfig;

import java.util.List;

public class PermissionDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private View mView;
    private PermissionDialogListener mExitdialoglistener;
    private TextView mDelete;
    private List<String> permissionList;
    private ImageView state1,state2;
    public PermissionDialog(Context context, int theme, List<String> list, PermissionDialogListener exitListener) {
        super(context, theme);
        this.mContext = context;
        this.mExitdialoglistener = exitListener;
        this.permissionList=list;
        switch (AppConfig.skin) {
            case 9:
                this.mView = LayoutInflater.from(context).inflate(AppConfig.resourceId(context, "jm_permission_dialog", "layout"), null);
                break;
            default:
                this.mView = LayoutInflater.from(context).inflate(AppConfig.resourceId(context, "jm_permission_dialog", "layout"), null);
                break;
        }
    }

    public interface PermissionDialogListener {
        void onAllow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);
        mDelete = (TextView) findViewById(AppConfig.resourceId(mContext, "dialog_delete", "id"));
        mDelete.setOnClickListener(this);
        state1 = (ImageView) findViewById(AppConfig.resourceId(mContext, "state1", "id"));
        state2 = (ImageView) findViewById(AppConfig.resourceId(mContext, "state2", "id"));
        Log.i("权限","permissionList"+permissionList);
        for (int i=0;i<permissionList.size();i++){
            if (permissionList.get(i).equals(Manifest.permission.READ_PHONE_STATE)) {
                state1.setImageResource(AppConfig.resourceId(mContext, "jm_permission_off2", "drawable"));
            }

            if (permissionList.get(i).equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                state2.setImageResource(AppConfig.resourceId(mContext, "jm_permission_off2", "drawable"));
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mDelete.getId()) {
            mExitdialoglistener.onAllow();
        }
        dismiss();
    }
}
