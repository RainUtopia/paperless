package com.pa.paperless.constant;

/**
 * Created by Administrator on 2018/5/12.
 */

public class IDEventF {
    // 议程界面通知会议界面查询议程信息
    public static final int query_agenda = 6000;
    // 会议界面发送议程信息给议程界面
    public static final int post_agenda = 6001;
    // 聊天界面通知查询在线参会人
    public static final int query_onLine_attend = 6002;
    // 会议界面发送在线参会人给聊天界面
    public static final int post_chat_onLineMember = 6003;
    // 聊天界面正在展示的状态下，会议界面发送新的聊天信息给ChatFragment
    public static final int post_new_chatMsg = 6004;
    // 发送消息
    public static final int send_new_chatMsg = 6005;
    //通知 查询/接收 会议目录
    public static final int need_meet_dir = 6006;
    public static final int post_meet_dir = 6007;
    // 发送会议目录文件
    public static final int post_meet_dirFile = 6008;
    // 发送批注目录的文件
    public static final int post_postil_file = 6009;
    // 发送长文本公告
    public static final int post_long_notice = 6010;
    // 发送共享资料的文件
    public static final int post_share_file = 6011;
    // 发送所有参会人信息到签到页面
    public static final int post_member_toSignin = 6012;
    // 发送签到信息
    public static final int post_signin_info = 6013;
    /** **** **  视屏直播  ** **** **/
    // 发送会议视频数据
    public static final int post_meet_video = 6014;
    // 发送设备信息给视屏直播页面
    public static final int post_devInfo_video = 6015;
    // 发送投票数据
    public static final int post_vote = 6016;
    // 发送指定投票的提交人
    public static final int post_member_byVote = 6017;
    // 发送网址
    public static final int post_newWeb_url = 6018;
    // 在WebView正在展示时 按返回键则发送回到上一个网页
    public static final int go_back_html = 6019;
    // 发送通知 本机是主持人
    public static final int you_are_compere = 6020;
    // 发送通知 本机不是主持人
    public static final int you_not_compere = 6021;
    // 发送通知打开图片
    public static final int open_picture = 6022;
    // 使用WPS编辑后，点击保存时发送消息上传到批注服务器列表中
    public static final int updata_to_postil = 6023;
    // 重新查找批注文件得到文件，与当前上传的文件进行比较
    public static final int upload_postil = 6024;
    /** **** **  发送给悬浮窗参会人和投影机信息  ** **** **/
    public static final int fab_member_pro = 6025;
    //通知打开会议笔记
    public static final int open_note = 6026;


}
