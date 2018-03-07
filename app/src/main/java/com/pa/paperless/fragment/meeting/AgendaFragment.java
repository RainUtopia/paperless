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
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.adapter.AgendaAdapter;
import com.pa.paperless.bean.AgendContext;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventAgenda;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.controller.MeetController;
import com.pa.paperless.event.EventBadge;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.DateUtil;
import com.pa.paperless.utils.Dispose;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.mBadge;
import static com.pa.paperless.activity.MeetingActivity.mReceiveMsg;

/**
 * Created by Administrator on 2017/11/1.
 * 公告议程-议程
 */

public class AgendaFragment extends BaseFragment implements CallListener {

    private NativeUtil nativeUtil;
    private ListView agenda_lv;
    List<AgendContext> mData;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IDivMessage.RECEIVE_MEET_IMINFO://收到新的会议交流信息
                    Log.e("MyLog","AgendaFragment.handleMessage 52行:  处理消息 --->>> ");
                    ArrayList resultNewMessage = msg.getData().getParcelableArrayList("resultNewMessage");
                    InterfaceMain2.pbui_Type_MeetIM newMessage = (InterfaceMain2.pbui_Type_MeetIM) resultNewMessage.get(0);
                    List<ReceiveMeetIMInfo> newMsglist = Dispose.ReceiveMeetIMinfo(newMessage);
                    for (int i = 0; i < newMsglist.size(); i++) {
                        ReceiveMeetIMInfo receiveMeetIMInfo = newMsglist.get(i);
                        //消息类型
                        int msgtype = receiveMeetIMInfo.getMsgtype();
                        int role1 = receiveMeetIMInfo.getRole();
                        int memberid = receiveMeetIMInfo.getMemberid();
                        String newMsg = receiveMeetIMInfo.getMsg();
                        long utcsecond = receiveMeetIMInfo.getUtcsecond();
                        //设置成接收到的消息
                        receiveMeetIMInfo.setType(true);
                    }
                    break;
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
            case IDivMessage.RECEIVE_MEET_IMINFO: //收到会议消息
                Log.e("MyLog", "SigninFragment.callListener 296行:  收到会议消息 --->>> ");
                InterfaceMain2.pbui_Type_MeetIM receiveMsg = (InterfaceMain2.pbui_Type_MeetIM) result;
                //获取之前的未读消息个数
                int badgeNumber1 = mBadge.getBadgeNumber();
                Log.e("MyLog", "SigninFragment.callListener 307行:  原来的个数 --->>> " + badgeNumber1);
                int all =  badgeNumber1 + 1;
                if (receiveMsg != null) {
                    List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(receiveMsg);
                    if (mReceiveMsg == null) {
                        mReceiveMsg = new ArrayList<>();
                    }
                    receiveMeetIMInfos.get(0).setType(true);
                    mReceiveMsg.add(receiveMeetIMInfos.get(0));
                    Log.e("MyLog", "SigninFragment.callListener: 收到的信息个数：  --->>> " + mReceiveMsg.size());
                }
                List<EventBadge> num = new ArrayList<>();
                num.add(new EventBadge(all));
                // TODO: 2018/3/7 通知界面更新
                Log.e("MyLog", "SigninFragment.callListener 319行:  传递过去的个数 --->>> " + all);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.UpDate_BadgeNumber, num));
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            nativeUtil = NativeUtil.getInstance();
            nativeUtil.setCallListener(this);
        }
    }
}
