package com.pa.paperless.fragment.manage.secondfloor.s.prepare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.zhy.android.percent.support.PercentLinearLayout;

/**
 * Created by Administrator on 2017/11/17.
 * 会前准备-摄像机管理
 */

public class CameraManagement_Fragment extends BaseFragment implements View.OnClickListener {


    private TextView mRoomItemNumber;
    private RecyclerView mCameraLeftRl;
    private Button mCameraAddBtn;
    private Button mCameraDeleteBtn;
    private RecyclerView mCameraRightRl;
    private PercentLinearLayout mCameraPlace;
    private EditText mCameraNameEdt;
    private Button mCameraAmend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.camera_management, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mRoomItemNumber = (TextView) inflate.findViewById(R.id.room_item_number);
        mCameraLeftRl = (RecyclerView) inflate.findViewById(R.id.camera_left_rl);
        mCameraAddBtn = (Button) inflate.findViewById(R.id.camera_add_btn);
        mCameraDeleteBtn = (Button) inflate.findViewById(R.id.camera_delete_btn);
        mCameraRightRl = (RecyclerView) inflate.findViewById(R.id.camera_right_rl);
        mCameraPlace = (PercentLinearLayout) inflate.findViewById(R.id.camera_place);
        mCameraNameEdt = (EditText) inflate.findViewById(R.id.camera_name_edt);
        mCameraAmend = (Button) inflate.findViewById(R.id.camera_amend);

        mCameraAddBtn.setOnClickListener(this);
        mCameraDeleteBtn.setOnClickListener(this);
        mCameraAmend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_add_btn:

                break;
            case R.id.camera_delete_btn:

                break;
            case R.id.camera_amend:

                break;
        }
    }
}