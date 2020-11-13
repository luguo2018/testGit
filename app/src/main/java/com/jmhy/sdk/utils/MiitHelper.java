//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jmhy.sdk.utils;

import android.content.Context;
import android.util.Log;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.supplier.IIdentifierListener;
import com.bun.supplier.IdSupplier;

public class MiitHelper implements IIdentifierListener {
    private com.jmhy.sdk.utils.MiitHelper.AppIdsUpdater _listener;

    public MiitHelper(com.jmhy.sdk.utils.MiitHelper.AppIdsUpdater callback) {
        this._listener = callback;
    }

    public void OnSupport(boolean isSupport, IdSupplier _supplier) {
        Log.e("aaaaa", "OnSupport  = " + isSupport);
        Log.e("aaaaa", "_supplier  = " + _supplier);
        if (_supplier != null) {
            String oaid = _supplier.getOAID();
            Log.e("aaaaa", "return oaid: " + oaid);
            if (this._listener != null) {
                this._listener.OnIdsAvalid(oaid);
            }

        }
    }

    public void getDeviceIds(Context cxt) {
        long timeb = System.currentTimeMillis();
        int nres = this.CallFromReflect(cxt);
        long timee = System.currentTimeMillis();
        long var10000 = timee - timeb;
        Log.e("aaaaa", "return value: " + String.valueOf(nres));
    }

    private int CallFromReflect(Context cxt) {
        return MdidSdkHelper.InitSdk(cxt, true, this);
    }


    public interface AppIdsUpdater {
        void OnIdsAvalid(String var1);

    }
}
