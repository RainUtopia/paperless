package com.pa.paperless.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pa.paperless.R;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.listener.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.checkProAll;
import static com.pa.paperless.activity.MeetingActivity.checkProOL;

/**
 * Created by Administrator on 2018/3/3.
 */

public class OnLineProjectorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int Typea;
    private List<DeviceInfo> mData;
    private ItemClickListener mListener;


    public OnLineProjectorAdapter(List<DeviceInfo> datas,int type) {
        mData = datas;
        Typea = type;
    }

    /**
     * 获取所有选中的人员
     *
     * @return
     */
    public List<DeviceInfo> getCheckedIds(int type) {
        List<DeviceInfo> checkedId = new ArrayList<>();
        //type 为1时 全部投影机
        if(type == 1) {
            if (mData.size() > 0) {
                for (int i = 0; i < checkProAll.size(); i++) {
                    if (checkProAll.get(i)) {
                        checkedId.add(mData.get(i));
                    }
                }
            }
        }else {
            if (mData.size() > 0) {
                for (int i = 0; i < checkProOL.size(); i++) {
                    if (checkProOL.get(i)) {
                        checkedId.add(mData.get(i));
                    }
                }
            }

        }
        return checkedId;
    }


    /**
     * 全选 按钮监听
     */
    public boolean setAllChecked() {
        //只要集合中包含false，就将该索引的位置改为true
        if (checkProAll.contains(false)) {
            for (int i = 0; i < checkProAll.size(); i++) {
                checkProAll.set(i, true);
            }
            notifyDataSetChanged();
            return true;
        } else {//全部已经选中状态（true），就全设为false
            for (int i = 0; i < checkProAll.size(); i++) {
                checkProAll.set(i, false);
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

        ((ViewHolder) holder).play_btn.setText(mData.get(position).getDevName());
        // 为1 是全部投影机
        if(Typea == 1) {
            ((ViewHolder) holder).play_btn.setSelected(checkProAll.get(position));
        }else {
            ((ViewHolder) holder).play_btn.setSelected(checkProOL.get(position));
        }

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