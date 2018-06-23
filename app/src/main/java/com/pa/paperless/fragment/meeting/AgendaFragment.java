package com.pa.paperless.fragment.meeting;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.pa.paperless.R;
import com.pa.paperless.bean.AgendContext;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.service.NativeService.nativeUtil;

/**
 * Created by Administrator on 2017/11/1.
 * 公告议程-议程
 */

public class AgendaFragment extends BaseFragment {
    private TextView agenda_tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.agenda, container, false);
        agenda_tv = (TextView) inflate.findViewById(R.id.agenda_text);
        try {
            //查询议程 -- 获取议程信息
            nativeUtil.queryAgenda();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.agenda_info://收到议程信息
                InterfaceAgenda.pbui_meetAgenda text = (InterfaceAgenda.pbui_meetAgenda) message.getObject();
                agenda_tv.setText(MyUtils.getBts(text.getText()));
                break;
            case IDEventMessage.AGENDA_CHANGE_INFO://议程变更通知
                nativeUtil.queryAgenda();
                break;
        }
    }

    @Override
    public void onDestroy() {
        Log.i("F_life", "AgendaFragment.onDestroy :   --> ");
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        Log.i("F_life", "AgendaFragment.onAttach :   --> ");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "AgendaFragment.onCreate :   --> ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "AgendaFragment.onActivityCreated :   --> ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i("F_life", "AgendaFragment.onStart :   --> ");
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onResume() {
        try {
            //查询议程 -- 获取议程信息
            nativeUtil.queryAgenda();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        Log.i("F_life", "AgendaFragment.onResume :   --> ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("F_life", "AgendaFragment.onPause :   --> ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("F_life", "AgendaFragment.onStop :   --> ");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("F_life", "AgendaFragment.onDestroyView :   --> ");
        super.onDestroyView();
    }


    @Override
    public void onDetach() {
        Log.i("F_life", "AgendaFragment.onDetach :   --> ");
        super.onDetach();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i("F_life", "AgendaFragment.onHiddenChanged :   --> " + hidden);
        if(!hidden){
            try {
                //查询议程 -- 获取议程信息
                nativeUtil.queryAgenda();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }
}
