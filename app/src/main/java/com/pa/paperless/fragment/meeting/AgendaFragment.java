package com.pa.paperless.fragment.meeting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pa.paperless.R;
import com.pa.paperless.bean.AgendContext;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import static com.pa.paperless.activity.MeetingActivity.nativeUtil;

/**
 * Created by Administrator on 2017/11/1.
 * 公告议程-议程
 */

public class AgendaFragment extends BaseFragment {
    private final String TAG = "AgendaFragment-->";
    private TextView agenda_tv;
    List<AgendContext> mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.agenda, container, false);
        agenda_tv = (TextView) inflate.findViewById(R.id.agenda_text);
        mData = new ArrayList<>();
        EventBus.getDefault().register(this);
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
            case IDEventF.post_agenda:
                String text = (String) message.getObject();
                Log.e(TAG, "AgendaFragment.getEventMessage :  议程界面收到文本信息 --->>> " + text);
                agenda_tv.setText(text);
                break;
            case IDEventMessage.AGENDA_CHANGE_INFO://议程变更通知
                nativeUtil.queryAgenda();
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
