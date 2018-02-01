package com.pa.paperless.fragment.manage.firstfloor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pa.paperless.R;
import com.pa.paperless.fragment.manage.secondfloor.s.meetcontrol.CameraControl_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.meetcontrol.DeviceControl_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.meetcontrol.FourScreenManagement_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.meetcontrol.MeetingCommunicate_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.meetcontrol.ScreenManagement_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.meetcontrol.VotingManagement_Fragment;
import com.pa.paperless.fragment.meeting.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.utils.MyUtils.setViewBackground;

/**
 * Created by Administrator on 2017/11/16.
 * 会中控制 第一层按钮的Fragment
 */

public class MeetingControlTitleFragment extends BaseFragment implements View.OnClickListener {

    private Button mDeviceControl;
    private Button mVoteManagement;
    private Button mMeetingCommunication;
    private Button mCameraControl;
    private Button mFourPoints;
    private Button mSplitScreenManagement;
    private List<BaseFragment> mFragments;
    private List<Button> mBtns;
    private FragmentManager childFragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.in_control, container, false);
        initView(inflate);
        initFragment();
        initButton();
        mDeviceControl.performClick();
        return inflate;
    }

    private void initButton() {
        mBtns = new ArrayList<>();
        mBtns.add(mDeviceControl);
        mBtns.add(mVoteManagement);
        mBtns.add(mMeetingCommunication);
        mBtns.add(mCameraControl);
        mBtns.add(mFourPoints);
        mBtns.add(mSplitScreenManagement);
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new DeviceControl_Fragment());
        mFragments.add(new VotingManagement_Fragment());
        mFragments.add(new MeetingCommunicate_Fragment());
        mFragments.add(new CameraControl_Fragment());
        mFragments.add(new FourScreenManagement_Fragment());
        mFragments.add(new ScreenManagement_Fragment());
    }

    public void changeFragment(int index) {
        childFragmentManager = getChildFragmentManager();
        fragmentTransaction = childFragmentManager.beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            if (i == index) {
                if (!mFragments.get(i).isAdded()) {
                    fragmentTransaction.add(R.id.in_control_fl, mFragments.get(i));
                    fragmentTransaction.show(mFragments.get(i));
                } else {
                    fragmentTransaction.show(mFragments.get(i));
                }
            } else {
                if (mFragments.get(i).isAdded()) {
                    fragmentTransaction.hide(mFragments.get(i));
                }
            }
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void initView(View inflate) {
        mDeviceControl = (Button) inflate.findViewById(R.id.device_control);
        mVoteManagement = (Button) inflate.findViewById(R.id.vote_management);
        mMeetingCommunication = (Button) inflate.findViewById(R.id.meeting_communication);
        mCameraControl = (Button) inflate.findViewById(R.id.camera_control);
        mFourPoints = (Button) inflate.findViewById(R.id.four_points);
        mSplitScreenManagement = (Button) inflate.findViewById(R.id.split_screen_management);

        mDeviceControl.setOnClickListener(this);
        mVoteManagement.setOnClickListener(this);
        mMeetingCommunication.setOnClickListener(this);
        mCameraControl.setOnClickListener(this);
        mFourPoints.setOnClickListener(this);
        mSplitScreenManagement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_control:
                setViewBackground(0, mBtns);
                changeFragment(0);
                break;
            case R.id.vote_management:
                setViewBackground(1, mBtns);
                changeFragment(1);
                break;
            case R.id.meeting_communication:
                setViewBackground(2, mBtns);
                changeFragment(2);
                break;
            case R.id.camera_control:
                setViewBackground(3, mBtns);
                changeFragment(3);
                break;
            case R.id.four_points:
                setViewBackground(4, mBtns);
                changeFragment(4);
                break;
            case R.id.split_screen_management:
                setViewBackground(5, mBtns);
                changeFragment(5);
                break;
        }
    }
}
