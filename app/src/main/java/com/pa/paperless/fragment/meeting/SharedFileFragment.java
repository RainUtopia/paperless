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
import android.os.Handler;
import android.os.Message;
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
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.adapter.ChooseDirDialogAdapter;
import com.pa.paperless.adapter.TypeFileAdapter;
import com.pa.paperless.bean.MeetDirBean;
import com.pa.paperless.bean.MeetDirFileInfo;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventBadge;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.MyUtils;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.libsdl.app.SDLActivity;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.mBadge;
import static com.pa.paperless.activity.MeetingActivity.mReceiveMsg;

/**
 * Created by Administrator on 2017/10/31.
 * 共享资料
 */

public class SharedFileFragment extends BaseFragment implements View.OnClickListener, CallListener {

    private NativeUtil nativeUtil;
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

    private List<MeetDirFileInfo> mData = new ArrayList<>();
    private List<MeetDirFileInfo> meetDirFileInfos = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_MEET_DIR://查询会议目录
                    ArrayList queryMeetDir = msg.getData().getParcelableArrayList("queryMeetDir");
                    InterfaceMain.pbui_Type_MeetDirDetailInfo o = (InterfaceMain.pbui_Type_MeetDirDetailInfo) queryMeetDir.get(0);
                    meetDirInfos.clear();
                    for (int i = 0; i < o.getItemCount(); i++) {
                        InterfaceMain.pbui_Item_MeetDirDetailInfo item = o.getItem(i);
                        String dirName = new String(item.getName().toByteArray());
                        int id = item.getId();
                        int parentid = item.getParentid();
                        // 过滤掉共享文件和批注文件
                        if (!dirName.equals("共享文件") && !dirName.equals("批注文件")) {
                            Log.e("MyLog", "SharedFileFragment.handleMessage 102行:  136.查询会议目录  --->>> 文件名：" + dirName + "  目录ID ：" + id + " 父级ID： " + parentid);
                            meetDirInfos.add(new MeetDirBean(dirName, id, parentid));
                        }
                    }
                    break;
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
    //会议目录的信息
    private List<MeetDirBean> meetDirInfos = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_sharedfile, container, false);
        initController();
        initView(inflate);
        initBtns();
        setBtnSelect(0);
        try {
            //136.查询会议目录
            nativeUtil.queryMeetDir();
            //143.查询会议目录文件（直接查询 共享资料(id 是固定为 1 )的文件）
            nativeUtil.queryMeetDirFile(1);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.MEETDIR_FILE_CHANGE_INFORM://142 会议目录文件变更通知
                InterfaceMain.pbui_MeetNotifyMsgForDouble object = (InterfaceMain.pbui_MeetNotifyMsgForDouble) message.getObject();
                nativeUtil.queryMeetDirFile(object.getId());
                break;
            case IDEventMessage.DOWN_FINISH://67 下载进度回调
                Toast.makeText(getActivity(), "  下载完成！ ", Toast.LENGTH_SHORT).show();
                break;
            case IDEventMessage.MEETDIR_CHANGE_INFORM://135 会议目录变更通知
                nativeUtil.queryMeetDir();
                break;
            case IDEventMessage.Upload_Progress://73 上传进度通知
                InterfaceMain.pbui_TypeUploadPosCb object1 = (InterfaceMain.pbui_TypeUploadPosCb) message.getObject();
                Log.e("MyLog", "SharedFileFragment.getEventMessage 174行:  当前进度 --->>> " + object1.getPer());
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
                if (FileUtil.isVideoFile(filename)) {
                    //如果是音频或视频则在线播放
                    startActivity(new Intent(getActivity(), SDLActivity.class));
					List<Integer> devIds = new ArrayList<Integer>();
                    devIds.add(MeetingActivity.getDevId());
                    nativeUtil.mediaPlayOperate(posion, devIds, 0);
                } else {
                    MyUtils.openFile(filename, getView(), nativeUtil, posion, getContext());
                }
            }
        });
        mAllAdapter.setDownListener(new TypeFileAdapter.setDownListener() {
            @Override
            public void onDownListener(int posion, String filename) {
                MyUtils.downLoadFile(filename, getView(), getContext(), posion, nativeUtil);
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }
    String path;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
                Log.e("MyLog", "SharedFileFragment.onActivityResult 316行:  选中文件的path路径 --->>> " + path);
                Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
                showDialog(path);
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(getContext(), uri);
                Log.e("MyLog", "SharedFileFragment.onActivityResult 322行:  选中文件的path路径 --->>> " + path);
                Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                Log.e("MyLog", "SharedFileFragment.onActivityResult 326行:  选中文件的path路径 --->>> " + path);
                Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
            }
            showDialog(path);
        }
    }

    /**
     * 上传文件
     *
     * @param path
     */
    // TODO: 2018/3/2 上传的图片文件会在其他类别中展示待解决。。。
    public void showDialog(final String path) {
        final EditText editText = new EditText(getContext());
        //获取选择的文件名
        String fileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        //给输入框设置默认文件名
        editText.setText(fileName);
        Log.e("MyLog", "SharedFileFragment.showDialog 378行:   --->>> " + fileName);
        new AlertDialog.Builder(getContext()).setTitle("请输入文件名")
                .setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!(TextUtils.isEmpty(editText.getText().toString().trim()))) {
                    String newName = editText.getText().toString();
                    //展示选择上传到哪个目录
                    showChooseDir(newName, path);
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
        }).show();
    }

    private int getMediaid(String path) {
        //其它
        if (FileUtil.isDocumentFile(path) || FileUtil.isOtherFile(path)) {
            return Macro.MEDIA_FILETYPE_OTHER | Macro.MEDIA_FILETYPE_OTHERSUB;
        }
        if (FileUtil.isDocumentFile(path) || FileUtil.isOtherFile(path)) {
            return Macro.MEDIA_FILETYPE_RECORD | Macro.MEDIA_FILETYPE_OTHERSUB;
        }
        if (FileUtil.isDocumentFile(path) || FileUtil.isOtherFile(path)) {
            return Macro.MEDIA_FILETYPE_UPDATE | Macro.MEDIA_FILETYPE_OTHERSUB;
        }
        if (FileUtil.isDocumentFile(path) || FileUtil.isOtherFile(path)) {
            return Macro.MEDIA_FILETYPE_TEMP | Macro.MEDIA_FILETYPE_OTHERSUB;
        }
        //
        if (FileUtil.isDocumentFile(path) || FileUtil.isOtherFile(path)) {
            return Macro.MAINTYPEBITMASK | Macro.SUBTYPEBITMASK;
        }
        //音频
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_AUDIO | Macro.MEDIA_FILETYPE_PCM;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_AUDIO | Macro.MEDIA_FILETYPE_MP3;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_AUDIO | Macro.MEDIA_FILETYPE_ADPCM;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_AUDIO | Macro.MEDIA_FILETYPE_FLAC;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_AUDIO | Macro.MEDIA_FILETYPE_MP4;
        }
        //视屏
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_VIDEO | Macro.MEDIA_FILETYPE_MKV;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_VIDEO | Macro.MEDIA_FILETYPE_RMVB;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_VIDEO | Macro.MEDIA_FILETYPE_AVI;
        }
        if (FileUtil.isVideoFile(path)) {
            return Macro.MEDIA_FILETYPE_VIDEO | Macro.MEDIA_FILETYPE_RM;
        }
        //图片
        if (FileUtil.isPictureFile(path)) {
            return Macro.MEDIA_FILETYPE_PICTURE | Macro.MEDIA_FILETYPE_BMP;
        }
        if (FileUtil.isPictureFile(path)) {
            return Macro.MEDIA_FILETYPE_PICTURE | Macro.MEDIA_FILETYPE_JPEG;
        }
        if (FileUtil.isPictureFile(path)) {
            return Macro.MEDIA_FILETYPE_PICTURE | Macro.MEDIA_FILETYPE_PNG;
        }

        return 0;
    }

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
            case IDivMessage.RECEIVE_MEET_IMINFO: //收到会议消息
                Log.e("MyLog", "SigninFragment.callListener 296行:  收到会议消息 --->>> ");
                InterfaceMain2.pbui_Type_MeetIM receiveMsg = (InterfaceMain2.pbui_Type_MeetIM) result;
                //获取之前的未读消息个数
                int badgeNumber1 = mBadge.getBadgeNumber();
                Log.e("MyLog", "SigninFragment.callListener 307行:  原来的个数 --->>> " + badgeNumber1);
                int all =  badgeNumber1 + 1;
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
