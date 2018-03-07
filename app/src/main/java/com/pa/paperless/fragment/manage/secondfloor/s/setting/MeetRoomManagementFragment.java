package com.pa.paperless.fragment.manage.secondfloor.s.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.adapter.setadapter.MeetingRoomLeftAdapter;
import com.pa.paperless.adapter.setadapter.MeetingRoomTopAdapter;
import com.pa.paperless.bean.DeviceBean;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.PlaceInfo;
import com.pa.paperless.bean.RoomTopBean;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.ItemClickListener;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/16.
 * 系统设置-会议室管理
 */

public class MeetRoomManagementFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private NativeUtil nativeUtil;
    private RecyclerView mRoomTopRl;
    private RecyclerView mRoomLeftRl;
    private Button mRoomAddBtn;
    private Button mRoomDeleteBtn;
    private RecyclerView mRoomRightRl;
    private EditText mRoomNameEdt;
    private EditText mRoomMeetplaceEdt;
    private EditText mRoomNoteEdt;
    private Button mBottomAddBtn;
    private Button mBottomDeleteBtn;
    private Button mBottomAmendBtn;
    private List<RoomTopBean> mData;
    private MeetingRoomTopAdapter mPlaceAdapter;

    public static int mPosion;
    public static boolean isDelete;
    public static boolean isAmend;
    private TextView room_item_name;
    private TextView room_item_place;
    private TextView room_item_note;
    /**
     * *********** ******  左边RecyclerView  ****** ************
     **/
    private List<DeviceBean> mDeviceLeftDatas;
    private List<DeviceBean> mDeviceRightDatas;
    private MeetingRoomLeftAdapter mLeftAdapter;
    private TextView mLeftNumberTv;
    private TextView mLeftNameTv;
    private TextView mLeftTypeTv;
    private boolean SelectedLeft;
    public static boolean isDeleteLeft;
    private String leftNumberStr;
    private String leftNameStr;
    private String leftTypeStr;
    public static int SelectLeftPosition;
    /**
     * *********** ******  右边RecycleView  ****** ************
     **/
    private boolean SelectedRight;
    private MeetingRoomLeftAdapter mRightAdapter;
    private TextView mRightNumberTv;
    private TextView mRightNameTv;
    private TextView mRightTypeTv;
    private String rightNumberStr;
    private String rightNameStr;
    private String rightTypeStr;
    public static boolean isDeleteRight;
    public static int SelectRightPosition;
