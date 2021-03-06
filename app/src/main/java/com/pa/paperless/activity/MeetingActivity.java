package com.pa.paperless.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceDownload;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeetfunction;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceUpload;
import com.mogujie.tt.protobuf.InterfaceWhiteboard;
import com.pa.paperless.R;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.MeetDirFileInfo;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventBadge;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fragment.meeting.AnnAgendaFragment;
import com.pa.paperless.fragment.meeting.ChatFragment;
import com.pa.paperless.fragment.meeting.MeetingFileFragment;
import com.pa.paperless.fragment.meeting.NotationFragment;
import com.pa.paperless.fragment.meeting.OverviewDocFragment;
import com.pa.paperless.fragment.meeting.SharedFileFragment;
import com.pa.paperless.fragment.meeting.SigninFragment;
import com.pa.paperless.fragment.meeting.VideoFragment;
import com.pa.paperless.fragment.meeting.VoteFragment;
import com.pa.paperless.fragment.meeting.WebBrowseFragment;
import com.pa.paperless.service.NativeService;
import com.pa.paperless.service.ShotApplication;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.MyUtils;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.pa.paperless.fragment.meeting.WebBrowseFragment.webView_isshowing;
import static com.pa.paperless.service.NativeService.nativeUtil;
import static com.pa.paperless.utils.MyUtils.getMediaid;


public class MeetingActivity extends BaseActivity implements View.OnClickListener {

    public static boolean PaletteIsShowing = false;
    private ImageButton mMeetingMessage, mMeetingChatOnline;
    private TextView mMeetingNowTime, mMeetingNowDate, mMeetingNowWeek, mMeetingTheme, mMeetingCompanyName;
    private PopupWindow mFunctionMsgPop;
    //动态添加的ImageView控件
    private ImageView WhiteBoardIV, VideoIV, MeetChatIV, PostilIV, ShareFileIV, MeetFileIV, DocumentIV, SigninIV, VoteIV, AgendaIV, WebIV;
    private List<ImageView> mImages;
    public static TextView mAttendee, mCompere;//参会人，主持人
    private FragmentManager mFm;
    private SigninFragment mSigninFragment;
    private AnnAgendaFragment mAnnAgendaFragment;
    private MeetingFileFragment mMeetingFileFragment;
    private SharedFileFragment mSharedFileFragment;
    private NotationFragment mNotationFragment;
    private ChatFragment mChatFragment;
    private VideoFragment mVideoFragment;
    private VoteFragment mVoteFragment;
    private WebBrowseFragment mWebbrowseFragment;
    private OverviewDocFragment mOverViewDocFragment;
    public static List<ReceiveMeetIMInfo> mReceiveMsg = new ArrayList<>();//存放收到的聊天信息
    private List<MemberInfo> memberInfos;
    public static Badge mBadge; //未读消息提示
    private List<Integer> mFunIndexs;
    //存放是否点中
    public static List<Boolean> checkedJoinMember = new ArrayList<>();
    public static List<Boolean> checkedJoinProjector = new ArrayList<>();
    public static InterfaceDevice.pbui_Type_DeviceFaceShowDetail DevMeetInfo = MainActivity.getLocalInfo();//设备会议信息
    private LinearLayout fragment_layout;
    private Resources resources;
    private MeetingActivity context;

