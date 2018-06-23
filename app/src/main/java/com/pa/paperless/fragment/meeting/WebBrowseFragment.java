package com.pa.paperless.fragment.meeting;

import android.content.Context;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.pa.paperless.R;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.pa.paperless.service.NativeService.nativeUtil;

/**
 * Created by Administrator on 2017/10/31.
 * 打开网页
 */

public class WebBrowseFragment extends BaseFragment implements View.OnClickListener {

    private final String TAG = "WebBrowseFragment-->";
    private final String HOME_URL = "http://www.baidu.com/";
    private WebView mWebView;
    public static boolean webView_isshowing = false;//用于判断用户点击返回键时该做什么
    private ImageButton back_pre;
    private ImageButton goto_nex;
    private ImageButton home;
    private EditText edt_url;
    private Button goto_url;
    private List<String> histroyURL;//保存所有网站（历史记录）

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.right_webbrowse, container, false);
        initView(inflate);
        initEvent();
        webView_isshowing = true;
        try {
            nativeUtil.webQuery();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return inflate;
    }


    private void initEvent() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
//        webSettings.setGeolocationDatabasePath("http://www.cvbaoli.com/webak/public/showAgreement");
        webSettings.setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                edt_url.setText(view.getUrl());//设置当前的网址内容
                Log.e(TAG, "WebBrowseFragment.shouldOverrideUrlLoading :  当前的网址 --> " + view.getUrl());
                view.loadUrl(uriHttpFirst(url));
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();//接受所有网站的证书
                super.onReceivedSslError(view, handler, error);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.net_url://收到网址
                InterfaceBase.pbui_meetUrl object = (InterfaceBase.pbui_meetUrl) message.getObject();
                String url = MyUtils.getBts(object.getUrl());
                if (url.equals("")) {
                    break;
                }
                if (histroyURL == null) {
                    histroyURL = new ArrayList<>();
                }
                histroyURL.add(uriHttpFirst(url));
                mWebView.loadUrl(uriHttpFirst(url));
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

    //地址HTTP协议判断，无HTTP打头的，增加http://，并返回。
    private String uriHttpFirst(String strUri) {
        if (strUri.indexOf("http://", 0) != 0 && strUri.indexOf("https://", 0) != 0) {
            strUri = "http://" + strUri;
        }
        return strUri;
    }

    @Override
    public void onDestroy() {
        Log.i("F_life", "WebBrowseFragment.onDestroy :   --> ");
        super.onDestroy();
    }


    private void initView(View inflate) {
        mWebView = (WebView) inflate.findViewById(R.id.web_view);
        mWebView.setOnClickListener(this);
        back_pre = (ImageButton) inflate.findViewById(R.id.back_pre);
        back_pre.setOnClickListener(this);
        goto_nex = (ImageButton) inflate.findViewById(R.id.goto_nex);
        goto_nex.setOnClickListener(this);
        home = (ImageButton) inflate.findViewById(R.id.home);
        home.setOnClickListener(this);
        edt_url = (EditText) inflate.findViewById(R.id.edt_url);
        edt_url.setOnClickListener(this);
        goto_url = (Button) inflate.findViewById(R.id.goto_url);
        goto_url.setOnClickListener(this);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i("F_life", "WebBrowseFragment.onHiddenChanged :   --> " + hidden);
        webView_isshowing = !hidden;
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_pre://上一个
                mWebView.goBack();
                break;
            case R.id.goto_nex://下一个
                mWebView.goForward();
                break;
            case R.id.home://回到主页
                mWebView.loadUrl(HOME_URL);
                break;
            case R.id.goto_url://前往
                String url = edt_url.getText().toString();
                mWebView.loadUrl(uriHttpFirst(url));
                break;
        }
    }


    @Override
    public void onAttach(Context context) {
        Log.i("F_life", "WebBrowseFragment.onAttach :   --> ");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "WebBrowseFragment.onCreate :   --> ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("F_life", "WebBrowseFragment.onActivityCreated :   --> ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i("F_life", "WebBrowseFragment.onStart :   --> ");
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i("F_life", "WebBrowseFragment.onResume :   --> ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("F_life", "WebBrowseFragment.onPause :   --> ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("F_life", "WebBrowseFragment.onStop :   --> ");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("F_life", "WebBrowseFragment.onDestroyView :   --> ");
        super.onDestroyView();
    }


    @Override
    public void onDetach() {
        Log.i("F_life", "WebBrowseFragment.onDetach :   --> ");
        super.onDetach();
    }
}
