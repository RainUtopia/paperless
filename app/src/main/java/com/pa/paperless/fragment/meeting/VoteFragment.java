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
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.pa.paperless.R;
import com.pa.paperless.adapter.VoteAdapter;
import com.pa.paperless.adapter.VoteInfoPopAdapter;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.bean.SubmitVoteBean;
import com.pa.paperless.bean.VoteBean;
import com.pa.paperless.bean.VoteInfo;
import com.pa.paperless.bean.VoteOptionsInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventBadge;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.Export;
import com.pa.paperless.utils.MyUtils;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.getMeetName;
import static com.pa.paperless.activity.MeetingActivity.mBadge;
import static com.pa.paperless.activity.MeetingActivity.mReceiveMsg;

/**
 * Created by Administrator on 2017/10/31.
 * 投票查询
 */

public class VoteFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private NativeUtil nativeUtil;
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
                    InterfaceVote.pbui_Type_MeetVoteDetailInfo o = (InterfaceVote.pbui_Type_MeetVoteDetailInfo) queryVote.get(0);
                    //获取到所有的投票信息
                    mVoteData = Dispose.Vote(o);
                    mVoteAdapter = new VoteAdapter(getContext(), mVoteData);
                    mVoteRl.setAdapter(mVoteAdapter);
                    if (!isCompere) {
                        mVoteAdapter.setItemListener(new ItemClickListener() {
                            @Override
                            public void onItemClick(View view, int posion) {
                                mVoteAdapter.setCheckedId(posion);
                            }
                        });
                        break;
                    }
                    mVoteAdapter.setItemListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(View view, final int posion) {
                            mVoteAdapter.setCheckedId(posion);
                            mPosion = posion;
                            voteInfo = mVoteData.get(mPosion);
                            int voteid = voteInfo.getVoteid();
                            int type = mVoteData.get(mPosion).getType();
                            Log.e("MyLog", "VoteFragment.onItemClick 103行:  item点击时选中的投票ID --->>> " + voteid + "  当前的状态:" + type);
                            isClicked = true;
//                            int mode = mVoteData.get(mPosion).getMode();
//                            int votestate = mVoteData.get(mPosion).getVotestate();
                            //需要记名 和 已经发起或结束的状态
//                            if (mode == 1 && votestate != 0) {
//                                try {
//                                    /** ************ ******  203.查询指定投票的提交人  ****** ************ **/
//                                    nativeUtil.queryOneVoteSubmitter(voteid);
//                                } catch (InvalidProtocolBufferException e) {
//                                    e.printStackTrace();
//                                }
//                            }
                        }
                    });
                    break;
                case IDivMessage.QUERY_MEMBER_BYVOTE://203.查询指定投票的提交人
                    ArrayList queryVoteMember = msg.getData().getParcelableArrayList("queryVoteMember");
                    InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo o1 = (InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo) queryVoteMember.get(0);
                    List<InterfaceVote.pbui_Item_MeetVoteSignInDetailInfo> itemList = o1.getItemList();
                    int voteid = o1.getVoteid();
                    //获取当前选中投票的全部选项信息
                    List<VoteOptionsInfo> optionInfo = voteInfo.getOptionInfo();
                    List<VoteBean> voteBeen = new ArrayList<>();
                    for (int i = 0; i < itemList.size(); i++) {
                        InterfaceVote.pbui_Item_MeetVoteSignInDetailInfo pbui_item_meetVoteSignInDetailInfo = itemList.get(i);
                        String chooseText = "";
                        String name = "";
                        int id = pbui_item_meetVoteSignInDetailInfo.getId();
                        int selcnt = pbui_item_meetVoteSignInDetailInfo.getSelcnt();
                        //int变量的二进制表示的字符串
                        String string = MyUtils.get10To2(selcnt);
                        //查找字符串中为1的索引位置
                        int length = string.length();
                        for (int j = 0; j < length; j++) {
                            char c = string.charAt(j);
                            //将 char 装换成int型整数
                            int a = c - '0';
                            if (a == 1) {
                                selectedItem = length - j - 1;//索引从0开始
                                Log.e("MyLog", "VoteFragment.handleMessage:   --->>> 选中了第_ " + selectedItem + " _项");
                                for (int k = 0; k < memberInfos.size(); k++) {
                                    if (memberInfos.get(k).getPersonid() == id) {
                                        name = memberInfos.get(k).getName();
                                    }
                                }
                                for (int k = 0; k < optionInfo.size(); k++) {
                                    if (k == selectedItem) {
                                        VoteOptionsInfo voteOptionsInfo = optionInfo.get(k);
                                        chooseText += " " + voteOptionsInfo.getText();
                                    }
                                }
                            }
                        }
                        voteBeen.add(new VoteBean(name, chooseText));
                        showDetailVoteInfo(voteBeen);
                    }
                    break;
                case IDivMessage.QUERY_ATTENDEE://92.查询参会人员
                    ArrayList queryMember = msg.getData().getParcelableArrayList("queryMember");
                    InterfaceMember.pbui_Type_MemberDetailInfo o2 = (InterfaceMember.pbui_Type_MemberDetailInfo) queryMember.get(0);
                    memberInfos = Dispose.MemberInfo(o2);
                    break;
            }
        }
    };
    private VoteAdapter mVoteAdapter;
    public static boolean isCompere;
    private Button mVoteOver;
    private PopupWindow mChoosePop;
    private int selectedItem = 0;
    private boolean isClicked = false;
    private VoteInfo voteInfo;
    private List<MemberInfo> memberInfos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_vote, container, false);
        initController();
        initView(inflate);
        try {
            //200.查询投票
            nativeUtil.queryVote();
            nativeUtil.queryAttendPeople();
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
                Log.e("MyLog", "VoteFragment.getEventMessage 213行:  投票变更通知 EventBus --->>> ");
                InterfaceBase.pbui_MeetNotifyMsg object1 = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                int opermethod = object1.getOpermethod();
                int id1 = object1.getId();

                nativeUtil.queryVote();

                if (opermethod == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber()) {
                    //操作方法为 查询投票 时
                    Log.e("MyLog", "VoteFragment.getEventMessage:  投票变更通知 EventBus --->>> 200.查询投票");
                    nativeUtil.queryVote();
                }
                //判断是否是发起投票方法引起的回调
                if (opermethod == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START.getNumber()) {
                    Log.e("MyLog", "VoteFragment.getEventMessage 224行:  投票变更通知 EventBus   --->>> 193.发起投票  " + opermethod + "   投票ID：" + id1);
                    if (isClicked) {
                        //mVoteData.get(mPosion)
                        showChoose(voteInfo);
                    }
                }
                if (opermethod == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_STOP.getNumber()) {
                    //操作方法为 停止投票 时
                    int id = object1.getId();
                    Log.e("MyLog", "VoteFragment.getEventMessage 238行:  投票变更通知 EventBus --->>> 195.停止投票   " + id);
//                    /** ************ ******  195.停止投票  ****** ************ **/
//                    nativeUtil.stopVote(id);
                    voteInfo.setVotestate(2);
                    mVoteAdapter.notifyDataSetChanged();
                }
                break;
            case IDEventMessage.VoteMember_ChangeInform://203.投票提交人变更通知
                Log.e("MyLog", "VoteFragment.getEventMessage:  投票提交人变更通知 EventBus --->>> ");
                int voteid = mVoteData.get(mPosion).getVoteid();
                InterfaceBase.pbui_MeetNotifyMsg object2 = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                int id2 = object2.getId();
                int opermethod2 = object2.getOpermethod();
                /** ************ ******  203.查询指定投票的提交人  ****** ************ **/
                if (opermethod2 == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber()) {
                    Log.e("MyLog", "VoteFragment.getEventMessage 240行:  投票提交人变更通知 EventBus--->>> ");
                    nativeUtil.queryOneVoteSubmitter(voteid);
                }
                break;
            case IDEventMessage.newVote_launch_inform://188 有新的投票发起通知
                InterfaceBase.pbui_MeetNotifyMsg object = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                int opermethod1 = object.getOpermethod();
                int id = object.getId();
                //判断是否是发起投票方法引起的回调
                if (opermethod1 == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START.getNumber()) {
                    Log.e("MyLog", "VoteFragment.getEventMessage 251行:  发起投票 EventBus  方法 --->>> " + opermethod1 + "   投票ID：" + id);
                    if (isClicked) {
                        //mVoteData.get(mPosion)
                        showChoose(voteInfo);
                    }
                }
                break;
            case IDEventMessage.MEMBER_CHANGE_INFORM:
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

        if (isCompere) {
            mVoteManagement.setVisibility(View.VISIBLE);
        } else {
            mVoteManagement.setVisibility(View.GONE);
        }
//        String string1 = MeetingActivity.mAttendee.getText().toString();
//        String string2 = MeetingActivity.mCompere.getText().toString();
//        if (string1.equals(string2)) {
//            isCompere = true;
//            mVoteManagement.setVisibility(View.VISIBLE);
//        } else {
//            isCompere = false;
//            mVoteManagement.setVisibility(View.GONE);
//        }
        mVoteOpen.setVisibility(View.GONE);
        mVoteOver.setVisibility(View.GONE);
        mVoteQuery.setVisibility(View.GONE);

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
                    try {
                        /** ************ ******  203.查询指定投票的提交人  ****** ************ **/
                        nativeUtil.queryOneVoteSubmitter(voteInfo.getVoteid());
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "未发起和匿名的投票无法查询", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.vote_export:      //结果导出
                String[] titles = {"投票ID", "投票内容", "投票类型", "是否记名", "状态", "选项一", "投票数", "选项二", "投票数", "选项三", "投票数", "选项四", "投票数", "选项五", "投票数"};
                String meetName = getMeetName();
                Log.e("MyLog", "VoteFragment.onClick 306行:  meetName --->>> " + meetName);
                boolean b = Export.ToVoteExcel(meetName, "投票结果", "Sheet1", titles, mVoteData);
                if (b) {
                    Toast.makeText(getContext(), "导出成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.vote_management:  //投票管理
                if (isClicked) {
                    /** ************ ******  如果是主持人则展示主持人功能  ****** ************ **/
                    mVoteOpen.setVisibility(View.VISIBLE);
                    mVoteOver.setVisibility(View.VISIBLE);
                    mVoteQuery.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "先选择投票", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.vote_open:        //发起投票
                // 判断是否是未发起状态
                if (votestate == 0 && isClicked) {
                    /** ************ ******  193.发起投票  ****** ************ **/
                    int voteid = mVoteData.get(mPosion).getVoteid();
                    Log.e("MyLog", "VoteFragment.onClick 341行:  发起的投票ID --->>> " + voteid);
                    nativeUtil.initiateVote(voteid);
                } else {
                    Toast.makeText(getContext(), "请选中未发起的投票", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.vote_over:        //结束投票
                //判断是否是进行状态
                if (votestate == 1) {
                    /** ************ ******  195.停止投票  ****** ************ **/
                    nativeUtil.stopVote(mVoteData.get(mPosion).getVoteid());
                }
                break;
        }
    }

    /**
     * 弹出进行投票选择选项的弹出框
     *
     * @param voteInfo 主持人当前选中的投票信息
     */
    private void showChoose(VoteInfo voteInfo) {
        isClicked = false;
        View popupView = getActivity().getLayoutInflater().inflate(R.layout.compere_vote_pop, null);
        mChoosePop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置动画
        mChoosePop.setAnimationStyle(R.style.AnimHorizontal);
        mChoosePop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mChoosePop.setTouchable(true);
        mChoosePop.setOutsideTouchable(true);
        ChooseViewHolder holder = new ChooseViewHolder(popupView, voteInfo);
        ChooseHolderEvent(holder, voteInfo);
        mChoosePop.showAtLocation(getActivity().findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    private void ChooseHolderEvent(final ChooseViewHolder holder, final VoteInfo voteInfo) {
        //存放选择框
        final ArrayList<CheckBox> btns = new ArrayList<>();
        btns.add(holder.chooseA);
        btns.add(holder.chooseB);
        btns.add(holder.chooseC);
        btns.add(holder.chooseD);
        btns.add(holder.chooseE);

        int selectcount = voteInfo.getSelectcount();
        int type = voteInfo.getType();
        //根据 type 设置可选择的个数
        switch (type) {
            case 0://多选
                setMaxSelect(selectcount, btns, holder);
                break;
            case 1://单选
                setMaxSelect(1, btns, holder);
                break;
            case 2://5 4
                setMaxSelect(4, btns, holder);
                break;
            case 3://5 3
                setMaxSelect(3, btns, holder);
                break;
            case 4://5 2
                setMaxSelect(2, btns, holder);
                break;
            case 5://3 2
                setMaxSelect(2, btns, holder);
                break;
        }

        //取消按钮
        holder.compereVotePopCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChoosePop.dismiss();
            }
        });
        //确定按钮
        holder.compereVotePopEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Integer> choose = new ArrayList<Integer>();
                int selectcount1 = mVoteData.get(mPosion).getSelectcount();
                for (int i = 0; i < selectcount1; i++) {
                    CheckBox checkBox = btns.get(i);
                    if (checkBox.isChecked()) {
                        switch (i) {
                            case 0://第0项被选中
                                choose.add(1);
                                break;
                            case 1://第1项被选中
                                choose.add(2);
                                break;
                            case 2://第2项被选中
                                choose.add(4);
                                break;
                            case 3://第3项被选中
                                choose.add(8);
                                break;
                            case 4://第4项被选中
                                // TODO: 2018/2/27   10000 从右往左第五个  10000的十进制为 16
                                choose.add(16);
                                break;
                        }
                    }
                }
                int bb = 0;
                for (int j = 0; j < choose.size(); j++) {
                    bb = bb + choose.get(j);
                }
                mChoosePop.dismiss();
                SubmitVoteBean submitVoteBean = new SubmitVoteBean(voteInfo.getVoteid(), selectcount1, bb);
                /** ************ ******  196.提交投票结果  ****** ************ **/
                nativeUtil.submitVoteResult(submitVoteBean);
            }
        });
    }

    /**
     * CheckBox 事件监听
     *
     * @param maxSelect 最多可以选中的个数
     * @param btns      存放选择框的集合
     */
    private void setMaxSelect(final int maxSelect, final ArrayList<CheckBox> btns, final ChooseViewHolder holder) {
        holder.chooseA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBoxEvent(btns, maxSelect);
            }
        });
        holder.chooseB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBoxEvent(btns, maxSelect);
            }
        });
        holder.chooseC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBoxEvent(btns, maxSelect);
            }
        });
        holder.chooseD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBoxEvent(btns, maxSelect);
            }
        });
        holder.chooseE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBoxEvent(btns, maxSelect);
            }
        });
    }

    /**
     * @param btns      CheckBox ArrayList集合
     * @param maxSelect 当前最大选中数
     */
    private void CheckBoxEvent(final ArrayList<CheckBox> btns, final int maxSelect) {
        final int nowSelect = getNowSelect(btns);
        if (maxSelect > nowSelect) {
            //如果当前选中的个数 小于 可选的个数
            for (int i = 0; i < btns.size(); i++) {
                if (!(btns.get(i).isChecked())) {
                    //就将未选中的选项设置成 可点击状态
                    btns.get(i).setClickable(true);
                }
            }
        } else if (maxSelect == nowSelect) {
            //如果当前选中的个数 等于 可选的个数
            Toast.makeText(getContext(), "最多可选择  " + maxSelect + "  个选项", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < btns.size(); i++) {
                if (!(btns.get(i).isChecked())) {
                    //就将未选中的选项设置成 不可点击状态
                    btns.get(i).setClickable(false);
                }
            }
        }
    }

    /**
     * @param btns CheckBox ArrayList集合
     * @return 返回当前选中的个数
     */
    private int getNowSelect(ArrayList<CheckBox> btns) {
        int nowSelect = 0;
        for (int i = 0; i < btns.size(); i++) {
            if (btns.get(i).isChecked()) {
                //如果未true 选中的话就将 nowSelect加1
                nowSelect++;
            }
        }
        return nowSelect;
    }


    //展示详细的投票信息
    private void showDetailVoteInfo(List<VoteBean> voteBeen) {
        View popupView = getActivity().getLayoutInflater().inflate(R.layout.vote_info_pop, null);
        mVoteInfoPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置动画
        mVoteInfoPop.setAnimationStyle(R.style.AnimHorizontal);
        mVoteInfoPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mVoteInfoPop.setTouchable(true);
        mVoteInfoPop.setOutsideTouchable(true);
        ResultViewHolder holder = new ResultViewHolder(popupView, voteBeen);
        holder_Event(holder, voteBeen);
        mVoteInfoPop.showAtLocation(getActivity().findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    private void holder_Event(ResultViewHolder holder, final List<VoteBean> voteBeen) {
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
                String[] titles = {"序号", "姓名", "选项"};
                boolean b = Export.ToVoteResultExcel(voteInfo.getContent(), "投票详情", "Sheet1", titles, voteBeen);
                if (b) {
                    Toast.makeText(getContext(), "导出成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public static class ResultViewHolder {
        public View rootView;
        public RecyclerView vote_pop_rl;
        public Button export_vote_pop;
        public Button back_vote_pop;

        public ResultViewHolder(View rootView, List<VoteBean> voteBeen) {
            this.rootView = rootView;
            this.vote_pop_rl = (RecyclerView) rootView.findViewById(R.id.vote_pop_rl);
            this.vote_pop_rl.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
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
                InterfaceVote.pbui_Type_MeetVoteDetailInfo result1 = (InterfaceVote.pbui_Type_MeetVoteDetailInfo) result;
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
            case IDivMessage.RECEIVE_MEET_IMINFO: //收到会议消息
                Log.e("MyLog", "SigninFragment.callListener 296行:  收到会议消息 --->>> ");
                InterfaceIM.pbui_Type_MeetIM receiveMsg = (InterfaceIM.pbui_Type_MeetIM) result;
                //获取之前的未读消息个数
                int badgeNumber1 = mBadge.getBadgeNumber();
                Log.e("MyLog", "SigninFragment.callListener 307行:  原来的个数 --->>> " + badgeNumber1);
                int all = badgeNumber1 + 1;
                if (receiveMsg != null) {
                    List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(receiveMsg);
                    if (mReceiveMsg == null) {
                        mReceiveMsg = new ArrayList<>();
                    }
                    receiveMeetIMInfos.get(0).setType(true);
                    mReceiveMsg.add(receiveMeetIMInfos.get(0));
                    Log.e("MyLog", "SigninFragment.callListener: 收到的信息个数：  --->>> " + mReceiveMsg.size());
                }
                List<EventBadge> num = new ArrayList<>();
                num.add(new EventBadge(all));
                // TODO: 2018/3/7 通知界面更新
                Log.e("MyLog", "SigninFragment.callListener 319行:  传递过去的个数 --->>> " + all);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.UpDate_BadgeNumber, num));
                break;

            case IDivMessage.QUERY_MEMBER_BYVOTE://203.查询指定投票的提交人
                InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo result2 = (InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo) result;
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
            case IDivMessage.QUERY_ATTENDEE:
                InterfaceMember.pbui_Type_MemberDetailInfo result3 = (InterfaceMember.pbui_Type_MemberDetailInfo) result;
                if (result3 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result3);
                    bundle.putParcelableArrayList("queryMember", arrayList);
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
        public TextView tvTitle;
        public CheckBox chooseA;
        public CheckBox chooseB;
        public CheckBox chooseC;
        public CheckBox chooseD;
        public CheckBox chooseE;
        public Button compereVotePopEnsure;
        public Button compereVotePopCancel;

        public ChooseViewHolder(View rootView, VoteInfo voteInfo) {
            this.rootView = rootView;
            this.tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
            this.chooseA = (CheckBox) rootView.findViewById(R.id.chooseA);
            this.chooseB = (CheckBox) rootView.findViewById(R.id.chooseB);
            this.chooseC = (CheckBox) rootView.findViewById(R.id.chooseC);
            this.chooseD = (CheckBox) rootView.findViewById(R.id.chooseD);
            this.chooseE = (CheckBox) rootView.findViewById(R.id.chooseE);
            this.compereVotePopEnsure = (Button) rootView.findViewById(R.id.compere_votePop_ensure);
            this.compereVotePopCancel = (Button) rootView.findViewById(R.id.compere_votePop_cancel);

            // 给选项投票弹出框设置文本值
            //获取投票内容
            tvTitle.setText(voteInfo.getContent());
            //获取选项信息
            List<VoteOptionsInfo> optionInfo = voteInfo.getOptionInfo();
            for (int i = 0; i < optionInfo.size(); i++) {
                String text = optionInfo.get(i).getText();
                if (i == 0) {
                    this.chooseA.setText(text);
                } else if (i == 1) {
                    this.chooseB.setText(text);
                } else if (i == 2) {
                    this.chooseC.setText(text);
                } else if (i == 3) {
                    this.chooseD.setText(text);
                } else if (i == 4) {
                    this.chooseE.setText(text);
                }
            }
            // 有多少个选项就展示多少个
            int selectcount = voteInfo.getSelectcount();
            switch (selectcount) {
                case 5:
                    this.chooseE.setVisibility(View.VISIBLE);
                    this.chooseD.setVisibility(View.VISIBLE);
                    this.chooseC.setVisibility(View.VISIBLE);
                    this.chooseB.setVisibility(View.VISIBLE);
                    this.chooseA.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    this.chooseD.setVisibility(View.VISIBLE);
                    this.chooseC.setVisibility(View.VISIBLE);
                    this.chooseB.setVisibility(View.VISIBLE);
                    this.chooseA.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    this.chooseC.setVisibility(View.VISIBLE);
                    this.chooseB.setVisibility(View.VISIBLE);
                    this.chooseA.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    this.chooseB.setVisibility(View.VISIBLE);
                    this.chooseA.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    this.chooseA.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            nativeUtil = NativeUtil.getInstance();
            nativeUtil.setCallListener(this);
        }
    }
}
