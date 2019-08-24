package com.jmhy.sdk.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.utils.DisplayUtil;

/**
 * create by yhz on 2018/9/29
 */
public class TipDialog extends Dialog {
    public TipDialog(Context context, String message) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int color = AppConfig.resourceId(getContext(), "jm_transparent", "color");
        getWindow().setBackgroundDrawableResource(color);

        int layout = AppConfig.resourceId(getContext(), "jm_dialog_ios", "layout");
        View view = getLayoutInflater().inflate(layout, null);
        TextView textView = (TextView) view.findViewById(AppConfig.resourceId(getContext(), "content", "id"));
        TextView button = (TextView) view.findViewById(AppConfig.resourceId(getContext(), "confirm", "id"));
        textView.setText(message);
        setContentView(view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = DisplayUtil.dip2px(context, 250);
    }
}
