package com.pa.paperless.constant;

/**
 * Created by Administrator on 2018/1/24.
 */

public class IDEventMessage {
    //会议信息变更通知
    public static final int MEETINFO_CHANGE_INFORM = 3000;
    //设备寄存器变更通知
    public static final int DEV_REGISTER_INFORM = 3001;
    //参会人员变更通知
    public static final int MEMBER_CHANGE_INFORM = 3002;
    //设备会议信息变更通知
    public static final int DEVMEETINFO_CHANGE_INFORM = 3003;
    //会场信息变更通知
    public static final int PLACEINFO_CHANGE_INFORM = 3004;
    //会场设备信息变更通知
    public static final int PLACE_DEVINFO_CHANGEINFORM = 3005;
    //管理员变更通知
    public static final int ADMIN_NOTIFI_INFORM = 3006;
    //59.会议管理员控制的会场变更通知
    public static final int MEETADMIN_PLACE_NOTIFIINFROM = 3007;
    //118.查询指定ID的会场
    public static final int QUERY_PLACE_BYID = 3008;
    //73.常用人员变更通知
    public static final int QUERY_COMMON_PEOPLE = 3009;
    //登录返回
    public static final int LOGIN_BACK = 3010;
    //会议界面时间回调
    public static final int MEET_DATE = 3011;
    //界面状态变更通知
    public static final int FACESTATUS_CHANGE_INFORM = 3012;
    //辅助签到变更通知
    public static final int SIGN_EVENT = 3013;
    //签到变更通知
    public static final int SIGN_CHANGE_INFORM = 3014;
    //收到打开白板操作
    public static final int OPEN_BOARD = 3015;
    //67 下载进度回调 -- 下载完成
    public static final int DOWN_FINISH = 3016;
    //135 会议目录变更通知
    public static final int MEETDIR_CHANGE_INFORM = 3017;
    //142 会议目录文件变更通知
    public static final int MEETDIR_FILE_CHANGE_INFORM = 3018;
    //40.网页变更通知
    public static final int NETWEB_INFORM = 3019;
    //251 媒体播放通知
    public static final int MEDIA_PLAY_INFORM = 3020;
    //180.会议排位变更通知
    public static final int MeetSeat_Change_Inform = 3021;
    //184.收到新的会议交流信息
    public static final int Receive_MeetChat_Info = 3022;
    //停止播放
    public static final int STOP_PLAY = 3023;
    //202 投票提交人变更通知
    public static final int VoteMember_ChangeInform = 3024;
    //198 投票变更通知
    public static final int Vote_Change_Inform = 3025;
    //188 有新的投票发起通知
    public static final int newVote_launch_inform = 3026;
    //72 上传进度通知 高频回调
    public static final int Upload_Progress = 3027;
    // 页面中通知会议页面更新 Badge的未读消息数
    public static final int UpDate_BadgeNumber = 3028;
    // 查询可加入的同屏会话
    public static final int QUERY_CAN_JOIN = 3029;
}