    private List<DeviceInfo> deviceInfos;
    private int MSG_TYPE;
    private boolean isRestart = false;
    private int saveIndex;//存放跳转之前所展示的Fragment索引
    private File upLoadPostilFile;//WPS编辑完成后要上传的文件
    public static boolean chatisshowing = false;//会议聊天界面是否显示状态

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            /** **** **  收到信息  ** **** **/
            case IDEventF.meet_function://收到会议功能信息
                receiveFunInfo(message);
                break;
            case IDEventF.member_info://收到参会人信息
                receiveMemberInfo(message);
                break;
            case IDEventF.dev_info://收到设备信息
                receiveDevInfo(message);
                break;
            case IDEventF.dev_meet_info://收到设备会议信息
                Log.e(TAG, "MeetingActivity.getEventMessage :  收到设备会议信息 --> ");
                receiveDevMeetInfo(message);
                break;
            case IDEventF.meet_seat://收到会议排位信息
                Log.e(TAG, "MeetingActivity.getEventMessage :  收到会议排位信息 --> ");
                receiveMeetSeatInfo(message);
                break;
            case IDEventF.meet_chat_info://收到会议交流信息
                Log.e(TAG, "MeetingActivity.getEventMessage :  收到会议交流信息 --> ");
                receiveMeetChatInfo(message);
                break;
            case IDEventF.member_byID://收到指定的参会人员
                Log.e(TAG, "MeetingActivity.getEventMessage :  收到指定的参会人员 --> ");
                receiveMemberByID(message);
                break;
            /** **** **  变更通知  ** **** **/
            case IDEventMessage.MEET_FUNCTION_CHANGEINFO://会议功能变更通知
                Log.e(TAG, "MeetingActivity.getEventMessage :  会议功能变更通知 --> ");
                //查询会议功能
                nativeUtil.queryMeetFunction();
                break;
            case IDEventMessage.MEMBER_CHANGE_INFORM:// 参会人员变更通知
                Log.e(TAG, "MeetingActivity.getEventMessage :  参会人员变更通知 --> ");
                //查询参会人员
                nativeUtil.queryAttendPeople();
                InterfaceBase.pbui_MeetNotifyMsg MrmberName = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                //查询指定ID的参会人 （接收到消息时）
                nativeUtil.queryAttendPeopleFromId(MrmberName.getId());
                break;
            case IDEventMessage.DEVMEETINFO_CHANGE_INFORM://设备会议信息变更通知
                Log.e(TAG, "MeetingActivity.getEventMessage :  设备会议信息变更通知 --> ");
                nativeUtil.queryDeviceMeetInfo();
                break;
            case IDEventMessage.DEV_REGISTER_INFORM://设备寄存器变更通知（监听参会人退出）
                Log.e(TAG, "MeetingActivity.getEventMessage :  设备寄存器变更通知 查询参会人--> ");
                //查询参会人信息
                nativeUtil.queryAttendPeople();
                break;
            case IDEventMessage.SIGN_CHANGE_INFORM://签到变更通知(监听参会人进入会议界面)
                Log.e(TAG, "MeetingActivity.getEventMessage :  签到变更通知(监听参会人进入会议界面) --> ");
                //查询参会人信息
//                nativeUtil.queryAttendPeople();
                nativeUtil.querySign();
                break;
            case IDEventMessage.FACESTATUS_CHANGE_INFORM://界面状态变更通知
                Log.e(TAG, "MeetingActivity.getEventMessage :  界面状态变更通知 --> ");
                nativeUtil.queryAttendPeople();
                break;
            case IDEventMessage.MEET_DATE:
                //更新时间UI
                updataTimeUi(message);
                break;

