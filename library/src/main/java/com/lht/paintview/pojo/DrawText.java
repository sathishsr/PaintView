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

    private Rect rect = new Rect();

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

    /**
     * Get text border rectangle
     * 获取文字边框矩形
     * @return
     */
    public Rect getTextRect() {
        paint.getTextBounds(text, 0, text.length(), rect);
        rect.set((int)x - TEXT_RECT_PADDING,
                (int)(y - paint.getActualTextSize() - TEXT_RECT_PADDING),
                (int)(x + rect.width() + TEXT_RECT_PADDING),
                (int)y + TEXT_RECT_PADDING);
        return rect;
    }

    public boolean isInTextRect(float x, float y) {
        return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom;
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix) {
        matrix.getValues(matrixValues);
        x = x * matrixValues[0] + y * matrixValues[1] + matrixValues[2];
        y = x * matrixValues[3] + y * matrixValues[4] + matrixValues[5];

        canvas.drawText(text, x, y, paint.setStrokeWidth());
    }
}
