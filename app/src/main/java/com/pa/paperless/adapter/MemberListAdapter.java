package com.pa.paperless.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.mogujie.tt.protobuf.InterfaceMember;
import com.pa.paperless.R;
import com.pa.paperless.bean.CheckedMemberIds;
import com.pa.paperless.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/22.
 * 聊天参会人列表
 */

public class MemberListAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<InterfaceMember.pbui_Item_MemberDetailInfo> datas;
    //存放是否选中结果集
    public static List<Boolean> itemChecked = new ArrayList<>();
    //存放选中的CheckBox文本
    public static List<String> names = new ArrayList<>();
    private List<CheckedMemberIds> mCheckedMemberIds = new ArrayList<>();
    private List<String> checkName;

    public MemberListAdapter(Context context, List<InterfaceMember.pbui_Item_MemberDetailInfo> data) {
        mContext = context;
        datas = data;
        //初始化：设置默认都是未选的
        for (int i = 0; i < this.datas.size(); i++) {
            itemChecked.add(false);
            //初始化names集合，全部设为空字符串
            names.add(i, "");
        }
    }

    @Override
    public int getCount() {
        return datas != null ? datas.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return datas != null ? datas.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * 获取全部选中的参会人ID
     */
    public List<CheckedMemberIds> getCheckedId() {
        mCheckedMemberIds.clear();
        for (int i = 0; i < itemChecked.size(); i++) {
            //因为itemChecked 的索引和 数据的一一对应
            if (itemChecked.get(i)) {
                //获取选中的人员的ID
                int personid = datas.get(i).getPersonid();
                mCheckedMemberIds.add(new CheckedMemberIds(personid));
            }
        }
        return mCheckedMemberIds;
    }

    public List<String> getCheckedName() {
        checkName = new ArrayList<>();
        if (!itemChecked.contains(false)) {
            //如果全部选中
            checkName.add("全体人员");
            return checkName;
        }
        for (int i = 0; i < itemChecked.size(); i++) {
            //因为itemChecked 的索引和 数据的一一对应
            if (itemChecked.get(i)) {
                //获取选中的人员的名字
                String name = MyUtils.getBts(datas.get(i).getName());
                checkName.add(name);
                if (checkName.size() > 3) {
                    //如果 checkName 中的名字(选中的个数)超过3个 就在最后添加省略号并直接 return
                    checkName.set(checkName.size() - 1, "... 等人");
                    return checkName;
                }
            }
        }
        return checkName;
    }

    /**
     * 全选 按钮监听
     */
    public void setAllChecked() {
        //只要集合中包含false，就将该索引的位置改为true
        if (itemChecked.contains(false)) {
            for (int i = 0; i < itemChecked.size(); i++) {
                itemChecked.set(i, true);
            }
        } else {//全部已经选中状态（true），就全设为false
            for (int i = 0; i < itemChecked.size(); i++) {
                itemChecked.set(i, false);
            }
        }
        notifyDataSetChanged();
    }

    //反选
    public void setInvert() {
        if (datas != null) {
            for (int i = 0; i < datas.size(); i++) {
                itemChecked.set(i, !itemChecked.get(i));
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final InterfaceMember.pbui_Item_MemberDetailInfo memberInfo = datas.get(i);
        String name = new String(memberInfo.getName().toByteArray());
        holder.chat_item_cb.setText(name);
        holder.chat_item_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //每次点击CheckBox，就将集合中该索引位置设置为反值
                itemChecked.set(i, !itemChecked.get(i));
                //设置names集合中的值
                setNames(itemChecked.get(i), i, memberInfo);
            }
        });
        Log.e("MemberAd", "com.pa.paperless.adapter_MemberListAdapter.getView :" +
                "   --->>>itemChecked.size() " + itemChecked.size() + "   当前的索引：" + i);
        if (itemChecked.size() > 0) {
            holder.chat_item_cb.setChecked(itemChecked.get(i));
            setNames(holder.chat_item_cb.isChecked(), i, memberInfo);
        }
        return view;
    }

    public void setNames(boolean checked, int i, InterfaceMember.pbui_Item_MemberDetailInfo bean) {
        String name = new String(bean.getName().toByteArray());
        if (checked) {
            names.set(i, name);
        } else {
            names.set(i, "");
        }
    }

    public static class ViewHolder {
        public View rootView;
        public CheckBox chat_item_cb;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.chat_item_cb = (CheckBox) rootView.findViewById(R.id.chat_item_cb);
        }

    }
}
