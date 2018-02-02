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

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.adapter.setadapter.SecretaryTopAdapter;
import com.pa.paperless.adapter.setadapter.VenueLeftAdapter;
import com.pa.paperless.adapter.setadapter.VenueRightAdapter;
import com.pa.paperless.bean.AdminInfo;
import com.pa.paperless.bean.AdminsBean;
import com.pa.paperless.bean.VenueBean;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.controller.SetController;
import com.pa.paperless.event.EventAdmin;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.Dispose;
//import com.pa.paperless.utils.RecycleViewDivider;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/16.
 * 系统设置-秘书管理Fragment
 */

public class SecretaryManagementFragment extends BaseFragment implements View.OnClickListener, CallListener {
    private RecyclerView mSecretaryTopRl;
    private RecyclerView mSecretaryLeftRl;
    private RecyclerView mSecretaryRightRl;
    private Button mSecretaryAddallBtn;
    private Button mSecretaryAddBtn;
    private Button mSecretaryDeleteallBtn;
    private Button mSecretaryDeleteBtn;
    private EditText mSecretaryUsernameEdt;
    private EditText mSecretaryPasswordEdt;
    private EditText mSecretaryNoteEdt;
    private Button mSecretaryCreateBtn;
    private Button mSecretaryDeleteBoottom;
    private Button mSecretaryAmendBtn;
    private List<AdminsBean> mTopData;
    public static int mPosion;
    public static boolean isDelete;
    public static boolean isAmend;
    private TextView topNameTv;
    private TextView topPasswordTv;
    private TextView topNoteTv;
    /**
     * *****          Adapter         ******
     **/
    private SecretaryTopAdapter mTopAdapter;
    private List<VenueBean> mLeftDatas;
    private List<VenueBean> mRightDatas;
    private VenueLeftAdapter mLeftAdapter;
    private VenueLeftAdapter mRightAdapter;
    //是否选中左/右边的item
    private boolean isSelectLeft;
    private boolean isSelectRight;
    //点击item时的索引
    public static int SelectLeftPosition;
    public static int SelectRightPosition;
    //是否可以删除
    public static boolean isDeleteLeft;
    public static boolean isDeleteRight;
//    private NativeUtil nativeUtil;
    private int leftPosion;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_ADMIN_INFO://52.查询管理员
                    ArrayList queryAdminInfo = msg.getData().getParcelableArrayList("queryAdminInfo");
                    InterfaceMain.pbui_TypeAdminDetailInfo o = (InterfaceMain.pbui_TypeAdminDetailInfo) queryAdminInfo.get(0);
                    adminInfos = o.getItemList();

                    mTopAdapter = new SecretaryTopAdapter(getContext(), adminInfos);
                    mSecretaryTopRl.setLayoutManager(new LinearLayoutManager(getContext()));
//                    mSecretaryTopRl.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL));
                    mSecretaryTopRl.setAdapter(mTopAdapter);
                    //item点击事件
                    mTopAdapter.setListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int posion) {
                            mPosion = posion;
                            isDelete = true;
                            isAmend = true;
                            topNameTv = view.findViewById(R.id.srcretary_item_name);
                            topPasswordTv = view.findViewById(R.id.srcretary_item_password);
                            topNoteTv = view.findViewById(R.id.srcretary_item_note);
                            mSecretaryUsernameEdt.setText(topNameTv.getText());
                            mSecretaryPasswordEdt.setText(topPasswordTv.getText());
                            mSecretaryNoteEdt.setText(topNoteTv.getText());
                            mTopAdapter.setCheckedId(posion);
                            InterfaceMain.pbui_Item_AdminDetailInfo pbui_item_adminDetailInfo = adminInfos.get(posion);
                            int adminid = pbui_item_adminDetailInfo.getAdminid();
                            Log.e("MyLog", "SecretaryManagementFragment.onItemClick:  adminid --->>> " + adminid);
                            try {
                                //112.查询会场
                                nativeUtil.queryPlace();
                                //60.查询会议管理员控制的会场
//                                nativeUtil.queryMeetManagerPlace();
                                //118.查询指定ID的会场
                                nativeUtil.queryPlaceById(adminid);
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case IDivMessage.QUERY_ADMIN_PLACE://60.查询会议管理员控制的会场
                    ArrayList queryAdminPlace = msg.getData().getParcelableArrayList("queryAdminPlace");
                    InterfaceMain.pbui_Type_MeetManagerRoomDetailInfo o1 = (InterfaceMain.pbui_Type_MeetManagerRoomDetailInfo) queryAdminPlace.get(0);
                    //会场ID
                    List<Integer> roomidList = o1.getRoomidList();
                    //管理员ID
                    int mgrid = o1.getMgrid();
                    for (int i = 0; i < roomidList.size(); i++) {
                        Integer integer = roomidList.get(i);
                        Log.e("MyLog", "SecretaryManagementFragment.handleMessage:  查询会议管理员控制的会场获得的管理员ID --->>> " + integer.toString());
                    }
                    break;
                case IDivMessage.QUERY_PLACE_BYID://118.查询指定ID的会场
                    ArrayList queryPlaceById = msg.getData().getParcelableArrayList("queryPlaceById");
                    InterfaceMain.pbui_Type_MeetRoomDetailInfo o2 = (InterfaceMain.pbui_Type_MeetRoomDetailInfo) queryPlaceById.get(0);
                    itemList = o2.getItemList();
                    mLeftAdapter = new VenueLeftAdapter(getContext(), itemList);
                    mSecretaryLeftRl.setLayoutManager(new LinearLayoutManager(getContext()));
//                    mSecretaryLeftRl.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL));
                    mSecretaryLeftRl.setAdapter(mLeftAdapter);


                    mLeftAdapter.setListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int posion) {
                            // TODO: 2017/11/22 左边
                            //控制按钮是否可以添加/删除
                            isDeleteLeft = true;
                            isSelectLeft = true;
                            isSelectRight = false;
                            SelectLeftPosition = posion;
                            mLeftAdapter.setCheckedId(posion);
                        }
                    });
                    break;
                case IDivMessage.QUERY_PLACE_INFO://112.查询会场
                    ArrayList queryPlaceInfo = msg.getData().getParcelableArrayList("queryPlaceInfo");
                    InterfaceMain.pbui_Type_MeetRoomDetailInfo o3 = (InterfaceMain.pbui_Type_MeetRoomDetailInfo) queryPlaceInfo.get(0);
                    itemListAll = o3.getItemList();

                    mRightAdapter = new VenueLeftAdapter(getContext(), itemListAll);
                    mSecretaryRightRl.setLayoutManager(new LinearLayoutManager(getContext()));
