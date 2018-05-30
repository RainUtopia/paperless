package com.pa.paperless.adapter.preadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.activity.PermissionActivity;
import com.pa.paperless.bean.PermissionBean;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/12/6.
 */

public class PermissionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<PermissionBean> mData;
    private ItemClickListener mListener;

    public void setListener(ItemClickListener listener) {
        mListener = listener;
    }

    public PermissionsAdapter(Context context, List<PermissionBean> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.permissions_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).permission_item_number.setText(position + 1 + "");
        ((ViewHolder) holder).permission_item_number.setChecked(PermissionActivity.checkeds.get(position));
        ((ViewHolder) holder).permission_item_name.setText(mData.get(position).getName());
        ((ViewHolder) holder).permission_item_screen.setText(mData.get(position).getScreen());
        ((ViewHolder) holder).permission_item_projection.setText(mData.get(position).getProjection());
        ((ViewHolder) holder).permission_item_upload.setText(mData.get(position).getUpload());
        ((ViewHolder) holder).permission_item_download.setText(mData.get(position).getDownload());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.itemView, holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public CheckBox permission_item_number;
        public TextView permission_item_name;
        public TextView permission_item_screen;
        public TextView permission_item_projection;
        public TextView permission_item_upload;
        public TextView permission_item_download;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.permission_item_number = (CheckBox) rootView.findViewById(R.id.permission_item_number);
            this.permission_item_name = (TextView) rootView.findViewById(R.id.permission_item_name);
            this.permission_item_screen = (TextView) rootView.findViewById(R.id.permission_item_screen);
            this.permission_item_projection = (TextView) rootView.findViewById(R.id.permission_item_projection);
            this.permission_item_upload = (TextView) rootView.findViewById(R.id.permission_item_upload);
            this.permission_item_download = (TextView) rootView.findViewById(R.id.permission_item_download);
        }

    }

}
