package com.pa.paperless.fragment.manage.secondfloor.s.meetcontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.pa.paperless.R;
import com.pa.paperless.fragment.meeting.BaseFragment;

/**
 * Created by Administrator on 2017/11/17.
 * 会中控制-摄像机控制
 */

public class CameraControl_Fragment extends BaseFragment implements View.OnClickListener {
    private SurfaceView mCameraTopLeftvideo;
    private SurfaceView mCameraTopRightvideo;
    private SurfaceView mCameraBottomLeftvideo;
    private SurfaceView mCameraBottomRightvideo;
    private ListView mCameraVideoLv;
    private Button mLookVideoCamera;
    private Button mStopVideoCamera;
    private Button mPushVideoCamera;
    private Button mProjectorVideoCamera;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.camera_control, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mCameraTopLeftvideo = (SurfaceView) inflate.findViewById(R.id.camera_top_leftvideo);
        mCameraTopRightvideo = (SurfaceView) inflate.findViewById(R.id.camera_top_rightvideo);
        mCameraBottomLeftvideo = (SurfaceView) inflate.findViewById(R.id.camera_bottom_leftvideo);
        mCameraBottomRightvideo = (SurfaceView) inflate.findViewById(R.id.camera_bottom_rightvideo);
        mCameraVideoLv = (ListView) inflate.findViewById(R.id.camera_video_lv);
        mLookVideoCamera = (Button) inflate.findViewById(R.id.look_video_camera);
        mStopVideoCamera = (Button) inflate.findViewById(R.id.stop_video_camera);
        mPushVideoCamera = (Button) inflate.findViewById(R.id.push_video_camera);
        mProjectorVideoCamera = (Button) inflate.findViewById(R.id.projector_video_camera_);

        mLookVideoCamera.setOnClickListener(this);
        mStopVideoCamera.setOnClickListener(this);
        mPushVideoCamera.setOnClickListener(this);
        mProjectorVideoCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.look_video_camera:

                break;
            case R.id.stop_video_camera:

                break;
            case R.id.push_video_camera:

                break;
            case R.id.projector_video_camera_:

                break;
        }
    }
}
