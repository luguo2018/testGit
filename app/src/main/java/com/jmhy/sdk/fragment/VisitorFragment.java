package com.jmhy.sdk.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jmhy.sdk.config.AppConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * create by yhz on 2018/8/16
 * 英文版游客登录
 */
public class VisitorFragment extends JmBaseFragment {
    private EditText email, password;

    private String upass;
    private String result;
    private String msg;
    private String uname;
    private String gametoken;
    private String openid;
    private String url;

    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(AppConfig.resourceId(getActivity(),
                "jmsetuser_en", "layout"), container, false);
        view.setClickable(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = (EditText)view.findViewById(AppConfig.resourceId(getActivity(), "email", "id"));
        password = (EditText)view.findViewById(AppConfig.resourceId(getActivity(), "password", "id"));
        View submit = view.findViewById(AppConfig.resourceId(getActivity(), "submit", "id"));
        password.setTypeface(Typeface.DEFAULT);

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            uname = getArguments().getString("username");
            upass = getArguments().getString("upass");
            msg = getArguments().getString("msg");
            gametoken = getArguments().getString("gametoken");
            openid = getArguments().getString("openid");
            url = getArguments().getString("url");

            email.setText(uname);
            password.setText(upass);

            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    saveCurrentImage();
                }
            }, 1000);
        }
    }

    @Override
    public void onDestroy() {
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    private void login(){
        wrapaLoginInfo("success", msg, uname, openid, gametoken);

        turnToIntent(url);
        getActivity().finish();
    }

    /**
     * 截屏
     */
    private void saveCurrentImage() {
        // 获取当前屏幕的大小
        // 生成相同大小的图片
        Bitmap temBitmap;
        // 找到当前页面的跟布局
        View view = getActivity().getWindow().getDecorView().getRootView();
        // 设置缓存
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        // 从缓存中获取当前屏幕的图片
        temBitmap = view.getDrawingCache();
        String path = Environment.getExternalStorageDirectory() + File.separator
                + "DCIM" + File.separator + "Camera" + File.separator;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        String fname = path + str + ".jpg";
        // 输出到sd卡
        if (temBitmap != null) {
            // System.out.println("bitmapgot!");
            try {

                FileOutputStream out = new FileOutputStream(fname);

                temBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                // System.out.println("file" + fname + "outputdone.");
                String snapshot = AppConfig.getString(getActivity(), "snapshot_save");
                showMsg(snapshot + path);

            } catch (Exception e) {
            }
            try {
                MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                        file.getAbsolutePath(), str + ".jpg", null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fname)));
        }
    }
}
