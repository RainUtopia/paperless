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
 * 会中控制-设备控制
 */

public class DeviceControl_Fragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mDeviceControlRl;
    private Button mDeviceControlRise;
    private Button mDeviceControlStop;
    private Button mDeviceControlDown;
    private Button mDeviceControlRiseBoot;
    private Button mDeviceControlRestart;
    private Button mDeviceControlShutdown;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.device_control, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mDeviceControlRl = (RecyclerView) inflate.findViewById(R.id.device_control_rl);
        mDeviceControlRise = (Button) inflate.findViewById(R.id.device_control_rise);
        mDeviceControlStop = (Button) inflate.findViewById(R.id.device_control_stop);
        mDeviceControlDown = (Button) inflate.findViewById(R.id.device_control_down);
        mDeviceControlRiseBoot = (Button) inflate.findViewById(R.id.device_control_rise_boot);
        mDeviceControlRestart = (Button) inflate.findViewById(R.id.device_control_restart);
        mDeviceControlShutdown = (Button) inflate.findViewById(R.id.device_control_shutdown);

        mDeviceControlRise.setOnClickListener(this);
        mDeviceControlStop.setOnClickListener(this);
        mDeviceControlDown.setOnClickListener(this);
        mDeviceControlRiseBoot.setOnClickListener(this);
        mDeviceControlRestart.setOnClickListener(this);
        mDeviceControlShutdown.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_control_rise:

                break;
            case R.id.device_control_stop:

                break;
            case R.id.device_control_down:

                break;
            case R.id.device_control_rise_boot:

                break;
            case R.id.device_control_restart:

                break;
            case R.id.device_control_shutdown:

                break;
        }
    }
}
