package com.jmhy.sdk.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jmhy.sdk.config.AppConfig;

public class HideDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private View mView;
    private HideDialogListener mExitdialoglistener;
    private TextView mDelete;
    private TextView mCancelbt;

    public HideDialog(Context context, int theme, HideDialogListener exitListener) {
        super(context, theme);
        this.mContext = context;
        this.mExitdialoglistener = exitListener;

        switch (AppConfig.skin) {
            case 9:
                this.mView = LayoutInflater.from(context).inflate(AppConfig.resourceId(context, "jm_hide_dialog", "layout"), null);
                break;
            default:
                this.mView = LayoutInflater.from(context).inflate(AppConfig.resourceId(context, "jm_hide_dialog", "layout"), null);
                break;
        }
    }

    public interface HideDialogListener {
        void onConfirm();
        void onCancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);
        mDelete = (TextView) findViewById(AppConfig.resourceId(mContext, "dialog_delete", "id"));
        mCancelbt = (TextView) findViewById(AppConfig.resourceId(mContext, "dialog_cancel", "id"));
        mDelete.setOnClickListener(this);
        mCancelbt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mDelete.getId()) {
            mExitdialoglistener.onConfirm();
        } else if (v.getId() == mCancelbt.getId()) {
            mExitdialoglistener.onCancel();
        }
        dismiss();
    }
}
