package com.mylive.live.view.web;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.mylive.live.BuildConfig;
import com.mylive.live.R;
import com.mylive.live.arch.annotation.JsBridgeApi;
import com.mylive.live.arch.jsbrige.JsBridgeWebViewClient;
import com.mylive.live.base.BaseActivity;
import com.mylive.live.databinding.ActivityWebBinding;
import com.mylive.live.utils.ToastUtils;

/**
 * Created by Developer Zailong Shi on 2019-07-09.
 */
public class WebActivity extends BaseActivity {

    private ActivityWebBinding binding;
    private JsBridgeWebViewClient jsBridge;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra("url");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web);
        jsBridge = new JsBridgeWebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                binding.navigationBar.setTitle("正在打开...");
            }

            @Override
            public void onPageFinished(WebView view, JsBridgeWebViewClient client, String url) {
                super.onPageFinished(view, client, url);
                binding.navigationBar.setTitle(view.getTitle());
            }
        };
        jsBridge.addJsBridgeApi(new JsBridgeApiImpl());
        binding.webView.setWebViewClient(jsBridge);
        binding.webView.loadUrl(url);
    }

    private class JsBridgeApiImpl {

        @JsBridgeApi("version")
        public String getVersion(String params) {
            return params + BuildConfig.VERSION_NAME;
        }

        @JsBridgeApi("toast")
        public void toast(String params) {
            ToastUtils.showShortToast(WebActivity.this, "toast:" + params);
        }
    }
}
