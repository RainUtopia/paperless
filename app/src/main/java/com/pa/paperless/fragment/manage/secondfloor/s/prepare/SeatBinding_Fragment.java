package com.pa.paperless.fragment.manage.secondfloor.s.prepare;

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
 * 会前准备-座位绑定
 */

public class SeatBinding_Fragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mSeatbindingLeftRl;
    private Button mSeatbinding;
    private Button mRemoveBind;
    private Button mSeatbingSave;
    private Button mSeatbindingFromExcel;
    private Button mSeatbindingToExcel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.seatbinding, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mSeatbindingLeftRl = (RecyclerView) inflate.findViewById(R.id.seatbinding_left_rl);
        mSeatbinding = (Button) inflate.findViewById(R.id.seatbinding);
        mRemoveBind = (Button) inflate.findViewById(R.id.remove_bind);
        mSeatbingSave = (Button) inflate.findViewById(R.id.seatbing_save);
        mSeatbindingFromExcel = (Button) inflate.findViewById(R.id.seatbinding_from_excel);
        mSeatbindingToExcel = (Button) inflate.findViewById(R.id.seatbinding_to_excel);

        mSeatbinding.setOnClickListener(this);
        mRemoveBind.setOnClickListener(this);
        mSeatbingSave.setOnClickListener(this);
        mSeatbindingFromExcel.setOnClickListener(this);
        mSeatbindingToExcel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seatbinding:

                break;
            case R.id.remove_bind:

                break;
            case R.id.seatbing_save:

                break;
            case R.id.seatbinding_from_excel:

                break;
            case R.id.seatbinding_to_excel:

                break;
        }
    }
}