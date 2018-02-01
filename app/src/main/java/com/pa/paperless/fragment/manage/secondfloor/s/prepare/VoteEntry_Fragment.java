package com.pa.paperless.fragment.manage.secondfloor.s.prepare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.pa.paperless.R;
import com.pa.paperless.fragment.meeting.BaseFragment;

/**
 * Created by Administrator on 2017/11/17.
 * 会前准备-投票录入
 */

public class VoteEntry_Fragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mVoteentryRl;
    private EditText mVoteContentEdt;
    private Spinner mSpinnerVote;
    private EditText mChooseOne;
    private EditText mChooseTwo;
    private EditText mChooseThree;
    private EditText mChooseFour;
    private Button mVoteAdd;
    private Button mVoteAmend;
    private Button mVoteDelete;
    private Button mVoteFromExcel;
    private Button mVoteToExcel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.voteentry, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mVoteentryRl = (RecyclerView) inflate.findViewById(R.id.voteentry_rl);
        mVoteContentEdt = (EditText) inflate.findViewById(R.id.vote_content_edt);
        mSpinnerVote = (Spinner) inflate.findViewById(R.id.spinner_vote);
        mChooseOne = (EditText) inflate.findViewById(R.id.choose_one);
        mChooseTwo = (EditText) inflate.findViewById(R.id.choose_two);
        mChooseThree = (EditText) inflate.findViewById(R.id.choose_three);
        mChooseFour = (EditText) inflate.findViewById(R.id.choose_four);
        mVoteAdd = (Button) inflate.findViewById(R.id.vote_add);
        mVoteAmend = (Button) inflate.findViewById(R.id.vote_amend);
        mVoteDelete = (Button) inflate.findViewById(R.id.vote_delete);
        mVoteFromExcel = (Button) inflate.findViewById(R.id.vote_from_excel);
        mVoteToExcel = (Button) inflate.findViewById(R.id.vote_to_excel);

        mVoteAdd.setOnClickListener(this);
        mVoteAmend.setOnClickListener(this);
        mVoteDelete.setOnClickListener(this);
        mVoteFromExcel.setOnClickListener(this);
        mVoteToExcel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vote_add:

                break;
            case R.id.vote_amend:

                break;
            case R.id.vote_delete:

                break;
            case R.id.vote_from_excel:

                break;
            case R.id.vote_to_excel:

                break;
        }
    }
}