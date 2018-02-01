package com.pa.paperless.fragment.manage.secondfloor.s.prepare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pa.paperless.R;
import com.pa.paperless.fragment.meeting.BaseFragment;

/**
 * Created by Administrator on 2017/11/17.
 * 会前准备-会议功能
 */

public class MeetingFunction_Fragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.meetint_function, container, false);
        return inflate;
    }
}