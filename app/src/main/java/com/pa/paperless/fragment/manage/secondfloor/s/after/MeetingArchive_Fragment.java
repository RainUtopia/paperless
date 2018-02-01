package com.pa.paperless.fragment.manage.secondfloor.s.after;

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
 * 会后整理-会议归档
 */

public class MeetingArchive_Fragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mArchiveTopRl;
    private RecyclerView mArchiveBottomRl;
    private CheckBox mEncryptionCb;
    private Button mStartArchiveBtn;
    private Button mCancelArchiveBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.meet_archive, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mArchiveTopRl = (RecyclerView) inflate.findViewById(R.id.archive_top_rl);
        mArchiveBottomRl = (RecyclerView) inflate.findViewById(R.id.archive_bottom_rl);
        mEncryptionCb = (CheckBox) inflate.findViewById(R.id.encryption_cb);
        mStartArchiveBtn = (Button) inflate.findViewById(R.id.start_archive_btn);
        mCancelArchiveBtn = (Button) inflate.findViewById(R.id.cancel_archive_btn);

        mStartArchiveBtn.setOnClickListener(this);
        mCancelArchiveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_archive_btn:

                break;
            case R.id.cancel_archive_btn:

                break;
        }
    }
}
