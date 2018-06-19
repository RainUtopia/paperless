package com.pa.paperless.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pa.paperless.R;
import com.pa.paperless.bean.VoteInfo;
import com.pa.paperless.bean.VoteOptionsInfo;
import com.pa.paperless.listener.ItemClickListener;

import java.util.List;

import static com.pa.paperless.utils.MyUtils.get10To2;

/**
 * Created by Administrator on 2017/11/14.
 * 投票查询 adapter
 */

public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.ViewHolder> {

    private final List<VoteInfo> mData;
    private final Context mContext;
    private ItemClickListener mListener;
    private int mCheckedPosion;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.vote_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    public VoteAdapter(Context context, List<VoteInfo> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //  item的序号
        int number = position + 1;
        VoteInfo voteInfo = mData.get(position);
        //  投票内容文本
        String content = voteInfo.getContent();
        //  投票主题类别
        int maintype = voteInfo.getMaintype();
        switch (maintype) {
            case 0://投票
                content += "  投票";
                break;
            case 1://选举
                content += "  选举";
                break;
            case 2://问卷调查
                content += "  问卷调查";
                break;
        }
        //  投票选项个数文本
        int type = voteInfo.getType();
        switch (type) {
            case 0://多选
                content += "（多选";
                break;
            case 1://单选
                content += "（单选";
                break;
            case 2://多选 5选4
                content += "（5选4";
                break;
            case 3://多选 5选3
                content += "（5选3";
                break;
            case 4://多选 5选2
                content += "（5选2";
                break;
            case 5://多选 3选2
                content += "（3选2";
                break;
        }
        //  投票是否记名文本
        int mode = voteInfo.getMode();
        switch (mode) {
            case 0://匿名投票
                content += "，匿名";
                break;
            case 1://记名投票
                content += "，记名";
                break;
        }
        //  投票当前状态文本
        int votestate = voteInfo.getVotestate();
        switch (votestate) {
            case 0://未发起
                content += "，未发起）";
                holder.vote_item_title.setText(number + ".  " + content);
                break;
            case 1://正在进行
                content += "，正在进行）";
                holder.vote_item_title.setText(number + ".  " + content);
                break;
            case 2://已经结束
                content += "，已经结束）";
                holder.vote_item_title.setText(number + ".  " + content);
                break;
        }
        List<VoteOptionsInfo> optionInfo = voteInfo.getOptionInfo();
        //  多少个选项
        int size = optionInfo.size();
        for (int i = 0; i < size; i++) {
            VoteOptionsInfo voteOptionsInfo = optionInfo.get(i);
            String text = voteOptionsInfo.getText();
            int selcnt = voteOptionsInfo.getSelcnt();
            String option = text + "  " + selcnt;
            if (i == 0) {
                holder.vote_option_1.setText(option);
            }
            if (i == 1) {
                holder.vote_option_2.setText(option);
            }
            if (i == 2) {
                holder.vote_option_3.setText(option);
            }
            if (i == 3) {
                holder.vote_option_4.setText(option);
            }
            if (i == 4) {
                holder.vote_option_5.setText(option);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            }

        });

        /** ************ ******  item设置选中效果  ****** ************ **/
        if (position == mCheckedPosion) {
            int color = mContext.getResources().getColor(R.color.CardBackgroundColor);
            holder.itemView.setBackgroundColor(color);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setCheckedId(int posion) {
        mCheckedPosion = posion;
        notifyDataSetChanged();
    }

    public void setItemListener(ItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView vote_item_title;
        public TextView vote_option_1;
        public TextView vote_option_2;
        public TextView vote_option_3;
        public TextView vote_option_4;
        public TextView vote_option_5;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.vote_item_title = (TextView) rootView.findViewById(R.id.vote_item_title);
            this.vote_option_1 = (TextView) rootView.findViewById(R.id.vote_option_1);
            this.vote_option_2 = (TextView) rootView.findViewById(R.id.vote_option_2);
            this.vote_option_3 = (TextView) rootView.findViewById(R.id.vote_option_3);
            this.vote_option_4 = (TextView) rootView.findViewById(R.id.vote_option_4);
            this.vote_option_5 = (TextView) rootView.findViewById(R.id.vote_option_5);
        }

    }
}
