package com.pa.paperless.adapter.setadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mogujie.tt.protobuf.InterfaceRoom;
import com.pa.paperless.R;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 * 系统设置-座位排布adapter
 */

public class SeatArrangementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> mData;
    private ItemClickListener mListener;
    private int mCheckedPosion;

    public void setCheckedId(int posion){
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }
    public SeatArrangementAdapter(Context context, List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> data) {
        mContext = context;
        mData = data;
    }

    public void setListener(ItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.seat_arrangement_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).seatarrangement_item_number.setText(position + 1 + "");
        String name = new String(mData.get(position).getName().toByteArray());
        ((ViewHolder) holder).seatarrangement_item_roomname.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.itemView, holder.getLayoutPosition());
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
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView seatarrangement_item_number;
        public TextView seatarrangement_item_roomname;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.seatarrangement_item_number = (TextView) rootView.findViewById(R.id.seatarrangement_item_number);
            this.seatarrangement_item_roomname = (TextView) rootView.findViewById(R.id.seatarrangement_item_roomname);
        }

    }
}
