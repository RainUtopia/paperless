package com.pa.paperless.adapter.setadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mogujie.tt.protobuf.InterfaceDevice;
import com.pa.paperless.R;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.MeetRoomManagementFragment;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/11/21.
 * 系统设置-会议室管理-会议室设备Adapter
 */

public class MeetingRoomLeftAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ItemClickListener mListener;
    private List<InterfaceDevice.pbui_Item_DeviceDetailInfo> mDatas;
    private Context mContext;
    private int mCheckedPosion;

    public void setCheckedPosion(int posion) {
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }

    public void setItemClick(ItemClickListener listener) {
        mListener = listener;
    }

    public MeetingRoomLeftAdapter(Context context, List<InterfaceDevice.pbui_Item_DeviceDetailInfo> datas) {
        mContext = context;
        mDatas = datas;
    }

    //item的删除
    public void removeItem(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDatas.size());
        //当索引大于数据大小时
        if (position > mDatas.size() - 1) {
            MeetRoomManagementFragment.SelectLeftPosition = mDatas.size() - 1;
        }
        if (mDatas.size() < 1) {
            MeetRoomManagementFragment.isDeleteLeft = false;
        }
    }

    //item的添加
    public void addItem(int position, InterfaceDevice.pbui_Item_DeviceDetailInfo data) {
        mDatas.add(position, data);
        notifyItemInserted(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.room_left_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        InterfaceDevice.pbui_Item_DeviceDetailInfo pbui_item_deviceDetailInfo = mDatas.get(position);
        String name = new String(pbui_item_deviceDetailInfo.getDevname().toByteArray());
        int facestate = pbui_item_deviceDetailInfo.getFacestate();
        if(facestate == 0){
            ((ViewHolder) holder).room_left_type.setText("会议数据库");
        }else {
            ((ViewHolder) holder).room_left_type.setText("会议茶水设备");
        }
        ((ViewHolder) holder).room_left_number.setText(position + 1 + "");
        ((ViewHolder) holder).room_left_name.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int layoutPosition = holder.getLayoutPosition();
                mListener.onItemClick(holder.itemView, layoutPosition);
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
        return mDatas != null ? mDatas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView room_left_number;
        public TextView room_left_name;
        public TextView room_left_type;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.room_left_number = (TextView) rootView.findViewById(R.id.room_left_number);
            this.room_left_name = (TextView) rootView.findViewById(R.id.room_left_name);
            this.room_left_type = (TextView) rootView.findViewById(R.id.room_left_type);
        }

    }
}
