package com.jmhy.sdk.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;


public class AndroidBug5497Workaround {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
    private static int downScrollBy = 0, scrollByHeight = 0, clickHeight = 0;
    private static View mChildOfContent;
    private static int usableHeightPrevious;//当前可用高度
    private static int usableHeightPrevious2;//当前可用高度2 view专用
    private static FrameLayout.LayoutParams frameLayoutParams;
    private static WindowManager.LayoutParams windowParams;

    public static void assistActivity(Activity activity) {
        new AndroidBug5497Workaround(activity);
    }



    private AndroidBug5497Workaround(final Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent(activity);
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();

    }
    private void possiblyResizeChildOfContent(Activity activity) {//调整子内容的大小
        int usableHeightNow = computeUsableHeight();//计算键盘高度
        //当前可见高度和上一次可见高度不一致 布局变动
        Log.i("测试","计量高度"+usableHeightNow+"上次高度:"+usableHeightPrevious);
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight(); //屏幕可用高度
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;  //高度差=屏幕可用高度-键盘高
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                //界面的高度变化超过1/4的屏幕高度，才会进行重新设置高度，能保证响应软键盘的弹出
                // 键盘弹出
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
//                Log.i("测试日志5", "弹出键盘" + usableHeightSansKeyboard + "possiblyResizeChildOfContent: " + frameLayoutParams.height);
                Log.i("测试日志5", "弹出键盘，屏幕高" + usableHeightSansKeyboard + "键盘高: " + usableHeightNow + "计算偏移量：");

//                downScrollBy = usableHeightNow ;//向上偏移 键盘的高度（411）
//                scrollByHeight += downScrollBy;  //偏移量
//                if ( scrollByHeight < (downScrollBy*2) ){
//                    mChildOfContent.scrollBy(0, downScrollBy);
//                    Log.i("测试偏移量","向上偏移量"+ scrollByHeight +"---限值"+(downScrollBy*2));
//                }else{
//                    scrollByHeight =0;
//                }
            }
            else {
                // 隐藏键盘
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
//                if (downScrollBy == 0) {//第一次进来走到这回调不做处理  后续如弹出键盘有偏移值  收起键盘时往回偏移
//                    Log.i("测试日志5", "首次收起键盘屏幕高" + usableHeightSansKeyboard + "偏移usableHeightNow: " + (-usableHeightNow));
//                } else {
//                    scrollByHeight -= downScrollBy;
//                    Log.i("测试偏移量","向下偏移量"+ scrollByHeight +"---限值"+(-downScrollBy));
//                    if ( scrollByHeight > (-downScrollBy) ){
//                        mChildOfContent.scrollBy(0, -downScrollBy);
//                    }else{
//                        scrollByHeight =0;
//                    }
//                }
                Log.i("测试日志5", "隐藏键盘" + usableHeightSansKeyboard + "possiblyResizeChildOfContent: " + frameLayoutParams.height);
//                if (heightDifference + 1 >=HasNotchInScreenUtil.getStatusBarHeight(activity)) {
//                    // 如果高度差大于导航栏高度，则认为此时虚拟导航栏显示
//                    frameLayoutParams.height = usableHeightSansKeyboard - HasNotchInScreenUtil.getStatusBarHeight(activity);
//                } else {
//                    // 其他情况直接设置为可视高度即可
//                    frameLayoutParams.height = usableHeightNow;
//                }
            }
            mChildOfContent.requestLayout();//请求布局 例：点击聊天框 刷新焦点
            usableHeightPrevious = usableHeightNow;
        }
    }

    private static int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return r.bottom - r.top;// 全屏模式下： return r.bottom  非全屏r.bottom - r.top
    }
    private static int computeUsableHeight2(View view) {
        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);
        return r.bottom - r.top;// 全屏模式下： return r.bottom  非全屏r.bottom - r.top
    }




}