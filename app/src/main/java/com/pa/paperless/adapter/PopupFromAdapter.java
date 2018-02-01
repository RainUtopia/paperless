package com.pa.paperless.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.bean.AttendeesBean;
import com.pa.paperless.fragment.manage.secondfloor.s.prepare.Attendees_Fragment;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/11/18.
 * 会前准备-参会人员-从常用参会人添加-Adapter
 */

public class PopupFromAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final Context mContext;
    private final List<AttendeesBean> mData;
    private ItemClickListener mListener;
    private int mCheckedPosion;

    public PopupFromAdapter(Context context, List<AttendeesBean> data) {
        mContext = context;
        mData = data;
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
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_add_from, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((ViewHolder) holder).add_from_cb.setText(position + 1 + "");
        ((ViewHolder) holder).add_from_cb.setChecked(Attendees_Fragment.checkeds.get(position));
        ((ViewHolder) holder).add_from_name.setText(mData.get(position).getName());
        ((ViewHolder) holder).add_from_unit.setText(mData.get(position).getUnit());
        ((ViewHolder) holder).add_from_job.setText(mData.get(position).getJob());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.itemView,holder.getLayoutPosition());
            }
        });
        //设置item选中效果
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
        public CheckBox add_from_cb;
        public TextView add_from_name;
        public TextView add_from_unit;
        public TextView add_from_job;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.add_from_cb = (CheckBox) rootView.findViewById(R.id.add_from_cb);
            this.add_from_name = (TextView) rootView.findViewById(R.id.add_from_name);
            this.add_from_unit = (TextView) rootView.findViewById(R.id.add_from_unit);
            this.add_from_job = (TextView) rootView.findViewById(R.id.add_from_job);
        }
    }
}
