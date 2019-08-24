package com.jmhy.sdk.view;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import com.jmhy.sdk.view.CornerCompatView.CornerView;

/**
 * create by yhz on 2018/10/8
 */
public class CornerLView implements CornerView {

    public void init(View view, final float radius) {
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });
        view.setClipToOutline(true);

        if(view instanceof ViewGroup){
            ViewGroup parent = (ViewGroup)view;
            parent.setClipChildren(true);
        }
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    @Override
    public void drawBefore(Canvas canvas) {

    }

    @Override
    public void drawAfter(Canvas canvas) {

    }
}
