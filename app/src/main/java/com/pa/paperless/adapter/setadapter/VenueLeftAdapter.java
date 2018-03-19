package com.pa.paperless.adapter.setadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.pa.paperless.R;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.SecretaryManagementFragment;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 * 系统设置 - 秘书管理 - 会场Adapter
 */

public class VenueLeftAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> mData;
    private ItemClickListener mListener;
    private int mCheckedPosion;

    //item的删除
    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
        //当索引大于数据大小时
        if (position > mData.size() - 1) {
            SecretaryManagementFragment.SelectLeftPosition = mData.size() - 1;
        }
        if (mData.size() < 1) {
            SecretaryManagementFragment.isDeleteLeft = false;
        }
    }

    //item的添加
    public void addItem(int position, InterfaceRoom.pbui_Item_MeetRoomDetailInfo data) {
        mData.add(position, data);
        notifyItemInserted(position);
    }

    /**
     * 加载全部
     *
     * @param data
     */
    public void addAll(List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> data) {
        for (int i = 0; i < data.size(); i++) {
            mData.add(data.get(i));
        }
        notifyDataSetChanged();
    }

    /**
     * 移除全部
     */
    public void removeAll() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void setListener(ItemClickListener listener) {
        mListener = listener;
    }

    public VenueLeftAdapter(Context context, List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> data) {
        mContext = context;
        mData = data;
    }

    public void setCheckedId(int posion) {
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.venue_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        InterfaceRoom.pbui_Item_MeetRoomDetailInfo pbui_item_meetRoomDetailInfo = mData.get(position);
        String name = new String(pbui_item_meetRoomDetailInfo.getName().toByteArray());
        String addr = new String(pbui_item_meetRoomDetailInfo.getAddr().toByteArray());
        ((ViewHolder) holder).venue_name.setText(name);
        ((ViewHolder) holder).venue_place.setText(addr);
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
