package com.jmhy.sdk.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jmhy.sdk.common.JMSDK;
import com.jmhy.sdk.view.TipDialog;

import java.lang.ref.WeakReference;

/**
 * create by yhz on 2018/10/8
 */
public class ForceActivity extends Activity {
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ForceHandler handler = new ForceHandler(this);

        String message = getIntent().getStringExtra("message");

        dialog = new TipDialog(this, message);
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
        dialog.show();

        handler.sendEmptyMessageDelayed(0, 10000);
    }

    public void hidden(){
        dialog.dismiss();
    }

    @Override
    public void finish() {
        super.finish();

        JMSDK.hideFloat();
        JMSDK.clearAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private static class ForceHandler extends Handler{
        WeakReference<ForceActivity> reference;

        ForceHandler(ForceActivity activity){
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ForceActivity activity = reference.get();
            if(activity == null){
                return;
            }

            activity.hidden();
        }
    }
}
