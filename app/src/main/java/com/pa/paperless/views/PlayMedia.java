package com.pa.paperless.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Administrator on 2018/3/8.
 */

public class PlayMedia extends View {

    public PlayMedia(Context context) {
        super(context);
    }

    public PlayMedia(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        SurfaceView surfaceView = new SurfaceView(getContext());

    }
}
