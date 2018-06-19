package com.pa.paperless.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfacePlaymedia;
import com.mogujie.tt.protobuf.InterfaceStream;
import com.pa.paperless.R;
import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.activity.NoteActivity;
import com.pa.paperless.activity.PeletteActivity;
import com.pa.paperless.adapter.CanJoinProAdapter;
import com.pa.paperless.adapter.OnLineProjectorAdapter;
import com.pa.paperless.adapter.ScreenControlAdapter;
import com.pa.paperless.adapter.setadapter.CanJoinMemberAdapter;
import com.pa.paperless.bean.DevMember;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.bean.VideoInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.MyUtils;
import com.pa.paperless.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.libsdl.app.SDLActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.pa.paperless.activity.MeetingActivity.DevMeetInfo;
import static com.pa.paperless.service.NativeService.nativeUtil;
import static com.pa.paperless.utils.MyUtils.getMediaid;

/**
 * Created by Administrator on 2018/5/31.
 * 弹窗
 */

public class FabService extends Service implements View.OnTouchListener {
    private final String TAG = "Fabservice-->";
    private WindowManager wm;
    private ImageView mImageView;
    private long downTime, upTime;
    private int mTouchStartX, mTouchStartY;
    private WindowManager.LayoutParams mParams, params, postilParams, notParams;
    private List<DevMember> onLineMembers;
    private List<DeviceInfo> allProjectors, onLineProjectors;
    public static List<Boolean> checks, checkProOL, checkProAll;
    public static OnLineProjectorAdapter allProjectorAdapter, onLineProjectorAdapter;
    public static ScreenControlAdapter onLineMemberAdapter;
    private ArrayList<Integer> sameMemberDevRrsIds, sameMemberDevIds, applyProjectionIds, informDev, onlineClientIds;
    private List<VideoInfo> videoInfos;
    private boolean fromVideo, openProjector;
    private View pop, screenPop, choosePlayerPop, projectPop, proListPop, callServePop, postilPop, edtPop, canJoinPop;
    private boolean clickScreen;//是否点击了同屏控制  预防后台数据自动更新时自动打开多次
    private boolean clickProject;//是否点击了投影控制  预防后台数据自动更新时自动打开多次
    private int MSG_TYPE;
    private boolean openProjectpopFromPostilPop, openScreenpopFromPostilpop;//是否是从批注页面打开投影或者同屏
    private boolean SDLIsShow;
    private Handler handler = new Handler();
    private int windowWidth;
    private int windowHeight;
    private DisplayMetrics metrics;
    private int mScreenDensity;
    private ImageReader mImageReader;
    private Intent mResultData;
    private int mResultCode;
    private MediaProjectionManager mMediaProjectionManager1;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private long tempImgName;
    private FabService fabContext;
    public static byte[] bytes;
    public static boolean isBusy;
    private List<DeviceInfo> deviceInfos;
    private List<MemberInfo> memberInfos;
    private List<DevMember> canJoinMember;//存放可加入同屏的参会人
    private List<DeviceInfo> canJoinPro;//存放可加入同屏的投影机
    public static CanJoinProAdapter canJoinProAdapter;
    public static CanJoinMemberAdapter canJoinMemberAdapter;
    public static List<Boolean> canjoinProSelect, canjoinMemberSelect;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(final EventMessage message) {
        if (message.getAction() == 0 && message.getType() == 0) {
            SDLIsShow = false;
        } else if (message.getAction() == 1 && message.getType() == 1) {
            SDLIsShow = true;
        }
        switch (message.getAction()) {
            case IDEventF.member_info://收到参会人信息
                receiveMemberInfo(message);
                break;
            case IDEventF.dev_info://收到设备信息
                receiveDevInfo(message);
                break;
            case IDEventMessage.open_screenspop://从视屏直播页面收到打开同屏控制
                openScreen(message);
                break;
            case IDEventMessage.open_projector://从视屏直播页面收到打开投影控制
                openProjector(message);
                break;
            case IDEventMessage.MEDIA_PLAY_INFORM://媒体播放通知
                receiveMediaPlayInform(message);
                break;
            case IDEventMessage.PLAY_STREAM_NOTIFY://收到流播放通知
                receivePlayStreamInform(message);
                break;
            case IDEventF.take_photo://获得拍摄的照片
                Log.e(TAG, "FabService.getEventMessage :  收到图片通知 --> ");
                Bitmap bitmap = (Bitmap) message.getObject();
                bytes = FileUtil.Bitmap2bytes(bitmap);
                showPostilPop(bytes);
                showPop(mImageView, postilPop, postilParams);
                break;
            case IDEventF.can_join://收到可加入同屏数据
                receiveCanJoinInfo(message);
                break;
            case IDEventF.all_stream_play://得到所有流播放信息
                receiveAllStreamInfo(message);
                break;
        }
    }

    private void openProjector(EventMessage message) {
        if (MeetingActivity.localPermissions != null) {
            if (MeetingActivity.localPermissions.contains(2)) {
                Log.e(TAG, "FabService.getEventMessage :  从视屏直播页面收到打开投影控制 --> ");
                openProjector = true;
                videoInfos = (List<VideoInfo>) message.getObject();
                clickProject = false;//设置不是点击弹窗触发
                showPop(mImageView, projectPop);
            } else {
                Toast.makeText(fabContext, "您没有打开投影的权限", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "FabService.getEventMessage :  您没有打开投影的权限 --> ");
            }
        } else {
            Log.e(TAG, "FabService.getEventMessage :  权限集合为空 --> ");
        }
    }

    private void openScreen(EventMessage message) {
        if (MeetingActivity.localPermissions != null) {
            if (MeetingActivity.localPermissions.contains(1)) {
                Log.e(TAG, "FabService.getEventMessage :  从视屏直播页面收到打开同屏控制 --> ");
                fromVideo = true;
                videoInfos = (List<VideoInfo>) message.getObject();
                clickScreen = false;//设置不是点击弹窗触发
                showPop(mImageView, screenPop);
            } else {
                Toast.makeText(fabContext, "您没有同屏的权限", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "FabService.getEventMessage :  您没有同屏的权限 --> ");
            }
        } else {
            Log.e(TAG, "FabService.getEventMessage :  本机权限集合为空 --> ");
        }
    }

