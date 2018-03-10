package com.pa.paperless.fragment.meeting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.adapter.VideoItemAdapter;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventBadge;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.Dispose;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.mBadge;
import static com.pa.paperless.activity.MeetingActivity.mReceiveMsg;
import static com.pa.paperless.activity.MeetingActivity.o;

/**
 * Created by Administrator on 2017/10/31.
 * 视屏直播
 */

public class VideoFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private NativeUtil nativeUtil;
    private RecyclerView mVideoLv;
    private SurfaceView mTopLeftVideo;
    private SurfaceView mTopRightVideo;
    private SurfaceView mBottomLeftVideo;
    private SurfaceView mBottomRightVideo;
    private Button mLookVideoBtn;
    private Button mStopVideoBtn;
    private Button mPushVideoBtn;
    private Button mProjectorVideoBtn;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_DEVICE_INFO:
                    ArrayList queryDevInfo = msg.getData().getParcelableArrayList("queryDevInfo");
                    InterfaceMain.pbui_Type_DeviceDetailInfo o = (InterfaceMain.pbui_Type_DeviceDetailInfo) queryDevInfo.get(0);
                    List<DeviceInfo> deviceInfos = Dispose.DevInfo(o);
                    allStreamDev = new ArrayList<>();
                    for (int i = 0; i < deviceInfos.size(); i++) {
                        DeviceInfo deviceInfo = deviceInfos.get(i);
                        int devId = deviceInfo.getDevId();
                        int i1 = devId & Macro.DEVICE_MEET_CAPTURE;
                        if (i1 == Macro.DEVICE_MEET_CAPTURE) {
                            //所有的流采集设备
                            allStreamDev.add(deviceInfo);
                        }
                    }
                    break;
            }
        }
    };
    //存放所有流设备
    private List<DeviceInfo> allStreamDev;
    private boolean b;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_video, container, false);
        initView(inflate);
        initController();
        try {
            /** ************ ******  6.查询设备信息  ****** ************ **/
            b = nativeUtil.queryDeviceInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.DEV_REGISTER_INFORM:
                if (!b) {
                    b = nativeUtil.queryDeviceInfo();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initController() {
        nativeUtil = NativeUtil.getInstance();
        nativeUtil.setCallListener(this);
    }

    private void initView(View inflate) {
        mVideoLv = (RecyclerView) inflate.findViewById(R.id.video_lv);
        if (allStreamDev != null) {
            VideoItemAdapter adapter = new VideoItemAdapter(allStreamDev);
            mVideoLv.setLayoutManager(new LinearLayoutManager(getContext()));
            mVideoLv.setAdapter(adapter);
            adapter.setItemListener(new ItemClickListener() {
                @Override
                public void onItemClick(View view, int posion) {
                    Log.e("MyLog", "VideoFragment.onItemClick 138行:   --->>> ");

                }
            });
        }

        mTopLeftVideo = (SurfaceView) inflate.findViewById(R.id.top_left_video);
        mTopLeftVideo.setOnClickListener(this);
        mTopRightVideo = (SurfaceView) inflate.findViewById(R.id.top_right_video);
        mTopRightVideo.setOnClickListener(this);
        mBottomLeftVideo = (SurfaceView) inflate.findViewById(R.id.bottom_left_video);
        mBottomLeftVideo.setOnClickListener(this);
        mBottomRightVideo = (SurfaceView) inflate.findViewById(R.id.bottom_right_video);
        mBottomRightVideo.setOnClickListener(this);
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

                break;
            case R.id.projector_video_btn:

                break;
            case R.id.top_left_video:

                break;
            case R.id.top_right_video:

                break;
            case R.id.bottom_left_video:

                break;
            case R.id.bottom_right_video:

                break;
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.RECEIVE_MEET_IMINFO: //收到会议消息
                Log.e("MyLog", "SigninFragment.callListener 296行:  收到会议消息 --->>> ");
                InterfaceMain2.pbui_Type_MeetIM receiveMsg = (InterfaceMain2.pbui_Type_MeetIM) result;
                //获取之前的未读消息个数
                int badgeNumber1 = mBadge.getBadgeNumber();
                Log.e("MyLog", "SigninFragment.callListener 307行:  原来的个数 --->>> " + badgeNumber1);
                int all = badgeNumber1 + 1;
                if (receiveMsg != null) {
                    List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(receiveMsg);
                    if (mReceiveMsg == null) {
                        mReceiveMsg = new ArrayList<>();
                    }
                    receiveMeetIMInfos.get(0).setType(true);
                    mReceiveMsg.add(receiveMeetIMInfos.get(0));
                    Log.e("MyLog", "SigninFragment.callListener: 收到的信息个数：  --->>> " + mReceiveMsg.size());
                }
                List<EventBadge> num = new ArrayList<>();
                num.add(new EventBadge(all));
                // TODO: 2018/3/7 通知界面更新
                Log.e("MyLog", "SigninFragment.callListener 319行:  传递过去的个数 --->>> " + all);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.UpDate_BadgeNumber, num));
                break;
            case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                InterfaceMain.pbui_Type_DeviceDetailInfo result1 = (InterfaceMain.pbui_Type_DeviceDetailInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryDevInfo", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
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
