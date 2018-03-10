package com.pa.paperless.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.activity.MeetingActivity;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.activity.MeetingActivity.checkedJoinMember;
import static com.pa.paperless.activity.MeetingActivity.checkedJoinProjector;

/**
 * Created by Administrator on 2018/3/10.
 */

public class JoinAdapter extends RecyclerView.Adapter<JoinAdapter.ViewHolder> {

    private final List<InterfaceMain.pbui_Item_DeviceResPlay> mData;
    private ItemClickListener mListener;

    public JoinAdapter(List<InterfaceMain.pbui_Item_DeviceResPlay> data) {
        mData = data;
    }

    public List<InterfaceMain.pbui_Item_DeviceResPlay> getCheckeds(int type) {
        List<InterfaceMain.pbui_Item_DeviceResPlay> checked = new ArrayList<>();
        if (mData.size() > 0) {
            if (type == 1) {
                for (int i = 0; i < checkedJoinMember.size(); i++) {
                    if (checkedJoinMember.get(i)) {
                        checked.add(mData.get(i));
                    }
                }
            } else {
                for (int i = 0; i < checkedJoinProjector.size(); i++) {
                    if (checkedJoinProjector.get(i)) {
                        checked.add(mData.get(i));
                    }
                }
            }
        }
        return checked;
    }

    public void setItemListener(ItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public JoinAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paly_rl, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final JoinAdapter.ViewHolder holder, int position) {
        holder.palyer_name.setText(MyUtils.getBts(mData.get(position).getName()));
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public Button palyer_name;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.palyer_name = (Button) rootView.findViewById(R.id.palyer_name);
        }

    }
}
