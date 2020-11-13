package com.jmhy.sdk.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jmhy.sdk.config.AppConfig;

public class AdTipDialog extends Dialog {
    private Context mContext;
    private View mView;
    private AdTipDialogListener mExitdialoglistener;
    private TextView ad_tip_text;
    private String data;
    public AdTipDialog(Context context, int theme, String data,AdTipDialogListener exitListener) {
        super(context, theme);
        this.mContext = context;
        this.mExitdialoglistener = exitListener;
        this.data = data;

        switch (AppConfig.skin) {
            case 9:
                this.mView = LayoutInflater.from(context).inflate(AppConfig.resourceId(context, "jm_ad_tip_dialog", "layout"), null);
                break;
            default:
                this.mView = LayoutInflater.from(context).inflate(AppConfig.resourceId(context, "jm_ad_tip_dialog", "layout"), null);
                break;
        }
    }

    public interface AdTipDialogListener {
        void onStratAd();
    }
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);

        ad_tip_text = (TextView) mView.findViewById(AppConfig.resourceId(mContext, "ad_tip_text", "id"));
        if(ad_tip_text!=null){
            ad_tip_text.setText(data);
        }

        if (handler==null){
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mExitdialoglistener.onStratAd();
                handler=null;
                dismiss();
            }
        },1000);

    }

}
