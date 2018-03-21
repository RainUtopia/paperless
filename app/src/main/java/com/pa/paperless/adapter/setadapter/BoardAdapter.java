package com.pa.paperless.adapter.setadapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pa.paperless.R;
import com.pa.paperless.bean.DevMember;
import com.pa.paperless.listener.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.checks;
import static com.pa.paperless.activity.PeletteActivity.boardCheck;


/**
 * Created by Administrator on 2017/11/9.
 * 同屏控制Adapter 参与人 和 投影机
 */

public class BoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DevMember> mData;
    private ItemClickListener mListener;


    public BoardAdapter(List<DevMember> datas) {
        mData = datas;
    }

    /**
     * 获取所有选中的人员
     *
     * @return
     */
    public List<DevMember> getCheckedIds() {
        List<DevMember> checkedId = new ArrayList<>();
        if (mData.size() > 0) {
            for (int i = 0; i < boardCheck.size(); i++) {
                if (boardCheck.get(i)) {
                    checkedId.add(mData.get(i));
                }
            }
        }
        return checkedId;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paly_rl, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).play_btn.setText(mData.get(position).getMemberInfos().getName());
        ((ViewHolder) holder).play_btn.setSelected(boardCheck.get(position));
        ((ViewHolder) holder).play_btn.setOnClickListener(new View.OnClickListener() {
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
