package com.pa.paperless.adapter.setadapter;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.bean.PlaceInfo;
import com.pa.paperless.bean.RoomTopBean;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.MeetRoomManagementFragment;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/11/20.
 * 系统设置-会议室管理-adapter
 */

public class MeetingRoomTopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<InterfaceMain.pbui_Item_MeetRoomDetailInfo> mData;
    private ItemClickListener mItemClick;
    private int mCheckedPosion;

    public void setCheckedId(int posion){
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
        //当索引大于数据大小时
        if (position > mData.size()-1) {
            MeetRoomManagementFragment.mPosion = mData.size()-1;
        }
        if (mData.size() < 1) {
            MeetRoomManagementFragment.isDelete = false;
        }
    }

    public void inserted(int position,InterfaceMain.pbui_Item_MeetRoomDetailInfo data){
        mData.add(position,data);
        notifyItemInserted(position);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        mItemClick = listener;
    }

    public MeetingRoomTopAdapter(Context context, List<InterfaceMain.pbui_Item_MeetRoomDetailInfo> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.room_top_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        InterfaceMain.pbui_Item_MeetRoomDetailInfo pbui_item_meetRoomDetailInfo = mData.get(position);
        String name = new String(pbui_item_meetRoomDetailInfo.getName().toByteArray());
        String addr = new String(pbui_item_meetRoomDetailInfo.getAddr().toByteArray());
        String comment = new String(pbui_item_meetRoomDetailInfo.getComment().toByteArray());
        ((ViewHolder)holder).room_item_number.setText(position+1+"");
        ((ViewHolder)holder).room_item_name.setText(name);
        ((ViewHolder)holder).room_item_place.setText(addr);
        ((ViewHolder)holder).room_item_note.setText(comment);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClick.onItemClick(holder.itemView, holder.getLayoutPosition());
            }
        });
        /** ************ ******  设置item选中效果  ****** ************ **/
        if(position == mCheckedPosion){
           int color = mContext.getResources().getColor(R.color.ItemCheckedColor);
           holder.itemView.setBackgroundColor(color);
        }else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView room_item_number;
        public TextView room_item_name;
        public TextView room_item_place;
        public TextView room_item_note;

        public ViewHolder(View rootView) {
            super(rootView);
            this.room_item_number = (TextView) rootView.findViewById(R.id.room_item_number);
            this.room_item_name = (TextView) rootView.findViewById(R.id.room_item_name);
            this.room_item_place = (TextView) rootView.findViewById(R.id.room_item_place);
            this.room_item_note = (TextView) rootView.findViewById(R.id.room_item_note);
        }
    }
}
