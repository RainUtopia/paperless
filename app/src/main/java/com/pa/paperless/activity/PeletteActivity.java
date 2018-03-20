package com.pa.paperless.activity;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.pa.paperless.R;
import com.pa.paperless.adapter.ScreenControlAdapter;
import com.pa.paperless.bean.DevMember;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.MyUtils;
import com.pa.paperless.views.ColorPickerDialog;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PeletteActivity extends AppCompatActivity implements View.OnClickListener, CallListener {

    private final int UPDATE_VIEW = -1;// 更新界面
    private final int SAVE_SUCCESS = 1;// 保存成功
    private final int SAVE_FAILED = 0;// 保存失败
    private final int MODE_ERASER = 0;// 橡皮擦
    private final int MODE_PAINT = 1;// 画笔
    private final int MODE_TEXT = 2;// 文本
    private final int MODE_LINE = 3;// 直线
    private final int MODE_CURVE = 4;// 曲线
    private final int MODE_CIRCLE = 5;// 圆环
    private final int MODE_SQUARE = 6;// 方框


    // 变量
    private int paintMode = MODE_PAINT;// 画笔模式
    private int paintWidth = 1;// 画笔宽度
    private int paintColor = Color.BLACK;// 画笔颜色
    private int paintMinNum = 1;// 画笔宽度最小值
    private int eraserMinNum = 21;// 橡皮擦宽度最小值
    private String savePath = "";// 保存画布内容的图片路径
    private String drawText = "";// 保存要绘制的文本

    public static int PHOTO_REQUEST_GALLERY = 1;
    private ImageView mPen;
    private ImageView mEraser;
    private ImageView mPalette;
    private ImageView mText;
    private ImageView mPic;
    private ImageView mSline;
    private ImageView mEllpse;
    private ImageView mRect;
    private ImageView mBack;
    private ImageView mClean;
    private SeekBar mSeb;
    private int mPaintWidth = 10; // 设置画笔的默认宽度是 10
    private ImageView mColorBlack;
    private ImageView mColorGray;
    private ImageView mColorRed;
    private ImageView mColorOrange;
    private ImageView mColorYellow;
    private ImageView mColorBrown;
    private ImageView mColorPurple;
    private ImageView mColorBlue;
    private ImageView mColorCyan;
    private ImageView mColorGreen;
    private Button mShareStart;
    private Button mShareStop;
    private Button mSave;
    private Button mExit;
    private List<ImageView> mTopImages;//上方的图片
    private List<ImageView> mBotImages;//下方的图片
    private LinearLayout mTopView;
    public static boolean ISSHARE = true;
    private Bitmap tempBmp;
    private Canvas tempCanvas;
    private Bitmap baseBmp;
    private Canvas canvas;
    private ImageView imageView;
    private ImageView tempImageView;
    private Bitmap dstbmp;
    private List<DrawPath> pathList;
    private Paint paint;
    private DrawPath drawPath;
    private Path path;
    private Paint tempPaint;
    private AlertDialog editDialog;
    private Thread thread;
    private Rect dst;
    private Bitmap srcbmp;
    int startX, startY;
    int endX, endY;
    private List<MemberInfo> memberInfos;
    public static ScreenControlAdapter onLineBoardMemberAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VIEW:
                    imageView.setImageBitmap(baseBmp);
                    break;
                case SAVE_SUCCESS:
                    Toast.makeText(PeletteActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    break;
                case SAVE_FAILED:
                    Toast.makeText(PeletteActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    break;
                case IDivMessage.QUERY_ATTENDEE://92.查询参会人员
                    ArrayList queryMember = msg.getData().getParcelableArrayList("queryMember");
                    InterfaceMember.pbui_Type_MemberDetailInfo o2 = (InterfaceMember.pbui_Type_MemberDetailInfo) queryMember.get(0);
                    memberInfos = Dispose.MemberInfo(o2);
                    break;
                case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息(查找在线状态的投影机)
                    ArrayList devInfos = msg.getData().getParcelableArrayList("devInfos");
                    if (devInfos != null) {
                        InterfaceDevice.pbui_Type_DeviceDetailInfo o5 = (InterfaceDevice.pbui_Type_DeviceDetailInfo) devInfos.get(0);
                        List<DeviceInfo> deviceInfos = Dispose.DevInfo(o5);
                        onLineMembers = new ArrayList<>();
                        for (int i = 0; i < deviceInfos.size(); i++) {
                            DeviceInfo deviceInfo = deviceInfos.get(i);
                            int netState = deviceInfo.getNetState();
                            int devId = deviceInfo.getDevId();
                            int memberId = deviceInfo.getMemberId();
                            if (netState == 1 && memberInfos != null) {
                                for (int j = 0; j < memberInfos.size(); j++) {
                                    MemberInfo memberInfo = memberInfos.get(j);
                                    if (memberInfo.getPersonid() == memberId) {
                                        //查找到在线状态的参会人员
                                        onLineMembers.add(new DevMember(memberInfo, devId));
                                    }
                                }
                            }
                        }
                        //初始化在线参会人是否选中集合
                        boardCheck = new ArrayList<>();
                        //初始化 全部设为false
                        for (int k = 0; k < onLineMembers.size(); k++) {
                            boardCheck.add(false);
                        }
                        onLineBoardMemberAdapter = new ScreenControlAdapter(onLineMembers, 1);
                    }
                    break;
            }
        }
    };
    private int width = 1;
    private int height = 1;
    public static PeletteActivity context;
    private NativeUtil nativeUtil;
    private int count = 0;
    private int DRAWTYPE = InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_INK.getNumber();
    private boolean ISSCREEN = false;//是否发起了同屏
    private PopupWindow mMemberPop;
    private PopupWindow mChooseMemberPop;
    private List<DevMember> onLineMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelette_view);
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        Log.e("MyLog", "PeletteActivity.onCreate 161行:   --->>> " + display);
        initNativeUtil();
        initView();
        initImages();
        initDefaultColor();
        context = this;
        initEvent();
        // 创建一个临时的用于显示橡皮擦图片的bitmap
        tempBmp = Bitmap.createBitmap(2048, 1041, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBmp);
        tempImageView.setImageBitmap(tempBmp);
        // 创建一个可以被修改的bitmap
        baseBmp = Bitmap.createBitmap(2048, 1041, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(baseBmp);
        dstbmp = ((BitmapDrawable) imageView.getBackground()).getBitmap();
        pathList = new ArrayList<>();
        imageView.setImageBitmap(baseBmp);
        EventBus.getDefault().register(this);
        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        paint = new Paint();
                        paintWidth = mPaintWidth;
                        setPaint();
                        // 获取手按下时的坐标
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        drawPath = new DrawPath();
                        switch (paintMode) {
                            case MODE_ERASER:
                            case MODE_PAINT:
                            case MODE_LINE:
                            case MODE_CIRCLE:
                            case MODE_SQUARE:
                                path = new Path();
                                drawPath.path = path;
                                drawPath.paint = paint;
                                path.moveTo(startX, startY);
                                break;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 获取手移动后的坐标
                        endX = (int) event.getX();
                        endY = (int) event.getY();
                        switch (paintMode) {
                            case MODE_ERASER:
                                drawEraser(endX, endY);
                                path.lineTo(startX, startY);
                                canvas.drawPath(path, paint);
                                // 刷新开始坐标
                                startX = (int) event.getX();
                                startY = (int) event.getY();
                                break;
                            case MODE_PAINT:
                                int dx = (int) Math.abs(endX - startX);
                                int dy = (int) Math.abs(endY - startY);
                                if (dx > 3 || dy > 3) {
                                    path.quadTo(startX, startY, (startX + endX) / 2,
                                            (startY + endY) / 2);
                                    canvas.drawPath(path, paint);
                                    // 重新为上一次的触摸点赋值
                                    startX = endX;
                                    startY = endY;
                                }
                                break;
                            case MODE_LINE:
                                drawLine(startX, startY, endX, endY);
                                break;
                            case MODE_SQUARE:
                                drawSquare(startX, startY, endX, endY);
                                break;
                            case MODE_CIRCLE:
                                int r = (endX - startX) / 2;
                                drawCircle(startX, startY, r);
//                                RectF oval = new RectF(startX,startY,endX,endY);
//                                drawOval(oval);
                                break;
                        }
                        imageView.setImageBitmap(baseBmp);
                        break;
                    case MotionEvent.ACTION_UP:
                        switch (paintMode) {
                            case MODE_TEXT:
                                EditTextDialog(endX, endY);
                                break;
                            case MODE_LINE:
                                path.lineTo(endX, endY);
                                clearTempCanvas();
                                canvas.drawPath(path, paint);
                                break;
                            case MODE_CIRCLE:
                                int distance = (endX - startX) / 2;
                                clearTempCanvas();
                                path.addCircle(startX, startY, distance, Path.Direction.CW);
                                // path.addOval(startX, startX, endX, endX,
                                // Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            case MODE_SQUARE:
                                path.addRect(startX, startY, endX, endY, Path.Direction.CW);
                                clearTempCanvas();
                                canvas.drawPath(path, paint);
                                break;
                        }
                        if (paintMode != MODE_TEXT) {
                            pathList.add(drawPath);
                        }
                        clearTempCanvas();
                        imageView.setImageBitmap(baseBmp);
                        String perW = MyUtils.getPercentage(startX / width);
                        String perH = MyUtils.getPercentage(startY / height);
                        Log.e("MyLog", "PeletteActivity.onTouch 278行:   --->>> " + perW + "   " + perH);
                        // 发送添加绘画方法
                        addDrawShape(DRAWTYPE);
                        break;
                }
                return true;
            }
        });
    }

    private void initNativeUtil() {
        nativeUtil = NativeUtil.getInstance();
        nativeUtil.setCallListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.MEMBER_CHANGE_INFORM:// 参会人员变更通知
                InterfaceBase.pbui_MeetNotifyMsg object2 = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                //92.查询参会人员
                nativeUtil.queryAttendPeople();
                break;
            case IDEventMessage.PLACE_DEVINFO_CHANGEINFORM:
                //6.查询设备信息
                nativeUtil.queryDeviceInfo();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //添加圆形、矩形
    private void addDrawShape(int type) {
        long timeMillis = System.currentTimeMillis();
        long time = 100;
        count++;
        nativeUtil.addDrawFigure(count, MeetingActivity.getDevId(), MeetingActivity.getDevId(),
                time, timeMillis, type, (int) paint.getStrokeWidth(), paint.getColor(), startX,
                startY, endX, endY);
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_ATTENDEE://查询参会人员
                MyUtils.handTo(IDivMessage.QUERY_ATTENDEE, (InterfaceMember.pbui_Type_MemberDetailInfo) result, "queryMember", handler);
                break;
            case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                MyUtils.handTo(IDivMessage.QUERY_DEVICE_INFO, (InterfaceDevice.pbui_Type_DeviceDetailInfo) result, "devInfos", handler);
                break;
        }
    }

    /**
     * 绘制文本
     */
    private void EditTextDialog(final int x, final int y) {
        editDialog = new AlertDialog.Builder(context).create();
        editDialog.setCanceledOnTouchOutside(false);
        editDialog.show();
        editDialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // 设置弹窗宽高
        Window window = editDialog.getWindow();
        window.setContentView(R.layout.whiteboard_dialog_edittext);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.2); // 高度设置为屏幕的0.7
        p.width = (int) (d.getWidth() * 0.3); // 宽度设置为屏幕的0.8
        window.setAttributes(p);
        final EditText edit = (EditText) window
                .findViewById(R.id.whiteboard_dialog_edit);
        Button confirm = (Button) window
                .findViewById(R.id.whiteboard_dialog_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                drawText = edit.getText().toString();
                paint.setTextSize(30);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawText(drawText, x, y, paint);
                editDialog.dismiss();
                imageView.setImageBitmap(baseBmp);
            }
        });
    }

    /**
     * 在临时画布上绘制橡皮擦图片
     */
    private void drawEraser(int x, int y) {
        clearTempCanvas();
        tempPaint = new Paint();
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.eraser);
        bmp = scaleBitmap(bmp, paintWidth, paintWidth);
        tempCanvas.drawBitmap(bmp, x - bmp.getWidth() / 2, y - bmp.getHeight()
                / 2, tempPaint);
    }

    /**
     * 在临时画布上绘制直线
     */
    private void drawLine(int startX, int startY, int endX, int endY) {
        clearTempCanvas();
        tempCanvas.drawLine(startX, startY, endX, endY, paint);
        int strokeWidth = (int) paint.getStrokeWidth();
    }

    /**
     * 在临时画布上绘制方框
     */
    private void drawSquare(int startX, int startY, int endX, int endY) {
        clearTempCanvas();
        tempCanvas.drawRect(startX, startY, endX, endY, paint);
    }

    /**
     * 在临时画布上绘制圆
     */
    private void drawCircle(int cx, int cy, int radius) {
        clearTempCanvas();
//         tempCanvas.drawOval(startX, startY, endX, endY, paint); //API 21
        tempCanvas.drawCircle(cx, cy, radius, paint);
    }

    private void drawOval(RectF oval) {
        clearTempCanvas();
        tempCanvas.drawOval(oval, paint);
    }

    /**
     * 清空tempCanvas
     */
    private void clearTempCanvas() {
        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        p.setColor(Color.TRANSPARENT);
        tempCanvas.drawPaint(p);
    }

    /**
     * Bitmap缩放
     *
     * @param bitmap 要缩放的图片
     * @param w      新的宽度
     * @param h      新的高度
     * @return resizeBmp指定尺寸的图片
     */
    private static Bitmap scaleBitmap(Bitmap bitmap, int w, int h) {
        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight); // 长和宽放大缩小的比例
        // 得到新的图片
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }


    public class DrawPath {
        public Paint paint;
        public Path path;
    }

    private void initEvent() {
        /**
         * 监听 SeekBar 的值 设置画笔的大小
         */
        mSeb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            /**
             * 停止
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //将停下时的值 赋给 mPaintWidth 达到控制画笔大小
                mPaintWidth = seekBar.getProgress();
            }
        });
        ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                width = imageView.getWidth();
                height = imageView.getHeight();
                Log.e("MyLog", "PeletteActivity.onPreDraw :   --->>> 控件的宽：" + width + " 高：" + height);
                //调用一次后需要注销这个监听，否则会阻塞ui线程。
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    /**
     * 初始化画笔
     */
    private void setPaint() {
        switch (paintMode) {
            case MODE_ERASER:
                paint.setStyle(Paint.Style.STROKE);// 画笔样式：实线
                paint.setStrokeWidth(paintWidth);// 线宽
                paint.setColor(Color.TRANSPARENT);// 颜色
                PorterDuffXfermode mode = new PorterDuffXfermode(
                        PorterDuff.Mode.SRC_OUT);
                paint.setXfermode(mode);// 转换模式
                paint.setAntiAlias(true);// 抗锯齿
                paint.setDither(true);// 防抖动
                paint.setStrokeJoin(Paint.Join.ROUND);// 设置线段连接处的样式为圆弧连接
                paint.setStrokeCap(Paint.Cap.ROUND);// 设置两端的线帽为圆的
                break;
            case MODE_PAINT:
            case MODE_TEXT:
            case MODE_LINE:
            case MODE_CIRCLE:
            case MODE_SQUARE:
            default:
                paint.setStyle(Paint.Style.STROKE);// 画笔样式：实线
                paint.setStrokeWidth(paintWidth);// 线宽
                paint.setColor(paintColor);// 颜色
                PorterDuffXfermode mode2 = new PorterDuffXfermode(
                        PorterDuff.Mode.DST_OVER);
                paint.setXfermode(null);// 转换模式
                paint.setAntiAlias(true);// 抗锯齿
                paint.setDither(true);// 防抖动
                paint.setStrokeJoin(Paint.Join.ROUND);// 设置线段连接处的样式为圆弧连接
                paint.setStrokeCap(Paint.Cap.ROUND);// 设置两端的线帽为圆的
                break;
        }
    }

    /**
     * 清空画布内容
     */
    private void clearCanvas() {
        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        p.setColor(Color.TRANSPARENT);
        canvas.drawPaint(p);
        imageView.setImageBitmap(baseBmp);
    }

    /**
     * 撤销
     */
    private void undo() {
        clearCanvas();
        if (pathList != null && pathList.size() > 0) {
            pathList.remove(pathList.size() - 1);
            // 将路径保存列表中的路径重绘在画布上
            Iterator<DrawPath> iter = pathList.iterator(); // 重复保存
            while (iter.hasNext()) {
                DrawPath dp = iter.next();
                if (dp.path != null && dp.path != null) {
                    canvas.drawPath(dp.path, dp.paint);
                }
            }
        }
        // showToast("pathList.size() = " + pathList.size(), 0);
    }

    /**
     * 设置可快捷选择的颜色
     */
    private void initDefaultColor() {
        setImageColor(mColorBlack, Color.BLACK);
        setImageColor(mColorGray, Color.GRAY);
        setImageColor(mColorRed, Color.RED);
        setImageColor(mColorOrange, Color.rgb(222, 162, 89));
        setImageColor(mColorYellow, Color.YELLOW);
        setImageColor(mColorBrown, Color.rgb(113, 93, 79));
        setImageColor(mColorPurple, Color.rgb(170, 0, 255));
        setImageColor(mColorBlue, Color.BLUE);
        setImageColor(mColorCyan, Color.CYAN);
        setImageColor(mColorGreen, Color.GREEN);
    }

    /**
     * 使用 shape 的同时，利用代码修改 shape 的填充颜色
     *
     * @param view 需要修改的颜色的 View
     * @param argb 需要改成的颜色
     */
    private void setImageColor(ImageView view, int argb) {
        GradientDrawable myGraw = (GradientDrawable) view.getBackground();
        myGraw.setColor(argb);
    }


    /**
     * 设置 图片选中状态的方法
     *
     * @param list
     * @param index
     */
    private void setImgSelect(List<ImageView> list, int index) {
        for (int i = 0; i < list.size(); i++) {
            if (i == index) {
                list.get(i).setSelected(true);
            } else {
                list.get(i).setSelected(false);
            }
        }
    }

    /**
     * 将有选择器的Image放到列表中
     */
    private void initImages() {
        //上方的图片
        mTopImages = new ArrayList<>();
        mTopImages.add(mPen);
        mTopImages.add(mEraser);
        mTopImages.add(mPalette);
        mTopImages.add(mText);
        mTopImages.add(mPic);
        mTopImages.add(mSline);
        mTopImages.add(mEllpse);
        mTopImages.add(mRect);
        mTopImages.add(mBack);
        mTopImages.add(mClean);
        //下方的图片 --> 10个颜色 圆形
        mBotImages = new ArrayList<>();
        mBotImages.add(mColorBlack);
        mBotImages.add(mColorGray);
        mBotImages.add(mColorRed);
        mBotImages.add(mColorOrange);
        mBotImages.add(mColorYellow);
        mBotImages.add(mColorBrown);
        mBotImages.add(mColorPurple);
        mBotImages.add(mColorBlue);
        mBotImages.add(mColorCyan);
        mBotImages.add(mColorGreen);

    }

    private void initView() {
        mTopView = (LinearLayout) findViewById(R.id.top_view);
        mPen = (ImageView) findViewById(R.id.pen);
        mEraser = (ImageView) findViewById(R.id.eraser);
        mPalette = (ImageView) findViewById(R.id.palette);
        mText = (ImageView) findViewById(R.id.text);
        mPic = (ImageView) findViewById(R.id.pic);
        mSline = (ImageView) findViewById(R.id.sline);
        mEllpse = (ImageView) findViewById(R.id.ellpse);
        mRect = (ImageView) findViewById(R.id.rect);
        mBack = (ImageView) findViewById(R.id.back);
        mClean = (ImageView) findViewById(R.id.clean);
        mSeb = (SeekBar) findViewById(R.id.seb);
        mColorBlack = (ImageView) findViewById(R.id.color_black);
        mColorGray = (ImageView) findViewById(R.id.color_gray);
        mColorRed = (ImageView) findViewById(R.id.color_red);
        mColorOrange = (ImageView) findViewById(R.id.color_orange);
        mColorYellow = (ImageView) findViewById(R.id.color_yellow);
        mColorBrown = (ImageView) findViewById(R.id.color_brown);
        mColorPurple = (ImageView) findViewById(R.id.color_purple);
        mColorBlue = (ImageView) findViewById(R.id.color_blue);
        mColorCyan = (ImageView) findViewById(R.id.color_cyan);
        mColorGreen = (ImageView) findViewById(R.id.color_green);
        mShareStart = (Button) findViewById(R.id.share_start);
        mShareStop = (Button) findViewById(R.id.share_stop);
        mSave = (Button) findViewById(R.id.save);
        mExit = (Button) findViewById(R.id.exit);
        tempImageView = (ImageView) findViewById(R.id.whiteboard_temp_imageview);
        imageView = (ImageView) findViewById(R.id.whiteboard_imageview);

        mPen.setOnClickListener(this);
        mEraser.setOnClickListener(this);
        mPalette.setOnClickListener(this);
        mText.setOnClickListener(this);
        mPic.setOnClickListener(this);
        mSline.setOnClickListener(this);
        mEllpse.setOnClickListener(this);
        mRect.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mClean.setOnClickListener(this);
        mColorBlack.setOnClickListener(this);
        mColorGray.setOnClickListener(this);
        mColorRed.setOnClickListener(this);
        mColorOrange.setOnClickListener(this);
        mColorYellow.setOnClickListener(this);
        mColorBrown.setOnClickListener(this);
        mColorPurple.setOnClickListener(this);
        mColorBlue.setOnClickListener(this);
        mColorCyan.setOnClickListener(this);
        mColorGreen.setOnClickListener(this);
        mShareStart.setOnClickListener(this);
        mShareStop.setOnClickListener(this);
        mSave.setOnClickListener(this);
        mExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pen:
                setImgSelect(mTopImages, 0);
                setPaintMode(MODE_PAINT);
                DRAWTYPE = InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_INK.getNumber();
                break;
            case R.id.eraser://橡皮擦
                setImgSelect(mTopImages, 1);
                setPaintMode(MODE_ERASER);
//                DRAWTYPE = InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_INK.getNumber();
                break;
            case R.id.palette://调色板
                setImgSelect(mTopImages, 2);
                new ColorPickerDialog(PeletteActivity.this, new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color) {
                        paintColor = color;
                    }
                }, Color.BLACK).show();
                break;
            case R.id.text://添加文字
                setImgSelect(mTopImages, 3);
                setPaintMode(MODE_TEXT);
                DRAWTYPE = InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_FREETEXT.getNumber();
                break;
            case R.id.pic://打开系统相册
                setImgSelect(mTopImages, 4);
                SelectFile();
                DRAWTYPE = InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_PICTURE.getNumber();
                break;
            case R.id.sline: //画直线
                setImgSelect(mTopImages, 5);
                setPaintMode(MODE_LINE);
                DRAWTYPE = InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_LINE.getNumber();
                break;
            case R.id.ellpse://画圆
                setImgSelect(mTopImages, 6);
                setPaintMode(MODE_CIRCLE);
                DRAWTYPE = InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_ELLIPSE.getNumber();
                break;
            case R.id.rect://矩形
                setImgSelect(mTopImages, 7);
                setPaintMode(MODE_SQUARE);
                DRAWTYPE = InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_RECTANGLE.getNumber();
                break;
            case R.id.back://撤销
                setImgSelect(mTopImages, 8);
                undo();
                break;
            case R.id.clean://清空
                setImgSelect(mTopImages, 9);
                clearCanvas();
                break;
            case R.id.color_black:
                setAnimator(mColorBlack);
                paintColor = Color.BLACK;
                break;
            case R.id.color_gray:
                setAnimator(mColorGray);
                paintColor = Color.GRAY;
                break;
            case R.id.color_red:
                setAnimator(mColorRed);
                paintColor = Color.RED;
                break;
            case R.id.color_orange:
                setAnimator(mColorOrange);
                paintColor = Color.rgb(222, 162, 89);
                break;
            case R.id.color_yellow:
                setAnimator(mColorYellow);
                paintColor = Color.YELLOW;
                break;
            case R.id.color_brown:
                setAnimator(mColorBrown);
                paintColor = Color.rgb(113, 93, 79);
                break;
            case R.id.color_purple:
                setAnimator(mColorPurple);
                paintColor = Color.rgb(170, 0, 255);
                break;
            case R.id.color_blue:
                setAnimator(mColorBlue);
                paintColor = Color.BLUE;
                break;
            case R.id.color_cyan:
                setAnimator(mColorCyan);
                paintColor = Color.CYAN;
                break;
            case R.id.color_green:
                setAnimator(mColorGreen);
                paintColor = Color.GREEN;
                break;
            case R.id.share_start://共享批注
                try {
                    nativeUtil.queryAttendPeople();
                    nativeUtil.queryDeviceInfo();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                showOlLineMember();
                break;
            case R.id.share_stop://停止共享

                break;
            case R.id.save:
//                ImageUtils.saveToGallery(getApplicationContext(), mPaletteView.buildBitmap());

                if (thread == null) {
                    thread = new Thread() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            super.run();
                            saveCanvas();
                            interrupt();
                            thread = null;
                        }
                    };
                    thread.start();
                }
                break;
            case R.id.exit:
                finish();
                break;
        }
    }

    /**
     * 展示在线人员弹出框
     */
    private void showOlLineMember() {
        View popupView = getLayoutInflater().inflate(R.layout.board_pop_member, null);
        mMemberPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(mMemberPop);
        mMemberPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mMemberPop.setTouchable(true);
        mMemberPop.setOutsideTouchable(true);
        MemberViewHolder holder = new MemberViewHolder(popupView);
        MemberHolder(holder);
        mMemberPop.showAtLocation(imageView, Gravity.CENTER, 0, 0);
    }

    public static List<Boolean> boardCheck;

    private void MemberHolder(MemberViewHolder holder) {
        //所有人员
        holder.all_mer_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (int i = 0; i < boardCheck.size(); i++) {
                        boardCheck.set(i, true);
                    }
                }
            }
        });
        //选择人员
        holder.choose_mer_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }
        });
        //确定
        holder.board_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //实现共享批注

            }
        });
        //取消
        holder.board_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMemberPop.dismiss();
            }
        });
        //返回
        holder.board_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMemberPop.dismiss();
            }
        });
    }

    /**
     *
     */
    private void show() {
        View popupView = getLayoutInflater().inflate(R.layout.board_choose_member, null);
        mChooseMemberPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(mChooseMemberPop);
        mChooseMemberPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mChooseMemberPop.setTouchable(true);
        mChooseMemberPop.setOutsideTouchable(true);
        ChooseMemberViewHolder holder = new ChooseMemberViewHolder(popupView);
        ChooseMemberEvent(holder);
        mChooseMemberPop.showAtLocation(imageView, Gravity.CENTER, 0, 0);
    }

    //选择参会人
    private void ChooseMemberEvent(final ChooseMemberViewHolder holder) {
        onLineBoardMemberAdapter.setItemClick(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                Button player = view.findViewById(R.id.palyer_name);
                boolean selected = !player.isSelected();
                player.setSelected(selected);
                boardCheck.set(posion, selected);
                holder.boardAllCheck.setChecked(!boardCheck.contains(false));
                onLineBoardMemberAdapter.notifyDataSetChanged();
            }
        });
        holder.boardAllCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //如果包含false
                    for (int i = 0; i < onLineMembers.size(); i++) {
                        if (!boardCheck.get(i)) {
                            //将包含false的选项设为true
                            boardCheck.set(i, true);
                        }
                    }
                } else {
                    //全选为false
                    if (!boardCheck.contains(false)) {
                        //不包含false 就是全部为true的状态
                        for (int i = 0; i < onLineMembers.size(); i++) {
                            //全部都设为false
                            boardCheck.set(i, false);
                        }
                    }
                }
                onLineBoardMemberAdapter.notifyDataSetChanged();
            }
        });
        holder.boardEnsureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<DevMember> checkedIds = onLineBoardMemberAdapter.getCheckedIds();
                Log.e("MyLog", "PeletteActivity.onClick 1005行:  选中的参会人个数 --->>> " + checkedIds.size());
                mChooseMemberPop.dismiss();
            }
        });
        holder.boardCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChooseMemberPop.dismiss();
            }
        });
    }

    // 打开文件管理器
    private void SelectFile() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据返回选择的文件，来进行操作
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK) {
            // 获取选中文件的uri
            Uri uri = data.getData();
            String realPath = getRealPathFromURI(uri);
            Log.e("MyLog", "PeletteActivity.onActivityResult :  选中的文件路径 --->>> " + realPath);
            if (realPath == null) {
                Toast.makeText(context, "获取该文件的路径失败", Toast.LENGTH_LONG).show();
            } else {
                // 执行操作
                dstbmp = BitmapFactory.decodeFile(realPath);
                Drawable drawable = new BitmapDrawable(dstbmp);
                imageView.setBackground(drawable);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 返回文件选择的全路径
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 保存画布内容
     */
    public void saveCanvas() {
        try {
            File file = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath(), System.currentTimeMillis() + ".png");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            savePath = file.getAbsolutePath();
            FileOutputStream stream = new FileOutputStream(file);
            baseBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            // 为了将画布内容和背景图片绘制到一起，保存为一张图片
            Paint p = new Paint();
            dst = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());// 屏幕>>目标矩形
            // 要先画一次背景图片
            canvas.drawBitmap(dstbmp, null, dst, p);// 画背景图 参数二：null为显示整个图片
            // 再画canvas的内容
            srcbmp = BitmapFactory.decodeFile(savePath);// 获取之前保存的画布图片
            canvas.drawBitmap(srcbmp, null, dst, p);// 画画布图片
            handler.sendEmptyMessage(UPDATE_VIEW);
            // 再保存
            if (file.isFile() && file.exists()) {
                file.delete();
                file = new File(savePath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            FileOutputStream stream2 = new FileOutputStream(file);
            baseBmp.compress(Bitmap.CompressFormat.PNG, 100, stream2);
            stream2.close();
            handler.sendEmptyMessage(SAVE_SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(SAVE_FAILED);
        }

    }

    private void setPaintMode(int mode) {
        paintMode = mode;
    }

    public static void setAnimator(View iv) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(iv, "scaleX", 1f, 1.5f, 1f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(iv, "scaleY", 1f, 1.5f, 1f);
        animatorX.setDuration(300);
        animatorY.setDuration(300);
        animatorX.start();
        animatorY.start();
    }


    public static class MemberViewHolder {
        private RadioButton all_mer_cb;
        private RadioButton choose_mer_cb;
        private Button board_confirm;
        private Button board_cancel;
        private Button board_back;
        public View rootView;

        public MemberViewHolder(View rootView) {
            this.rootView = rootView;
            this.all_mer_cb = (RadioButton) rootView.findViewById(R.id.all_mer_cb);
            this.choose_mer_cb = (RadioButton) rootView.findViewById(R.id.choose_mer_cb);
            this.board_confirm = (Button) rootView.findViewById(R.id.board_confirm);
            this.board_cancel = (Button) rootView.findViewById(R.id.board_cancel);
            this.board_back = (Button) rootView.findViewById(R.id.board_back);
        }

    }

    public static class ChooseMemberViewHolder {
        public View rootView;
        public CheckBox boardAllCheck;
        public RecyclerView boardMemberRl;
        public Button boardEnsureBtn;
        public Button boardCancelBtn;

        public ChooseMemberViewHolder(View rootView) {
            this.rootView = rootView;
            this.boardAllCheck = (CheckBox) rootView.findViewById(R.id.board_all_check);
            this.boardMemberRl = (RecyclerView) rootView.findViewById(R.id.board_member_rl);
//            this.boardMemberRl.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
            this.boardMemberRl.setLayoutManager(new LinearLayoutManager(context));
            if (onLineBoardMemberAdapter != null) {
                this.boardMemberRl.setAdapter(onLineBoardMemberAdapter);
            }
            this.boardEnsureBtn = (Button) rootView.findViewById(R.id.board_ensure_btn);
            this.boardCancelBtn = (Button) rootView.findViewById(R.id.board_cancel_btn);
        }

    }
}
