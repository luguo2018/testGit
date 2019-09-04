package com.jmhy.sdk.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * create by yhz on 2018/11/12
 */
public class ConfigUtils {
    private final static String TAG = ConfigUtils.class.getSimpleName();

    public static JSONObject getConfigData(Context context){
        StringBuilder builder = new StringBuilder();
        BufferedReader bufReader = null;
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getAssets().open("jmhy_config.json"));
            bufReader = new BufferedReader(inputReader);
            String line="";
            while ((line = bufReader.readLine()) != null){
                builder.append(line);
            }
        } catch (IOException e) {
            Log.w(TAG, e.getMessage());
            return null;
        } finally {
            if(bufReader != null){
                try {
                    bufReader.close();
                } catch (IOException e) {
                    Log.w(TAG, e.getMessage());
                }
            }
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(builder.toString());
        } catch (JSONException e) {
            Log.w(TAG, e.getMessage());
        }
        return jsonObject;
    }
}
