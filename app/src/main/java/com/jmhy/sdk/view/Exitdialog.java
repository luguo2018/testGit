package com.jmhy.sdk.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.jmhy.sdk.config.AppConfig;

public class Exitdialog extends Dialog implements android.view.View.OnClickListener {
	private Context mContext;
	private View mView;
	private ExitDialogListener mExitdialoglistener;
	private Button mExitebt;
	private Button mCancelbt;

	public Exitdialog(Context context, int theme, ExitDialogListener exitListener) {
		super(context, theme);
		this.mContext = context;
		this.mExitdialoglistener = exitListener;

		switch (AppConfig.skin){
			case 6:
				this.mView = LayoutInflater.from(context).inflate(
						AppConfig.resourceId(context, "jmexitdialog_6", "layout"),
						null);
				break;
			case 5:
			case 4:
				this.mView = LayoutInflater.from(context).inflate(
						AppConfig.resourceId(context, "jmexitdialog_4", "layout"),
						null);
				break;
			case 3:
				this.mView = LayoutInflater.from(context).inflate(
						AppConfig.resourceId(context, "jmexitdialog_3", "layout"),
						null);
				break;
			case 2:
				this.mView = LayoutInflater.from(context).inflate(
						AppConfig.resourceId(context, "jmexitdialog_new", "layout"),
						null);
				break;
			default:
				this.mView = LayoutInflater.from(context).inflate(
						AppConfig.resourceId(context, "jmexitdialog", "layout"),
						null);
		}
	}

	public interface ExitDialogListener {
		void onExit();
		void onCancel();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(mView);
		mExitebt = (Button) findViewById(AppConfig.resourceId(mContext,"dialog_exit", "id"));
		mCancelbt = (Button)findViewById(AppConfig.resourceId(mContext,"dialog_cancel", "id"));
		mExitebt.setOnClickListener(this);
		mCancelbt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mExitebt.getId()) {
			mExitdialoglistener.onExit();
		} else if (v.getId() == mCancelbt.getId()) {
			mExitdialoglistener.onCancel();
		}
		dismiss();
	}
}
