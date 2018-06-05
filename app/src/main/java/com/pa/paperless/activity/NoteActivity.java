package com.pa.paperless.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.utils.Export;
import com.pa.paperless.utils.MyUtils;

import java.io.File;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "NoteActivity-->";
    private TextView empty;
    private EditText edt_note;
    private Button note_import;
    private Button note_save;
    private Button note_back;
    private String mNoteCentent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        initView();
        initEvent();
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
                MyUtils.showTxtDialog(NoteActivity.this, MyUtils.getSDPostfixFile(".txt"), edt_note);
                break;
            case R.id.note_save://保存
                String s1 = edt_note.getText().toString();
                Export.ToNoteText(s1, "会议笔记", Macro.MEETFILE);
                break;
            case R.id.note_back://返回
                String s2 = edt_note.getText().toString();
                Export.ToNoteText(s2, "会议笔记存档", getCacheDir().getAbsolutePath()+"/");
                Log.e(TAG, "NoteActivity.onClick :   --> "+getCacheDir().getAbsolutePath());
                finish();
                break;
        }
    }
}
