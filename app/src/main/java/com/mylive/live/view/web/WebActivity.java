package com.mylive.live.view.web;

import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.mylive.live.BuildConfig;
import com.mylive.live.R;
import com.mylive.live.arch.theme.StatusBarCompat;
import com.mylive.live.component.JsBridgeWebViewClient.Callback;
import com.mylive.live.component.JsBridgeWebViewClient.Callback2;
import com.mylive.live.component.JsBridgeWebViewClient.JsBridgeApi;
import com.mylive.live.component.JsBridgeWebViewClient;
import com.mylive.live.base.BaseActivity;
import com.mylive.live.databinding.ActivityWebBinding;
import com.mylive.live.dialog.AlertDialog;
import com.mylive.live.model.Config;
import com.mylive.live.router.WebActivityStarter;
import com.mylive.live.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Developer Zailong Shi on 2019-07-09.
 */
public class WebActivity extends BaseActivity {

    private ActivityWebBinding binding;
    private RemoteApi remoteApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarCompat.getSettings(this).setLightMode(true).apply();
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
        String url = getIntent().getStringExtra("url");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web);
        binding.navigationBar.setRightButtonText("next");
        binding.navigationBar.setOnRightButtonClickListener(v -> {
            //Config.instance().homePage = "http://192.168.1.104:8080";
            WebActivityStarter.create(Config.instance().homePage)
                    .start(WebActivity.this);
        });
        JsBridgeWebViewClient jsBridge = new JsBridgeWebViewClient(binding.webView) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, JsBridgeWebViewClient client,
                                      String url, Bitmap favicon) {
                super.onPageStarted(view, this, url, favicon);
                binding.navigationBar.setTitle("正在打开...");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.navigationBar.setTitle(view.getTitle());
            }
        };
        jsBridge.addLocalApi(new LocalApi());
        remoteApi = jsBridge.createRemoteApi(RemoteApi.class);
        binding.webView.setWebViewClient(jsBridge);
        binding.webView.clearCache(BuildConfig.DEBUG);
        binding.webView.loadUrl(url);
    }

    @Override
    public void finish() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
            return;
        }
        super.finish();
    }

    private class LocalApi {

        @JsBridgeApi("getVersion")
        public String getVersion() {
            return "100";//BuildConfig.VERSION_NAME;
        }

        @JsBridgeApi("getInteger")
        public int getInteger() {
            return 100;
        }

        @JsBridgeApi("getBoolean")
        public boolean getBoolean(boolean bool) {
            return bool;
        }

        @JsBridgeApi("getArray")
        public Object[] getArray() throws JSONException {
            return new Object[]{"1000012", "1000013", true, false,
                    new Object[]{"123", 123, new Object[]{"123", 123}},
                    new JSONObject("{name: 'aaron', sex: 1}")};
        }

        @JsBridgeApi("getJson")
        public JSONObject getJson() throws JSONException {
            return new JSONObject("{\"name\": \"aaron\", \"sex\": \"male\"}");
        }

        @JsBridgeApi("getUserId")
        public void getUserId(Callback callback) throws JSONException {
            callback.call("1000012", "1000013", true, false,
                    new Object[]{"123", 123, new Object[]{"123", 123}},
                    new JSONObject("{name: 'aaron', sex: 1}"));
        }

        @JsBridgeApi("toast")
        public void toast(String message) {
            ToastUtils.showShortToast(WebActivity.this, message);
        }

        @JsBridgeApi("ask")
        public void alert(String message) {
            new AlertDialog.Builder(WebActivity.this)
                    .setMessage(message)
                    .setConfirmText("肯定")
                    .setCancelText("否定")
                    .setOnConfirmClickListener((dialog, which) -> {
                        dialog.dismiss();
                        remoteApi.feedback(true, args -> {
                            String fb = args[0].toString();
                            ToastUtils.showShortToast(WebActivity.this, fb);
                        });
                    })
                    .setOnCancelClickListener((dialog, which) -> {
                        dialog.dismiss();
                        remoteApi.feedback(false, args -> {
                            ToastUtils.showShortToast(WebActivity.this, args[0].toString());
                        });
                    })
                    .setOnCancelListener(dialog -> {
                        remoteApi.feedback(false, args -> {
                            ToastUtils.showShortToast(WebActivity.this, args[0].toString());
                        });
                    })
                    .show();
        }
    }

    public interface RemoteApi {
        @JsBridgeApi("feedback")
        void feedback(boolean positive, Callback callback);

        @JsBridgeApi("testCallback2")
        void testCallback2(Callback2<String> callback);
    }
}
