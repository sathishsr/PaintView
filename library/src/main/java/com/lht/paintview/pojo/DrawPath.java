package com.lht.paintview.pojo;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;

/**
 * Created by lht on 16/10/17.
 */

public class DrawPath extends DrawShape {

    private Path path;

    public DrawPath(Path path, StrokePaint paint) {
        this.path = path;
        this.paint = paint;
    }

    @Override
    public void draw(Canvas canvas, Matrix m) {
        //缩放坐标映射
        path.transform(m);

        canvas.drawPath(path, paint.setStrokeWidth());
    }
}
