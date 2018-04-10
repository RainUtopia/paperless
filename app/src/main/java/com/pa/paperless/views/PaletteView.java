package com.pa.paperless.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.pa.paperless.bean.DrawPropertyBean;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.listener.CallListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 * 自定义画板
 */

public class PaletteView extends View {
    private Paint mPaint;
    //画图片用的 Paint 对象
    private Paint mPaintImage;
    //绘图的路径
    private Path mPath;

    private float mStartX;
    private float mStartY;
    private Bitmap mBufferBitmap;
    public Canvas mBufferCanvas;
    //画笔的颜色
    private int PaintColor = Color.BLACK;
    //最多存放20条 path
    private static final int MAX_CACHE_STEP = 20;

    private List<DrawingInfo> mDrawingList;
    private List<DrawingInfo> mRemovedList;

    private Xfermode mClearMode;
    private Xfermode mSrcOverMode;
    // 两种模式的画笔大小
    private float mDrawSize = 20;
    private float mEraserSize;

    private boolean mCanEraser;

    private Callback mCallback;
    private float moveX;
    private float moveY;
    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private Paint easerPaint;
    private CallListener mListener;
    private Mode mMode = Mode.DRAW;
    private float x;
    private float y;

    public enum Mode {
        DRAW, CIRCLE, RECT, LINE, ERASER, TEXT
    }


    //获取当前的画笔和路径信息
    public DrawPropertyBean getDrawPro() {
        int color = mPaint.getColor();
        int strokeWidth = (int) mPaint.getStrokeWidth();
        return new DrawPropertyBean(mMode, strokeWidth, color, downX, downY, moveX, moveY);
    }

