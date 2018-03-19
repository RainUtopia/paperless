package com.pa.paperless.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pa.paperless.R;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

//import static com.pa.paperless.fragment.meeting.VideoFragment.checkStreams;

/**
 * Created by Administrator on 2017/11/9.
 */

public class VideoItemAdapter extends RecyclerView.Adapter<VideoItemAdapter.ViewHolder> {


    private List<DeviceInfo> mDatas;
    private ItemClickListener mListener;

    public VideoItemAdapter(List<DeviceInfo> data) {
        mDatas = data;
    }

    /**
     * 获取选中的流
     * @return
     */
//    public List<InterfaceMain.pbui_Item_MeetVideoDetailInfo> getChecked() {
//        List<InterfaceMain.pbui_Item_MeetVideoDetailInfo> checkedStreams = new ArrayList<>();
//        for (int i = 0; i < checkStreams.size(); i++) {
//            if (checkStreams.get(i)) {
//                checkedStreams.add(mDatas.get(i));
//            }
//        }
//        return checkedStreams;
//    }


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
    public void onBindViewHolder(final VideoItemAdapter.ViewHolder holder, int position) {
        holder.name_tv.setText(mDatas.get(position).getDevName());
//        holder.name_tv.setSelected(checkStreams.get(position));
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
