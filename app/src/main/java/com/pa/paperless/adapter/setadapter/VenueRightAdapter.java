package com.pa.paperless.adapter.setadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.bean.VenueBean;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.SecretaryManagementFragment;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 * 系统设置 - 秘书管理 - 会场Adapter
 */

public class VenueRightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<VenueBean> mData;
    private ItemClickListener mListener;
    private int mCheckedPosion;

    //item的删除
    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
        //当索引大于数据大小时
        if (position > mData.size() - 1) {
            SecretaryManagementFragment.SelectRightPosition = mData.size() - 1;
        }
        if (mData.size() < 1) {
            SecretaryManagementFragment.isDeleteRight = false;
        }
    }

    //item的添加
    public void addItem(int position, VenueBean data) {
        mData.add(position, data);
        notifyItemInserted(position);
    }

    public void removeAll() {
        mData.clear();
        notifyDataSetChanged();
    }


    public void addAll(List<VenueBean> data) {
        for (int i = 0; i < data.size(); i++) {
            mData.add(data.get(i));
        }
        notifyDataSetChanged();
    }

    public void setCheckedId(int posion) {
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }

    public void setListener(ItemClickListener listener) {
        mListener = listener;
    }

    public VenueRightAdapter(Context context, List<VenueBean> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.venue_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).venue_name.setText(mData.get(position).getName());
        ((ViewHolder) holder).venue_place.setText(mData.get(position).getPlace());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.itemView, holder.getLayoutPosition());
            }
        });
        if (position == mCheckedPosion) {
            int color = mContext.getResources().getColor(R.color.ItemCheckedColor);
            holder.itemView.setBackgroundColor(color);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView venue_name;
        public TextView venue_place;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.venue_name = (TextView) rootView.findViewById(R.id.venue_name);
            this.venue_place = (TextView) rootView.findViewById(R.id.venue_place);
        }

    }
}
