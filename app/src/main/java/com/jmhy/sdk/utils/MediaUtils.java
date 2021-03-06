package com.jmhy.sdk.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.jmhy.sdk.common.JiMiSDK;



import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MediaUtils {
    private final static String TAG = MediaUtils.class.getSimpleName();
    private static Bitmap bitmap;
    public Bitmap urlReturnBitMap(final String url){
        byte[] bitmapArray =android.util.Base64.decode(url.split(",")[1], android.util.Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        return bitmap;
    }

    public static Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;

            int length = http.getContentLength();

            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    public static void saveImage(Activity context, Bitmap bitmap){
        File dir;
        if(VERSION.SDK_INT < VERSION_CODES.Q){
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        }else{
//            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
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


    public static String getImagePath(String imgUrl, Context context) {
        String path = null;
        FutureTarget<File> future = Glide.with(context)
                .load(imgUrl)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        try {
            File cacheFile = future.get();
            path = cacheFile.getAbsolutePath();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static void copyFile(String oldPath, final String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
