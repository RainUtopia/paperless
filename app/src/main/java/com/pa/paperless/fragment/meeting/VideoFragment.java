package com.pa.paperless.fragment.meeting;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
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
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.pa.paperless.R;
import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.adapter.VideoItemAdapter;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.VideoInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.ItemClickListener;
import com.wind.myapplication.ScreenRecorder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MEDIA_PROJECTION_SERVICE;
import static com.pa.paperless.activity.MeetingActivity.nativeUtil;
import static com.pa.paperless.adapter.VideoItemAdapter.VideoStreamChecked;


/**
 * Created by Administrator on 2017/10/31.
 * 视屏直播
 */

public class VideoFragment extends BaseFragment implements View.OnClickListener {


    private RecyclerView mVideoLv;
    private Button mLookVideoBtn;
    private Button mStopVideoBtn;
    private Button mPushVideoBtn;
    private Button mProjectorVideoBtn;
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
    private List<Integer> res;
    private List<Integer> ids;
    private List<VideoInfo> videoList;

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
        Log.d(TAG, "VideoFragment.onActivityResult 169行:   --->>> " + recorder.toString());
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
        cxt = getContext();
        try {
            nativeUtil.queryMeetVedio();
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
            case IDEventF.post_meet_video://收到会议视频数据
                InterfaceVideo.pbui_Type_MeetVideoDetailInfo object = (InterfaceVideo.pbui_Type_MeetVideoDetailInfo) message.getObject();
                List<InterfaceVideo.pbui_Item_MeetVideoDetailInfo> itemList = object.getItemList();
                videoList = new ArrayList<>();
                for (int i = 0; i < itemList.size(); i++) {
                    videoList.add(new VideoInfo(itemList.get(i)));
                }
                if (videoList != null) {
                    //查询设备信息
                    nativeUtil.queryDeviceInfo();
                }
                break;
            case IDEventF.post_devInfo_video://收到设备数据
                List<DeviceInfo> deviceInfos = (List<DeviceInfo>) message.getObject();
                if (videoList != null && deviceInfos != null) {
                    for (int i = 0; i < videoList.size(); i++) {
                        for (int j = 0; j < deviceInfos.size(); j++) {
                            if (videoList.get(i).getVideoInfo().getDeviceid() == deviceInfos.get(j).getDevId()) {
                                videoList.get(i).setName(deviceInfos.get(j).getDevName());
                            }
                        }
                    }
                }
                /** **** **  每次更新时都同步索引  ** **** **/
                if (VideoStreamChecked == null) {
                    VideoStreamChecked = new ArrayList<>();
                } else {
                    VideoStreamChecked.clear();
                }
                for (int j = 0; j < videoList.size(); j++) {
                    VideoStreamChecked.add(false);
                }
                if (adapter == null) {
                    adapter = new VideoItemAdapter(cxt, videoList);
                    mVideoLv.setLayoutManager(new LinearLayoutManager(getContext()));
                    mVideoLv.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();
                adapter.setItemListener(new ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int posion) {
                        Log.e(TAG, "VideoFragment.onItemClick :  选中了索引为 --->>> " + posion);
                        adapter.setCheckedId(posion);
                    }
                });

                break;
            case IDEventMessage.Meet_vedio_changeInform://会议视频变更通知
                nativeUtil.queryMeetVedio();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopRecord();
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
            case R.id.look_video_btn://观看直播
                res = new ArrayList<>();
                ids = new ArrayList<>();
                List<VideoInfo> checkedItemInfo = adapter.getCheckedItemInfo();
                VideoInfo videoInfo = checkedItemInfo.get(0);
                int deviceid = videoInfo.getVideoInfo().getDeviceid();
                int subid = videoInfo.getVideoInfo().getSubid();
                Log.e(TAG, "VideoFragment.onClick :  观看触发 subid -->" + subid + ", deviceid -->" + deviceid);
                res.add(0);
                ids.add(MeetingActivity.getDevId());
                nativeUtil.streamPlay(deviceid, subid, 0, res, ids);
                break;
            case R.id.stop_video_btn://停止观看
                if (res != null && ids != null) {
                    Log.e(TAG, "VideoFragment.onClick :  停止播放触发 --> ");
                    nativeUtil.stopResourceOperate(res, ids);
                }
                break;
            case R.id.push_video_btn://推送视屏
//                if (stopRecord()) {
//                    Toast.makeText(getActivity(), "屏幕录制已停止", Toast.LENGTH_LONG).show();
//                } else {
//                    // 2: 发起屏幕捕捉请求
//                    Intent intent = null;
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                        intent = manager.createScreenCaptureIntent();
//                    }
//                    startActivityForResult(intent, REQUEST_CODE);
//                }
                List<VideoInfo> checkedItemInfo1 = adapter.getCheckedItemInfo();
                if (checkedItemInfo1 != null) {
                    EventBus.getDefault().post(new EventMessage(IDEventMessage.open_screenspop, checkedItemInfo1));
                } else {
                    Toast.makeText(cxt, "请先选择", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.projector_video_btn://投影视屏
                List<VideoInfo> checkedItemInfo2 = adapter.getCheckedItemInfo();
                if (checkedItemInfo2 != null) {
                    EventBus.getDefault().post(new EventMessage(IDEventMessage.open_projector, checkedItemInfo2));
                } else {
                    Toast.makeText(cxt, "请先选择", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            Log.e(TAG, "VideoFragment.onHiddenChanged :  视屏直播显示状态 --->>> ");
            try {
                /** **** **  查询会议视频  ** **** **/
                nativeUtil.queryMeetVedio();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }
}
