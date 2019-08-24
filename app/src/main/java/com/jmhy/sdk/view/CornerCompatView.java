package com.jmhy.sdk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.jmhy.sdk.config.AppConfig;

/**
 * create by yhz on 2018/10/8
 */
public class CornerCompatView extends FrameLayout {
    private CornerView cornerView;

    public CornerCompatView(Context context) {
        this(context, null);
    }

    public CornerCompatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerCompatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if(Build.VERSION.SDK_INT < VERSION_CODES.P){
            cornerView = new CornerPerLView();
        }else{
            cornerView = new CornerLView();
        }

        //cornerView = new CornerPerLView();

        /*float radius;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CornerCompatView, defStyleAttr, 0);
        radius = a.getDimension(R.styleable.CornerCompatView_jm_radius, 5);
        a.recycle();*/
        int[] attr = new int[1];
        attr[0] = AppConfig.resourceId(context, "jm_radius", "attr");
        int radius;
        TypedArray a = context.obtainStyledAttributes(attrs, attr, defStyleAttr, 0);
        radius = (int)a.getDimension(0, 5);
        a.recycle();

        cornerView.init(this, radius);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        cornerView.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        cornerView.drawBefore(canvas);
        super.draw(canvas);
        cornerView.drawAfter(canvas);
    }

    /*private boolean unSupport(){
        Log.i("CornerCompatView", "Build.BRAND = " + Build.BRAND);
        if(TextUtils.equals(Build.BRAND, "OPPO") || TextUtils.equals(Build.BRAND, "Meizu")){
            return true;
        }
        return false;
    }*/

    public interface CornerView{
        void init(View view, float radius);
        void onLayout(boolean changed, int left, int top, int right, int bottom);
        void drawBefore(Canvas canvas);
        void drawAfter(Canvas canvas);
    }
}
