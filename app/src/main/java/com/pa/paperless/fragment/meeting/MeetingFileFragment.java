package com.pa.paperless.fragment.meeting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.adapter.MeetingFileTypeAdapter;
import com.pa.paperless.adapter.TypeFileAdapter;
import com.pa.paperless.bean.MeetDirFileInfo;
import com.pa.paperless.bean.MeetingFileTypeBean;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.SDCardUtils;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 * 会议资料
 * 进入页面：1.查询文件目录 2.根据目录ID查找文件 3.先展示出第一个item 的文档类的数据
 */

public class MeetingFileFragment extends BaseFragment implements View.OnClickListener, CallListener {


    private ListView dir_lv;
    private Button rightmeetfile_document;
    private Button rightmeetfile_picture;
    private Button rightmeetfile_video;
    private Button rightmeetfile_other;
    private ListView rightmeetfile_lv;
    private Button rightmeetfile_prepage;
    private Button rightmeetfile_nextpage;
    private Button rightmeetfile_import;
    private List<MeetingFileTypeBean> mDirData = new ArrayList<>();
    private MeetingFileTypeAdapter mDirAdapter;
    private List<Button> mBtns;
    private List<MeetDirFileInfo> mFileData = new ArrayList<>();
    private TypeFileAdapter dataAdapter;
    private NativeUtil nativeUtil;
    private MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_MEET_DIR://查询会议目录
                    mDirData.clear();
                    ArrayList queryMeetDir = msg.getData().getParcelableArrayList("queryMeetDir");
                    InterfaceMain.pbui_Type_MeetDirDetailInfo o = (InterfaceMain.pbui_Type_MeetDirDetailInfo) queryMeetDir.get(0);
                    for (int i = 0; i < o.getItemCount(); i++) {
                        InterfaceMain.pbui_Item_MeetDirDetailInfo item = o.getItem(i);
                        String dirName = new String(item.getName().toByteArray());
                        int id = item.getId();
                        int parentid = item.getParentid();
                        //过滤掉共享文件和批注文件
                        if (!dirName.equals("共享文件") && !dirName.equals("批注文件")) {
                            mDirData.add(new MeetingFileTypeBean(id, parentid, dirName, ""));
                            Log.e("MyLog", "MeetingFileFragment.handleMessage:  136.查询会议目录  --->>> 文件名：" + dirName + "  目录ID ：" + id + " 父级ID： " + parentid);
                        }
                    }
                    mDirAdapter.notifyDataSetChanged();
                    //当第一次进入会议资料界面时，就展示第一个目录item中的文档类信息
                    Log.e("MyLog", "MeetingFileFragment.initData:  size --->>> " + mDirData.size());
                    if (mDirData.size() > 0 && isFirstIn) {
                        Log.e("MyLog", "MeetingFileFragment.initData:  第一次进入了 --->>> ");
                        try {
                            nativeUtil.queryMeetDirFile(mDirData.get(0).getDirId());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                    //设置默认点击第一个按钮
                    rightmeetfile_document.performClick();

                    break;
                case IDivMessage.QUERY_MEET_DIR_FILE://查询会议目录文件
                    ArrayList queryMeetDirFile = msg.getData().getParcelableArrayList("queryMeetDirFile");
                    InterfaceMain.pbui_Type_MeetDirFileDetailInfo o2 = (InterfaceMain.pbui_Type_MeetDirFileDetailInfo) queryMeetDirFile.get(0);
                    meetDirFileInfos = Dispose.MeetDirFile(o2);
                    //每次点击目录的item时，就展示该目录下的文档类文件
                    if (meetDirFileInfos != null) {
                        mFileData.clear();
                        for (int i = 0; i < meetDirFileInfos.size(); i++) {
                            MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                            String fileName = documentBean.getFileName();
                            if (FileUtil.isDocumentFile(fileName)) {
                                mFileData.add(documentBean);
                            }
                        }
                        dataAdapter.notifyDataSetChanged();
                        dataAdapter.PAGE_NOW = 0;
                        checkButton();
                        setBtnSelect(0);
                    }
                    break;
                case IDivMessage.QUERY_DEVMEET_INFO:
                    ArrayList devMeetInfos = msg.getData().getParcelableArrayList("devMeetInfos");
                    InterfaceMain.pbui_Type_DeviceFaceShowDetail o3 = (InterfaceMain.pbui_Type_DeviceFaceShowDetail) devMeetInfos.get(0);
                    int deviceid = o3.getDeviceid();

                    break;
            }
        }
    };
    private List<MeetDirFileInfo> meetDirFileInfos = new ArrayList<>();
    private int devID;
    private ImageView div_line;
    private LinearLayout rightmeetfile_type3;
    private PercentLinearLayout right_meetingfilelayout;
    private PopupWindow mediaPop;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isFirstIn = true;
        Log.e("FramentLife", "MeetingFileFragment.onAttach:   --->>> ");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e("FramentLife", "MeetingFileFragment.onCreateView:   --->>> ");
        View inflate = inflater.inflate(R.layout.right_mettingfile, container, false);
        initController();
        initView(inflate);
        initData();
        initBtns();
        try {
            //136.查询会议目录
            nativeUtil.queryMeetDir();
//            nativeUtil.placeDeviceRankingInfo()

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        //获得传递过来的ID
        devID = MeetingActivity.getTitles();

        EventBus.getDefault().register(this);
        return inflate;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.DOWN_FINISH://67 下载进度回调
                Toast.makeText(getActivity(), "  下载完成！ ", Toast.LENGTH_SHORT).show();
                break;
            case IDEventMessage.MEETDIR_CHANGE_INFORM://135 会议目录变更通知
                nativeUtil.queryMeetDir();
                break;
            case IDEventMessage.MEETDIR_FILE_CHANGE_INFORM://142 会议目录文件变更通知
                InterfaceMain.pbui_MeetNotifyMsgForDouble object1 = (InterfaceMain.pbui_MeetNotifyMsgForDouble) message.getObject();
                nativeUtil.queryMeetDirFile(object1.getId());
                break;
            case IDEventMessage.MEDIA_PLAY_INFORM://251 媒体播放通知
                InterfaceMain2.pbui_Type_MeetMediaPlay object = (InterfaceMain2.pbui_Type_MeetMediaPlay) message.getObject();
                int res = object.getRes();
                int triggerid = object.getTriggerid();
                int triggeruserval = object.getTriggeruserval();
                int createdeviceid = object.getCreatedeviceid();
                int mediaid = object.getMediaid();
                Log.e("MyLog", "MeetingFileFragment.getEventMessage:  object.getMediaid() --->>> " + mediaid + " createdeviceid： " + createdeviceid
                        + "  triggeruserval " + triggeruserval + "  triggerid : " + triggerid + "  res: " + res);
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

    private static boolean isFirstIn = true;

    private void initData() {
        mDirAdapter = new MeetingFileTypeAdapter(getActivity(), mDirData);
        dir_lv.setAdapter(mDirAdapter);
        //当点击其它Fragment时，nativeUtil对象就已经被改变了
        final MeetingFileFragment listener = this;
        dir_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("MyLog", "MeetingFileFragment.onItemClick:  item 点击 --->>> " + i);
                nativeUtil = NativeUtil.getInstance();
                //从新设置的nativeUtil后还要从新设置回调
                nativeUtil.setCallListener(listener);
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
                isFirstIn = false;
            }
        });
        //将导入文件按钮隐藏
        rightmeetfile_import.setVisibility(View.GONE);
        dataAdapter = new TypeFileAdapter(getContext(), mFileData);
        rightmeetfile_lv.setAdapter(dataAdapter);
        //打开文件
        dataAdapter.setLookListener(new TypeFileAdapter.setLookListener() {
            @Override
            public void onLookListener(final int mediaId, final String filename) {
                // TODO: 2018/1/29 如果是视屏文件可在线观看
                if (FileUtil.isVideoFile(filename)) {
//                    View inflate = LayoutInflater.from(getContext()).inflate(R.layout.media_pop, null);
//                     //right_meetingfilelayout  rightmeetfile_type3
//                    PopupWindow popupWindow = PopWindowUtil.getInstance().makePopupWindow(getContext(), right_meetingfilelayout, inflate, Color.RED).showLocationWithAnimation(
//                            getContext(), inflate, 10, 10, R.style.AnimHorizontal);
                    showPlayMedia();
                    nativeUtil.mediaPlayOperate(mediaId, devID, 0);

                } else {
                    OpenFile(mediaId, filename);
                }
            }
        });
        //下载文件
        dataAdapter.setDownListener(new TypeFileAdapter.setDownListener() {
            @Override
            public void onDownListener(int mediaId, String filename) {
                downLoadFile(mediaId, filename);
            }
        });
    }

    /**
     * 展示播放视屏 popupWindow
     * @return
     */
    private View showPlayMedia() {
        mediaPlayer = new MediaPlayer();
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.media_pop, null);
        mediaPop = new PopupWindow(inflate, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        mediaPop.setAnimationStyle(R.style.AnimHorizontal);
        mediaPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mediaPop.setTouchable(true);
        mediaPop.setOutsideTouchable(true);
        SurfaceView sv = inflate.findViewById(R.id.sv);
        Button stop_play = inflate.findViewById(R.id.stop_play);
        stop_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //248.停止资源操作
                nativeUtil.stopResourceOperate(0, devID);
                mediaPop.dismiss();
            }
        });
        mediaPop.showAtLocation(getActivity().findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
        return inflate;
    }

    /**
     * 打开文件
     * @param posion
     * @param filename
     */
    private void OpenFile(final int posion, final String filename) {
        //点击查看文件 如果手机上没有就得先下载下来
        if (SDCardUtils.isSDCardEnable()) {
            String file = SDCardUtils.getSDCardPath();
            file += filename;
            File file1 = new File(file);
            if (!file1.exists()) {
                final String finalFile = file;
                Snackbar.make(getView(), " 文件不存在，是否先下载？ ", Snackbar.LENGTH_LONG)
                        .setAction("立即下载", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                nativeUtil.creationFileDownload(finalFile, posion, 0, 0);
                            }
                        }).show();
            } else {
                //已经存在才打开文件
                FileUtil.openFile(getContext(), file1);
            }
        }
    }

    /**
     * 下载文件
     * @param posion
     * @param filename
     */
    private void downLoadFile(int posion, String filename) {
        if (SDCardUtils.isSDCardEnable()) {
            String sdCardPath = SDCardUtils.getSDCardPath();
            sdCardPath += filename;
            final File file = new File(sdCardPath);
            if (file.exists()) {
                Snackbar.make(getView(), "  文件已经存在是否直接打开？  ", Snackbar.LENGTH_LONG)
                        .setAction("打开查看", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // TODO: 2018/1/27 查看文件
                                Log.e("MyLog", "MeetingFileFragment.onClick:  查看文件操作 --->>> ");
                                FileUtil.openFile(getContext(), file);
                            }
                        }).show();
            } else {
                Log.e("MyLog", "MeetingFileFragment.onLookListener: 下载操作： 文件的绝对路径： --->>> " + sdCardPath);
                nativeUtil.creationFileDownload(sdCardPath, posion, 0, 0);
            }
        }
    }

    private void initView(View inflate) {
        dir_lv = (ListView) inflate.findViewById(R.id.type_lv);
        rightmeetfile_document = (Button) inflate.findViewById(R.id.rightmeetfile_document);
        right_meetingfilelayout = (PercentLinearLayout) inflate.findViewById(R.id.right_meetingfilelayout);
        rightmeetfile_picture = (Button) inflate.findViewById(R.id.rightmeetfile_picture);
        rightmeetfile_video = (Button) inflate.findViewById(R.id.rightmeetfile_video);
        div_line = (ImageView) inflate.findViewById(R.id.right_div_line);
        rightmeetfile_other = (Button) inflate.findViewById(R.id.rightmeetfile_other);
        rightmeetfile_lv = (ListView) inflate.findViewById(R.id.rightmeetfile_lv);
        rightmeetfile_prepage = (Button) inflate.findViewById(R.id.rightmeetfile_prepage);
        rightmeetfile_nextpage = (Button) inflate.findViewById(R.id.rightmeetfile_nextpage);
        rightmeetfile_import = (Button) inflate.findViewById(R.id.rightmeetfile_import);
        rightmeetfile_type3 = (LinearLayout) inflate.findViewById(R.id.rightmeetfile_type3);

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
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_MEET_DIR://136.查询会议目录
                InterfaceMain.pbui_Type_MeetDirDetailInfo result1 = (InterfaceMain.pbui_Type_MeetDirDetailInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryMeetDir", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_MEET_DIR_FILE:
                InterfaceMain.pbui_Type_MeetDirFileDetailInfo result3 = (InterfaceMain.pbui_Type_MeetDirFileDetailInfo) result;
                if (result3 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result3);
                    bundle.putParcelableArrayList("queryMeetDirFile", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                InterfaceMain.pbui_Type_DeviceDetailInfo devInfos = (InterfaceMain.pbui_Type_DeviceDetailInfo) result;
                if (devInfos != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(devInfos);
                    bundle.putParcelableArrayList("devInfos", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_DEVMEET_INFO://110.查询设备会议信息
                InterfaceMain.pbui_Type_DeviceFaceShowDetail devMeetInfos = (InterfaceMain.pbui_Type_DeviceFaceShowDetail) result;
                if (devMeetInfos != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(devMeetInfos);
                    bundle.putParcelableArrayList("devMeetInfos", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }
}
