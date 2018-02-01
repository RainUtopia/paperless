package com.pa.paperless.fragment.manage.secondfloor.s.prepare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pa.paperless.R;
import com.pa.paperless.adapter.preadapter.MeetManageAdapter;
import com.pa.paperless.bean.MeetManageBean;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.pa.paperless.listener.ItemClickListener;
//import com.pa.paperless.utils.RecycleViewDivider;
import com.pa.paperless.views.DateTimeButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/17.
 * 会前准备-会议管理
 */


public class MeettingManagement_Fragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mMeetManageRl;
    private TextView mMeetShowError;
    private EditText mMeetNameEdt;
    private Spinner mSpinnerRoom;
    private Spinner mSpinnerSecret;
    private DateTimeButton mBeginDateEdt;
    private DateTimeButton mEndDateEdt;
    private Button mMeetAddBtn;
    private Button mMeetAmendBtn;
    private Button mMeetDeleteBtn;
    private Button mBeginMeeting;
    private Button mOverMeeting;
    private List<MeetManageBean> mData;
    private MeetManageAdapter mAdapter;
    private List<String> mSpinnerData ;
    private String[] YorN = {" 是 "," 否 "};
    public static int mPosion;
    public static boolean isDelete;
    private String spinnerSelectedRoomName;
    private String spinnerIsSecret;
    private TextView meetNameTv;
    private TextView meetRoomNameTv;
    private TextView meetSecretTv;
    private TextView meetStartTv;
    private TextView meetOverTv;
    private TextView meetStateTv;
    public static boolean isAmend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.meeting_management, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        /** ******          RecyclerView         ****** **/


        mMeetManageRl = (RecyclerView) inflate.findViewById(R.id.meet_manage_rl);
        mData = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            mData.add(new MeetManageBean("会议名称"+i,"未开始","会议室名称","否","2011-01-01","2012-01-01"));
        }
        mAdapter = new MeetManageAdapter(getContext(),mData);
        mMeetManageRl.setLayoutManager(new LinearLayoutManager(getContext()));
//        mMeetManageRl.addItemDecoration(new RecycleViewDivider(getContext(),LinearLayoutManager.HORIZONTAL));
        mAdapter.setListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                Toast.makeText(getContext(), "点击了"+posion, Toast.LENGTH_SHORT).show();
                mPosion = posion;
                isDelete = true;
                isAmend = true;
                meetNameTv = view.findViewById(R.id.meetmanage_item_meetname);
                meetRoomNameTv = view.findViewById(R.id.meetmanage_item_roomname);
                meetSecretTv = view.findViewById(R.id.meetmanage_item_secret);
                meetStateTv = view.findViewById(R.id.meetmanage_item_meetstate);
                meetStartTv = view.findViewById(R.id.meetmanage_item_starttime);
                meetOverTv = view.findViewById(R.id.meetmanage_item_overtime);
                mMeetNameEdt.setText(meetNameTv.getText().toString());
                mBeginDateEdt.setText(meetStartTv.getText().toString());
                mEndDateEdt.setText(meetOverTv.getText().toString());
                mAdapter.setCheckedId(posion);
            }
        });
        mMeetManageRl.setAdapter(mAdapter);

        /** ******          Spinner         ****** **/

        mSpinnerRoom = (Spinner) inflate.findViewById(R.id.spinner_room);
        mSpinnerData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mSpinnerData.add("Spinner_"+i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,mSpinnerData);
        mSpinnerRoom.setAdapter(adapter);
        mSpinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelectedRoomName = mSpinnerData.get(i);
                adapterView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /** ************ ******    ****** ************ **/

        mSpinnerSecret = (Spinner) inflate.findViewById(R.id.spinner_secret);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,YorN);
        mSpinnerSecret.setAdapter(adapter1);
        mSpinnerSecret.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerIsSecret = YorN[i];
                adapterView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /** ******          开始时间选择         ****** **/
        mBeginDateEdt = (DateTimeButton) inflate.findViewById(R.id.begin_date_edt);
        /** ******          结束时间选择         ****** **/
        mEndDateEdt = (DateTimeButton) inflate.findViewById(R.id.end_date_edt);
        mMeetNameEdt = (EditText)inflate.findViewById(R.id.meet_name_edt);
        mMeetShowError = (TextView) inflate.findViewById(R.id.meet_show_error);
        mMeetAddBtn = (Button) inflate.findViewById(R.id.meet_add_btn);
        mMeetAmendBtn = (Button) inflate.findViewById(R.id.meet_amend_btn);
        mMeetDeleteBtn = (Button) inflate.findViewById(R.id.meet_delete_btn);
        mBeginMeeting = (Button) inflate.findViewById(R.id.begin_meeting);
        mOverMeeting = (Button) inflate.findViewById(R.id.over_meeting);

        mMeetAddBtn.setOnClickListener(this);
        mMeetAmendBtn.setOnClickListener(this);
        mMeetDeleteBtn.setOnClickListener(this);
        mBeginMeeting.setOnClickListener(this);
        mOverMeeting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meet_add_btn:
                String name = mMeetNameEdt.getText().toString();
                String begin = mBeginDateEdt.getText().toString();
                String end = mEndDateEdt.getText().toString();
                mData.add(mData.size(),new MeetManageBean(name,"未开始",spinnerSelectedRoomName,spinnerIsSecret,begin,end));
                mAdapter.notifyItemInserted(mData.size());
                break;
            case R.id.meet_amend_btn:
                if(isAmend) {
                    meetNameTv.setText(mMeetNameEdt.getText().toString());
                    meetRoomNameTv.setText(spinnerSelectedRoomName);
                    // TODO: 2017/11/23 会议状态需要传值
//                meetStateTv.setText();
                    meetSecretTv.setText(spinnerIsSecret);
                    meetStartTv.setText(mBeginDateEdt.getText().toString());
                    meetOverTv.setText(mEndDateEdt.getText().toString());
                    isAmend = false;
                }else {
                    Toast.makeText(getContext(), "请选中您想要修改的选项！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.meet_delete_btn:
                if(isDelete){
                    mAdapter.removeItem(mPosion);
                }
                break;
            case R.id.begin_meeting:
                mData.get(mPosion).setMeetState("正在进行");
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.over_meeting:
                mData.get(mPosion).setMeetState("已结束");
                mAdapter.notifyDataSetChanged();
                break;
        }
    }
}