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
import com.pa.paperless.fragment.manage.secondfloor.s.prepare.Attendees_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.prepare.CameraManagement_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.prepare.MeetingFunction_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.prepare.MeettingAgenda_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.prepare.MeettingManagement_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.prepare.Meetting_FileFragment;
import com.pa.paperless.fragment.manage.secondfloor.s.prepare.SeatBinding_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.prepare.TabletShow_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.prepare.VoteEntry_Fragment;
import com.pa.paperless.fragment.meeting.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.utils.MyUtils.setViewBackground;

/**
 * Created by Administrator on 2017/11/16.
 * 会前准备 第一层按钮的Fragment
 */

public class MeetingPrepareFragment extends BaseFragment implements View.OnClickListener {
    private Button mMeetingManagement;
    private Button mMeetingAgenda;
    private Button mAttendees;
    private Button mMeetingFile;
    private Button mCameraManagenment;
    private Button mVoteEntry;
    private Button mSeatBinding;
    private Button mStandsShow;
    private Button mMeetingFunctional;
    private List<BaseFragment> mFragments;
    private List<Button> mBtns;
    private FragmentManager childFragmentManager;
    private FragmentTransaction fragmentTransaction;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.meet_prepare_btn, container, false);
        initView(inflate);
        initFragment();
        initButton();
        //默认点击
        mMeetingManagement.performClick();
        return inflate;
    }

    private void initButton() {
        mBtns = new ArrayList<>();
        mBtns.add(mMeetingManagement);
        mBtns.add(mMeetingAgenda);
        mBtns.add(mAttendees);
        mBtns.add(mMeetingFile);
        mBtns.add(mCameraManagenment);
        mBtns.add(mVoteEntry);
        mBtns.add(mSeatBinding);
        mBtns.add(mStandsShow);
        mBtns.add(mMeetingFunctional);
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new MeettingManagement_Fragment());
        mFragments.add(new MeettingAgenda_Fragment());
        mFragments.add(new Attendees_Fragment());
        mFragments.add(new Meetting_FileFragment());
        mFragments.add(new CameraManagement_Fragment());
        mFragments.add(new VoteEntry_Fragment());
        mFragments.add(new SeatBinding_Fragment());
        mFragments.add(new TabletShow_Fragment());
        mFragments.add(new MeetingFunction_Fragment());
    }

    public void changeFragment(int index) {
        childFragmentManager = getChildFragmentManager();
        fragmentTransaction = childFragmentManager.beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            if (i == index) {
                if (!mFragments.get(i).isAdded()) {
                    fragmentTransaction.add(R.id.meeting_prepare_fl, mFragments.get(i));
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
        mMeetingManagement = (Button) inflate.findViewById(R.id.meeting_management);
        mMeetingAgenda = (Button) inflate.findViewById(R.id.meeting_agenda);
        mAttendees = (Button) inflate.findViewById(R.id.attendees);
        mMeetingFile = (Button) inflate.findViewById(R.id.meeting_file);
        mCameraManagenment = (Button) inflate.findViewById(R.id.camera_managenment);
        mVoteEntry = (Button) inflate.findViewById(R.id.vote_entry);
        mSeatBinding = (Button) inflate.findViewById(R.id.seat_binding);
        mStandsShow = (Button) inflate.findViewById(R.id.stands_show);
        mMeetingFunctional = (Button) inflate.findViewById(R.id.meeting_functional);

        mMeetingManagement.setOnClickListener(this);
        mMeetingAgenda.setOnClickListener(this);
        mAttendees.setOnClickListener(this);
        mMeetingFile.setOnClickListener(this);
        mCameraManagenment.setOnClickListener(this);
        mVoteEntry.setOnClickListener(this);
        mSeatBinding.setOnClickListener(this);
        mStandsShow.setOnClickListener(this);
        mMeetingFunctional.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_management:
                setViewBackground(0,mBtns);
                changeFragment(0);
                break;
            case R.id.meeting_agenda:
                setViewBackground(1,mBtns);
                changeFragment(1);
                break;
            case R.id.attendees:
                setViewBackground(2,mBtns);
                changeFragment(2);
                break;
            case R.id.meeting_file:
                setViewBackground(3,mBtns);
                changeFragment(3);
                break;
            case R.id.camera_managenment:
                setViewBackground(4,mBtns);
                changeFragment(4);
                break;
            case R.id.vote_entry:
                setViewBackground(5,mBtns);
                changeFragment(5);
                break;
            case R.id.seat_binding:
                setViewBackground(6,mBtns);
                changeFragment(6);
                break;
            case R.id.stands_show:
                setViewBackground(7,mBtns);
                changeFragment(7);
                break;
            case R.id.meeting_functional:
                setViewBackground(8,mBtns);
                changeFragment(8);
                break;
        }
    }
}
