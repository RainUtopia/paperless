package com.pa.paperless.adapter.setadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.protobuf.ByteString;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.bean.AdminInfo;
import com.pa.paperless.bean.AdminsBean;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.SecretaryManagementFragment;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 */

public class SecretaryTopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final List<InterfaceMain.pbui_Item_AdminDetailInfo> mData;
    private ItemClickListener mListener;
    private int mCheckedPosion;

    public void setListener(ItemClickListener listener) {
        mListener = listener;
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
        //当索引大于数据大小时
        if (position > mData.size() - 1) {
            SecretaryManagementFragment.mPosion = mData.size() - 1;
        }
        if (mData.size() < 1) {
            SecretaryManagementFragment.isDelete = false;
        }
    }

    public void setCheckedId(int posion) {
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }

    public void inserted(int position, InterfaceMain.pbui_Item_AdminDetailInfo data) {
        mData.add(position, data);
        notifyItemInserted(position);
    }

    public SecretaryTopAdapter(Context context, List<InterfaceMain.pbui_Item_AdminDetailInfo> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.secretary_top_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        InterfaceMain.pbui_Item_AdminDetailInfo pbui_item_adminDetailInfo = mData.get(position);
        String adminName = new String(pbui_item_adminDetailInfo.getAdminname().toByteArray());
        String pwd = new String(pbui_item_adminDetailInfo.getPw().toByteArray());
        String comment = new String(pbui_item_adminDetailInfo.getComment().toByteArray());

        ((ViewHolder) holder).srcretary_item_name.setText(adminName);
        ((ViewHolder) holder).srcretary_item_password.setText(pwd);
        ((ViewHolder) holder).srcretary_item_note.setText(comment);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.itemView, holder.getLayoutPosition());
            }
        });
        if (position == mCheckedPosion) {
            int color = mContext.getResources().getColor(R.color.ItemCheckedColor);
            holder.itemView.setBackgroundColor(color);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView srcretary_item_name;
        public TextView srcretary_item_password;
        public TextView srcretary_item_note;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.srcretary_item_name = (TextView) rootView.findViewById(R.id.srcretary_item_name);
            this.srcretary_item_password = (TextView) rootView.findViewById(R.id.srcretary_item_password);
            this.srcretary_item_note = (TextView) rootView.findViewById(R.id.srcretary_item_note);
        }

    }
}
