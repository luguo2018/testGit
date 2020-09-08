package com.jmhy.sdk.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jmhy.sdk.config.AppConfig;

public class PermissionDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private View mView;
    private PermissionDialogListener mExitdialoglistener;
    private TextView mDelete;

    public PermissionDialog(Context context, int theme, PermissionDialogListener exitListener) {
        super(context, theme);
        this.mContext = context;
        this.mExitdialoglistener = exitListener;

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

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mDelete.getId()) {
            mExitdialoglistener.onAllow();
        }
        dismiss();
    }
}
