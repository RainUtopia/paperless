package com.pa.paperless.fragment.meeting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeetfunction;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.pa.paperless.R;
import com.pa.paperless.adapter.SigninLvAdapter;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.bean.SigninBean;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.DateUtil;
import com.pa.paperless.utils.Export;
import com.pa.paperless.utils.MyUtils;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/11/1.
 * 签到状态Fragment
 */

public class SigninFragment extends BaseFragment implements View.OnClickListener, CallListener {
    private ListView mSigninLv;
    private Button mPrepageBtn;
    private Button mNextpageBtn;
    private Button mExportBtn;
    public static int pageItem = 6;//每一页最多显示 6 个item
    public static int nowPage = 0; //当前页数
    private List<SigninBean> mDatas;
    private SigninLvAdapter signinLvAdapter;
    private NativeUtil nativeUtil;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_signin, container, false);
        EventBus.getDefault().register(this);
        initController();
        initView(inflate);
        checkButton();
        try {
            /** ************ ******  92.查询参会人员  ****** ************ **/
            nativeUtil.queryAttendPeople();
            /** ************ ******  206.查询签到信息  ****** ************ **/
            nativeUtil.querySign();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return inflate;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IDivMessage.QUERY_ATTENDEE://查询参会人员
                    queryAttendee(msg);
                    break;
                case IDivMessage.QUERY_SIGN://查询签到
                    if(mDatas!=null) {
                        querySignIn(msg);
                    }
                    break;
            }
        }
    };


    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_SIGN://查询签到
                MyUtils.handTo(IDivMessage.QUERY_SIGN, (InterfaceSignin.pbui_Type_MeetSignInDetailInfo) result, "signInfo", mHandler);
                break;
            case IDivMessage.QUERY_ATTENDEE://查询参会人员
                MyUtils.handTo(IDivMessage.QUERY_ATTENDEE, (InterfaceMember.pbui_Type_MemberDetailInfo) result, "queryAttende", mHandler);
                break;
            case IDivMessage.RECEIVE_MEET_IMINFO: //收到会议消息
                Log.e("MyLog", "SigninFragment.callListener 296行:  收到会议消息 --->>> ");
                MyUtils.receiveMessage((InterfaceIM.pbui_Type_MeetIM) result, nativeUtil);
                break;
            // TODO: 2018/4/11 使用对比可以获得名字
            case IDivMessage.QUERY_ATTEND_BYID://查询指定ID的参会人（接收到会议消息时查看发送消息人的名称）
                MyUtils.queryName((InterfaceMember.pbui_Type_MemberDetailInfo) result);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.SIGN_CHANGE_INFORM:
                Log.e("MyLog", "SigninFragment.getEventMessage:  签到变更通知 EventBus --->>> ");
                /** ************ ******  206.查询签到  ****** ************ **/
                nativeUtil.querySign();
                break;
            case IDEventMessage.MEMBER_CHANGE_INFORM://90 参会人员变更通知
                nativeUtil.queryAttendPeople();
                InterfaceBase.pbui_MeetNotifyMsg MrmberName = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                /** ************ ******  91.查询指定ID的参会人 （接收到消息时） ****** ************ **/
                nativeUtil.queryAttendPeopleFromId(MrmberName.getId());
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void queryAttendee(Message msg) {
        ArrayList queryAttenda = msg.getData().getParcelableArrayList("queryAttende");
        if (queryAttenda != null) {
            mDatas = new ArrayList<>();
            InterfaceMember.pbui_Type_MemberDetailInfo o2 = (InterfaceMember.pbui_Type_MemberDetailInfo) queryAttenda.get(0);
            List<InterfaceMember.pbui_Item_MemberDetailInfo> itemList = o2.getItemList();
            int itemCount2 = o2.getItemCount();
            for (int i = 0; i < itemCount2; i++) {
                InterfaceMember.pbui_Item_MemberDetailInfo item = o2.getItem(i);
                String nName = new String(item.getName().toByteArray());
                int personid = item.getPersonid();
                //获取到人员ID
                //通过人员ID查找签到状态
                mDatas.add(new SigninBean(personid, mDatas.size() + 1 + "", nName));
            }
        }
    }

    private void querySignIn(Message msg) {
        ArrayList signInfo = msg.getData().getParcelableArrayList("signInfo");
        InterfaceSignin.pbui_Type_MeetSignInDetailInfo o = (InterfaceSignin.pbui_Type_MeetSignInDetailInfo) signInfo.get(0);
        int itemCount = o.getItemCount();
        Log.e("MyLog", "SigninFragment.handleMessage 97行:  签到数量 --->>> " + itemCount);
        for (int i = 0; i < itemCount; i++) {
            InterfaceSignin.pbui_Item_MeetSignInDetailInfo item = o.getItem(i);
            int nameId = item.getNameId();  // 已经签到的人员ID
            int signinType = item.getSigninType();
            long utcseconds = item.getUtcseconds();
            String[] gtmDate = DateUtil.getDate(utcseconds * 1000);
            String dateTime = gtmDate[0] + "  " + gtmDate[2];
            for (int j = 0; j < mDatas.size(); j++) {
                if (mDatas.get(j).getId() == nameId) {
                    //找出已经签到的人
                    mDatas.get(j).setSignin_date(dateTime);
                    mDatas.get(j).setSign_in(signinType);
                }
            }
        }
        signinLvAdapter = new SigninLvAdapter(getActivity(), mDatas);
        mSigninLv.setAdapter(signinLvAdapter);
    }

    @Override
    protected void initController() {
        nativeUtil = NativeUtil.getInstance();
        nativeUtil.setCallListener(this);
    }

    private void initView(View inflate) {
        mSigninLv = (ListView) inflate.findViewById(R.id.signin_lv);
        mPrepageBtn = (Button) inflate.findViewById(R.id.prepage_btn);
        mNextpageBtn = (Button) inflate.findViewById(R.id.nextpage_btn);
        mExportBtn = (Button) inflate.findViewById(R.id.export_btn);
        mPrepageBtn.setOnClickListener(this);
        mNextpageBtn.setOnClickListener(this);
        mExportBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prepage_btn://上一页
                prePage();
                break;
            case R.id.nextpage_btn://下一页
                nextPage();
                break;
            case R.id.export_btn://导出签到信息
                String[] titles = {"序号", "姓名", "签到时间", "是否签到"};
                Export.ToSigninExcel("签到信息", "Sheet1", titles, mDatas);
                break;
        }
    }

    public void checkButton() {
        //如果页码已经是第一页了
        if (nowPage <= 0) {
            mPrepageBtn.setEnabled(false);
            //关键代码--->>>设置下一页按钮有用
            mNextpageBtn.setEnabled(true);
        }
        //值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
        else if (mDatas.size() - nowPage * pageItem <= pageItem) {
            mNextpageBtn.setEnabled(false);
            //关键代码--->>>设置上一页按钮有用
            mPrepageBtn.setEnabled(true);
        } else {
            //否则两个按钮都设为可用
            mPrepageBtn.setEnabled(true);
            mNextpageBtn.setEnabled(true);
        }

    }

    private void nextPage() {
        nowPage++;
        signinLvAdapter.notifyDataSetChanged();
        checkButton();
    }

    private void prePage() {
        nowPage--;
        signinLvAdapter.notifyDataSetChanged();
        checkButton();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            nativeUtil = NativeUtil.getInstance();
            nativeUtil.setCallListener(this);
        }
    }
}