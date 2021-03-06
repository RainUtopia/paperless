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
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
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
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceWhiteboard;
import com.pa.paperless.R;
import com.pa.paperless.adapter.setadapter.BoardAdapter;
import com.pa.paperless.bean.DevMember;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fragment.meeting.OverviewDocFragment;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.service.FabService;
import com.pa.paperless.service.NativeService;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.MyUtils;
import com.pa.paperless.views.ColorPickerDialog;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.pa.paperless.service.NativeService.nativeUtil;

public class PeletteActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "PeletteActivity-->";
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
    private String savePath = "";// 保存画布内容的图片路径
    private String drawText = "";// 保存要绘制的文本

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
    private ImageView imageView, tempImageView/*, BgImageView*/;
    private Paint paint, tempPaint;
    private DrawPath drawPath;
    private Path path;
    private Thread thread;
    private Rect dst;
    float startX, startY, endX, endY, sx, sy;
    private List<MemberInfo> memberInfos;
    public static BoardAdapter onLineBoardMemberAdapter;
    public static PeletteActivity context;
    private PopupWindow mMemberPop, mChooseMemberPop;
    private List<DevMember> onLineMembers;
    private int mWidth, mHeight;
    public static int W = 0;
    public static int H = 0;
    private static int IMAGE_CODE = 777;
    public static boolean isSharing = false;//是否共享中
    public static int launchPersonId = NativeService.localMemberId;//默认发起的人员ID是本机
    public static boolean ISFROMDOCUMENTFRAGMENT = false;//初始化是否从外部文档打开

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
            }
        }
    };
    public static long mSrcwbid;//发起人的白板标识
    private List<PointF> points;
    private PointF pointF;
    private PointF p1;
    private PointF p2;
    private List<DrawPath> pathList = new ArrayList<>();
    private List<DrawPath> LocalPathList;
    private String screenshot;
    private ArrayList<Integer> localOperids;
    private List<InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper> sharedMembers;//用来存放当前正在共享中的人数
    private boolean clicked;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.member_info://收到参会人信息
                receiveMemberInfo(message);
                break;
            case IDEventF.dev_info://收到设备信息
                receiveDevInfo(message);
                break;
            case IDEventMessage.MEMBER_CHANGE_INFORM:// 参会人员变更通知
                //92.查询参会人员
                nativeUtil.queryAttendPeople();
                break;
            case IDEventMessage.PLACE_DEVINFO_CHANGEINFORM:// 会场设备信息变更通知
                //6.查询设备信息
                if (this.memberInfos != null) {
                    nativeUtil.queryDeviceInfo();
                }
                break;
            case IDEventMessage.OPEN_BOARD://收到白板打开操作
                if (MeetingActivity.PaletteIsShowing) {
                    receiveOpenWhiteBoard(message);
                }
                break;
            case IDEventMessage.AGREED_JOIN://同意加入通知
                agreedJoin(message);
                break;
            case IDEventMessage.REJECT_JOIN://拒绝加入通知
                whoToast(((InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper) message.getObject()).getOpermemberid(), " 拒绝加入共享");
                break;
            case IDEventMessage.EXIT_WHITE_BOARD://参会人员退出白板通知
                InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper object = (InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper) message.getObject();
                /** **** **  确保当前退出的人员是保存在集合中的，因为有可能是其他一队共享的人退出  ** **** **/
                if (sharedMembers != null) {
                    if (sharedMembers.contains(object)) {
                        sharedMembers.remove(object);
                        Log.e(TAG, "PeletteActivity.getEventMessage :  删除掉 --> " + sharedMembers.size());
                        if (sharedMembers.size() == 0) {
                            /** **** **  如果没有其他人了就退出共享  ** **** **/
                            stopShare();
                        }
                    }
                }
                whoToast(object.getOpermemberid(), " 退出了共享");
                break;
            case IDEventMessage.ADD_DRAW_INFORM://添加矩形、直线、圆形通知
                receiveAddLine(message);
                break;
            case IDEventMessage.ADD_DRAW_TEXT://添加文本通知
                receiveAddText(message);
                break;
            case IDEventMessage.ADD_PIC_INFORM://添加图片通知
                receiveAddPic(message);
                break;
            case IDEventMessage.ADD_INK_INFORM://添加墨迹通知
                receiveAddInk(message);
                break;
            case IDEventMessage.WHITEBROADE_DELETE_RECOREINFORM://白板删除记录通知
                receiveDeleteEmptyRecore(message, 1);
                break;
            case IDEventMessage.WHITEBOARD_EMPTY_RECORDINFORM://白板清空记录通知
                receiveDeleteEmptyRecore(message, 2);
                break;
        }
    }

    private void receiveMemberInfo(EventMessage message) {
        InterfaceMember.pbui_Type_MemberDetailInfo object1 = (InterfaceMember.pbui_Type_MemberDetailInfo) message.getObject();
        memberInfos = Dispose.MemberInfo(object1);
        if (memberInfos != null) {
            try {
                nativeUtil.queryDeviceInfo();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveDevInfo(EventMessage message) {
        if (clicked) {
            clicked = false;
            InterfaceDevice.pbui_Type_DeviceDetailInfo object2 = (InterfaceDevice.pbui_Type_DeviceDetailInfo) message.getObject();
            List<DeviceInfo> deviceInfos = Dispose.DevInfo(object2);
            onLineMembers = new ArrayList<>();
            for (int i = 0; i < deviceInfos.size(); i++) {
                DeviceInfo deviceInfo = deviceInfos.get(i);
                int netState = deviceInfo.getNetState();
                int devId = deviceInfo.getDevId();
                int memberId = deviceInfo.getMemberId();
                if (netState == 1) {//在线状态
                    for (int j = 0; j < memberInfos.size(); j++) {
                        MemberInfo memberInfo = memberInfos.get(j);
                        if (memberInfo.getPersonid() == memberId) {
                            //查找到在线状态的参会人员
//                                if (devId != MeetingActivity.getDevId()) {//过滤自己的设备
                            if (devId != NativeService.localDevId) {//过滤自己的设备
                                onLineMembers.add(new DevMember(memberInfo, devId));
                            }
                        }
                    }
                }
            }
            if (onLineMembers.size() > 0) {
                //初始化在线参会人是否选中集合
                boardCheck = new ArrayList<>();
                //初始化 全部设为false
                for (int k = 0; k < onLineMembers.size(); k++) {
                    boardCheck.add(false);
                }
                if (onLineBoardMemberAdapter == null) {
                    onLineBoardMemberAdapter = new BoardAdapter(onLineMembers);
                }
                onLineBoardMemberAdapter.notifyDataSetChanged();
            } else {
                showToast("没有找到在线状态的参会人");
            }
            if (!isSharing && onLineMembers.size() > 0) {//不在共享中并且拥有数据才能打开弹出框
                showOlLineMember();
            } else if (isSharing) {
                showToast("已经在共享中");
            } else if (onLineMembers.size() == 0) {
                showToast("没有查找到参会人");
            }
        }
    }

    /**
     * 收到删除/清空记录操作
     *
     * @param message
     */
    private void receiveDeleteEmptyRecore(EventMessage message, int type) {
        if (!isSharing) {
            Log.e(TAG, "PeletteActivity.receiveDeleteEmptyRecore :  不在共享中 --> ");
            return;
        }
        InterfaceWhiteboard.pbui_Type_MeetClearWhiteBoard object = (InterfaceWhiteboard.pbui_Type_MeetClearWhiteBoard) message.getObject();
        Log.e(TAG, "PeletteActivity.receiveDeleteEmptyRecore :  白板删除记录通知EventBus --->>> ");
        int operid = object.getOperid();
        int opermemberid = object.getOpermemberid();
        int srcmemid = object.getSrcmemid();
        long srcwbid = object.getSrcwbid();
        long utcstamp = object.getUtcstamp();
        int figuretype = object.getFiguretype();
        if (type == 1) { //删除
            //1.先清空画板
            clearCanvas();
            //2.删除指定的路径
            for (int i = 0; i < pathList.size(); i++) {
                if (pathList.get(i).operid == operid && pathList.get(i).opermemberid == opermemberid
                        && pathList.get(i).srcwbid == srcwbid) {//操作ID
                    Log.e(TAG, "PeletteActivity.receiveDeleteEmptyRecore :  确认过眼神 --> ");
                    pathList.remove(i);
                }
            }
            //删除后重新绘制不需要删除的
            drawAgain(pathList);
            //因为清空了画板，所以自己绘制的要重新再绘制
            drawAgain(LocalPathList);
        } else if (type == 2) {//清空
            clearCanvas();
            //遍历删除多个，for循环不能删除多个，因为pathList删除后长度会改变，而 i 作为索引一直在增加
            Iterator<DrawPath> iterator = pathList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().opermemberid == opermemberid /*&& iterator.next().srcwbid == srcwbid*/) {
                    iterator.remove();
                }
            }
            //删除后重新绘制不需要删除的
            drawAgain(pathList);
            //因为清空了画板，所以自己绘制的要重新再绘制
            drawAgain(LocalPathList);
        }
    }

    /**
     * 重新绘制
     *
     * @param pathList
     */
    public void drawAgain(List<DrawPath> pathList) {
        //3.重复保存
        Iterator<DrawPath> iter = pathList.iterator();
        //4.重新绘制
        while (iter.hasNext()) {
            DrawPath next = iter.next();
            if (next.paint != null) {
                if (next.path != null) {
                    canvas.drawPath(next.path, next.paint);
                } else if (next.text != null) {
                    if (next.lw < next.rw) {
                        addfunDraw(next.paint, next.height, next.cansee, (int) next.pointF.x, (int) next.pointF.y, next.text);
                    } else {
                        canvas.drawText(next.text, next.pointF.x, next.pointF.y, next.paint);
                    }
                }
            }
        }
    }

    /**
     * 收到添加墨迹操作
     *
     * @param message
     */
    private void receiveAddInk(EventMessage message) {
        if (!isSharing) {
            Log.e(TAG, "PeletteActivity.receiveAddInk :  不在共享中 --> ");
            return;
        }
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardInkItem object = (InterfaceWhiteboard.pbui_Type_MeetWhiteBoardInkItem) message.getObject();
        Log.e(TAG, "PeletteActivity.receiveAddInk :  收到添加墨迹操作EventBus --->>> ");
        int operid = object.getOperid();
        int opermemberid = object.getOpermemberid();
        int figuretype = object.getFiguretype();
        int srcmemid = object.getSrcmemid();
        long srcwbid = object.getSrcwbid();
        int linesize = object.getLinesize();
        int argb = object.getArgb();
        List<Float> pinklistList = object.getPinklistList();
        Log.e(TAG, "PeletteActivity.receiveAddInk 266行:   接收到的xy个数--->>> " + object.getPinklistCount());
        if (points == null) {
            points = new ArrayList<>();
        } else {
            points.clear();
        }
        for (int i = 0; i < pinklistList.size(); i++) {
            Float aFloat = pinklistList.get(i);
            if (i % 2 == 0) {
                pointF = new PointF();
//                pointF.x = aFloat * mWidth;
                pointF.x = aFloat;
            } else {
//                pointF.y = aFloat * mHeight;
                pointF.y = aFloat;
                points.add(pointF);
            }
        }
        //新建 paint 和 path
        Paint newPaint = getNewPaint(linesize, argb);
        Path allInkPath = new Path();
        p1 = new PointF();
        p2 = new PointF();
        //绘画
        float sx;
        float sy;
        if (figuretype == InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_INK.getNumber()) {
            p1.x = points.get(0).x;
            p1.y = points.get(0).y;
            Path newPath = new Path();
            sx = p1.x;
            sy = p1.y;
            newPath.moveTo(p1.x, p1.y);
            for (int i = 1; i < points.size() - 1; i++) {
                p2.x = points.get(i).x;
                p2.y = points.get(i).y;
                float dx = Math.abs(p2.x - sx);
                float dy = Math.abs(p2.y - sy);
                if (dx >= 3 || dy >= 3) {
                    float cx = (p2.x + sx) / 2;
                    float cy = (p2.y + sy) / 2;
                    newPath.quadTo(sx, sy, cx, cy);
                }
                canvas.drawPath(newPath, newPaint);
                sx = p2.x;
                sy = p2.y;
//                newPath.quadTo((points.get(i - 1).x + p2.x) / 2, (points.get(i - 1).y + p2.y) / 2, p2.x, p2.y);
                allInkPath.addPath(newPath);
            }
            DrawPath drawPath = new DrawPath();
            drawPath.paint = newPaint;
            drawPath.path = allInkPath;
            drawPath.operid = operid;
            drawPath.srcwbid = srcwbid;
            drawPath.srcmemid = srcmemid;
            drawPath.opermemberid = opermemberid;
            //将路径保存到共享中绘画信息
            pathList.add(drawPath);
            points.clear();
        }
        imageView.setImageBitmap(baseBmp);
    }

    /**
     * 收到添加图片操作
     *
     * @param message
     */
    private void receiveAddPic(EventMessage message) {
        if (!isSharing) {
            Log.e(TAG, "PeletteActivity.receiveAddPic :  不在共享中 --> ");
            return;
        }
        InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail object = (InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail) message.getObject();
        Log.e(TAG, "PeletteActivity.receiveAddPic :  收到添加图片操作EventBus --->>> ");
        ByteString picdata = object.getPicdata();
        byte[] bytes2 = picdata.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes2);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        setImage(bitmap);
        imageView.setImageBitmap(baseBmp);
//        Drawable drawable = new BitmapDrawable(bitmap);
//        imageView.setBackground(drawable);
    }

    /**
     * 收到添加矩形、直线、圆形操作
     *
     * @param message
     */
    private void receiveAddLine(EventMessage message) {
        if (!isSharing) {
            Log.e(TAG, "PeletteActivity.receiveAddLine :  不在共享中 --> ");
            return;
        }
        InterfaceWhiteboard.pbui_Item_MeetWBRectDetail object = (InterfaceWhiteboard.pbui_Item_MeetWBRectDetail) message.getObject();
        Log.e(TAG, "PeletteActivity.receiveAddLine : 收到添加矩形、直线、圆形通知EventBus--->>> 发起的人员ID： " + object.getSrcmemid());
        int operid = object.getOperid();
        int opermemberid = object.getOpermemberid();
        int srcmemid2 = object.getSrcmemid();
        long srcwbid2 = object.getSrcwbid();
        long utcstamp = object.getUtcstamp();
        int figuretype = object.getFiguretype();
        int linesize = object.getLinesize();
        int color = object.getArgb();
        List<Float> ptList = object.getPtList();

        Paint newPaint = getNewPaint(linesize, color);
        Path newPath = new Path();
        float[] allPoint = getFloats(ptList);
        //根据图形类型绘制
        if (figuretype == InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_RECTANGLE.getNumber()) {
            //矩形
            newPath.addRect(allPoint[0], allPoint[1], allPoint[2], allPoint[3], Path.Direction.CW);
        } else if (figuretype == InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_LINE.getNumber()) {
            //直线
            newPath.moveTo(allPoint[0], allPoint[1]);
            newPath.lineTo(allPoint[2], allPoint[3]);
        } else if (figuretype == InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_ELLIPSE.getNumber()) {
            //圆
            int distance = (int) ((allPoint[2] - allPoint[0]) / 2);
            newPath.addCircle(allPoint[0], allPoint[1], distance, Path.Direction.CW);
        }
        //实时更新界面
//        clearTempCanvas();
        canvas.drawPath(newPath, newPaint);
        imageView.setImageBitmap(baseBmp);
        DrawPath drawPath = new DrawPath();
        drawPath.paint = newPaint;
        drawPath.path = newPath;
        drawPath.operid = operid;
        drawPath.srcwbid = srcwbid2;
        drawPath.srcmemid = srcmemid2;
        drawPath.opermemberid = opermemberid;
        Log.e(TAG, "PeletteActivity.receiveAddLine 451行:  添加时的 --->>> " + opermemberid);
        //将路径保存到共享中绘画信息
        pathList.add(drawPath);
    }


    /**
     * 收到添加文本操作
     *
     * @param message
     */
    private void receiveAddText(EventMessage message) {
        if (!isSharing) {
            Log.e(TAG, "PeletteActivity.receiveAddText :  不在共享中 --> ");
            return;
        }
        InterfaceWhiteboard.pbui_Item_MeetWBTextDetail object = (InterfaceWhiteboard.pbui_Item_MeetWBTextDetail) message.getObject();
        Log.e(TAG, "PeletteActivity.receiveAddText :  添加文本通知EventBus --->>> ");
        int operid = object.getOperid();
        int opermemberid = object.getOpermemberid();
        int srcmemid = object.getSrcmemid();
        long srcwbid = object.getSrcwbid();
        long utcstamp = object.getUtcstamp();
        int figuretype = object.getFiguretype();
        int fontsize = object.getFontsize();
        int fontflag = object.getFontflag();// Normal/Bold/Italic/Bold Italic
        Log.e(TAG, "PeletteActivity.receiveAddText 394行:  fontflag --->>> " + fontflag);
        int argb = object.getArgb();
        String fontname = MyUtils.getBts(object.getFontname());// 宋体/黑体...
        float lx = object.getLx();  // 获取到文本起点x
        float ly = object.getLy();  // 获取到文本起点y
        String ptext = MyUtils.getBts(object.getPtext());  // 文本内容
        Log.e(TAG, "PeletteActivity.receiveAddText :  收到的文本内容 --->>> " + ptext);
        if (figuretype == InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_FREETEXT.getNumber()) {
            int x = (int) (lx);
            int y = (int) (ly);
            //5689
            Paint newPaint = getNewPaint(3, argb);
            newPaint.setTextSize(fontsize);
            newPaint.setFlags(fontflag);
            newPaint.setStyle(Paint.Style.FILL_AND_STROKE);

            Rect rect = new Rect();
            newPaint.getTextBounds(ptext, 0, ptext.length(), rect);
            int width = rect.width();//所有文本的宽度
            int height = rect.height();//文本的高度（用于换行显示）
            int remainWidth = mWidth - x;//可容许显示文本的宽度
            int ilka = width / ptext.length();//每个文本的宽度
            int canSee = remainWidth / ilka;//可以显示的文本个数

            if (remainWidth < width) {// 小于所有文本的宽度（不够显示）
                addfunDraw(newPaint, height, canSee - 1, x, y, ptext);
            } else {//足够空间显示则直接画出来
                canvas.drawText(ptext, lx, ly, newPaint);
            }
            DrawPath drawPath = new DrawPath();
            drawPath.opermemberid = opermemberid;
            drawPath.operid = operid;
            drawPath.srcwbid = srcwbid;
            drawPath.srcmemid = srcmemid;
            drawPath.paint = newPaint;
            drawPath.height = height;
            drawPath.text = ptext;
            drawPath.pointF = new PointF(x, y);
            drawPath.cansee = canSee;
            drawPath.lw = remainWidth;
            drawPath.rw = width;
            pathList.add(drawPath);
            imageView.setImageBitmap(baseBmp);
        }
    }


    private void addfunDraw(Paint paint, int height, int canSee, int fx, int fy, String text) {
        if (text.length() > canSee) {//如果文本的个数大于可以显示的个数
            String canSeeText = text.substring(0, canSee);//获取可以显示的文本
            canvas.drawText(canSeeText, fx, fy, paint);//先将可显示部门画出
            String substring = text.substring(canSee, text.length());//获得剩下无法显示的文本
            if (substring.length() > 0) {
                addfunDraw(paint, height, canSee, fx, fy + height, substring);
            }
        } else {
            canvas.drawText(text, fx, fy, paint);
        }
    }

    /**
     * 吐丝信息：  谁_怎么了
     *
     * @param opermemberid
     * @param something
     */
    private void whoToast(int opermemberid, String something) {
        if (memberInfos != null) {
            for (int i = 0; i < memberInfos.size(); i++) {
                if (memberInfos.get(i).getPersonid() == opermemberid) {
                    showToast(memberInfos.get(i).getName() + something);
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelette_view);
        MeetingActivity.PaletteIsShowing = true;
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        Log.e(TAG, "PeletteActivity.onCreate 161行:   --->>> " + display);
        Log.e(TAG, "PeletteActivity.onCreate 558行:   --->>> " + isSharing + "    " + launchPersonId + "   " + mSrcwbid);
        initView();
        getSeekBar();
        initImages();
        initDefaultColor();
        context = this;
        getIntentData();
        imageView.post(new Runnable() {
            @Override
            public void run() {
                mWidth = imageView.getWidth();
                mHeight = imageView.getHeight();
                Log.e(TAG, "PeletteActivity.run 573行:  初始化时画板的宽高 --->>> " + mWidth + "  " + mHeight);
                draw();
            }
        });
    }

    /**
     * 获取到传递其它页面过来的数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        screenshot = intent.getStringExtra("postilpic");
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
                //将其他人的绘制信息重新绘制
                drawAgain(pathList);
                if (isSharing && localOperids.size() > 0) {//保证好存在撤销的路径
                    //本地的绘制数据要清空
                    LocalPathList.clear();
                    localOperids.clear();
                    long utcstamp = System.currentTimeMillis();
                    int operid = (int) (utcstamp / 10);
                    int opermemberid = NativeService.localMemberId;
                    int srcmemid = launchPersonId;
                    long srcwbid = mSrcwbid;
                    int figuretype = InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_ZERO.getNumber();
                    //发送清空消息
                    nativeUtil.whiteBoardClearRecord(operid, opermemberid, srcmemid, srcwbid, utcstamp, figuretype);
                }
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
                clicked = true;
                //查询参会人员
                try {
                    nativeUtil.queryAttendPeople();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.share_stop://停止共享
                //广播本身退出白板  退出成功则设置不在共享中
                if (isSharing) {
                    stopShare();
                } else {
                    showToast("当前并不在共享状态中");
                }
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
                if (isSharing) {
                    stopShare();
                    finish();
                } else {
                    finish();
                }
                break;
        }
    }


    /**
     * 退出共享
     */
    private void stopShare() {
        List<Integer> alluserid = new ArrayList<>();
        alluserid.add(NativeService.localMemberId);
        nativeUtil.broadcastStopWhiteBoard(InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_EXIT.getNumber(),
                "退出白板", NativeService.localMemberId, launchPersonId, mSrcwbid, alluserid);
        isSharing = false;
        mSrcwbid = 0;
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
        //判断打开画板的时候，是否是从外部文档打开的
        if (ISFROMDOCUMENTFRAGMENT) {
            Bitmap bitmap = BitmapFactory.decodeFile(Macro.TAKE_PHOTO + OverviewDocFragment.now_time + ".jpg");
            canvas.drawBitmap(bitmap, 0, 0, new Paint());
            ISFROMDOCUMENTFRAGMENT = false;
        }
        if (screenshot != null && screenshot.equals("postilpic")) {
            if (FabService.bytes != null) {
                Bitmap bitmap = FileUtil.bytes2Bitmap(FabService.bytes);
//            Bitmap bitmap = FileUtil.bytes2Bitmap(screenshot);
//            int halfW = bitmap.getWidth() / 2;
//            int halfH = bitmap.getHeight() / 2;
                FabService.bytes = null;
                Log.e(TAG, "PeletteActivity.draw 808行:  图片的宽高 --->>> " + bitmap.getWidth() + "," + bitmap.getHeight());
//            canvas.drawBitmap(bitmap, mWidth / 2 - halfW, mHeight / 2 - halfH, new Paint());
                setImage(bitmap);
            }
        }
        imageView.setImageBitmap(baseBmp);
        LocalPathList = new ArrayList<>();
        points = new ArrayList<>();

        Log.i(TAG, "new points arraylist");
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        paint = new Paint();
                        paintWidth = mPaintWidth;
                        setPaint();
                        // 获取手按下时的坐标
                        startX = event.getX();
                        startY = event.getY();
                        sx = startX;
                        sy = startY;
                        //添加按下时的点坐标
//                        float sxf = (float) MyUtils.div(sx, mWidth, 2);
//                        float syf = (float) MyUtils.div(sy, mHeight, 2);
//                        points.add(new PointF(sxf, syf));
//                        Log.e(TAG, "PeletteActivity.onTouch 658行:   --->>> " + sxf + "   " + syf);
                        points.add(new PointF(sx, sy));//添加按下时的点
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
                        endX = event.getX();
                        endY = event.getY();
                        switch (paintMode) {
                            case MODE_ERASER:
                                drawEraser(endX, endY);
                                path.lineTo(startX, startY);
                                canvas.drawPath(path, paint);
                                // 刷新开始坐标
                                startX = event.getX();
                                startY = event.getY();
                                break;
                            case MODE_PAINT:
                                // 添加移动时的点坐标
                                judge();
//                                float mxf = (float) MyUtils.div(endX, mWidth, 2);
//                                float myf = (float) MyUtils.div(endY, mHeight, 2);
//                                points.add(new PointF(mxf, myf));
//                                Log.e(TAG, "PeletteActivity.onTouch 693行:   --->>> " + mxf + "   " + myf);
                                points.add(new PointF(endX, endY));
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
                                float r = Math.abs(endX - startX) / 2;
                                drawCircle(startX, startY, r);
//                                drawOval(startX, startY, endX, endY);
                                break;
                        }
                        imageView.setImageBitmap(baseBmp);
                        break;
                    case MotionEvent.ACTION_UP:
                        switch (paintMode) {
                            case MODE_TEXT:
                                DrawText(endX, endY);
                                break;
                            case MODE_LINE:
                                path.lineTo(endX, endY);
                                clearTempCanvas();
                                canvas.drawPath(path, paint);
                                break;
                            case MODE_CIRCLE:
                                float distance = Math.abs(endX - startX) / 2;
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
                            case MODE_PAINT:
                                break;
                        }
                        if (paintMode != MODE_TEXT) {
                            LocalPathList.add(drawPath);
                        }
                        clearTempCanvas();
                        imageView.setImageBitmap(baseBmp);
                        judge();
                        //只有在同屏状态中才发送
                        if (isSharing) {
                            if (localOperids == null) {
                                //用来保存本机的操作ID
                                localOperids = new ArrayList<>();
                            }
                            List<Float> allpt = new ArrayList<Float>();

                            if (sx > endX || sy > endY) {
                                float a = sx;
                                sx = endX;
                                endX = a;
                                float b = sy;
                                sy = endY;
                                endY = b;
                            }
                            allpt.add(sx);
                            allpt.add(sy);
                            allpt.add(endX);
                            allpt.add(endY);
                            Log.e(TAG, "PeletteActivity.onTouch 864行:   --->>> " + sx + "  " + sy + "  " + endX + "  " + endY);
                            switch (paintMode) {
                                // 发送添加矩形、直线、圆形方法
                                case MODE_SQUARE:
                                    addDrawShape(InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_RECTANGLE.getNumber(), allpt);
                                    break;
                                case MODE_LINE:
                                    addDrawShape(InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_LINE.getNumber(), allpt);
                                    break;
                                case MODE_CIRCLE:
                                    addDrawShape(InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_ELLIPSE.getNumber(), allpt);
                                    break;
                                case MODE_PAINT:
                                    addInk(points);
                                    points.clear();
                                    break;
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }


    private void addInk(List<PointF> allpt) {
        long srcwbid = mSrcwbid;
        long utcstamp = System.currentTimeMillis();
        int operid = (int) (utcstamp / 10);
        localOperids.add(operid);
        int opermemberid = NativeService.localMemberId;
        int srcmemid = launchPersonId;
        int figuretype = InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_INK.getNumber();
        Log.e(TAG, "PeletteActivity.addInk :  发送墨迹 --> operid:" + operid + ",opermemberid:" + opermemberid
                + ",srcmemid:" + srcmemid + ",srcwbid:" + srcwbid + ",utcstamp:" + utcstamp + ",figuretype:" + figuretype);
        nativeUtil.addInk(operid, opermemberid, srcmemid, srcwbid, utcstamp, figuretype, paintWidth, paintColor, allpt);
    }


    //添加圆形、矩形、直线
    private void addDrawShape(int type, List<Float> allpt) {
        long utcstamp = System.currentTimeMillis();
        int operid = (int) (utcstamp / 10);
        localOperids.add(operid);
        int opermemberid = NativeService.localMemberId;
        int srcmemid = launchPersonId;
        for (int i = 0; i < allpt.size(); i++) {
            Log.e(TAG, "PeletteActivity.addDrawShape 853行:   --->>> " + allpt.get(i));
        }
        Log.e(TAG, "PeletteActivity.addDrawShape 664行:   --->>> 发起人的人员ID：" + launchPersonId + "  自身的人员ID " + NativeService.localMemberId + "  发起人的白板标识： " + mSrcwbid);
        nativeUtil.addDrawFigure(operid, opermemberid, srcmemid,/*:发起人的人员ID*/
                mSrcwbid, utcstamp, type, paintWidth, paintColor, allpt);
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


    /**
     * 收到同意加入操作
     *
     * @param message
     */
    private void agreedJoin(EventMessage message) {
        if (sharedMembers == null && launchPersonId == NativeService.localMemberId) {//如果是本人发起
            sharedMembers = new ArrayList<>();
        }
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper object3 = (InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper) message.getObject();
        if (launchPersonId == NativeService.localMemberId) {//确定是本人发起的共享
            sharedMembers.add(object3);
        }
        Log.e(TAG, "PeletteActivity.agreedJoin 602行:   --->>> 同意加入通知EventBus");
//        beforeList = pathList;//保存加入共享前绘制的信息
//        clearCanvas();//清空画布
        whoToast(object3.getOpermemberid(), " 加入了共享");
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
        Log.e(TAG, "PeletteActivity.receiveOpenWhiteBoard 612行:   --->>> 收到白板打开操作EventBus");
        int operflag = object1.getOperflag();
        ByteString medianame = object1.getMedianame();
        int opermemberid = object1.getOpermemberid();
        int srcmemid = object1.getSrcmemid();
        long srcwbidd = object1.getSrcwbid();
        if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_FORCEOPEN.getNumber()) {
            Log.e(TAG, "PeletteActivity.receiveOpenWhiteBoard 619行:   --->>> 这是强制式打开白板");
            //强制打开白板  直接强制同意加入
            nativeUtil.agreeJoin(opermemberid, srcmemid, srcwbidd);
            isSharing = true;//如果同意加入就设置已经在共享中
            mSrcwbid = srcwbidd;//发起人的白板标识
            launchPersonId = srcmemid;//设置发起的人员ID
        } else if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber()) {
            Log.e(TAG, "PeletteActivity.receiveOpenWhiteBoard 625行:   --->>> 这是询问式打开白板");
            //询问打开白板
            WhetherOpen(opermemberid, srcmemid, srcwbidd, MyUtils.getBts(medianame));
        }
    }

    private float[] getFloats(List<Float> ptList) {
        float[] allPoint = new float[4];
        for (int i = 0; i < ptList.size(); i++) {
            Float aFloat = ptList.get(i);
            switch (i) {
                case 0:
                    allPoint[0] = aFloat;
                    break;
                case 1:
                    allPoint[1] = aFloat;
                    break;
                case 2:
                    allPoint[2] = aFloat;
                    break;
                case 3:
                    allPoint[3] = aFloat;
                    break;
            }
        }
        return allPoint;
    }

    @NonNull
    private Paint getNewPaint(int linesize, int color) {
        Paint newPaint = new Paint();
        newPaint.setColor(color);
        newPaint.setStrokeWidth(linesize);
        newPaint.setStyle(Paint.Style.STROKE);// 画笔样式：实线
        PorterDuffXfermode mode2 = new PorterDuffXfermode(
                PorterDuff.Mode.DST_OVER);
        newPaint.setXfermode(null);// 转换模式
        newPaint.setAntiAlias(true);// 抗锯齿
        newPaint.setDither(true);// 防抖动
        newPaint.setStrokeJoin(Paint.Join.ROUND);// 设置线段连接处的样式为圆弧连接
        newPaint.setStrokeCap(Paint.Cap.ROUND);// 设置两端的线帽为圆的
        return newPaint;
    }

    //是否同意加入弹出框
    private void WhetherOpen(final int opermemberid, final int srcmemid, final long srcwbidd, String medianame) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("是否同意加入 " + medianame + " 发起的共享批注？");
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //同意加入
                Log.e(TAG, "PeletteActivity.onClick 700行:   --->>> " + opermemberid + " : " + NativeService.localMemberId);
                nativeUtil.agreeJoin(NativeService.localMemberId, srcmemid, srcwbidd);
                isSharing = true;//如果同意加入就设置已经在共享中
                launchPersonId = srcmemid;//设置发起的人员ID
                mSrcwbid = srcwbidd;
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nativeUtil.rejectJoin(NativeService.localMemberId, srcmemid, srcwbidd);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MeetingActivity.PaletteIsShowing = false;
        Log.e("MyLife", "onDestroy :   --->>> ");
        if (isSharing) {
            stopShare();
        }
    }

    @Override
    protected void onResume() {
        Log.i("A_life", "PeletteActivity.onResume :   --->>> " + getTaskId());
        super.onResume();
    }

    /**
     * 绘制文本
     *
     * @param x
     * @param y
     */
    private void DrawText(final float x, final float y) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText drawEdt = new EditText(context);
        float ex = x;
        float ey = y;
        int size;
        //控制文本的大小（不能太小）
        if (paintWidth < 30) {
            size = 30;
        } else {
            size = paintWidth;
        }
        //控制文本完全显示出来（小于某个值顶部会隐藏）
        if (y < 50) {
            if (size == 30) {
                ey = 30;
            } else if (size > 30 && size < 60) {
                ey = 50;
            } else if (size > 60) {
                ey = 80;
            }
        }
        builder.setView(drawEdt);
        final int finalSize = size;
        final float fx = ex;
        final float fy = ey;
        //当点击Dialog外部时/隐藏时调用
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                drawText = drawEdt.getText().toString();
                if (!drawText.equals("")) {
                    //5689
                    paint.setTextSize(finalSize);
                    paint.setStyle(Paint.Style.FILL);
                    Rect rect = new Rect();
                    paint.getTextBounds(drawText, 0, drawText.length(), rect);
                    int width = rect.width();//输入的所有文本的宽度
                    int height = rect.height();//文本的高度（用于换行显示）
                    float remainWidth = mWidth - fx;//可容许显示文本的宽度
                    Log.e(TAG, "PeletteActivity.DrawText 935行:   --->>> 字符串的宽度： " + width + "  可容许显示的宽度："
                            + remainWidth + "  字符串的高度：" + height + "  输入的文本：" + drawText + "  字符串的个数：" + drawText.length());
                    int ilka = width / drawText.length();//每个文本的宽度
                    int canSee = (int) (remainWidth / ilka);//可以显示的文本个数
                    if (remainWidth < width) {// 小于所有文本的宽度（不够显示）
                        funDraw(height, canSee - 1, fx, fy, drawText);
                    } else {
                        canvas.drawText(drawText, fx, fy, paint);
                    }

                    // 将绘制的信息保存到本机绘制列表中
                    DrawPath drawPath = new DrawPath();
                    drawPath.paint = paint;
                    drawPath.text = drawText;
                    drawPath.pointF = new PointF(fx, fy);
                    drawPath.height = height;
                    drawPath.srcmemid = launchPersonId;
                    drawPath.lw = (int) remainWidth;
                    drawPath.rw = width;
                    drawPath.cansee = canSee;
                    LocalPathList.add(drawPath);
                    imageView.setImageBitmap(baseBmp);
                    if (isSharing) {//如果当前在共享中，则发送添加字体
                        addText(x, y, finalSize);
                    }
                }
            }
        });
        builder.create().show();
    }

    private void addText(float x, float y, int finalSize) {
        long time = System.currentTimeMillis();
        int operid = (int) (time / 10);
//        double divX = MyUtils.div(x, mWidth, 2);
//        double divY = MyUtils.div(y, mHeight, 2);
        nativeUtil.addText(operid, NativeService.localMemberId, launchPersonId, mSrcwbid,
                time, InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_FREETEXT.getNumber(),
                finalSize, 2, paintColor, "宋体", x, y, drawText);
    }

    private void funDraw(float height, int canSee, float fx, float fy, String text) {
        if (text.length() > canSee) {
            String canSeeText = text.substring(0, canSee);
            canvas.drawText(canSeeText, fx, fy, paint);
            String substring = text.substring(canSee, text.length());//获得剩下无法显示的文本
            if (substring.length() > 0) {
                funDraw(height, canSee, fx, fy + height, substring);
            }
        } else {
            canvas.drawText(text, fx, fy, paint);
        }
    }

    /**
     * 在临时画布上绘制橡皮擦图片
     */
    private void drawEraser(float x, float y) {
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
    private void drawLine(float startX, float startY, float endX, float endY) {
        clearTempCanvas();
        tempCanvas.drawLine(startX, startY, endX, endY, paint);
    }

    /**
     * 在临时画布上绘制方框
     */
    private void drawSquare(float startX, float startY, float endX, float endY) {
        clearTempCanvas();
        tempCanvas.drawRect(startX, startY, endX, endY, paint);
    }

    /**
     * 在临时画布上绘制圆
     */
    private void drawCircle(float cx, float cy, float radius) {
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

    public void setImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.e(TAG, "PeletteActivity.setImage :  没使用前宽高 --->>> " + width + "," + height);
        /** **** **    ** **** **/
        float cw = 1;
        float ch = 1;
        float newWidth = mWidth / 2 + (mWidth / 3);
        float newHeight = mHeight / 2 + (mHeight / 3);
        if (width > mWidth && height > mHeight) {
            cw = newWidth / width;
            ch = newHeight / height;
        } else if (width > mWidth) {
            cw = newWidth / width;
            ch = cw;
        } else if (height > mHeight) {
            ch = newHeight / height;
            cw = ch;
        }
//        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(cw, ch);
        // 得到新的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        /** **** **    ** **** **/
        /** **** **    ** **** **/
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        Log.e(TAG, "PeletteActivity.setImage:  使用后宽高 --->>> " + width + "," + height);
        /** **** **  居中显示：屏幕宽高的一半、减去图片宽高的各一半  ** **** **/
        Log.e(TAG, "PeletteActivity.setImage:   --->>>画板的宽 mWidth:" + mWidth + " 高 mHeight:" + mHeight);
        int x = (mWidth / 2) - (width / 2);
        int y = (mHeight / 2) - (height / 2);
        Log.e(TAG, "PeletteActivity.setImage:  图片最终的左上角坐标 --->>> " + x + "," + y);
        Rect rect1 = new Rect(0, 0, width, height);
        Rect rect2 = new Rect(x, y, x + width, y + height);
        canvas.drawBitmap(bitmap, rect1, rect2, new Paint());
    }

    public class DrawPath {
        public Paint paint; //画笔
        public Path path = null; //路径
        public int operid;//操作ID
        public int opermemberid;//当前该命令的人员ID
        public long srcwbid;//发起人的白板标识
        public int srcmemid;//发起人的人员ID

        public String text = null;//绘制的文本
        public PointF pointF;//添加文本的起点（x,y）
        public int height;//文本的高度（每个字的高度）
        public int cansee;//可以显示的文本的个数
        public int lw;//可容许显示文本的区域宽度
        public int rw;//所有文本的宽度
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
                paint = getNewPaint(paintWidth, paintColor);
//                paint.setStyle(Paint.Style.STROKE);// 画笔样式：实线
//                paint.setStrokeWidth(paintWidth);// 线宽
//                paint.setColor(paintColor);// 颜色
//                PorterDuffXfermode mode2 = new PorterDuffXfermode(
//                        PorterDuff.Mode.DST_OVER);
//                paint.setXfermode(null);// 转换模式
//                paint.setAntiAlias(true);// 抗锯齿
//                paint.setDither(true);// 防抖动
//                paint.setStrokeJoin(Paint.Join.ROUND);// 设置线段连接处的样式为圆弧连接
//                paint.setStrokeCap(Paint.Cap.ROUND);// 设置两端的线帽为圆的
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
        //撤销时要保证有东西可撤销
        if (LocalPathList != null && LocalPathList.size() > 0) {
            clearCanvas();
            //删除最后一个DrawPath对象
            LocalPathList.remove(LocalPathList.size() - 1);
            //删除最后得一个操作，再从新绘制
            drawAgain(LocalPathList);
            //将其他人的绘制信息也重新绘制
            drawAgain(pathList);
        }
        //本地的路径撤销后，如果在共享中则需要发送撤销通知
        if (isSharing && localOperids.size() > 0) {//如果是在共享状态中
            long srcwbid = mSrcwbid;
            long utcstamp = System.currentTimeMillis();
            //操作ID是 之前的操作ID
            int operid = localOperids.get(localOperids.size() - 1);
            //已经撤销后就要删除最后一个
            localOperids.remove(localOperids.size() - 1);
            int opermemberid = NativeService.localMemberId;
            int srcmemid = launchPersonId;
            int figuretype = InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_ZERO.getNumber();
            Log.e(TAG, "PeletteActivity.undo :  发送撤销通知其他人撤销 --> operid:" + operid + ",opermemberid：" + opermemberid
                    + ",srcmemid:" + srcmemid + ",srcwbid:" + srcwbid + ",utcstamp:" + utcstamp + ",figuretype:" + figuretype);
            nativeUtil.whiteBoardDeleteRecord(opermemberid, operid, opermemberid, srcmemid, srcwbid, utcstamp, figuretype);
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
//        BgImageView = (ImageView) findViewById(R.id.whiteboard_background);

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

    private void MemberHolder(final MemberViewHolder holder) {
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
                showChooseOnLineMember();
                mMemberPop.dismiss();
            }
        });
        //确定
        holder.board_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.all_mer_cb.isChecked()) {
                    for (int i = 0; i < boardCheck.size(); i++) {
                        boardCheck.set(i, true);
                    }
                }
                ArrayList<Integer> devIds = new ArrayList<Integer>();
                if (onLineBoardMemberAdapter != null) {
                    List<DevMember> checkedIds = onLineBoardMemberAdapter.getCheckedIds();
                    for (int i = 0; i < checkedIds.size(); i++) {
                        devIds.add(checkedIds.get(i).getMemberInfos().getPersonid());
//                        devIds.add(checkedIds.get(i).getDevId());
                    }
                }
                if (devIds.size() > 0) {
                    // 发起共享批注  强制：Pb_MEETPOTIL_FLAG_FORCEOPEN  Pb_MEETPOTIL_FLAG_REQUESTOPEN
                    if (mSrcwbid == 0) {
                        mSrcwbid = System.currentTimeMillis();
                    }
                    Log.e(TAG, "PeletteActivity.onClick 1260行:  发起时的白板标识 --->>> " + mSrcwbid);
                    nativeUtil.coerceStartWhiteBoard(InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber(),
                            MyUtils.getBts(MainActivity.getLocalInfo().getMembername()), NativeService.localMemberId,
                            NativeService.localMemberId, mSrcwbid, devIds);
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
    private void showChooseOnLineMember() {
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
                ArrayList<Integer> devIds = new ArrayList<Integer>();
                if (onLineBoardMemberAdapter != null) {
                    List<DevMember> checkedIds = onLineBoardMemberAdapter.getCheckedIds();
                    for (int i = 0; i < checkedIds.size(); i++) {
                        devIds.add(checkedIds.get(i).getMemberInfos().getPersonid());
//                        devIds.add(checkedIds.get(i).getDevId());
                    }
                }
                if (devIds.size() > 0) {
                    // 发起共享批注  强制：Pb_MEETPOTIL_FLAG_FORCEOPEN  Pb_MEETPOTIL_FLAG_REQUESTOPEN
                    if (mSrcwbid == 0) {
                        mSrcwbid = System.currentTimeMillis();
                    }
                    Log.e(TAG, "PeletteActivity.onClick 1260行:  发起时的白板标识 --->>> " + mSrcwbid);
                    nativeUtil.coerceStartWhiteBoard(InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber(),
                            MyUtils.getBts(MainActivity.getLocalInfo().getMembername()), NativeService.localMemberId,
                            NativeService.localMemberId, mSrcwbid, devIds);
                }
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
        super.onActivityResult(requestCode, resultCode, data);//super方法要开启,因为录屏在基类中做的处理
        if (requestCode == IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            // 获取选中文件的uri
            Uri uri = data.getData();
            String realPath = getRealPathFromURI(uri);
            Log.e(TAG, "PeletteActivity.onActivityResult :  选中的文件路径 --->>> " + realPath);
            if (realPath == null) {
                Toast.makeText(context, "获取该文件的路径失败", Toast.LENGTH_LONG).show();
            } else {
                // TODO: 2018/4/25 进行缩放绘制到画板正中心处
                // 执行操作
                dstbmp = BitmapFactory.decodeFile(realPath);
                int width = dstbmp.getWidth();
                int height = dstbmp.getHeight();
                /** **** **    ** **** **/
                float cw = 1;
                float ch = 1;
                float newWidth = mWidth / 2 + (mWidth / 3);
                float newHeight = mHeight / 2 + (mHeight / 3);
                if (width > mWidth && height > mHeight) {
                    Log.e(TAG, "PeletteActivity.onActivityResult 1809行:  1111 --->>> ");
                    cw = newWidth / width;
                    ch = newHeight / height;
                } else if (width > mWidth) {
                    Log.e(TAG, "PeletteActivity.onActivityResult 1830行:  22222 --->>> ");
                    cw = newWidth / width;
                    ch = cw;
                } else if (height > mHeight) {
                    Log.e(TAG, "PeletteActivity.onActivityResult 1834行:  33333333 --->>> ");
                    ch = newHeight / height;
                    cw = ch;
                }
                // 取得想要缩放的matrix参数
                Matrix matrix = new Matrix();
                matrix.postScale(cw, ch);
                // 得到新的图片
                dstbmp = Bitmap.createBitmap(dstbmp, 0, 0, width, height, matrix, true);
                width = dstbmp.getWidth();
                height = dstbmp.getHeight();
                int x = (mWidth / 2) - (width / 2);
                int y = (mHeight / 2) - (height / 2);
                Rect rect1 = new Rect(0, 0, width, height);
                Rect rect2 = new Rect(x, y, x + width, y + height);
                canvas.drawBitmap(dstbmp, rect1, rect2, new Paint());
                imageView.setImageBitmap(baseBmp);
                if (isSharing) {
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        dstbmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] bytes = baos.toByteArray();
                        InputStream is = new ByteArrayInputStream(bytes);
                        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                        byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据
                        int rc = 0;
                        while ((rc = is.read(buff, 0, 100)) > 0) {
                            swapStream.write(buff, 0, rc);
                        }
                        byte[] bytes1 = swapStream.toByteArray(); //in_b为转换之后的结果
                        ByteString picdata = ByteString.copyFrom(bytes1);
                        /** ************ ******    ****** ************ **/
                        long time = System.currentTimeMillis();
                        int operid = (int) (time / 10);
                        nativeUtil.addPicture(operid, NativeService.localMemberId, launchPersonId, mSrcwbid, time,
                                InterfaceMacro.Pb_MeetPostilFigureType.Pb_WB_FIGURETYPE_PICTURE.getNumber(), 0, 0, picdata);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