//                    mSecretaryRightRl.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL));
                    mSecretaryRightRl.setAdapter(mRightAdapter);

                    mRightAdapter.setListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int posion) {
                            // TODO: 2018/1/25 右边
                            //控制是否可以添加或删除
                            isDeleteRight = true;
                            isSelectLeft = false;
                            isSelectRight = true;
                            SelectRightPosition = posion;
                            mRightAdapter.setCheckedId(posion);
                        }
                    });
                    break;
            }
        }
    };
    private List<InterfaceMain.pbui_Item_AdminDetailInfo> adminInfos;
    private List<InterfaceMain.pbui_Item_MeetRoomDetailInfo> itemListAll;
    private List<InterfaceMain.pbui_Item_MeetRoomDetailInfo> itemList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.secretary_management, container, false);
        initController();
        initView(inflate);
        try {
            nativeUtil.queryAdmin();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.PLACEINFO_CHANGE_INFORM:
                Log.e("MyLog", "SecretaryManagementFragment.EventMessage:  查询会场 EventBus --->>> ");
                nativeUtil.queryPlace();
                break;
            case IDEventMessage.QUERY_PLACE_BYID:
                Log.e("MyLog", "SecretaryManagementFragment.EventMessage:  查询指定ID的会场 EventBus --->>> " + message.getType());
                nativeUtil.queryPlaceById(message.getType());
                break;
            case IDEventMessage.ADMIN_NOTIFI_INFORM:
                Log.e("MyLog", "SecretaryManagementFragment.EventMessage:  查询管理员 EventBus --->>> ");
                nativeUtil.queryAdmin();
                break;
            case IDEventMessage.MEETADMIN_PLACE_NOTIFIINFROM:
                Log.e("MyLog", "SecretaryManagementFragment.EventMessage:  查询会议管理员控制的会场 EventBus --->>> ");
                nativeUtil.queryMeetManagerPlace();
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
        mSecretaryTopRl = (RecyclerView) inflate.findViewById(R.id.secretary_top_rl);
        mSecretaryLeftRl = (RecyclerView) inflate.findViewById(R.id.secretary_left_rl);
        mSecretaryRightRl = (RecyclerView) inflate.findViewById(R.id.secretary_right_rl);

        mSecretaryAddallBtn = (Button) inflate.findViewById(R.id.secretary_addall_btn);
        mSecretaryAddBtn = (Button) inflate.findViewById(R.id.secretary_add_btn);
        mSecretaryDeleteallBtn = (Button) inflate.findViewById(R.id.secretary_deleteall_btn);
        mSecretaryDeleteBtn = (Button) inflate.findViewById(R.id.secretary_delete_btn);
        mSecretaryUsernameEdt = (EditText) inflate.findViewById(R.id.secretary_username_edt);
        mSecretaryPasswordEdt = (EditText) inflate.findViewById(R.id.secretary_password_edt);
        mSecretaryNoteEdt = (EditText) inflate.findViewById(R.id.secretary_note_edt);
        mSecretaryCreateBtn = (Button) inflate.findViewById(R.id.secretary_create_btn);
        mSecretaryDeleteBoottom = (Button) inflate.findViewById(R.id.secretary_delete_boottom);
        mSecretaryAmendBtn = (Button) inflate.findViewById(R.id.secretary_amend_btn);


        mSecretaryAddallBtn.setOnClickListener(this);
        mSecretaryAddBtn.setOnClickListener(this);
        mSecretaryDeleteallBtn.setOnClickListener(this);
        mSecretaryDeleteBtn.setOnClickListener(this);
        mSecretaryCreateBtn.setOnClickListener(this);
        mSecretaryDeleteBoottom.setOnClickListener(this);
        mSecretaryAmendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.secretary_addall_btn:
                //将右边的全部添加到左边
//                mLeftAdapter.addAll(mRightDatas);
//                mRightAdapter.removeAll();
                break;
            case R.id.secretary_add_btn:
                if (isSelectRight && isDeleteRight) {
                    //将右边的item添加到左边
                    //从data中获取的值才是正确的值，改变过的值
                    if (mRightDatas.size() > 0) {//添加全部之后SelectRightPosition并不会改变，而data已经为null，所以需要判断
                        VenueBean venueBean = mRightDatas.get(SelectRightPosition);
//                        mLeftAdapter.addItem(mLeftDatas.size(), venueBean);
                        mRightAdapter.removeItem(SelectRightPosition);
                    }
                }
                break;
            case R.id.secretary_deleteall_btn://移除全部
                //将左边的全部添加到右边
//                mRightAdapter.addAll(mLeftDatas);
                mLeftAdapter.removeAll();
                break;
            case R.id.secretary_delete_btn://移除
                if (isSelectLeft && isDeleteLeft) {
                    //将左边的item添加到右边
                    if (mLeftDatas.size() > 0) {
                        VenueBean venueBean = mLeftDatas.get(SelectLeftPosition);
//                        mRightAdapter.addItem(mRightDatas.size(), venueBean);
                        mLeftAdapter.removeItem(SelectLeftPosition);
                    }
                }
                break;
            case R.id.secretary_create_btn://新建
                String nameStr = String.valueOf(mSecretaryUsernameEdt.getText());
                String passwordStr = String.valueOf(mSecretaryPasswordEdt.getText());
                String noteStr = String.valueOf(mSecretaryNoteEdt.getText());
                InterfaceMain.pbui_Item_AdminDetailInfo defaultInstance = InterfaceMain.pbui_Item_AdminDetailInfo.getDefaultInstance();
//                defaultInstance.parseFrom();
                nativeUtil.addAdmin(defaultInstance);
//                mTopAdapter.inserted(mTopData.size(), new AdminInfo(nameStr, passwordStr, noteStr));
                break;
            case R.id.secretary_delete_boottom:
                if (isDelete) {
                    mTopAdapter.removeItem(mPosion);
                }
                break;
            case R.id.secretary_amend_btn:
                if (isAmend) {
                    String amendnameStr = String.valueOf(mSecretaryUsernameEdt.getText());
                    String amendpasswordStr = String.valueOf(mSecretaryPasswordEdt.getText());
                    String amendnoteStr = String.valueOf(mSecretaryNoteEdt.getText());
                    topNameTv.setText(amendnameStr);
                    topPasswordTv.setText(amendpasswordStr);
                    topNoteTv.setText(amendnoteStr);
                    isAmend = false;
                } else {
                    Toast.makeText(getContext(), "请先选择您想要修改的选项！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_ADMIN_INFO:
                InterfaceMain.pbui_TypeAdminDetailInfo result1 = (InterfaceMain.pbui_TypeAdminDetailInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryAdminInfo", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_ADMIN_PLACE:
                InterfaceMain.pbui_Type_MeetManagerRoomDetailInfo result2 = (InterfaceMain.pbui_Type_MeetManagerRoomDetailInfo) result;
                if (result2 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result2);
                    bundle.putParcelableArrayList("queryAdminPlace", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_PLACE_BYID:
                InterfaceMain.pbui_Type_MeetRoomDetailInfo result3 = (InterfaceMain.pbui_Type_MeetRoomDetailInfo) result;
                if (result3 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result3);
                    bundle.putParcelableArrayList("queryPlaceById", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_PLACE_INFO:
                InterfaceMain.pbui_Type_MeetRoomDetailInfo result4 = (InterfaceMain.pbui_Type_MeetRoomDetailInfo) result;
                if (result4 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result4);
                    bundle.putParcelableArrayList("queryPlaceInfo", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }
}
