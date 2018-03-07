package com.pa.paperless.fragment.manage.secondfloor.s.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.adapter.setadapter.DeviceManageAdapter;
import com.pa.paperless.bean.IpInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.ItemClickListener;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/11/16.
 * 系统设置-设备管理
 */

public class DeviceManagementFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private NativeUtil nativeUtil;
    private RecyclerView mDevrceManageRl;
    private EditText mDeviceNameEdt;
    private EditText mDeviceIpEdt;
    private Button mModifyBtn;
    private Button mDeleteBtn;
    private DeviceManageAdapter mAdapter;
    private String mTextName;
    private String mTextIp;
    public static int mPosion;
    private TextView mItemNameTv;
    private TextView mItemIpTv;

//    private NativeUtil nativeUtil;
    public static boolean isDelete;
    public static boolean isModifi;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_DEVICE_INFO:
                    ArrayList queryDeviceInfo = msg.getData().getParcelableArrayList("queryDeviceInfo");
                    InterfaceMain.pbui_Type_DeviceDetailInfo o = (InterfaceMain.pbui_Type_DeviceDetailInfo) queryDeviceInfo.get(0);
                    pdevList = o.getPdevList();
                    mAdapter = new DeviceManageAdapter(getActivity(), pdevList);
                    mDevrceManageRl.setLayoutManager(new LinearLayoutManager(getContext()));
                    mDevrceManageRl.setAdapter(mAdapter);
                    mAdapter.setListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int posion) {
                            isDelete = true;
                            isModifi = true;
                            //获取当前索引item中的控件
                            mItemNameTv = view.findViewById(R.id.device_name);
                            mItemIpTv = view.findViewById(R.id.device_ip);
                            //将当前选中item 的名称和IP同步设置到EditText中
                            mDeviceNameEdt.setText(mItemNameTv.getText());
                            mDeviceIpEdt.setText(mItemIpTv.getText());
                            //保存当前点击索引
                            mPosion = posion;
                            checkedDevId = pdevList.get(mPosion).getDevcieid();
                            Log.e("MyLog", "DeviceManagementFragment.onItemClick:  --->>> item索引： " + mPosion + " 选中的设备的ID：" + checkedDevId);
                            /** ************ ******  设置选中的索引  ****** ************ **/
                            mAdapter.setCheckedId(posion);
                        }
                    });
                    break;
            }
        }
    };


    private List<InterfaceMain.pbui_Item_DeviceDetailInfo> pdevList;
    private int checkedDevId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.device_management, container, false);
        initView(inflate);
        initController();
        try {
            //6.查询设备信息
            nativeUtil.queryDeviceInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) {
        switch (message.getAction()) {
            case IDEventMessage.DEV_REGISTER_INFORM:
                Log.e("MyLog", "DeviceManagementFragment.getEventMessage:  设备寄存器变更 EventBus --->>> ");
                try {
                    //6.查询设备信息
                    InterfaceMain.pbui_Type_MeetDeviceBaseInfo object = (InterfaceMain.pbui_Type_MeetDeviceBaseInfo) message.getObject();
                    Log.e("MyLog", "DeviceManagementFragment.getEventMessage:  object.getAttribid() --->>> " + object.getAttribid()
                            + "  object.getDeviceid() ： " + object.getDeviceid());

                    nativeUtil.queryDeviceInfo();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
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
        mDevrceManageRl = (RecyclerView) inflate.findViewById(R.id.devrce_manage_rl);
        mDeviceNameEdt = (EditText) inflate.findViewById(R.id.device_name_edt);
        mDeviceIpEdt = (EditText) inflate.findViewById(R.id.device_ip_edt);
        mModifyBtn = (Button) inflate.findViewById(R.id.modify_btn);
        mDeleteBtn = (Button) inflate.findViewById(R.id.delete_btn);
        mModifyBtn.setOnClickListener(this);
        mDeleteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_btn:
                if (isModifi) {
                    mTextName = String.valueOf(mDeviceNameEdt.getText());
                    mTextIp = String.valueOf(mDeviceIpEdt.getText());
                    /** ************ ******  13修改设备信息  ****** ************ **/
                    nativeUtil.modifyDeviceInfo(
                            InterfaceMacro.Pb_DeviceModifyFlag.Pb_DEVICE_MODIFYFLAG_NAME.getNumber()
                                    | InterfaceMacro.Pb_DeviceModifyFlag.Pb_DEVICE_MODIFYFLAG_IPADDR.getNumber(),
                            checkedDevId, mTextName, mTextIp);
                    mAdapter.notifyDataSetChanged();
                    isModifi = false;
                } else {
                    Toast.makeText(getContext(), "请先选择您想要修改的选项！", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.delete_btn:
                if (isDelete) {
                    /** ************ ******  14删除设备  ****** ************ **/
                    Log.e("MyLog", "DeviceManagementFragment.onClick:  deviceid --->>> " + checkedDevId);
                    nativeUtil.deleteDevice(checkedDevId);
                    mAdapter.notifyDataSetChanged();
                    isDelete = false;
                }
                break;
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_DEVICE_INFO:
                InterfaceMain.pbui_Type_DeviceDetailInfo result1 = (InterfaceMain.pbui_Type_DeviceDetailInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryDeviceInfo", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }

}
