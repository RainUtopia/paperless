package com.pa.paperless.fragment.meeting;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.adapter.TypeFileAdapter;
import com.pa.paperless.bean.MeetDirFileInfo;
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
 * 共享资料
 */

public class SharedFileFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private Button btn_document;
    private Button btn_picture;
    private Button btn_video;
    private Button btn_other;
    private ListView sharedfile_lv;
    private Button share_prepage;
    private Button share_nextpage;
    private Button share_import;
    private List<Button> mBtns;
    private TypeFileAdapter mAllAdapter;
    private PopupWindow mPopupWindow;
//    private NativeUtil nativeUtil;

    private List<MeetDirFileInfo> mData = new ArrayList<>();
    private List<MeetDirFileInfo> meetDirFileInfos = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_MEET_DIR_FILE://查询会议目录文件
                    ArrayList queryMeetDirFile = msg.getData().getParcelableArrayList("queryMeetDirFile");
                    InterfaceMain.pbui_Type_MeetDirFileDetailInfo o2 = (InterfaceMain.pbui_Type_MeetDirFileDetailInfo) queryMeetDirFile.get(0);
                    meetDirFileInfos = Dispose.MeetDirFile(o2);
                    if (meetDirFileInfos != null) {
                        mData.clear();
                        for (int i = 0; i < meetDirFileInfos.size(); i++) {
                            MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                            String fileName = documentBean.getFileName();
                            if (FileUtil.isDocumentFile(fileName)) {
                                mData.add(documentBean);
                            }
                        }
                        mAllAdapter.notifyDataSetChanged();
                        mAllAdapter.PAGE_NOW = 0;
                        checkButton();
                        setBtnSelect(0);
                    }
                    btn_document.performClick();
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_sharedfile, container, false);
        initController();
        initView(inflate);
        initBtns();
        setBtnSelect(0);
        try {
            nativeUtil.queryMeetDir();
            //136.查询会议目录文件（直接查询 共享资料(id 是固定为 1 )的文件）
            nativeUtil.queryMeetDirFile(1);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()){
            case IDEventMessage.MEETDIR_FILE_CHANGE_INFORM://142 会议目录文件变更通知
                InterfaceMain.pbui_MeetNotifyMsgForDouble object = (InterfaceMain.pbui_MeetNotifyMsgForDouble) message.getObject();
                nativeUtil.queryMeetDirFile(object.getId());
                break;
            case IDEventMessage.DOWN_FINISH://67 下载进度回调
                Toast.makeText(getActivity(), "  下载完成！ ", Toast.LENGTH_SHORT).show();
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
//        nativeUtil = new NativeUtil();
        nativeUtil.setCallListener(this);
    }

    private void initBtns() {
        mBtns = new ArrayList<>();
        mBtns.add(btn_document);
        mBtns.add(btn_picture);
        mBtns.add(btn_video);
        mBtns.add(btn_other);
    }


    private void initView(View inflate) {
        btn_document = (Button) inflate.findViewById(R.id.rightsharefile_document);
        btn_picture = (Button) inflate.findViewById(R.id.rightsharefile_picture);
        btn_video = (Button) inflate.findViewById(R.id.rightsharefile_video);
        btn_other = (Button) inflate.findViewById(R.id.rightsharefile_other);
        sharedfile_lv = (ListView) inflate.findViewById(R.id.rightsharefile_lv);

        share_prepage = (Button) inflate.findViewById(R.id.rightsharefile_prepage);
        share_nextpage = (Button) inflate.findViewById(R.id.rightsharefile_nextpage);
        share_import = (Button) inflate.findViewById(R.id.rightsharefile_import);

        mAllAdapter = new TypeFileAdapter(getContext(), mData);
        sharedfile_lv.setAdapter(mAllAdapter);
        mAllAdapter.setLookListener(new TypeFileAdapter.setLookListener() {
            @Override
            public void onLookListener(int posion, String filename) {
                OpenFile(posion, filename);
            }
        });
        mAllAdapter.setDownListener(new TypeFileAdapter.setDownListener() {
            @Override
            public void onDownListener(int posion, String filename) {
                downLoadFile(posion, filename);
            }
        });

        btn_document.setOnClickListener(this);
        btn_picture.setOnClickListener(this);
        btn_video.setOnClickListener(this);
        btn_other.setOnClickListener(this);
        share_prepage.setOnClickListener(this);
        share_nextpage.setOnClickListener(this);
        share_import.setOnClickListener(this);
    }
    private void OpenFile(final int posion, final String filename) {
        //点击查看文件 如果手机上没有就得先下载下来
        if (SDCardUtils.isSDCardEnable()) {
            String file = SDCardUtils.getSDCardPath();
            file += filename;
            File file1 = new File(file);
            if (!file1.exists()) {
                final String finalFile = file;
                Snackbar.make(getView(), " 文件不存在，是否先下载？ ", Snackbar.LENGTH_LONG)
                        .setAction(" 下载 ", new View.OnClickListener() {
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
    private void downLoadFile(int posion, String filename) {
        if (SDCardUtils.isSDCardEnable()) {
            String sdCardPath = SDCardUtils.getSDCardPath();
            sdCardPath += filename;
            final File file = new File(sdCardPath);
            if (file.exists()) {
                Snackbar.make(getView(), "  文件已经存在是否直接打开？  ", Snackbar.LENGTH_LONG)
                        .setAction("打开", new View.OnClickListener() {
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
    //设置选中状态
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rightsharefile_document:
                if (meetDirFileInfos != null) {
                    mData.clear();
                    for (int i = 0; i < meetDirFileInfos.size(); i++) {
                        MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                        String fileName = documentBean.getFileName();
                        if (FileUtil.isDocumentFile(fileName)) {
                            mData.add(documentBean);
                        }
                    }
                }
                mAllAdapter.notifyDataSetChanged();
                mAllAdapter.PAGE_NOW = 0;
                checkButton();
                setBtnSelect(0);
                break;
            case R.id.rightsharefile_picture:
                if (meetDirFileInfos != null) {
                    mData.clear();
                    for (int i = 0; i < meetDirFileInfos.size(); i++) {
                        MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                        String fileName = documentBean.getFileName();
                        if (FileUtil.isPictureFile(fileName)) {
                            mData.add(documentBean);
                        }
                    }
                }
                mAllAdapter.PAGE_NOW = 0;
                checkButton();
                mAllAdapter.notifyDataSetChanged();
                setBtnSelect(1);
                break;
            case R.id.rightsharefile_video:
                if (meetDirFileInfos != null) {
                    mData.clear();
                    for (int i = 0; i < meetDirFileInfos.size(); i++) {
                        MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                        String fileName = documentBean.getFileName();
                        if (FileUtil.isVideoFile(fileName)) {
                            mData.add(documentBean);
                        }
                    }
                }
                mAllAdapter.PAGE_NOW = 0;
                checkButton();
                mAllAdapter.notifyDataSetChanged();
                setBtnSelect(2);
                break;
            case R.id.rightsharefile_other:
                if (meetDirFileInfos != null) {
                    mData.clear();
                    for (int i = 0; i < meetDirFileInfos.size(); i++) {
                        MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                        String fileName = documentBean.getFileName();
                        if (FileUtil.isOtherFile(fileName)) {
                            mData.add(documentBean);
                        }
                    }
                }
                mAllAdapter.PAGE_NOW = 0;
                checkButton();
                mAllAdapter.notifyDataSetChanged();
                setBtnSelect(3);
                break;
            case R.id.rightsharefile_prepage://上一页
                prePage();
                break;
            case R.id.rightsharefile_nextpage://下一页
                nextPage();
                break;
            case R.id.rightsharefile_import://导入文件
                showPop();
                break;
        }
    }

    private void showPop() {
        View popupView = getLayoutInflater(new Bundle()).inflate(R.layout.pop_import_file, null);
        mPopupWindow = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(getActivity().findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    //下一页
    private void nextPage() {
        mAllAdapter.PAGE_NOW++;
        mAllAdapter.notifyDataSetChanged();
        checkButton();
    }

    //上一页
    private void prePage() {
        mAllAdapter.PAGE_NOW--;
        mAllAdapter.notifyDataSetChanged();
        checkButton();
    }

    //设置两个按钮是否可用
    public void checkButton() {
        //如果页码已经是第一页了
        if (mAllAdapter.PAGE_NOW <= 0) {
            share_prepage.setEnabled(false);
            //如果不设置的话，只要进入一次else if ，那么下一页按钮就一直是false，不可点击状态
            share_nextpage.setEnabled(true);
        }
        //值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
        else if (mData.size() - mAllAdapter.PAGE_NOW * mAllAdapter.ITEM_COUNT <= mAllAdapter.ITEM_COUNT) {
            share_nextpage.setEnabled(false);
            share_prepage.setEnabled(true);
        } else {
            //否则两个按钮都设为可用
            share_prepage.setEnabled(true);
            share_nextpage.setEnabled(true);
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
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
        }
    }
}
