package com.pa.paperless.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pa.paperless.R;
import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.bean.ScreenControlBean;
import com.pa.paperless.listener.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.setIsAllSelect;


/**
 * Created by Administrator on 2017/11/9.
 * 同屏控制Adapter 参与人 和 投影机
 */

public class ScreenControlAdapter extends RecyclerView.Adapter<ScreenControlAdapter.ViewHolder> {

    private List<ScreenControlBean> mData;
    private ItemClickListener mListener;
    //存放是否选中结果集
    private List<Boolean> itemChecked;


    public ScreenControlAdapter(List<ScreenControlBean> datas) {
        mData = datas;
        if (mData != null) {
            itemChecked = new ArrayList<>();
            for (int i = 0; i < mData.size(); i++) {
                itemChecked.add(false);
            }
        }
    }

    /**
     * 获取选中人的ID
     *
     * @return
     */
    public List<Integer> getCheckedIds() {
        List<Integer> checkedId = new ArrayList<>();
        for (int i = 0; i < itemChecked.size(); i++) {
            if (itemChecked.get(i)) {
                checkedId.add(mData.get(i).getId());
            }
        }
        return checkedId;
    }

    /**
     * 全选 按钮监听
     */
    public boolean setAllChecked() {
        //只要集合中包含false，就将该索引的位置改为true
        if (itemChecked.contains(false)) {
            for (int i = 0; i < itemChecked.size(); i++) {
                itemChecked.set(i, true);
            }
            notifyDataSetChanged();
            return true;
        } else {//全部已经选中状态（true），就全设为false
            for (int i = 0; i < itemChecked.size(); i++) {
                itemChecked.set(i, false);
            }
            notifyDataSetChanged();
            return false;
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paly_rl, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.play_btn.setText(mData.get(position).getName());
        holder.play_btn.setSelected(MeetingActivity.checks.get(position));

//        holder.play_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean e = !itemChecked.get(position);
//                itemChecked.set(position, e);
//                holder.play_btn.setSelected(e);
//                Log.e("MyLog", "ScreenControlAdapter.onClick: 点击之后  --->>> " + itemChecked.get(position));
//                //包含false ：其中有false  不包含false：其中没有false
//                setIsAllSelect(!itemChecked.contains(false));
//                notifyDataSetChanged();
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button play_btn;

        public ViewHolder(View itemView) {
            super(itemView);
            play_btn = itemView.findViewById(R.id.palyer_name);
        }
    }

    public void setItemClickListener(ItemClickListener listener) {
        mListener = listener;
    }
}