    private void receiveMediaPlayInform(final EventMessage message) {
        Log.e(TAG, "FabService.getEventMessage :  媒体播放通知EventBus --> ");
        if (!SDLIsShow) {
            InterfacePlaymedia.pbui_Type_MeetMediaPlay data = (InterfacePlaymedia.pbui_Type_MeetMediaPlay) message.getObject();
            startActivity(new Intent(FabService.this, SDLActivity.class)
                    .setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    .putExtra("action", IDEventMessage.MEDIA_PLAY_INFORM)
                    .putExtra("data", data.toByteArray()));
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InterfacePlaymedia.pbui_Type_MeetMediaPlay data = (InterfacePlaymedia.pbui_Type_MeetMediaPlay) message.getObject();
                    startActivity(new Intent(FabService.this, SDLActivity.class)
                            .setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            .putExtra("action", IDEventMessage.MEDIA_PLAY_INFORM)
                            .putExtra("data", data.toByteArray()));
                }
            }, 600);
        }
    }

    private void receivePlayStreamInform(final EventMessage message) {
        Log.e(TAG, "FabService.getEventMessage :  流播放通知EventBus --> ");
        if (!SDLIsShow) {
            InterfaceStream.pbui_Type_MeetStreamPlay data = (InterfaceStream.pbui_Type_MeetStreamPlay) message.getObject();
            startActivity(new Intent(FabService.this, SDLActivity.class)
                    .setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    .putExtra("action", IDEventMessage.PLAY_STREAM_NOTIFY)
                    .putExtra("data", data.toByteArray()));
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        InterfacePlaymedia.pbui_Type_MeetMediaPlay data = (InterfacePlaymedia.pbui_Type_MeetMediaPlay) message.getObject();
                        startActivity(new Intent(FabService.this, SDLActivity.class)
                                .setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                .putExtra("action", IDEventMessage.MEDIA_PLAY_INFORM)
                                .putExtra("data", data.toByteArray()));
                    } catch (Exception e) {
                        Toast.makeText(FabService.this, "操作失败：类型装换问题", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "FabService.run :  异常信息： --> " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }, 600);
        }
    }

    private void receiveCanJoinInfo(EventMessage message) {
        Log.e(TAG, "FabService.getEventMessage :  收到可加入同屏数据 --> ");
        InterfaceDevice.pbui_Type_DeviceResPlay object = (InterfaceDevice.pbui_Type_DeviceResPlay) message.getObject();
        List<InterfaceDevice.pbui_Item_DeviceResPlay> pdevList = object.getPdevList();
        if (canJoinMember == null) {
            canJoinMember = new ArrayList<>();
        } else {
            canJoinMember.clear();
        }
        if (canJoinPro == null) {
            canJoinPro = new ArrayList<>();
        } else {
            canJoinPro.clear();
        }
        for (int i = 0; i < pdevList.size(); i++) {
            InterfaceDevice.pbui_Item_DeviceResPlay item = pdevList.get(i);
            int devceid = item.getDevceid();//设备ID
            int n = devceid & Macro.DEVICE_MEET_PROJECTIVE;
            if (n == Macro.DEVICE_MEET_PROJECTIVE) {// 可加入同屏的投影机
                for (int j = 0; j < deviceInfos.size(); j++) {
                    if (deviceInfos.get(j).getDevId() == devceid) {
                        //匹配添加
                        canJoinPro.add(deviceInfos.get(j));
                    }
                }
            } else {// 可加入同屏的参会人
                int memberid = item.getMemberid();//参会人员ID
                String name = MyUtils.getBts(item.getName());//参会人员名称
                Log.e(TAG, "FabService.getEventMessage :  devceid --> " + devceid + "   memberid:" + memberid + "   name:" + name);
                for (int j = 0; j < memberInfos.size(); j++) {
                    if (memberInfos.get(j).getPersonid() == memberid) {
                        canJoinMember.add(new DevMember(memberInfos.get(j), devceid));
                    }
                }
            }
            /** **** **  初始化可加入同屏的投影机  ** **** **/
            if (canJoinPro.size() > 0) {
                if (canjoinProSelect == null) {
                    canjoinProSelect = new ArrayList<>();
                } else {
                    canjoinProSelect.clear();
                }
                for (int j = 0; j < canJoinPro.size(); j++) {
                    canjoinProSelect.add(false);
                }
            }
            if (canJoinProAdapter == null) {
                canJoinProAdapter = new CanJoinProAdapter(canJoinPro);
            } else {
                canJoinProAdapter.notifyDataSetChanged();
            }
            /** **** **  初始化可加入同屏的参会人  ** **** **/
            if (canJoinMember.size() > 0) {
                if (canjoinMemberSelect == null) {
                    canjoinMemberSelect = new ArrayList<>();
                } else {
                    canjoinMemberSelect.clear();
                }
                for (int j = 0; j < canJoinMember.size(); j++) {
                    canjoinMemberSelect.add(false);
                }
            }
            if (canJoinMemberAdapter == null) {
                canJoinMemberAdapter = new CanJoinMemberAdapter(canJoinMember);
            } else {
                canJoinMemberAdapter.notifyDataSetChanged();
            }
            showJoinPop();
        }
    }

    private void receiveAllStreamInfo(EventMessage message) {
        InterfaceStream.pbui_Type_MeetStreamPlayDetailInfo object1 = (InterfaceStream.pbui_Type_MeetStreamPlayDetailInfo) message.getObject();
        if (object1 != null) {
            int playID = 0;
            //获取选择播放的参会人或则投影机，只能选择一个
            List<DeviceInfo> checkedIds = canJoinProAdapter.getCheckedIds();
            List<DevMember> checkedIds1 = canJoinMemberAdapter.getCheckedIds();
            if (checkedIds.size() == 1) {
                playID = checkedIds.get(0).getDevId();
            }
            if (checkedIds1.size() == 1) {
                playID = checkedIds1.get(0).getDevId();
            }
            List<InterfaceStream.pbui_Item_MeetStreamPlayDetailInfo> itemList = object1.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceStream.pbui_Item_MeetStreamPlayDetailInfo item = itemList.get(i);
                if (item.getDeviceid() == playID) {//进行匹配要播放的所在流的设备ID
                    List<Integer> res = new ArrayList<>();
                    List<Integer> ids = new ArrayList<>();
                    res.add(0);
                    ids.add(NativeService.localDevId);
                    /** **** **  进行流播放  ** **** **/
                    nativeUtil.streamPlay(playID, item.getSubid(), 0, res, ids);
                }
            }
        }
    }

    private void receiveDevInfo(EventMessage message) {
        Log.e(TAG, "FabService.receiveDevInfo :  收到设备信息 --> ");
        InterfaceDevice.pbui_Type_DeviceDetailInfo o = (InterfaceDevice.pbui_Type_DeviceDetailInfo) message.getObject();
        deviceInfos = Dispose.DevInfo(o);
        int number = Macro.DEVICE_MEET_PROJECTIVE;
        if (onLineMembers == null) {
            onLineMembers = new ArrayList<>();
        } else {
            onLineMembers.clear();
        }
        if (onLineProjectors == null) {
            onLineProjectors = new ArrayList<>();
        } else {
            onLineProjectors.clear();
        }
        if (allProjectors == null) {
            allProjectors = new ArrayList<>();
        } else {
            allProjectors.clear();
        }
        if (onlineClientIds == null) {//存放在线状态客户端ID
            onlineClientIds = new ArrayList<>();
        } else {
            onlineClientIds.clear();
        }
        for (int i = 0; i < deviceInfos.size(); i++) {
            DeviceInfo deviceInfo = deviceInfos.get(i);
            int netState = deviceInfo.getNetState();
            int faceState = deviceInfo.getFaceState();
            int devId = deviceInfo.getDevId();
            int memberId = deviceInfo.getMemberId();
            int i1 = devId & number;
            //判断是否是投影机
            if (i1 == number) {
                // 添加所有投影机
                allProjectors.add(deviceInfo);
                //判断是否是在线状态
                if (netState == 1) {
                    //说明是在线状态的投影机
                    onLineProjectors.add(deviceInfo);
                }
            }
            //判断是否是在线的并且界面状态为1的参会人
            if (faceState == 1 && memberInfos != null && netState == 1) {
                for (int j = 0; j < memberInfos.size(); j++) {
                    MemberInfo memberInfo = memberInfos.get(j);
                    int personid = memberInfo.getPersonid();
                    if (personid == memberId) {
                        /** **** **  过滤掉自己的设备  ** **** **/
                        if (devId != DevMeetInfo.getDeviceid()) {
                            //查找到在线状态的参会人员
                            onLineMembers.add(new DevMember(memberInfos.get(j), devId));
                        }
                    }
                }
            }
            if ((devId & Macro.DEVICE_MEET_CLIENT) == Macro.DEVICE_MEET_CLIENT) {//客户端
//                if (netState == 1 && devId != MeetingActivity.getDevId()) {
                if (netState == 1 && devId != NativeService.localDevId) {
                    onlineClientIds.add(devId);//添加在线客户端
                }
            }
        }
        //初始化投影机是否选中集合
        checkProAll = new ArrayList<>();
        //初始化 全部设为false
        for (int k = 0; k < allProjectors.size(); k++) {
            checkProAll.add(false);
        }
        //初始化同屏控制参会人是否选中集合
        checks = new ArrayList<>();
        //初始化 全部设为false
        for (int k = 0; k < onLineMembers.size(); k++) {
            checks.add(false);
        }
        //初始化同屏控制投影机选择集合
        checkProOL = new ArrayList<>();
        for (int i = 0; i < onLineProjectors.size(); i++) {
            checkProOL.add(false);
        }
        if (allProjectorAdapter == null) {
            allProjectorAdapter = new OnLineProjectorAdapter(allProjectors, 1);
        }
        if (onLineProjectorAdapter == null) {
            onLineProjectorAdapter = new OnLineProjectorAdapter(onLineProjectors, 0);
        }
        if (onLineMemberAdapter == null) {
            onLineMemberAdapter = new ScreenControlAdapter(onLineMembers);
        }
        /** **** **  刷新Adapter  ** **** **/
        allProjectorAdapter.notifyDataSetChanged();
        onLineProjectorAdapter.notifyDataSetChanged();
        onLineMemberAdapter.notifyDataSetChanged();

        Log.e(TAG, "FabService.getEventMessage :  收到参会人和投影机信息 --> 是否点击了同屏控制按钮" + clickScreen);
//        initData(message);
        if (clickScreen) {//判断是点击了同屏控制按钮才能打开
            if (MeetingActivity.localPermissions != null) {//本机权限集合
                if (MeetingActivity.localPermissions.contains(1)) {//有同屏权限
                    Log.e(TAG, "FabService.getEventMessage :  选中参会人集合 --> " + checks.toString());
                    Log.e(TAG, "FabService.getEventMessage :  点击后打开同屏控制 --> ");
                    clickScreen = false;
                    showPop(pop, screenPop);//打开同屏控制
                } else {
                    Toast.makeText(fabContext, "您没有权限这样做", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (clickProject) {
            if (MeetingActivity.localPermissions != null) {
                if (MeetingActivity.localPermissions.contains(2)) {//选中第二项，投影权限
                    Log.e(TAG, "FabService.getEventMessage :  点击后打开投影控制 --> ");
                    clickProject = false;
                    showPop(pop, projectPop);
                } else {
                    Toast.makeText(fabContext, "您没有权限这样做", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void receiveMemberInfo(EventMessage message) {
        Log.e(TAG, "FabService.receiveMemberInfo :  收到参会人信息 --> ");
        InterfaceMember.pbui_Type_MemberDetailInfo object = (InterfaceMember.pbui_Type_MemberDetailInfo) message.getObject();
        memberInfos = Dispose.MemberInfo(object);
        try {
            //查询设备信息
            nativeUtil.queryDeviceInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
    //起点
    private void showFab() {
        //获取 WindowManager
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowWidth = wm.getDefaultDisplay().getWidth();
        windowHeight = wm.getDefaultDisplay().getHeight();
        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2);
        //初始化悬浮按钮和弹出框布局
        initViews();
        // 初始化需要使用的Params
        initParams();
        wm.addView(mImageView, mParams);
    }

    public void startVirtual() {
        if (mMediaProjection != null) {
            Log.i(TAG, "want to display virtual");
            virtualDisplay();
        } else {
            Log.i(TAG, "start screen capture intent");
            Log.i(TAG, "want to build mediaprojection and display virtual");
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    private void startScreen() {
        Image image = mImageReader.acquireLatestImage();
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        image.close();
        Log.i(TAG, "image data captured");

        if (bitmap != null) {
            try {
                MyUtils.CreateFile(Macro.TAKE_PHOTO);
                File fileImage = new File(Macro.TAKE_PHOTO + tempImgName + ".jpg");
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                    Log.i(TAG, "image file created");
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(fileImage);
                    media.setData(contentUri);
//                    this.sendBroadcast(media);
                    Log.i(TAG, "screen image saved");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUpMediaProjection() {
        mResultData = ((ShotApplication) getApplication()).getIntent();
        mResultCode = ((ShotApplication) getApplication()).getResult();
        mMediaProjectionManager1 = ((ShotApplication) getApplication()).getMediaProjectionManager();
        mMediaProjection = mMediaProjectionManager1.getMediaProjection(mResultCode, mResultData);
        Log.i(TAG, "mMediaProjection defined");
    }

    private void virtualDisplay() {
        try {
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                    windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
            Log.i(TAG, "virtual displayed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化View
    private void initViews() {
        /** **** **  悬浮按钮  ** **** **/
        mImageView = new ImageView(this);
        mImageView.setImageResource(R.drawable.side);
        mImageView.setOnTouchListener(this);
        /** **** **  弹出框主页  ** **** **/
        pop = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_fab, null);
        FabViewHolder holder = new FabViewHolder(pop);
        holderEvent(holder);
        /** **** **  同屏控制  ** **** **/
        screenPop = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_sscreen, null);
        ScreenViewHolder screenHolder = new ScreenViewHolder(screenPop);
        Screen_Event(screenHolder);
        /** **** **  投影控制  ** **** **/
        projectPop = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_projector, null);
        ProjectViewHolder proHolder = new ProjectViewHolder(projectPop);
        proHolder_Event(proHolder);
    }

    // 初始化 WindowManager.LayoutParams
    private void initParams() {
        /** **** **  悬浮按钮  ** **** **/
        mParams = new WindowManager.LayoutParams();
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.width = 100;
        mParams.height = 100;
        mParams.x = 0;
        mParams.y = 100;
        mParams.windowAnimations = R.style.AnimHorizontal;
        /** **** **  弹框  ** **** **/
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//总是出现在应用程序窗口之上
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.CENTER;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        params.windowAnimations = R.style.AnimHorizontal;
        /** **** **  充满屏幕  ** **** **/
        postilParams = new WindowManager.LayoutParams();
        postilParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//总是出现在应用程序窗口之上
        postilParams.format = PixelFormat.RGBA_8888;
        postilParams.gravity = Gravity.CENTER;
        postilParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        postilParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
        postilParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
        postilParams.windowAnimations = R.style.AnimHorizontal;
        /** **** **  外部不可点击  ** **** **/
        notParams = new WindowManager.LayoutParams();
        notParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//总是出现在应用程序窗口之上
        notParams.format = PixelFormat.RGBA_8888;
        notParams.gravity = Gravity.CENTER;
        notParams.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        notParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        notParams.windowAnimations = R.style.AnimHorizontal;
    }

    private void showPostilPop(byte[] bytes) {
        /** **** **  批注图片页面  ** **** **/
        postilPop = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_postil, null);
        PostilViewHolder postilHolder = new PostilViewHolder(postilPop);
        postilHolder_Event(postilHolder, bytes);
    }

    //批注页面事件
    private void postilHolder_Event(final PostilViewHolder postilHolder, final byte[] bytes) {
        /** **** **  将截取的屏幕图片展示到ImageView  ** **** **/
        Glide.with(getApplicationContext()).load(bytes).into(postilHolder.postil_image);
//        postilHolder.postil_image.setImageBitmap(FileUtil.bytes2Bitmap(bytes));
        //保存本地
        postilHolder.postil_save_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePop(bytes, 1);
            }
        });
        //保存服务器
        postilHolder.postil_save_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePop(bytes, 2);
            }
        });
        //截图批注
        postilHolder.postil_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EventBus.getDefault().post(new EventMessage(IDEventF.screen_postil,bytes));
                Intent intent = new Intent(fabContext, PeletteActivity.class);
                Log.e(TAG, "FabService.onClick :  数组的大小bytes --> " + bytes.length);
                intent.putExtra("postilpic", "postilpic");
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                fabContext.startActivity(intent);
                showPop(postilPop, mImageView, mParams);
            }
        });
        //发起同屏
        postilHolder.postil_start_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPop(postilPop, screenPop, params);
                openScreenpopFromPostilpop = true;
                wm.addView(screenPop, notParams);
            }
        });
        //停止同屏
        postilHolder.postil_stop_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sameMemberDevRrsIds != null && sameMemberDevIds != null) {
                    /** ************ ******  停止资源操作  ****** ************ **/
                    for (int i = 0; i < sameMemberDevIds.size(); i++) {
                        Log.e(TAG, "停止同屏按钮 :  sameMemberDevIds --->>> " + sameMemberDevIds.get(i));
                    }
                    for (int i = 0; i < sameMemberDevRrsIds.size(); i++) {
                        Log.e(TAG, "停止同屏按钮 :  sameMemberDevRrsIds --->>> " + sameMemberDevRrsIds.get(i));
                    }
                    nativeUtil.stopResourceOperate(sameMemberDevRrsIds, sameMemberDevIds);
                }
            }
        });
        // 发起投影
        postilHolder.postil_start_projection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPop(postilPop, projectPop, params);
                openProjectpopFromPostilPop = true;
                wm.addView(projectPop, notParams);
            }
        });
        // 停止投影
        postilHolder.postil_stop_projection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (applyProjectionIds != null && informDev != null) {
                    /** ************ ******  停止资源操作  ****** ************ **/
                    nativeUtil.stopResourceOperate(informDev, applyProjectionIds);
                }
            }
        });
        //退出
        postilHolder.postil_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop(postilPop, mImageView, mParams);
            }
        });
    }

    //截图信息保存本地/服务器
    private void savePop(final byte[] bytes, final int type) {
        /** **** **  输入框  ** **** **/
        edtPop = LayoutInflater.from(getApplicationContext()).inflate(R.layout.edt_file_name, null);
        EdtViewHolder edtHolder = new EdtViewHolder(edtPop);
        EdtHolder_Event(edtHolder, bytes, type);
    }

    //输入文件名的输入框
    private void EdtHolder_Event(final EdtViewHolder holder, final byte[] bytes, final int type) {
        wm.addView(edtPop, notParams);
        holder.edt_name.setHint("请输入文件名");
        holder.edt_name.setText(System.currentTimeMillis() + "");
        //确定
        holder.ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = FileUtil.bytes2Bitmap(bytes);
                MyUtils.CreateFile(Macro.POSTILFILE);
                String newName = holder.edt_name.getText().toString();
                if (newName.equals("")) {
                    Toast.makeText(FabService.this, "请先填写文件名", Toast.LENGTH_SHORT).show();
                } else {
                    File file = new File(Macro.POSTILFILE, newName + ".jpg");
                    try {
                        if (!file.exists()) {
                            file.createNewFile();
                            FileUtil.saveBitmap(bitmap, file);
                        }
                        if (type == 2) {//上传到服务器
                            String path = file.getPath();
                            int mediaid = getMediaid(path);
                            String fileEnd = path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase();
                            nativeUtil.uploadFile(InterfaceMacro.Pb_Upload_Flag.Pb_MEET_UPLOADFLAG_ONLYENDCALLBACK.getNumber(), 2, 0, newName + "." + fileEnd, path, 0, mediaid);
                            //无论是保存本地还是上传都要先保存，如果是上传到服务器则在上传完成后删除
                            file.delete();
                        }
                    } catch (InvalidProtocolBufferException e) {
                        Log.e(TAG, "FabService.onClick :  上传到服务器异常 --> " + e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e(TAG, "FabService.onClick :  文件创建失败 --> " + e.getMessage());
                        e.printStackTrace();
                    }
                    wm.removeView(edtPop);
                }
            }
        });
        //取消
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(edtPop);
            }
        });
    }

    //呼叫服务事件
    private void callServeHolder_Event(final FunViewHolder holder) {
        final List<Integer> arr = new ArrayList<>();
        arr.add(0);//会议服务类请求则为 0
        MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber();
        //纸
        holder.paper_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
//                /** ************ ******  185.发送会议交流信息  ****** ************ **/
                holder.edt_msg.setText("纸张申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Paper.getNumber();
            }
        });
        //笔
        holder.pen_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("需要一支笔");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Pen.getNumber();
            }
        });
        //茶水
        holder.tea_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("茶水申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Tea.getNumber();
            }
        });
        //矿泉水
        holder.water_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("矿泉水申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Water.getNumber();
            }
        });
        //计算器
        holder.calculator_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("计算器申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber();
            }
        });
        //服务员
        holder.waiter_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("服务员申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Waiter.getNumber();
            }
        });
        //清扫
        holder.sweep_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("需要清扫");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber();
            }
        });
        //技术员
        holder.technician_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("技术员申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Technical.getNumber();
            }
        });
        //发送
        holder.send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String other = holder.edt_msg.getText().toString();
                if (!other.trim().equals("")) {
                    nativeUtil.sendMeetChatInfo(other, MSG_TYPE, arr);
                    /** **** **  发送后清空输入框  ** **** **/
                    holder.edt_msg.setText("");
                }
                showPop(callServePop, mImageView, mParams);
            }
        });
    }

    //投影控制事件
    private void proHolder_Event(ProjectViewHolder proHolder) {
        //所有投影机
        proHolder.all_pro_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < checkProAll.size(); i++) {
                        checkProAll.set(i, true);
                    }
                }
            }
        });
        //选择投影机
        proHolder.choose_pro_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** **** **  打开投影机列表  ** **** **/
                showProRlPop();
            }
        });
        //申请投影
        proHolder.start_pro.setOnClickListener(new View.OnClickListener() {
            private int subid;
            private int srcdeviceid;

            @Override
            public void onClick(View v) {
                applyProjectionIds = new ArrayList<Integer>();
                informDev = new ArrayList<Integer>();
                if (openProjector) {
                    srcdeviceid = videoInfos.get(0).getVideoInfo().getDeviceid();
                    subid = videoInfos.get(0).getVideoInfo().getSubid();
//                    applyProjectionIds.add(MeetingActivity.getDevId());
                    applyProjectionIds.add(NativeService.localDevId);
                } else {
                    subid = 2;
                    srcdeviceid = DevMeetInfo.getDeviceid();
                }
                if (allProjectorAdapter != null) {
                    List<DeviceInfo> checkedIds = allProjectorAdapter.getCheckedIds(1);
                    for (int i = 0; i < checkedIds.size(); i++) {
                        applyProjectionIds.add(checkedIds.get(i).getDevId());
                    }
                    informDev.add(0);
                    applyProjectionIds.add(0);
                    /** ************ ******  流播放  0x1080004****** ************ **/
                    nativeUtil.streamPlay(srcdeviceid, subid, 0, informDev, applyProjectionIds);
                }

            }
        });
        //结束投影
        proHolder.stop_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (applyProjectionIds != null && informDev != null) {
                    /** ************ ******  停止资源操作  ****** ************ **/
                    nativeUtil.stopResourceOperate(informDev, applyProjectionIds);
                }
            }
        });
        //取消
        proHolder.pro_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openProjectpopFromPostilPop) {//判断是否是在批注页面打开的
                    openProjectpopFromPostilPop = false;//重新设置为false
                    wm.removeView(projectPop);
                } else {
                    showPop(projectPop, mImageView, mParams);
                }
            }
        });
    }

    //投影机列表
    private void showProRlPop() {
        proListPop = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pro_pop, null);
        ProListViewHolder proListHolder = new ProListViewHolder(proListPop);
        proListHolder_Event(proListHolder);
        showPop(projectPop, proListPop);
    }

    //投影机列表事件
    private void proListHolder_Event(final ProListViewHolder proListHolder) {
        allProjectorAdapter.setItemClick(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                Button player = view.findViewById(R.id.palyer_name);
                boolean selected = !player.isSelected();
                player.setSelected(selected);
                checkProAll.set(posion, selected);
                proListHolder.pro_all_cb.setChecked(!checkProAll.contains(false));
                allProjectorAdapter.notifyDataSetChanged();
            }
        });
        // 全选按钮监听
        proListHolder.pro_all_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //如果包含false
                    for (int i = 0; i < allProjectors.size(); i++) {
                        if (!checkProAll.get(i)) {
                            //将包含false的选项设为true
                            checkProAll.set(i, true);
                        }
                    }
                } else {
                    //全选为false
                    if (!checkProAll.contains(false)) {
                        //不包含false 就是全部为true的状态
                        for (int i = 0; i < allProjectors.size(); i++) {
                            //全部都设为false
                            checkProAll.set(i, false);
                        }
                    }
                }
                allProjectorAdapter.notifyDataSetChanged();
            }
        });
        proListHolder.pro_ensure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allProjectorAdapter != null) {
                    List<DeviceInfo> checkedIds = allProjectorAdapter.getCheckedIds(1);
                    Log.e("MyLog", "MeetingActivity.onClick 1067行:  选中的投影机有 --->>> " + checkedIds.size());
                }
                showPop(proListPop, projectPop);
                allProjectorAdapter.notifyDataSetChanged();
            }
        });
        proListHolder.pro_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPop(proListPop, projectPop);
            }
        });
    }

    //同屏控制事件
    private void Screen_Event(ScreenViewHolder holder) {
        //选中所有人
        holder.everyone_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //如果 所有人 为true
                    for (int i = 0; i < checks.size(); i++) {
                        checks.set(i, true);
                    }
                }
            }
        });
        //选择参与人
        holder.players.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择参与人 和 投影机
                showPlayerPop();
            }
        });

        //选中所有投影机
        holder.all_projector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    for (int i = 0; i < checkProOL.size(); i++) {
                        checkProOL.set(i, true);
                    }
                }
            }
        });

        //自由选择投影机
        holder.choose_projector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlayerPop();
            }
        });

        //同屏控制按钮
        holder.screens_btn.setOnClickListener(new View.OnClickListener() {

            private int subid;  // 2：屏幕 3：摄像头
            private int srcdeviceid;//需要采集的设备ID

            @Override
            public void onClick(View view) {
                sameMemberDevRrsIds = new ArrayList<Integer>();
                sameMemberDevIds = new ArrayList<Integer>();
                /** **** **  在线的参会人  ** **** **/
                if (onLineMemberAdapter != null) {
                    List<DevMember> checkedIds = onLineMemberAdapter.getCheckedIds();
                    for (int i = 0; i < checkedIds.size(); i++) {
                        Log.e(TAG, "添加参会人 --> ");
                        sameMemberDevIds.add(checkedIds.get(i).getDevId());
                    }
                } else {
                    Log.e(TAG, "在线参会人Adapter为空 --> ");
                }
                /** **** **  在线的投影机  ** **** **/
                if (onLineProjectorAdapter != null) {
                    List<DeviceInfo> checkedIds1 = onLineProjectorAdapter.getCheckedIds(0);
                    for (int i = 0; i < checkedIds1.size(); i++) {
                        Log.e(TAG, "添加投影机 --> ");
                        sameMemberDevRrsIds.add(checkedIds1.get(i).getDevId());
                    }
                } else {
                    Log.e(TAG, "在线投影机Adapter为空 --> ");
                }
                /** **** **  如果是从视屏直播列表点击：  ** **** **/
                Log.e(TAG, "是否从视屏直播点击 --->>> " + fromVideo);
                if (fromVideo) {
                    srcdeviceid = videoInfos.get(0).getVideoInfo().getDeviceid();
                    subid = videoInfos.get(0).getVideoInfo().getSubid();
//                    sameMemberDevIds.add(MeetingActivity.getDevId());
                    sameMemberDevIds.add(NativeService.localDevId);
                } else {
                    srcdeviceid = DevMeetInfo.getDeviceid();
                    subid = 2;
                }
                Log.e(TAG, "需要采集屏幕还是摄像头 --->>> " + subid);
                sameMemberDevRrsIds.add(0);
//                sameMemberDevIds.add(0);//要播放的屏幕源(需要看的设备ID)
                Log.e(TAG, "FabService.onClick :   --> " + sameMemberDevIds.toString());
                Log.e(TAG, "FabService.onClick :   --> " + sameMemberDevRrsIds.toString());
                /** ************ ******  流播放  ******0x1080004  0x1100003************ **/
                nativeUtil.streamPlay(srcdeviceid, subid, 0, sameMemberDevRrsIds, sameMemberDevIds);
                showPop(screenPop, mImageView, mParams);
            }
        });

        //停止同屏按钮
        holder.stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sameMemberDevRrsIds != null && sameMemberDevIds != null) {
                    /** ************ ******  停止资源操作  ****** ************ **/
                    Log.e(TAG, "onClick :  停止同屏操作 --->>> ");
                    for (int i = 0; i < sameMemberDevIds.size(); i++) {
                        Log.e(TAG, "停止同屏按钮 :  sameMemberDevIds --->>> " + sameMemberDevIds.get(i));
                    }
                    for (int i = 0; i < sameMemberDevRrsIds.size(); i++) {
                        Log.e(TAG, "停止同屏按钮 :  sameMemberDevRrsIds --->>> " + sameMemberDevRrsIds.get(i));
                    }
                    nativeUtil.stopResourceOperate(sameMemberDevRrsIds, sameMemberDevIds);
                    Log.e(TAG, "FabService.onClick :  同屏控制 --> " + 1);
                }
            }
        });
        //加入同屏按钮
        holder.join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    nativeUtil.queryCanJoin();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
        //取消
        holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromVideo = false;
                if (openScreenpopFromPostilpop) {
                    openScreenpopFromPostilpop = false;
                    wm.removeView(screenPop);
                } else {
                    showPop(screenPop, mImageView, mParams);
                }
            }
        });
    }

    //可加入同屏
    private void showJoinPop() {
        canJoinPop = LayoutInflater.from(getApplicationContext()).inflate(R.layout.canjoin_pop, null);
        CanJoinViewHolder holder = new CanJoinViewHolder(canJoinPop);
        canJoin_Event(holder);
        showPop(screenPop, canJoinPop, notParams);
    }

    //可加入同屏事件
    private void canJoin_Event(final CanJoinViewHolder holder) {
        canJoinMemberAdapter.setItemClick(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                /** **** **  点击哪个item就设置哪个选中  ** **** **/
//                Button player = view.findViewById(R.id.palyer_name);
//                boolean selected = !player.isSelected();
//                player.setSelected(selected);
                setOtherNoClick();
                canjoinMemberSelect.set(posion, true);
//                canjoinMemberSelect.set(posion, selected);
                canJoinMemberAdapter.notifyDataSetChanged();
            }
        });
        canJoinProAdapter.setItemClick(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
//                Button player = view.findViewById(R.id.palyer_name);
//                boolean selected = !player.isSelected();
//                player.setSelected(selected);
                setOtherNoClick();
                canjoinProSelect.set(posion, true);
//                holder.canjoin_projector_cb.setChecked(!canjoinProSelect.contains(false));
                canJoinProAdapter.notifyDataSetChanged();
            }
        });
        //确定
        holder.canjoin_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** **** **  查询流播放  ** **** **/
                try {
                    nativeUtil.queryStreamPlay();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setOtherNoClick() {
        if (canjoinMemberSelect != null && canjoinMemberSelect.size() > 0) {
            for (int i = 0; i < canjoinMemberSelect.size(); i++) {
                canjoinMemberSelect.set(i, false);
            }
        }
        if (canjoinProSelect != null && canjoinProSelect.size() > 0) {
            for (int i = 0; i < canjoinProSelect.size(); i++) {
                canjoinProSelect.set(i, false);
            }
        }
    }

    //选择参会人和投影机
    private void showPlayerPop() {
        choosePlayerPop = LayoutInflater.from(getApplicationContext()).inflate(R.layout.player_pop, null);
        PlayerViewHolder holder = new PlayerViewHolder(choosePlayerPop);
        PlayerEvent(holder);
        showPop(screenPop, choosePlayerPop);
    }

    //选择参会人弹出框事件监听
    private void PlayerEvent(final PlayerViewHolder holder) {
        //参会人item点击事件
        onLineMemberAdapter.setItemClick(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                Button player = view.findViewById(R.id.palyer_name);
                boolean selected = !player.isSelected();
                player.setSelected(selected);
                checks.set(posion, selected);
                holder.players_all_cb.setChecked(!checks.contains(false));
                onLineMemberAdapter.notifyDataSetChanged();
            }
        });
        //投影机item点击事件
        onLineProjectorAdapter.setItemClick(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                Button player = view.findViewById(R.id.palyer_name);
                boolean selected = !player.isSelected();
                player.setSelected(selected);
                checkProOL.set(posion, selected);
                holder.projector_all_cb.setChecked(!checkProOL.contains(false));
                onLineProjectorAdapter.notifyDataSetChanged();
            }
        });
        //全选按钮状态监听
        holder.players_all_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e("MyLog", "MeetingActivity.onCheckedChanged:  是否全部选中 --->>> " + b);
                if (b) {
                    //如果包含false
                    for (int i = 0; i < onLineMembers.size(); i++) {
                        if (!checks.get(i)) {
                            //将包含false的选项设为true
                            checks.set(i, true);
                        }
                    }
                } else {
                    //全选为false
                    if (!checks.contains(false)) {
                        //不包含false 就是全部为true的状态
                        for (int i = 0; i < onLineMembers.size(); i++) {
                            //全部都设为false
                            checks.set(i, false);
                        }
                    }
                }
                onLineMemberAdapter.notifyDataSetChanged();
            }
        });
        // 投影机全选 选择框
        holder.projector_all_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //如果包含false
                    for (int i = 0; i < onLineProjectors.size(); i++) {
                        if (!checkProOL.get(i)) {
                            //将包含false的选项设为true
                            checkProOL.set(i, true);
                        }
                    }
                } else {
                    //全选为false
                    if (!checkProOL.contains(false)) {
                        //不包含false 就是全部为true的状态
                        for (int i = 0; i < onLineProjectors.size(); i++) {
                            //全部都设为false
                            checkProOL.set(i, false);
                        }
                    }
                }
                onLineProjectorAdapter.notifyDataSetChanged();

            }
        });
        //确定按钮
        holder.player_pop_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onLineMemberAdapter != null) {
                    List<DevMember> checkedIds = onLineMemberAdapter.getCheckedIds();
                    Log.e("MyLog", "MeetingActivity.onClick 1262行:  选中的人员数量： --->>> " + checkedIds.size());
                }
                if (onLineProjectorAdapter != null) {
                    List<DeviceInfo> checkedIds1 = onLineProjectorAdapter.getCheckedIds(0);
                    Log.e("MyLog", "MeetingActivity.onClick 1266行:  选中投影机数量 --->>> " + checkedIds1.size());
                }
                onLineMemberAdapter.notifyDataSetChanged();
                onLineProjectorAdapter.notifyDataSetChanged();
                showPop(choosePlayerPop, screenPop);
            }
        });
        //取消按钮
        holder.player_pop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPop(choosePlayerPop, screenPop);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e(TAG, "FabService.onTouch :   --> " + this);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                mTouchStartX = (int) event.getRawX();
                mTouchStartY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int rawX = (int) event.getRawX();
                int rawY = (int) event.getRawY();
                int mx = rawX - mTouchStartX;
                int my = rawY - mTouchStartY;
                mParams.x += mx;
                mParams.y += my;//相对于屏幕左上角的位置
                wm.updateViewLayout(mImageView, mParams);
                mTouchStartX = rawX;
                mTouchStartY = rawY;
                break;
            case MotionEvent.ACTION_UP:
                upTime = System.currentTimeMillis();
                if (upTime - downTime > 150) {
                    int screenWidth = ScreenUtils.getScreenWidth(this);
                    Log.e(TAG, "FabService.onTouch :  screenWidth --> " + screenWidth + " mImageView.getWidth():" + mImageView.getWidth());
                    mParams.x = screenWidth - mImageView.getWidth();
                    mParams.y = mTouchStartY - mImageView.getHeight();
                    wm.updateViewLayout(mImageView, mParams);
                } else {
                    Log.e(TAG, "FabService.onTouch :  打开小功能主页 --> ");
                    showPop(mImageView, pop);
                }
                break;
        }
        return true;
    }

    /**
     * 展示新的弹框
     *
     * @param removeView 原来展示的view
     * @param addView    需要展示的view
     * @param params     params配置
     */
    private void showPop(View removeView, View addView, WindowManager.LayoutParams params) {
        wm.removeView(removeView);
        wm.addView(addView, params);
    }

    private void showPop(View removeView, View addView) {
        wm.removeView(removeView);
        wm.addView(addView, params);
    }

    //弹框主页事件
    private void holderEvent(FabViewHolder holder) {
        //返回
        holder.back_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickScreen = false;
                showPop(pop, mImageView, mParams);
            }
        });
        //截图
        holder.screens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempImgName = System.currentTimeMillis();
                Log.e(TAG, "FabService.onClick :  isBusy --> " + isBusy);
                if (isBusy) {//正在录屏或者正在播放视屏时，使用应用内截图
                    Toast.makeText(fabContext, "您正在同屏中...", Toast.LENGTH_SHORT).show();
                } else {
                    ScreenShot();
                }
            }
        });
        //同屏控制
        holder.one_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickScreen = true;
                try {
                    nativeUtil.queryAttendPeople();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
        //投影控制
        holder.projection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickProject = true;
                try {
                    nativeUtil.queryAttendPeople();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
        //会议笔记
        holder.note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotePop();
            }
        });
        //呼叫服务
        holder.call_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showServePop();
            }
        });
    }

    //截图
    private void ScreenShot() {
        wm.removeView(pop);
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                startVirtual();
                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startScreen();
                        Handler handler3 = new Handler();
                        handler3.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                File file = new File(Macro.TAKE_PHOTO + tempImgName + ".jpg");
                                if (file.exists()) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(Macro.TAKE_PHOTO + tempImgName + ".jpg");
                                    bytes = FileUtil.Bitmap2bytes(bitmap);
                                    showPostilPop(bytes);
                                    wm.addView(postilPop, postilParams);
                                    //将临时图片删除
                                    file.delete();
                                } else {
                                    Toast.makeText(FabService.this, "找不到该图片", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, 200);
                    }
                }, 200);
            }
        }, 500);
