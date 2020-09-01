package com.jmhy.sdk.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.jmhy.sdk.config.AppConfig;

public class DeleteDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private View mView;
    private DeleteDialogListener mExitdialoglistener;
    private Button mDelete;
    private Button mCancelbt;

    public DeleteDialog(Context context, int theme, DeleteDialogListener exitListener) {
        super(context, theme);
        this.mContext = context;
        this.mExitdialoglistener = exitListener;

        switch (AppConfig.skin) {
            case 9:
                this.mView = LayoutInflater.from(context).inflate(AppConfig.resourceId(context, "jm_delete_dialog", "layout"), null);
                break;
            default:
                this.mView = LayoutInflater.from(context).inflate(AppConfig.resourceId(context, "jm_delete_dialog", "layout"), null);
                break;
        }
    }

    public interface DeleteDialogListener {
        void onDelete();
        void onCancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);
        mDelete = (Button) findViewById(AppConfig.resourceId(mContext, "dialog_delete", "id"));
        mCancelbt = (Button) findViewById(AppConfig.resourceId(mContext, "dialog_cancel", "id"));
        mDelete.setOnClickListener(this);
        mCancelbt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mDelete.getId()) {
            mExitdialoglistener.onDelete();
        } else if (v.getId() == mCancelbt.getId()) {
            mExitdialoglistener.onCancel();
        }
        dismiss();
    }
}
