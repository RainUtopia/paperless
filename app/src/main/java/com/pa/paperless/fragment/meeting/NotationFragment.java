package com.pa.paperless.fragment.meeting;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.pa.paperless.R;
import com.pa.paperless.adapter.TypeFileAdapter;
import com.pa.paperless.bean.MeetDirFileInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.service.NativeService.nativeUtil;


/**
 * Created by Administrator on 2017/10/31.
 * 查看批注
 */

public class NotationFragment extends BaseFragment implements View.OnClickListener {

    private ListView mNotaLv;
    private Button mNotaPrepage;
    private Button mNotaNextpage;
    private TypeFileAdapter mAdapter;
    private Button mDocument;
    private Button mPicture;
    private List<Button> mBtns;
    private final String TAG = "NotationFragment -->>";
    //所有批注文件
    private List<MeetDirFileInfo> meetDirFileInfos = new ArrayList<>();
    //用于临时存放不同类型的文件
    private List<MeetDirFileInfo> mData = new ArrayList<>();
    public static boolean notationfragment_isshowing = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_notation, container, false);
        initView(inflate);
        notationfragment_isshowing = true;
        mBtns = new ArrayList<>();
        mBtns.add(mDocument);
        mBtns.add(mPicture);
        try {
            nativeUtil.queryMeetDir();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.meet_dir://收到会议目录信息
                receiveMeetDir(message);
                break;
            case IDEventF.meet_dir_file://收到会议目录文件
                receiveMeetDirFile(message);
                break;
            case IDEventMessage.MEETDIR_FILE_CHANGE_INFORM://会议目录文件变更通知
                if (!isHidden()) {
                    InterfaceBase.pbui_MeetNotifyMsgForDouble object = (InterfaceBase.pbui_MeetNotifyMsgForDouble) message.getObject();
                    Log.e(TAG, "NotationFragment.getEventMessage :  查询会议目录文件的目录ID --->>> " + object.getId());
                    nativeUtil.queryMeetDirFile(object.getId());//批注文件的目录ID  固定为 2
                }
                break;
        }
    }

    private void receiveMeetDirFile(EventMessage message) {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo object = (InterfaceFile.pbui_Type_MeetDirFileDetailInfo) message.getObject();
        Log.e(TAG, "NotationFragment.receiveMeetDirFile :  object.getDirid() --> " + object.getDirid());
        if (object.getDirid() == 2) {//批注文件
            meetDirFileInfos = Dispose.MeetDirFile(object);
            if (meetDirFileInfos != null) {
                if (mAdapter == null) {
                    mAdapter = new TypeFileAdapter(getContext(), mData);
                    mNotaLv.setAdapter(mAdapter);
                }
                mData.clear();
                for (int i = 0; i < meetDirFileInfos.size(); i++) {
                    MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                    mData.add(documentBean);
                }
                mAdapter.notifyDataSetChanged();
                mAdapter.PAGE_NOW = 0;
                checkButton();
                setSelect(0);
            }
        }
    }

    private void receiveMeetDir(EventMessage message) {
        InterfaceFile.pbui_Type_MeetDirDetailInfo object = (InterfaceFile.pbui_Type_MeetDirDetailInfo) message.getObject();
        List<InterfaceFile.pbui_Item_MeetDirDetailInfo> itemList = object.getItemList();
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceFile.pbui_Item_MeetDirDetailInfo info = itemList.get(i);
            String dirName = MyUtils.getBts(info.getName());
            Log.e(TAG, "NotationFragment.receiveMeetDir :  目录名称 --> " + dirName + " 目录ID：" + info.getId());
            if (dirName.equals("批注文件")) {
                try {
                    Log.e(TAG, "NotationFragment.receiveMeetDir :  批注文件的目录ID： --> " + info.getId());
                    //查询会议目录文件
                    nativeUtil.queryMeetDirFile(info.getId());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.i("F_life", "NotationFragment.onDestroy :   --> ");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
                MyUtils.openFile(Macro.POSTILFILE, filename, nativeUtil, posion, getContext(), filesize);
//                OpenFile(Macro.POSTILFILE, posion, filename, filesize);
            }
        });
        mAdapter.setDownListener(new TypeFileAdapter.setDownListener() {
            @Override
            public void onDownListener(int posion, String filename, long filesize) {
//                downLoadFile(Macro.POSTILFILE, posion, filename, filesize);
                MyUtils.downLoadFile(Macro.POSTILFILE, filename, getContext(), posion, nativeUtil, filesize);
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
            if (mData.size() > mAdapter.ITEM_COUNT) {
                mNotaNextpage.setEnabled(true);
            } else {
                mNotaNextpage.setEnabled(false);
            }
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
    public void onHiddenChanged(boolean hidden) {
        Log.i("F_life", "NotationFragment.onHiddenChanged :   --->>> " + hidden);
        notationfragment_isshowing = !hidden;
        if (!hidden) {
            try {
                Log.e("MyLog", "NotationFragment.onHiddenChanged 341行:  不隐藏状态 --->>> ");
                //136.查询会议目录文件（直接查询 批注文件(id 是固定为 2 )的文件）
                nativeUtil.queryMeetDir();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.i("F_life", "NotationFragment.onAttach :   --> ");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "NotationFragment.onCreate :   --> ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "NotationFragment.onActivityCreated :   --> ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i("F_life", "NotationFragment.onStart :   --> ");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i("F_life", "NotationFragment.onResume :   --> ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("F_life", "NotationFragment.onPause :   --> ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("F_life", "NotationFragment.onStop :   --> ");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("F_life", "NotationFragment.onDestroyView :   --> ");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        Log.i("F_life", "NotationFragment.onDetach :   --> ");
        super.onDetach();
    }
}
