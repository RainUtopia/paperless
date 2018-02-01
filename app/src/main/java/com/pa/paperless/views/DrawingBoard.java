package com.pa.paperless.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;




/**
 * Created by Administrator on 2017/11/3.
 * 自定义画板
 */
public class DrawingBoard extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    //SurfaceHolder实例
    private SurfaceHolder mSurfaceHolder;
    //Canvas 对象
    private Canvas mCanvas;


    //控制子线程是否运行
    public boolean startDraw;
    //path 实例
    private Path mPath = new Path();
    //paint实例
    public Paint mPaint = new Paint();
    //设置画笔的默认宽度 和 颜色
    private int mPaintWidth = 10;
    private int mPaintColor = Color.BLACK;
    private int mCanvasColor = Color.WHITE;


    private List<DrawingInfo> mDrawingList;
    private List<DrawingInfo> mRemovedList;

    public  List<DrawPath> mSavePath = new ArrayList<>();
    private MODEDRAW DrawMode = MODEDRAW.LINE;
    private int startX;
    private int startY;
    private int moveX;
    private int moveY;
    private int dX;
    private int dY;

    private enum MODEDRAW{
        CIRCLE,
        RECT,
        LINE,
        ERASE,
        CURVE
    }
    /**
     * 用来保存记录path路径  为撤销操作做准备
     */
    private class DrawPath{
        private DrawPath(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }

        private Path path;
        private Paint paint;
    }

    /**
     * 改变绘画图形
     * @param mode
     */
    public void setMode(MODEDRAW mode){
        DrawMode = mode;
    }

    public DrawingBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        //设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常亮
        this.setKeepScreenOn(true);
    }

    /**
     * 创建a
     *
     * @param surfaceHolder
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startDraw = true;
        new Thread(this).start();
    }

    /**
     * 改变
     *
     * @param surfaceHolder
     * @param i
     * @param i1
     * @param i2
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    /**
     * 销毁
     *
     * @param surfaceHolder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        startDraw = false;

    }

    @Override
    public void run() {
        //如果不停止
        while (startDraw) {
            //就一直绘制
            draw();
        }
    }

    private void draw() {
        mCanvas = mSurfaceHolder.lockCanvas();
        //设置画布的颜色
        mCanvas.drawColor(mCanvasColor);
        mPaint.setStyle(Paint.Style.STROKE);
        //设置画笔的初始宽度、颜色
        mPaint.setStrokeWidth(mPaintWidth);
        mPaint.setColor(mPaintColor);
        mCanvas.drawPath(mPath, mPaint);
        //将路径和画笔存放到集合中--》撤销
        mSavePath.add(new DrawPath(mPath,mPaint));
        //对画布内容进行提交
        if (mCanvas != null) {
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取手指移动的x坐标
        startX = (int) event.getX();
        //获取手指移动的y坐标
        startY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(startX, startY);
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                dX = moveX - startX;
                dY = moveY - startY;
                switch (DrawMode){
                    case CIRCLE:
//                        changeCircle();
                        mPath.addCircle(dX/2+startX,dY/2+startY,dX/2, Path.Direction.CW);

                        break;
                    case RECT:

                        changeRect();
                        break;
                    case ERASE:

                        changeErase();
                        break;
                    case LINE:

                        changeLine();
                        break;
                    case CURVE:
//                        mPath.lineTo(startX,startY);
//                        mPath.lineTo(moveX,moveY);
                        //这里终点设为两点的中心点的目的在于使绘制的曲线更平滑，如果终点直接设置为x,y，效果和lineto是一样的,实际是折线效果
                        mPath.quadTo(startX, startY, (startX + startX) / 2, (startY + startY) / 2);
//                        changeCurve();
                        break;
                }
//                mPath.lineTo(startX,startY);
                break;

            case MotionEvent.ACTION_UP:
                //将最新的x y值设置成新的起始位置
                mPath.addCircle(dX/2+startX,dY/2+startY,dX/2, Path.Direction.CW);
                mSavePath.add(new DrawPath(mPath,mPaint));
                startX = (int) event.getX();
                startY = (int) event.getY();
        }
        invalidate();
        return true;
    }


    /**
     * 清除、重置画布
     */
    public void reset() {
        mPath.reset();
    }

    /**
     * 用来改变画笔的宽度
     *
     * @param width
     */
    public void changePaintWidth(int width) {
        //将画布的颜色 设置成透明色
        mCanvasColor = Color.TRANSPARENT;
        this.mPaintWidth = width;
        reset();
    }

    /**
     * 用来改变画笔的颜色
     *
     * @param color
     */
    public void changePaintColor(int color) {

        mCanvasColor = Color.TRANSPARENT;
        this.mPaintColor = color;
        mPath.reset();
    }
    public void changeErase(){

    }

    private void changeCurve() {

    }
    private abstract static class DrawingInfo {
        Paint paint;
        abstract void draw(Canvas canvas);
    }
    private static class PathDrawingInfo extends DrawingInfo{

        Path path;

        @Override
        void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }
    }
    /**
     * 撤销
     */
    public void undo(){
        //最多存20条记录
        if (mSavePath == null) {
            mSavePath = new ArrayList<>(20);
        } else if (mSavePath.size() == 20) {
            mSavePath.remove(0);
        }
        Path cachePath = new Path(mPath);
        Paint cachePaint = new Paint(mPaint);
        PathDrawingInfo info = new PathDrawingInfo();
        info.path = cachePath;
        info.paint = cachePaint;
//        mSavePath.add(info);
        //对画布内容进行提交
        if (mCanvas != null) {
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
//        mCanEraser = true;
//        if (mCanvas != null) {
//            mCallback.onUndoRedoStatusChanged();
//        }
    }
    public void changeLine(){
        mCanvas.drawLine(startX, startY, moveX, moveY,mPaint);
    }
    public void changeRect(){
        mCanvas.drawRect(startX, startY, moveX, moveY,mPaint);
    }

    public void changeCircle(){

        mCanvas.drawCircle(dX /2+ startX, dY /2+ startY, dX /2,mPaint);
    }

}
