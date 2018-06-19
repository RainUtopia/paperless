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
    //收到参会人员数量和签到数量广播通知
    public static final int signin_count = 6027;
    // 界面配置变更通知
    public static final int ICC_changed_inform = 6028;
    // 拍摄的照片
    public static final int take_photo = 6029;
    // 发送截图批注消息
    public static final int screen_postil = 6030;
    // 发送消息打开PDF文件
    public static final int well_open_pdf = 6031;
    // 会议笔记导入文本状态
    public static final int is_loading = 6032;
    // 查询得到的参会人权限
    public static final int permission_list = 6033;
    /** **** **    ** **** **/
    //后台时间
    public static final int now_time = 6034;
    //上下文
    public static final int context_proper = 6035;
    //设备信息
    public static final int dev_info = 6036;
    //设备会议信息
    public static final int dev_meet_info = 6037;
    //初始化NativeUtil
    public static final int init_native = 6038;
    //会议功能
    public static final int meet_function = 6039;
    //参会人信息
    public static final int member_info = 6040;
    //会议排位信息
    public static final int meet_seat = 6041;
    //参会人权限
    public static final int member_mission = 6042;
    //会议交流信息
    public static final int meet_chat_info = 6043;
    //指定ID的参会人员
    public static final int member_byID = 6044;
    //议程信息
    public static final int agenda_info = 6045;
    //会议目录
    public static final int meet_dir = 6046;
    //会议目录文件
    public static final int meet_dir_file = 6047;
    //公告文本
    public static final int notice = 6048;
    //签到信息
    public static final int signin_info = 6049;
    //会议视频
    public static final int meet_video = 6050;
    //投票信息
    public static final int vote = 6051;
    //指定投票的提交人
    public static final int vote_member_byId = 6052;
    //查询网页
    public static final int net_url = 6053;
    //查询可加入
    public static final int can_join = 6054;
    //查询流播放
    public static final int all_stream_play = 6055;
    //文件导出成功
    public static final int export_finish = 6056;
}
