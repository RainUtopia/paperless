package com.pa.paperless.fragment.meeting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.pa.paperless.R;
import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.adapter.VoteAdapter;
import com.pa.paperless.adapter.VoteInfoPopAdapter;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.bean.SubmitVoteBean;
import com.pa.paperless.bean.VoteBean;
import com.pa.paperless.bean.VoteInfo;
import com.pa.paperless.bean.VoteOptionsInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.Export;
import com.pa.paperless.utils.MyUtils;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.getMeetName;
import static com.pa.paperless.service.NativeService.nativeUtil;

/**
 * Created by Administrator on 2017/10/31.
 * 投票查询
 */

public class VoteFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "VoteFragment-->>";
    private RecyclerView mVoteRl;
    private Button mVoteQuery;
    private Button mVoteExport;
    private Button mVoteManagement;
    private Button mVoteOpen;
    private PopupWindow mVoteInfoPop;
    private RelativeLayout mHostFunction;
    private List<VoteInfo> mVoteData;
    private int mPosion;
    private VoteAdapter mVoteAdapter;
    public static boolean isCompere;
    private Button mVoteOver;
    private PopupWindow mChoosePop;
    private int selectedItem = 0;
    private VoteInfo voteInfo;
    private List<MemberInfo> memberInfos;
    private List<VoteBean> someOneVoteInfo;//当前选中的投票的投票结果
    private int SelectedVoteid;//当前选中的投票ID
    private boolean isFind = true;//是否查找到指定的投票提交人
    private boolean manageBtnisOpen = false;
    private boolean isHave;//是否有人投票过
    private boolean resultquery;//点击了结果查询
    private boolean isClickedItem; //点击了item
    private boolean localClick;//本人发起投票则为true

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_vote, container, false);
        initView(inflate);
        try {
            //200.查询投票
            nativeUtil.queryVote();
            nativeUtil.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO.getNumber());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.vote://收到投票信息
                receiveVoteInfo(message);
                break;
            case IDEventF.member_info://收到参会人数据
                InterfaceMember.pbui_Type_MemberDetailInfo o = (InterfaceMember.pbui_Type_MemberDetailInfo) message.getObject();
                memberInfos = Dispose.MemberInfo(o);
                if (memberInfos != null && mVoteAdapter != null) {//有可能还未查询投票就收到了参会人数据
                    VoteItem();
                }
                break;
            case IDEventF.vote_member_byId://收到指定投票的提交人数据
                receiveMemberByVote(message);
                break;
            case IDEventF.you_are_compere:// 变成主持人了
                mVoteManagement.setVisibility(View.VISIBLE);
                manageBtnisOpen = true;
                break;
            case IDEventF.you_not_compere:// 被取消主持人资格了
                mVoteManagement.setVisibility(View.GONE);
                mVoteOpen.setVisibility(View.GONE);
                mVoteOver.setVisibility(View.GONE);
                mVoteQuery.setVisibility(View.GONE);
                break;
            case IDEventMessage.Vote_Change_Inform://投票变更通知
                nativeUtil.queryVote();
                break;
            case IDEventMessage.VoteMember_ChangeInform://203.投票提交人变更通知
                Log.e(TAG, "VoteFragment.getEventMessage 164行:   --->>> 投票提交人变更通知");
                InterfaceBase.pbui_MeetNotifyMsg object2 = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                int opermethod2 = object2.getOpermethod();
                /** ************ ******  203.查询指定投票的提交人  ****** ************ **/
                if (opermethod2 == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber()) {
                    /** **** **  查询指定投票的提交人  ** **** **/
                    Log.e(TAG, "VoteFragment.getEventMessage :  查询指定投票的提交人 --->>> ");
                    isFind = nativeUtil.queryOneVoteSubmitter(SelectedVoteid);
                }
            case IDEventMessage.newVote_launch_inform://188 有新的投票发起通知
                Log.e(TAG, "VoteFragment.getEventMessage :  有新的投票发起通知 --> ");
                if (isClickedItem) {//点击了item
                    if (localClick) {//判断是本人发起的投票操作
                        Log.e(TAG, "VoteFragment.getEventMessage :  打开选项148 --> ");
                        showChoose(mVoteData.get(mPosion), true);
                    }
                } else {//别人发起
                    InterfaceBase.pbui_MeetNotifyMsg newVote = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                    int id = newVote.getId();
//                    /** **** **  查询指定ID的投票  ** **** **/
//                    nativeUtil.queryVoteById(id);
                    for (int i = 0; i < mVoteData.size(); i++) {
                        if (mVoteData.get(i).getVoteid() == id) {
                            Log.e(TAG, "VoteFragment.getEventMessage :  打开选项158 --> ");
                            showChoose(mVoteData.get(i), false);
                            break;
                        }
                    }
                }
                break;
        }
    }

    private void receiveVoteInfo(EventMessage message) {
        InterfaceVote.pbui_Type_MeetVoteDetailInfo object = (InterfaceVote.pbui_Type_MeetVoteDetailInfo) message.getObject();
        mVoteData = Dispose.Vote(object);
        if (mVoteAdapter == null) {
            mVoteAdapter = new VoteAdapter(getContext(), mVoteData);
            mVoteAdapter.setHasStableIds(true);
            mVoteRl.setAdapter(mVoteAdapter);
        } else {
            mVoteAdapter.notifyDataSetChanged();
        }
        try {
            nativeUtil.queryAttendPeople();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void receiveMemberByVote(EventMessage message) {
        Log.e(TAG, "VoteFragment.receiveMemberByVote :  收到指定投票的提交人信息 --> ");
        InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo object = (InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo) message.getObject();
        List<InterfaceVote.pbui_Item_MeetVoteSignInDetailInfo> itemList = object.getItemList();
        //获取当前选中投票的全部选项信息
        List<VoteOptionsInfo> optionInfo = voteInfo.getOptionInfo();
        List<Integer> ids = new ArrayList<>();//存放选过的人员ID
        if (someOneVoteInfo == null) {
            someOneVoteInfo = new ArrayList<>();
        } else {
            someOneVoteInfo.clear();
        }
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceVote.pbui_Item_MeetVoteSignInDetailInfo pbui_item_meetVoteSignInDetailInfo = itemList.get(i);
            String chooseText = "";//存放选中选项的文本内容
            String name = "";//当前选择的人员名称
            int id = pbui_item_meetVoteSignInDetailInfo.getId();//查询到的投票人ID
            Log.e(TAG, "VoteFragment.receiveMemberByVote :  选中的人员ID --> " + id + "  本机人员ID：" + MeetingActivity.getMemberId());
            ids.add(id);
            int selcnt = pbui_item_meetVoteSignInDetailInfo.getSelcnt();
            //int变量的二进制表示的字符串
            String string = MyUtils.get10To2(selcnt);
            //查找字符串中为1的索引位置
            int length = string.length();
            for (int j = 0; j < length; j++) {
                char c = string.charAt(j);
                //将 char 转换成int型整数
                int a = c - '0';
                if (a == 1) {
                    selectedItem = length - j - 1;//索引从0开始
                    Log.e(TAG, "VoteFragment.receiveMemberByVote :   --> 选中了索引为_ " + selectedItem + " _项");
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
            someOneVoteInfo.add(new VoteBean(name, chooseText));
        }
        //如果当前的投票人ID不是自己（自己没有投过）
        if (!ids.contains(MeetingActivity.getMemberId())) {
            /** **** **  2.打开投票窗口  ** **** **/
            Log.e(TAG, "VoteFragment.queryVoterById 274行:   --->>> 打开选项233");
            showChoose(mVoteData.get(mPosion), false);
            ids.clear();
        } else {
            showTip("你已经投过了");
        }
        if (resultquery) {
            showDetailVoteInfo(someOneVoteInfo);
        }
    }


    private void VoteItem() {
        mVoteAdapter.setItemListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, final int posion) {
                mVoteAdapter.setCheckedId(posion);
                mPosion = posion;
                voteInfo = mVoteData.get(posion);
                int votestate = voteInfo.getVotestate();
                SelectedVoteid = voteInfo.getVoteid();
                Log.e(TAG, "VoteFragment.onItemClick :  item点击时选中的投票ID --->>> " + SelectedVoteid + "  当前的状态:" + votestate);
                isClickedItem = true;//设置点击了item
                isHave = false;//设置默认当前投票没有人选过
                /** **** **  获取当前投票的选项数据  ** **** **/
                List<VoteOptionsInfo> optionInfo = voteInfo.getOptionInfo();
                //遍历查看，每一个选项已经投票的个数
                for (int i = 0; i < optionInfo.size(); i++) {
                    if (optionInfo.get(i).getSelcnt() > 0) {//只要有一个已投票的个数大于0
                        isHave = true;//设置有投票数据
                        break;//如果查找到数据就终止for循环
                    }
                }
                /** **** **  用户点击时，如果没选择过就展示出选项框  ** **** **/
                if (votestate == 1) {//确保该投票是发起状态
                    if (isHave) {//有人投过
                        try {
                            //查询指定投票的提交人,进一步确定本机之前是不是投过了，如果投过不需要再投
                            nativeUtil.queryOneVoteSubmitter(SelectedVoteid);
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    } else {//没有人投过
                        Log.e(TAG, "VoteFragment.onItemClick :  打开选项277 --> ");
                        showChoose(mVoteData.get(posion), false);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.i("F_life", "VoteFragment.onDestroy :   --->>> ");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View inflate) {
        mVoteRl = (RecyclerView) inflate.findViewById(R.id.vote_rl);
        /** **** **  禁用item变化时的动画效果,闪烁问题  ** **** **/
        mVoteRl.setItemAnimator(null);
        mVoteRl.setLayoutManager(new LinearLayoutManager(getContext()));

        mVoteQuery = (Button) inflate.findViewById(R.id.vote_query);
        mVoteExport = (Button) inflate.findViewById(R.id.vote_export);
        mVoteManagement = (Button) inflate.findViewById(R.id.vote_management);
        mVoteOpen = (Button) inflate.findViewById(R.id.vote_open);
        mVoteOver = (Button) inflate.findViewById(R.id.vote_over);
        mHostFunction = (RelativeLayout) inflate.findViewById(R.id.host_function);
        if (isCompere) {
            mVoteManagement.setVisibility(View.VISIBLE);
            manageBtnisOpen = true;
        } else {
            mVoteManagement.setVisibility(View.GONE);
        }
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
        switch (v.getId()) {
            case R.id.vote_query:       //结果查询
                if (voteInfo != null) {
                    int votestate = voteInfo.getVotestate();
                    int mode = voteInfo.getMode();
                    // 需要记名 和 已经发起或结束的状态
                    if (mode == 1 && votestate != 0 && isHave) {
                        try {
                            resultquery = true;
                            nativeUtil.queryOneVoteSubmitter(SelectedVoteid);
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    } else if (!isClickedItem) {
                        showTip("需要先选中投票");
                    } else if (!isHave) {
                        showTip("该项没有数据可查询");
                    } else {
                        showTip("未发起和匿名的投票无法查询");
                    }
                }
                break;
            case R.id.vote_export://全部结果导出
                String[] titles = {"投票ID", "投票内容", "投票类型", "是否记名", "状态", "选项一", "投票数", "选项二", "投票数", "选项三", "投票数", "选项四", "投票数", "选项五", "投票数"};
                String meetName = getMeetName();
                Log.e(TAG, "VoteFragment.onClick 405行:   --->>> meetName --->>> " + meetName);
                boolean b = Export.ToVoteExcel(meetName, "投票结果", "Sheet1", titles, mVoteData);
                if (b) {
                    Toast.makeText(getContext(), "导出成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.vote_management://投票管理
                if (isClickedItem) {
                    /** ************ ******  如果是主持人则展示主持人功能  ****** ************ **/
                    mVoteOpen.setVisibility(View.VISIBLE);
                    mVoteOver.setVisibility(View.VISIBLE);
                    mVoteQuery.setVisibility(View.VISIBLE);
                } else {
                    showTip("请先选择投票");
                }
                break;
            case R.id.vote_open://发起投票
                if (voteInfo != null) {
                    int votestate = voteInfo.getVotestate();
                    int mode = voteInfo.getMode();
                    // 判断是否是未发起状态
                    if (votestate == 0 && isClickedItem) {
                        /** ************ ******  193.发起投票  ****** ************ **/
//                    int voteid = mVoteData.get(mPosion).getVoteid();
                        Log.e(TAG, "VoteFragment.onClick 429行:   --->>> 发起的投票ID --->>> " + SelectedVoteid);
                        localClick = nativeUtil.initiateVote(SelectedVoteid);
                    } else {
                        showTip("请先选择未发起的投票");
                    }
                }
                break;
            case R.id.vote_over://结束投票
                if (voteInfo != null) {
                    int votestate = voteInfo.getVotestate();
                    //判断是否是进行状态
                    if (votestate == 1 && isClickedItem) {
                        /** ************ ******  195.停止投票  ****** ************ **/
                        nativeUtil.stopVote(SelectedVoteid);
                    } else {
                        showTip("请先选择正在进行的投票！");
                    }
                }
                break;
        }
    }

    /**
     * 弹出进行投票选择选项的弹出框
     *
     * @param voteInfo 主持人当前选中的投票信息
     */
    private void showChoose(VoteInfo voteInfo, boolean isStart) {
        if (isStart) {//主持人点击的
            isClickedItem = false;
            localClick = false;
        }
        View popupView = getActivity().getLayoutInflater().inflate(R.layout.compere_vote_pop, null);
        mChoosePop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置动画
        mChoosePop.setAnimationStyle(R.style.AnimHorizontal);
        mChoosePop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mChoosePop.setTouchable(true);
        mChoosePop.setOutsideTouchable(true);
        ChooseViewHolder holder = new ChooseViewHolder(popupView, voteInfo);
        ChooseHolderEvent(holder, voteInfo);
        mChoosePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mVoteAdapter.notifyDataSetChanged();
            }
        });
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
                int selectcount1 = voteInfo.getSelectcount();
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
                if (choose.size() > 0) {//有选中项才执行
                    int bb = 0;
                    for (int j = 0; j < choose.size(); j++) {
                        bb = bb + choose.get(j);
                    }
                    mChoosePop.dismiss();
                    SubmitVoteBean submitVoteBean = new SubmitVoteBean(voteInfo.getVoteid(), selectcount1, bb);
                    /** ************ ******  196.提交投票结果  ****** ************ **/
                    nativeUtil.submitVoteResult(submitVoteBean);
                    mVoteAdapter.notifyDataSetChanged();
                }
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

    //展示详细的投票信息 （导出投票结果）
    private void showDetailVoteInfo(List<VoteBean> voteBeen) {
        resultquery = false;
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
        Log.i("F_life", "VoteFragment.onHiddenChanged :   --->>> " + hidden);
        if (!hidden) {
            try {
                nativeUtil.queryVote();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.i("F_life", "VoteFragment.onAttach :   --> ");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "VoteFragment.onCreate :   --> ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "VoteFragment.onActivityCreated :   --> ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i("F_life", "VoteFragment.onStart :   --> ");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i("F_life", "VoteFragment.onResume :   --> ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("F_life", "VoteFragment.onPause :   --> ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("F_life", "VoteFragment.onStop :   --> ");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("F_life", "VoteFragment.onDestroyView :   --> ");
        super.onDestroyView();
    }


    @Override
    public void onDetach() {
        Log.i("F_life", "VoteFragment.onDetach :   --> ");
        super.onDetach();
    }
}
