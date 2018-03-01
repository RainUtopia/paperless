package com.pa.paperless.fragment.meeting;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.adapter.MulitpleItemAdapter;
import com.pa.paperless.adapter.MemberListAdapter;
import com.pa.paperless.bean.CheckedMemberIds;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.DateUtil;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.MyUtils;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Administrator on 2017/10/31.
 * 互动交流
 */

public class ChatFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private ListView chat_lv;
    private Button btn_all;
    private Button btn_invert;
    private MemberListAdapter mMemberAdapter;
    private RecyclerView chat_online_rl;
    private PercentRelativeLayout chat_left;
    private EditText chat_edt;
    private TextView chat_send;
    private NativeUtil nativeUtil;
    private MulitpleItemAdapter chatAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_ATTENDEE://92.查询参会人员
                    ArrayList attendee = msg.getData().getParcelableArrayList("attendee");
                    InterfaceMain.pbui_Type_MemberDetailInfo o = (InterfaceMain.pbui_Type_MemberDetailInfo) attendee.get(0);
                    memberInfos = o.getItemList();
                    break;
                case IDivMessage.RECEIVE_MEET_IMINFO://184.收到新的会议交流信息
                    ArrayList receiveMeetIMInfo = msg.getData().getParcelableArrayList("receiveMeetIMInfo");
                    InterfaceMain2.pbui_Type_MeetIM o1 = (InterfaceMain2.pbui_Type_MeetIM) receiveMeetIMInfo.get(0);
                    int msgtype = o1.getMsgtype();
                    switch (msgtype) {
                        case 0://文本消息
                            int memberid = o1.getMemberid();
                            mReceiveMsg.add(Dispose.ReceiveMeetIMinfo(o1).get(0));
                            //为true时，表示是接收的消息
                            mReceiveMsg.get(mReceiveMsg.size() - 1).setType(true);
                            try {
                                //91.查询指定ID的参会人员
                                nativeUtil.queryAttendPeopleFromId(memberid);
                                Log.e("MyLog", "ChatFragment.handleMessage:  指定ID： --->>> " + memberid);
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                            //刷新
                            chatAdapter.notifyDataSetChanged();
                            break;
//                        case 1://多媒体链接
//                            Log.e("MyLog", "ChatFragment.handleMessage:  多媒体链接 --->>> " + 1);
//                            break;
//                        case 2://水
//                            Log.e("MyLog", "ChatFragment.handleMessage:  水 --->>> " + 2);
//                            break;
//                        case 3://茶
//                            Log.e("MyLog", "ChatFragment.handleMessage:  茶 --->>> " + 3);
//                            break;
//                        case 4://咖啡
//                            Log.e("MyLog", "ChatFragment.handleMessage:  咖啡 --->>> " + 4);
//                            break;
//                        case 5://笔
//                            Log.e("MyLog", "ChatFragment.handleMessage:  笔 --->>> " + 5);
//                            break;
//                        case 6://纸
//                            Log.e("MyLog", "ChatFragment.handleMessage:  纸 --->>> " + 6);
//                            break;
//                        case 7://技术员
//                            Log.e("MyLog", "ChatFragment.handleMessage:  技术员 --->>> " + 7);
//                            break;
//                        case 8://服务员
//                            Log.e("MyLog", "ChatFragment.handleMessage:  服务员 --->>> " + 8);
//                            break;
//                        case 9://其它服务
//                            Log.e("MyLog", "ChatFragment.handleMessage:  其它服务 --->>> " + 9);
//                            break;
//                        case 10://申请主持
//                            Log.e("MyLog", "ChatFragment.handleMessage:  申请主持 --->>> " + 10);
//                            break;
//                    }
                        //参会人员角色
//                    int role = o1.getRole();
//                    if(role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_nouser.getNumber()){
//                        //未使用
//
//                    }else if(role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_normal.getNumber()){
//                        //一般参会人员
//
//                    }else if(role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere.getNumber()){
//                        //主持人
//
//                    }else if(role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary.getNumber()){
//                        //秘书
//
//                    }else if(role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_device_projector.getNumber()){
//                        //投影仪
//
//                    }else if(role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin.getNumber()){
//                        //管理员
//
//                    }
                    }
                    break;
                case IDivMessage.QUERY_ATTEND_BYID://查询指定参会人信息
                    ArrayList queryAttendById = msg.getData().getParcelableArrayList("queryAttendById");
                    InterfaceMain.pbui_Type_MemberDetailInfo o2 = (InterfaceMain.pbui_Type_MemberDetailInfo) queryAttendById.get(0);
                    List<InterfaceMain.pbui_Item_MemberDetailInfo> itemList = o2.getItemList();
                    if (itemList != null) {
                        String name = MyUtils.getBts(itemList.get(0).getName());
                        mReceiveMsg.get(mReceiveMsg.size() - 1).setName(name);
                        Log.e("MyLog", "ChatFragment.handleMessage:  指定参会人名称： --->>> " + name);
                    }
                    break;
                case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                    ArrayList devinfo = msg.getData().getParcelableArrayList("devinfo");
                    InterfaceMain.pbui_Type_DeviceDetailInfo o3 = (InterfaceMain.pbui_Type_DeviceDetailInfo) devinfo.get(0);
                    //获取到所有的设备信息，查找在线的设备
                    List<InterfaceMain.pbui_Item_DeviceDetailInfo> pdevList = o3.getPdevList();
                    //用来存放在线的设备
                    List<InterfaceMain.pbui_Item_MemberDetailInfo> OnLineMembers = new ArrayList<>();
                    for (int i = 0; i < pdevList.size(); i++) {
                        InterfaceMain.pbui_Item_DeviceDetailInfo pbui_item_deviceDetailInfo = pdevList.get(i);
                        int netstate = pbui_item_deviceDetailInfo.getNetstate();
                        Log.e("MyLog", "ChatFragment.handleMessage 171行:  值为1是在线 ：--->>> " + netstate);
                        if (netstate == 1) {
                            //获取在线状态的设备绑定的人员ID
                            int memberid = pbui_item_deviceDetailInfo.getMemberid();
                            for (int j = 0; j < memberInfos.size(); j++) {
                                InterfaceMain.pbui_Item_MemberDetailInfo pbui_item_memberDetailInfo = memberInfos.get(j);
                                if (pbui_item_memberDetailInfo.getPersonid() == memberid) {
                                    //添加在线状态的设备
                                    OnLineMembers.add(pbui_item_memberDetailInfo);
                                }
                            }
                        }
                    }
                    //将在线状态的设备进行Adapter绑定
                    mMemberAdapter = new MemberListAdapter(getActivity(), OnLineMembers);
                    chat_lv.setAdapter(mMemberAdapter);
                    break;

            }
        }
    };
    private List<ReceiveMeetIMInfo> receiveMeetIMInfos = new ArrayList<>();
    private List<InterfaceMain.pbui_Item_MemberDetailInfo> memberInfos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_chat, container, false);
        initController();
        initView(inflate);
        try {
            //92.查询参会人员
            nativeUtil.queryAttendPeople();
            /** ************ ******  6.查询设备信息  ****** ************ **/
            nativeUtil.queryDeviceInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.MEMBER_CHANGE_INFORM:
                Log.e("MyLog", "ChatFragment.getEventMessage:  90 参会人员变更通知 EventBus --->>> ");
                InterfaceMain.pbui_MeetNotifyMsg object = (InterfaceMain.pbui_MeetNotifyMsg) message.getObject();
                Log.e("MyLog", "ChatFragment.getEventMessage:  EventBus 指定ID：--->>> " + object.getId());
                nativeUtil.queryAttendPeopleFromId(object.getId());
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
//        nativeUtil = new NativeUtil();
        nativeUtil.setCallListener(this);
    }

    private void initView(View inflate) {
        /** ****************聊天参会人列表******************* **/
        chat_lv = (ListView) inflate.findViewById(R.id.right_chat_lv);

        /** *********************************** **/

        btn_all = (Button) inflate.findViewById(R.id.rightbtn_all);
        btn_invert = (Button) inflate.findViewById(R.id.rightbtn_invert);
        btn_all.setOnClickListener(this);
        btn_invert.setOnClickListener(this);

        /** ***************聊天界面******************** **/
        chat_online_rl = (RecyclerView) inflate.findViewById(R.id.rightchat_online_rl);
        chat_online_rl.setLayoutManager(new LinearLayoutManager(getContext()));
        if (mReceiveMsg == null) {
            mReceiveMsg = new ArrayList<>();
        }
        chatAdapter = new MulitpleItemAdapter(getContext(), mReceiveMsg);
        chat_online_rl.setAdapter(chatAdapter);
        /** *********************************** **/

        chat_online_rl.setOnClickListener(this);
        chat_left = (PercentRelativeLayout) inflate.findViewById(R.id.rightchat_left);
        chat_left.setOnClickListener(this);
        chat_edt = (EditText) inflate.findViewById(R.id.rightchat_edt);
        chat_edt.setOnClickListener(this);
        chat_send = (TextView) inflate.findViewById(R.id.rightchat_send);
        chat_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rightbtn_all://全选
                mMemberAdapter.setAllChecked();
                break;
            case R.id.rightbtn_invert://反选
                mMemberAdapter.setInvert();
                break;
            case R.id.rightchat_send: //发送
                // 获取选中的参会人 ID
                List<CheckedMemberIds> checkedId = mMemberAdapter.getCheckedId();
                List<Integer> ids = new ArrayList<>();
                for (int i = 0; i < checkedId.size(); i++) {
                    CheckedMemberIds checkedMemberIds = checkedId.get(i);
                    //将ID添加到 List<Integer> 集合中，List实现了Iterable，泛型对应的是Integer
                    ids.add(checkedMemberIds.getCheckid());
                }
                String string = chat_edt.getText().toString();
                if (!TextUtils.isEmpty(string) && ids.size() > 0) {
                    //185.发送会议交流信息
                    if (string.length() <= 300) {
                        boolean b = nativeUtil.sendMeetChatInfo(string, InterfaceMacro.Pb_String_LenLimit.Pb_MEETIM_CHAR_MSG_MAXLEN.getNumber(), ids);
                        if (b) { //发送成功
                            //RecyclerView 更新聊天的界面
                            List<String> checkedName = mMemberAdapter.getCheckedName();

                            ReceiveMeetIMInfo sendInfo = new ReceiveMeetIMInfo();
                            sendInfo.setNames(checkedName);
                            sendInfo.setMsg(string);
                            sendInfo.setUtcsecond(System.currentTimeMillis());
                            // false发送的消息  true接收的消息
                            sendInfo.setType(false);
                            mReceiveMsg.add(sendInfo);
                            chatAdapter.notifyDataSetChanged();
                            //清除输入框内容
                            chat_edt.setText("".trim());
                        }
                    } else {
                        Toast.makeText(getActivity(), " 输入的字数不得大于300字 ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "请输入内容", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rightchat_edt: //编辑

                break;
        }
    }


    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_ATTENDEE:
                InterfaceMain.pbui_Type_MemberDetailInfo result1 = (InterfaceMain.pbui_Type_MemberDetailInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("attendee", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
                break;
            case IDivMessage.RECEIVE_MEET_IMINFO://184.收到新的会议交流信息
                InterfaceMain2.pbui_Type_MeetIM result2 = (InterfaceMain2.pbui_Type_MeetIM) result;
                if (result2 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result2);
                    bundle.putParcelableArrayList("receiveMeetIMInfo", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_ATTEND_BYID:
                InterfaceMain.pbui_Type_MemberDetailInfo result3 = (InterfaceMain.pbui_Type_MemberDetailInfo) result;
                if (result3 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result3);
                    bundle.putParcelableArrayList("queryAttendById", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_DEVICE_INFO:
                InterfaceMain.pbui_Type_DeviceDetailInfo result4 = (InterfaceMain.pbui_Type_DeviceDetailInfo) result;
                if (result4 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result4);
                    bundle.putParcelableArrayList("devinfo", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
                break;
        }
    }
}
