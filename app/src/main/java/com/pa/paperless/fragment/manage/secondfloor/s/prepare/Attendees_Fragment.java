package com.pa.paperless.fragment.manage.secondfloor.s.prepare;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pa.paperless.R;
import com.pa.paperless.activity.PermissionActivity;
import com.pa.paperless.adapter.AttendAdapter;
import com.pa.paperless.adapter.PopupFromAdapter;
import com.pa.paperless.bean.AttendeesBean;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.pa.paperless.listener.ItemClickListener;
//import com.pa.paperless.utils.RecycleViewDivider;
import com.pa.paperless.utils.SDCardUtils;
import com.pa.paperless.utils.TxtFileFilter;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/17.
 * 会前准备-参会人员
 */

public class Attendees_Fragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mAttendeesRl;
    private EditText mAttendeesNameEdt;
    private EditText mAttendeesUnitEdt;
    private EditText mAttendeesPositionEdt;
    private EditText mAttendeesNoteEdt;
    private Button mAttendeesSetPermissions;
    private Button mAttendeesAddBtn;
    private Button mAttendeesAmendBtn;
    private Button mAttendeesDeleteBtn;
    private Button mAttendeesFrom;
    private Button mAttendeesExport;
    private Button mAddFromCommonly;
    private Button mExportToCommonly;
    private PopupWindow mFromCommonParticipantsAddPop;//从常用参会人添加
    private List<AttendeesBean> mData;
    private AttendAdapter adapter;//参会人员Adapter
    public static int mPosion;
    public static boolean isDelete;
    private TextView itemNameTv;
    private TextView itemUnitTv;
    private TextView itemJobTv;
    private TextView itemTypeTv;
    private TextView itemPwdTv;
    private TextView itemNoteTv;
    private boolean isSelect;//控制修改按钮
    private File[] files;
    /**
     * *********** ******    ****** ************
     **/
    //pop中的数据
    private List<AttendeesBean> mAddFromData;
    public static List<Boolean> checkeds;
    private List<AttendeesBean> popAddData;
    private PopupFromAdapter addFromAttendeeAdapter;
    private CheckBox all_check;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.attendees, container, false);
        initView(inflate);
        mAddFromData = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            mAddFromData.add(new AttendeesBean("name_" + i, "unit_" + i, "job_" + i, ""));
        }
        addFromAttendeeAdapter = new PopupFromAdapter(getContext(), mAddFromData);
        return inflate;
    }

    private void initView(View inflate) {
        mAttendeesRl = (RecyclerView) inflate.findViewById(R.id.attendees_rl);
        mData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mData.add(new AttendeesBean("name_" + i, "unit_" + i, "job_" + i, ""));
        }
        adapter = new AttendAdapter(getContext(), mData);
        mAttendeesRl.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                Toast.makeText(getContext(), "点击了" + posion, Toast.LENGTH_SHORT).show();
                mPosion = posion;
                isDelete = true;
                isSelect = true;
                itemNameTv = view.findViewById(R.id.attend_item_name);
                itemUnitTv = view.findViewById(R.id.attend_item_unit);
                itemJobTv = view.findViewById(R.id.attend_item_job);
                itemTypeTv = view.findViewById(R.id.attend_item_type);
                itemPwdTv = view.findViewById(R.id.attend_item_signpwd);
                itemNoteTv = view.findViewById(R.id.attend_item_note);
                //同步到EditText中
                mAttendeesNameEdt.setText(itemNameTv.getText().toString());
                mAttendeesUnitEdt.setText(itemUnitTv.getText().toString());
                mAttendeesPositionEdt.setText(itemJobTv.getText().toString());
                mAttendeesNoteEdt.setText(itemNoteTv.getText().toString());
                adapter.setCheckedId(posion);

            }
        });
        mAttendeesRl.setAdapter(adapter);

        mAttendeesNameEdt = (EditText) inflate.findViewById(R.id.attendees_name_edt);
        mAttendeesUnitEdt = (EditText) inflate.findViewById(R.id.attendees_unit_edt);
        mAttendeesPositionEdt = (EditText) inflate.findViewById(R.id.attendees_position_edt);
        mAttendeesNoteEdt = (EditText) inflate.findViewById(R.id.attendees_note_edt);
        mAttendeesSetPermissions = (Button) inflate.findViewById(R.id.attendees_set_permissions);
        mAttendeesAddBtn = (Button) inflate.findViewById(R.id.attendees_add_btn);
        mAttendeesAmendBtn = (Button) inflate.findViewById(R.id.attendees_amend_btn);
        mAttendeesDeleteBtn = (Button) inflate.findViewById(R.id.attendees_delete_btn);
        mAttendeesFrom = (Button) inflate.findViewById(R.id.attendees_from);
        mAttendeesExport = (Button) inflate.findViewById(R.id.attendees_export);
        mAddFromCommonly = (Button) inflate.findViewById(R.id.add_from_commonly);
        mExportToCommonly = (Button) inflate.findViewById(R.id.export_to_commonly);

        mAttendeesSetPermissions.setOnClickListener(this);
        mAttendeesAddBtn.setOnClickListener(this);
        mAttendeesAmendBtn.setOnClickListener(this);
        mAttendeesDeleteBtn.setOnClickListener(this);
        mAttendeesFrom.setOnClickListener(this);
        mAttendeesExport.setOnClickListener(this);
        mAddFromCommonly.setOnClickListener(this);
        mExportToCommonly.setOnClickListener(this);
    }

    /**
     * 获取到sd卡目录下所有的Excel文件
     *
     * @return
     */
    private List<File> getSDExcelFile() {
        List<File> ExcleFile = new ArrayList<>();
        File file = new File(SDCardUtils.getSDCardPath());
        TxtFileFilter txtFileFilter = new TxtFileFilter();
        txtFileFilter.addType(".xls");
        txtFileFilter.addType(".xlsx");
        files = file.listFiles(txtFileFilter);
        for (int i = 0; i < files.length; i++) {
            Log.e("MyLog", "MeettingAgenda_Fragment.onClick:  文件 --->>> " + files[i]);
            ExcleFile.add(files[i]);
        }
        return ExcleFile;
    }

    /**
     * 以弹出框的形式展示查找到的Excel表格文件
     *
     * @param txtFile 用来定义数组的大小
     */
    private void showExcelDialog(List<File> txtFile) {
        //存放xlsx文件的路径
        final String[] txtFilePath = new String[txtFile.size()];
        //xlsx文件的名称
        final String[] txtFileName = new String[txtFile.size()];
        for (int i = 0; i < txtFile.size(); i++) {
            txtFilePath[i] = txtFile.get(i).toString();//  /storage/emulated/0/游戏文本.xlsx
            txtFileName[i] = txtFile.get(i).getName();//   游戏文本.xlsx
        }
        new AlertDialog.Builder(getContext()).setTitle("Excel文件目录")
                .setItems(txtFileName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "点击了" + txtFileName[i], Toast.LENGTH_SHORT).show();
                        // TODO: 2017/11/25 保存到Excel中
//                        String s = ReadTxtFile(txtFilePath[i]);
//                        edt.setText(s);
                    }
                }).create().show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attendees_set_permissions://参会人权限设置
                startActivity(new Intent(getContext(), PermissionActivity.class));
                break;
            case R.id.attendees_add_btn://增加
                String name = mAttendeesNameEdt.getText().toString();
                String unit = mAttendeesUnitEdt.getText().toString();
                String job = mAttendeesPositionEdt.getText().toString();
                String note = mAttendeesNoteEdt.getText().toString();
                // TODO: 2017/11/25 类别和签到密码待传、、、
                mData.add(mData.size(), new AttendeesBean(name, unit, job, "", "", note));
                adapter.notifyItemInserted(mData.size());
                break;
            case R.id.attendees_amend_btn://修改
                if (isSelect) {
                    String name1 = mAttendeesNameEdt.getText().toString();
                    String unit1 = mAttendeesUnitEdt.getText().toString();
                    String job1 = mAttendeesPositionEdt.getText().toString();
                    String note1 = mAttendeesNoteEdt.getText().toString();
                    itemNameTv.setText(name1);
                    itemUnitTv.setText(unit1);
                    itemJobTv.setText(job1);
                    itemNoteTv.setText(note1);
                    isSelect = false;
                }
                break;
            case R.id.attendees_delete_btn://删除
                if (isDelete) {
                    adapter.removeItem(mPosion);
                }
                break;
            case R.id.attendees_from://从Excel中导入
                showExcelDialog(getSDExcelFile());
                break;
            case R.id.attendees_export://导出到Excel

                break;
            case R.id.add_from_commonly://从常用参会人添加
                showAddFromPop();
                break;
            case R.id.export_to_commonly://导出到常用参会人
                if (isSelect) {
                    String name2 = itemNameTv.getText().toString();
                    String unit2 = mAttendeesUnitEdt.getText().toString();
                    String job2 = mAttendeesPositionEdt.getText().toString();
                    mAddFromData.add(new AttendeesBean(name2, unit2, job2, ""));
                    addFromAttendeeAdapter.notifyItemInserted(mAddFromData.size());
                    Toast.makeText(getContext(), "导出成功~", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "请先选中需要导出的参会人~", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    /**
     * 从常用参会人添加
     */

    private void showAddFromPop() {
        final View popupView = getActivity().getLayoutInflater().inflate(R.layout.pop_attendees, null);
        mFromCommonParticipantsAddPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        RecyclerView add_from_rl = (RecyclerView) popupView.findViewById(R.id.pop_add_from);
        all_check = popupView.findViewById(R.id.all_checked_cb);
        add_from_rl.setLayoutManager(new LinearLayoutManager(getContext()));
        //用来保存是否选中的结果集
        checkeds = new ArrayList<>();
        //初始化全部设为false
        for (int i = 0; i < mAddFromData.size(); i++) {
            checkeds.add(false);
        }
        //PopupWindow item点击事件监听
        //要添加的数据
        popAddData = new ArrayList<>();
        addFromAttendeeAdapter.setListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                CheckBox itemCb = view.findViewById(R.id.add_from_cb);
                itemCb.setChecked(!itemCb.isChecked());
                if (itemCb.isChecked()) {
                    if (!popAddData.contains(mAddFromData.get(posion))) {
                        popAddData.add(mAddFromData.get(posion));
                    }
//                    checkeds.set(posion, true);
                } else {
                    //删除该项
                    if (popAddData.contains(mAddFromData.get(posion))) {
                        for (int i = 0; i < popAddData.size(); i++) {
                            if (popAddData.get(i).equals(mAddFromData.get(posion))) {
                                popAddData.remove(i);
                            }
                        }
                    }
//                    checkeds.set(posion, false);
                }
                checkeds.set(posion, itemCb.isChecked());
//                if (checkeds.contains(false)) {
                all_check.setChecked(!(checkeds.contains(false)));
//                } else {
//                    all_check.setChecked(true);
//                }
                addFromAttendeeAdapter.setCheckedId(posion);
                Log.e("MyLog", "Attendees_Fragment.onItemClick:  Pop中要添加的数据 --->>> " + popAddData.size());
            }
        });
        add_from_rl.setAdapter(addFromAttendeeAdapter);
