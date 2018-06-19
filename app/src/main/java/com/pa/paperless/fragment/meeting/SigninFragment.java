package com.pa.paperless.fragment.meeting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.pa.paperless.R;
import com.pa.paperless.adapter.SigninLvAdapter;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.bean.SigninBean;
import com.pa.paperless.utils.DateUtil;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.Export;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.service.NativeService.nativeUtil;


/**
 * Created by Administrator on 2017/11/1.
 * 签到状态Fragment
 */

public class SigninFragment extends BaseFragment implements View.OnClickListener {
    private final String TAG = "SigninFragment-->";
    private ListView mSigninLv;
    private Button mPrepageBtn;
    private Button mNextpageBtn;
    private Button mExportBtn;
    public static int pageItem = 6;//每一页最多显示 6 个item
    public static int nowPage = 0; //当前页数
    private List<SigninBean> mDatas;
    private SigninLvAdapter signinLvAdapter;
    private List<MemberInfo> memberInfos;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_signin, container, false);
        EventBus.getDefault().register(this);
        initView(inflate);
        try {
            /** ************ ******  92.查询参会人员  ****** ************ **/
            nativeUtil.queryAttendPeople();
            /** **** **  将签到信息先缓存下来  ** **** **/
            nativeUtil.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN.getNumber());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return inflate;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.member_info://收到所有的参会人的数据
                InterfaceMember.pbui_Type_MemberDetailInfo o = (InterfaceMember.pbui_Type_MemberDetailInfo) message.getObject();
                memberInfos = Dispose.MemberInfo(o);
                if (memberInfos != null) {
                    /** ************ ******  206.查询签到信息  ****** ************ **/
                    nativeUtil.querySign();
                }
                break;
            case IDEventF.signin_info://收到签到信息
                receiveSigninInfo(message);
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void receiveSigninInfo(EventMessage message) {
        InterfaceSignin.pbui_Type_MeetSignInDetailInfo object1 = (InterfaceSignin.pbui_Type_MeetSignInDetailInfo) message.getObject();
        List<InterfaceSignin.pbui_Item_MeetSignInDetailInfo> itemList = object1.getItemList();
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        } else {
            mDatas.clear();
        }
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceSignin.pbui_Item_MeetSignInDetailInfo item = itemList.get(i);
            int nameId = item.getNameId();
            int signinType = item.getSigninType();
            long utcseconds = item.getUtcseconds();
            String[] gtmDate = DateUtil.getDate(utcseconds * 1000);
            String dateTime = gtmDate[0] + "  " + gtmDate[2];
            for (int j = 0; j < memberInfos.size(); j++) {
                if (memberInfos.get(j).getPersonid() == nameId) {
                    mDatas.add(new SigninBean(nameId, mDatas.size() + 1 + "", memberInfos.get(j).getName(), dateTime, signinType));
                }
            }
        }
        if (signinLvAdapter == null) {
            signinLvAdapter = new SigninLvAdapter(getActivity(), mDatas);
            mSigninLv.setAdapter(signinLvAdapter);
        } else {
            signinLvAdapter.notifyDataSetChanged();
        }
        checkButton();
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
            if (mDatas.size() > pageItem) {
                mNextpageBtn.setEnabled(true);
            } else {
                mNextpageBtn.setEnabled(false);
            }

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
            try {
                nativeUtil.queryAttendPeople();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }
}