package com.jmhy.sdk.utils;

import android.app.Activity;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by yhz on 2015/9/22.
 */
public class ActivityStackManager {
    private LinkedList<WeakReference<Activity>> activityStack = new LinkedList<>();

    public void pushActivity(Activity activity){

        WeakReference<Activity> reference = new WeakReference<>(activity);
        activityStack.push(reference);
        Log.i("JimiSDK", "activityStack = " + activityStack.size() + "  push activity = " + activity.toString());

    }

    public Activity popActivity(){
        WeakReference<Activity> reference = activityStack.pop();
        if(reference == null){
            return null;
        }
        return reference.get();
    }

    public boolean isEmpty(){
        return activityStack.isEmpty();
    }

    public void removeActivity(Activity activity){
        Iterator<WeakReference<Activity>> i = activityStack.iterator();
        while (i.hasNext()){
            WeakReference<Activity> reference = i.next();
            Activity a = reference.get();
            if(a == null){
                i.remove();
                continue;
            }

            if(a == activity){
                i.remove();
                break;
            }
        }
    }

    public Activity getTopActivity() {
        WeakReference<Activity> reference = activityStack.peek();
        if(reference == null){
            return null;
        }
        return reference.get();
    }

    public Activity getBottomActivity() {
        Log.i("JiMiSDK", "activityStack getBottomactivity = " + activityStack.size());
        if (activityStack.size() == 0){
            return null;
        }
        WeakReference<Activity> reference = activityStack.get(0);
        if(reference == null){
            return null;
        }
        return reference.get();
    }

    public void clearAllActivity(){
        for (WeakReference<Activity> reference : activityStack){
            Activity a = reference.get();
            if(a == null){
                continue;
            }

            a.finish();
        }

        activityStack.clear();
    }
}
