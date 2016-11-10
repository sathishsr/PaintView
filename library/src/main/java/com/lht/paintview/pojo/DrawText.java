package com.lht.paintview.pojo;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * Created by lht on 16/11/10.
 */

public class DrawText extends DrawShape {

    private static final int TEXT_RECT_PADDING = 15;

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

    public Rect getTextRect() {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        rect.set((int)x - TEXT_RECT_PADDING,
                (int)(y - paint.getTextSize() - TEXT_RECT_PADDING),
                (int)(x + rect.width() + TEXT_RECT_PADDING),
                (int)y + TEXT_RECT_PADDING);
        return rect;
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix) {
        matrix.getValues(matrixValues);
        x = x * matrixValues[0] + y * matrixValues[1] + matrixValues[2];
        y = x * matrixValues[3] + y * matrixValues[4] + matrixValues[5];

        canvas.drawText(text, x, y, paint.setStrokeWidth());
    }
}
