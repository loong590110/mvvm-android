package com.mylive.live.component;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
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
public class JsBridgeWebViewClient extends WebViewClient {

    private static final String TAG = JsBridgeWebViewClient.class.getSimpleName();
    private static final String JS_BRIDGE = "jsBridge";
    private Handler handler = new Handler(Looper.getMainLooper());
    private Object jsBridgeApi;
    private String invokeMethodName, onReturnMethodName;
    private Map<String, Method> apiMap;
    private WeakReference<WebView> view;
    private String jsBridge;

    {
        Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(JsBridgeApi.class)) {
                if ("invoke".equals(method.getAnnotation(JsBridgeApi.class).value())) {
                    invokeMethodName = method.getName();
                } else if ("onReturn".equals(method.getAnnotation(JsBridgeApi.class).value())) {
                    onReturnMethodName = method.getName();
                }
                if (invokeMethodName != null && onReturnMethodName != null) {
                    break;
                }
            }
        }
    }

    public JsBridgeWebViewClient(WebView view) {
        this(view, JS_BRIDGE);
    }

    public JsBridgeWebViewClient(WebView view, String name) {
        Objects.requireNonNull(view);
        Objects.requireNonNull(name);
        this.jsBridge = name;
        if (this.view == null || this.view.get() != view) {
            this.view = new WeakReference<>(view);
        }
        view.getSettings().setJavaScriptEnabled(true);
        view.addJavascriptInterface(this, jsBridge);
    }

    @Override
    public final void onPageStarted(WebView view, String url, Bitmap favicon) {
        injectBridgeApi();
        injectCustomApi();
        super.onPageStarted(view, url, favicon);
        onPageStarted(view, this, url, favicon);
    }

    public void onPageStarted(WebView view, JsBridgeWebViewClient client,
                              String url, Bitmap favicon) {
    }

    private void injectBridgeApi() {
        String proxy = invokeMethodName != null && !"invoke".equals(invokeMethodName) ?
                ("window.$jsBridge.invoke = function(name, params) {"
                        + "window.$jsBridge.$invoke(name, params);"
                        + "};")
                        .replace("$invoke", invokeMethodName)
                : "";
        proxy += onReturnMethodName != null && !"onReturn".equals(onReturnMethodName) ?
                ("window.$jsBridge.onReturn = function(name, params) {"
                        + "window.$jsBridge.$onReturn(name, params);"
                        + "};")
                        .replace("$onReturn", onReturnMethodName)
                : "";
        String injectScript = proxy
                + "window.$jsBridge.callbacks = {};"
                + "window.$jsBridge.callback = function(callbackId, returnValues) {"
                + "var args = '\"' + returnValues.join('\",\"') + '\"';"
                + "window.eval('window.$jsBridge.callbacks[\"' + callbackId + '\"](' + args + ')');"
                + "};"
                + "window.$jsBridge.error = function(error) {"
                + "console.error('JsBridge error: ' + error);"
                + "};";
        evaluateJavascript(injectScript.replace("$jsBridge", jsBridge));
    }

    private void injectCustomApi() {
        if (apiMap == null) {
            throw new IllegalStateException("请在页面开始加载前设置JsBridgeApi对象。");
        }
        StringBuilder injectScript = new StringBuilder();
        for (Map.Entry<String, Method> entry : apiMap.entrySet()) {
            String name = entry.getKey();
            Class<?>[] paramTypes = entry.getValue().getParameterTypes();
            StringBuilder saveCallbacks = new StringBuilder();
            String[] args = new String[paramTypes.length];
            int argIndex = 1, callbackIndex = 1;
            for (int i = 0; i < paramTypes.length; i++) {
                if (Callback.class.isAssignableFrom(paramTypes[i])) {
                    args[i] = "callback$i".replace("$i", String.valueOf(callbackIndex++));
                    saveCallbacks.append("window.$jsBridge.callbacks['$callbackId']=$callback;"
                            .replace("$jsBridge", jsBridge)
                            .replace("$callbackId", callbackId(name, args[i]))
                            .replace("$callback", args[i]));
                } else {
                    args[i] = "arg$i".replace("$i", String.valueOf(argIndex++));
                }
            }
            StringBuilder argsStr = new StringBuilder();
            StringBuilder argsStrArr = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                argsStr.append(args[i]);
                argsStrArr.append(args[i].startsWith("callback") ?
                        "'$callback'".replace("$callback", args[i])
                        : args[i]);
                if (i < args.length - 1) {
                    argsStr.append(",");
                    argsStrArr.append(",");
                }

            }
            injectScript.append(
                    ("window.$jsBridge.$name = function($args) {"
                            + "$save_callbacks"
                            + "return window.$jsBridge.invoke('$name', [$argArray]);"
                            + "};")
                            .replace("$jsBridge", jsBridge)
                            .replace("$name", name)
                            .replace("$args", argsStr)
                            .replace("$save_callbacks", saveCallbacks)
                            .replace("$argArray", argsStrArr)
            );
        }
        evaluateJavascript(injectScript.toString());
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
                    || method.isAnnotationPresent(JsBridgeApi.class)) {
//                Class<?>[] paramTypes = method.getParameterTypes();
//                Class<?> returnType = method.getReturnType();
//                if (!void.class.isAssignableFrom(returnType)
//                        && !String.class.isAssignableFrom(returnType)) {
//                    throw new IllegalArgumentException("方法" + method.getName() + "无效，"
//                            + "方法的返回值只允许String类型和void类型。");
//                }
//                for (Class<?> paramType : paramTypes) {
//                    if (!String.class.isAssignableFrom(paramType)
//                            && !Callback.class.isAssignableFrom(paramType)) {
//                        throw new IllegalArgumentException("方法" + method.getName() + "无效，"
//                                + "方法的参数只允许String类型和Callback类型。");
//                    }
//                }
                String name = method.isAnnotationPresent(JsBridgeApi.class) ?
                        method.getAnnotation(JsBridgeApi.class).value()
                        : method.getName();
                apiMap.put(name, method);
            }
        }
    }

    @JsBridgeApi("invoke")
    @JavascriptInterface
    public String invoke(String name, String... params) {
        Method method = apiMap.get(name);
        if (method != null) {
            try {
                Object[] args = handleParams(method, name, params);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if (!void.class.isAssignableFrom(method.getReturnType())) {
                    Object returnValue = method.invoke(jsBridgeApi, args);
                    return handleReturnValue(returnValue);
                } else {
                    method.invoke(jsBridgeApi, args);
                }
            } catch (Exception e) {
                error(collectExceptionMessage(e));
                e.printStackTrace();
            }
        } else {
            String message = "JsBridge Exception: 找不到名为" + name + "的方法(函数)。";
            Log.e(TAG, message);
            error(message);
        }
        return null;
    }

    @JsBridgeApi("onReturn")
    @JavascriptInterface
    public void onReturn(String returnValue) {

    }

    private void callback(String callbackId, String... returnValues) {
        StringBuilder returnValuesString = new StringBuilder("['");
        for (String returnValue : returnValues) {
            returnValuesString.append(returnValue).append("','");
        }
        int index = returnValuesString.lastIndexOf("','");
        if (index != -1) {
            returnValuesString.delete(index, index + "','".length());
        }
        returnValuesString.append("']");
        String callCallbackFunc = String.format("window.$jsBridge.callback('%s', %s);"
                        .replace("$jsBridge", jsBridge),
                callbackId, returnValuesString);
        evaluateJavascript(callCallbackFunc);
    }

    private String callbackId(String functionName, String callbackName) {
        return functionName + "#" + callbackName;
    }

    private void error(String error) {
        String callErrorFunc = String.format("window.$jsBridge.error('%s');"
                .replace("$jsBridge", jsBridge), error);
        evaluateJavascript(callErrorFunc);
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

    private Object[] handleParams(Method method, String name, String... params)
            throws JSONException, InstantiationException, IllegalAccessException {
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        for (int i = 0; params != null && i < params.length && i < args.length; i++) {
            String param = params[i];
            Class<?> paramType = paramTypes[i];
            if (String.class.isAssignableFrom(paramType)) {
                args[i] = param;
            } else if (Integer.class.isAssignableFrom(paramType)) {
                args[i] = Integer.valueOf(param);
            } else if (Long.class.isAssignableFrom(paramType)) {
                args[i] = Long.valueOf(param);
            } else if (Float.class.isAssignableFrom(paramType)) {
                args[i] = Float.valueOf(param);
            } else if (Double.class.isAssignableFrom(paramType)) {
                args[i] = Double.valueOf(param);
            } else if (JSONObject.class.isAssignableFrom(paramType)) {
                args[i] = new JSONObject(param);
            } else if (Callback.class.isAssignableFrom(paramType)) {
                args[i] = (Callback) returnValues -> {
                    String[] callbackArgs = new String[returnValues.length];
                    for (int j = 0; j < callbackArgs.length; j++) {
                        try {
                            callbackArgs[j] = handleReturnValue(returnValues[j]);
                        } catch (IllegalAccessException ignore) {
                        } catch (JSONException e) {
                            error(collectExceptionMessage(e));
                            e.printStackTrace();
                        }
                    }
                    callback(callbackId(name, param), callbackArgs);
                };
            } else {
                JSONObject jsonObject = new JSONObject(param);
                Object object = paramType.newInstance();
                Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    String key = field.isAnnotationPresent(JsBridgeField.class) ?
                            field.getAnnotation(JsBridgeField.class).value()
                            : field.getName();
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    field.set(object, jsonObject.get(key));
                }
            }
        }
        return args;
    }

    private String handleReturnValue(Object returnValue)
            throws IllegalAccessException, JSONException {
        if (returnValue instanceof String
                || returnValue instanceof Integer
                || returnValue instanceof Long
                || returnValue instanceof Float
                || returnValue instanceof Double
                || returnValue instanceof JSONObject) {
            return returnValue.toString();
        } else if (returnValue != null) {
            JSONObject jsonObject = new JSONObject();
            Field[] fields = returnValue.getClass().getDeclaredFields();
            for (Field field : fields) {
                String key = field.isAnnotationPresent(JsBridgeField.class) ?
                        field.getAnnotation(JsBridgeField.class).value()
                        : field.getName();
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                jsonObject.put(key, field.get(returnValue));
            }
            return jsonObject.toString();
        }
        return null;
    }

    private String collectExceptionMessage(Throwable e) {
        StringBuilder message = new StringBuilder();
        collectExceptionMessage(message, e);
        return message.toString();
    }

    private void collectExceptionMessage(StringBuilder message, Throwable e) {
        message.append(e.toString()).append(": ").append(e.getMessage()).append("\n");
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            message.append("at ").append(stackTraceElement.toString()).append("\n");
        }
        if (e.getCause() != null && e.getCause() != e) {
            collectExceptionMessage(message, e);
        }
    }

    public interface Callback {
        void call(Object... args);
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface JsBridgeApi {
        String value();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface JsBridgeField {
        String value();
    }
}
