package com.pa.paperless.fragment.manage.secondfloor.s.after;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pa.paperless.R;
import com.pa.paperless.fragment.meeting.BaseFragment;

/**
 * Created by Administrator on 2017/11/17.
 * 会后整理-批注查看
 */

public class NotationToView_Fragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mNotationLeftRl;
    private View mLineview1;
    private RecyclerView mNotationRightRl;
    private View mLineview2;
    private Button mNotationDownload;
    private Button mNotationDownloadTo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.notation_to_view, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        mNotationLeftRl = (RecyclerView) inflate.findViewById(R.id.notation_left_rl);
        mLineview1 = (View) inflate.findViewById(R.id.lineview1);
        mNotationRightRl = (RecyclerView) inflate.findViewById(R.id.notation_right_rl);
        mLineview2 = (View) inflate.findViewById(R.id.lineview2);
        mNotationDownload = (Button) inflate.findViewById(R.id.notation_download);
        mNotationDownloadTo = (Button) inflate.findViewById(R.id.notation_download_to);

        mNotationDownload.setOnClickListener(this);
        mNotationDownloadTo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notation_download:

                break;
            case R.id.notation_download_to:

                break;
        }
    }
}