//                Bitmap bitmap = ScreenUtils.snapShotWithoutStatusBar(MeetingActivity.context);
//                byte[] bytes = FileUtil.Bitmap2bytes(bitmap);
//                showPostilPop(bytes);
//                showPop(pop, postilPop, postilParams);
    }

    //打开会议笔记界面
    private void showNotePop() {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        showPop(pop, mImageView, mParams);
    }

    // 呼叫服务弹框
    private void showServePop() {
        callServePop = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_function_msg, null);
        FunViewHolder callServeHolder = new FunViewHolder(callServePop);
        callServeHolder_Event(callServeHolder);
        showPop(pop, callServePop, notParams);
    }

    @Override
    public void onCreate() {
        Log.i("Fab_life", "FabService.onCreate :   --> " + this);
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        boolean fabService = MyUtils.isServiceRunning(fabContext, "com.pa.paperless.service.FabService");
        Log.i("Fab_life", "FabService.onDestroy :   --> " + this + "  " + fabService);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        Log.i("Fab_life", "FabService.unbindService :   --->>> " + this);
        super.unbindService(conn);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "FabService.onBind :   --> ");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Fab_life", "FabService.onStartCommand :  --> " + this);
        showFab();
        fabContext = this;
        return super.onStartCommand(intent, flags, startId);
    }

    public static class FabViewHolder {
        public View rootView;
        public TextView screens;
        public TextView one_screen;
        public TextView back_pop;
        public TextView projection;
        public TextView note;
        public TextView call_service;

        public FabViewHolder(View rootView) {
            this.rootView = rootView;
            this.screens = (TextView) rootView.findViewById(R.id.screens);
            this.one_screen = (TextView) rootView.findViewById(R.id.one_screen);
            this.note = (TextView) rootView.findViewById(R.id.note);
            this.back_pop = (TextView) rootView.findViewById(R.id.back_pop);
            this.projection = (TextView) rootView.findViewById(R.id.projection);
            this.call_service = (TextView) rootView.findViewById(R.id.call_service);
        }
    }

    public static class ScreenViewHolder {
        public View rootView;
        public RadioButton everyone_cb;
        public RadioButton players;
        public RadioButton all_projector;
        public RadioButton choose_projector;
        public Button screens_btn;
        public Button stop_btn;
        public Button join_btn;
        public Button cancel_btn;

        public ScreenViewHolder(View rootView) {
            this.rootView = rootView;
            this.everyone_cb = (RadioButton) rootView.findViewById(R.id.everyone_cb);
            this.players = (RadioButton) rootView.findViewById(R.id.players);
            this.all_projector = (RadioButton) rootView.findViewById(R.id.all_projector);
            this.choose_projector = (RadioButton) rootView.findViewById(R.id.choose_projector);
            this.screens_btn = (Button) rootView.findViewById(R.id.screens_btn);
            this.stop_btn = (Button) rootView.findViewById(R.id.stop_btn);
            this.join_btn = (Button) rootView.findViewById(R.id.join_btn);
            this.cancel_btn = (Button) rootView.findViewById(R.id.cancel_btn);
        }

    }

    public static class PlayerViewHolder {
        public View rootView;
        public CheckBox players_all_cb;
        public RecyclerView players_rl;
        public CheckBox projector_all_cb;
        public RecyclerView projector_rl;
        public Button player_pop_ensure;
        public Button player_pop_cancel;

        public PlayerViewHolder(View rootView) {
            this.rootView = rootView;
            this.players_all_cb = (CheckBox) rootView.findViewById(R.id.players_all_cb);
            this.players_rl = (RecyclerView) rootView.findViewById(R.id.players_rl);
            //瀑布流布局
            this.players_rl.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL));
            this.players_rl.setAdapter(onLineMemberAdapter);
            this.projector_all_cb = (CheckBox) rootView.findViewById(R.id.projector_all_cb);
            this.projector_rl = (RecyclerView) rootView.findViewById(R.id.projector_rl);
            //瀑布流布局
            this.projector_rl.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
            this.projector_rl.setAdapter(onLineProjectorAdapter);
            this.player_pop_ensure = (Button) rootView.findViewById(R.id.player_pop_ensure);
            this.player_pop_cancel = (Button) rootView.findViewById(R.id.player_pop_cancel);
        }

    }

    public static class ProjectViewHolder {
        public View rootView;
        public RadioButton all_pro_cb;
        public RadioButton choose_pro_cb;
        public Button start_pro;
        public Button stop_pro;
        public Button pro_cancel;

        public ProjectViewHolder(View rootView) {
            this.rootView = rootView;
            this.all_pro_cb = (RadioButton) rootView.findViewById(R.id.all_pro_cb);
            this.choose_pro_cb = (RadioButton) rootView.findViewById(R.id.choose_pro_cb);
            this.start_pro = (Button) rootView.findViewById(R.id.start_pro);
            this.stop_pro = (Button) rootView.findViewById(R.id.stop_pro);
            this.pro_cancel = (Button) rootView.findViewById(R.id.pro_cancel);
        }

    }

    public static class ProListViewHolder {
        public View rootView;
        public CheckBox pro_all_cb;
        public RecyclerView pro_rl;
        public Button pro_ensure_btn;
        public Button pro_cancel_btn;

        public ProListViewHolder(View rootView) {
            this.rootView = rootView;
            this.pro_all_cb = (CheckBox) rootView.findViewById(R.id.pro_all_cb);
            this.pro_rl = (RecyclerView) rootView.findViewById(R.id.pro_rl);
            this.pro_rl.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL));
            this.pro_rl.setAdapter(allProjectorAdapter);
            this.pro_ensure_btn = (Button) rootView.findViewById(R.id.pro_ensure_btn);
            this.pro_cancel_btn = (Button) rootView.findViewById(R.id.pro_cancel_btn);
        }
    }

    public static class FunViewHolder {
        public View rootView;
        public TextView paper_msg;
        public TextView pen_msg;
        public TextView tea_msg;
        public TextView water_msg;
        public TextView calculator_msg;
        public TextView waiter_msg;
        public TextView sweep_msg;
        public TextView technician_msg;
        public EditText edt_msg;
        public TextView send_msg;

        public FunViewHolder(View rootView) {
            this.rootView = rootView;
            this.paper_msg = (TextView) rootView.findViewById(R.id.paper_msg);
            this.pen_msg = (TextView) rootView.findViewById(R.id.pen_msg);
            this.tea_msg = (TextView) rootView.findViewById(R.id.tea_msg);
            this.water_msg = (TextView) rootView.findViewById(R.id.water_msg);
            this.calculator_msg = (TextView) rootView.findViewById(R.id.calculator_msg);
            this.waiter_msg = (TextView) rootView.findViewById(R.id.waiter_msg);
            this.sweep_msg = (TextView) rootView.findViewById(R.id.sweep_msg);
            this.technician_msg = (TextView) rootView.findViewById(R.id.technician_msg);
            this.edt_msg = (EditText) rootView.findViewById(R.id.edt_msg);
            this.send_msg = (TextView) rootView.findViewById(R.id.send_msg);
        }
    }

    public static class PostilViewHolder {
        public View rootView;
        public ImageView postil_image;
        public Button postil_save_local;
        public Button postil_save_server;
        public Button postil_pic;
        public Button postil_start_screen;
        public Button postil_stop_screen;
        public Button postil_start_projection;
        public Button postil_stop_projection;
        public Button postil_pre;
        public Button postil_next;
        public LinearLayout postil_layout_id;

        public PostilViewHolder(View rootView) {
            this.rootView = rootView;
            this.postil_image = (ImageView) rootView.findViewById(R.id.postil_image);
            this.postil_save_local = (Button) rootView.findViewById(R.id.postil_save_local);
            this.postil_save_server = (Button) rootView.findViewById(R.id.postil_save_server);
            this.postil_pic = (Button) rootView.findViewById(R.id.postil_pic);
            this.postil_start_screen = (Button) rootView.findViewById(R.id.postil_start_screen);
            this.postil_stop_screen = (Button) rootView.findViewById(R.id.postil_stop_screen);
            this.postil_start_projection = (Button) rootView.findViewById(R.id.postil_start_projection);
            this.postil_stop_projection = (Button) rootView.findViewById(R.id.postil_stop_projection);
            this.postil_pre = (Button) rootView.findViewById(R.id.postil_pre);
//            this.postil_next = (Button) rootView.findViewById(R.id.postil_next);
            this.postil_layout_id = (LinearLayout) rootView.findViewById(R.id.postil_layout_id);
        }

    }

    public static class EdtViewHolder {
        public View rootView;
        public EditText edt_name;
        public Button ensure;
        public Button cancel;

        public EdtViewHolder(View rootView) {
            this.rootView = rootView;
            this.edt_name = (EditText) rootView.findViewById(R.id.edt_name);
            this.ensure = (Button) rootView.findViewById(R.id.ensure);
            this.cancel = (Button) rootView.findViewById(R.id.cancel);
        }

    }

    public static class CanJoinViewHolder {
        public View rootView;
        public CheckBox canjoin_player_cb;
        public RecyclerView canjoin_players_rl;
        public CheckBox canjoin_projector_cb;
        public RecyclerView canjoin_projector_rl;
        public Button canjoin_ensure;
        public Button canjoin_cancel;

        public CanJoinViewHolder(View rootView) {
            this.rootView = rootView;
            this.canjoin_player_cb = (CheckBox) rootView.findViewById(R.id.canjoin_player_cb);
            this.canjoin_player_cb.setVisibility(View.GONE);
            //可加入同屏的参会人（有录屏数据）列表
            this.canjoin_players_rl = (RecyclerView) rootView.findViewById(R.id.canjoin_players_rl);
            this.canjoin_players_rl.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.HORIZONTAL));
            this.canjoin_players_rl.setAdapter(canJoinMemberAdapter);
            this.canjoin_projector_cb = (CheckBox) rootView.findViewById(R.id.canjoin_projector_cb);
            this.canjoin_projector_cb.setVisibility(View.GONE);//最多选择一个，所以进行隐藏
            //可加入同屏的正在播放的投影机列表
            this.canjoin_projector_rl = (RecyclerView) rootView.findViewById(R.id.canjoin_projector_rl);
            this.canjoin_projector_rl.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
            this.canjoin_projector_rl.setAdapter(canJoinProAdapter);
            this.canjoin_ensure = (Button) rootView.findViewById(R.id.canjoin_ensure);
            this.canjoin_cancel = (Button) rootView.findViewById(R.id.canjoin_cancel);
        }

    }
}
