package com.pa.paperless.fragment.manage.secondfloor.s.prepare;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pa.paperless.R;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.pa.paperless.utils.SDCardUtils;
import com.pa.paperless.utils.TxtFileFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
                showTxtDialog(getSDTxtFile(), mMeetAgendaEdt);
                break;
            case R.id.notice_from:
                showTxtDialog(getSDTxtFile(), mMeetNoticeEdt);
                break;
            case R.id.agenda_export:
                showDialog(mMeetAgendaEdt);
                break;
            case R.id.notice_export:
                showDialog(mMeetNoticeEdt);
                break;
        }
    }


    private List<File> getSDTxtFile() {
        //1.获取到sd卡目录下所有的txt文件
        List<File> txtFile = new ArrayList<>();
        File file = new File(SDCardUtils.getSDCardPath());
        TxtFileFilter txtFileFilter = new TxtFileFilter();
        txtFileFilter.addType(".txt");
        files = file.listFiles(txtFileFilter);
        if (files!=null) {
            for (int i = 0; i < files.length; i++) {
                txtFile.add(files[i]);
            }
            return txtFile;
        }
        Toast.makeText(getContext(), "没有找到相关文件！", Toast.LENGTH_SHORT).show();
        return new ArrayList<>();
    }

    /**
     * 以弹出框的形式展示查找到的txt文档文件
     *
     * @param txtFile 用来定义数组的大小
     * @param edt     需要获取TXT文本内容的输入框
     */
    private void showTxtDialog(List<File> txtFile, final EditText edt) {
        //存放txt文件的路径
        final String[] txtFilePath = new String[txtFile.size()];
        //txt文件的名称
        final String[] txtFileName = new String[txtFile.size()];
        for (int i = 0; i < txtFile.size(); i++) {
            txtFilePath[i] = txtFile.get(i).toString();//  /storage/emulated/0/游戏文本.txt
            txtFileName[i] = txtFile.get(i).getName();//   游戏文本.txt
        }
        new AlertDialog.Builder(getContext()).setTitle("SD卡中所有的文档文件目录")
                .setItems(txtFileName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //  读取内容获得文本
                        String s = ReadTxtFile(txtFilePath[i]);
                        edt.setText(s);
                    }
                }).create().show();
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

    public String ReadTxtFile(String strFilePath) {
        String path = String.valueOf(strFilePath);
        String content = ""; //文件内容字符串
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return content;
    }
}
