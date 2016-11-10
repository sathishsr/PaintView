package com.lht.paintview.pojo;

import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * Created by lht on 16/11/10.
 */

public class DrawText extends DrawShape {

    private float x = 0, y = 0;
    private String text = "";
    private float[] matrixValues = new float[9];

    public DrawText(StrokePaint paint) {
        this.paint = paint;
    }

    public void setCoordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix) {
        matrix.getValues(matrixValues);
        x = x * matrixValues[0] + y * matrixValues[1] + matrixValues[2];
        y = x * matrixValues[3] + y * matrixValues[4] + matrixValues[5];

        canvas.drawText(text, x, y, paint.setStrokeWidth());
    }
}
