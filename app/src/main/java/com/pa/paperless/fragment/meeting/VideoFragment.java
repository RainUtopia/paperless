package com.pa.paperless.fragment.meeting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.protobuf.ByteString;
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
import com.pa.paperless.utils.MyUtils;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.libsdl.app.SDLActivity;

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

    private List<InterfaceMain.pbui_Item_MeetVideoDetailInfo> allStreamDev;
    public static List<Boolean> checkStreams;
    private VideoItemAdapter adapter;
    private int topLeftW;
    private int topLeftH;
    private int topLeftX;
    private int topLeftY;
    private int topRightW;
    private int topRightH;
    private int topRightX;
    private int topRightY;
    private int bottomLeftW;
    private int bottomLeftH;
    private int bottomLeftX;
    private int bottomLeftY;
    private int bottomRightW;
    private int bottomRightH;
    private int bottomRightX;
    private int bottomRightY;
    private int mPosion;//当前点击的列表的索引
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_MEET_VEDIO://172.查询会议视频
                    ArrayList queryMeetVedio = msg.getData().getParcelableArrayList("queryMeetVedio");
                    InterfaceMain.pbui_Type_MeetVideoDetailInfo o1 = (InterfaceMain.pbui_Type_MeetVideoDetailInfo) queryMeetVedio.get(0);
                    allStreamDev = o1.getItemList();
                    checkStreams = new ArrayList<>();
                    for (int i = 0; i < allStreamDev.size(); i++) {
                        checkStreams.add(false);
                    }
                    if (allStreamDev != null) {
                        adapter = new VideoItemAdapter(allStreamDev);
                        mVideoLv.setAdapter(adapter);
                        adapter.setItemListener(new ItemClickListener() {
                            @Override
                            public void onItemClick(View view, int posion) {
                                Log.e("MyLog", "VideoFragment.onItemClick 113行:  222222222 --->>> ");
                                mPosion = posion;
                                Button player = view.findViewById(R.id.name_tv);
                                boolean selected = !player.isSelected();
                                player.setSelected(selected);
                                for (int i = 0; i < checkStreams.size(); i++) {
                                    if (i == posion) {
                                        checkStreams.set(i, selected);
                                    } else {
                                        checkStreams.set(i, false);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "点击了视屏播放列表", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_video, container, false);
        initView(inflate);
        /** ************ ******  测量 SurfaceView 的起始坐标和宽高  ****** ************ **/
        getSurfaceViewXYWH();
        initController();
        try {
            //172.查询会议视频
            nativeUtil.queryMeetVedio();

            /** ************ ******  初始化播放资源  ****** ************ **/
//            nativeUtil.initvideores(0, topLeftX, topLeftY, topLeftH, topLeftW);
//            nativeUtil.initvideores(0, topRightX, topRightY, topRightH, topRightW);
//            nativeUtil.initvideores(0, bottomLeftX, bottomLeftY, bottomLeftH, bottomLeftW);
//            nativeUtil.initvideores(0, bottomRightX, bottomRightY, bottomRightH, bottomRightW);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.Meet_vedio_changeInform://会议视频变更通知
                //172.查询会议视频
                nativeUtil.queryMeetVedio();
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
        mVideoLv.setLayoutManager(new LinearLayoutManager(getContext()));

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

    /**
     * 测量 SurfaceView的起始坐标与宽高
     */
    private void getSurfaceViewXYWH() {
        mTopLeftVideo.post(new Runnable() {
            @Override
            public void run() {
                topLeftW = mTopLeftVideo.getWidth();
                topLeftH = mTopLeftVideo.getHeight();
                topLeftX = (int) mTopLeftVideo.getX();
                topLeftY = (int) mTopLeftVideo.getY();
                Log.e("MyLog", "VideoFragment.run 169行:  左上 --->>> " + topLeftW + "  " + topLeftH + "  " + topLeftX + "  " + topLeftY);

            }
        });
        mTopRightVideo.post(new Runnable() {
            @Override
            public void run() {
                topRightW = mTopRightVideo.getWidth();
                topRightH = mTopRightVideo.getHeight();
                topRightX = (int) mTopRightVideo.getX();
                topRightY = (int) mTopRightVideo.getY();
                Log.e("MyLog", "VideoFragment.run 169行:  右上 --->>> " + topRightW + "  " + topRightH + "  " + topRightX + "  " + topRightY);
            }
        });
        mBottomLeftVideo.post(new Runnable() {
            @Override
            public void run() {
                bottomLeftW = mBottomLeftVideo.getWidth();
                bottomLeftH = mBottomLeftVideo.getHeight();
                bottomLeftX = (int) mBottomLeftVideo.getX();
                bottomLeftY = (int) mBottomLeftVideo.getY();
                Log.e("MyLog", "VideoFragment.run 169行:  左下 --->>> " + bottomLeftW + "  " + bottomLeftH + "  " + bottomLeftX + "  " + bottomLeftY);
            }
        });
        mBottomRightVideo.post(new Runnable() {
            @Override
            public void run() {
                bottomRightW = mBottomRightVideo.getWidth();
                bottomRightH = mBottomRightVideo.getHeight();
                bottomRightX = (int) mBottomRightVideo.getX();
                bottomRightY = (int) mBottomRightVideo.getY();
                Log.e("MyLog", "VideoFragment.run 169行:  右下 --->>> " + bottomRightW + "  " + bottomRightH + "  " + bottomRightX + "  " + bottomRightY);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.look_video_btn://观看直播
                InterfaceMain.pbui_Item_MeetVideoDetailInfo pbui_item_meetVideoDetailInfo = allStreamDev.get(mPosion);
                int subid = pbui_item_meetVideoDetailInfo.getSubid();
                int id = pbui_item_meetVideoDetailInfo.getId();
                int deviceid = pbui_item_meetVideoDetailInfo.getDeviceid();
                pbui_item_meetVideoDetailInfo.getAddr();

                List<Integer> res = new ArrayList<>();
                List<Integer> ids = new ArrayList<>();
                res.add(id);
                ids.add(deviceid);
                // 流播放
                nativeUtil.streamPlay(o.getDevId(), subid, 0, res, ids);
//                nativeUtil.streamPlay(o.getDevId(),3,0,res,ids);
                break;
            case R.id.stop_video_btn://停止观看
                // 停止资源操作
//                nativeUtil.stopResourceOperate();
                break;
            case R.id.push_video_btn://推送视屏

                break;
            case R.id.projector_video_btn://投影视屏

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
                int all = badgeNumber1 + 1;
                if (receiveMsg != null) {
                    List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(receiveMsg);
                    if (mReceiveMsg == null) {
                        mReceiveMsg = new ArrayList<>();
                    }
                    receiveMeetIMInfos.get(0).setType(true);
                    mReceiveMsg.add(receiveMeetIMInfos.get(0));
                }
                List<EventBadge> num = new ArrayList<>();
                num.add(new EventBadge(all));
                // TODO: 2018/3/7 通知界面更新 Badge
                EventBus.getDefault().post(new EventMessage(IDEventMessage.UpDate_BadgeNumber, num));
                break;
            case IDivMessage.QUERY_MEET_VEDIO://172.查询会议视频
                InterfaceMain.pbui_Type_MeetVideoDetailInfo result2 = (InterfaceMain.pbui_Type_MeetVideoDetailInfo) result;
                if (result2 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result2);
                    bundle.putParcelableArrayList("queryMeetVedio", arrayList);
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
