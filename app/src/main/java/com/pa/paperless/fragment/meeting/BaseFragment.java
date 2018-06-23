package com.pa.paperless.fragment.meeting;

import android.support.v4.app.Fragment;
import android.widget.Toast;


/**
 * Created by Administrator on 2017/5/18 0018.
 */

public abstract class BaseFragment extends Fragment {

    protected void showTip(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }
}