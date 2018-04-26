package com.pa.paperless.fragment.meeting;


import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import android.widget.Toast;


import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.pa.paperless.R;
import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.adapter.VideoItemAdapter;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.Dispose;

import com.pa.paperless.utils.MyUtils;
import com.wind.myapplication.NativeUtil;
import com.wind.myapplication.ScreenRecorder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MEDIA_PROJECTION_SERVICE;


/**
 * Created by Administrator on 2017/10/31.
 * 视屏直播
 */

public class VideoFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private NativeUtil nativeUtil;
    private RecyclerView mVideoLv;
    private Button mLookVideoBtn;
    private Button mStopVideoBtn;
    private Button mPushVideoBtn;
    private Button mProjectorVideoBtn;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_DEVICE_INFO://查询设备信息
                    ArrayList queryDevInfo = msg.getData().getParcelableArrayList("queryDevInfo");
                    InterfaceDevice.pbui_Type_DeviceDetailInfo o = (InterfaceDevice.pbui_Type_DeviceDetailInfo) queryDevInfo.get(0);
                    List<DeviceInfo> deviceInfos = Dispose.DevInfo(o);
                    allStreamDev = new ArrayList<>();
                    for (int i = 0; i < deviceInfos.size(); i++) {
                        DeviceInfo deviceInfo = deviceInfos.get(i);
                        int devId = deviceInfo.getDevId();
                        int i1 = devId & Macro.DEVICE_MEET_CAPTURE;
                        if (i1 == Macro.DEVICE_MEET_CAPTURE) {
                            Log.e("MyLog", "VideoFragment.handleMessage 98行:  添加所有的流采集设备 --->>> ");
                            //所有的流采集设备
                            allStreamDev.add(deviceInfo);
                        }
                        adapter = new VideoItemAdapter(cxt, allStreamDev);
                        mVideoLv.setLayoutManager(new LinearLayoutManager(getContext()));
                        mVideoLv.setAdapter(adapter);
                        adapter.setItemListener(new ItemClickListener() {
                            @Override
                            public void onItemClick(View view, int posion) {
                                Log.e("MyLog", "VideoFragment.onItemClick 100行:  item点击了 --->>> " + posion);
                            }
                        });
                    }
                    break;
            }
        }
    };
    //存放所有流设备
    private List<DeviceInfo> allStreamDev;

    /**
     * add by gowcage
     */
    private final String TAG = "videoFragment-->";

    final int REQUEST_CODE = 1;// >=0
    private int width, height, dpi, bitrate, VideoQuality = 1;//VideoQuality:1/2/3
    private float density;

    private MediaProjectionManager manager;
    private MediaProjection projection;
    private ScreenRecorder recorder;

    private ArrayList allRes = new ArrayList(), devIds = new ArrayList();
    private VideoItemAdapter adapter;
    private Context cxt;

    private void initScreenParam() {
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels; // �屏幕宽度（像素）
        height = metric.heightPixels; // �屏幕高度（像素）
        Log.i(TAG, "w:" + width + "/h:" + height);
        density = metric.density; // �屏幕密度（0.75 / 1.0 / 1.5）
        dpi = metric.densityDpi; // �屏幕密度DPI（120 / 160 / 240）
        bitrate = width * height * VideoQuality;//�比特率/码率
    }

    private boolean stopRecord() {
        if (recorder != null) {
            Log.d(TAG, "quit");
            /** ************ ******  停止资源操作  ****** ************ **/
            nativeUtil.stopResourceOperate(allRes, devIds);
            /** ************ ******  释放播放资源  ****** ************ **/
            nativeUtil.mediaDestroy(0);
            recorder.quit();
            recorder = null;
            return true;
        } else return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: ");
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                projection = manager.getMediaProjection(resultCode, data);
            }

        if (projection == null) {
            Log.e(TAG, "media projection is null");
            return;
        }
        initScreenParam();
        if (recorder != null) recorder = null;
        recorder = new ScreenRecorder(width, height, bitrate, 1, projection, "");
        allRes.add(0);
        nativeUtil.streamPlay(MeetingActivity.getDevId(), 2, 0, allRes, devIds);
        recorder.start();//�启动录屏线程
        Toast.makeText(getActivity(), "屏幕录制中...", Toast.LENGTH_LONG).show();
    }

    //add by gowcage

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_video, container, false);
        initView(inflate);
        initController();
        cxt = getContext();
        try {
            /** ************ ******  6.查询设备信息  ****** ************ **/
            nativeUtil.queryDeviceInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);

        // 1: 拿到 MediaProjectionManager 实例
        manager = (MediaProjectionManager) getActivity().getSystemService(MEDIA_PROJECTION_SERVICE);
        return inflate;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.DEV_REGISTER_INFORM://设备寄存器变更通知
                //查询设备信息
                nativeUtil.queryDeviceInfo();
                break;
            case IDEventMessage.MEMBER_CHANGE_INFORM://90 参会人员变更通知
                InterfaceBase.pbui_MeetNotifyMsg MrmberName = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                /** ************ ******  91.查询指定ID的参会人  ****** ************ **/
                nativeUtil.queryAttendPeopleFromId(MrmberName.getId());
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopRecord();
    }

    @Override
    protected void initController() {
        nativeUtil = NativeUtil.getInstance();
        nativeUtil.setCallListener(this);
    }

    private void initView(View inflate) {
        mVideoLv = (RecyclerView) inflate.findViewById(R.id.video_lv);
        mLookVideoBtn = (Button) inflate.findViewById(R.id.look_video_btn);
        mLookVideoBtn.setOnClickListener(this);
        mStopVideoBtn = (Button) inflate.findViewById(R.id.stop_video_btn);
        mStopVideoBtn.setOnClickListener(this);
        mPushVideoBtn = (Button) inflate.findViewById(R.id.push_video_btn);
        mPushVideoBtn.setOnClickListener(this);
        mProjectorVideoBtn = (Button) inflate.findViewById(R.id.projector_video_btn);
        mProjectorVideoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.look_video_btn:
                List<Integer> res = new ArrayList<>();
                List<Integer> ids = new ArrayList<>();
//                nativeUtil.streamPlay(o.getDevId(),3,0,res,ids);
                break;
            case R.id.stop_video_btn:

                break;
            case R.id.push_video_btn:
                if (stopRecord()) {
                    Toast.makeText(getActivity(), "屏幕录制已停止", Toast.LENGTH_LONG).show();
                } else {
                    // 2: 发起屏幕捕捉请求
                    Intent intent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        intent = manager.createScreenCaptureIntent();
                    }
                    startActivityForResult(intent, REQUEST_CODE);
                }

                break;
            case R.id.projector_video_btn:

                break;
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.RECEIVE_MEET_IMINFO: //收到会议消息
                Log.e("MyLog", "VideoFragment.callListener 296行:  收到会议消息 --->>> ");
                MyUtils.receiveMessage((InterfaceIM.pbui_Type_MeetIM) result, nativeUtil);
                break;
            case IDivMessage.QUERY_ATTEND_BYID://查询指定ID的参会人
                MyUtils.queryName((InterfaceMember.pbui_Type_MemberDetailInfo) result);
                break;
            case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                MyUtils.handTo(IDivMessage.QUERY_DEVICE_INFO, (InterfaceDevice.pbui_Type_DeviceDetailInfo) result, "queryDevInfo", mHandler);
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            nativeUtil = NativeUtil.getInstance();
            nativeUtil.setCallListener(this);
        }
    }
}
