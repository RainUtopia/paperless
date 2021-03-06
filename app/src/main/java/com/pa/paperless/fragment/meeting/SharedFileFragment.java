package com.pa.paperless.fragment.meeting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.pa.paperless.R;
import com.pa.paperless.adapter.ChooseDirDialogAdapter;
import com.pa.paperless.adapter.ShareFileAdapter;
import com.pa.paperless.bean.MeetDirBean;
import com.pa.paperless.bean.MeetDirFileInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.service.NativeService;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.MyUtils;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.service.NativeService.nativeUtil;
import static com.pa.paperless.utils.MyUtils.getMediaid;


/**
 * Created by Administrator on 2017/10/31.
 * 共享资料
 */

public class SharedFileFragment extends BaseFragment implements View.OnClickListener {

    private Button btn_document;
    private Button btn_picture;
    private Button btn_video;
    private Button btn_other;
    private ListView sharedfile_lv;
    private Button share_prepage;
    private Button share_nextpage;
    private Button share_import;
    private List<Button> mBtns;
    private ShareFileAdapter mAllAdapter;
    public static boolean sharefile_isshowing = false;

    private List<MeetDirFileInfo> mData;//用于临时存放不同类型的文件
    private List<MeetDirFileInfo> meetDirFileInfos;
    //会议目录的信息
    private List<MeetDirBean> meetDirInfos = new ArrayList<>();

