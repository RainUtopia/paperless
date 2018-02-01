package com.pa.paperless.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.bean.AttendeesBean;
import com.pa.paperless.fragment.manage.secondfloor.s.prepare.Attendees_Fragment;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/11/25.
 * 会前准备-参会人员Adapter
 */

public class AttendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final List<AttendeesBean> mData;
    private ItemClickListener mListener;
    private int mCheckedPosion;

    public AttendAdapter(Context context, List<AttendeesBean> data) {
        mContext = context;
        mData = data;
    }
    public void removeItem(int position){
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
        //当索引大于数据大小时
        if (position > mData.size()-1) {
            Attendees_Fragment.mPosion = mData.size()-1;
        }
        if (mData.size() < 1) {
            Attendees_Fragment.isDelete = false;
        }
    }
    public void setCheckedId(int posion){
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }
    public void setListener(ItemClickListener listener){
        mListener = listener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.attend_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).attend_item_number.setText(position+1+"");
        ((ViewHolder)holder).attend_item_name.setText(mData.get(position).getName());
        ((ViewHolder)holder).attend_item_unit.setText(mData.get(position).getUnit());
        ((ViewHolder)holder).attend_item_job.setText(mData.get(position).getJob());
        ((ViewHolder)holder).attend_item_type.setText(mData.get(position).getType());
        ((ViewHolder)holder).attend_item_signpwd.setText(mData.get(position).getSignPassword());
        ((ViewHolder)holder).attend_item_note.setText(mData.get(position).getNote());
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
        public TextView attend_item_number;
        public TextView attend_item_name;
        public TextView attend_item_unit;
        public TextView attend_item_job;
        public TextView attend_item_type;
        public TextView attend_item_signpwd;
        public TextView attend_item_note;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.attend_item_number = (TextView) rootView.findViewById(R.id.attend_item_number);
            this.attend_item_name = (TextView) rootView.findViewById(R.id.attend_item_name);
            this.attend_item_unit = (TextView) rootView.findViewById(R.id.attend_item_unit);
            this.attend_item_job = (TextView) rootView.findViewById(R.id.attend_item_job);
            this.attend_item_type = (TextView) rootView.findViewById(R.id.attend_item_type);
            this.attend_item_signpwd = (TextView) rootView.findViewById(R.id.attend_item_signpwd);
            this.attend_item_note = (TextView) rootView.findViewById(R.id.attend_item_note);
        }

    }
}
