package com.lht.paintview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by lht on 16/10/17.
 */

public class PaintView extends View {

    private OnDrawListener mOnDrawListener;
    public interface OnDrawListener {
        void afterDraw(ArrayList<DrawShape> mDrawShapes);
    }

    //view尺寸
    private int mWidth, mHeight;

    //背景色
    final static int BG_COLOR = Color.WHITE;
    //绘制标记Paint
    private Paint mPaint;
    //绘制背景图Paint
    private Paint mBitmapPaint;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    //背景图
    private Bitmap mBgBitmap = null;

    //当前坐标
    private float mCurX, mCurY;
    //当前绘制路径
    private Path mCurPath;

    //绘制list
    private ArrayList<DrawShape> mDrawShapes = new ArrayList<>();
    private boolean bPathDrawing = false;

    public PaintView(Context context) {
        super(context);
        init(context);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setOnDrawListener(OnDrawListener onDrawListener) {
        mOnDrawListener = onDrawListener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mWidth = right - left;
        mHeight = bottom - top;

        drawBitmapToCanvas(mBgBitmap);
        invalidate();
    }

    private void init(Context context) {
        setDrawingCacheEnabled(true);

        //先赋值一个初始值，用于创建Bitmap
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWidth = manager.getDefaultDisplay().getWidth();
        mHeight = manager.getDefaultDisplay().getHeight();

        initPaint();
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        initCanvas();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
    }

    private void initCanvas() {
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(BG_COLOR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);

        for (DrawShape dp : mDrawShapes) {
            dp.draw(canvas);
        }
    }

    private void touchDown(float x, float y) {
        mCurX = x;
        mCurY = y;
    }

    private void touchMove(float x, float y) {
        final float previousX = mCurX;
        final float previousY = mCurY;

        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);

        //两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3) {
            if (!bPathDrawing) {
                mCurPath = new Path();
                mCurPath.moveTo(previousX, previousY);
                mDrawShapes.add(new DrawPath(mCurPath, mPaint));
                bPathDrawing = true;
            }

            //设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;

            //二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            mCurPath.quadTo(previousX, previousY, cX, cY);

            //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mCurX = x;
            mCurY = y;
        }
    }

    private void touchUp(float x, float y) {
        if (!bPathDrawing && x == mCurX && y == mCurY) {
            mDrawShapes.add(new DrawPoint(x, y, mPaint));
        }
        bPathDrawing = false;

        if (mOnDrawListener != null) {
            mOnDrawListener.afterDraw(mDrawShapes);
        }
    }

    /**
     * 撤销
     */
    public boolean undo() {
        if (mDrawShapes != null && mDrawShapes.size() > 0) {
            clear();
            mDrawShapes.remove(mDrawShapes.size() - 1);
            invalidate();
        }

        return mDrawShapes != null && mDrawShapes.size() > 0;
    }

    /**
     * 清除画布
     */
    public void clear() {
        if (mCanvas != null) {
            initCanvas();

            if (mBgBitmap != null) {
                drawBitmapToCanvas(mBgBitmap);
            }

            invalidate();
        }
    }

    /**
     * 设置笔的颜色
     * @param color 0xaarrggbb
     */
    public void setColor(int color) {
        Paint paint = new Paint(mPaint);
        paint.setColor(color);
        mPaint = paint;
    }

    /**
     * 设置笔的宽度
     * @param width
     */
    public void setStrokeWidth(int width) {
        Paint paint = new Paint(mPaint);
        paint.setStrokeWidth(width);
        mPaint = paint;
    }

    public Bitmap getBitmap() {
        destroyDrawingCache();
        return getDrawingCache();
    }

    /**
     * 设置画布原始图案
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        mBgBitmap = bitmap;
    }

    private void drawBitmapToCanvas(Bitmap bitmap) {
        if (bitmap.getWidth() > mWidth || bitmap.getHeight() > mHeight ) {
            bitmap = ImageUtil.zoomImg(bitmap, mWidth, mHeight);
        }

        float left = (mWidth - bitmap.getWidth()) / 2;
        float top = (mHeight - bitmap.getHeight()) / 2;
        //缩放后
        if (bitmap.getWidth() < mWidth && bitmap.getHeight() < mHeight) {
            mCanvas.drawBitmap(bitmap, left, top, mBitmapPaint);
        }
        else if (bitmap.getWidth() < mWidth) {
            mCanvas.drawBitmap(bitmap, left, 0, mBitmapPaint);
        }
        else if (bitmap.getHeight() < mHeight) {
            mCanvas.drawBitmap(bitmap, 0, top, mBitmapPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(x, y);
                break;
        }
        invalidate();
        return true;
    }
}
