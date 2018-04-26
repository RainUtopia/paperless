package com.pa.paperless.fragment.meeting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfacePlaymedia;
import com.pa.paperless.R;
import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.adapter.MeetingFileTypeAdapter;
import com.pa.paperless.adapter.TypeFileAdapter;
import com.pa.paperless.bean.MeetDirFileInfo;
import com.pa.paperless.bean.MeetingFileTypeBean;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.MyUtils;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.libsdl.app.SDLActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 * 会议资料
 * 进入页面：1.查询文件目录 2.根据目录ID查找文件 3.先展示出第一个item 的文档类的数据
 */

public class MeetingFileFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private NativeUtil nativeUtil;
    private ListView dir_lv;
    private Button rightmeetfile_document;
    private Button rightmeetfile_picture;
    private Button rightmeetfile_video;
    private Button rightmeetfile_other;
    private ListView rightmeetfile_lv;
    private Button rightmeetfile_prepage;
    private Button rightmeetfile_nextpage;
    private Button rightmeetfile_import;
    private MeetingFileTypeAdapter mDirAdapter;
    private List<Button> mBtns;
    private TypeFileAdapter dataAdapter;

    //  存放目录ID、目录名称
    private List<MeetingFileTypeBean> mDirData = new ArrayList<>();
    //  存放会议目录文件的详细信息
    private List<MeetDirFileInfo> meetDirFileInfos = new ArrayList<>();
    //  用来临时存放不同类型的文件并展示
    private List<MeetDirFileInfo> mFileData = new ArrayList<>();
    //播放时的媒体ID
    public static int mMediaid;
    private int mDirId;//要查找的目录ID，查找该目录下的文件
    private boolean isFirstIn;//默认设置是第一次进入

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e("FramentLife", "MeetingFileFragment.onCreateView:   --->>> ");
        View inflate = inflater.inflate(R.layout.right_mettingfile, container, false);
        isFirstIn = true;
        initController();
        initView(inflate);
        initData();
        initBtns();
        try {
            //136.查询会议目录
            nativeUtil.queryMeetDir();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_MEET_DIR://查询会议目录
                    queryMeetDirU(msg);
                    break;
                case IDivMessage.QUERY_MEET_DIR_FILE://查询会议目录文件
                    queryMeetDirFileU(msg);
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.MEETDIR_CHANGE_INFORM://135 会议目录变更通知
                Log.e("MyLog", "MeetingFileFragment.getEventMessage 173行:  会议目录变更通知EventBus --->>> ");
                nativeUtil.queryMeetDir();
                break;
            case IDEventMessage.MEETDIR_FILE_CHANGE_INFORM://142 会议目录文件变更通知
                if (!isHidden()) {
                    Log.e("MyLog", "MeetingFileFragment.getEventMessage 180行:  会议目录文件变更通知EventBus --->>> ");
                    InterfaceBase.pbui_MeetNotifyMsgForDouble object1 = (InterfaceBase.pbui_MeetNotifyMsgForDouble) message.getObject();
                    Log.e("MyLog", "MeetingFileFragment.getEventMessage 126行:  EventBus会议目录ID --->>> " + object1.getId());
                    nativeUtil.queryMeetDirFile(object1.getId());
                }
                break;
            case IDEventMessage.MEDIA_PLAY_INFORM://251 媒体播放通知
                InterfacePlaymedia.pbui_Type_MeetMediaPlay object = (InterfacePlaymedia.pbui_Type_MeetMediaPlay) message.getObject();
                int res = object.getRes();
                int triggerid = object.getTriggerid();
                int triggeruserval = object.getTriggeruserval();
                int createdeviceid = object.getCreatedeviceid();
                int mediaid = object.getMediaid();
                Log.e("MyLog", "MeetingFileFragment.getEventMessage:  object.getMediaid() --->>> " + mediaid + " createdeviceid： " + createdeviceid
                        + "  triggeruserval " + triggeruserval + "  triggerid : " + triggerid + "  res: " + res);
                break;
            case IDEventMessage.STOP_PLAY:
                //248.停止资源操作
//                nativeUtil.stopResourceOperate(0, devID);
                break;
            case IDEventMessage.MEMBER_CHANGE_INFORM://90 参会人员变更通知
                InterfaceBase.pbui_MeetNotifyMsg object2 = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                /** ************ ******  91.查询指定ID的参会人  ****** ************ **/
                nativeUtil.queryAttendPeopleFromId(object2.getId());
                break;
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_MEET_DIR://136.查询会议目录
                MyUtils.handTo(IDivMessage.QUERY_MEET_DIR, (InterfaceFile.pbui_Type_MeetDirDetailInfo) result, "queryMeetDir", mHandler);
                break;
            case IDivMessage.QUERY_MEET_DIR_FILE://查询会议目录文件
                MyUtils.handTo(IDivMessage.QUERY_MEET_DIR_FILE, (InterfaceFile.pbui_Type_MeetDirFileDetailInfo) result, "queryMeetDirFile", mHandler);
                break;
            case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                MyUtils.handTo(IDivMessage.QUERY_DEVICE_INFO, (InterfaceDevice.pbui_Type_DeviceDetailInfo) result, "devInfos", mHandler);
                break;
            case IDivMessage.QUERY_DEVMEET_INFO://110.查询设备会议信息
                MyUtils.handTo(IDivMessage.QUERY_DEVMEET_INFO, (InterfaceDevice.pbui_Type_DeviceFaceShowDetail) result, "devMeetInfos", mHandler);
                break;
            case IDivMessage.RECEIVE_MEET_IMINFO: //收到会议消息
                Log.e("MyLog", "SigninFragment.callListener 296行:  收到会议消息 --->>> ");
                MyUtils.receiveMessage((InterfaceIM.pbui_Type_MeetIM) result, nativeUtil);
                break;
            case IDivMessage.QUERY_ATTEND_BYID://查询指定ID的参会人
                MyUtils.queryName((InterfaceMember.pbui_Type_MemberDetailInfo) result);
                break;
        }
    }


    private void queryMeetDirU(Message msg) {
        mDirData.clear();
        ArrayList queryMeetDir = msg.getData().getParcelableArrayList("queryMeetDir");
        InterfaceFile.pbui_Type_MeetDirDetailInfo o = (InterfaceFile.pbui_Type_MeetDirDetailInfo) queryMeetDir.get(0);
        for (int i = 0; i < o.getItemCount(); i++) {
            InterfaceFile.pbui_Item_MeetDirDetailInfo item = o.getItem(i);
            String dirName = new String(item.getName().toByteArray());
            int id = item.getId();
            int parentid = item.getParentid();
            Log.e("MyLog", "MeetingFileFragment.handleMessage 91行:  文件目录名称 --->>> " + dirName);
            //过滤掉共享文件和批注文件
            if (!dirName.equals("共享文件") && !dirName.equals("批注文件")) {
                mDirData.add(new MeetingFileTypeBean(id, parentid, dirName, ""));
                Log.e("MyLog", "MeetingFileFragment.handleMessage:  mDirData添加的目录：  --->>> 文件名：" + dirName + "  目录ID ：" + id + " 父级ID： " + parentid);
            }
        }
        mDirAdapter.notifyDataSetChanged();
        //当第一次进入会议资料界面时，就展示第一个目录item中的文档类信息
        Log.e("MyLog", "MeetingFileFragment.initData:  size --->>> " + mDirData.size());
        if (mDirData.size() > 0 && isFirstIn) {
            Log.e("MyLog", "MeetingFileFragment.initData:  第一次进入了 --->>> ");
            try {
                mDirId = mDirData.get(0).getDirId();
                //查询会议目录文件
                nativeUtil.queryMeetDirFile(mDirId);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        //设置默认点击第一个按钮
//        rightmeetfile_document.performClick();
    }

    private void queryMeetDirFileU(Message msg) {
        ArrayList queryMeetDirFile = msg.getData().getParcelableArrayList("queryMeetDirFile");
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo o2 = (InterfaceFile.pbui_Type_MeetDirFileDetailInfo) queryMeetDirFile.get(0);
        if (meetDirFileInfos == null) {
            meetDirFileInfos = new ArrayList<>();
        }
        meetDirFileInfos.clear();
        meetDirFileInfos = Dispose.MeetDirFile(o2);
        //每次点击目录的item时，就展示该目录下的文档类文件
        if (meetDirFileInfos != null) {
            mFileData.clear();
            for (int i = 0; i < meetDirFileInfos.size(); i++) {
                MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                String fileName = documentBean.getFileName();
                if (FileUtil.isDocumentFile(fileName)) {//文档类
                    mFileData.add(documentBean);
                }
            }
            dataAdapter.notifyDataSetChanged();
            dataAdapter.PAGE_NOW = 0;
            checkButton();
            setBtnSelect(0);
            isFirstIn = false;
        }
    }


    private void initData() {
        mDirAdapter = new MeetingFileTypeAdapter(getActivity(), mDirData);
        dir_lv.setAdapter(mDirAdapter);
        //当点击其它Fragment时，nativeUtil对象就已经被改变了
//        final MeetingFileFragment listener = this;
        dir_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("MyLog", "MeetingFileFragment.onItemClick:  item 点击 --->>> " + i);
//                nativeUtil = NativeUtil.getInstance();
                //从新设置的nativeUtil后还要从新设置回调
//                nativeUtil.setCallListener(listener);
                mDirAdapter.setCheck(i);
                int dirId = mDirData.get(i).getDirId();
                meetDirFileInfos.clear();
                mFileData.clear();
                dataAdapter.notifyDataSetChanged();
                try {
                    Log.e("MyLog", "MeetingFileFragment.onItemClick:  从item中传递过去的目录ID --->>> " + dirId);
                    nativeUtil.queryMeetDirFile(dirId);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
        //将导入文件按钮隐藏
        rightmeetfile_import.setVisibility(View.GONE);
        dataAdapter = new TypeFileAdapter(getContext(), mFileData);
        rightmeetfile_lv.setAdapter(dataAdapter);
        //打开文件
        dataAdapter.setLookListener(new TypeFileAdapter.setLookListener() {
            @Override
            public void onLookListener(final int mediaId, final String filename, long filesize) {
                mMediaid = mediaId;
                // TODO: 2018/1/29 如果是视屏文件可在线观看
                if (FileUtil.isVideoFile(filename)) {
                    startActivity(new Intent(getActivity(), SDLActivity.class));
                    //媒体播放操作
                    List<Integer> devIds = new ArrayList<Integer>();
                    devIds.add(MeetingActivity.getDevId());
                    nativeUtil.mediaPlayOperate(mediaId, devIds, 0);
                } else {
                    MyUtils.openFile(Macro.MEETMATERIAL, filename, getView(), nativeUtil, mediaId, getContext(), filesize);
                }
            }
        });
        //下载文件
        dataAdapter.setDownListener(new TypeFileAdapter.setDownListener() {
            @Override
            public void onDownListener(int mediaId, String filename, long filesize) {
                MyUtils.downLoadFile(Macro.MEETMATERIAL, filename, getView(), getContext(), mediaId, nativeUtil, filesize);
            }
        });
    }

    private void initView(View inflate) {
        dir_lv = (ListView) inflate.findViewById(R.id.type_lv);
        rightmeetfile_document = (Button) inflate.findViewById(R.id.rightmeetfile_document);
        rightmeetfile_picture = (Button) inflate.findViewById(R.id.rightmeetfile_picture);
        rightmeetfile_video = (Button) inflate.findViewById(R.id.rightmeetfile_video);
        rightmeetfile_other = (Button) inflate.findViewById(R.id.rightmeetfile_other);
        rightmeetfile_lv = (ListView) inflate.findViewById(R.id.rightmeetfile_lv);
        rightmeetfile_prepage = (Button) inflate.findViewById(R.id.rightmeetfile_prepage);
        rightmeetfile_nextpage = (Button) inflate.findViewById(R.id.rightmeetfile_nextpage);
        rightmeetfile_import = (Button) inflate.findViewById(R.id.rightmeetfile_import);
        rightmeetfile_document.setOnClickListener(this);
        rightmeetfile_picture.setOnClickListener(this);
        rightmeetfile_video.setOnClickListener(this);
        rightmeetfile_other.setOnClickListener(this);
        rightmeetfile_prepage.setOnClickListener(this);
        rightmeetfile_nextpage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rightmeetfile_document:
                if (meetDirFileInfos != null) {
                    mFileData.clear();
                    for (int i = 0; i < meetDirFileInfos.size(); i++) {
                        MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                        String fileName = documentBean.getFileName();
                        boolean documentFile = FileUtil.isDocumentFile(fileName);
                        if (documentFile) {
                            Log.e("MyLog", "MeetingFileFragment.onClick:  其中的文档类文件： --->>> " + fileName);
                            mFileData.add(documentBean);
                        }
                    }
                }
                dataAdapter.notifyDataSetChanged();
                dataAdapter.PAGE_NOW = 0;
                checkButton();
                setBtnSelect(0);
                break;
            case R.id.rightmeetfile_picture:
                if (meetDirFileInfos != null) {
                    mFileData.clear();
                    for (int i = 0; i < meetDirFileInfos.size(); i++) {
                        MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                        String fileName = documentBean.getFileName();
                        if (FileUtil.isPictureFile(fileName)) {
                            Log.e("MyLog", "MeetingFileFragment.onClick:  其中的图片类文件 --->>> " + fileName);
                            mFileData.add(documentBean);
                        }
                    }
                }
                dataAdapter.notifyDataSetChanged();
                dataAdapter.PAGE_NOW = 0;
                checkButton();
                setBtnSelect(1);
                break;
            case R.id.rightmeetfile_video:
                if (meetDirFileInfos != null) {
                    mFileData.clear();
                    for (int i = 0; i < meetDirFileInfos.size(); i++) {
                        MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                        String fileName = documentBean.getFileName();
                        //过滤视频文件
                        if (FileUtil.isVideoFile(fileName)) {
                            Log.e("MyLog", "MeetingFileFragment.onClick:  其中的视频类文件 --->>> " + fileName);
                            mFileData.add(documentBean);
                        }
                    }
                }
                dataAdapter.notifyDataSetChanged();
                dataAdapter.PAGE_NOW = 0;
                checkButton();
                setBtnSelect(2);
                break;
            case R.id.rightmeetfile_other:
                if (meetDirFileInfos != null) {
                    mFileData.clear();
                    for (int i = 0; i < meetDirFileInfos.size(); i++) {
                        MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                        String fileName = documentBean.getFileName();
                        //过滤视频文件
                        if (FileUtil.isOtherFile(fileName)) {
                            Log.e("MyLog", "MeetingFileFragment.onClick:  其它类文件 --->>> " + fileName);
                            mFileData.add(documentBean);
                        }
                    }
                }
                dataAdapter.notifyDataSetChanged();
                dataAdapter.PAGE_NOW = 0;
                checkButton();
                setBtnSelect(3);
                break;
            case R.id.rightmeetfile_prepage:
                prePage();
                break;
            case R.id.rightmeetfile_nextpage:
                nextPage();
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

    private void initBtns() {
        mBtns = new ArrayList<>();
        mBtns.add(rightmeetfile_document);
        mBtns.add(rightmeetfile_picture);
        mBtns.add(rightmeetfile_video);
        mBtns.add(rightmeetfile_other);
    }

    private void setBtnSelect(int index) {
        for (int i = 0; i < mBtns.size(); i++) {
            if (i == index) {
                mBtns.get(i).setSelected(true);
                mBtns.get(i).setTextColor(Color.WHITE);
            } else {
                mBtns.get(i).setSelected(false);
                mBtns.get(i).setTextColor(Color.BLACK);
            }
        }
    }

    //下一页
    private void nextPage() {
        dataAdapter.PAGE_NOW++;
        dataAdapter.notifyDataSetChanged();
        checkButton();
    }

    //上一页
    private void prePage() {
        dataAdapter.PAGE_NOW--;
        dataAdapter.notifyDataSetChanged();
        checkButton();
    }

    //设置两个按钮是否可用
    public void checkButton() {
        //如果页码已经是第一页了
        if (dataAdapter.PAGE_NOW <= 0) {
            rightmeetfile_prepage.setEnabled(false);
            //如果不设置的话，只要进入一次else if ，那么下一页按钮就一直是false，不可点击状态
            rightmeetfile_nextpage.setEnabled(true);
        }
        //值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
        else if (mFileData.size() - dataAdapter.PAGE_NOW * dataAdapter.ITEM_COUNT <= dataAdapter.ITEM_COUNT) {
            rightmeetfile_nextpage.setEnabled(false);
            rightmeetfile_prepage.setEnabled(true);
        } else {
            //否则两个按钮都设为可用
            rightmeetfile_prepage.setEnabled(true);
            rightmeetfile_nextpage.setEnabled(true);
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            initController();
            try {
                Log.e("MyLog", "MeetingFileFragment.onHiddenChanged 462行:  不隐藏状态 --->>> ");
                //136.查询会议目录
                nativeUtil.queryMeetDir();
                nativeUtil.queryMeetDirFile(mDirId);
                //设置默认点击第一个按钮
                rightmeetfile_document.performClick();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }
}