//        if (checkeds.contains(false)) {
//            all_check.setChecked(false);
//        } else {
//            all_check.setChecked(true);
//        }
        all_check.setChecked(!(checkeds.contains(false)));
        all_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // 全选选中 且 有item未选中
                if (b) {
                    if (checkeds.contains(false)) {
                        for (int i = 0; i < mAddFromData.size(); i++) {
                            if (!checkeds.get(i)) {
                                checkeds.set(i, true);
                                popAddData.add(mAddFromData.get(i));
                            }
                        }
                    }
                }
                //全选未选中 且 有item未选中
                else {
                    if (!checkeds.contains(false)) {
                        for (int i = 0; i < mAddFromData.size(); i++) {
                            if (checkeds.get(i)) {
                                checkeds.set(i, false);
                                AttendeesBean attendeesBean = mAddFromData.get(i);
                                for (int j = 0; j < popAddData.size(); j++) {
                                    if (popAddData.get(j).equals(attendeesBean)) {
                                        popAddData.remove(j);
                                    }
                                }
                            }
                        }
                    }
                }
                Log.e("MyLog", "Attendees_Fragment.onClick:  Pop中要添加的数据 --->>> " + popAddData.size());
                addFromAttendeeAdapter.notifyDataSetChanged();
            }
        });
        /** ************ ******    ****** ************ **/
        Button pop_add = popupView.findViewById(R.id.pop_add);
        Button pop_dis = popupView.findViewById(R.id.pop_dis);
        //popupWindow添加按钮事件监听
        pop_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int j = 0; j < popAddData.size(); j++) {
                    mData.add(mData.size(), popAddData.get(j));
                    adapter.notifyItemInserted(mData.size());
                }
                mFromCommonParticipantsAddPop.dismiss();
            }
        });
        /** ************ ******    ****** ************ **/
        //popupWindow返回按钮事件监听
        pop_dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFromCommonParticipantsAddPop.dismiss();
            }
        });
        mFromCommonParticipantsAddPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mFromCommonParticipantsAddPop.setTouchable(true);
        mFromCommonParticipantsAddPop.setOutsideTouchable(true);
        mFromCommonParticipantsAddPop.showAtLocation(getActivity().findViewById(R.id.manage_activity_id), Gravity.CENTER, 0, 0);
    }


}