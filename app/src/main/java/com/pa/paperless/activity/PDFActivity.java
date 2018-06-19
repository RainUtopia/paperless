package com.pa.paperless.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.pa.paperless.R;
import com.pa.paperless.utils.ScreenUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PDFActivity extends AppCompatActivity {

    private ViewPager vpPdf;

    private LayoutInflater mInflater;
    private ParcelFileDescriptor mDescriptor;
    private PdfRenderer mRenderer;

    private String filepath;
    private TextView page_tv;
    private int mPosition;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                page_tv.setText(mPosition + 1 + " / " + mRenderer.getPageCount());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        page_tv = (TextView) findViewById(R.id.page_tv);
        filepath = getIntent().getStringExtra("pdffilepath");
        Log.e(TAG, "PDFActivity.onCreate :  PDFfilepath --> " + filepath);
        mInflater = LayoutInflater.from(this);
        vpPdf = (ViewPager) findViewById(R.id.vpager);
        File file = new File(filepath);
        try {
            openRender(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openRender(File file) throws IOException {
//        File file = new File(getExternalCacheDir(), FILE_NAME);
        if (!file.exists()) {
            //复制文件到本地存储
//            InputStream asset = getAssets().open(FILE_NAME);
            InputStream asset = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];

            int size;
            while ((size = asset.read(buffer)) != -1) {
                fos.write(buffer, 0, size);
            }
            asset.close();
            fos.close();
        }

        //初始化PdfRender
        mDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        if (mDescriptor != null) {
            mRenderer = new PdfRenderer(mDescriptor);
        }
        page_tv.setText("1 / " + mRenderer.getPageCount() + "");
        //初始化ViewPager的适配器并绑定
        MyAdapter adapter = new MyAdapter();
        vpPdf.setAdapter(adapter);
        /** **** **  添加监听器获取显示中的索引  ** **** **/
        vpPdf.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "PDFActivity.onPageSelected :  当前显示 --> " + position);
                mPosition = position;
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private final String TAG = "VPageActivity-->";

    @Override
    protected void onDestroy() {
        //销毁页面的时候释放资源,习惯
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void closeRenderer() throws IOException {
        mRenderer.close();
        mDescriptor.close();
    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mRenderer.getPageCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mInflater.inflate(R.layout.item_pdf, null);
            PhotoView pvPdf = view.findViewById(R.id.iv_pdf);
//            pvPdf.setZoomable(true);
//            pvPdf.enable();
//            pvPdf.canZoom();
            if (getCount() <= position) {
                return view;
            }
            PdfRenderer.Page currentPage = mRenderer.openPage(position);
//            Bitmap bitmap = Bitmap.createBitmap(1080, 1760, Bitmap.Config.ARGB_8888);
            int screenWidth = ScreenUtils.getScreenWidth(getApplicationContext());
            int contentHeight = ScreenUtils.getContentHeight(getApplicationContext());
            Bitmap bitmap = Bitmap.createBitmap(screenWidth, contentHeight, Bitmap.Config.ARGB_8888);
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            pvPdf.setImageBitmap(bitmap);
            //关闭当前Page对象
            currentPage.close();
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.e(TAG, "MyAdapter.destroyItem :  销毁 --> " + position);
            //销毁需要销毁的视图
            container.removeView((View) object);
        }
    }
}
