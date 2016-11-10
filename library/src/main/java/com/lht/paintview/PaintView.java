package com.lht.paintview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lht.paintview.pojo.DrawPath;
import com.lht.paintview.pojo.DrawPoint;
import com.lht.paintview.pojo.DrawShape;
import com.lht.paintview.pojo.DrawText;
import com.lht.paintview.pojo.StrokePaint;

import java.util.ArrayList;

/**
 * Created by lht on 16/10/17.
 */

public class PaintView extends View {

    private OnDrawListener mOnDrawListener;

    public interface OnDrawListener {
        void afterDraw(ArrayList<DrawShape> mDrawShapes);
    }

    public void setOnDrawListener(OnDrawListener onDrawListener) {
        mOnDrawListener = onDrawListener;
    }

    private static final float SCALE_MAX = 2f, SCALE_MIN = 0.5f;

    //View Size
    //View尺寸
    private int mWidth, mHeight;

    //Background Color
    //背景色
    private int mBgColor = Color.WHITE;
    //Paint List for Stroke
    //绘制笔迹Paint列表
    private ArrayList<StrokePaint> mPaintList = new ArrayList<>();

    //
    private boolean bTextDrawing = false;
    private DrawText mCurrentText;

    //Background Image
    //背景图
    private Bitmap mBgBitmap = null;
    //Background Paint
    //绘制背景图Paint
    private Paint mBgPaint;

    //Current Coordinate
    //当前坐标
    private float mCurrentX, mCurrentY;
    //Current Drawing Path
    //当前绘制路径
    private Path mCurrentPath;

    //Shape List(Path and Point)
    //绘制列表(线和点）
    private ArrayList<DrawShape> mDrawShapes = new ArrayList<>();
    private boolean bPathDrawing = false;

    //Gesture
    //手势
    private final static int SINGLE_FINGER = 1, DOUBLE_FINGER = 2;
    private enum MODE {
        NONE, DRAG, ZOOM
    }
    private MODE mode = MODE.NONE;
    private boolean bDragEnable = true;

    //Center Point of Two Fingers
    //当次两指中心点
    private float mCurrentCenterX, mCurrentCenterY;
    //当次两指间距
    private float mCurrentLength = 0;
    //当次位移
    private float mCurrentDistanceX, mCurrentDistanceY;
    //当次缩放
    private float mCurrentScale;

    //整体矩阵
    private Matrix mMainMatrix = new Matrix();
    private float[] mMainMatrixValues = new float[9];
    //当次矩阵
    private Matrix mCurrentMatrix = new Matrix();

    public PaintView(Context context) {
        super(context);
        init();
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setDrawingCacheEnabled(true);

        initPaint();
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setDither(true);
    }

    private void initPaint() {
        StrokePaint paint = new StrokePaint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        paint.setStrokeCap(Paint.Cap.ROUND);// 形状

        mPaintList.add(paint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mWidth = right - left;
        mHeight = bottom - top;

        resizeBgBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mBgColor);
        canvas.drawBitmap(mBgBitmap, mMainMatrix, mBgPaint);
        for (DrawShape shape : mDrawShapes) {
            shape.draw(canvas, mCurrentMatrix);
        }
    }

    public void startText() {
        bTextDrawing = true;
        mCurrentText = new DrawText(getCurrentPaint());
        mDrawShapes.add(mCurrentText);
    }

    public void changeText(String text) {
        mCurrentText.setText(text);
        invalidate();
    }

    public void endText() {
        bTextDrawing = false;
    }

    /**
     * Undo
     * 撤销
     * @return is Undo still available 是否还能撤销
     */
    public boolean undo() {
        if (mDrawShapes != null && mDrawShapes.size() > 0) {
            mDrawShapes.remove(mDrawShapes.size() - 1);
            invalidate();
        }

        if (mOnDrawListener != null) {
            mOnDrawListener.afterDraw(mDrawShapes);
        }

        return mDrawShapes != null && mDrawShapes.size() > 0;
    }

    /**
     * 设置背景颜色
     * Set background color
     * @param color 0xaarrggbb
     */
    public void setBgColor(int color) {
        mBgColor = color;
    }

    /**
     * 设置画笔颜色
     * Set paint color
     * @param color 0xaarrggbb
     */
    public void setColor(int color) {
        StrokePaint paint = new StrokePaint(getCurrentPaint());
        paint.setColor(color);
        mPaintList.add(paint);
    }

    /**
     * 设置画笔宽度
     * Set stroke width
     * @param width
     */
    public void setStrokeWidth(int width) {
        StrokePaint paint = new StrokePaint(getCurrentPaint());
        paint.setStrokeWidth(width);
        mPaintList.add(paint);
    }

    /**
     * 获取绘制结果图
     * @return paint result 绘制结果图
     */
    public Bitmap getBitmap() {
        destroyDrawingCache();
        return getDrawingCache();
    }

