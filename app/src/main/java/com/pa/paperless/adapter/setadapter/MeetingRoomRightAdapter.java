package com.pa.paperless.adapter.setadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.bean.DeviceBean;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.MeetRoomManagementFragment;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/11/21.
 * 系统设置-会议室管理-所有设备Adapter
 */

public class MeetingRoomRightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ItemClickListener mListener;
    private List<DeviceBean> mDatas;
    private Context mContext;
    private int mCheckedPosion;


    //item的删除
    public void removeItem(int position){
        mDatas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDatas.size());
        //当索引大于数据大小时
        if (position > mDatas.size()-1) {
            MeetRoomManagementFragment.SelectRightPosition= mDatas.size()-1;
        }
        if (mDatas.size() < 1) {
            MeetRoomManagementFragment.isDeleteRight = false;
        }
    }
    //item的添加
    public void addItem(int position,DeviceBean data){
        mDatas.add(position,data);
        notifyItemInserted(position);
    }
    public void setCheckedPosion(int posion){
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }
    public void setItemClick(ItemClickListener listener){
        mListener = listener;
    }

    public MeetingRoomRightAdapter(Context context, List<DeviceBean> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.room_left_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).room_left_number.setText(position+1+"");
        ((ViewHolder) holder).room_left_name.setText(mDatas.get(position).getDeviceName());
        ((ViewHolder) holder).room_left_type.setText(mDatas.get(position).getDeviceType());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int layoutPosition = holder.getLayoutPosition();
                mListener.onItemClick(holder.itemView,layoutPosition);
            }
        });
        if(position == mCheckedPosion){
           int color = mContext.getResources().getColor(R.color.ItemCheckedColor);
           holder.itemView.setBackgroundColor(color);
        }else {
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
