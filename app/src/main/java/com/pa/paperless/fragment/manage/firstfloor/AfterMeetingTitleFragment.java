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
import com.pa.paperless.fragment.manage.secondfloor.s.after.MeetingArchive_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.after.NotationToView_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.after.SginInCase_Fragment;
import com.pa.paperless.fragment.manage.secondfloor.s.after.VoteResults_Fragment;
import com.pa.paperless.fragment.meeting.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.utils.MyUtils.setViewBackground;

/**
 * Created by Administrator on 2017/11/16.
 * 会后整理 第一层按钮的Fragment
 */

public class AfterMeetingTitleFragment extends BaseFragment implements View.OnClickListener {


    private Button mSginInCase;
    private Button mNotationToView;
    private Button mVoteResults;
    private Button mMeetingArchive;

    private List<BaseFragment> mFragments;
    private List<Button> mBtns;
    private FragmentManager childFragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.after_metting, container, false);
        initView(inflate);
        initFragment();
        initButton();
        mSginInCase.performClick();
        return inflate;
    }

    private void initButton() {
        mBtns = new ArrayList<>();
        mBtns.add(mSginInCase);
        mBtns.add(mNotationToView);
        mBtns.add(mVoteResults);
        mBtns.add(mMeetingArchive);
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new SginInCase_Fragment());
        mFragments.add(new NotationToView_Fragment());
        mFragments.add(new VoteResults_Fragment());
        mFragments.add(new MeetingArchive_Fragment());
    }

    public void changeFragment(int index) {
        childFragmentManager = getChildFragmentManager();
        fragmentTransaction = childFragmentManager.beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            if (i == index) {
                if (!mFragments.get(i).isAdded()) {
                    fragmentTransaction.add(R.id.after_meeting_fl, mFragments.get(i));
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
        mSginInCase = (Button) inflate.findViewById(R.id.sgin_in_case);
        mNotationToView = (Button) inflate.findViewById(R.id.notation_to_view);
        mVoteResults = (Button) inflate.findViewById(R.id.vote_results);
        mMeetingArchive = (Button) inflate.findViewById(R.id.meeting_archive);

        mSginInCase.setOnClickListener(this);
        mNotationToView.setOnClickListener(this);
        mVoteResults.setOnClickListener(this);
        mMeetingArchive.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sgin_in_case:
                setViewBackground(0, mBtns);
                changeFragment(0);
                break;
            case R.id.notation_to_view:
                setViewBackground(1, mBtns);
                changeFragment(1);
                break;
            case R.id.vote_results:
                setViewBackground(2, mBtns);
                changeFragment(2);
                break;
            case R.id.meeting_archive:
                setViewBackground(3, mBtns);
                changeFragment(3);
                break;
        }
    }
}