    /**
     * 设置背景图
     * Set background image
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        mBgBitmap = bitmap;
    }

    private void resizeBgBitmap() {
        if (mBgBitmap.getWidth() > mWidth || mBgBitmap.getHeight() > mHeight) {
            mBgBitmap = zoomImg(mBgBitmap, mWidth, mHeight);
        }

        float left = (mWidth - mBgBitmap.getWidth()) / 2;
        float top = (mHeight - mBgBitmap.getHeight()) / 2;
        //缩放后
        if (mBgBitmap.getWidth() < mWidth && mBgBitmap.getHeight() < mHeight) {
            mMainMatrix.setTranslate(left, top);
        }
        else if (mBgBitmap.getWidth() < mWidth) {
            mMainMatrix.setTranslate(left, 0);
        }
        else if (mBgBitmap.getHeight() < mHeight) {
            mMainMatrix.setTranslate(0, top);
        }
    }

    private Bitmap zoomImg(Bitmap bm, int newWidth , int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        float scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    /**
     * 获得当前笔迹
     */
    private StrokePaint getCurrentPaint() {
        return mPaintList.get(mPaintList.size() - 1);
    }

    /**
     * 缩放所有笔迹
     */
    private void scaleStrokeWidth(float scale) {
        for (StrokePaint paint: mPaintList) {
            paint.setScale(paint.getScale() * scale);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        mode = MODE.NONE;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //多点按下
            case MotionEvent.ACTION_POINTER_DOWN:
                doubleFingerDown(event);
                break;
            //单点按下
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                break;
            //移动
            case MotionEvent.ACTION_MOVE:
                //单点移动
                if (event.getPointerCount() == SINGLE_FINGER) {
                    touchMove(x, y);
                }
                //多点移动
                else if (event.getPointerCount() == DOUBLE_FINGER) {
                    doubleFingerMove(event);
                }
                break;
            //单点抬起
            case MotionEvent.ACTION_UP:
                touchUp(x, y);
                break;
        }

        switch (mode) {
            case DRAG:
                mMainMatrix.postTranslate(mCurrentDistanceX, mCurrentDistanceY);
                mCurrentMatrix.setTranslate(mCurrentDistanceX, mCurrentDistanceY);
                break;
            case ZOOM:
                mMainMatrix.postScale(mCurrentScale, mCurrentScale,
                        mCurrentCenterX, mCurrentCenterY);
                mCurrentMatrix.setScale(mCurrentScale, mCurrentScale,
                        mCurrentCenterX, mCurrentCenterY);
                scaleStrokeWidth(mCurrentScale);
                break;
            case NONE:
                mCurrentMatrix.reset();
                break;
        }

        mMainMatrix.getValues(mMainMatrixValues);

        invalidate();
        return true;
    }

    private void touchDown(float x, float y) {
        mCurrentX = x;
        mCurrentY = y;
    }

    private void touchMove(float x, float y) {
        final float previousX = mCurrentX;
        final float previousY = mCurrentY;

        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);

        //两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3) {
            if (!bPathDrawing) {
                mCurrentPath = new Path();
                mCurrentPath.moveTo(previousX, previousY);
                mDrawShapes.add(
                        new DrawPath(mCurrentPath, getCurrentPaint()));
                bPathDrawing = true;
            }

            //设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;

            //二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            mCurrentPath.quadTo(previousX, previousY, cX, cY);

            //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mCurrentX = x;
            mCurrentY = y;
        }
    }

    private void touchUp(float x, float y) {
        if (!bTextDrawing && !bPathDrawing && x == mCurrentX && y == mCurrentY) {
            mDrawShapes.add(
                    new DrawPoint(x, y, getCurrentPaint()));
        }

        if (bTextDrawing && x == mCurrentX && y == mCurrentY) {
            mCurrentText.setCoordinate(x, y);
        }

        bPathDrawing = false;

        if (mOnDrawListener != null) {
            mOnDrawListener.afterDraw(mDrawShapes);
        }
    }

    //两点按下
    private void doubleFingerDown(MotionEvent event) {
        mCurrentCenterX = (event.getX(0) + event.getX(1)) / 2;
        mCurrentCenterY = (event.getY(0) + event.getY(1)) / 2;

        mCurrentLength = getDistance(event);
    }

    //两点移动
    private void doubleFingerMove(MotionEvent event) {
        //当前中心点
        float curCenterX = (event.getX(0) + event.getX(1)) / 2;
        float curCenterY = (event.getY(0) + event.getY(1)) / 2;

        //当前两点间距离
        float curLength = getDistance(event);

        //拖动
        if (bDragEnable && Math.abs(mCurrentLength - curLength) < 5) {
            mode = MODE.DRAG;
            mCurrentDistanceX = curCenterX - mCurrentCenterX;
            mCurrentDistanceY = curCenterY - mCurrentCenterY;
        }
        //放大 || 缩小
        else if (mCurrentLength < curLength || mCurrentLength > curLength){
            mode = MODE.ZOOM;
            mCurrentScale = curLength / mCurrentLength;

            float toScale = mMainMatrixValues[Matrix.MSCALE_X] * mCurrentScale;
            if (toScale > SCALE_MAX || toScale < SCALE_MIN) {
                mCurrentScale = 1;
            }
        }

        mCurrentCenterX = curCenterX;
        mCurrentCenterY = curCenterY;

        mCurrentLength = curLength;

//        bDragEnable = mMainMatrixValues[Matrix.MSCALE_X] > 1;
    }

    /**
     * 获取两个触控点之间的距离
     * @param event
     * @return 两个触控点之间的距离
     */
    private float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float)Math.sqrt(x * x + y * y);
    }
}
