package com.pa.paperless.fragment.manage.secondfloor.s.meetcontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.pa.paperless.R;
import com.pa.paperless.fragment.meeting.BaseFragment;

/**
 * Created by Administrator on 2017/11/17.
 * 分屏管理
 */

public class ScreenManagement_Fragment extends BaseFragment implements View.OnClickListener {
    private CheckBox mDeviceCb;
    private CheckBox mGoalScreen;
    private RecyclerView mScreenLeftRl;
    private RecyclerView mScreenMidRl;
    private RecyclerView mScreenRightRl;
    private Button mPreview;
    private Button mStartScreen;
    private Button mStopScreen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.screen_management, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mDeviceCb = (CheckBox) inflate.findViewById(R.id.device_cb);
        mGoalScreen = (CheckBox) inflate.findViewById(R.id.goal_screen);
        mScreenLeftRl = (RecyclerView) inflate.findViewById(R.id.screen_left_rl);
        mScreenMidRl = (RecyclerView) inflate.findViewById(R.id.screen_mid_rl);
        mScreenRightRl = (RecyclerView) inflate.findViewById(R.id.screen_right_rl);
        mPreview = (Button) inflate.findViewById(R.id.preview);
        mStartScreen = (Button) inflate.findViewById(R.id.start_screen);
        mStopScreen = (Button) inflate.findViewById(R.id.stop_screen);

        mPreview.setOnClickListener(this);
        mStartScreen.setOnClickListener(this);
        mStopScreen.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preview:

                break;
            case R.id.start_screen:

                break;
            case R.id.stop_screen:

                break;
        }
    }
}
