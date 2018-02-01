package com.pa.paperless.fragment.meeting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.adapter.AgendaAdapter;
import com.pa.paperless.bean.AgendContext;
import com.pa.paperless.event.EventAgenda;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.controller.MeetController;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.DateUtil;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/1.
 * 公告议程-议程
 */

public class AgendaFragment extends BaseFragment implements CallListener {

    private ListView agenda_lv;
    private NativeUtil nativeUtil;
    List<AgendContext> mData;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IDivMessage.QUERY_AGENDA:
                    ArrayList agenda = msg.getData().getParcelableArrayList("agenda");
                    if (agenda != null) {
                        mData.clear();
                        InterfaceMain.pbui_meetAgenda o = (InterfaceMain.pbui_meetAgenda) agenda.get(0);
                        //议程类型 参见Pb_AgendaType
                        int agendatype = o.getAgendatype();
                        //媒体ID
                        int mediaid = o.getMediaid();
                        //议程文本
                        String text = new String(o.getText().toByteArray());
                        Log.e("MyLog","AgendaFragment.handleMessage:  agendatype --->>> "+agendatype+"   mediaid : "+mediaid+"   text:  "+text);
                        //时间轴式
                        for (int i = 0; i < o.getItemCount(); i++) {
                            InterfaceMain.pbui_ItemAgendaTimeInfo item = o.getItem(i);
                            int agendaid = item.getAgendaid();  //议程ID
                            int status = item.getStatus();      //议程状态
                            int dirid = item.getDirid();        //绑定目录ID
                            long startutctime = item.getStartutctime();
                            long endutctime = item.getEndutctime();
                            String sTime = DateUtil.getTime(startutctime);
                            String eTime = DateUtil.getTime(endutctime);
                            String time = sTime + "-" + eTime;  // 8:00-9:00
                            String descText = new String(item.getDesctext().toByteArray());//描述内容
                        }
                        mData.add(new AgendContext(mediaid+"", text));
                        //设置数据
                        setData(mData);
                        agendaAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };
    private AgendaAdapter agendaAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.agenda, container, false);
        initController();
        initView(inflate);
        mData = new ArrayList<>();
        setData(mData);
        try {
            //查询议程 -- 获取议程信息
            nativeUtil.queryAgenda();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventAgenda(EventAgenda eventAgenda) {
        Log.e("MyLog","AgendaFragment.getEventAgenda:  查询议程 EventBus --->>> ");
        nativeUtil = NativeUtil.getInstance();
        try {
            nativeUtil.queryAgenda();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initController() {
        mController = new MeetController(getContext());
        mController.setIModelChangeListener(this);
        nativeUtil = NativeUtil.getInstance();
        nativeUtil.setCallListener(this);
    }

    private void initView(View inflate) {
        agenda_lv = (ListView) inflate.findViewById(R.id.agenda_lv);
    }

    public void setData(List<AgendContext> data) {
        Log.e("MyLog", "AgendaFragment.setData:  获取到了议程数据 --->>> ");
        agendaAdapter = new AgendaAdapter(getContext(), data);
        agenda_lv.setAdapter(agendaAdapter);
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_AGENDA:
                InterfaceMain.pbui_meetAgenda agenda = (InterfaceMain.pbui_meetAgenda) result;
                if (agenda != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(agenda);
                    bundle.putParcelableArrayList("agenda", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }
}
