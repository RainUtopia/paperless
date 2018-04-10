package com.pa.paperless.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.pa.paperless.R;

public class PostilActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mPostilImage;
    Button mPostilSaveLocal,mPostilSaveServer,mPostilPic,mPostilStartScreen,mPostilStopScreen,
            mPostilStartProjection,mPostilStopProjection,mPostilPre,mPostilNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postil);
        initView();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("postil");
        String postil = bundle.getString("filepath");
        Log.e("MyLog","PostilActivity.onCreate 33行:   --->>> "+postil);
        Glide.with(this).load(postil).into(mPostilImage);
    }

    private void initView() {
        mPostilImage = (ImageView) findViewById(R.id.postil_image);
        mPostilSaveLocal = (Button) findViewById(R.id.postil_save_local);
        mPostilSaveServer = (Button) findViewById(R.id.postil_save_server);
        mPostilPic = (Button) findViewById(R.id.postil_pic);
        mPostilStartScreen = (Button) findViewById(R.id.postil_start_screen);
        mPostilStopScreen = (Button) findViewById(R.id.postil_stop_screen);
        mPostilStartProjection = (Button) findViewById(R.id.postil_start_projection);
        mPostilStopProjection = (Button) findViewById(R.id.postil_stop_projection);
        mPostilPre = (Button) findViewById(R.id.postil_pre);
        mPostilNext = (Button) findViewById(R.id.postil_next);

        mPostilSaveLocal.setOnClickListener(this);
        mPostilSaveServer.setOnClickListener(this);
        mPostilPic.setOnClickListener(this);
        mPostilStartScreen.setOnClickListener(this);
        mPostilStopScreen.setOnClickListener(this);
        mPostilStartProjection.setOnClickListener(this);
        mPostilStopProjection.setOnClickListener(this);
        mPostilPre.setOnClickListener(this);
        mPostilNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.postil_save_local:

                break;
            case R.id.postil_save_server:

                break;
            case R.id.postil_pic:

                break;
            case R.id.postil_start_screen:

                break;
            case R.id.postil_stop_screen:

                break;
            case R.id.postil_start_projection:

                break;
            case R.id.postil_stop_projection:

                break;
            case R.id.postil_pre:

                break;
            case R.id.postil_next:

                break;
        }
    }
}
