package com.pa.paperless.fragment.meeting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.pa.paperless.R;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.views.CustomSurfaceView;
import com.wind.myapplication.NativeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 *  视屏直播
 */

public class VideoFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private ListView mVideoLv;
    private List<String> mCameras;
    private SurfaceView mTopLeftVideo;
    private SurfaceView mTopRightVideo;
    private SurfaceView mBottomLeftVideo;
    private SurfaceView mBottomRightVideo;
    private Button mLookVideoBtn;
    private Button mStopVideoBtn;
    private Button mPushVideoBtn;
    private Button mProjectorVideoBtn;
//    private NativeUtil nativeUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_video, container, false);
        initView(inflate);
        initController();
        return inflate;
    }

    @Override
    protected void initController() {
        nativeUtil = NativeUtil.getInstance();
//        nativeUtil = new NativeUtil();
        nativeUtil.setCallListener(this);
    }

    private void initView(View inflate) {
        mCameras = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            mCameras.add(i + "号摄像头");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, mCameras);
        mVideoLv = (ListView) inflate.findViewById(R.id.video_lv);
        mVideoLv.setAdapter(adapter);
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
        switch (action){
            case 1:

                break;
        }
    }
}
