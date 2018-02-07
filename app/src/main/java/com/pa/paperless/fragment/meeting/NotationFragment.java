package com.pa.paperless.fragment.meeting;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.adapter.TypeFileAdapter;
import com.pa.paperless.bean.MeetDirFileInfo;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.SDCardUtils;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 * 查看批注
 */

public class NotationFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private ListView mNotaLv;
    private Button mNotaPrepage;
    private Button mNotaNextpage;
    private TypeFileAdapter mAdapter;
    private Button mDocument;
    private Button mPicture;
    private List<Button> mBtns;
//    private NativeUtil nativeUtil;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_MEET_DIR_FILE:
                    ArrayList queryMeetDirFile = msg.getData().getParcelableArrayList("queryMeetDirFile");
                    InterfaceMain.pbui_Type_MeetDirFileDetailInfo o = (InterfaceMain.pbui_Type_MeetDirFileDetailInfo) queryMeetDirFile.get(0);
                    meetDirFileInfos = Dispose.MeetDirFile(o);
                    if (meetDirFileInfos != null) {
                        mData.clear();
                        for (int i = 0; i < meetDirFileInfos.size(); i++) {
                            MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                            String fileName = documentBean.getFileName();
                            if (FileUtil.isDocumentFile(fileName)) {
                                mData.add(documentBean);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        mAdapter.PAGE_NOW = 0;
                        checkButton();
                        setSelect(0);
                    }
                    mDocument.performClick();
                    break;
            }

        }
    };
    private List<MeetDirFileInfo> meetDirFileInfos = new ArrayList<>();
    private List<MeetDirFileInfo> mData = new ArrayList<>();

    //当前的页码 默认第0页
    //public static int PAGE_NOW = 0;
    //设置每一页的item个数 可以随意设置
    //public static int ITEM_COUNT = 6;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_notation, container, false);
        initView(inflate);
        initController();
        mBtns = new ArrayList<>();
        mBtns.add(mDocument);
        mBtns.add(mPicture);
        try {
            nativeUtil.queryMeetDir();
            //136.查询会议目录文件（直接查询 批注文件(id 是固定为 2 )的文件）
            nativeUtil.queryMeetDirFile(2);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()){
            case IDEventMessage.MEETDIR_FILE_CHANGE_INFORM:
                InterfaceMain.pbui_MeetNotifyMsgForDouble object = (InterfaceMain.pbui_MeetNotifyMsgForDouble) message.getObject();
                nativeUtil.queryMeetDirFile(object.getId());
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

    private void setSelect(int index) {
        for (int i = 0; i < mBtns.size(); i++) {
            if (index == i) {
                mBtns.get(i).setSelected(true);
                mBtns.get(i).setTextColor(Color.WHITE);
            } else {
                mBtns.get(i).setSelected(false);
                mBtns.get(i).setTextColor(Color.BLACK);
            }
        }
    }

    private void initView(View inflate) {
        mNotaLv = (ListView) inflate.findViewById(R.id.nota_lv);
        mAdapter = new TypeFileAdapter(getContext(), mData);
        mNotaLv.setAdapter(mAdapter);
        mAdapter.setLookListener(new TypeFileAdapter.setLookListener() {
            @Override
            public void onLookListener(int posion, String filename) {
                OpenFile(posion, filename);
            }
        });
        mAdapter.setDownListener(new TypeFileAdapter.setDownListener() {
            @Override
            public void onDownListener(int posion, String filename) {
                downLoadFile(posion, filename);
            }
        });
        mNotaPrepage = (Button) inflate.findViewById(R.id.nota_prepage);
        mNotaNextpage = (Button) inflate.findViewById(R.id.nota_nextpage);

        mNotaPrepage.setOnClickListener(this);
        mNotaNextpage.setOnClickListener(this);
        mDocument = (Button) inflate.findViewById(R.id.document);
        mDocument.setOnClickListener(this);
        mPicture = (Button) inflate.findViewById(R.id.picture);
        mPicture.setOnClickListener(this);
    }

    /**
     * 打开文件操作
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

    /**
     * 下载文件操作
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nota_prepage:
                prePage();
                break;
            case R.id.nota_nextpage:
                nextPage();
                break;
            case R.id.document:
                if (meetDirFileInfos != null) {
                    mData.clear();
                    for (int i = 0; i < meetDirFileInfos.size(); i++) {
                        MeetDirFileInfo meetDirFileInfo = meetDirFileInfos.get(i);
                        if (FileUtil.isDocumentFile(meetDirFileInfo.getFileName())) {
                            mData.add(meetDirFileInfo);
                        }
                    }
                }
                mAdapter.PAGE_NOW = 0;
                setSelect(0);
                checkButton();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.picture:
                if (meetDirFileInfos != null) {
                    mData.clear();
                    for (int i = 0; i < meetDirFileInfos.size(); i++) {
                        MeetDirFileInfo meetDirFileInfo = meetDirFileInfos.get(i);
                        if (FileUtil.isPictureFile(meetDirFileInfo.getFileName())) {
                            mData.add(meetDirFileInfo);
                        }
                    }
                }
                setSelect(1);
                mAdapter.PAGE_NOW = 0;
                checkButton();
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    //下一页
    private void nextPage() {
        mAdapter.PAGE_NOW++;
        mAdapter.notifyDataSetChanged();
        checkButton();
    }

    //上一页
    private void prePage() {
        mAdapter.PAGE_NOW--;
        mAdapter.notifyDataSetChanged();
        checkButton();
    }

    //设置两个按钮是否可用
    public void checkButton() {
        //如果页码已经是第一页了
        if (mAdapter.PAGE_NOW <= 0) {
            mNotaPrepage.setEnabled(false);
            //如果不设置的话，只要进入一次else if ，那么下一页按钮就一直是false，不可点击状态
            mNotaNextpage.setEnabled(true);
        }
        //值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
        else if (mData.size() - mAdapter.PAGE_NOW * mAdapter.ITEM_COUNT <= mAdapter.ITEM_COUNT) {
            mNotaNextpage.setEnabled(false);
            mNotaPrepage.setEnabled(true);
        } else {
            //否则两个按钮都设为可用
            mNotaPrepage.setEnabled(true);
            mNotaNextpage.setEnabled(true);
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_MEET_DIR_FILE:
                InterfaceMain.pbui_Type_MeetDirFileDetailInfo result1 = (InterfaceMain.pbui_Type_MeetDirFileDetailInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryMeetDirFile", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.RECEIVE_MEET_IMINFO:
                Log.e("MyLog","NotationFragment.callListener:  收到会议消息 --->>> ");
                InterfaceMain2.pbui_Type_MeetIM receiveMsg = (InterfaceMain2.pbui_Type_MeetIM) result;
                if (receiveMsg != null) {
                    List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(receiveMsg);
                    if (mReceiveMsg == null) {
                        mReceiveMsg = new ArrayList<>();
                    }
                    mReceiveMsg.add(receiveMeetIMInfos.get(0));
                }
                break;
        }
    }
}
