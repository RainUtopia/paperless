package com.pa.paperless.fragment.manage.secondfloor.s.after;

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
 * 投票结果
 */

public class VoteResults_Fragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mVoteResultsRl;
    private Button mResultsExport;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.vote_results, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mVoteResultsRl = (RecyclerView) inflate.findViewById(R.id.vote_results_rl);
        mResultsExport = (Button) inflate.findViewById(R.id.results_export);

        mResultsExport.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.results_export:

                break;
        }
    }
}
