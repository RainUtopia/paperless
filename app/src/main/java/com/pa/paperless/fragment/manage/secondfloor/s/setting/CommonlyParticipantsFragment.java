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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.adapter.setadapter.ParticipantsAdapter;
import com.pa.paperless.bean.AttendeesBean;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.controller.SetController;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.ItemClickListener;
//import com.pa.paperless.utils.RecycleViewDivider;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/16.
 * 系统设置-常用参会人
 * Fragment
 */

public class CommonlyParticipantsFragment extends BaseFragment implements View.OnClickListener, CallListener {
    private RecyclerView mCommonlyRl;
    private EditText mCommonlyNameEdt;
    private EditText mCommonlyUnitEdt;
    private EditText mCommonlyPositionEdt;
    private EditText mCommonlyNoteEdt;
    private LinearLayout mCimmonlyPlace;
    private Button mCommonlyAddBtn;
    private Button mCommonlyAmendBtn;
    private Button mCommonlyDeleteBtn;
    private Button mFromExcelBtn;
    private Button mOuttoExcelBtn;
    private List<AttendeesBean> mData;

    private ParticipantsAdapter mAdapter;
    private TextView mItemNameTv;
    private TextView mItemUnitTv;
    private TextView mItemJobTv;
    private TextView mItemNoteTv;
    public static int mPosion;
    public static boolean isDelete;
    public static boolean isAmend;
//    private NativeUtil nativeUtil;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_COMMON_PEOPLE:
                    ArrayList queryCommonPeople = msg.getData().getParcelableArrayList("queryCommonPeople");
                    InterfaceMain.pbui_Type_PersonDetailInfo o = (InterfaceMain.pbui_Type_PersonDetailInfo) queryCommonPeople.get(0);
                    itemList = o.getItemList();

                    mAdapter = new ParticipantsAdapter(getContext(), itemList);
                    mCommonlyRl.setLayoutManager(new LinearLayoutManager(getContext()));
//                    mCommonlyRl.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL));
                    mCommonlyRl.setAdapter(mAdapter);
                    initEvent();
                    break;
            }
        }
    };
    private List<InterfaceMain.pbui_Item_PersonDetailInfo> itemList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.commonly_partic, container, false);
        initController();
        initView(inflate);
        try {
            //75.查询常用人员
            nativeUtil.queryCommonPeople();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.QUERY_COMMON_PEOPLE:
                Log.e("MyLog", "CommonlyParticipantsFragment.getEventMessage:  查询常用人员 EventBus --->>> ");
                nativeUtil.queryCommonPeople();
                break;
        }
    }

    private void initEvent() {
        mAdapter.setListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                Toast.makeText(getContext(), "点击了" + posion, Toast.LENGTH_SHORT).show();
                mPosion = posion;
                isDelete = true;
                isAmend = true;
                //获取item组件
                mItemNameTv = view.findViewById(R.id.participants_name);
                mItemUnitTv = view.findViewById(R.id.participants_unit);
                mItemJobTv = view.findViewById(R.id.participants_job);
                mItemNoteTv = view.findViewById(R.id.participants_note);
                //获取item组件的信息
                String mItemNameStr = mItemNameTv.getText().toString();
                String mItemUnitStr = mItemUnitTv.getText().toString();
                String mItemJobStr = mItemJobTv.getText().toString();
                String mItemNoteStr = mItemNoteTv.getText().toString();
                //同步信息到EditText中
                mCommonlyNameEdt.setText(mItemNameStr);
                mCommonlyUnitEdt.setText(mItemUnitStr);
                mCommonlyPositionEdt.setText(mItemJobStr);
                mCommonlyNoteEdt.setText(mItemNoteStr);
                mAdapter.setCheckedId(posion);
            }
        });


    }


    @Override
    protected void initController() {
        nativeUtil = NativeUtil.getInstance();
//        nativeUtil = new NativeUtil();
        nativeUtil.setCallListener(this);
    }

    private void initView(View inflate) {
        mCommonlyRl = (RecyclerView) inflate.findViewById(R.id.commonly_rl);

        mCommonlyNameEdt = (EditText) inflate.findViewById(R.id.commonly_name_edt);
        mCommonlyUnitEdt = (EditText) inflate.findViewById(R.id.commonly_unit_edt);
        mCommonlyPositionEdt = (EditText) inflate.findViewById(R.id.commonly_position_edt);
        mCommonlyNoteEdt = (EditText) inflate.findViewById(R.id.commonly_note_edt);

        mCimmonlyPlace = (LinearLayout) inflate.findViewById(R.id.cimmonly_place);
        mCommonlyAddBtn = (Button) inflate.findViewById(R.id.commonly_add_btn);
        mCommonlyAmendBtn = (Button) inflate.findViewById(R.id.commonly_amend_btn);
        mCommonlyDeleteBtn = (Button) inflate.findViewById(R.id.commonly_delete_btn);
        mFromExcelBtn = (Button) inflate.findViewById(R.id.from_excel_btn);
        mOuttoExcelBtn = (Button) inflate.findViewById(R.id.outto_excel_btn);

        mCommonlyAddBtn.setOnClickListener(this);
        mCommonlyAmendBtn.setOnClickListener(this);
        mCommonlyDeleteBtn.setOnClickListener(this);
        mFromExcelBtn.setOnClickListener(this);
        mOuttoExcelBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commonly_add_btn:
                String getEdtName = mCommonlyNameEdt.getText().toString();
                String getEdtUnit = mCommonlyUnitEdt.getText().toString();
                String getEdtPosition = mCommonlyPositionEdt.getText().toString();
                String getEdtNote = mCommonlyNoteEdt.getText().toString();
                /** ************ ******  76.添加常用人员  ****** ************ **/
                int personid = itemList.get(itemList.size() - 1).getPersonid();
                nativeUtil.addCommonPeople(personid + 1, getEdtName, getEdtUnit, getEdtPosition, getEdtNote, "", "", "");
//                mAdapter.notifyDataSetChanged();
                break;
            case R.id.commonly_amend_btn:
                if (isAmend) {
                    String mItemNameStr = mCommonlyNameEdt.getText().toString();
                    String mItemUnitStr = mCommonlyUnitEdt.getText().toString();
                    String mItemJobStr = mCommonlyPositionEdt.getText().toString();
                    String mItemNoteStr = mCommonlyNoteEdt.getText().toString();
                    isAmend = false;
                    /** ************ ******  77.修改常用人员  ****** ************ **/

                    nativeUtil.modifCommonPeople(mPosion, mItemNameStr, mItemUnitStr, mItemJobStr, mItemNoteStr, "", "", "");
                } else {
                    Toast.makeText(getContext(), "请先选择您想要修改的选项！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.commonly_delete_btn:
                if (isDelete) {
                    mAdapter.removeItem(mPosion);
                    /** ************ ******  78删除常用人员  ****** ************ **/
//                    nativeUtil.deleteCommonPeople(mPosion);
                }
                break;
            case R.id.from_excel_btn:

                break;
            case R.id.outto_excel_btn:

                break;
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_COMMON_PEOPLE:
                InterfaceMain.pbui_Type_PersonDetailInfo result1 = (InterfaceMain.pbui_Type_PersonDetailInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryCommonPeople", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }
}