    private final String TAG = "SharedFileFragment-->";
    private boolean isLocalOperate;//控制是否是当前操作查询权限

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i("F_life", "SharedFileFragment.onCreateView :   --->>> ");
        View inflate = inflater.inflate(R.layout.right_sharedfile, container, false);
        initView(inflate);
        sharefile_isshowing = true;
        initBtns();
        setBtnSelect(0);
        try {
            //143.查询会议目录文件（直接查询 共享资料(id 是固定为 1 )的文件）
            nativeUtil.queryMeetDirFile(1);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return inflate;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.meet_dir://收到会议目录
//                receiveMeetDir(message);
                break;
            case IDEventF.meet_dir_file://收到会议目录文件信息
                receiveMeetDirFile(message);
                break;
            case IDEventF.permission_list://获得本人的权限
                List<Integer> localPermissions = (List<Integer>) message.getObject();
                if (localPermissions.contains(3) && isLocalOperate) {
                    Log.e(TAG, "SharedFileFragment.getEventMessage :  本人拥有上传权限 --> ");
                    isLocalOperate = false;
                    showPop();
                }
                break;
        }
    }

    private void receiveMeetDirFile(EventMessage message) {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo object = (InterfaceFile.pbui_Type_MeetDirFileDetailInfo) message.getObject();
        if (object.getDirid() == 1) {//共享文件
            Log.d("file_get", "SharedFileFragment.receiveMeetDirFile :   --> " + object.getDirid());
            meetDirFileInfos = Dispose.MeetDirFile(object);
            if (meetDirFileInfos != null) {
                if (mData == null) {
                    mData = new ArrayList<>();
                } else {
                    mData.clear();
                }
                for (int i = 0; i < meetDirFileInfos.size(); i++) {
                    MeetDirFileInfo documentBean = meetDirFileInfos.get(i);
                    mData.add(documentBean);
                }
                if (mAllAdapter == null) {
                    mAllAdapter = new ShareFileAdapter(getContext(), mData);
                    sharedfile_lv.setAdapter(mAllAdapter);
                }
                mAllAdapter.notifyDataSetChanged();
                mAllAdapter.PAGE_NOW = 0;
                checkButton();
                setBtnSelect(0);

                mAllAdapter.setLookListener(new ShareFileAdapter.setLookListener() {
                    @Override
                    public void onLookListener(int posion, String filename, long filesize) {
                        if (FileUtil.isVideoFile(filename)) {
                            //如果是音频或视频则在线播放
                            List<Integer> devIds = new ArrayList<Integer>();
//                    devIds.add(MeetingActivity.getDevId());
                            devIds.add(NativeService.localDevId);
                            nativeUtil.mediaPlayOperate(posion, devIds, 0);
                        } else {
                            MyUtils.openFile(Macro.SHAREMATERIAL, filename, nativeUtil, posion, getContext(), filesize);
                        }
                    }
                });
                mAllAdapter.setDownListener(new ShareFileAdapter.setDownListener() {
                    @Override
                    public void onDownListener(int posion, String filename, long filesize) {
                        MyUtils.downLoadFile(Macro.SHAREMATERIAL, filename, getContext(), posion, nativeUtil, filesize);
                    }
                });
            }
        }
    }

    private void receiveMeetDir(EventMessage message) {
        InterfaceFile.pbui_Type_MeetDirDetailInfo object = (InterfaceFile.pbui_Type_MeetDirDetailInfo) message.getObject();
        List<InterfaceFile.pbui_Item_MeetDirDetailInfo> itemList = object.getItemList();
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceFile.pbui_Item_MeetDirDetailInfo info = itemList.get(i);
            String dirName = MyUtils.getBts(info.getName());
            if (dirName.equals("共享文件") && info.getId() == 1) {
                try {
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
        Log.i("F_life", "SharedFileFragment.onDestroy :   --> ");
        super.onDestroy();
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

        btn_document.setOnClickListener(this);
        btn_picture.setOnClickListener(this);
        btn_video.setOnClickListener(this);
        btn_other.setOnClickListener(this);
        share_prepage.setOnClickListener(this);
        share_nextpage.setOnClickListener(this);
        share_import.setOnClickListener(this);
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
                        Log.d("files", "SharedFileFragment.onClick : 共享中所有文件  --->>> " + fileName);
                        if (FileUtil.isDocumentFile(fileName)) {
                            Log.i("files", "SharedFileFragment.onClick : 共享中文档文件  --->>> " + fileName);
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
                        Log.d("files", "SharedFileFragment.onClick : 共享中所有文件 --->>> " + fileName);
                        if (FileUtil.isPictureFile(fileName)) {
                            Log.i("files", "SharedFileFragment.onClick : 共享中图片文件  --->>> " + fileName);
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
                        Log.d("files", "SharedFileFragment.onClick :  共享中所有文件 --->>> " + fileName);
                        if (FileUtil.isVideoFile(fileName)) {
                            Log.i("files", "SharedFileFragment.onClick :  共享中视频文件 --->>> " + fileName);
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
                        Log.d("files", "SharedFileFragment.onClick :  共享中所有文件 --->>> " + fileName);
                        if (FileUtil.isOtherFile(fileName)) {
                            Log.i("files", "SharedFileFragment.onClick :  共享中其它文件 --->>> " + fileName);
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
                isLocalOperate = true;
                try {
                    nativeUtil.queryAttendPeoplePermissions();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void showPop() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    String path;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//super方法要开启,因为录屏在基类中做的处理
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
                Log.e("MyLog", "com.pa.paperless.fragment.meeting_SharedFileFragment.onActivityResult :  路径 --->>> " + path);
//                Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
                showDialog(path);
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(getContext(), uri);
//                Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
//                Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
            }
            Log.e("MyLog", "SharedFileFragment.onActivityResult 370行:  选中文件的path路径 --->>> " + path);
            showDialog(path);
        }
    }

    /**
     * 上传文件
     *
     * @param path
     */
    public void showDialog(final String path) {
        final EditText editText = new EditText(getContext());
        //获取选择的文件名
        String fileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        //给输入框设置默认文件名
        editText.setText(fileName);
        Log.e("MyLog", "SharedFileFragment.showDialog 378行:   --->>> " + fileName);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("请输入文件名")
                .setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!(TextUtils.isEmpty(editText.getText().toString().trim()))) {
                    String newName = editText.getText().toString();
                    //展示选择上传到哪个目录
//                    showChooseDir(newName, path);
                    int mediaid = getMediaid(path);
                    String fileEnd = path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase();
                    try {
                        nativeUtil.uploadFile(InterfaceMacro.Pb_Upload_Flag.Pb_MEET_UPLOADFLAG_ONLYENDCALLBACK.getNumber(),
                                1, 0, newName + "." + fileEnd, path, 0, mediaid);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                    dialogInterface.dismiss();
                } else {
                    Toast.makeText(getContext(), "请输入有效文件名", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 选择上传到哪个目录
     *
     * @param newName
     * @param path
     */
    public void showChooseDir(final String newName, final String path) {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.pop_filedir, null);
        RecyclerView fileDirRl = popupView.findViewById(R.id.fileDir_rl);
        fileDirRl.setLayoutManager(new LinearLayoutManager(getContext()));
        ChooseDirDialogAdapter adapter = new ChooseDirDialogAdapter(getContext(), meetDirInfos);
        fileDirRl.setAdapter(adapter);
        final PopupWindow mDirPopView = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(mDirPopView);
        mDirPopView.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mDirPopView.setTouchable(true);
        mDirPopView.setOutsideTouchable(true);

        adapter.setItemListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                TextView viewById = view.findViewById(R.id.dir_btn);
                MyUtils.setAnimator(viewById);
                int dirId = meetDirInfos.get(posion).getDirId();
                Log.e("MyLog", "SharedFileFragment.onItemClick 478行:  获得的目录ID --->>> " + dirId);
                //计算出 媒体ID
                int mediaid = getMediaid(path);
                String fileEnd = path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase();
                Log.e("MyLog", "SharedFileFragment.onClick 375行:  获得的媒体ID --->>> " + mediaid);
                /** ************ ******  69.上传文件  ****** ************ **/
                try {
                    nativeUtil.uploadFile(InterfaceMacro.Pb_Upload_Flag.Pb_MEET_UPLOADFLAG_ONLYENDCALLBACK.getNumber(),
                            dirId, 0, newName + "." + fileEnd, path, 0, mediaid);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                mDirPopView.dismiss();
            }
        });
        mDirPopView.showAtLocation(getActivity().findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
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
            if (mData.size() > mAllAdapter.ITEM_COUNT) {
                share_nextpage.setEnabled(true);
            } else {
                share_nextpage.setEnabled(false);
            }
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
    public void onAttach(Context context) {
        Log.i("F_life", "SharedFileFragment.onAttach :   --> ");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "SharedFileFragment.onCreate :   --> ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "SharedFileFragment.onActivityCreated :   --> ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i("F_life", "SharedFileFragment.onStart :   --> ");
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i("F_life", "SharedFileFragment.onResume :   --> ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("F_life", "SharedFileFragment.onPause :   --> ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("F_life", "SharedFileFragment.onStop :   --> ");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("F_life", "SharedFileFragment.onDestroyView :   --> ");
        super.onDestroyView();
    }


    @Override
    public void onDetach() {
        Log.i("F_life", "SharedFileFragment.onDetach :   --> ");
        super.onDetach();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        sharefile_isshowing = !hidden;
        Log.e(TAG, "SharedFileFragment.onHiddenChanged :  是否隐藏 --> " + hidden);
        if (!hidden) {
            try {
                //143.查询会议目录文件（直接查询 共享资料(id 是固定为 1 )的文件）
                nativeUtil.queryMeetDirFile(1);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }
}
