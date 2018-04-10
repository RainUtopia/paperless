package com.pa.paperless.fragment.meeting;

import android.content.Intent;
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
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.pa.paperless.R;
import com.pa.paperless.activity.PostilActivity;
import com.pa.paperless.adapter.TypeFileAdapter;
import com.pa.paperless.bean.MeetDirFileInfo;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventBadge;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.MyUtils;
import com.pa.paperless.utils.SDCardUtils;
import com.wind.myapplication.NativeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.mBadge;
import static com.pa.paperless.activity.MeetingActivity.mReceiveMsg;

/**
 * Created by Administrator on 2017/10/31.
 * 查看批注
 */

public class NotationFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private NativeUtil nativeUtil;
    private ListView mNotaLv;
    private Button mNotaPrepage;
    private Button mNotaNextpage;
    private TypeFileAdapter mAdapter;
    private Button mDocument;
    private Button mPicture;
    private List<Button> mBtns;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_MEET_DIR_FILE:
                    ArrayList queryMeetDirFile = msg.getData().getParcelableArrayList("queryMeetDirFile");
                    InterfaceFile.pbui_Type_MeetDirFileDetailInfo o = (InterfaceFile.pbui_Type_MeetDirFileDetailInfo) queryMeetDirFile.get(0);
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
        switch (message.getAction()) {
            case IDEventMessage.MEETDIR_FILE_CHANGE_INFORM:
                InterfaceBase.pbui_MeetNotifyMsgForDouble object = (InterfaceBase.pbui_MeetNotifyMsgForDouble) message.getObject();
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
            public void onLookListener(int posion, String filename, long filesize) {
                OpenFile(Macro.POSTILFILE, posion, filename, filesize);
            }
        });
        mAdapter.setDownListener(new TypeFileAdapter.setDownListener() {
            @Override
            public void onDownListener(int posion, String filename, long filesize) {
                downLoadFile(Macro.POSTILFILE, posion, filename, filesize);
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
     *
     * @param posion
     * @param filename
     */
    private void OpenFile(String dir, final int posion, final String filename, long filesize) {
        //点击查看文件 如果手机上没有就得先下载下来
        if (SDCardUtils.isSDCardEnable()) {
            MyUtils.CreateFile(dir);
            dir += filename;
            File file1 = new File(dir);
            if (!file1.exists()) {//文件不存在
                final String finalFile = dir;
                hintDownload(posion, finalFile, "文件不存在，是否先下载？");
            } else if (file1.length() != filesize) {//文件大小与服务器中不同
                file1.delete();//目前采用删除再下载
                final String finalFile = dir;
                hintDownload(posion, finalFile, "本地文件与当前文件不符，是否重新下载？");
            } else {
                if (FileUtil.isPictureFile(filename)) {
                    //已经存在才打开文件
                    Intent intent = new Intent(getActivity().getBaseContext(), PostilActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("filepath", dir);
                    intent.putExtra("postil", bundle);
                    startActivity(intent);
                } else {
                    FileUtil.openFile(getContext(), file1);
                }
            }
        }
    }

    private void hintDownload(final int posion, final String finalFile, String showMeg) {
        Snackbar.make(getView(), showMeg, Snackbar.LENGTH_LONG)
                .setAction(" 下载 ", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nativeUtil.creationFileDownload(finalFile, posion, 0, 0);
                    }
                }).show();
    }

    /**
     * 下载文件操作
     *
     * @param posion
     * @param filename
     */
    private void downLoadFile(String dir, int posion, String filename, long filesize) {
        if (SDCardUtils.isSDCardEnable()) {
            MyUtils.CreateFile(dir);
            dir += filename;
            final File file = new File(dir);
            if (file.exists() && file.length() == filesize) {
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
                Log.e("MyLog", "MeetingFileFragment.onLookListener: 下载操作： 文件的绝对路径： --->>> " + dir);
                nativeUtil.creationFileDownload(dir, posion, 0, 0);
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
                InterfaceFile.pbui_Type_MeetDirFileDetailInfo result1 = (InterfaceFile.pbui_Type_MeetDirFileDetailInfo) result;
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
            case IDivMessage.RECEIVE_MEET_IMINFO: //收到会议消息
                Log.e("MyLog", "SigninFragment.callListener 296行:  收到会议消息 --->>> ");
                InterfaceIM.pbui_Type_MeetIM receiveMsg = (InterfaceIM.pbui_Type_MeetIM) result;
                //获取之前的未读消息个数
                int badgeNumber1 = mBadge.getBadgeNumber();
                Log.e("MyLog", "SigninFragment.callListener 307行:  原来的个数 --->>> " + badgeNumber1);
                int all = badgeNumber1 + 1;
                if (receiveMsg != null) {
                    List<ReceiveMeetIMInfo> receiveMeetIMInfos = Dispose.ReceiveMeetIMinfo(receiveMsg);
                    if (mReceiveMsg == null) {
                        mReceiveMsg = new ArrayList<>();
                    }
                    receiveMeetIMInfos.get(0).setType(true);
                    mReceiveMsg.add(receiveMeetIMInfos.get(0));
                    Log.e("MyLog", "SigninFragment.callListener: 收到的信息个数：  --->>> " + mReceiveMsg.size());
                }
                List<EventBadge> num = new ArrayList<>();
                num.add(new EventBadge(all));
                // TODO: 2018/3/7 通知界面更新
                Log.e("MyLog", "SigninFragment.callListener 319行:  传递过去的个数 --->>> " + all);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.UpDate_BadgeNumber, num));
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            nativeUtil = NativeUtil.getInstance();
            nativeUtil.setCallListener(this);
        }
    }
}
