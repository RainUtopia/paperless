package com.pa.paperless.constant;

/**
 * Created by Administrator on 2018/1/24.
 */

public class IDEventMessage {
    //平台初始化完毕
    public static final int MEETINFO_CHANGE_INFORM = 2999;
    //会议信息变更通知
    public static final int PLATFORM_INITIALIZATION = 3000;
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

    // 播放进度通知
    public static final int PLAY_PROGRESS_NOTIFY = 3030;
    // 流播放通知
    public static final int PLAY_STREAM_NOTIFY = 3031;
    // 采集流通知
    public static final int START_COLLECTION_STREAM_NOTIFY = 3032;
    // 停止采集流通知
    public static final int STOP_COLLECTION_STREAM_NOTIFY = 3033;
    // 会议视频变更通知
    public static final int Meet_vedio_changeInform = 3034;
    // 添加绘画通知
    public static final int ADD_DRAW_INFORM = 3035;
    // 改善时间回调
    public static final int DATE_TIME = 3036;
    // 同意加入通知
    public static final int AGREED_JOIN = 3037;
    // 拒绝加入通知
    public static final int REJECT_JOIN = 3038;
    // 参会人员退出白板通知
    public static final int EXIT_WHITE_BOARD = 3039;
    // 添加文本通知
    public static final int ADD_DRAW_TEXT = 3040;
    // 添加图片通知
    public static final int ADD_PIC_INFORM = 3041;
    // 添加墨迹通知
    public static final int ADD_INK_INFORM = 3042;
    // 白板删除记录通知
    public static final int WHITEBROADE_DELETE_RECOREINFORM = 3043;
    // 白板清空记录通知
    public static final int WHITEBOARD_EMPTY_RECORDINFORM = 3044;
    // 236.会议功能变更通知
    public static final int MEET_FUNCTION_CHANGEINFO = 3045;
    // 议程变更通知
    public static final int AGENDA_CHANGE_INFO = 3046;
    // 公告变更通知
    public static final int NOTICE_CHANGE_INFO = 3047;
    // 会议界面更新后发送消息给聊天界面
    public static final int updata_chat_onLineMember = 3048;
    // 在视屏直播页面通知会议界面打开同屏控制
    public static final int open_screenspop = 3049;
    // 在视屏直播界面通知会议界面打开投影控制
    public static final int open_projector = 3050;
    // 停止资源通知
    public static final int stop_stram_inform = 3052;
    // 参会人权限变更通知
    public static final int member_permission_inform = 3053;
}
