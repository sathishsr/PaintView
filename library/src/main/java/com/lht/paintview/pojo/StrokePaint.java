package com.lht.paintview.pojo;

import android.graphics.Paint;

/**
 * Created by lht on 16/11/10.
 */

public class StrokePaint extends Paint {

    //scale
    //缩放
    private float mScale = 1;
    //custom stroke width
    //笔迹宽度
    private float mStrokeWidth = super.getStrokeWidth();
    //custom text size
    private float mTextSize = 28;//TODO
    //actual stroke width: mWidth * mScale
    //实际笔迹宽度为 mWidth * mScale

    public StrokePaint() {
        super();
    }

    public StrokePaint(StrokePaint paint) {
        super(paint);
        mScale = paint.getScale();
        mStrokeWidth = paint.getStrokeWidth();
        mTextSize = paint.getTextSize();
    }

    public float getScale() {
        return mScale;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.mStrokeWidth = strokeWidth;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
    }

    public float getActualStrokeWidth() {
        return mStrokeWidth * mScale;
    }

    public float getActualTextSize() {
        return mTextSize * mScale;
    }

    public StrokePaint setStrokeWidth() {
        super.setStrokeWidth(getActualStrokeWidth());
        super.setTextSize(getActualTextSize());

        return this;
    }
}
