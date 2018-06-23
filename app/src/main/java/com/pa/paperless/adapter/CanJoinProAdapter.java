package com.pa.paperless.adapter;

/**
 * Created by Administrator on 2018/6/15.
 */

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

import static com.pa.paperless.service.FabService.canjoinProSelect;


/**
 * Created by Administrator on 2018/3/3.
 */

public class CanJoinProAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DeviceInfo> mData;
    private ItemClickListener mListener;

    public CanJoinProAdapter(List<DeviceInfo> datas) {
        mData = datas;
    }

    /**
     * 获取所有选中的人员
     *
     * @return
     */
    public DeviceInfo getCheckedIds() {
        //type 为1时 全部投影机
        if (mData.size() > 0) {
            for (int i = 0; i < canjoinProSelect.size(); i++) {
                if (canjoinProSelect.get(i)) {//当前索引项是否选中
                    return mData.get(i);
                }
            }
        }
        return null;
    }


    /**
     * 全选 按钮监听
     */
    public boolean setAllChecked() {
        //只要集合中包含false，就将该索引的位置改为true
        if (canjoinProSelect.contains(false)) {
            for (int i = 0; i < canjoinProSelect.size(); i++) {
                canjoinProSelect.set(i, true);
            }
            notifyDataSetChanged();
            return true;
        } else {//全部已经选中状态（true），就全设为false
            for (int i = 0; i < canjoinProSelect.size(); i++) {
                canjoinProSelect.set(i, false);
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
        ((ViewHolder) holder).play_btn.setSelected(canjoinProSelect.get(position));
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