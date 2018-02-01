package com.pa.paperless.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.bean.MemberInfo;

import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 */

public class MainMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final List<MemberInfo> mData;

    public MainMemberAdapter(Context context, List<MemberInfo> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_member_main, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).memberNumItem.setText(position + 1 + "");
        ((ViewHolder) holder).memberNameItem.setText(mData.get(position).getName());
        ((ViewHolder) holder).memberJobItem.setText(mData.get(position).getJob());
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView memberNameItem;
        public TextView memberJobItem;
        public TextView memberNumItem;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.memberNameItem = (TextView) rootView.findViewById(R.id.member_name_item);
            this.memberJobItem = (TextView) rootView.findViewById(R.id.member_job_item);
            this.memberNumItem = (TextView) rootView.findViewById(R.id.member_num_item);
        }

    }
}
