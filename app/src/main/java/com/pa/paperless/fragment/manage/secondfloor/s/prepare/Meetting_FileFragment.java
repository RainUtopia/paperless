package com.pa.paperless.fragment.manage.secondfloor.s.prepare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.pa.paperless.R;
import com.pa.paperless.activity.DirPermissionActivity;
import com.pa.paperless.fragment.meeting.BaseFragment;

/**
 * Created by Administrator on 2017/11/17.
 * 会前准备-会议资料
 */

public class Meetting_FileFragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mMeetfileRlLeft;
    private RecyclerView mMeetfileRlRight;
    private EditText mDirectoryEdt;
    private Button mMeetfileAdd;
    private Button mMeetfileAmend;
    private Button mMeetfileDelete;
    private Button mMeetfileDirectory;
    private LinearLayout mMeetPlace;
    private EditText mMeetfileNameEdt;
    private Button mRightAdd;
    private Button mRightAmend;
    private Button mRightDelete;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.meeting_file, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mMeetfileRlLeft = (RecyclerView) inflate.findViewById(R.id.meetfile_rl_left);
        mMeetfileRlRight = (RecyclerView) inflate.findViewById(R.id.meetfile_rl_right);
        mDirectoryEdt = (EditText) inflate.findViewById(R.id.directory_edt);
        mMeetfileAdd = (Button) inflate.findViewById(R.id.meetfile_add);
        mMeetfileAmend = (Button) inflate.findViewById(R.id.meetfile_amend);
        mMeetfileDelete = (Button) inflate.findViewById(R.id.meetfile_delete);
        mMeetfileDirectory = (Button) inflate.findViewById(R.id.meetfile_directory);
        mMeetPlace = (LinearLayout) inflate.findViewById(R.id.meet_place);
        mMeetfileNameEdt = (EditText) inflate.findViewById(R.id.meetfile_name_edt);
        mRightAdd = (Button) inflate.findViewById(R.id.right_add);
        mRightAmend = (Button) inflate.findViewById(R.id.right_amend);
        mRightDelete = (Button) inflate.findViewById(R.id.right_delete);

        mMeetfileAdd.setOnClickListener(this);
        mMeetfileAmend.setOnClickListener(this);
        mMeetfileDelete.setOnClickListener(this);
        mMeetfileDirectory.setOnClickListener(this);
        mRightAdd.setOnClickListener(this);
        mRightAmend.setOnClickListener(this);
        mRightDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meetfile_add:

                break;
            case R.id.meetfile_amend:

                break;
            case R.id.meetfile_delete:

                break;
            case R.id.meetfile_directory://目录权限
                startActivity(new Intent(this.getContext(), DirPermissionActivity.class));

                break;
            case R.id.right_add:

                break;
            case R.id.right_amend:

                break;
            case R.id.right_delete:

                break;
        }
    }
}

