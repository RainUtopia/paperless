package com.pa.paperless.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.bean.AgendContext;

import java.util.List;

/**
 * Created by Administrator on 2017/11/1.
 * meet 议程 Adapter
 */

public class AgendaAdapter extends BaseAdapter {

    public AgendaAdapter(Context context, List<AgendContext> data) {
        super(context);
        mDatas = data;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = mInflater.inflate(R.layout.item_agenda_lv, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        AgendContext bean = (AgendContext) mDatas.get(i);
        holder.agenda_item_number.setText(i+1+"");
        holder.agenda_item_data.setText(bean.getTime());
        holder.agenda_item_theme.setText(bean.getText());
        return view;
    }

    public static class ViewHolder {
        public View rootView;
        public TextView agenda_item_number;
        public TextView agenda_item_data;
        public TextView agenda_item_theme;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.agenda_item_number = (TextView) rootView.findViewById(R.id.agenda_item_number);
            this.agenda_item_data = (TextView) rootView.findViewById(R.id.agenda_item_data);
            this.agenda_item_theme = (TextView) rootView.findViewById(R.id.agenda_item_theme);
        }

    }
}