    public PaletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置绘图缓存启用
        setDrawingCacheEnabled(true);
        init();
    }

    public interface Callback {
        void onUndoRedoStatusChanged();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        //如果该项设置为true，则图像在动画进行中会滤掉对Bitmap图像的优化操作，加快显示速度，本设置项依赖于dither和xfermode的设置。
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mDrawSize);
        mPaint.setColor(PaintColor);
        mPaint.setAntiAlias(true);// 去锯齿
        mClearMode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mSrcOverMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
        //橡皮擦
        easerPaint = new Paint();
        easerPaint.setStyle(Paint.Style.FILL);
        easerPaint.setAntiAlias(true);
        easerPaint.setDither(true);
        easerPaint.setAlpha(0);
        easerPaint.setColor(Color.TRANSPARENT);
        easerPaint.setStrokeJoin(Paint.Join.ROUND);
        easerPaint.setStrokeCap(Paint.Cap.ROUND);
        easerPaint.setStrokeWidth(mDrawSize);
        easerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        //绘制图片的画笔
        mPaintImage = new Paint();
    }

    public void initBuffer() {
        mBufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);
    }

    private abstract static class DrawingInfo {
        Paint paint;

        abstract void draw(Canvas canvas);
    }

    private static class PathDrawingInfo extends DrawingInfo {

        Path path;

        @Override
        void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }
    }

    public Mode getMode() {
        return mMode;
    }

    //绘画模式和橡皮擦模式
    public void setMode(Mode mode) {
        //如果模式不同则更改
        if (mode != mMode) {
            mMode = mode;
            if (mMode == Mode.ERASER) {
                mPaint.setXfermode(mSrcOverMode);
            } else {
                mPaint.setXfermode(mSrcOverMode);
            }
        }
    }

    //画图片
    public void drawImage(Bitmap bitmap) {
        // TODO: 2017/12/9 撤销操作 -->> 需要保存绘制路径
        if (mBufferCanvas == null) {
            initBuffer();
            mBufferCanvas.drawBitmap(bitmap, 10, 10, mPaintImage);
        } else {
//            mBufferCanvas.drawBitmap(bitmap,new Matrix(),new Paint());
            mBufferCanvas.drawBitmap(bitmap, 10, 10, mPaintImage);
        }

    }

    /**
     * 设置橡皮擦大小
     *
     * @param size
     */
    public void setEraserSize(float size) {
        mEraserSize = size;
    }

    /**
     * 设置画笔的大小
     *
     * @param size
     */
    public void setPenRawSize(float size) {
//        mDrawSize = size ;
        mPaint.setStrokeWidth(size);
    }

    /**
     * 设置画笔的颜色
     *
     * @param color
     */
    public void setPenColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 设置画笔透明度  0 ~  255  值越小越透明
     *
     * @param alpha
     */
    public void setPenAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    private void reDraw() {
        if (mDrawingList != null) {
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            for (DrawingInfo drawingInfo : mDrawingList) {
                drawingInfo.draw(mBufferCanvas);
            }
            invalidate();
        }
    }

    //是否可以反撤销
    public boolean canRedo() {
        return mRemovedList != null && mRemovedList.size() > 0;
    }

    //是否可以撤销
    public boolean canUndo() {
        return mDrawingList != null && mDrawingList.size() > 0;
    }

    //反撤销
    public void redo() {
        int size = mRemovedList == null ? 0 : mRemovedList.size();
        if (size > 0) {
            DrawingInfo info = mRemovedList.remove(size - 1);
            mDrawingList.add(info);
            mCanEraser = true;
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    /**
     * 撤销
     */
    public void undo() {
        int size = mDrawingList == null ? 0 : mDrawingList.size();
        if (size > 0) {
            DrawingInfo info = mDrawingList.remove(size - 1);
            if (mRemovedList == null) {
                mRemovedList = new ArrayList<>(MAX_CACHE_STEP);
            }
            if (size == 1) {
                mCanEraser = false;
            }
            mRemovedList.add(info);
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    /**
             * 清除
             */
        public void clear() {
            if (mBufferBitmap != null) {
                if (mDrawingList != null) {
                    mDrawingList.clear();
                }
                if (mRemovedList != null) {
                    mRemovedList.clear();
                }
            mCanEraser = false;
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            invalidate();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    /**
     * 保存 图片
     */
    public Bitmap buildBitmap() {
        Bitmap bm = getDrawingCache();
        Bitmap result = Bitmap.createBitmap(bm);
        destroyDrawingCache();
        Log.e("MyLog", "PaletteView.buildBitmap:  运行 --->>> ");
        return result;
    }

    private void saveDrawingPath() {
        if (mDrawingList == null) {
            mDrawingList = new ArrayList<>(MAX_CACHE_STEP);
        } else if (mDrawingList.size() == MAX_CACHE_STEP) {
            mDrawingList.remove(0);
        }
        Path cachePath = new Path(mPath);
        Paint cachePaint = new Paint(mPaint);
        PathDrawingInfo info = new PathDrawingInfo();
        info.path = cachePath;
        info.paint = cachePaint;
        mDrawingList.add(info);
        mCanEraser = true;
        if (mCallback != null) {
            mCallback.onUndoRedoStatusChanged();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.e("MyLog", "PaletteView.onMeasure:  widthSize --->>> " + widthSize + "   heightSize --->>> " + heightSize);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setListener(CallListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        x = event.getX();
        y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(event);
                break;
        }
        return true;
    }

    private void touchUp(MotionEvent event) {
        upX = event.getX();
        upY = event.getY();
        //保存绘制的Path -- >> 撤销操作
        saveDrawingPath();
//        mBufferCanvas.drawPath(mPath, mPaint);
        mPath.reset();
        invalidate();
    }

    private void touchMove(MotionEvent event) {
        moveX = event.getX();
        moveY = event.getY();
        if (mMode == Mode.DRAW) {
            //这里终点设为两点的中心点的目的在于使绘制的曲线更平滑，如果终点直接设置为x,y，效果和lineto是一样的,实际是折线效果
            mPath.quadTo(mStartX, mStartY, (x + mStartX) / 2, (y + mStartY) / 2);
        } else if (mMode == Mode.CIRCLE) {
            mPath.reset();
            RectF oval = new RectF(downX, downY, x, y);
            mPath.addOval(oval, Path.Direction.CCW);
        } else if (mMode == Mode.RECT) {
            mPath.reset();
            RectF rectF = new RectF(downX, downY, x, y);
            mPath.addRect(rectF, Path.Direction.CCW);
        } else if (mMode == Mode.LINE) {
            mPath.reset();
            mPath.moveTo(downX, downY);
            mPath.lineTo(moveX, moveY);
        }
        if (mBufferBitmap == null) {
            initBuffer();
        }
        if (mMode == Mode.DRAW) {
            mBufferCanvas.drawPath(mPath, mPaint);
        }
        if (mMode == Mode.ERASER) {
            mBufferCanvas.drawPath(mPath, easerPaint);
        }
        mBufferCanvas.drawPath(mPath, mPaint);
        mStartX = x;
        mStartY = y;
        invalidate();
        mListener.callListener(IDivMessage.GET_DRAW_INFO, getDrawPro());
    }

    private void touchDown(MotionEvent event) {
        if (mMode == Mode.TEXT) {

        }
        mStartX = x;
        mStartY = y;
        downX = event.getX();
        downY = event.getY();
        if (mPath == null) {
            mPath = new Path();
        }
        mPath.moveTo(x, y);
        invalidate();
    }

}
