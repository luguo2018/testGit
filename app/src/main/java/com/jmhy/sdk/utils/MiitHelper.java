package com.jmhy.sdk.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.IIdentifierListener;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.supplier.IdSupplier;


import com.jmhy.sdk.common.JMApplication;

public class MiitHelper implements IIdentifierListener {

    private AppIdsUpdater _listener;
    public MiitHelper(AppIdsUpdater callback){
        _listener=callback;
    }


    public interface AppIdsUpdater {
        void OnIdsAvalid(@NonNull String ids);
    }

    @Override
    public void OnSupport(boolean isSupport, IdSupplier _supplier) {


        if (_supplier == null) {
            return;
        }


        String oaid = _supplier.getOAID();
        Log.e("aaaaa", "return oaid: " + oaid);
        if (_listener != null) {
            _listener.OnIdsAvalid(oaid);
        }
    }

    public void getDeviceIds(Context cxt) {
        long timeb = System.currentTimeMillis();
        int nres = CallFromReflect(cxt);
        //        int nres=DirectCall(cxt);
        long timee = System.currentTimeMillis();
        long offset = timee - timeb;

        Log.e("aaaaa", "return value: " + String.valueOf(nres));

    }

    /*
     * 通过反射调用，解决android 9以后的类加载升级，导至找不到so中的方法
     *
     * */
    private int CallFromReflect(Context cxt) {
        return MdidSdkHelper.InitSdk(cxt, true, this);
    }

}
