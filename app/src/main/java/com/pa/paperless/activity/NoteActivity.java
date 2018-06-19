package com.pa.paperless.activity;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pa.paperless.R;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.utils.Export;
import com.pa.paperless.utils.MyUtils;
import com.pa.paperless.utils.SDCardUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "NoteActivity-->";
    private TextView empty;
    private EditText edt_note;
    private Button note_import;
    private Button note_save;
    private Button note_back;
    private String mNoteCentent;
    private ProgressBar load_pb;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        resources = getResources();
        initView();
        initEvent();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.is_loading:
                boolean isload = (boolean) message.getObject();
                if (isload && load_pb.getVisibility() == View.GONE) {
                    Log.e(TAG, "NoteActivity.getEventMessage :  loading...  ");
                    load_pb.setVisibility(View.VISIBLE);
                } else if (!isload && load_pb.getVisibility() == View.VISIBLE) {
                    Log.e(TAG, "NoteActivity.getEventMessage :  finish!  ");
                    load_pb.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initEvent() {
        //获取到之前写得会议笔记存档
        File file = new File(getCacheDir().getAbsolutePath() + "/会议笔记存档.txt");
        if (file.exists()) {
            mNoteCentent = Export.readText(file);
            edt_note.setText(mNoteCentent);
        }
        //将光标移动到末尾
        edt_note.setSelection(edt_note.getText().toString().length());
    }

    private void initView() {
        empty = (TextView) findViewById(R.id.empty);
        edt_note = (EditText) findViewById(R.id.edt_note);
        note_import = (Button) findViewById(R.id.note_import);
        note_save = (Button) findViewById(R.id.note_save);
        note_back = (Button) findViewById(R.id.note_back);
        load_pb = (ProgressBar) findViewById(R.id.load_pb);
        load_pb.setVisibility(View.GONE);
        empty.setOnClickListener(this);
        note_import.setOnClickListener(this);
        note_save.setOnClickListener(this);
        note_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empty://清空
                edt_note.setText("");
                break;
            case R.id.note_import://导入
                List<File> files = new ArrayList<>();
                List<File> fs = MyUtils.GetFiles(SDCardUtils.getSDCardPath(), ".txt", true, files);
                MyUtils.showTxtDialog(NoteActivity.this, fs, edt_note);
                break;
            case R.id.note_save://保存
                String s1 = edt_note.getText().toString();
                if (!s1.trim().equals("")) {
                    showEdtPop(s1, Macro.MEETFILE);
                }
                break;
            case R.id.note_back://返回
                String s2 = edt_note.getText().toString();
                Export.ToNoteText(s2, "会议笔记存档", getCacheDir().getAbsolutePath() + "/");
                Log.e(TAG, "NoteActivity.onClick :   --> " + getCacheDir().getAbsolutePath());
                finish();
                break;
        }
    }

    //展示输入文件名弹出框
    private void showEdtPop(final String content, final String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText edt = new EditText(this);
        builder.setTitle(resources.getString(R.string.please_enter_file_name));
        edt.setHint(resources.getString(R.string.please_enter_file_name));
        edt.setText(System.currentTimeMillis() + "");
        edt.setSelection(edt.getText().toString().length());
        builder.setView(edt);
        builder.setPositiveButton(resources.getString(R.string.ensure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = edt.getText().toString().trim();
                if (s.equals("")) {
                    Toast.makeText(NoteActivity.this, resources.getString(R.string.please_enter_file_name), Toast.LENGTH_SHORT).show();
                } else {
                    Export.ToNoteText(content, s + ".txt", path);
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(resources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