//    private NativeUtil nativeUtil;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_PLACE_INFO://查询会场
                    ArrayList queryPlaceInfo = msg.getData().getParcelableArrayList("queryPlaceInfo");
                    InterfaceMain.pbui_Type_MeetRoomDetailInfo o = (InterfaceMain.pbui_Type_MeetRoomDetailInfo) queryPlaceInfo.get(0);
                    itemList = o.getItemList();
                    mPlaceAdapter = new MeetingRoomTopAdapter(getContext(), itemList);
                    mRoomTopRl.setLayoutManager(new LinearLayoutManager(getContext()));
                    mRoomTopRl.setAdapter(mPlaceAdapter);

                    mPlaceAdapter.setOnItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int posion) {
                            isDelete = true;
                            isAmend = true;
                            //获取当前索引item中的控件
                            room_item_name = view.findViewById(R.id.room_item_name);
                            room_item_place = view.findViewById(R.id.room_item_place);
                            room_item_note = view.findViewById(R.id.room_item_note);
                            //将当前选中item 的名称和IP同步设置到EditText中
                            mRoomNameEdt.setText(room_item_name.getText());
                            mRoomMeetplaceEdt.setText(room_item_place.getText());
                            mRoomNoteEdt.setText(room_item_note.getText());

                            //保存当前点击索引
                            mPosion = posion;
                            mPlaceAdapter.setCheckedId(posion);
                            //获取当前点击item的会场ID
                            InterfaceMain.pbui_Item_MeetRoomDetailInfo pbui_item_meetRoomDetailInfo = itemList.get(posion);
                            int roomid = pbui_item_meetRoomDetailInfo.getRoomid();

                            try {
                                //6.查询设备信息
                                nativeUtil.queryDeviceInfo();
                                //122.查询会场设备坐标朝向信息  获取设备ID
                                nativeUtil.queryPlaceDeviceCoordFaceInfo(roomid);
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                    ArrayList queryDevInfo = msg.getData().getParcelableArrayList("queryDevInfo");
                    InterfaceMain.pbui_Type_DeviceDetailInfo o1 = (InterfaceMain.pbui_Type_DeviceDetailInfo) queryDevInfo.get(0);
                    pdevList = o1.getPdevList();
                    break;

                case IDivMessage.QUERY_PLACEDEV://122.查询会场设备坐标朝向信息  获取设备ID
                    /** ************ ******  根据设备ID过滤显示该会场绑定的设备  ****** ************ **/
                    ArrayList query_placedev = msg.getData().getParcelableArrayList("query_placedev");
                    InterfaceMain.pbui_Type_MeetRoomDevPosInfo o3 = (InterfaceMain.pbui_Type_MeetRoomDevPosInfo) query_placedev.get(0);
                    //获取该会场的设备（只能从中获取设备ID）
                    List<InterfaceMain.pbui_Item_MeetRoomDevPosInfo> itemList = o3.getItemList();
                    //新建一个集合用来存放会场设备
                    placeDev = new ArrayList<>();
                    //存放除去会场设备的所有设备
                    allDev = new ArrayList<>();
                    /** ************ ******  会议室设备  ****** ************ **/
                    Log.e("MyLog", "MeetRoomManagementFragment.handleMessage:  pdevList.size() --->>> " + pdevList.size());
                    for (int i = 0; i < itemList.size(); i++) {
                        for (int j = 0; j < pdevList.size(); j++) {
                            InterfaceMain.pbui_Item_MeetRoomDevPosInfo pbui_item_meetRoomDevPosInfo = itemList.get(i);
                            int devid = pbui_item_meetRoomDevPosInfo.getDevid();
                            InterfaceMain.pbui_Item_DeviceDetailInfo pbui_item_deviceDetailInfo = pdevList.get(j);
                            int devcieid = pbui_item_deviceDetailInfo.getDevcieid();
                            if (devcieid == devid) {
                                placeDev.add(pbui_item_deviceDetailInfo);
                            } else {
                                if (devcieid != devid && !allDev.contains(pdevList.get(j))) {
                                    allDev.add(pdevList.get(j));
                                }
                            }
                        }
                    }
                    /** ************ ******  会场设备  ****** ************ **/
                    mLeftAdapter = new MeetingRoomLeftAdapter(getContext(), placeDev);
                    mRoomLeftRl.setLayoutManager(new LinearLayoutManager(getContext()));
                    mRoomLeftRl.setAdapter(mLeftAdapter);
                    //会场设备 item点击事件
                    mLeftAdapter.setItemClick(new ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int posion) {
                            Log.e("MyLog", "MeetRoomManagementFragment.onItemClick:  会场设备的索引 --->>> " + posion);
                            //控制添加和删除按钮
                            SelectedLeft = true;
                            SelectedRight = false;
                            isDeleteLeft = true;
                            SelectLeftPosition = posion;
                            mLeftAdapter.setCheckedPosion(posion);
                        }
                    });
                    /** ************ ******  所有设备  ****** ************ **/
                    mRightAdapter = new MeetingRoomLeftAdapter(getContext(), allDev);
                    mRoomRightRl.setLayoutManager(new LinearLayoutManager(getContext()));
                    mRoomRightRl.setAdapter(mRightAdapter);
                    //所有设备item点击事件
                    mRightAdapter.setItemClick(new ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int posion) {
                            Log.e("MyLog", "MeetRoomManagementFragment.onItemClick:  所有设备的索引 --->>> " + posion);
                            //控制添加和删除按钮
                            isDeleteRight = true;
                            SelectedLeft = false;
                            SelectedRight = true;
                            SelectRightPosition = posion;
                            mRightAdapter.setCheckedPosion(posion);
                        }
                    });
                    break;
            }
        }
    };

    //  所有的会议室
    private List<InterfaceMain.pbui_Item_MeetRoomDetailInfo> itemList;
    //  所有的设备
    private List<InterfaceMain.pbui_Item_DeviceDetailInfo> pdevList;
    private List<InterfaceMain.pbui_Item_DeviceDetailInfo> placeDev;
    private List<InterfaceMain.pbui_Item_DeviceDetailInfo> allDev;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.meet_room_management, container, false);
        initView(inflate);
        initController();
        try {
            /** ************ ******  112查询会场    发送消息获取数据  ****** ************ **/
            nativeUtil.queryPlace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.PLACEINFO_CHANGE_INFORM:
                Log.e("MyLog", "MeetRoomManagementFragment.getEventMessage:  查询会场 --->>> ");
                nativeUtil.queryPlace();
                break;
            case IDEventMessage.DEV_REGISTER_INFORM:
                Log.e("MyLog", "MeetRoomManagementFragment.getEventMessage:  设备寄存器变更 EventBus --->>> ");
                //6.查询设备信息
                nativeUtil.queryDeviceInfo();
                break;
            case IDEventMessage.PLACE_DEVINFO_CHANGEINFORM:
                Log.e("MyLog", "MeetRoomManagementFragment.getEventMessage:  会场设备信息变更通知 EventBus --->>> ");
                InterfaceMain.pbui_MeetNotifyMsgForDouble o = (InterfaceMain.pbui_MeetNotifyMsgForDouble) message.getObject();
                int id = o.getId();
                //122.查询会场设备坐标朝向信息  获取设备ID
                nativeUtil.queryPlaceDeviceCoordFaceInfo(id);
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
        mRoomTopRl = (RecyclerView) inflate.findViewById(R.id.room_top_rl);
        mRoomLeftRl = (RecyclerView) inflate.findViewById(R.id.room_left_rl);
        mRoomRightRl = (RecyclerView) inflate.findViewById(R.id.room_right_rl);
        mRoomAddBtn = (Button) inflate.findViewById(R.id.room_add_btn);
        mRoomDeleteBtn = (Button) inflate.findViewById(R.id.room_delete_btn);

        mRoomNameEdt = (EditText) inflate.findViewById(R.id.room_name_edt);
        mRoomMeetplaceEdt = (EditText) inflate.findViewById(R.id.room_meetplace_edt);
        mRoomNoteEdt = (EditText) inflate.findViewById(R.id.room_note_edt);

        mRoomAddBtn.setOnClickListener(this);
        mRoomDeleteBtn.setOnClickListener(this);
        mBottomAddBtn = (Button) inflate.findViewById(R.id.bottom_add_btn);
        mBottomAddBtn.setOnClickListener(this);
        mBottomDeleteBtn = (Button) inflate.findViewById(R.id.bottom_delete_btn);
        mBottomDeleteBtn.setOnClickListener(this);
        mBottomAmendBtn = (Button) inflate.findViewById(R.id.bottom_amend_btn);
        mBottomAmendBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.room_add_btn:
                if (SelectedRight && isDeleteRight) {
                    //将右边的item添加到左边
                    //从data中获取的值才是正确，改变过的值
                    InterfaceMain.pbui_Item_DeviceDetailInfo pbui_item_deviceDetailInfo = allDev.get(SelectRightPosition);
                    mLeftAdapter.addItem(allDev.size(), pbui_item_deviceDetailInfo);
                    mRightAdapter.removeItem(SelectRightPosition);
//                    DeviceBean deviceBean = mDeviceRightDatas.get(SelectRightPosition);
//                    mLeftAdapter.addItem(mDeviceLeftDatas.size(), deviceBean);
//                    mRightAdapter.removeItem(SelectRightPosition);
                }
                break;
            case R.id.room_delete_btn:
                if (SelectedLeft && isDeleteLeft) {
                    //将左边的item添加到右边
                    InterfaceMain.pbui_Item_DeviceDetailInfo pbui_item_deviceDetailInfo = placeDev.get(SelectLeftPosition);
                    mRightAdapter.addItem(allDev.size(), pbui_item_deviceDetailInfo);
                    mLeftAdapter.removeItem(SelectLeftPosition);
//                    DeviceBean deviceBean = mDeviceLeftDatas.get(SelectLeftPosition);
//                    mRightAdapter.addItem(mDeviceRightDatas.size(), deviceBean);
//                    mLeftAdapter.removeItem(SelectLeftPosition);
                }
                break;
            case R.id.bottom_add_btn:
                //会场ID
                //会场底图ID
                //管理员ID
                //名称 地址 备注
                String addName = String.valueOf(mRoomNameEdt.getText());
                String addPlace = String.valueOf(mRoomMeetplaceEdt.getText());
                String addNote = String.valueOf(mRoomNoteEdt.getText());
                //会场名称
                ByteString name = ByteString.copyFrom(addName, Charset.forName("UTF-8"));
                //会场地址
                ByteString place = ByteString.copyFrom(addPlace, Charset.forName("UTF-8"));
                //会场备注
                ByteString comment = ByteString.copyFrom(addNote, Charset.forName("UTF-8"));
                PlaceInfo placeInfo = new PlaceInfo(addName, addPlace, addNote);
                nativeUtil.addPlace(placeInfo);

                break;
            case R.id.bottom_delete_btn:
                if (isDelete) {
                    mPlaceAdapter.removeItem(mPosion);
                }
                break;
            case R.id.bottom_amend_btn:
                if (isAmend) {
//                    String name = String.valueOf(mRoomNameEdt.getText());
//                    String place = String.valueOf(mRoomMeetplaceEdt.getText());
//                    String note = String.valueOf(mRoomNoteEdt.getText());
//                    room_item_name.setText(name);
//                    room_item_place.setText(place);
//                    room_item_note.setText(note);
//                    isAmend = false;
                } else {
                    Toast.makeText(getContext(), "请先选择您想要修改的选项！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_PLACE_INFO:
                InterfaceMain.pbui_Type_MeetRoomDetailInfo result1 = (InterfaceMain.pbui_Type_MeetRoomDetailInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryPlaceInfo", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_DEVICE_INFO:
                InterfaceMain.pbui_Type_DeviceDetailInfo result2 = (InterfaceMain.pbui_Type_DeviceDetailInfo) result;
                if (result2 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result2);
                    bundle.putParcelableArrayList("queryDevInfo", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;

            case IDivMessage.QUERY_CONFORM_DEVID:
                InterfaceMain.pbui_Type_ResMeetRoomDevInfo result3 = (InterfaceMain.pbui_Type_ResMeetRoomDevInfo) result;
                if (result3 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result3);
                    bundle.putParcelableArrayList("queryConformDevId", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_PLACEDEV:
                InterfaceMain.pbui_Type_MeetRoomDevPosInfo result4 = (InterfaceMain.pbui_Type_MeetRoomDevPosInfo) result;
                if (result4 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result4);
                    bundle.putParcelableArrayList("query_placedev", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }
}
