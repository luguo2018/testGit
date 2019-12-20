package com.jmhy.sdk.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.jmhy.sdk.common.JiMiSDK;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * create by yhz on 2018/12/6
 */
public class MediaUtils {
    private final static String TAG = MediaUtils.class.getSimpleName();

    public static void saveImage(Activity context, Bitmap bitmap){
        File dir;
        if(VERSION.SDK_INT < VERSION_CODES.Q){
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        }else{
            dir = JiMiSDK.mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        File path = new File(dir, str + ".jpg");

        if(VERSION.SDK_INT < VERSION_CODES.Q){
            saveImage(context, path, bitmap);
        }else{
            saveImage_29(context, bitmap);
        }
    }

    private static void saveImage_29(Context context, Bitmap bitmap){
        Uri uri = insertImage(context, null);
        saveImage(context, uri, bitmap);
    }

    private static void saveImage(Context context, File path, Bitmap bitmap){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            refreshFile(context, path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void saveImage(Context context, Uri imageFile, Bitmap bitmap){
        ContentResolver contentResolver = context.getContentResolver();

        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = contentResolver.openFileDescriptor(imageFile, "w");
            if(parcelFileDescriptor == null){
                Log.i(TAG, "parcelFileDescriptor is null");
                return;
            }
            FileOutputStream outputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(parcelFileDescriptor != null){
                try {
                    parcelFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //插入数据库
    private static Uri insertImage(Context context, File imageFile) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg");
        if(imageFile != null) {
            if (VERSION.SDK_INT >= VERSION_CODES.Q) {
                values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, imageFile.getParentFile().getName());
                values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, imageFile.lastModified());
            }else{
                values.put(MediaStore.Images.ImageColumns.DATA, imageFile.getPath());
            }
        }

        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private static void refreshFile(final Context context, File file){
        final Uri uri = Uri.fromFile(file);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.sendBroadcast(intent);

                Log.i(TAG, "refreshFile uri = " + uri);
            }
        });
    }
}
