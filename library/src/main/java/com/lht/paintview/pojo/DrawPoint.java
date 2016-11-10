package com.lht.paintview.pojo;

import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * Created by lht on 16/10/17.
 */

public class DrawPoint extends DrawShape {

    private float x, y;
    private float[] matrixValues = new float[9];

    public DrawPoint(float x, float y, StrokePaint paint) {
        this.x = x;
        this.y = y;
        this.paint = paint;
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix) {
        //缩放坐标映射
        matrix.getValues(matrixValues);
        x = x * matrixValues[0] + y * matrixValues[1] + matrixValues[2];
        y = x * matrixValues[3] + y * matrixValues[4] + matrixValues[5];

        canvas.drawPoint(x, y, paint.setStrokeWidth());
    }
}
