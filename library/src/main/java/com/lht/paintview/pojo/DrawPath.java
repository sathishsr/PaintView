package com.lht.paintview.pojo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by lht on 16/10/17.
 */

public class DrawPath extends DrawShape {

    private Path path;

    public DrawPath(Path path, Paint paint) {
        this.path = path;
        this.paint = paint;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }
}
