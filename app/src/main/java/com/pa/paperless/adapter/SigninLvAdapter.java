package com.pa.paperless.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pa.paperless.R;
import com.pa.paperless.bean.SigninBean;

import java.util.List;

import static com.pa.paperless.fragment.meeting.SigninFragment.nowPage;
import static com.pa.paperless.fragment.meeting.SigninFragment.pageItem;

/**
 * Created by Administrator on 2017/11/1.
 */

public class SigninLvAdapter extends BaseAdapter {

    private List<SigninBean> mDataes;

    @Override
    public int getCount() {
        //  第0页到当前页显示满的所有数据个数 = 每页的个数 x 当前页数
        int ori = pageItem * nowPage;
        //这一页要显示的个数 ： 值的总个数-前几页的个数，如果比默认的值小，说明这是最后一页，只需显示这么多就可以了
        if (mDataes.size() - ori < pageItem) {
            return mDataes.size() - ori;
        } else {
            //如果比默认的值还要大，说明一页显示不完，还要用换一页显示，这一页用默认的值显示满就可以了。
            return pageItem;
        }
    }

    public SigninLvAdapter(Context context, List<SigninBean> data) {
        super(context);
        mDataes = data;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.right_signin_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //要显示的是当前的位置+前几页已经显示的位置个数的对应的位置上的值。
        SigninBean bean = (SigninBean) mDataes.get(i + (int) (nowPage * pageItem));
        holder.signin_item_number.setText(bean.getSignin_num());
        holder.signin_item_name.setText(bean.getSignin_name());
        holder.signin_item_signtime.setText(bean.getSignin_date());
        if(!(holder.signin_item_signtime.getText().toString().isEmpty())){
            holder.signin_item_signstate.setText("已签到");
        }
        else {
            holder.signin_item_signstate.setText(" ");
        }
        return view;
    }

    class ViewHolder {
        public View rootView;
        public TextView signin_item_number;
        public TextView signin_item_name;
        public TextView signin_item_signtime;
        public TextView signin_item_signstate;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.signin_item_number = (TextView) rootView.findViewById(R.id.signin_item_number);
            this.signin_item_name = (TextView) rootView.findViewById(R.id.signin_item_name);
            this.signin_item_signtime = (TextView) rootView.findViewById(R.id.signin_item_signtime);
            this.signin_item_signstate = (TextView) rootView.findViewById(R.id.signin_item_signstate);
        }
    }
}
