package com.pa.paperless.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */

public class VideoItemAdapter extends RecyclerView.Adapter<VideoItemAdapter.ViewHolder> {
    private final List<DeviceInfo> mData;
    private ItemClickListener mListener;

    public VideoItemAdapter(List<DeviceInfo> data) {
        mData = data;
    }

    public void setItemListener(ItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public VideoItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final VideoItemAdapter.ViewHolder holder, int position) {
        holder.name_tv.setText(mData.get(position).getDevName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView name_tv;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.name_tv = (TextView) rootView.findViewById(R.id.name_tv);
        }

    }
}
