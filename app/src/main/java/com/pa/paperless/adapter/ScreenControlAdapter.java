package com.pa.paperless.adapter;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pa.paperless.R;
import com.pa.paperless.bean.ScreenControlBean;
import com.pa.paperless.listener.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.checks;
import static com.pa.paperless.utils.MyUtils.setAnimator;

/**
 * Created by Administrator on 2017/11/9.
 * 同屏控制Adapter 参与人 和 投影机
 */

public class ScreenControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ScreenControlBean> mData;
    private ItemClickListener mListener;


    public ScreenControlAdapter(List<ScreenControlBean> datas) {
        mData = datas;
    }

    /**
     * 获取所有选中的人员
     *
     * @return
     */
    public List<ScreenControlBean> getCheckedIds() {
        List<ScreenControlBean> checkedId = new ArrayList<>();
        for (int i = 0; i < checks.size(); i++) {
            if (checks.get(i)) {
                checkedId.add(mData.get(i));
            }
        }
        return checkedId;
    }


    /**
     * 全选 按钮监听
     */
    public boolean setAllChecked() {
        //只要集合中包含false，就将该索引的位置改为true
        if (checks.contains(false)) {
            for (int i = 0; i < checks.size(); i++) {
                checks.set(i, true);
            }
            notifyDataSetChanged();
            return true;
        } else {//全部已经选中状态（true），就全设为false
            for (int i = 0; i < checks.size(); i++) {
                checks.set(i, false);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ((ViewHolder) holder).play_btn.setText(mData.get(position).getName());
        ((ViewHolder) holder).play_btn.setSelected(checks.get(position));

        ((ViewHolder) holder).play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    Log.e("MyLog", "ScreenControlAdapter.onClick: rrrrr  --->>> ");
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
        public View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.play_btn = (Button) itemView.findViewById(R.id.palyer_name);
        }
    }

    public void setItemClick(ItemClickListener listener) {
        mListener = listener;
    }
}
