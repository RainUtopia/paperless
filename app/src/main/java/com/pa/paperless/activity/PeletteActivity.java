package com.pa.paperless.activity;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
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

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceWhiteboard;
import com.pa.paperless.R;
import com.pa.paperless.adapter.setadapter.BoardAdapter;
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

public class PeletteActivity extends Activity implements View.OnClickListener, CallListener {

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
    private ImageView mPen, mEraser, mPalette, mText, mPic, mSline, mEllpse, mRect, mBack, mClean;
    private SeekBar mSeb;
    private int mPaintWidth = 10; // 设置画笔的默认宽度是 10
    private ImageView mColorBlack, mColorGray, mColorRed, mColorOrange, mColorYellow, mColorBrown, mColorPurple, mColorBlue, mColorCyan, mColorGreen;
    private Button mShareStart, mShareStop, mSave, mExit;
    private List<ImageView> mTopImages;//上方的图片
    private List<ImageView> mBotImages;//下方的图片
    private LinearLayout mTopView;
    private Bitmap tempBmp, baseBmp, srcbmp, dstbmp;
    private Canvas tempCanvas, canvas;
    private ImageView imageView, tempImageView;
    private List<DrawPath> pathList;
    private Paint paint, tempPaint;
    private DrawPath drawPath;
    private Path path;
    private AlertDialog editDialog;
    private Thread thread;
    private Rect dst;
    int startX, startY, endX, endY;
    private List<MemberInfo> memberInfos;
    public static BoardAdapter onLineBoardMemberAdapter;
    private boolean isHasData = false;//是否有数据
    public static PeletteActivity context;
    private NativeUtil nativeUtil;
    private PopupWindow mMemberPop, mChooseMemberPop;
    private List<DevMember> onLineMembers;
    private int mWidth, mHeight;
    public static int W = 0;
    public static int H = 0;
    private static int IMAGE_CODE = 1;
    private boolean isSharing = false;//是否共享中
    private List<DrawPath> beforeList;//用来保存共享之前绘制的信息
    private List<DrawPath> afterPath = new ArrayList<>();//用来保存共享中所绘制的信息
    private int sx, sy;
    private int launchPersonId = MainActivity.getLocalInfo().getMemberid();//默认发起的人员ID是本机
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
                case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                    DisposeQueryDevInfo(msg);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelette_view);
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        Log.e("MyLog", "PeletteActivity.onCreate 161行:   --->>> " + display);
        initNativeUtil();
        initView();
        getSeekBar();
        initImages();
        initDefaultColor();
        context = this;
        try {
            //查询参会人员
            nativeUtil.queryAttendPeople();
            //查询设备信息
            nativeUtil.queryDeviceInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
    }

    /**
     * onWindowFocusChanged（）方法是在onLayout（）之后执行的，所以getWidth（）与getHeight（）会得到具体的数值
     * 想要测量出 ImageView （控件）的宽高，必须在 onWindowFocusChanged 方法中
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mWidth = imageView.getWidth();
        mHeight = imageView.getHeight();
        //绘画
        draw();
        Log.e("MyLog", "PeletteActivity.onWindowFocusChanged :   --->>> ImageView的宽：" + mWidth + " 高：" + mHeight);
    }

    /**
     * 处理查询设备信息
     *
     * @param msg
     */
    private void DisposeQueryDevInfo(Message msg) {
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
                            if (devId != MeetingActivity.getDevId()) {//过滤自己的设备
                                onLineMembers.add(new DevMember(memberInfo, devId));
                            }
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
            onLineBoardMemberAdapter = new BoardAdapter(onLineMembers);
            isHasData = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pen:
                setImgSelect(mTopImages, 0);
                setPaintMode(MODE_PAINT);
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
                break;
            case R.id.pic://打开系统相册
                setImgSelect(mTopImages, 4);
                SelectFile();
                break;
            case R.id.sline: //画直线
                setImgSelect(mTopImages, 5);
                setPaintMode(MODE_LINE);
                break;
            case R.id.ellpse://画圆
                setImgSelect(mTopImages, 6);
                setPaintMode(MODE_CIRCLE);
                break;
            case R.id.rect://矩形
                setImgSelect(mTopImages, 7);
                setPaintMode(MODE_SQUARE);
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
                if (!isSharing && isHasData) {//不在共享中并且拥有数据才能打开弹出框
                    showOlLineMember();
                } else if (isSharing) {
                    showToast("已经在共享中");
                } else if (!isHasData) {
                    showToast("没有查找到参会人，请再次点击");
                    try {
                        //查询参会人员
                        nativeUtil.queryAttendPeople();
                        //查询设备信息
                        nativeUtil.queryDeviceInfo();
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.share_stop://停止共享
                List<Integer> alluserid = new ArrayList<>();
                alluserid.add(MeetingActivity.getMemberId());
                //广播本身退出白板  退出成功则设置不在共享中
                isSharing = !(nativeUtil.broadcastStopWhiteBoard(InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_EXIT.getNumber(),
                        "退出白板", MeetingActivity.getMemberId(), MeetingActivity.getMemberId(), System.currentTimeMillis(), alluserid));
                break;
            case R.id.save:
//                ImageUtils.saveToGallery(getApplicationContext(), mPaletteView.buildBitmap());
                if (thread == null) {
                    thread = new Thread() {
                        @Override
                        public void run() {
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

    private void draw() {
        // 创建一个临时的用于显示橡皮擦图片的bitmap
        tempBmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBmp);
        tempImageView.setImageBitmap(tempBmp);
        // 创建一个可以被修改的bitmap
        baseBmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(baseBmp);
        dstbmp = ((BitmapDrawable) imageView.getBackground()).getBitmap();
        pathList = new ArrayList<>();
        imageView.setImageBitmap(baseBmp);
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
                        sx = startX;
                        sy = startY;
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
//                                drawOval(startX, startY, endX, endY);
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
//                                path.addOval(startX, startX, endX, endX, Path.Direction.CW);
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
                        judge();
                        //只有在同屏状态中才发送
                        if (isSharing) {//
//                            double psw = MyUtils.div(sx, mWidth, 2);
//                            double psy = MyUtils.div(sy, mHeight, 2);
//                            double pex = MyUtils.div(endX, mWidth, 2);
//                            double pey = MyUtils.div(endY, mHeight, 2);
//                            Log.e("MyLog", "PeletteActivity.onTouch 381行:   --->>> " + sx + " / " + mWidth + " = " + psw + "   " + sy + " / " + mHeight + " = "
//                                    + psy + "   " + endX + " / " + mHeight + " = " + pex + "  " + endY + " / " + mHeight + " = " + pey);
                            List<Float> allpt = new ArrayList<Float>();
                            allpt.add((float) MyUtils.div(sx, mWidth, 2));
                            allpt.add((float) MyUtils.div(sy, mHeight, 2));
                            allpt.add((float) MyUtils.div(endX, mWidth, 2));
                            allpt.add((float) MyUtils.div(endY, mHeight, 2));
                            // 发送添加绘画方法
                            addDrawShape(paintMode, allpt);
                        }
                        break;
                }
                return true;
            }
        });
    }

    //添加圆形、矩形、直线
    private void addDrawShape(int type, List<Float> allpt) {
        long timeMillis = System.currentTimeMillis();
        int score = (int) (timeMillis / 10);
        long time = timeMillis + 10000;
        Log.e("MyLog", "PeletteActivity.addDrawShape 538行:   --->>> " + timeMillis + "   " + score + "  " + time);
        nativeUtil.addDrawFigure(score, MainActivity.getCompereInfo().getPersonid(), launchPersonId,/*:发起人的人员ID*/
                time, timeMillis, type, paintWidth, paintColor, allpt);
    }

    /**
     * 设置最大与最小值
     */
    private void judge() {
        if (startX > mWidth) {
            startX = mWidth;
        }
        if (startX < 0) {
            startX = 0;
        }
        if (startY > mHeight) {
            startY = mHeight;
        }
        if (startY < 0) {
            startY = 0;
        }
        if (endX > mWidth) {
            endX = mWidth;
        }
        if (endX < 0) {
            endX = 0;
        }
        if (endY > mHeight) {
            endY = mHeight;
        }
        if (endY < 0) {
            endY = 0;
        }
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
            case IDEventMessage.PLACE_DEVINFO_CHANGEINFORM:// 会场设备信息变更通知
                //6.查询设备信息
                nativeUtil.queryDeviceInfo();
                break;
            case IDEventMessage.ADD_DRAW_INFORM://添加矩形、直线、圆形通知
                receiveAddLine(message);
                break;
            case IDEventMessage.OPEN_BOARD://收到白板打开操作
                receiveOpenWhiteBoard(message);
                break;
            case IDEventMessage.AGREED_JOIN://同意加入通知
                agreedJoin(message);
                break;
        }
    }

    /**
     * 收到同意加入操作
     *
     * @param message
     */
    private void agreedJoin(EventMessage message) {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper object3 = (InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper) message.getObject();
        Log.e("MyLog", "PeletteActivity.agreedJoin 602行:   --->>> 同意加入通知EventBus");
        beforeList = pathList;//保存加入共享前绘制的信息
        clearCanvas();//清空画布
        int opermemberid1 = object3.getOpermemberid();//当前该命令的人员ID
        int srcmemid1 = object3.getSrcmemid();//发起人的人员ID 白板标识使用
        long srcwbid1 = object3.getSrcwbid();//发起人的白板标识 取微秒级的时间作标识 白板标识使用
        for (int i = 0; i < memberInfos.size(); i++) {
            MemberInfo memberInfo = memberInfos.get(i);
            if (memberInfo.getPersonid() == opermemberid1) {
                String name = memberInfo.getName();
                showToast(name + "  加入了共享");
            }
        }
        //作为发起端，只要有人同意了就设置在共享中
        isSharing = true;
    }

    /**
     * 收到白板打开操作
     *
     * @param message
     */
    private void receiveOpenWhiteBoard(EventMessage message) {
        InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard object1 = (InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard) message.getObject();
        Log.e("MyLog", "PeletteActivity.receiveOpenWhiteBoard 625行:   --->>> 收到白板打开操作EventBus");
        int operflag = object1.getOperflag();
        ByteString medianame = object1.getMedianame();
        int opermemberid = object1.getOpermemberid();
        int srcmemid = object1.getSrcmemid();
        long srcwbid = object1.getSrcwbid();
        launchPersonId = srcmemid;//设置发起的人员ID
        if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_FORCEOPEN.getNumber()) {
            Log.e("MyLog", "PeletteActivity.getEventMessage 446行:  这是强制打开白板 --->>> ");
            //强制打开白板  直接强制同意加入
            nativeUtil.agreeJoin(opermemberid, srcmemid, srcwbid);
            isSharing = true;//如果同意加入就设置已经在共享中
        } else if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber()) {
            Log.e("MyLog", "PeletteActivity.getEventMessage 450行:  这是询问打开白板 --->>> ");
            //询问打开白板
            WhetherOpen(opermemberid, srcmemid, srcwbid, MyUtils.getBts(medianame));
        }
    }

    /**
     * 收到添加矩形、直线、圆形操作
     *
     * @param message
     */
    private void receiveAddLine(EventMessage message) {
        InterfaceWhiteboard.pbui_Item_MeetWBRectDetail object = (InterfaceWhiteboard.pbui_Item_MeetWBRectDetail) message.getObject();
        Log.e("MyLog", "PeletteActivity.receiveAddLine : 收到添加矩形、直线、圆形通知EventBus--->>> 发起的人员ID： " + object.getSrcmemid());
        int operid = object.getOperid();
        int opermemberid2 = object.getOpermemberid();
        int srcmemid2 = object.getSrcmemid();
        long srcwbid2 = object.getSrcwbid();
        long utcstamp = object.getUtcstamp();
        int figuretype = object.getFiguretype();
        int linesize = object.getLinesize();
        int color = object.getArgb();
        float gsx = 0, gsy = 0, gex = 0, gey = 0;   // 得到的坐标
        List<Float> ptList = object.getPtList();
        for (int i = 0; i < ptList.size(); i++) {
            Float aFloat = ptList.get(i);
            switch (i) {
                case 0:
                    gsx = mWidth * aFloat;
                case 1:
                    gsy = mHeight * aFloat;
                    break;
                case 2:
                    gex = mWidth * aFloat;
                    break;
                case 3:
                    gey = mHeight * aFloat;
                    break;
            }
            Log.e("MyLog", "PeletteActivity.getEventMessage 403行:   --->>> 得到的小数：" + aFloat);
        }
        paint.setColor(color);
        paint.setStrokeWidth(linesize);
        //判断图形类型
        switch (figuretype) {
            case MODE_LINE://直线
                path.moveTo(gsx, gsy);
                path.lineTo(gex, gey);
                canvas.drawPath(path, paint);
                break;
            case MODE_CIRCLE://圆环
                int distance = (int) ((gex - gsx) / 2);
                path.addCircle(gsx, gsy, distance, Path.Direction.CW);
                canvas.drawPath(path, paint);
                break;
            case MODE_SQUARE://方框
                path.addRect(gsx, gsy, gex, gey, Path.Direction.CW);
                canvas.drawPath(path, paint);
                break;
        }
        DrawPath drawPath = new DrawPath();
        drawPath.paint = paint;
        drawPath.path = path;
        afterPath.add(drawPath);
    }


    //是否同意加入弹出框
    private void WhetherOpen(final int opermemberid, final int srcmemid, final long srcwbid, String medianame) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("是否同意加入" + medianame + "发起的共享批注白板？");
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //同意加入
                Log.e("MyLog", "PeletteActivity.onClick 635行:   --->>> " + opermemberid + " : " + MeetingActivity.getMemberId());
                nativeUtil.agreeJoin(MeetingActivity.getMemberId(), srcmemid, srcwbid);
                isSharing = true;//如果同意加入就设置已经在共享中
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    private void drawOval(int sx, int sy, int ex, int ey) {
        clearTempCanvas();
        tempCanvas.drawOval(sx, sy, ex, ey, paint);
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


    /**
     * 监听 SeekBar 的值 设置画笔的大小
     */
    private void getSeekBar() {
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
                if (onLineBoardMemberAdapter != null) {
                    show();
                } else {
                    showToast("没有找到在线的参会人");
                }
            }
        });
        //确定
        holder.board_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Integer> devIds = new ArrayList<Integer>();
                if (onLineBoardMemberAdapter != null) {
                    List<DevMember> checkedIds = onLineBoardMemberAdapter.getCheckedIds();
                    for (int i = 0; i < checkedIds.size(); i++) {
                        devIds.add(checkedIds.get(i).getMemberInfos().getPersonid());
                    }
                }
                if (devIds.size() > 0) {
                    // 发起共享批注
                    nativeUtil.coerceStartWhiteBoard(InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber(),
                            MyUtils.getBts(MainActivity.getLocalInfo().getMembername()), MeetingActivity.getMemberId(), MeetingActivity.getMemberId(), System.currentTimeMillis(), devIds);
                }
                mMemberPop.dismiss();
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

    //吐丝
    private void showToast(String message) {
        Toast.makeText(PeletteActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 打开选择参会人列表
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
                Button player = (Button) view.findViewById(R.id.palyer_name);
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
                Log.e("MyLog", "PeletteActivity.onClick 1220行:   --->>> 选中参会人个数： " + checkedIds.size());
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
            startActivityForResult(intent, IMAGE_CODE);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据返回选择的文件，来进行操作
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CODE && resultCode == Activity.RESULT_OK) {
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
            canvas.drawBitmap(dstbmp, null, dst, p);// 画背景图 参数二：null 为显示整个图片
            // 再画 canvas 的内容
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
            this.boardMemberRl.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            if (onLineBoardMemberAdapter != null) {
                this.boardMemberRl.setAdapter(onLineBoardMemberAdapter);
            }
            this.boardEnsureBtn = (Button) rootView.findViewById(R.id.board_ensure_btn);
            this.boardCancelBtn = (Button) rootView.findViewById(R.id.board_cancel_btn);
        }
    }
}
