package com.lht.paintview.pojo;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by lht on 16/10/17.
 */

public abstract class DrawShape {
    Paint paint;

    public abstract void draw(Canvas canvas);
}
