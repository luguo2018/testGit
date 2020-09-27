package com.jmhy.sdk.http;

import com.huosdk.gson.internal.$Gson$Types;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 请求接口
 * @param <T> 传入Result<T> T为实体类
 */
public abstract class ResponseCallback<T> {
    Type mType;
    public ResponseCallback(){
        Type superclass  = getClass().getGenericSuperclass();
        if(superclass instanceof Class){
            mType =null;
            throw new RuntimeException("请传入实体类");
        }else {
            ParameterizedType parameterizedType = (ParameterizedType) superclass;
            mType = $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);

        }
    }
    //请求成功回调事件处理
    public abstract void onSuccess(T t);

    //请求失败回调事件处理
    public abstract void onFailure(OkHttpException e);
}
