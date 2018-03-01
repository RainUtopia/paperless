package com.pa.paperless.fragment.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.adapter.SigninLvAdapter;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.event.EventSign;
import com.pa.paperless.bean.SigninBean;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.controller.MeetController;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.DateUtil;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.Export;
import com.pa.paperless.utils.MyUtils;
import com.pa.paperless.utils.SaveToExcelUtil;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.libsdl.app.SDLActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
    private List<SigninBean> mDatas;
    public static int pageItem = 6;//每一页最多显示 6 个item
    public static int nowPage = 0; //当前页数
    private SigninLvAdapter signinLvAdapter;
    private NativeUtil nativeUtil;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IDivMessage.QUERY_ATTENDEE://查询参会人员
                    ArrayList queryAttenda = msg.getData().getParcelableArrayList("queryAttende");
                    if (queryAttenda != null) {
                        mDatas = new ArrayList<>();
                        InterfaceMain.pbui_Type_MemberDetailInfo o2 = (InterfaceMain.pbui_Type_MemberDetailInfo) queryAttenda.get(0);
                        List<InterfaceMain.pbui_Item_MemberDetailInfo> itemList = o2.getItemList();
                        int itemCount2 = o2.getItemCount();
                        for (int i = 0; i < itemCount2; i++) {
                            InterfaceMain.pbui_Item_MemberDetailInfo item = o2.getItem(i);
                            String nName = new String(item.getName().toByteArray());
                            int personid = item.getPersonid();
                            String comment = new String(item.getComment().toByteArray());
                            String company = new String(item.getCompany().toByteArray());
                            String email = new String(item.getEmail().toByteArray());
                            String job = new String(item.getJob().toByteArray());
                            String password = new String(item.getPassword().toByteArray());
                            String phone = new String(item.getPhone().toByteArray());
                            //获取到人员ID
                            //通过人员ID查找签到状态
                            mDatas.add(new SigninBean(personid, mDatas.size() + 1 + "", nName));
                        }
                    }
                    break;
                case IDivMessage.QUERY_SIGN://查询签到
                    ArrayList signInfo = msg.getData().getParcelableArrayList("signInfo");
                    InterfaceMain2.pbui_Type_MeetSignInDetailInfo o = (InterfaceMain2.pbui_Type_MeetSignInDetailInfo) signInfo.get(0);
                    int itemCount = o.getItemCount();
                    Log.e("MyLog","SigninFragment.handleMessage 97行:  签到数量 --->>> "+itemCount);
                    for (int i = 0; i < itemCount; i++) {
                        InterfaceMain2.pbui_Item_MeetSignInDetailInfo item = o.getItem(i);
                        int nameId = item.getNameId();  // 已经签到的人员ID
                        String passWord = new String(item.getPassword().toByteArray());
                        String psignData = new String(item.getPsigndata().toByteArray());
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
                    break;
                case IDivMessage.QUERY_CONFORM_DEVID://125.查询符合要求的设备ID(查询投影机)
                    ArrayList queryProjector = msg.getData().getParcelableArrayList("queryProjector");
                    InterfaceMain.pbui_Type_ResMeetRoomDevInfo o5 = (InterfaceMain.pbui_Type_ResMeetRoomDevInfo) queryProjector.get(0);
                    //获得投影机的设备ID
                    List<Integer> devidList = o5.getDevidList();
                    for (int i = 0; i < devidList.size(); i++) {
                        int number = Macro.DEVICE_MEET_PROJECTIVE;
                        Integer devId = devidList.get(i);
                        int i1 = devId & number;
                        Log.e("MyLog", "MeetingActivity.handleMessage:  所有的设备ID： --->>> " + devId + "  相于的结果  ：" + i1);
                        if (i1 == number) {
                            Log.e("MyLog", "MeetingActivity.handleMessage:  该设备ID为投影机 --->>> ");
                            try {
                                /** ************ ******  7.按属性ID查询指定设备属性  ****** ************ **/
                                // TODO: 2018/2/27 没有二次查找  等待二次查找。。。。
                                nativeUtil.queryDevicePropertiesById(
                                        //表示只查找名称，返回时字符串格式解析
                                        InterfaceMacro.Pb_MeetDevicePropertyID.Pb_MEETDEVICE_PROPERTY_NAME.getNumber() |
                                                InterfaceMacro.Pb_MeetDevicePropertyID.Pb_MEETDEVICE_PROPERTY_IPADDR.getNumber(),
                                        devId,
                                        0);
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case IDivMessage.Dev_proById: //7.按属性ID查询指定设备属性（投影机）
                    ArrayList queryProjector1 = msg.getData().getParcelableArrayList("queryProjectorName");
                    byte[] array = (byte[]) queryProjector1.get(0);
                    // 根据查找的类型 查询的是名称就解析成字符串格式
                    InterfaceMain.pbui_DeviceStringProperty pbui_deviceStringProperty = InterfaceMain.pbui_DeviceStringProperty.getDefaultInstance();
                    try {
                        InterfaceMain.pbui_DeviceStringProperty pbui_deviceStringProperty1 = pbui_deviceStringProperty.parseFrom(array);
                        //获取到投影机的名称
                        String ProName = new String(pbui_deviceStringProperty1.getPropertytext().toByteArray());
                        Log.e("MyLog", "SigninFragment.handleMessage 145行:  投影机名称 --->>> " + ProName);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_signin, container, false);
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
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.SIGN_CHANGE_INFORM:
                Log.e("MyLog", "SigninFragment.getEventMessage:  签到变更通知 EventBus --->>> ");
                /** ************ ******  206.查询签到  ****** ************ **/
                nativeUtil.querySign();
                break;
            case IDEventMessage.MEMBER_CHANGE_INFORM:
                Log.e("MyLog", "SigninFragment.getEventMessage:  参会人员变更通知 EventBus --->>> ");
                /** ************ ******  92.查询参会人员  ****** ************ **/
                nativeUtil.queryAttendPeople();
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

//                SaveToExcelUtil.createSmtrautExcel("签到信息","sheetName",arr,cod,);
                break;
        }
    }

    /**
     * 保存方法
     */
    private void savaInfo(Object obj, String path) {
        File file = new File(path);
        if (!file.exists()) {
            //文件不存在--->>> 创建
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(obj);
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_SIGN://查询签到
                InterfaceMain2.pbui_Type_MeetSignInDetailInfo result2 = (InterfaceMain2.pbui_Type_MeetSignInDetailInfo) result;
                if (result2 != null) {
                    if (result2 != null) {
                        Bundle bundle = new Bundle();
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(result2);
                        bundle.putParcelableArrayList("signInfo", arrayList);
                        Message message = new Message();
                        message.what = action;
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                }
                break;
            case IDivMessage.QUERY_ATTENDEE://查询参会人员
                InterfaceMain.pbui_Type_MemberDetailInfo result3 = (InterfaceMain.pbui_Type_MemberDetailInfo) result;
                if (result3 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result3);
                    bundle.putParcelableArrayList("queryAttende", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.RECEIVE_MEET_IMINFO:
                Log.e("MyLog", "SigninFragment.callListener: 签到状态： 收到会议消息 --->>> ");
                InterfaceMain2.pbui_Type_MeetIM receiveMsg = (InterfaceMain2.pbui_Type_MeetIM) result;
                if (receiveMsg != null) {
                    List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(receiveMsg);
                    if (mReceiveMsg == null) {
                        mReceiveMsg = new ArrayList<>();
                    }
                    mReceiveMsg.add(receiveMeetIMInfos.get(0));
                    Log.e("MyLog", "SigninFragment.callListener: 收到的信息个数：  --->>> " + mReceiveMsg.size());
                }
                break;

            case IDivMessage.QUERY_CONFORM_DEVID: //125.查询符合要求的设备ID
                InterfaceMain.pbui_Type_ResMeetRoomDevInfo result5 = (InterfaceMain.pbui_Type_ResMeetRoomDevInfo) result;
                if (result5 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result5);
                    bundle.putParcelableArrayList("queryProjector", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.Dev_proById://7.按属性ID查询指定设备属性（投影机）
                InterfaceMain.pbui_DeviceStringProperty result1 = (InterfaceMain.pbui_DeviceStringProperty) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryProjectorName", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }
}