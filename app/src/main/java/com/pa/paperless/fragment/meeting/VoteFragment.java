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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.adapter.VoteAdapter;
import com.pa.paperless.adapter.VoteInfoPopAdapter;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.bean.VoteBean;
import com.pa.paperless.bean.VoteInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.ItemClickListener;
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
    private PopupWindow mVoteInfoPop;
    private RelativeLayout mHostFunction;
    private List<VoteInfo> mVoteData = new ArrayList<>();
    private int mPosion;
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
                    if (!isCompere) {
                        mVoteAdapter.setItemListener(new ItemClickListener() {
                            @Override
                            public void onItemClick(View view, int posion) {
                                Log.e("MyLog", "VoteFragment.onItemClick:  不是主持人点击 --->>> " + posion);
                                mVoteAdapter.setCheckedId(posion);
                            }
                        });
                        break;
                    }
                    mVoteAdapter.setItemListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(View view, final int posion) {
                            Log.e("MyLog", "VoteFragment.onItemClick:  主持人点击 --->>> " + posion);
                            mVoteAdapter.setCheckedId(posion);
                            mPosion = posion;
                            VoteInfo voteInfo = mVoteData.get(mPosion);
                            int voteid = voteInfo.getVoteid();
                            int mode = voteInfo.getMode();
                            int votestate = voteInfo.getVotestate();
                            //需要记名 和 已经发起或结束的状态
                            if (mode == 1 && votestate != 0) {
                                try {
                                    /** ************ ******  203.查询指定投票的提交人  ****** ************ **/
                                    nativeUtil.queryOneVoteSubmitter(voteid);
                                } catch (InvalidProtocolBufferException e) {
                                    e.printStackTrace();
                                }
                            }
//                            TextView tv1 = view.findViewById(R.id.vote_option_1);
//                            TextView tv2 = view.findViewById(R.id.vote_option_2);
//                            TextView tv3 = view.findViewById(R.id.vote_option_3);
//                            TextView tv4 = view.findViewById(R.id.vote_option_4);
//                            TextView tv5 = view.findViewById(R.id.vote_option_5);
//                            tv1.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Log.e("MyLog", "VoteFragment.onClick:  点击了第一项 --->>> " + posion);
//                                    MyUtils.setAnimator(view);
//                                }
//                            });
//                            tv2.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Log.e("MyLog", "VoteFragment.onClick:  点击了第二项 --->>> " + posion);
//                                    MyUtils.setAnimator(view);
//                                }
//                            });
//                            tv3.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Log.e("MyLog", "VoteFragment.onClick:  点击了第三项 --->>> " + posion);
//                                    MyUtils.setAnimator(view);
//                                }
//                            });
//                            tv4.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Log.e("MyLog", "VoteFragment.onClick:  点击了第四项 --->>> " + posion);
//                                    MyUtils.setAnimator(view);
//                                }
//                            });
//                            tv5.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Log.e("MyLog", "VoteFragment.onClick:  点击了第五项 --->>> " + posion);
//                                    MyUtils.setAnimator(view);
//                                }
//                            });
                        }
                    });
                    break;
                case IDivMessage.QUERY_MEMBER_BYVOTE://203.查询指定投票的提交人
                    ArrayList queryVoteMember = msg.getData().getParcelableArrayList("queryVoteMember");
                    InterfaceMain2.pbui_Type_MeetVoteSignInDetailInfo o1 = (InterfaceMain2.pbui_Type_MeetVoteSignInDetailInfo) queryVoteMember.get(0);
                    List<InterfaceMain2.pbui_Item_MeetVoteSignInDetailInfo> itemList = o1.getItemList();
                    for (int i = 0; i < itemList.size(); i++) {
                        InterfaceMain2.pbui_Item_MeetVoteSignInDetailInfo pbui_item_meetVoteSignInDetailInfo = itemList.get(i);
                        int id = pbui_item_meetVoteSignInDetailInfo.getId();
                        int selcnt = pbui_item_meetVoteSignInDetailInfo.getSelcnt();
                        // TODO: 2018/2/8 selcnt   装换成二进制进行比较 哪个位数为1 则表示选择了哪个
                        String string = Integer.toBinaryString(selcnt);
                        int i1 = string.indexOf("1");
                        int length = string.length();
                        for (int j = 0; j < length; j++) {
                            if(i1 == j){
                                // TODO: 2018/2/8 计算出在哪个位置
                                int i2 = length - j;
                                // TODO: 2018/2/8 当前人员选择的是第 i2 项的选项值
                            }
                        }
                        Log.e("MyLog", "VoteFragment.handleMessage:  投票人员ID: --->>> " + id +
                                "  选择了哪一项：  " + selcnt + "  十进制：" + string + "   位置：  " + i1);
                    }
                    break;
            }
        }
    };
    private VoteAdapter mVoteAdapter;
    private boolean isCompere = false;
    private Button mVoteOver;
    private PopupWindow mChoosePop;

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
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.Vote_Change_Inform://投票变更通知
                Log.e("MyLog", "VoteFragment.getEventMessage:  投票变更通知 EventBus --->>> ");
                //200.查询投票
                nativeUtil.queryVote();
                break;
            case IDEventMessage.VoteMember_ChangeInform://投票提交人变更通知
                Log.e("MyLog", "VoteFragment.getEventMessage:  投票提交人变更通知 EventBus --->>> ");
                int voteid = mVoteData.get(mPosion).getVoteid();
                nativeUtil.queryOneVoteSubmitter(voteid);
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
        mVoteRl = (RecyclerView) inflate.findViewById(R.id.vote_rl);
        mVoteRl.setLayoutManager(new LinearLayoutManager(getContext()));

        mVoteQuery = (Button) inflate.findViewById(R.id.vote_query);
        mVoteExport = (Button) inflate.findViewById(R.id.vote_export);
        mVoteManagement = (Button) inflate.findViewById(R.id.vote_management);
        mVoteOpen = (Button) inflate.findViewById(R.id.vote_open);
        mVoteOver = (Button) inflate.findViewById(R.id.vote_over);
        mHostFunction = (RelativeLayout) inflate.findViewById(R.id.host_function);

        /** ************ ******  如果是主持人则展示主持人功能  ****** ************ **/
        String string1 = MeetingActivity.mAttendee.getText().toString();
        String string2 = MeetingActivity.mCompere.getText().toString();
        if (string1.equals(string2)) {
            isCompere = true;
            mHostFunction.setVisibility(View.VISIBLE);
        } else {
            isCompere = false;
            mHostFunction.setVisibility(View.GONE);
        }
        mVoteOpen.setOnClickListener(this);
        mVoteOver.setOnClickListener(this);
        mVoteManagement.setOnClickListener(this);
        mVoteQuery.setOnClickListener(this);
        mVoteExport.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        VoteInfo voteInfo = mVoteData.get(mPosion);
        int votestate = voteInfo.getVotestate();
        int mode = voteInfo.getMode();
        switch (v.getId()) {
            case R.id.vote_query:       //结果查询
                // 需要记名 和 已经发起或结束的状态
                if (mode == 1 && votestate != 0) {
                    showDetailVoteInfo();
                } else {
                    Toast.makeText(getContext(), "未发起和匿名的投票无法查询", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.vote_export:      //结果导出
                break;
            case R.id.vote_management:  //投票管理
                break;
            case R.id.vote_open:        //发起投票
                //判断是否是未发起状态
                if (votestate == 0) {
                    //未发起状态设置成进行状态
                    voteInfo.setVotestate(1);
                    mVoteAdapter.notifyDataSetChanged();
                    showChoose();
                } else if (votestate == 1) {
                    showChoose();
                }
                break;
            case R.id.vote_over:        //结束投票
                //判断是否是进行状态
                if (votestate == 1) {
                    //进行状态设置成结束状态
                    voteInfo.setVotestate(2);
                    mVoteAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void showChoose() {

        View popupView = getActivity().getLayoutInflater().inflate(R.layout.compere_vote_pop, null);
        /** ************ ******  1.判断有几个选项  ****** ************ **/
        /** ************ ******  2.判断可以选择几个  ****** ************ **/

        mChoosePop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置动画
        mChoosePop.setAnimationStyle(R.style.AnimHorizontal);
        mChoosePop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mChoosePop.setTouchable(true);
        mChoosePop.setOutsideTouchable(true);
        ChooseViewHolder holder = new ChooseViewHolder(popupView);
        ChooseHolderEvent(holder);
        mChoosePop.showAtLocation(getActivity().findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    private void ChooseHolderEvent(ChooseViewHolder holder) {

    }

    //展示详细的投票信息
    private void showDetailVoteInfo() {
        View popupView = getActivity().getLayoutInflater().inflate(R.layout.vote_info_pop, null);
        mVoteInfoPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置动画
        mVoteInfoPop.setAnimationStyle(R.style.AnimHorizontal);
        mVoteInfoPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mVoteInfoPop.setTouchable(true);
        mVoteInfoPop.setOutsideTouchable(true);
        ResultViewHolder holder = new ResultViewHolder(popupView);
        holder_Event(holder);
        mVoteInfoPop.showAtLocation(getActivity().findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    private void holder_Event(ResultViewHolder holder) {
        // 返回
        holder.back_vote_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVoteInfoPop.dismiss();
            }
        });
        // 导出
        holder.export_vote_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    public static class ResultViewHolder {
        public View rootView;
        public RecyclerView vote_pop_rl;
        public Button export_vote_pop;
        public Button back_vote_pop;

        public ResultViewHolder(View rootView) {
            this.rootView = rootView;
            this.vote_pop_rl = (RecyclerView) rootView.findViewById(R.id.vote_pop_rl);
            this.vote_pop_rl.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            // TODO: 2018/2/8 数据待传
            List<VoteBean> voteBeen = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                voteBeen.add(new VoteBean("name_" + i, "choose_" + i));
            }
            VoteInfoPopAdapter adapter = new VoteInfoPopAdapter(rootView.getContext(), voteBeen);
            this.vote_pop_rl.setAdapter(adapter);
            this.export_vote_pop = (Button) rootView.findViewById(R.id.export_vote_pop);
            this.back_vote_pop = (Button) rootView.findViewById(R.id.back_vote_pop);
        }

    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_VOTE:
                InterfaceMain2.pbui_Type_MeetVoteDetailInfo result1 = (InterfaceMain2.pbui_Type_MeetVoteDetailInfo) result;
                if (result1 != null) {
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
            case IDivMessage.RECEIVE_MEET_IMINFO:
                Log.e("MyLog", "VoteFragment.callListener:  收到会议消息 --->>> ");
                InterfaceMain2.pbui_Type_MeetIM receiveMsg = (InterfaceMain2.pbui_Type_MeetIM) result;
                if (receiveMsg != null) {
                    List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(receiveMsg);
                    if (mReceiveMsg == null) {
                        mReceiveMsg = new ArrayList<>();
                    }
                    mReceiveMsg.add(receiveMeetIMInfos.get(0));
                }
                break;

            case IDivMessage.QUERY_MEMBER_BYVOTE://203.查询指定投票的提交人
                InterfaceMain2.pbui_Type_MeetVoteSignInDetailInfo result2 = (InterfaceMain2.pbui_Type_MeetVoteSignInDetailInfo) result;
                if (result2 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result2);
                    bundle.putParcelableArrayList("queryVoteMember", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }

    public static class ChooseViewHolder {
        public View rootView;
        public RadioButton chooseA;
        public RadioButton chooseB;
        public RadioButton chooseC;
        public RadioButton chooseD;
        public RadioButton chooseE;
        public Button compereVotePopEnsure;
        public Button compereVotePopCancel;

        public ChooseViewHolder(View rootView) {
            this.rootView = rootView;
            this.chooseA = (RadioButton) rootView.findViewById(R.id.chooseA);
            this.chooseB = (RadioButton) rootView.findViewById(R.id.chooseB);
            this.chooseC = (RadioButton) rootView.findViewById(R.id.chooseC);
            this.chooseD = (RadioButton) rootView.findViewById(R.id.chooseD);
            this.chooseE = (RadioButton) rootView.findViewById(R.id.chooseE);
            this.compereVotePopEnsure = (Button) rootView.findViewById(R.id.compere_votePop_ensure);
            this.compereVotePopCancel = (Button) rootView.findViewById(R.id.compere_votePop_cancel);
        }

    }
}
