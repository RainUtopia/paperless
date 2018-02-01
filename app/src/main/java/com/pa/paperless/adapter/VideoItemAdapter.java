package com.pa.paperless.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

/**
 * Created by Administrator on 2017/11/9.
 */

public class VideoItemAdapter extends ArrayAdapter {

    public VideoItemAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

}
