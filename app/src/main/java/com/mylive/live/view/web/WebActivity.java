package com.mylive.live.view.web;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.mylive.live.BuildConfig;
import com.mylive.live.R;
import com.mylive.live.component.JsBridgeWebViewClient.Callback;
import com.mylive.live.component.JsBridgeWebViewClient.JsBridgeApi;
import com.mylive.live.component.JsBridgeWebViewClient;
import com.mylive.live.base.BaseActivity;
import com.mylive.live.config.HttpConfig;
import com.mylive.live.databinding.ActivityWebBinding;
import com.mylive.live.model.Config;
import com.mylive.live.router.WebActivityStarter;
import com.mylive.live.utils.ToastUtils;

/**
 * Created by Developer Zailong Shi on 2019-07-09.
 */
public class WebActivity extends BaseActivity {

    private ActivityWebBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
        String url = getIntent().getStringExtra("url");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web);
        binding.navigationBar.setRightButtonText("next");
        binding.navigationBar.setOnRightButtonClickListener(v -> {
            Config.instance().homePage = "http://192.168.1.104:8080";
            WebActivityStarter.create(Config.instance().homePage)
                    .start(WebActivity.this);
        });
        JsBridgeWebViewClient jsBridge = new JsBridgeWebViewClient(binding.webView) {
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
        binding.webView.clearCache(BuildConfig.DEBUG);
        binding.webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    private class JsBridgeApiImpl {

        @JsBridgeApi("getVersion")
        public String getVersion() {
            return BuildConfig.VERSION_NAME;
        }

        @JsBridgeApi("getUserId")
        public void getUserId(Callback callback) {
            callback.call("1000012", "1000013");
        }

        @JsBridgeApi("toast")
        public void toast(String params) {
            ToastUtils.showShortToast(WebActivity.this, "toast: " + params);
        }
    }
}
