package com.pa.paperless.fragment.meeting;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.pa.paperless.R;
import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.adapter.MulitpleItemAdapter;
import com.pa.paperless.adapter.MemberListAdapter;
import com.pa.paperless.bean.CheckedMemberIds;
import com.pa.paperless.bean.DevMember;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.MyUtils;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.mBadge;
import static com.pa.paperless.activity.MeetingActivity.mReceiveMsg;
import static com.pa.paperless.activity.MeetingActivity.chatisshowing;
import static com.pa.paperless.adapter.MemberListAdapter.itemChecked;
import static com.pa.paperless.adapter.MemberListAdapter.names;
import static com.pa.paperless.service.NativeService.nativeUtil;

/**
 * Created by Administrator on 2017/10/31.
 * 互动交流
 */

public class ChatFragment extends BaseFragment implements View.OnClickListener {

    private ListView chat_lv;
    private Button btn_all;
    private Button btn_invert;
    private MemberListAdapter mMemberAdapter;
    private RecyclerView chat_online_rl;
    private PercentRelativeLayout chat_left;
    private EditText chat_edt;
    private TextView chat_send;
    public static MulitpleItemAdapter chatAdapter;
    private List<DevMember> mChatonLineMember;
    private List<MemberInfo> memberInfos;
    private List<DeviceInfo> deviceInfos;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_chat, container, false);
        initView(inflate);
        try {
            //92.查询参会人员
            nativeUtil.queryAttendPeople();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        chatisshowing = true;
        EventBus.getDefault().register(this);
        return inflate;
    }

    private final String TAG = "ChatFragment-->";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.member_info://得到参会人信息
                InterfaceMember.pbui_Type_MemberDetailInfo o = (InterfaceMember.pbui_Type_MemberDetailInfo) message.getObject();
                memberInfos = Dispose.MemberInfo(o);
                //查询设备信息
                nativeUtil.queryDeviceInfo();
                break;
            case IDEventF.dev_info://得到设备信息
                receiveDevInfo(message);
                break;
            case IDEventF.meet_chat_info://收到新的聊天信息
                if (chatisshowing) {
                    InterfaceIM.pbui_Type_MeetIM object = (InterfaceIM.pbui_Type_MeetIM) message.getObject();
                    if (object.getMsgtype() == 0) {//证明是文本类消息
                        List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(object);
                        receiveMeetIMInfos.get(0).setType(true);//设置是接收的消息
                        mReceiveMsg.add(receiveMeetIMInfos.get(0));
                        try {
                            //查询指定ID的参会人  获取名称
                            nativeUtil.queryAttendPeopleFromId(receiveMeetIMInfos.get(0).getMemberid());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case IDEventF.member_byID://收到指定ID的参会人
                InterfaceMember.pbui_Type_MemberDetailInfo member = (InterfaceMember.pbui_Type_MemberDetailInfo) message.getObject();
                String name = MyUtils.getBts(member.getItemList().get(0).getName());
                // 给之前添加进去的最后一个设置参会人名字
                mReceiveMsg.get(mReceiveMsg.size() - 1).setName(name);
                if (chatAdapter == null) {
                    chatAdapter = new MulitpleItemAdapter(getContext(), mReceiveMsg);
                    chat_online_rl.setAdapter(chatAdapter);
                } else {
                    chatAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void receiveDevInfo(EventMessage message) {
        if (mChatonLineMember == null) {
            mChatonLineMember = new ArrayList<>();
        } else {
            mChatonLineMember.clear();
        }
        InterfaceDevice.pbui_Type_DeviceDetailInfo o1 = (InterfaceDevice.pbui_Type_DeviceDetailInfo) message.getObject();
        deviceInfos = Dispose.DevInfo(o1);
        for (int i = 0; i < deviceInfos.size(); i++) {
            DeviceInfo deviceInfo = deviceInfos.get(i);
            int netState = deviceInfo.getNetState();
            int faceState = deviceInfo.getFaceState();
            int devId = deviceInfo.getDevId();
            int memberId = deviceInfo.getMemberId();
            //判断是否是在线的并且界面状态为1的参会人
            if (faceState == 1 && memberInfos != null && netState == 1) {
                for (int j = 0; j < memberInfos.size(); j++) {
                    MemberInfo memberInfo = memberInfos.get(j);
                    int personid = memberInfo.getPersonid();
                    if (personid == memberId) {
                        /** **** **  过滤掉自己的设备  ** **** **/
                        if (devId != MeetingActivity.DevMeetInfo.getDeviceid()) {
                            //查找到在线状态的参会人员
                            mChatonLineMember.add(new DevMember(memberInfos.get(j), devId));
                        }
                    }
                }
            }
        }
        if (mChatonLineMember.size() == 0) {
            return;
        }
        //将在线状态的设备进行Adapter绑定
        /**
         * 下面代码逻辑：首次查询后就绑定lv，当查询的结果和之前的有变更
         有新的参会人加入
         * 添加新的默认项
         有参会人退出
         * 根据之前保存的选中人员
         最后进行Adapter刷新
         */
        if (mMemberAdapter == null) {
            mMemberAdapter = new MemberListAdapter(getActivity(), mChatonLineMember);
            chat_lv.setAdapter(mMemberAdapter);
        } else {
            itemChecked.clear();
            names.clear();
            // 多了几个就添加几个
            for (int i = 0; i < mChatonLineMember.size(); i++) {
                itemChecked.add(false);
                names.add("");
                Log.e(TAG, "ChatFragment.getEventMessage :  添加几个 --> itemChecked.size()当前大小：" + itemChecked.size());
            }
            mMemberAdapter.notifyDataSetChanged();
        }
        chat_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMemberAdapter.itemChecked.set(position, !mMemberAdapter.itemChecked.get(position));
                mMemberAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onDestroy() {
        Log.i("F_life", "ChatFragment.onDestroy :   --> ");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View inflate) {
        /** ****************聊天参会人列表******************* **/
        chat_lv = (ListView) inflate.findViewById(R.id.right_chat_lv);
        btn_all = (Button) inflate.findViewById(R.id.rightbtn_all);
        btn_invert = (Button) inflate.findViewById(R.id.rightbtn_invert);
        btn_all.setOnClickListener(this);
        btn_invert.setOnClickListener(this);
        /** ***************聊天界面******************** **/
        chat_online_rl = (RecyclerView) inflate.findViewById(R.id.rightchat_online_rl);
        chat_online_rl.setLayoutManager(new LinearLayoutManager(getContext()));
        mBadge.setBadgeNumber(0);
        chatAdapter = new MulitpleItemAdapter(getContext(), mReceiveMsg);
        chat_online_rl.setAdapter(chatAdapter);
        /** *********************************** **/

        chat_online_rl.setOnClickListener(this);
        chat_left = (PercentRelativeLayout) inflate.findViewById(R.id.rightchat_left);
        chat_left.setOnClickListener(this);
        chat_edt = (EditText) inflate.findViewById(R.id.rightchat_edt);
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
                    Log.e(TAG, "ChatFragment.onClick 283行:  选中的ID --->>> " + checkedMemberIds.getCheckid());
                }
                if (ids.size() < 1) {
                    showTip("请先选择聊天对象！");
                    break;
                }
                String string = chat_edt.getText().toString();
                if (!TextUtils.isEmpty(string) /*&& ids.size() > 0*/) {
                    //185.发送会议交流信息
                    if (string.length() <= 300) {
                        boolean b = nativeUtil.sendMeetChatInfo(string, InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Message.getNumber(), ids);
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
                        showTip(" 输入的字数不得大于300字 ");
                    }
                } else {
                    showTip("请输入内容");
                }
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i("F_life", "ChatFragment.onHiddenChanged :   --> " + hidden);
        chatisshowing = !hidden;
        if (!hidden) {
            try {
                //92.查询参会人员
                nativeUtil.queryAttendPeople();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.i("F_life", "ChatFragment.onAttach :   --> ");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "ChatFragment.onCreate :   --> ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "ChatFragment.onActivityCreated :   --> ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i("F_life", "ChatFragment.onStart :   --> ");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i("F_life", "ChatFragment.onResume :   --> ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("F_life", "ChatFragment.onPause :   --> ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("F_life", "ChatFragment.onStop :   --> ");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("F_life", "ChatFragment.onDestroyView :   --> ");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        Log.i("F_life", "ChatFragment.onDetach :   --> ");
        super.onDetach();
    }
}
