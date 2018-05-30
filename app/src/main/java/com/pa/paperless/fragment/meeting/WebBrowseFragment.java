package com.pa.paperless.fragment.meeting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pa.paperless.R;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.pa.paperless.activity.MeetingActivity.nativeUtil;

/**
 * Created by Administrator on 2017/10/31.
 * 打开网页
 */

public class WebBrowseFragment extends BaseFragment{

    private final String TAG = "WebBrowseFragment-->";
    private WebView mWebView;
    public static boolean webView_isshowing = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_webbrowse, container, false);
        initView(inflate);
        webView_isshowing =true;
        try {
            nativeUtil.webQuery();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        return inflate;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.post_newWeb_url://收到网址
                String url = (String) message.getObject();
                mWebView.loadUrl("http://" + url);
                break;
            case IDEventMessage.NETWEB_INFORM:
                Log.e(TAG, "WebBrowseFragment.getEventMessage:  网页查询EventBus --->>> ");
                nativeUtil.webQuery();
                break;
            case IDEventF.go_back_html:
                Log.e(TAG, "WebBrowseFragment.getEventMessage :  返回上一个网页 --> ");
                mWebView.goBack();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void initView(View inflate) {
        mWebView = (WebView) inflate.findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings s = mWebView.getSettings();
        s.setBuiltInZoomControls(true);
        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setSavePassword(true);
        s.setSaveFormData(true);
        s.setJavaScriptEnabled(true);
        s.setGeolocationEnabled(true);
        s.setGeolocationDatabasePath("http://www.cvbaoli.com/webak/public/showAgreement");
        s.setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        webView_isshowing = !hidden;
        super.onHiddenChanged(hidden);
    }
}
