package com.pa.paperless.adapter.setadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mogujie.tt.protobuf.InterfaceMain;
import com.pa.paperless.R;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.IpInfo;
import com.pa.paperless.bean.ResInfo;
import com.pa.paperless.fragment.manage.secondfloor.s.setting.DeviceManagementFragment;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

import static com.pa.paperless.fragment.manage.secondfloor.s.setting.DeviceManagementFragment.mPosion;

/**
 * 系统设置-设备管理-adapter
 */

public class DeviceManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final List<InterfaceMain.pbui_Item_DeviceDetailInfo> mData;
    private ItemClickListener mListener;
    private int mDataCount;
    private int mCheckedPosion;

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
        //当索引大于数据大小时
        if (position > mData.size()-1) {
            mPosion = mData.size()-1;
        }
        if (mData.size() < 1) {
            DeviceManagementFragment.isDelete = false;
        }
    }

    public void setCheckedId(int posion) {
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }

    public DeviceManageAdapter(Context context, List<InterfaceMain.pbui_Item_DeviceDetailInfo> data) {
        mContext = context;
        mData = data;
    }

    public void setListener(ItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.device_tiem, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        InterfaceMain.pbui_Item_DeviceDetailInfo pbui_item_deviceDetailInfo = mData.get(position);
        ((ViewHolder) holder).device_number.setText(position + 1 + "");
        String name = new String(pbui_item_deviceDetailInfo.getDevname().toByteArray());
        ((ViewHolder) holder).device_name.setText(name);
        //IP信息  先设置第一个
        InterfaceMain.pbui_SubItem_DeviceIpAddrInfo ipinfo = pbui_item_deviceDetailInfo.getIpinfo(0);
        String ip = new String(ipinfo.getIp().toByteArray());
        ((ViewHolder) holder).device_ip.setText(ip);

        //资料信息
        List<InterfaceMain.pbui_SubItem_DeviceResInfo> resinfoList = pbui_item_deviceDetailInfo.getResinfoList();
        for (int i = 0; i < resinfoList.size(); i++) {
            InterfaceMain.pbui_SubItem_DeviceResInfo pbui_subItem_deviceResInfo = resinfoList.get(i);
            int playstatus = pbui_subItem_deviceResInfo.getPlaystatus();
            int triggerId = pbui_subItem_deviceResInfo.getTriggerId();
            int val = pbui_subItem_deviceResInfo.getVal();
            int val2 = pbui_subItem_deviceResInfo.getVal2();
        }
        //设置在线状态
        int netState = pbui_item_deviceDetailInfo.getNetstate();
        if(netState == 0){
            ((ViewHolder) holder).device_state.setText("离线");
        }else {
            ((ViewHolder) holder).device_state.setText("在线");
        }
//        ((ViewHolder) holder).device_type.setText(mData.get(position).get());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.itemView, holder.getLayoutPosition());
            }
        });
        /** ************ ******  item设置选中效果  ****** ************ **/
        if (position == mCheckedPosion) {
            int color = mContext.getResources().getColor(R.color.ItemCheckedColor);
            holder.itemView.setBackgroundColor(color);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        mDataCount = mData != null ? mData.size() : 0;
        return mDataCount;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView device_number;
        public TextView device_name;
        public TextView device_type;
        public TextView device_ip;
        public TextView device_state;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.device_number = (TextView) rootView.findViewById(R.id.device_number);
            this.device_name = (TextView) rootView.findViewById(R.id.device_name);
            this.device_type = (TextView) rootView.findViewById(R.id.device_type);
            this.device_ip = (TextView) rootView.findViewById(R.id.device_ip);
            this.device_state = (TextView) rootView.findViewById(R.id.device_state);
        }

    }
}
