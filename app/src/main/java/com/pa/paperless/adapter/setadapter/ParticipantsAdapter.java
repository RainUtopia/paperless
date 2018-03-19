package com.pa.paperless.adapter.setadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mogujie.tt.protobuf.InterfacePerson;
import com.pa.paperless.R;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.CommonlyParticipantsFragment;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 * 常用参会人
 */

public class ParticipantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<InterfacePerson.pbui_Item_PersonDetailInfo> mData;
    private ItemClickListener mListener;
    private int mCheckedPosion;

    public void removeItem(int position){
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
        //当索引大于数据大小时
        if (position > mData.size()-1) {
            CommonlyParticipantsFragment.mPosion = mData.size()-1;
        }
        if (mData.size() < 1) {
            CommonlyParticipantsFragment.isDelete = false;
        }
    }

    public ParticipantsAdapter(Context context, List<InterfacePerson.pbui_Item_PersonDetailInfo> data) {
        mContext = context;
        mData = data;
    }
    public void setCheckedId(int posion){
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.participants_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    public void setListener(ItemClickListener listener){
        mListener =listener;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        InterfacePerson.pbui_Item_PersonDetailInfo pbui_item_personDetailInfo = mData.get(position);
        String name = new String(pbui_item_personDetailInfo.getName().toByteArray());
        String job = new String(pbui_item_personDetailInfo.getJob().toByteArray());
        String comment = new String(pbui_item_personDetailInfo.getComment().toByteArray());
        String company = new String(pbui_item_personDetailInfo.getCompany().toByteArray());
        Log.e("MyLog","ParticipantsAdapter.onBindViewHolder:  pbui_item_personDetailInfo 参会人员ID：--->>> "+pbui_item_personDetailInfo.getPersonid());

        ((ViewHolder)holder).participants_number.setText(position+1+"");
        ((ViewHolder)holder).participants_name.setText(name);
        ((ViewHolder)holder).participants_unit.setText(company);
        ((ViewHolder)holder).participants_job.setText(job);
        ((ViewHolder)holder).participants_note.setText(comment);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView participants_number;
        public TextView participants_name;
        public TextView participants_unit;
        public TextView participants_job;
        public TextView participants_note;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.participants_number = (TextView) rootView.findViewById(R.id.participants_number);
            this.participants_name = (TextView) rootView.findViewById(R.id.participants_name);
            this.participants_unit = (TextView) rootView.findViewById(R.id.participants_unit);
            this.participants_job = (TextView) rootView.findViewById(R.id.participants_job);
            this.participants_note = (TextView) rootView.findViewById(R.id.participants_note);
        }

    }
}
