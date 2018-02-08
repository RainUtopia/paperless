package com.pa.paperless.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.bean.VoteBean;

import java.util.List;

/**
 * Created by Administrator on 2018/2/8.
 */

public class VoteInfoPopAdapter extends RecyclerView.Adapter<VoteInfoPopAdapter.ViewHolder> {

    private final Context mContext;
    private final List<VoteBean> mData;

    public VoteInfoPopAdapter(Context context, List<VoteBean> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public VoteInfoPopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.vote_pop_item, parent, false);
        ViewHolder holder = new ViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.number_vote_pop.setText(position + 1 + "");
        holder.name_vote_pop.setText(mData.get(position).getName());
        holder.choose_vote_pop.setText(mData.get(position).getChoose());
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView number_vote_pop;
        public TextView name_vote_pop;
        public TextView choose_vote_pop;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.number_vote_pop = (TextView) rootView.findViewById(R.id.number_vote_pop);
            this.name_vote_pop = (TextView) rootView.findViewById(R.id.name_vote_pop);
            this.choose_vote_pop = (TextView) rootView.findViewById(R.id.choose_vote_pop);
        }

    }
}
