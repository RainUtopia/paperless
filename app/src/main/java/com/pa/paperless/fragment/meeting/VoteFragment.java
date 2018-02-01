package com.pa.paperless.fragment.meeting;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.adapter.VoteAdapter;
import com.pa.paperless.bean.VoteInfo;
import com.pa.paperless.bean.VoteOptionsInfo;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventVote;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.Dispose;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 * 投票查询
 */

public class VoteFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private RecyclerView mVoteRl;
    private Button mVoteQuery;
    private Button mVoteExport;
    private Button mVoteManagement;
    private Button mVoteOpen;
    private PopupWindow mPopupWindow;
    private RelativeLayout mHostFunction;
    private NativeUtil nativeUtil;
    private List<VoteInfo> mVoteData = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_VOTE:
                    mVoteData.clear();
                    ArrayList queryVote = msg.getData().getParcelableArrayList("queryVote");
                    InterfaceMain2.pbui_Type_MeetVoteDetailInfo o = (InterfaceMain2.pbui_Type_MeetVoteDetailInfo) queryVote.get(0);
                    mVoteData = Dispose.Vote(o);
                    mVoteAdapter = new VoteAdapter(getContext(), mVoteData);
                    mVoteRl.setAdapter(mVoteAdapter);
                    break;
            }
        }
    };
    private VoteAdapter mVoteAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_vote, container, false);
        initController();
        initView(inflate);
        try {
            //200.查询投票
            nativeUtil.queryVote();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventVote(EventVote eventVote) {
        Log.e("MyLog", "VoteFragment.EventVote:  查询投票 EventBus 111--->>> ");
        try {
            //200.查询投票
            nativeUtil.queryVote();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
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
        nativeUtil.setCallListener(this);
    }

    private void initView(View inflate) {
        mVoteRl = (RecyclerView) inflate.findViewById(R.id.vote_rl);
        mVoteRl.setLayoutManager(new LinearLayoutManager(getContext()));

        mVoteQuery = (Button) inflate.findViewById(R.id.vote_query);
        mVoteExport = (Button) inflate.findViewById(R.id.vote_export);
        mVoteManagement = (Button) inflate.findViewById(R.id.vote_management);
        mVoteOpen = (Button) inflate.findViewById(R.id.vote_open);
        mHostFunction = (RelativeLayout) inflate.findViewById(R.id.host_function);
        // TODO: 2018/1/20 如果是主持人则展示主持人功能
        if (false) {
            mHostFunction.setVisibility(View.VISIBLE);
            mVoteOpen.setVisibility(View.VISIBLE);
        }
        mVoteOpen.setOnClickListener(this);
        mVoteManagement.setOnClickListener(this);
        mVoteQuery.setOnClickListener(this);
        mVoteExport.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vote_query:       //结果查询

                break;
            case R.id.vote_export:      //结果导出

                break;
            case R.id.vote_management:  //投票管理

                break;
            case R.id.vote_open:        //发起投票
                showPop();
                break;
        }
    }

    private void showPop() {
        View popupView = getActivity().getLayoutInflater().inflate(R.layout.pop_vote, null);
        mPopupWindow = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(getActivity().findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }


    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_VOTE:
                Log.e("MyLog", "VoteFragment.callListener:  111 --->>> ");
                InterfaceMain2.pbui_Type_MeetVoteDetailInfo result1 = (InterfaceMain2.pbui_Type_MeetVoteDetailInfo) result;
                if (result1 != null) {
                    Log.e("MyLog", "VoteFragment.callListener:  222 --->>> ");
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryVote", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }
}
