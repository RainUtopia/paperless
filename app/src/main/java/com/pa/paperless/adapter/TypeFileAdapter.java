package com.pa.paperless.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pa.paperless.R;
import com.pa.paperless.bean.MeetDirFileInfo;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.FileSizeUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/11/3.
 * 文档/图片...分类使用 adapter
 */

public class TypeFileAdapter extends BaseAdapter {

    public int ITEM_COUNT = 6;
    public int PAGE_NOW = 0;
    private setLookListener mLookListener;
    private setDownListener mDownListener;

    public TypeFileAdapter(Context context, List<MeetDirFileInfo> data) {
        super(context);
        mDatas = data;
    }

    @Override
    public int getCount() {
        //  数据的总数
        int ori = ITEM_COUNT * PAGE_NOW;
        //值的总个数-前几页的个数就是这一页要显示的个数，如果比默认的值小，说明这是最后一页，只需显示这么多就可以了
        if (mDatas.size() - ori < ITEM_COUNT) {
            return mDatas.size() - ori;
        } else {
            //如果比默认的值还要大，说明一页显示不完，还要用换一页显示，这一页用默认的值显示满就可以了。
            return ITEM_COUNT;
        }
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_document_lv, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        /**
         * 这一步是重点，括号中的值是获取到item的准确索引
         */
        final MeetDirFileInfo bean = (MeetDirFileInfo) mDatas.get(i + ITEM_COUNT * PAGE_NOW);
        holder.document_item_number.setText(i + 1 + "");
        holder.document_item_filename.setText(bean.getFileName());
        holder.document_item_filesize.setText(FileSizeUtil.FormetFileSize(bean.getSize()));
        holder.document_item_uploadname.setText(bean.getUploader_name());

        final int mediaId = bean.getMediaId();
        final String fileName = bean.getFileName();
        holder.document_item_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLookListener != null) {
                    //下载媒体id
                    mLookListener.onLookListener(mediaId, fileName,bean.getSize());
                }
            }
        });
        holder.document_item_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDownListener != null) {
                    mDownListener.onDownListener(mediaId, fileName,bean.getSize());
                }
            }
        });
        return view;
    }

    public void setLookListener(setLookListener listener) {
        mLookListener = listener;
    }

    public void setDownListener(setDownListener listener) {
        mDownListener = listener;
    }

    public interface setLookListener {
        void onLookListener(int posion, String filename,long filesize);
    }

    public interface setDownListener {
        void onDownListener(int posion, String filename,long filesize);
    }

    public static class ViewHolder {
        public View rootView;
        public TextView document_item_number;
        public TextView document_item_filename;
        public TextView document_item_filesize;
        public TextView document_item_uploadname;
        public ImageView document_item_look;
        public ImageView document_item_download;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.document_item_number = (TextView) rootView.findViewById(R.id.document_item_number);
            this.document_item_filename = (TextView) rootView.findViewById(R.id.document_item_filename);
            this.document_item_filesize = (TextView) rootView.findViewById(R.id.document_item_filesize);
            this.document_item_uploadname = (TextView) rootView.findViewById(R.id.document_item_uploadname);
            this.document_item_look = (ImageView) rootView.findViewById(R.id.look);
            this.document_item_download = (ImageView) rootView.findViewById(R.id.download);
        }

    }
}

