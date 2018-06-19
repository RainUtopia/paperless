package com.pa.paperless.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mogujie.tt.protobuf.InterfaceVideo;
import com.pa.paperless.R;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.VideoInfo;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/11/9.
 * 视屏播放页面 Adapter
 */

public class VideoItemAdapter extends RecyclerView.Adapter<VideoItemAdapter.ViewHolder> {


    private List<VideoInfo> mDatas;
    private ItemClickListener mListener;
    private final Context mCxt;
    //存放是否选中结果集
    public static List<Boolean> VideoStreamChecked;
    private int mCheckedPosion;

    public void setCheckedId(int posion) {
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }

    public VideoItemAdapter(Context cxt, List<VideoInfo> data) {
        mDatas = data;
        mCxt = cxt;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItemListener(ItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public VideoItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final VideoItemAdapter.ViewHolder holder, final int position) {
        holder.name_tv.setText(mDatas.get(position).getName() + " | " + MyUtils.getBts(mDatas.get(position).getVideoInfo().getName()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                    //一点击就设置反值
//                    VideoStreamChecked.set(position, !VideoStreamChecked.get(position));
                }
            }
        });
        /** ************ ******  item设置选中效果  ****** ************ **/
        if (position == mCheckedPosion) {
            holder.name_tv.setSelected(true);
            VideoStreamChecked.set(position, true);
            for (int i = 0; i < VideoStreamChecked.size(); i++) {
                if (i != position) {
                    VideoStreamChecked.set(i, false);
                }
            }
        } else {
            holder.name_tv.setSelected(false);
        }
    }


    /**
     * 获取所有选中的流
     *
     * @return
     */
    public List<VideoInfo> getCheckedItemInfo() {
        List<VideoInfo> checkedItemInfo = new ArrayList<>();
        for (int i = 0; i < VideoStreamChecked.size(); i++) {
            if (VideoStreamChecked.get(i)) {//如果当前的是选中的
                checkedItemInfo.add(mDatas.get(i));
            }
        }
        return checkedItemInfo;
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public Button name_tv;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.name_tv = (Button) rootView.findViewById(R.id.name_tv);
        }

    }
}
