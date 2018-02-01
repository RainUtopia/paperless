package com.pa.paperless.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.pa.paperless.R;

/**
 * 目录权限
 */
public class DirPermissionActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mDirpermissRlLeft;
    private RecyclerView mDirpermissRlRight;
    private Button mSave;
    private Button mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dir_permission);
        initView();
    }

    private void initView() {
        mDirpermissRlLeft = (RecyclerView) findViewById(R.id.dirpermiss_rl_left);
        mDirpermissRlRight = (RecyclerView) findViewById(R.id.dirpermiss_rl_right);
        mSave = (Button) findViewById(R.id.save);
        mBack = (Button) findViewById(R.id.back);

        mSave.setOnClickListener(this);
        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                Log.e("MyLog","DirPermissionActivity.onClick:  点击保存 --->>> ");
                break;
            case R.id.back:
                Log.e("MyLog","DirPermissionActivity.onClick:  点击返回 --->>> ");
                finish();
                break;
        }
    }
}
