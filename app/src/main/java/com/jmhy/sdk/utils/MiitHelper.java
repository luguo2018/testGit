package com.jmhy.sdk.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.supplier.IIdentifierListener;
import com.bun.supplier.IdSupplier;
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
        Log.e("aaaaa","OnSupport  = " + isSupport );
        Log.e("aaaaa","_supplier  = " + _supplier );

//        if (!isSupport)
//        {
//            return;
//        }

        if (_supplier == null) {
            return;
        }

        JMApplication.setIsSupportOaid(isSupport);

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
        if (nres == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT) {//1008612 不支持的设备
            JMApplication.setIsSupportOaid(false, nres);
        } else if (nres == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE) {//1008613 加载配置文件出错
            JMApplication.setIsSupportOaid(false, nres);
        } else if (nres == ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT) {//1008611 不支持的设备厂商
            JMApplication.setIsSupportOaid(false, nres);
        } else if (nres == ErrorCode.INIT_ERROR_RESULT_DELAY) {//1008614 获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程
            //TODO 这种情况，还不清楚怎么解决呢。暂且先返false
            JMApplication.setIsSupportOaid(false, nres);
        } else if (nres == ErrorCode.INIT_HELPER_CALL_ERROR) {//1008615 反射调用出错
            JMApplication.setIsSupportOaid(false, nres);
        }
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
