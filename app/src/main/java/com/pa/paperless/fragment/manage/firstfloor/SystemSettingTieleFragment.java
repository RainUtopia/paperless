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
import com.pa.paperless.fragment.manage.secondfloor.s.setting.CommonlyParticipantsFragment;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.DeviceManagementFragment;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.MeetRoomManagementFragment;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.OtherSettingFragment;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.SeatArrangementFragment;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.SecretaryManagementFragment;
import com.pa.paperless.fragment.meeting.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.utils.MyUtils.setViewBackground;

/**
 * Created by Administrator on 2017/11/16.
 * 系统设置 第一层按钮的Fragment
 */

public class SystemSettingTieleFragment extends BaseFragment implements View.OnClickListener {
    private Button mDeviceManagement;
    private Button mMeetingRoomManagement;
    private Button mSeatingArrangement;
    private Button mSecretaryManagement;
    private Button mFrequentlyUsedAttendees;
    private Button mOtherSetting;
    private List<BaseFragment> mFragments;
    private List<Button> mBtns;
    private FragmentManager childFragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.sys_setting_btn, container, false);
        initView(inflate);
        initFragment();
        initButton();
        //默认点击
        mDeviceManagement.performClick();
        return inflate;
    }

    private void initFragment() {

        mFragments = new ArrayList<>();
        mFragments.add(new DeviceManagementFragment());
        mFragments.add(new MeetRoomManagementFragment());
        mFragments.add(new SeatArrangementFragment());
        mFragments.add(new SecretaryManagementFragment());
        mFragments.add(new CommonlyParticipantsFragment());
        mFragments.add(new OtherSettingFragment());

    }

    private void initButton() {
        mBtns = new ArrayList<>();
        mBtns.add(mDeviceManagement);
        mBtns.add(mMeetingRoomManagement);
        mBtns.add(mSeatingArrangement);
        mBtns.add(mSecretaryManagement);
        mBtns.add(mFrequentlyUsedAttendees);
        mBtns.add(mOtherSetting);
    }

    private void initView(View inflate) {
        mDeviceManagement = (Button) inflate.findViewById(R.id.device_management);
        mMeetingRoomManagement = (Button) inflate.findViewById(R.id.meeting_room_management);
        mSeatingArrangement = (Button) inflate.findViewById(R.id.seating_arrangement);
        mSecretaryManagement = (Button) inflate.findViewById(R.id.secretary_management);
        mFrequentlyUsedAttendees = (Button) inflate.findViewById(R.id.frequently_used_attendees);
        mOtherSetting = (Button) inflate.findViewById(R.id.other_setting);


        mDeviceManagement.setOnClickListener(this);
        mMeetingRoomManagement.setOnClickListener(this);
        mSeatingArrangement.setOnClickListener(this);
        mSecretaryManagement.setOnClickListener(this);
        mFrequentlyUsedAttendees.setOnClickListener(this);
        mOtherSetting.setOnClickListener(this);
    }


    public void changeFragment(int index) {
        childFragmentManager = getChildFragmentManager();
        fragmentTransaction = childFragmentManager.beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            if (i == index) {
                if (!mFragments.get(i).isAdded()) {
                    fragmentTransaction.add(R.id.content_fl, mFragments.get(i));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_management:
                setViewBackground(0,mBtns);
                changeFragment(0);
                break;
            case R.id.meeting_room_management:
                setViewBackground(1,mBtns);
                changeFragment(1);
                break;
            case R.id.seating_arrangement:
                setViewBackground(2,mBtns);
                changeFragment(2);
                break;
            case R.id.secretary_management:
                setViewBackground(3,mBtns);
                changeFragment(3);
                break;
            case R.id.frequently_used_attendees:
                setViewBackground(4,mBtns);
                changeFragment(4);
                break;
            case R.id.other_setting:
                setViewBackground(5,mBtns);
                changeFragment(5);
                break;
        }
    }
}
