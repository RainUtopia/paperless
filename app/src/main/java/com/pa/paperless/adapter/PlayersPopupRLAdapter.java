package com.pa.paperless.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.MyUtils;

import java.util.List;


/**
 * Created by Administrator on 2017/11/9.
 */

public class PlayersPopupRLAdapter extends RecyclerView.Adapter<PlayersPopupRLAdapter.ViewHolder> {

    private  List<String> mData;
    private ItemClickListener mListener;
    private boolean isCheckAll = false;
    public PlayersPopupRLAdapter(List<String> datas) {
        mData = datas;
    }
    public void setCheckAll(boolean tf){
        isCheckAll = tf ;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paly_rl, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.play_btn.setText(mData.get(position));
        holder.play_btn.setSelected(isCheckAll);
        holder.play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean tf = holder.play_btn.isSelected();
                holder.play_btn.setSelected(!tf);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null){
                    int layoutPosition = holder.getLayoutPosition();
                    mListener.onItemClick(holder.itemView,layoutPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData!=null?mData.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button play_btn;
        public ViewHolder(View itemView) {
            super(itemView);
            play_btn = itemView.findViewById(R.id.palyer_name);
        }
    }
    public void setMyItemClickListener(ItemClickListener listener){
        mListener = listener;
    }
}