            case IDEventF.well_open_pdf://打开PDF类文件
                String s = (String) message.getObject();
                Intent intent1 = new Intent(context, PDFActivity.class);
                intent1.putExtra("pdffilepath", s);
                startActivity(intent1);
                break;
            case IDEventF.updata_to_postil://收到WPS编辑完成后的广播
                Log.e(TAG, "MeetingActivity.getEventMessage :  收到WPS编辑完成后的文件路径 --> ");
                //获取要上传的文件
                upLoadPostilFile = MyUtils.getmFile();
                //查找一次所有的批注文件
                nativeUtil.queryMeetDirFile(2);//2 为批注目录
                break;
            case IDEventF.meet_dir_file://收到会议目录文件（批注文件）
                if (upLoadPostilFile != null) {
                    InterfaceFile.pbui_Type_MeetDirFileDetailInfo object2 = (InterfaceFile.pbui_Type_MeetDirFileDetailInfo) message.getObject();
                    List<MeetDirFileInfo> meetDirFileInfos = Dispose.MeetDirFile(object2);
                    String path = upLoadPostilFile.getPath();
                    int mediaid = getMediaid(path);
                    String fileName = upLoadPostilFile.getName();
                    for (int i = 0; i < meetDirFileInfos.size(); i++) {
                        if (upLoadPostilFile.getName().equals(meetDirFileInfos.get(i).getFileName())) {
                            // 如果批注文件中有这个文件名,那么将时间信息添加进去
                            fileName = "(" + mMeetingNowDate.getText().toString() + "" + mMeetingNowTime.getText().toString() + "-修订)" + upLoadPostilFile.getName();
                        }
                    }
                    /** **** **  将编辑保存完的文件上传到批注文档目录  ** **** **/
                    nativeUtil.uploadFile(InterfaceMacro.Pb_Upload_Flag.Pb_MEET_UPLOADFLAG_ONLYENDCALLBACK.getNumber(),
                            2, 0, fileName, path, 0, mediaid);
                    upLoadPostilFile = null;//重新置空
                }
                break;
            case IDEventMessage.Upload_Progress://上传进度通知
                //上传完成重新设置为false
                InterfaceUpload.pbui_TypeUploadPosCb object = (InterfaceUpload.pbui_TypeUploadPosCb) message.getObject();
                break;
            case IDEventF.open_picture://收到打开图片
                String filepath = (String) message.getObject();
                Log.e(TAG, "MeetingActivity.getEventMessage :  将要打开的图片的路径 --> " + filepath);
                FileUtil.openPicture(filepath, context, findViewById(R.id.meeting_layout_id));
                break;
            case IDEventMessage.MEETDIR_CHANGE_INFORM://135 会议目录变更通知
                nativeUtil.queryMeetDir();
                break;
            case IDEventMessage.MEETDIR_FILE_CHANGE_INFORM://142 会议目录文件变更通知
                InterfaceBase.pbui_MeetNotifyMsgForDouble object1 = (InterfaceBase.pbui_MeetNotifyMsgForDouble) message.getObject();
                int id = object1.getId();
                nativeUtil.queryMeetDirFile(id);
                break;
            case IDEventMessage.AGENDA_CHANGE_INFO://议程变更通知
                nativeUtil.queryAgenda();
                break;
            case IDEventMessage.MeetSeat_Change_Inform://会议排位变更通知
//                nativeUtil.queryMeetRanking();
                nativeUtil.queryAttendPeople();
                break;
            case IDEventMessage.PLACE_DEVINFO_CHANGEINFORM://会场设备信息变更通知
                //119 会场设备信息变更通知(投影机)
                /** ************ ******  125.查询符合要求的设备ID  （投影机）  ****** ************ **/
                nativeUtil.queryRequestDeviceId(DevMeetInfo.getRoomid(),
                        Macro.DEVICE_MEET_PROJECTIVE,
//                        InterfaceMacro.Pb_RoomDeviceFilterFlag.Pb_MEET_ROOMDEVICE_FLAG_PROJECTIVE.getNumber(),
                        InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace.getNumber(), 0);
                break;
            case IDEventMessage.DOWN_FINISH://67 下载进度回调
                enentDownFinish(message);
                break;
            case IDEventMessage.OPEN_BOARD://收到白板打开操作
                if (!PaletteIsShowing) {
                    eventOpenBoard(message);
                }
                break;
        }
    }

    private void receiveMemberByID(EventMessage message) {
        Log.d(TAG, "receiveMemberByID: ");
        InterfaceMember.pbui_Type_MemberDetailInfo someOneMember = (InterfaceMember.pbui_Type_MemberDetailInfo) message.getObject();
        List<InterfaceMember.pbui_Item_MemberDetailInfo> itemList = someOneMember.getItemList();
        if (itemList != null) {
            String name = MyUtils.getBts(itemList.get(0).getName());
            mReceiveMsg.get(mReceiveMsg.size() - 1).setName(name);//设置好发消息过来的参会人名称
            EventBus.getDefault().post(new EventMessage(IDEventF.post_new_chatMsg));
        }
    }

    private void receiveMeetChatInfo(EventMessage message) {
        Log.d(TAG, "receiveMeetChatInfo: ");
        if (!chatisshowing) {//确保会议交流界面不在显示状态
            InterfaceIM.pbui_Type_MeetIM receiveMsg = (InterfaceIM.pbui_Type_MeetIM) message.getObject();
            //获取之前的未读消息个数
            int badgeNumber1 = mBadge.getBadgeNumber();
            Log.e("MyLog", "MyUtils.receiveMessage :  原来的个数 --->>> " + badgeNumber1);
            if (receiveMsg != null) {
                if (receiveMsg.getMsgtype() == 0) {//证明是文本类消息
                    int all = badgeNumber1 + 1;
                    List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(receiveMsg);
                    receiveMeetIMInfos.get(0).setType(true);//设置是接收的消息
                    mReceiveMsg.add(receiveMeetIMInfos.get(0));
                    try {
                        //查询指定ID的参会人  获取名称
                        nativeUtil.queryAttendPeopleFromId(receiveMeetIMInfos.get(0).getMemberid());
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                    Log.e("MyLog", "MyUtils.receiveMessage : 收到的信息个数：  --->>> " + mReceiveMsg.size());
                    List<EventBadge> num = new ArrayList<>();
                    num.add(new EventBadge(all));
                    Log.e("MyLog", "MyUtils.receiveMessage :  传递的总未读消息个数 --->>> " + all);
                    mBadge.setBadgeNumber(all);
                }
            }
        }
    }

    private void receiveMeetSeatInfo(EventMessage message) {
        Log.d(TAG, "receiveMeetSeatInfo: ");
        InterfaceRoom.pbui_Type_MeetSeatDetailInfo o5 = (InterfaceRoom.pbui_Type_MeetSeatDetailInfo) message.getObject();
        List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> itemList3 = o5.getItemList();
        for (int i = 0; i < itemList3.size(); i++) {
            InterfaceRoom.pbui_Item_MeetSeatDetailInfo pbui_item_meetSeatDetailInfo = itemList3.get(i);
            int nameId = pbui_item_meetSeatDetailInfo.getNameId();
            int seatid = pbui_item_meetSeatDetailInfo.getSeatid();//这是设备ID
            int role = pbui_item_meetSeatDetailInfo.getRole();
            if (role == 3) {
                if (DevMeetInfo.getMemberid() == nameId) {
                    EventBus.getDefault().post(new EventMessage(IDEventF.you_are_compere));
                    //本机的身份为主持人
                    VoteFragment.isCompere = true;
                    // 设置控件展示主持人名称
                    mCompere.setText(MyUtils.getBts(DevMeetInfo.getMembername()));
                } else {
                    //本机的身份不是主持人
                    VoteFragment.isCompere = false;
                    EventBus.getDefault().post(new EventMessage(IDEventF.you_not_compere));
                    // 设置控件展示主持人名称
                    for (int j = 0; j < deviceInfos.size(); j++) {
                        if (deviceInfos.get(j).getDevId() == seatid) {  //查找到 为主持人身份的设备信息
                            for (int k = 0; k < memberInfos.size(); k++) {
                                if (memberInfos.get(k).getPersonid() == nameId) {    //匹对人员ID  获取参会人（主持人）姓名
                                    Log.e(TAG, "MeetingActivity.receiveMeetSeatInfo :  主持人名称 --> " + memberInfos.get(k).getName());
                                    mCompere.setText(memberInfos.get(k).getName());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void receiveDevMeetInfo(EventMessage message) {
        Log.d(TAG, "receiveDevMeetInfo: ");
        DevMeetInfo = (InterfaceDevice.pbui_Type_DeviceFaceShowDetail) message.getObject();
        mMeetingCompanyName.setText(MyUtils.getBts(DevMeetInfo.getCompany()));
        mMeetingTheme.setText(MyUtils.getBts(DevMeetInfo.getMeetingname()));
        mAttendee.setText(MyUtils.getBts(DevMeetInfo.getMembername()));
        try {
            //181.查询会议排位
            nativeUtil.queryMeetRanking();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        /** **** **  设置保存本机设备ID和参会人ID  ** **** **/
        NativeService.localDevId = DevMeetInfo.getDeviceid();
        NativeService.localMemberId = DevMeetInfo.getMemberid();
    }

    private void receiveDevInfo(EventMessage message) {
        Log.d(TAG, "receiveDevInfo: ");
        InterfaceDevice.pbui_Type_DeviceDetailInfo o = (InterfaceDevice.pbui_Type_DeviceDetailInfo) message.getObject();
        deviceInfos = Dispose.DevInfo(o);
        try {
            //查询设备会议信息
            nativeUtil.queryDeviceMeetInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void receiveMemberInfo(EventMessage message) {
        Log.d(TAG, "receiveMemberInfo: ");
        InterfaceMember.pbui_Type_MemberDetailInfo o = (InterfaceMember.pbui_Type_MemberDetailInfo) message.getObject();
        memberInfos = Dispose.MemberInfo(o);
        try {
            //查询设备信息
            nativeUtil.queryDeviceInfo();
            //查询参会人员权限
            nativeUtil.queryAttendPeoplePermissions();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void receiveFunInfo(EventMessage message) {
        Log.d(TAG, "receiveFunInfo: ");
        InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo meetfun = (InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo) message.getObject();
        List<InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo> itemList4 = meetfun.getItemList();
        if (mFunIndexs != null) {
            mFunIndexs.clear();
            fragment_layout.removeAllViews();
        } else {
            mFunIndexs = new ArrayList<>();
        }
        if (mImages != null) {
            mImages.clear();
        } else {
            mImages = new ArrayList<>();
        }
        for (int i = 0; i < itemList4.size(); i++) {
            InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo function = itemList4.get(i);
            int funcode = function.getFuncode();
            if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_AGENDA_BULLETIN.getNumber()) {
                AgendaIV = new ImageView(context, null, R.style.MeetBottomImageView);
                AgendaIV.setBackground(resources.getDrawable(R.drawable.selector_announce_agenda));
                fragment_layout.addView(AgendaIV);
                mImages.add(AgendaIV);
                mFunIndexs.add(0);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MATERIAL.getNumber()) {
                mFunIndexs.add(1);
                MeetFileIV = new ImageView(context, null, R.style.MeetBottomImageView);
                MeetFileIV.setBackground(resources.getDrawable(R.drawable.selector_meetingfile));
                fragment_layout.addView(MeetFileIV);
                mImages.add(MeetFileIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SHAREDFILE.getNumber()) {
                mFunIndexs.add(2);
                ShareFileIV = new ImageView(context, null, R.style.MeetBottomImageView);
                ShareFileIV.setBackground(resources.getDrawable(R.drawable.selector_sharedfile));
                fragment_layout.addView(ShareFileIV);
                mImages.add(ShareFileIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_POSTIL.getNumber()) {
                mFunIndexs.add(3);
                PostilIV = new ImageView(context, null, R.style.MeetBottomImageView);
                PostilIV.setBackground(resources.getDrawable(R.drawable.selector_postil));
                fragment_layout.addView(PostilIV);
                mImages.add(PostilIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MESSAGE.getNumber()) {
                mFunIndexs.add(4);
                MeetChatIV = new ImageView(context, null, R.style.MeetBottomImageView);
                MeetChatIV.setBackground(resources.getDrawable(R.drawable.selector_chat));
                fragment_layout.addView(MeetChatIV);
                mImages.add(MeetChatIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VIDEOSTREAM.getNumber()) {
                mFunIndexs.add(5);
                VideoIV = new ImageView(context, null, R.style.MeetBottomImageView);
                VideoIV.setBackground(resources.getDrawable(R.drawable.selector_video));
                fragment_layout.addView(VideoIV);
                mImages.add(VideoIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WHITEBOARD.getNumber()) {
                WhiteBoardIV = new ImageView(context, null, R.style.MeetBottomImageView);
                WhiteBoardIV.setBackground(resources.getDrawable(R.drawable.selector_whiteboard));
                fragment_layout.addView(WhiteBoardIV);
                mFunIndexs.add(6);
                mImages.add(WhiteBoardIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WEBBROWSER.getNumber()) {
                mFunIndexs.add(7);
                WebIV = new ImageView(context, null, R.style.MeetBottomImageView);
                WebIV.setBackground(resources.getDrawable(R.drawable.selector_web));
                fragment_layout.addView(WebIV);
                mImages.add(WebIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VOTERESULT.getNumber()) {
                VoteIV = new ImageView(context, null, R.style.MeetBottomImageView);
                VoteIV.setBackground(resources.getDrawable(R.drawable.selector_vote));
                fragment_layout.addView(VoteIV);
                mImages.add(VoteIV);
                mFunIndexs.add(8);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SIGNINRESULT.getNumber()) {
                SigninIV = new ImageView(context, null, R.style.MeetBottomImageView);
                SigninIV.setBackground(resources.getDrawable(R.drawable.selector_signin));
                fragment_layout.addView(SigninIV);
                mImages.add(SigninIV);
                mFunIndexs.add(9);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_DOCUMENT.getNumber()) {
                DocumentIV = new ImageView(context, null, R.style.MeetBottomImageView);
                DocumentIV.setBackground(resources.getDrawable(R.drawable.selector_document));
                fragment_layout.addView(DocumentIV);
                mImages.add(DocumentIV);
                mFunIndexs.add(10);
            }
        }
        if (mImages.size() > 0 && mFunIndexs.size() > 0) {
            /** **** **  查询到会议功能才进行下一步  ** **** **/
            //设置默认点击第一项
            if (mFunIndexs.contains(Macro.Pb_MEET_FUNCODE_POSTIL)) {
                for (int i = 0; i < mFunIndexs.size(); i++) {
                    if (mFunIndexs.get(i) == Macro.Pb_MEET_FUNCODE_POSTIL) {
                        /** **** **  目前的方法：如果有批注就先展示批注页面  ** **** **/
                        showFragment(Macro.Pb_MEET_FUNCODE_POSTIL);
                        setImgSelect(i);
                    }
                }
                /** **** **  如果是第一次，则是默认值0  如果是经过了 restart 则是屏幕跳转前的值  ** **** **/
            }
            if (isRestart) {
                showFragment(saveIndex);
                setImgSelect(saveIndex);
                isRestart = false;
            } else {
                showFragment(0);
                setImgSelect(0);
            }
            initImages(mImages, mFunIndexs);
            startIntent(REQUEST_MEDIA_PROJECTION);
            try {
                /** **** **  小圆点中需要实现的方法  ** **** **/
                //92.查询参会人员
                nativeUtil.queryAttendPeople();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    private void enentDownFinish(EventMessage message) throws InvalidProtocolBufferException {
        Log.d(TAG, "enentDownFinish: ");
        InterfaceDownload.pbui_Type_DownloadCb object5 = (InterfaceDownload.pbui_Type_DownloadCb) message.getObject();
        int mediaid = object5.getMediaid();
        int progress = object5.getProgress();
        byte[] bytes = nativeUtil.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber()
                , mediaid);
        InterfaceBase.pbui_CommonTextProperty pbui_commonTextProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
        String downOverFileName = MyUtils.getBts(pbui_commonTextProperty.getPropertyval());
        if (object5.getProgress() < 100) {
            showTip(downOverFileName + " 当前下载进度：" + progress + "%");
        } else {
            showTip(downOverFileName + " 下载完毕 ");
            File file = FileUtil.findFilePathByName(Macro.MEETFILE, downOverFileName);
            if (file != null) {
                MyUtils.OpenThisFile(context, file);
            }
        }
    }

    private void eventOpenBoard(EventMessage message) {
        Log.d(TAG, "eventOpenBoard: ");
        InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard object4 = (InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard) message.getObject();
        int operflag = object4.getOperflag();
        ByteString medianame = object4.getMedianame();
        int opermemberid = object4.getOpermemberid();
        int srcmemid = object4.getSrcmemid();
        long srcwbidd = object4.getSrcwbid();
        if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_FORCEOPEN.getNumber()) {
            //强制打开白板  直接强制同意加入
            nativeUtil.agreeJoin(opermemberid, srcmemid, srcwbidd);
            startActivity(new Intent(context, PeletteActivity.class));
            PeletteActivity.isSharing = true;//如果同意加入就设置已经在共享中
            PeletteActivity.launchPersonId = srcmemid;//设置发起的人员ID
            PeletteActivity.mSrcwbid = srcwbidd;
        } else if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber()) {
            //询问打开白板
            WhetherOpen(opermemberid, srcmemid, srcwbidd, MyUtils.getBts(medianame));
        }
    }

    private void WhetherOpen(final int opermemberid, final int srcmemid, final long srcwbidd, String medianame) {
        Log.d(TAG, "WhetherOpen: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("是否同意加入 " + medianame + " 发起的共享批注？");
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //同意加入
                nativeUtil.agreeJoin(NativeService.localMemberId, srcmemid, srcwbidd);
                PeletteActivity.isSharing = true;//如果同意加入就设置已经在共享中
                PeletteActivity.launchPersonId = srcmemid;//设置发起的人员ID
                PeletteActivity.mSrcwbid = srcwbidd;
                startActivity(new Intent(context, PeletteActivity.class));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nativeUtil.rejectJoin(NativeService.localMemberId, srcmemid, srcwbidd);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        Log.d(TAG, "onSaveInstanceState: ");
////        super.onSaveInstanceState(outState);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("A_life", "MeetingActivity.onCreate :   --> ");
        setContentView(R.layout.activity_meeting);
        context = this;
        resources = context.getResources();
        initView();
        mFm = getSupportFragmentManager();
        //8.修改本机界面状态
        nativeUtil.setInterfaceState(InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace.getNumber());
        try {
            nativeUtil.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY.getNumber());//缓存会议目录
            nativeUtil.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber(), 0);//缓存会场设备
            nativeUtil.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT.getNumber());//缓存会议排位
            nativeUtil.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber());//缓存参会人信息
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        try {
            //238.查询会议功能
            nativeUtil.queryMeetFunction();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    //获取当前的会议名称
    public static String getMeetName() {
        Log.d(TAG, "getMeetName: ");
        return MyUtils.getBts(DevMeetInfo.getMeetingname());
    }

    private void updataTimeUi(EventMessage message) {
        Log.d(TAG, "updataTimeUi: ");
        String[] object = (String[]) message.getObject();
        mMeetingNowDate.setText(object[0]);
        mMeetingNowWeek.setText(object[1]);
        mMeetingNowTime.setText(object[2]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("A_life", "MeetingActivity.onDestroy :   --->>> ");
        //关闭悬浮窗的服务
        ((ShotApplication) getApplication()).del();
        //8.修改本机界面状态
        nativeUtil.setInterfaceState(InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MainFace.getNumber());
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "MeetingActivity.onBackPressed :   --> ");
        if (webView_isshowing) {//判断当前网络界面是否显示
            EventBus.getDefault().post(new EventMessage(IDEventF.go_back_html));
        } else {
            //回到主界面操作
            nativeUtil.backToMainInterfaceOperate(DevMeetInfo.getDeviceid());
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: ");
        super.onNewIntent(intent);
    }

    @Override
    public void finish() {
        Log.d(TAG, "finish: ");
//        moveTaskToBack(true);
        super.finish();
    }

    @Override
    protected void onRestart() {
        //Activity进行跳转之后回到本界面时重新设置
        Log.e("A_life", "MeetingActivity.onRestart :   --->>> ");
        isRestart = true;
        /** **** **  回到MeetingActivity时重新设置默认点击  ** **** **/
        try {
            nativeUtil.queryMeetFunction();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        super.onRestart();
    }

    //设置功能按钮和相关点击事件
    private void initImages(List<ImageView> imgs, final List<Integer> mf) {
        for (int i = 0; i < imgs.size(); i++) {
            ImageView imageView = imgs.get(i);
            final int finalI = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setImgSelect(finalI);
                    for (int j = 0; j < mf.size(); j++) {
                        if (finalI == j) {
                            showFragment(mf.get(j));
                            if (mf.get(j) == 6) {//电子白板
                                startActivity(new Intent(context, PeletteActivity.class));
                            }
                        }
                    }
                }
            });
        }
    }

    private void setImgSelect(int index) {
        for (int i = 0; i < mImages.size(); i++) {
            ImageView imageView = mImages.get(i);
            if (i == index) {
                imageView.setSelected(true);
            } else {
                imageView.setSelected(false);
            }
        }
    }

    public void showFragment(int index) {
        Log.d(TAG, "showFragment: ");
        if (Macro.Pb_MEET_FUNCODE_POSTIL != index && Macro.Pb_MEET_FUNCODE_WHITEBOARD != index) {
            saveIndex = index;
        }
        FragmentTransaction ft = mFm.beginTransaction();
        hideFragment(ft);
        switch (index) {
            case Macro.Pb_MEET_FUNCODE_SIGNINRESULT:
                if (mSigninFragment == null) {
                    mSigninFragment = new SigninFragment();
                    ft.add(R.id.right_layout, mSigninFragment);
                }
                ft.show(mSigninFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_AGENDA_BULLETIN:
                if (mAnnAgendaFragment == null) {
                    mAnnAgendaFragment = new AnnAgendaFragment();
                    ft.add(R.id.right_layout, mAnnAgendaFragment);
                }
                ft.show(mAnnAgendaFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_MATERIAL:
                if (mMeetingFileFragment == null) {
                    mMeetingFileFragment = new MeetingFileFragment();
                    ft.add(R.id.right_layout, mMeetingFileFragment);
                }
                ft.show(mMeetingFileFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_SHAREDFILE:
                if (mSharedFileFragment == null) {
                    mSharedFileFragment = new SharedFileFragment();
                    ft.add(R.id.right_layout, mSharedFileFragment);
                }
                ft.show(mSharedFileFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_MESSAGE:
                if (mChatFragment == null) {
                    mChatFragment = new ChatFragment();
                    ft.add(R.id.right_layout, mChatFragment);
                }
                ft.show(mChatFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_VIDEOSTREAM:
                if (mVideoFragment == null) {
                    mVideoFragment = new VideoFragment();
                    ft.add(R.id.right_layout, mVideoFragment);
                }
                ft.show(mVideoFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_VOTERESULT:
                if (mVoteFragment == null) {
                    mVoteFragment = new VoteFragment();
                    ft.add(R.id.right_layout, mVoteFragment);
                }
                ft.show(mVoteFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_POSTIL:
                if (mNotationFragment == null) {
                    mNotationFragment = new NotationFragment();
                    ft.add(R.id.right_layout, mNotationFragment);
                }
                ft.show(mNotationFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_WEBBROWSER:
                if (mWebbrowseFragment == null) {
                    mWebbrowseFragment = new WebBrowseFragment();
                    ft.add(R.id.right_layout, mWebbrowseFragment);
                }
                ft.show(mWebbrowseFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_DOCUMENT:
                if (mOverViewDocFragment == null) {
                    mOverViewDocFragment = new OverviewDocFragment();
                    ft.add(R.id.right_layout, mOverViewDocFragment);
                }
                ft.show(mOverViewDocFragment);
                break;
        }
        ft.commit();
    }

    /**
     * 如果Fragment不为空 就先隐藏起来
     *
     * @param ft
     */
    public void hideFragment(FragmentTransaction ft) {
        Log.d(TAG, "hideFragment: ");

        if (mSigninFragment != null) {
            ft.hide(mSigninFragment);
        }
        if (mAnnAgendaFragment != null) {
            ft.hide(mAnnAgendaFragment);
        }
        if (mMeetingFileFragment != null) {
            ft.hide(mMeetingFileFragment);
        }
        if (mSharedFileFragment != null) {
            ft.hide(mSharedFileFragment);
        }
        if (mChatFragment != null) {
            ft.hide(mChatFragment);
        }
        if (mVideoFragment != null) {
            ft.hide(mVideoFragment);
        }
        if (mVoteFragment != null) {
            ft.hide(mVoteFragment);
        }
        if (mNotationFragment != null) {
            ft.hide(mNotationFragment);
        }
        if (mWebbrowseFragment != null) {
            ft.hide(mWebbrowseFragment);
        }
        if (mOverViewDocFragment != null) {
            ft.hide(mOverViewDocFragment);
        }

    }

    private void initView() {
        mMeetingCompanyName = (TextView) findViewById(R.id.meeting_company_name);
        mMeetingTheme = (TextView) findViewById(R.id.meeting_theme);
        mMeetingMessage = (ImageButton) findViewById(R.id.meeting_message);
        /** ************ ******  设置未读消息展示  ****** ************ **/
        mBadge = new QBadgeView(this).bindTarget(mMeetingMessage);
        mBadge.setBadgeGravity(Gravity.END | Gravity.TOP);
        mBadge.setBadgeTextSize(20, false);
        mBadge.setShowShadow(true);
        mBadge.setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
            @Override
            public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                //只需要空实现，就可以拖拽消除未读消息
            }
        });
        mMeetingChatOnline = (ImageButton) findViewById(R.id.meeting_chat_online);
        mMeetingNowTime = (TextView) findViewById(R.id.meeting_now_time);
        mMeetingNowDate = (TextView) findViewById(R.id.meeting_now_date);
        mMeetingNowWeek = (TextView) findViewById(R.id.meeting_now_week);
        mAttendee = (TextView) findViewById(R.id.meeting_participate_name);
        mCompere = (TextView) findViewById(R.id.meeting_host_name);
//        mFab = (FloatingActionButton) findViewById(R.id.fab);
        fragment_layout = (LinearLayout) findViewById(R.id.fragment_layout);
        mMeetingMessage.setOnClickListener(this);
        mMeetingChatOnline.setOnClickListener(this);
//        mFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_message:
                showFragment(4);
                mBadge.setBadgeNumber(0);
                MeetChatIV.setSelected(true);
                break;
            case R.id.meeting_chat_online:
                // TODO: 2018/2/7 打开发送服务选项
                showSendFunctionMsg();
                break;
        }
    }

    /**
     * 服务功能选项
     */
    private void showSendFunctionMsg() {
        Log.d(TAG, "showSendFunctionMsg: ");
        View popupView = getLayoutInflater().inflate(R.layout.pop_function_msg, null);
        mFunctionMsgPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(mFunctionMsgPop);
        mFunctionMsgPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mFunctionMsgPop.setTouchable(true);
        mFunctionMsgPop.setOutsideTouchable(true);
        FunViewHolder holder = new FunViewHolder(popupView);
        Function_Event(holder);
        mFunctionMsgPop.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    /**
     * 服务功能选项事件监听
     *
     * @param holder
     */
    private void Function_Event(final FunViewHolder holder) {
        final List<Integer> arr = new ArrayList<>();
        arr.add(0);//会议服务类请求则为 0
        MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber();
        // TODO: 2018/2/7 服务功能事件 发送会议交流信息
        //纸
        holder.paper_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
//                /** ************ ******  185.发送会议交流信息  ****** ************ **/
                holder.edt_msg.setText("纸张申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Paper.getNumber();
            }
        });
        //笔
        holder.pen_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("需要一支笔");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Pen.getNumber();
            }
        });
        //茶水
        holder.tea_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("茶水申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Tea.getNumber();
            }
        });
        //矿泉水
        holder.water_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("矿泉水申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Water.getNumber();
            }
        });
        //计算器
        holder.calculator_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("计算器申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber();
            }
        });
        //服务员
        holder.waiter_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("服务员申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Waiter.getNumber();
            }
        });
        //清扫
        holder.sweep_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("需要清扫");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber();
            }
        });
        //技术员
        holder.technician_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("技术员申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Technical.getNumber();
            }
        });
        //发送
        holder.send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String other = holder.edt_msg.getText().toString();
                nativeUtil.sendMeetChatInfo(other, MSG_TYPE, arr);
                /** **** **  发送后清空输入框  ** **** **/
                holder.edt_msg.setText("");
            }
        });

    }

    /**
     * 服务功能 控件
     */
    public static class FunViewHolder {
        public View rootView;
        public TextView paper_msg;
        public TextView pen_msg;
        public TextView tea_msg;
        public TextView water_msg;
        public TextView calculator_msg;
        public TextView waiter_msg;
        public TextView sweep_msg;
        public TextView technician_msg;
        public EditText edt_msg;
        public TextView send_msg;

        public FunViewHolder(View rootView) {
            this.rootView = rootView;
            this.paper_msg = (TextView) rootView.findViewById(R.id.paper_msg);
            this.pen_msg = (TextView) rootView.findViewById(R.id.pen_msg);
            this.tea_msg = (TextView) rootView.findViewById(R.id.tea_msg);
            this.water_msg = (TextView) rootView.findViewById(R.id.water_msg);
            this.calculator_msg = (TextView) rootView.findViewById(R.id.calculator_msg);
            this.waiter_msg = (TextView) rootView.findViewById(R.id.waiter_msg);
            this.sweep_msg = (TextView) rootView.findViewById(R.id.sweep_msg);
            this.technician_msg = (TextView) rootView.findViewById(R.id.technician_msg);
            this.edt_msg = (EditText) rootView.findViewById(R.id.edt_msg);
            this.send_msg = (TextView) rootView.findViewById(R.id.send_msg);
        }
    }

    /**
     * add by gowcage
     */
    public static final String TAG = "MeetingActivity-->";


    @Override
    protected void onStart() {
        Log.e("A_life", "MeetingActivity.onStart :   --->>> ");
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.e("A_life", "MeetingActivity.onResume :  TaskId --->>> " + getTaskId());
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e("A_life", "MeetingActivity.onPause :   --->>> ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        //取消注册事件
        EventBus.getDefault().unregister(this);
        Log.e("A_life", "MeetingActivity.onStop :   --->>> ");
        super.onStop();
    }
}

