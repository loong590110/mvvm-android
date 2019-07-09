package com.mylive.live.arch.jsbrige;

import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.mylive.live.arch.annotation.JsBridgeApi;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
public class JsBridge {

    private Object jsBridgeApi;
    private Map<String, Method> apiMap;

    public JsBridge(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "jsBridge");
    }

    public void onPageFinished(WebView view) {
        String js = "javascript:"
                + "var version = window.jsBridge.invoke('version', 'ver');"
                + "var t = window.jsBridge.invoke('toast', version);";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.evaluateJavascript(js, value -> {

            });
        } else {
            view.loadUrl(js);
        }
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
            if (method.getModifiers() == Modifier.PUBLIC
                    && method.isAnnotationPresent(JsBridgeApi.class)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                Class<?> returnType = method.getReturnType();
                if (paramTypes.length == 1 && String.class.isAssignableFrom(paramTypes[0])
                        && String.class.isAssignableFrom(returnType)) {
                    JsBridgeApi api = method.getAnnotation(JsBridgeApi.class);
                    apiMap.put(api.value(), method);
                    continue;
                }
                throw new IllegalArgumentException("方法" + method.getName() + "无效，" +
                        "方法必须有且只有一个String参数，" +
                        "而且返回值必须是String类型。");
            }
        }
    }

    @JavascriptInterface
    public String invoke(String name, String params) {
        try {
            Method method = apiMap.get(name);
            if (method != null) {
                Object returnValue = method.invoke(jsBridgeApi, params);
                return callback(name, (String) returnValue);
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    public String callback(String name, String params) {
        return String.format("{\"name\":\"%s\",\"return\":\"%s\"}", name, params);
    }
}
