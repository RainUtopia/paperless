package com.pa.paperless.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.pa.paperless.R;
import com.pa.paperless.adapter.preadapter.PermissionsAdapter;
import com.pa.paperless.bean.PermissionBean;
import com.pa.paperless.listener.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 参会人权限设置
 */
public class PermissionActivity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox mCheckAllPermission;
    private RecyclerView mPermissionRl;
    private Button mScreenPermission;
    private Button mProjectionPermission;
    private Button mUploadPermission;
    private Button mDownloadPermission;
    private Button mSavePermission;
    private Button mUnScreenPermission;
    private Button mUnProjectionPermission;
    private Button mUnUploadPermission;
    private Button mUnDownloadPermission;
    private Button mBackPermission;
    private List<PermissionBean> PermissionData;
    private PermissionsAdapter mAdapter;
    public static List<Boolean> checkeds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        initView();
    }

    private void initView() {
        mCheckAllPermission = (CheckBox) findViewById(R.id.check_all_permission);
        /** ************ ******  RecyclerView  ****** ************ **/
        mPermissionRl = (RecyclerView) findViewById(R.id.permission_rl);
        PermissionData = new ArrayList<>();
        checkeds = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PermissionData.add(new PermissionBean("name_" + i));
            checkeds.add(false);
        }
        mAdapter = new PermissionsAdapter(getApplicationContext(), PermissionData);
        mPermissionRl.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mPermissionRl.setAdapter(mAdapter);
        //item点击事件
        mAdapter.setListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                Toast.makeText(PermissionActivity.this, "点击了" + posion, Toast.LENGTH_SHORT).show();
                /** ************ ******  获取到点击item的控件  ****** ************ **/
                CheckBox itemCb = view.findViewById(R.id.permission_item_number);
                itemCb.setChecked(!itemCb.isChecked());
                if (itemCb.isChecked()) {
                    checkeds.set(posion, true);
                } else {
                    checkeds.set(posion, false);
                }
                //如果包含了false
                if (checkeds.contains(false)) {
                    mCheckAllPermission.setChecked(false);
                } else {
                    mCheckAllPermission.setChecked(true);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        //全选点击事件监听
        mCheckAllPermission.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (checkeds.contains(false)) {
                        for (int i = 0; i < checkeds.size(); i++) {
                            if (!checkeds.get(i)) {
                                checkeds.set(i, true);
                            }
                        }
                    }
                } else {
                    if (!checkeds.contains(false)) {
                        for (int i = 0; i < checkeds.size(); i++) {
                            if(checkeds.get(i)){
                                checkeds.set(i,false);
                            }
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

        });


        mScreenPermission = (Button) findViewById(R.id.screen_permission);
        mProjectionPermission = (Button) findViewById(R.id.projection_permission);
        mUploadPermission = (Button) findViewById(R.id.upload_permission);
        mDownloadPermission = (Button) findViewById(R.id.download_permission);
        mSavePermission = (Button) findViewById(R.id.save_permission);
        mUnScreenPermission = (Button) findViewById(R.id.un_screen_permission);
        mUnProjectionPermission = (Button) findViewById(R.id.un_projection_permission);
        mUnUploadPermission = (Button) findViewById(R.id.un_upload_permission);
        mUnDownloadPermission = (Button) findViewById(R.id.un_download_permission);
        mBackPermission = (Button) findViewById(R.id.back_permission);

        mScreenPermission.setOnClickListener(this);
        mProjectionPermission.setOnClickListener(this);
        mUploadPermission.setOnClickListener(this);
        mDownloadPermission.setOnClickListener(this);
        mSavePermission.setOnClickListener(this);
        mUnScreenPermission.setOnClickListener(this);
        mUnProjectionPermission.setOnClickListener(this);
        mUnUploadPermission.setOnClickListener(this);
        mUnDownloadPermission.setOnClickListener(this);
        mBackPermission.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.screen_permission://添加同屏
                for (int i = 0; i < checkeds.size(); i++) {
                    if (checkeds.get(i)) {
                        PermissionBean permissionBean = PermissionData.get(i);
                        permissionBean.setScreen("√");
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case R.id.projection_permission://添加投影
                for (int i = 0; i < checkeds.size(); i++) {
                    if (checkeds.get(i)) {
                        PermissionBean permissionBean = PermissionData.get(i);
                        permissionBean.setProjection("√");
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.upload_permission://添加上传
                for (int i = 0; i < checkeds.size(); i++) {
                    if (checkeds.get(i)) {
                        PermissionBean permissionBean = PermissionData.get(i);
                        permissionBean.setUpload("√");
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.download_permission://添加下载
                for (int i = 0; i < checkeds.size(); i++) {
                    if (checkeds.get(i)) {
                        PermissionBean permissionBean = PermissionData.get(i);
                        permissionBean.setDownload("√");
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.save_permission://保存

                break;
            case R.id.un_screen_permission://取消同屏
                for (int i = 0; i < checkeds.size(); i++) {
                    if (checkeds.get(i)) {
                        PermissionBean permissionBean = PermissionData.get(i);
                        permissionBean.setScreen("");
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.un_projection_permission://取消投影
                for (int i = 0; i < checkeds.size(); i++) {
                    if (checkeds.get(i)) {
                        PermissionBean permissionBean = PermissionData.get(i);
                        permissionBean.setProjection("");
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.un_upload_permission://取消上传
                for (int i = 0; i < checkeds.size(); i++) {
                    if (checkeds.get(i)) {
                        PermissionBean permissionBean = PermissionData.get(i);
                        permissionBean.setUpload("");
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.un_download_permission://取消下载
                for (int i = 0; i < checkeds.size(); i++) {
                    if (checkeds.get(i)) {
                        PermissionBean permissionBean = PermissionData.get(i);
                        permissionBean.setDownload("");
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.back_permission://返回
                finish();
                break;
        }
    }
}
