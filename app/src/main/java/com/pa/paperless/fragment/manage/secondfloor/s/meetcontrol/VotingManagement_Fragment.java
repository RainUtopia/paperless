package com.pa.paperless.fragment.manage.secondfloor.s.meetcontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pa.paperless.R;
import com.pa.paperless.fragment.meeting.BaseFragment;

/**
 * Created by Administrator on 2017/11/17.
 * 会中控制-投票管理
 */

public class VotingManagement_Fragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mVotingRl;
    private Button mVotingStartVote;
    private Button mVotingStartAllvote;
    private Button mVotingEndVote;
    private Button mExportToExcel;
    private Button mLookPillar;
    private Button mLookStockInfo;
    private Button mStartProjection;
    private Button mStopProjection;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.voting_management, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mVotingRl = (RecyclerView) inflate.findViewById(R.id.voting_rl);
        mVotingStartVote = (Button) inflate.findViewById(R.id.voting_start_vote);
        mVotingStartAllvote = (Button) inflate.findViewById(R.id.voting_start_allvote);
        mVotingEndVote = (Button) inflate.findViewById(R.id.voting_end_vote);
        mExportToExcel = (Button) inflate.findViewById(R.id.export_to_excel);
        mLookPillar = (Button) inflate.findViewById(R.id.look_pillar);
        mLookStockInfo = (Button) inflate.findViewById(R.id.look_stock_info);
        mStartProjection = (Button) inflate.findViewById(R.id.start_projection);
        mStopProjection = (Button) inflate.findViewById(R.id.stop_projection);

        mVotingStartVote.setOnClickListener(this);
        mVotingStartAllvote.setOnClickListener(this);
        mVotingEndVote.setOnClickListener(this);
        mExportToExcel.setOnClickListener(this);
        mLookPillar.setOnClickListener(this);
        mLookStockInfo.setOnClickListener(this);
        mStartProjection.setOnClickListener(this);
        mStopProjection.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.voting_start_vote:

                break;
            case R.id.voting_start_allvote:

                break;
            case R.id.voting_end_vote:

                break;
            case R.id.export_to_excel:

                break;
            case R.id.look_pillar:

                break;
            case R.id.look_stock_info:

                break;
            case R.id.start_projection:

                break;
            case R.id.stop_projection:

                break;
        }
    }
}
