package com.jmhy.sdk.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;

import com.jmhy.sdk.view.CornerCompatView.CornerView;

/**
 * create by yhz on 2018/10/8
 */
public class CornerPerLView implements CornerView{
    private final RectF roundRect = new RectF();
    private float radius;
    private final Paint maskPaint = new Paint();
    private final Paint zonePaint = new Paint();

    private View view;

    public void init(View view, float radius) {
        this.view = view;
        this.radius = radius;

        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        zonePaint.setAntiAlias(true);
        zonePaint.setColor(Color.WHITE);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int w = view.getWidth();
        int h = view.getHeight();
        roundRect.set(0, 0, w, h);
    }

    @Override
    public void drawBefore(Canvas canvas) {
        canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawRoundRect(roundRect, radius, radius, zonePaint);
        canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
    }

    @Override
    public void drawAfter(Canvas canvas) {
        canvas.restore();
    }
}
