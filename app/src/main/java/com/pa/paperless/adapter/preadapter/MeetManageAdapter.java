package com.pa.paperless.adapter.preadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.bean.MeetManageBean;
import com.pa.paperless.fragment.manage.secondfloor.s.prepare.MeettingManagement_Fragment;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/11/23.
 * 会前准备-会议管理Adapter
 */

public class MeetManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final List<MeetManageBean> mData;
    private final Context mContext;
    private ItemClickListener mListener;
    private int mCheckedPosion;

    public MeetManageAdapter(Context context, List<MeetManageBean> data) {
        mContext = context;
        mData = data;
    }
    public void removeItem(int position){
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
        //当索引大于数据大小时
        if (position > mData.size()-1) {
            MeettingManagement_Fragment.mPosion = mData.size()-1;
        }
        if (mData.size() < 1) {
            MeettingManagement_Fragment.isDelete = false;
        }
    }
    public void setCheckedId(int posion){
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.meet_manage_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }
    public void setListener(ItemClickListener listener){
        mListener =listener;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).meetmanage_item_number.setText(position+1+"");
        ((ViewHolder)holder).meetmanage_item_meetname.setText(mData.get(position).getMeetName());
        ((ViewHolder)holder).meetmanage_item_meetstate.setText(mData.get(position).getMeetState());
        ((ViewHolder)holder).meetmanage_item_roomname.setText(mData.get(position).getMeetRoomName());
        ((ViewHolder)holder).meetmanage_item_secret.setText(mData.get(position).getSecret());
        ((ViewHolder)holder).meetmanage_item_starttime.setText(mData.get(position).getStartTime());
        ((ViewHolder)holder).meetmanage_item_overtime.setText(mData.get(position).getOverTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.itemView,holder.getLayoutPosition());
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

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public View rootView;
        public TextView meetmanage_item_number;
        public TextView meetmanage_item_meetname;
        public TextView meetmanage_item_meetstate;
        public TextView meetmanage_item_roomname;
        public TextView meetmanage_item_secret;
        public TextView meetmanage_item_starttime;
        public TextView meetmanage_item_overtime;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.meetmanage_item_number = (TextView) rootView.findViewById(R.id.meetmanage_item_number);
            this.meetmanage_item_meetname = (TextView) rootView.findViewById(R.id.meetmanage_item_meetname);
            this.meetmanage_item_meetstate = (TextView) rootView.findViewById(R.id.meetmanage_item_meetstate);
            this.meetmanage_item_roomname = (TextView) rootView.findViewById(R.id.meetmanage_item_roomname);
            this.meetmanage_item_secret = (TextView) rootView.findViewById(R.id.meetmanage_item_secret);
            this.meetmanage_item_starttime = (TextView) rootView.findViewById(R.id.meetmanage_item_starttime);
            this.meetmanage_item_overtime = (TextView) rootView.findViewById(R.id.meetmanage_item_overtime);
        }

    }
}
