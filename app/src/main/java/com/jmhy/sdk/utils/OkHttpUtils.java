package com.jmhy.sdk.utils;

import com.huosdk.huounion.sdk.okhttp3.FormBody;
import com.huosdk.huounion.sdk.okhttp3.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OkHttpUtils {
    public static RequestBody hasMapToRequestBody(HashMap<String, String> parameter) {
        if (parameter.isEmpty()) {
            return null;
        }
        Set<String> keys = parameter.keySet();
        FormBody.Builder builder = new FormBody.Builder();
        for (String s : keys) {
            builder.add(s,parameter.get(s));
        }
        return builder.build();
    }
}
