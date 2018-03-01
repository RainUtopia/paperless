package com.pa.paperless.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pa.paperless.R;
import com.pa.paperless.bean.MeetDirBean;

import java.util.List;

/**
 * Created by Administrator on 2018/3/1.
 */

public class ChooseDirDialogAdapter extends RecyclerView.Adapter<ChooseDirDialogAdapter.ViewHolder> {
    private final Context mContext;
    private final List<MeetDirBean> mData;

    public ChooseDirDialogAdapter(Context context, List<MeetDirBean> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public ChooseDirDialogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.choose_dir_dialog, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MeetDirBean meetDirBean = mData.get(position);
        holder.dir_tv.setText(meetDirBean.getDirName());
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public Button dir_tv;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.dir_tv = (Button) rootView.findViewById(R.id.dir_btn);
        }

    }
}
