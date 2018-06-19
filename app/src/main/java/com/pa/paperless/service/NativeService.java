package com.pa.paperless.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceContext;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMeetfunction;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.mogujie.tt.protobuf.InterfaceStream;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.pa.paperless.activity.NoteActivity;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.MyUtils;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.pa.paperless.utils.MyUtils.handTo;

/**
 * Created by Administrator on 2018/6/12.
 */

public class NativeService extends Service implements CallListener {

    private final String TAG = "NativeService-->";
    public static NativeUtil nativeUtil;
    public static int localDevId;//本机设备ID
    public static int localMemberId;//本机参会人ID

    @Override
    public void onDestroy() {
        Log.i("S_life", "NativeService.onDestroy ");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        Log.i("S_life", "NativeService.onCreate ");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("S_life", "NativeService.onStartCommand ");
        nativeUtil = NativeUtil.getInstance();
        nativeUtil.setCallListener(this);
        //发送消息到主页初始化完毕
        EventBus.getDefault().post(new EventMessage(IDEventF.init_native));
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("S_life", "NativeService.onBind ");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("S_life", "NativeService.onUnbind ");
        return super.onUnbind(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) {
        switch (message.getAction()) {
            case IDEventF.export_finish://文件导出成功
                String fileName = (String) message.getObject();
                Toast.makeText(this, fileName + "导出成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IDivMessage.QUERY_CONTEXT://按属性ID查询指定上下文属性
                    ArrayList queryContext = msg.getData().getParcelableArrayList("queryContext");
                    InterfaceContext.pbui_MeetContextInfo o = (InterfaceContext.pbui_MeetContextInfo) queryContext.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.context_proper, o));
                    break;
                case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                    ArrayList devInfos = msg.getData().getParcelableArrayList("devInfos");
                    InterfaceDevice.pbui_Type_DeviceDetailInfo devInfo = (InterfaceDevice.pbui_Type_DeviceDetailInfo) devInfos.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.dev_info, devInfo));
                    break;
                case IDivMessage.QUERY_DEVMEET_INFO://110.查询设备会议信息
                    ArrayList devMeetInfos = msg.getData().getParcelableArrayList("devMeetInfos");
                    InterfaceDevice.pbui_Type_DeviceFaceShowDetail devMeetInfo = (InterfaceDevice.pbui_Type_DeviceFaceShowDetail) devMeetInfos.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.dev_meet_info, devMeetInfo));
                    break;
                case IDivMessage.QUERY_MEET_FUNCTION://查询会议功能
                    ArrayList meetFun = msg.getData().getParcelableArrayList("queryMeetFunction");
                    InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo meetFuns = (InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo) meetFun.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.meet_function, meetFuns));
                    break;
                case IDivMessage.QUERY_ATTENDEE://92.查询参会人员
                    ArrayList members = msg.getData().getParcelableArrayList("queryMember");
                    InterfaceMember.pbui_Type_MemberDetailInfo member = (InterfaceMember.pbui_Type_MemberDetailInfo) members.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.member_info, member));
                    break;
                case IDivMessage.Query_MeetSeat_Inform://181.查询会议排位
                    ArrayList queryMeetRanking = msg.getData().getParcelableArrayList("queryMeetRanking");
                    InterfaceRoom.pbui_Type_MeetSeatDetailInfo meetSeat = (InterfaceRoom.pbui_Type_MeetSeatDetailInfo) queryMeetRanking.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.meet_seat, meetSeat));
                    break;
                case IDivMessage.member_mission://查询参会人权限
                    ArrayList member_mission = msg.getData().getParcelableArrayList("member_mission");
                    InterfaceMember.pbui_Type_MemberPermission memberMission = (InterfaceMember.pbui_Type_MemberPermission) member_mission.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.member_mission, memberMission));
                    break;
                case IDivMessage.QUERY_ATTEND_BYID://查询指定ID的参会人(发送消息的人员)
                    ArrayList assignMembers = msg.getData().getParcelableArrayList("assignMember");
                    InterfaceMember.pbui_Type_MemberDetailInfo assignMember = (InterfaceMember.pbui_Type_MemberDetailInfo) assignMembers.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.member_byID, assignMember));
                    break;
                case IDivMessage.QUERY_AGENDA://收到议程的数据
                    ArrayList agenda_meet = msg.getData().getParcelableArrayList("agenda_meet");
                    InterfaceAgenda.pbui_meetAgenda agendaInfo = (InterfaceAgenda.pbui_meetAgenda) agenda_meet.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.agenda_info, agendaInfo));
                    break;
                case IDivMessage.QUERY_MEET_DIR://136.查询会议目录
                    /**
                     * 共享文件 1
                     * 批注文件 2
                     * 其它 3 4...
                     */
                    ArrayList queryMeetDir = msg.getData().getParcelableArrayList("queryMeetDir");
                    InterfaceFile.pbui_Type_MeetDirDetailInfo queryMeetDirInfo = (InterfaceFile.pbui_Type_MeetDirDetailInfo) queryMeetDir.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.meet_dir, queryMeetDirInfo));
                    break;
                case IDivMessage.QUERY_MEET_DIR_FILE://查询会议目录文件
                    ArrayList queryMeetDirFile = msg.getData().getParcelableArrayList("queryMeetDirFile");
                    InterfaceFile.pbui_Type_MeetDirFileDetailInfo MeetDirFileInfo = (InterfaceFile.pbui_Type_MeetDirFileDetailInfo) queryMeetDirFile.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.meet_dir_file, MeetDirFileInfo));
                    break;
                case IDivMessage.QUERY_LONG_NOTICE://查询长公告
                    ArrayList notice = msg.getData().getParcelableArrayList("notice");
                    InterfaceBullet.pbui_BigBulletDetailInfo noticeInfo = (InterfaceBullet.pbui_BigBulletDetailInfo) notice.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.notice, noticeInfo));
                    break;
                case IDivMessage.QUERY_SIGN://查询签到
                    ArrayList signInfo = msg.getData().getParcelableArrayList("signInfo");
                    InterfaceSignin.pbui_Type_MeetSignInDetailInfo signInfos = (InterfaceSignin.pbui_Type_MeetSignInDetailInfo) signInfo.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.signin_info, signInfos));
                    break;
                case IDivMessage.QUERY_MEET_VEDIO://查询会议视频
                    ArrayList queryMeetVideo = msg.getData().getParcelableArrayList("queryMeetVideo");
                    InterfaceVideo.pbui_Type_MeetVideoDetailInfo MeetVideoInfo = (InterfaceVideo.pbui_Type_MeetVideoDetailInfo) queryMeetVideo.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.meet_video, MeetVideoInfo));
                    break;
                case IDivMessage.QUERY_VOTE://投票查询
                    ArrayList queryVote = msg.getData().getParcelableArrayList("queryVote");
                    InterfaceVote.pbui_Type_MeetVoteDetailInfo VoteInfo = (InterfaceVote.pbui_Type_MeetVoteDetailInfo) queryVote.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.vote, VoteInfo));
                    break;
                case IDivMessage.QUERY_MEMBER_BYVOTE://203.查询指定投票的提交人
                    ArrayList queryVoteMember = msg.getData().getParcelableArrayList("queryVoteMember");
                    InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo VoteMemberById = (InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo) queryVoteMember.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.vote_member_byId, VoteMemberById));
                    break;
                case IDivMessage.QUERY_NET://查询网页
                    ArrayList queryNet = msg.getData().getParcelableArrayList("queryNet");
                    InterfaceBase.pbui_meetUrl NetUrlInfo = (InterfaceBase.pbui_meetUrl) queryNet.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.net_url, NetUrlInfo));
                    break;
                case IDivMessage.QUERY_CAN_JOIN://查询可加入同屏
                    ArrayList queryCanJoin = msg.getData().getParcelableArrayList("queryCanJoin");
                    InterfaceDevice.pbui_Type_DeviceResPlay CanJoin = (InterfaceDevice.pbui_Type_DeviceResPlay) queryCanJoin.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.can_join, CanJoin));
                    break;
                case IDivMessage.QUERY_STREAM_PLAY://查询流播放
                    ArrayList queryStreamPlay = msg.getData().getParcelableArrayList("queryStreamPlay");
                    InterfaceStream.pbui_Type_MeetStreamPlayDetailInfo StreamPlayInfo = (InterfaceStream.pbui_Type_MeetStreamPlayDetailInfo) queryStreamPlay.get(0);
                    EventBus.getDefault().post(new EventMessage(IDEventF.all_stream_play, StreamPlayInfo));
                    break;
            }
        }
    };

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_CONTEXT://按属性ID查询指定上下文属性
                handTo(IDivMessage.QUERY_CONTEXT, (InterfaceContext.pbui_MeetContextInfo) result, "queryContext", handler);
                break;
            case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                handTo(IDivMessage.QUERY_DEVICE_INFO, (InterfaceDevice.pbui_Type_DeviceDetailInfo) result, "devInfos", handler);
                break;
            case IDivMessage.QUERY_DEVMEET_INFO://110.查询设备会议信息
                handTo(IDivMessage.QUERY_DEVMEET_INFO, (InterfaceDevice.pbui_Type_DeviceFaceShowDetail) result, "devMeetInfos", handler);
                break;
            case IDivMessage.QUERY_MEET_FUNCTION://查询会议功能
                MyUtils.handTo(IDivMessage.QUERY_MEET_FUNCTION, (InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo) result, "queryMeetFunction", handler);
                break;
            case IDivMessage.QUERY_ATTENDEE://92.查询参会人员
                MyUtils.handTo(IDivMessage.QUERY_ATTENDEE, (InterfaceMember.pbui_Type_MemberDetailInfo) result, "queryMember", handler);
                break;
            case IDivMessage.Query_MeetSeat_Inform://181.查询会议排位
                MyUtils.handTo(IDivMessage.Query_MeetSeat_Inform, (InterfaceRoom.pbui_Type_MeetSeatDetailInfo) result, "queryMeetRanking", handler);
                break;
            case IDivMessage.member_mission://查询参会人权限
                handTo(IDivMessage.member_mission, (InterfaceMember.pbui_Type_MemberPermission) result, "member_mission", handler);
                break;
            case IDivMessage.QUERY_ATTEND_BYID://查询指定ID的参会人(发送消息的人员)
                MyUtils.handTo(IDivMessage.QUERY_ATTEND_BYID, (InterfaceMember.pbui_Type_MemberDetailInfo) result, "assignMember", handler);
                break;
            case IDivMessage.QUERY_AGENDA://收到议程的数据
                MyUtils.handTo(IDivMessage.QUERY_AGENDA, (InterfaceAgenda.pbui_meetAgenda) result, "agenda_meet", handler);
                break;
            case IDivMessage.QUERY_MEET_DIR://136.查询会议目录
                MyUtils.handTo(IDivMessage.QUERY_MEET_DIR, (InterfaceFile.pbui_Type_MeetDirDetailInfo) result, "queryMeetDir", handler);
                break;
            case IDivMessage.QUERY_MEET_DIR_FILE://查询会议目录文件
                MyUtils.handTo(IDivMessage.QUERY_MEET_DIR_FILE, (InterfaceFile.pbui_Type_MeetDirFileDetailInfo) result, "queryMeetDirFile", handler);
                break;
            case IDivMessage.QUERY_LONG_NOTICE://查询公告
                MyUtils.handTo(IDivMessage.QUERY_LONG_NOTICE, (InterfaceBullet.pbui_BigBulletDetailInfo) result, "notice", handler);
                break;
            case IDivMessage.QUERY_SIGN://查询签到
                MyUtils.handTo(IDivMessage.QUERY_SIGN, (InterfaceSignin.pbui_Type_MeetSignInDetailInfo) result, "signInfo", handler);
                break;
            case IDivMessage.QUERY_MEET_VEDIO://查询会议视频
                MyUtils.handTo(IDivMessage.QUERY_MEET_VEDIO, (InterfaceVideo.pbui_Type_MeetVideoDetailInfo) result, "queryMeetVideo", handler);
                break;
            case IDivMessage.QUERY_VOTE://投票查询
                MyUtils.handTo(IDivMessage.QUERY_VOTE, (InterfaceVote.pbui_Type_MeetVoteDetailInfo) result, "queryVote", handler);
                break;
            case IDivMessage.QUERY_MEMBER_BYVOTE://203.查询指定投票的提交人
                MyUtils.handTo(IDivMessage.QUERY_MEMBER_BYVOTE, (InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo) result, "queryVoteMember", handler);
                break;
            case IDivMessage.QUERY_NET://网页查询
                MyUtils.handTo(IDivMessage.QUERY_NET, (InterfaceBase.pbui_meetUrl) result, "queryNet", handler);
                break;
            case IDivMessage.QUERY_CAN_JOIN://查询可加入同屏
                MyUtils.handTo(IDivMessage.QUERY_CAN_JOIN, (InterfaceDevice.pbui_Type_DeviceResPlay) result, "queryCanJoin", handler);
                break;
            case IDivMessage.QUERY_STREAM_PLAY://查询流播放
                MyUtils.handTo(IDivMessage.QUERY_STREAM_PLAY, (InterfaceStream.pbui_Type_MeetStreamPlayDetailInfo) result, "queryStreamPlay", handler);
                break;
        }
    }
}
