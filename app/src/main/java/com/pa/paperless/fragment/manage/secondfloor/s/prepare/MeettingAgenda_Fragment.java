package com.pa.paperless.fragment.manage.secondfloor.s.prepare;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pa.paperless.R;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.pa.paperless.utils.MyUtils;
import com.pa.paperless.utils.SDCardUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/17.
 * 会前准备-会议议程
 */

public class MeettingAgenda_Fragment extends BaseFragment implements View.OnClickListener {
    private EditText mMeetAgendaEdt;
    private Button mAgendaFrom;
    private Button mAgendaExport;
    private EditText mMeetNoticeEdt;
    private Button mNoticeFrom;
    private Button mNoticeExport;
    private File[] files;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.meeting_agenda, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mMeetAgendaEdt = (EditText) inflate.findViewById(R.id.meet_agenda_edt);
        mAgendaFrom = (Button) inflate.findViewById(R.id.agenda_from);
        mAgendaExport = (Button) inflate.findViewById(R.id.agenda_export);

        mMeetNoticeEdt = (EditText) inflate.findViewById(R.id.meet_notice_edt);
        mNoticeFrom = (Button) inflate.findViewById(R.id.notice_from);
        mNoticeExport = (Button) inflate.findViewById(R.id.notice_export);

        mAgendaFrom.setOnClickListener(this);
        mAgendaExport.setOnClickListener(this);
        mNoticeFrom.setOnClickListener(this);
        mNoticeExport.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agenda_from:
                List<File> files = new ArrayList<>();
                List<File> fs = MyUtils.GetFiles(SDCardUtils.getSDCardPath(), ".txt", true, files);
                MyUtils.showTxtDialog(getContext(),fs, mMeetAgendaEdt);
                break;
            case R.id.notice_from:
                List<File> files1 = new ArrayList<>();
                List<File> fs1 = MyUtils.GetFiles(SDCardUtils.getSDCardPath(), ".txt", true, files1);
                MyUtils.showTxtDialog(getContext(),fs1, mMeetAgendaEdt);
                break;
            case R.id.agenda_export:
                showDialog(mMeetAgendaEdt);
                break;
            case R.id.notice_export:
                showDialog(mMeetNoticeEdt);
                break;
        }
    }



    /**
     * 弹出输入文件名的对话框
     * @param edt EditText 控件
     */
    public void showDialog(final EditText edt) {
        final EditText editText = new EditText(getContext());
        new AlertDialog.Builder(getContext()).setTitle("请输入文件名")
                .setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!(TextUtils.isEmpty(editText.getText().toString().trim()))) {
                    String SaveFileName = editText.getText().toString();
                    //调用保存方法
                    saveTxt(SaveFileName, edt);
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

    /**
     * 将文本保存到sdcard中
     *
     * @param filename 文件名
     * @param editText 需要获取保存的EditText
     */
    private void saveTxt(String filename, EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
            Toast.makeText(getContext(), "请先输入有效文本~", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FileWriter fw = new FileWriter("/sdcard/" + filename + ".txt");
            fw.flush();
            fw.write(editText.getText().toString());
            fw.close();
            Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
