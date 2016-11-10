package com.lht.paintview.pojo;

import android.graphics.Paint;

/**
 * Created by lht on 16/11/10.
 */

public class StrokePaint extends Paint {

    //笔迹宽度
    private float mWidth = 1;
    //笔迹缩放
    private float mScale = 1;
    //实际笔迹宽度为 mWidth * mScale

    public StrokePaint() {
        super();
    }

    public StrokePaint(StrokePaint paint) {
        super(paint);
        mWidth = paint.getWidth();
        mScale = paint.getScale();
    }

    public float getScale() {
        return mScale;
    }

    public void setScale(float mScale) {
        this.mScale = mScale;
    }

    public float getWidth() {
        return mWidth;
    }

    public void setWidth(float mWidth) {
        this.mWidth = mWidth;
    }

    public StrokePaint setStrokeWidth() {
        setStrokeWidth(mWidth * mScale);

        return this;
    }
}
