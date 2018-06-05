package com.pa.paperless.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.pa.paperless.R;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fragment.manage.firstfloor.AfterMeetingTitleFragment;
import com.pa.paperless.fragment.manage.firstfloor.MeetingControlTitleFragment;
import com.pa.paperless.fragment.manage.firstfloor.MeetingPrepareFragment;
import com.pa.paperless.fragment.manage.firstfloor.SystemSettingTieleFragment;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.pa.paperless.listener.CallListener;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ManageActivity extends BaseActivity implements View.OnClickListener, CallListener {

    private FragmentManager mFm;
    private TextView mSystemSetting;
    private TextView mMettingPrepare;
    private TextView mMeetingControl;
    private TextView mMeetingManage;
    private TextView mTitleCurrentUser;
    private TextView mTextPlace;
    private TextView mTitleState;
    private TextView mMeetingTitleName;
    private TextView mTitleRight;
    private FrameLayout mMeetingFl;
    private List<TextView> mViews = new ArrayList<>();
    private List<BaseFragment> mFragments;
    private FragmentTransaction mBeginTransaction;
    private NativeUtil nativeUtil ;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case IDivMessage.QUERY_MEET:
                    ArrayList queryMeet = msg.getData().getParcelableArrayList("queryMeet");
                    InterfaceMeet.pbui_Type_MeetMeetInfo o = (InterfaceMeet.pbui_Type_MeetMeetInfo) queryMeet.get(0);
                    List<InterfaceMeet.pbui_Item_MeetMeetInfo> itemList = o.getItemList();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        initView();
        initController();
        initList();
        initFragment();
        //  修改本机界面状态：后台管理界面
        nativeUtil.setInterfaceState(InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_AdminFace.getNumber());
        try {
            nativeUtil.queryMeet();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        //  设置默认点击
        mSystemSetting.performClick();
        EventBus.getDefault().register(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()){
            case IDEventMessage.MEETINFO_CHANGE_INFORM:
                Log.e("MyLog","ManageActivity.getEventMessage:  查询会议 EventBus --->>> ");
                nativeUtil.queryMeet();
                break;
        }
    }
    @Override
    protected void initController() {
        nativeUtil = NativeUtil.getInstance();
//        nativeUtil = new NativeUtil();
        nativeUtil.setCallListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //回到主界面
//        nativeUtil.backToMainInterfaceOperate();
    }

    public void changeFragment(int index) {
        mFm = getSupportFragmentManager();
        mBeginTransaction = mFm.beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            if (i == index) {
                if (!mFragments.get(i).isAdded()) {
                    mBeginTransaction.add(R.id.meeting_fl, mFragments.get(i));
                    mBeginTransaction.show(mFragments.get(i));
                } else {
                    mBeginTransaction.show(mFragments.get(i));
                }
            } else {
                if (mFragments.get(i).isAdded()) {
                    mBeginTransaction.hide(mFragments.get(i));
                }
            }
        }
        mBeginTransaction.commit();
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new SystemSettingTieleFragment());
        mFragments.add(new MeetingPrepareFragment());
        mFragments.add(new MeetingControlTitleFragment());
        mFragments.add(new AfterMeetingTitleFragment());
    }

    private void initList() {
        mViews.add(mSystemSetting);
        mViews.add(mMettingPrepare);
        mViews.add(mMeetingControl);
        mViews.add(mMeetingManage);
    }

    private void initView() {
        mSystemSetting = (TextView) findViewById(R.id.system_setting);
        mMettingPrepare = (TextView) findViewById(R.id.metting_prepare);
        mMeetingControl = (TextView) findViewById(R.id.meeting_control);
        mMeetingManage = (TextView) findViewById(R.id.meeting_manage);
        mTitleCurrentUser = (TextView) findViewById(R.id.title_current_user);
        mTextPlace = (TextView) findViewById(R.id.text_place);
        mTitleState = (TextView) findViewById(R.id.title_state);
        mMeetingTitleName = (TextView) findViewById(R.id.meeting_title_name);
        mTitleRight = (TextView) findViewById(R.id.title_right);
        mMeetingFl = (FrameLayout) findViewById(R.id.meeting_fl);

        mSystemSetting.setOnClickListener(this);
        mMettingPrepare.setOnClickListener(this);
        mMeetingControl.setOnClickListener(this);
        mMeetingManage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.system_setting:
                mSystemSetting.setTextColor(Color.WHITE);
                setCheckTextColor(0);
                changeFragment(0);
                break;
            case R.id.metting_prepare:
                mMettingPrepare.setTextColor(Color.WHITE);
                setCheckTextColor(1);
                changeFragment(1);
                break;
            case R.id.meeting_control:
                mMeetingControl.setTextColor(Color.WHITE);
                setCheckTextColor(2);
                changeFragment(2);
                break;
            case R.id.meeting_manage:
                mMeetingManage.setTextColor(Color.WHITE);
                setCheckTextColor(3);
                changeFragment(3);
                break;
        }
    }

    public void setCheckTextColor(int posion) {
        for (int i = 0; i < mViews.size(); i++) {
            if (i == posion) {
                mViews.get(i).setSelected(true);
                mViews.get(i).setTextColor(Color.WHITE);
            } else {
                mViews.get(i).setSelected(false);
                mViews.get(i).setTextColor(Color.BLACK);
            }
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action){
            case IDivMessage.QUERY_MEET:
                InterfaceMeet.pbui_Type_MeetMeetInfo result1 = (InterfaceMeet.pbui_Type_MeetMeetInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryMeet", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }
}
