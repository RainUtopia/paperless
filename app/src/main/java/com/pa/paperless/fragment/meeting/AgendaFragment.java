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
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.pa.paperless.R;
import com.pa.paperless.bean.AgendContext;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.MyUtils;
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

    private NativeUtil nativeUtil;
    private TextView agenda_tv;
    List<AgendContext> mData;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IDivMessage.RECEIVE_MEET_IMINFO://收到新的会议交流信息
                    Log.e("MyLog","AgendaFragment.handleMessage 52行:  处理消息 --->>> ");
                    ArrayList resultNewMessage = msg.getData().getParcelableArrayList("resultNewMessage");
                    InterfaceIM.pbui_Type_MeetIM newMessage = (InterfaceIM.pbui_Type_MeetIM) resultNewMessage.get(0);
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
                        InterfaceAgenda.pbui_meetAgenda o = (InterfaceAgenda.pbui_meetAgenda) agenda.get(0);
                        //议程类型 参见Pb_AgendaType
                        int agendatype = o.getAgendatype();
                        //媒体ID
                        int mediaid = o.getMediaid();
                        //议程文本
                        String  text = new String(o.getText().toByteArray());
                        Log.e("MyLog","AgendaFragment.handleMessage:  agendatype --->>> "+agendatype+"   mediaid : "+mediaid+"   text:  "+ text);
                        //时间轴式
//                        for (int i = 0; i < o.getItemCount(); i++) {
//                            InterfaceAgenda.pbui_ItemAgendaTimeInfo item = o.getItem(i);
//                            int agendaid = item.getAgendaid();  //议程ID
//                            int status = item.getStatus();      //议程状态
//                            int dirid = item.getDirid();        //绑定目录ID
//                            long startutctime = item.getStartutctime();
//                            long endutctime = item.getEndutctime();
//                            String sTime = DateUtil.getTime(startutctime);
//                            String eTime = DateUtil.getTime(endutctime);
//                            String time = sTime + "-" + eTime;  // 8:00-9:00
//                            String descText = new String(item.getDesctext().toByteArray());//描述内容
//                        }
//                        mData.add(new AgendContext(mediaid+"", text));
                        //设置数据
                        agenda_tv.setText(text);
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.agenda, container, false);
        initController();
        initView(inflate);
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
            case IDEventMessage.MEMBER_CHANGE_INFORM://90 参会人员变更通知
                InterfaceBase.pbui_MeetNotifyMsg MrmberName = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                /** ************ ******  91.查询指定ID的参会人  ****** ************ **/
                nativeUtil.queryAttendPeopleFromId(MrmberName.getId());
                break;
            case IDEventMessage.AGENDA_CHANGE_INFO:
                nativeUtil.queryAgenda();
                break;
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
        agenda_tv = (TextView) inflate.findViewById(R.id.agenda_text);
    }


    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_AGENDA:
                MyUtils.handTo(IDivMessage.QUERY_AGENDA,(InterfaceAgenda.pbui_meetAgenda) result,"agenda",mHandler);
                break;
            case IDivMessage.RECEIVE_MEET_IMINFO: //收到会议消息
                Log.e("MyLog", "SigninFragment.callListener 296行:  收到会议消息 --->>> ");
                MyUtils.receiveMessage((InterfaceIM.pbui_Type_MeetIM) result,nativeUtil);
                break;
            case IDivMessage.QUERY_ATTEND_BYID://查询指定ID的参会人
                MyUtils.queryName((InterfaceMember.pbui_Type_MemberDetailInfo) result);
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
