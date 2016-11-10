package com.lht.paintview.pojo;

import android.graphics.Canvas;
import android.graphics.Matrix;


/**
 * Created by lht on 16/10/17.
 */

public abstract class DrawShape {
    StrokePaint paint;

    public abstract void draw(Canvas canvas, Matrix matrix);
}
