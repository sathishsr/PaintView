package com.lht.paintview.pojo;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * Created by lht on 16/11/10.
 */

public class DrawRect extends DrawShape {

    private Rect rect;

    public DrawRect(Rect rect, StrokePaint paint) {
        this.rect = rect;
        this.paint = paint;
    }

    public void setRect(Rect r) {
        rect.set(r);
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix) {
        canvas.drawRect(rect, paint);
    }
}
