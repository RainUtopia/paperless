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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.pa.paperless.R;
import com.pa.paperless.adapter.setadapter.SeatArrangementAdapter;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventPlace;
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
 * 系统设置-座位排布Fragment
 */

public class SeatArrangementFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private NativeUtil nativeUtil;
    private RecyclerView mSeatLeftRl;
    private ImageView mSeatRightImg;
    private EditText mFileNameEdt;
    private Button mSeatLoadPic;
    private TextView mSeatShowPicinfo;
    private Button mSeatDeviceBind;
    //会议室名称数据
    private SeatArrangementAdapter mAdapter;
//    private NativeUtil nativeUtil;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_PLACE_INFO:
                    ArrayList queryPlace = msg.getData().getParcelableArrayList("queryPlace");
                    InterfaceRoom.pbui_Type_MeetRoomDetailInfo o = (InterfaceRoom.pbui_Type_MeetRoomDetailInfo) queryPlace.get(0);
                    itemList = o.getItemList();
                    mAdapter = new SeatArrangementAdapter(getContext(), itemList);
                    mSeatLeftRl.setLayoutManager(new LinearLayoutManager(getContext()));
//                    mSeatLeftRl.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL));
                    mSeatLeftRl.setAdapter(mAdapter);

                    mAdapter.setListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int posion) {
                            Toast.makeText(getContext(), "点击了" + posion, Toast.LENGTH_SHORT).show();
                            mAdapter.setCheckedId(posion);
                        }
                    });
                    break;
            }
        }
    };
    private List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> itemList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.seat_arrangement, container, false);

        initView(inflate);
        try {
            //112.查询会场
            nativeUtil.queryPlace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventPlace(EventPlace eventPlace) {
        Log.e("MyLog","SeatArrangementFragment.EventPlace:  查询会场 EventBus --->>> ");
        try {
            nativeUtil.queryPlace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    private void initView(View inflate) {
        mSeatLeftRl = (RecyclerView) inflate.findViewById(R.id.seat_left_rl);


        mSeatRightImg = (ImageView) inflate.findViewById(R.id.seat_right_img);
        mFileNameEdt = (EditText) inflate.findViewById(R.id.file_name_edt);
        mSeatLoadPic = (Button) inflate.findViewById(R.id.seat_load_pic);
        mSeatShowPicinfo = (TextView) inflate.findViewById(R.id.seat_show_picinfo);
        mSeatDeviceBind = (Button) inflate.findViewById(R.id.seat_device_bind);
        mSeatLoadPic.setOnClickListener(this);
        mSeatDeviceBind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seat_load_pic:

                break;
            case R.id.seat_device_bind:

                break;
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_PLACE_INFO:
                InterfaceRoom.pbui_Type_MeetRoomDetailInfo result1 = (InterfaceRoom.pbui_Type_MeetRoomDetailInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryPlace", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }
}
