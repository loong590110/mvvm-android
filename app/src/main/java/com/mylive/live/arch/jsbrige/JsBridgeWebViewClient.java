package com.mylive.live.arch.jsbrige;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mylive.live.arch.annotation.JsBridgeApi;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 使用说明：
 * 1、
 * 2、
 * 注：利用注解实现防混淆，不需添加防混淆规则
 * <p>
 * Created by Developer Zailong Shi on 2019-07-09.
 */
public class JsBridgeWebViewClient extends WebViewClient {

    private static final String JS_BRIDGE = "jsBridge";
    private Handler handler = new Handler(Looper.getMainLooper());
    private Object jsBridgeApi;
    private String invokeMethodName;
    private Map<String, Method> apiMap;
    private WeakReference<WebView> view;

    {
        Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(JsBridgeApi.class)
                    && "invoke".equals(method.getAnnotation(JsBridgeApi.class).value())) {
                invokeMethodName = method.getName();
                break;
            }
        }
    }

    @Override
    public final void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (this.view == null || this.view.get() != view) {
            this.view = new WeakReference<>(view);
            view.getSettings().setJavaScriptEnabled(true);
            view.addJavascriptInterface(this, JS_BRIDGE);
        }
        if (view == null) {
            return;
        }
        String proxy = invokeMethodName != null && !"invoke".equals(invokeMethodName) ?
                "window.jsBridge.invoke = function(name, params){"
                        + "window.jsBridge.$invoke(name, params);"
                        + "};"
                : null;
        String js = (proxy != null ? proxy.replace("$invoke", invokeMethodName) : "")
                + "window.jsBridge.callback = function(name, returnValue){"
                + "window.jsBridge.invoke('toast', new Array(name + ':' + returnValue));"
                + "};"
                + "window.jsBridge.error = function(error){"
                + "console.log(error);"
                + "};"
                + "window.jsBridge.invoke('getUserId', new Array('callback'));";
        evaluateJavascript(js);
        onPageFinished(view, this, url);
    }

    public void onPageFinished(WebView view, JsBridgeWebViewClient client, String url) {
    }

    public void addJsBridgeApi(Object jsBridgeApi) {
        Objects.requireNonNull(jsBridgeApi);
        this.jsBridgeApi = jsBridgeApi;
        if (apiMap == null) {
            apiMap = new HashMap<>();
        }
        apiMap.clear();
        Method[] methods = jsBridgeApi.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(JsBridgeApi.class)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                Class<?> returnType = method.getReturnType();
                if (!void.class.isAssignableFrom(returnType)
                        && !String.class.isAssignableFrom(returnType)) {
                    throw new IllegalArgumentException("方法" + method.getName() + "无效，"
                            + "方法的返回值只允许String类型和void类型。");
                }
                for (Class<?> type : paramTypes) {
                    if (!String.class.isAssignableFrom(type)
                            && !Callback.class.isAssignableFrom(type)) {
                        throw new IllegalArgumentException("方法" + method.getName() + "无效，"
                                + "方法的参数只允许String类型和Callback类型。");
                    }
                }
                JsBridgeApi api = method.getAnnotation(JsBridgeApi.class);
                apiMap.put(api.value(), method);
            }
        }
    }

    @JsBridgeApi("invoke")
    @JavascriptInterface
    public void invoke(String name, String... params) {
        Method method = apiMap.get(name);
        if (method != null) {
            handler.post(() -> {
                try {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    Callback callback = returnValue -> callback(name, returnValue);
                    Object[] args = new Object[params == null ? 0 : params.length];
                    for (int i = 0; params != null && i < params.length; i++) {
                        args[i] = "callback".equals(params[i]) ? callback : params[i];
                    }
                    if (String.class.isAssignableFrom(method.getReturnType())) {
                        Object returnValue = method.invoke(jsBridgeApi, args);
                        callback(name, (String) returnValue);
                    } else {
                        method.invoke(jsBridgeApi, args);
                    }
                } catch (Exception e) {
                    error(e.getMessage());
                }
            });
            return;
        }
        error("名为" + name + "的方法(函数)不存在。");
    }

    private void callback(String name, String returnValue) {
        String js = String.format("window.jsBridge.callback('%s','%s');", name, returnValue);
        evaluateJavascript(js);
    }

    private void error(String error) {
        String js = String.format("window.jsBridge.error('%s');", error);
        evaluateJavascript(js);
    }

    private void evaluateJavascript(String javascript) {
        if (TextUtils.isEmpty(javascript)) {
            return;
        }
        WebView view = this.view.get();
        if (view == null) {
            return;
        }
        if (!javascript.startsWith("javascript:")) {
            javascript = "javascript:" + javascript;
        }
        String finalJavascript = javascript;
        handler.post(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.evaluateJavascript(finalJavascript, value -> {
                });
            } else {
                view.loadUrl(finalJavascript);
            }
        });

    }

    public interface Callback {
        void call(String returnValue);
    }
}
