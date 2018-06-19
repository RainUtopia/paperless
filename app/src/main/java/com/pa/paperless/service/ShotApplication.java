package com.pa.paperless.service;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

/**
 * Created by Administrator on 2018/6/9.
 */

public class ShotApplication extends Application {
    private int result;
    private Intent intent;
    private MediaProjectionManager mMediaProjectionManager;

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public int getResult(){
        return result;
    }

    public Intent getIntent(){
        return intent;
    }

    public MediaProjectionManager getMediaProjectionManager(){
        return mMediaProjectionManager;
    }

    public void setResult(int result1){
        this.result = result1;
    }

    public void setIntent(Intent intent1){
        this.intent = intent1;
    }

    public void setMediaProjectionManager(MediaProjectionManager mMediaProjectionManager){
        this.mMediaProjectionManager = mMediaProjectionManager;
    }
}
