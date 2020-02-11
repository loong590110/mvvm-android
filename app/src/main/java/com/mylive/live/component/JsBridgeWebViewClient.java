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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 使用说明：
 * 1、通过addLocalApi方法注册供给Javascript调用的java方法。
 * 默认注册的Javascript对象名称是“jsBridge”，可自定义该对象名称。
 * 2、通过createRemoteApi方法创建动态代理类来调用Javascript注册在“jsBridge”对象上的函数。
 * 注：可利用@JsBridgeApi和@JsBridgeField注解实现防混淆，或添加防混淆规则（本类已实现通过注解防混淆）
 * <p>
 * Created by Developer Zailong Shi on 2019-07-09.
 */
public class JsBridgeWebViewClient extends WebViewClient {

    private static final String TAG = JsBridgeWebViewClient.class.getSimpleName();
    private static final String JS_BRIDGE = "jsBridge";
    private static final String INVOKE = "invoke";
    private static final String ON_CALLBACK = "onCallback";
    private static final String CALLBACKS = "callbacks";
    private static final String CALLBACK = "callback";
    private static final String CALL = "call";
    private static final String ERROR = "error";
    private Handler handler = new Handler(Looper.getMainLooper());
    private String invokeMethodName, onCallbackMethodName;
    private Object localApi;
    private Map<String, Method> localApiMap;
    private Map<String, Object> callbackMap;
    private List<String> reservedMethod;//保留方法
    private WeakReference<WebView> view;
    private String jsBridge;

    {
        Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(JsBridgeApi.class)) {
                if (INVOKE.equals(method.getAnnotation(JsBridgeApi.class).value())) {
                    invokeMethodName = method.getName();
                } else if (ON_CALLBACK.equals(method.getAnnotation(JsBridgeApi.class).value())) {
                    onCallbackMethodName = method.getName();
                }
                if (invokeMethodName != null && onCallbackMethodName != null) {
                    break;
                }
            }
        }
        reservedMethod = new ArrayList<>();
        reservedMethod.add(INVOKE);
        reservedMethod.add(ON_CALLBACK);
        reservedMethod.add(invokeMethodName);//混淆以后这个方法名会变
        reservedMethod.add(onCallbackMethodName);//同上
        reservedMethod.add(CALLBACKS);
        reservedMethod.add(CALLBACK);
        reservedMethod.add(CALL);
        reservedMethod.add(ERROR);
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
        String proxy = invokeMethodName != null && !INVOKE.equals(invokeMethodName) ?
                ("window.$jsBridge.$invoke = function(name, params) {"
                        + "window.$jsBridge.$_invoke(name, params);"
                        + "};")
                        .replace("$_invoke", invokeMethodName)
                        .replace("$invoke", INVOKE)
                : "";
        proxy += onCallbackMethodName != null && !ON_CALLBACK.equals(onCallbackMethodName) ?
                ("window.$jsBridge.$onCallback = function(name, params) {"
                        + "window.$jsBridge.$_onCallback(name, params);"
                        + "};")
                        .replace("$_onCallback", onCallbackMethodName)
                        .replace("$onCallback", ON_CALLBACK)
                : "";
        String injectScript = proxy
                + ("window.$jsBridge.$callbacks = {};"
                + "window.$jsBridge.$callback = function(callbackId, args) {"
                + "window.eval('window.$jsBridge.$callbacks[\"' + callbackId + '\"](' + String(args) + ')');"
                + "};"
                + "window.$jsBridge.$call = function(name, args) {"
                + "window.eval('window.$jsBridge.' + name + '(' + String(args) + ')');"
                + "};"
                + "window.$jsBridge.$error = function(error) {"
                + "console.error('JsBridge error: ' + error);"
                + "};")
                .replace("$callbacks", CALLBACKS)//此处优先“$callback”替换
                .replace("$callback", CALLBACK)
                .replace("$call", CALL)
                .replace("$error", ERROR);
        evaluateJavascript(injectScript.replace("$jsBridge", jsBridge));
    }

    private void injectCustomApi() {
        if (localApiMap == null) {
            throw new IllegalStateException("请在页面开始加载前设置JsBridgeApi对象。");
        }
        StringBuilder injectScript = new StringBuilder();
        for (Map.Entry<String, Method> entry : localApiMap.entrySet()) {
            String name = entry.getKey();
            Class<?>[] paramTypes = entry.getValue().getParameterTypes();
            StringBuilder saveCallbacks = new StringBuilder();
            String[] args = new String[paramTypes.length];
            int argIndex = 1, callbackIndex = 1;
            for (int i = 0; i < paramTypes.length; i++) {
                if (Callback.class.isAssignableFrom(paramTypes[i])) {
                    args[i] = "callback$i".replace("$i", String.valueOf(callbackIndex++));
                    saveCallbacks.append("window.$jsBridge.$callbacks['$callbackId']=$callback;"
                            .replace("$jsBridge", jsBridge)
                            .replace("$callbacks", CALLBACKS)//此处优先“$callback”替换
                            .replace("$callbackId", callbackId(name, args[i]))
                            .replace("$callback", args[i])
                    );
                } else {
                    args[i] = "arg$i".replace("$i", String.valueOf(argIndex++));
                }
            }
            StringBuilder argsStr = new StringBuilder();
            StringBuilder argsStrArr = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                argsStr.append(args[i]);
                if (Callback.class.isAssignableFrom(paramTypes[i])) {
                    argsStrArr.append("'$arg'".replace("$arg", args[i]));
                } else {
                    argsStrArr.append("String($arg)".replace("$arg", args[i]));
                }
                if (i < args.length - 1) {
                    argsStr.append(",");
                    argsStrArr.append(",");
                }
            }
            injectScript.append(
                    ("window.$jsBridge.$name = function($args) {"
                            + "$save_callbacks"
                            + "return eval(window.$jsBridge.$invoke('$name', [$argArray]));"
                            + "};")
                            .replace("$jsBridge", jsBridge)
                            .replace("$name", name)
                            .replace("$args", argsStr)
                            .replace("$save_callbacks", saveCallbacks)
                            .replace("$argArray", argsStrArr)
                            .replace("$invoke", INVOKE)
            );
        }
        evaluateJavascript(injectScript.toString());
    }

    public <T> T createRemoteApi(Class<T> remoteApi) {
        Objects.requireNonNull(remoteApi);
        if (!remoteApi.isInterface()) {
            throw new IllegalArgumentException("定义远程api必须用接口类型。");
        }
        callbackMap = new HashMap<>();
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(
                remoteApi.getClassLoader(),
                new Class[]{remoteApi},
                (proxy, method, args) -> {
                    if (isNotProxyMethod(method)) {
                        return invokeIsNotProxyMethod(proxy, method, args);
                    }
                    String name = getMethodName(method);
                    if (!void.class.isAssignableFrom(method.getReturnType())) {
                        throw new IllegalStateException(name + "方法有返回值，"
                                + "调用Javascript函数不支持同步获取返回值，只可使用异步回调。");
                    }
                    if (localApiMap.containsKey(name)
                            || reservedMethod.contains(name)) {
                        throw new IllegalStateException("Javascript函数" + name
                                + "与本地方法冲突。");
                    }
                    call(name, args);
                    return null;
                });
    }

    public void addLocalApi(Object localApi) {
        Objects.requireNonNull(localApi);
        this.localApi = localApi;
        if (localApiMap == null) {
            localApiMap = new HashMap<>();
        }
        localApiMap.clear();
        Method[] methods = this.localApi.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getModifiers() == Modifier.PUBLIC
                    || method.isAnnotationPresent(JsBridgeApi.class)) {
                String name = getMethodName(method);
                localApiMap.put(name, method);
            }
        }
    }

    @JsBridgeApi(INVOKE)
    @JavascriptInterface
    public String invoke(String name, String... params) {
        Method method = localApiMap.get(name);
        if (method != null) {
            try {
                Object[] args = handleParams(method, name, params);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if (!void.class.isAssignableFrom(method.getReturnType())
                        && !Void.class.isAssignableFrom(method.getReturnType())) {
                    return convert(method.invoke(localApi, args));
                } else {
                    method.invoke(localApi, args);
                }
            } catch (Exception e) {
                log(e);
            }
        } else {
            log("JsBridge Exception: 找不到名为" + name + "的方法(函数)。");
        }
        return null;
    }

    @JsBridgeApi(ON_CALLBACK)
    @JavascriptInterface
    public void onCallback(String callbackName, String... params) {
        Object callback = callbackMap.get(callbackName);
        if (callback != null) {
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                args[i] = convert(params[i]);
            }
            if (callback instanceof Callback) {
                ((Callback) callback).call(args);
            } else if (callback instanceof Callback2) {
                Object arg = null;
                if (args.length > 0) {
                    arg = args[0];
                    //仅实现JSONObject转换成简单的Java对象
                    if (arg instanceof JSONObject) {
                        Method[] methods = callback.getClass().getDeclaredMethods();
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(JsBridgeApi.class)) {
                                Type[] types = method.getGenericParameterTypes();
                                if (types.length > 0 && types[0] instanceof Class) {
                                    Class<?> cls = (Class) types[0];
                                    if (!isAssignableFromBaseType(cls)) {
                                        try {
                                            arg = parseJSON((JSONObject) arg, cls);
                                        } catch (JSONException e) {
                                            log(e);
                                        } catch (IllegalAccessException e) {
                                            log(e);
                                        } catch (InstantiationException e) {
                                            log(e);
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    } else if (arg instanceof JSONArray) {
                        //暂不支持JSONArray数组转换
                    }
                }
                ((Callback2) callback).call(arg);
            }
        }
    }

    private void call(String function, Object... args) {
        String callJsFunc = String.format("window.$jsBridge.$call('%s', %s);"
                        .replace("$jsBridge", jsBridge)
                        .replace("$call", CALL),
                function, construct(args));
        evaluateJavascript(callJsFunc);
    }

    private void callback(String callbackId, Object... args) {
        String callCallbackFunc = String.format("window.$jsBridge.$callback('%s', %s);"
                        .replace("$jsBridge", jsBridge)
                        .replace("$callback", CALLBACK),
                callbackId, construct(args));
        evaluateJavascript(callCallbackFunc);
    }

    private String callbackId(String functionName, String callbackName) {
        return functionName + "#" + callbackName;
    }

    private String getMethodName(Method method) {
        return method.isAnnotationPresent(JsBridgeApi.class) ?
                method.getAnnotation(JsBridgeApi.class).value()
                : method.getName();
    }

    private boolean isNotProxyMethod(Method method) {
        if (method.getDeclaringClass() == Object.class) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return method.isDefault();
        }
        return false;
    }

    private Object invokeIsNotProxyMethod(Object proxy, Method method, Object... args) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                final Constructor<MethodHandles.Lookup> lookupConstructor;
                lookupConstructor = MethodHandles.Lookup.class
                        .getDeclaredConstructor(Class.class, int.class);
                lookupConstructor.setAccessible(true);
                Class<?> declaringClass = method.getDeclaringClass();
                // Used mode -1 = TRUST, because Modifier.PRIVATE failed for me in If 8.
                MethodHandles.Lookup lookup
                        = lookupConstructor.newInstance(declaringClass, -1);
                try {
                    return lookup.findSpecial(declaringClass, method.getName(),
                            MethodType.methodType(method.getReturnType(),
                                    method.getParameterTypes()), declaringClass)
                            .bindTo(proxy)
                            .invokeWithArguments(args);
                } catch (Throwable e) {
                    try {
                        return lookup.unreflectSpecial(method, declaringClass)
                                .bindTo(proxy)
                                .invokeWithArguments(args);
                    } catch (Throwable ignore) {
                    }
                }
            } catch (Exception ignore) {
            }
        } else {
            try {
                method.invoke(this, args);
            } catch (IllegalAccessException ignore) {
            } catch (InvocationTargetException ignore) {
            }
        }
        return null;
    }

    private void error(String error) {
        if (error != null && error.contains("\n")) {
            error = error.replace("\n", "'\n+'");
        }
        String callErrorFunc = String.format(
                ("window.$jsBridge.$error('%s');"
                        .replace("$jsBridge", jsBridge)
                        .replace("$error", ERROR)
                ),
                error);
        evaluateJavascript(callErrorFunc);
    }

    private void log(Throwable e) {
        log(collectExceptionMessage(e));
    }

    private void log(String message) {
        Log.e(TAG, message);
        error(message);
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
            } else if (int.class.isAssignableFrom(paramType)
                    || Integer.class.isAssignableFrom(paramType)) {
                args[i] = Integer.valueOf(param);
            } else if (long.class.isAssignableFrom(paramType)
                    || Long.class.isAssignableFrom(paramType)) {
                args[i] = Long.valueOf(param);
            } else if (float.class.isAssignableFrom(paramType)
                    || Float.class.isAssignableFrom(paramType)) {
                args[i] = Float.valueOf(param);
            } else if (double.class.isAssignableFrom(paramType)
                    || Double.class.isAssignableFrom(paramType)) {
                args[i] = Double.valueOf(param);
            } else if (boolean.class.isAssignableFrom(paramType)
                    || Boolean.class.isAssignableFrom(paramType)) {
                args[i] = Boolean.valueOf(param);
            } else if (JSONObject.class.isAssignableFrom(paramType)) {
                args[i] = new JSONObject(param);
            } else if (Callback.class.isAssignableFrom(paramType)) {
                args[i] = (Callback) objectArgs -> {
                    callback(callbackId(name, param), objectArgs);
                };
            } else {
                args[i] = parseJSON(param, paramType);
            }
        }
        return args;
    }

    private String convert(Object object)
            throws IllegalAccessException, JSONException {
        if (object instanceof Integer
                || object instanceof Long
                || object instanceof Float
                || object instanceof Double
                || object instanceof Boolean
                || object instanceof JSONArray) {
            return object.toString();
        } else if (object instanceof String) {
            return "'" + object.toString() + "'";
        } else if (object instanceof Object[]) {
            return toJSONArray((Object[]) object).toString();
        } else if (object instanceof Callback
                || object instanceof Callback2) {
            String name = object.toString();
            callbackMap.put(name, object);
            return ("function() {"
                    + "var args = Array.prototype.slice.apply(arguments);"
                    + "for (var i = 0; i < args.length; i++) {"
                    + "    var arg = args[i];"
                    + "    switch (arg.constructor) {"
                    + "        case String:"
                    + "             args[i] = \"'\" + String(arg) + \"'\";"
                    + "             break;"
                    + "        case Array:"
                    + "        case Object:"
                    + "             args[i] = JSON.stringify(arg);"
                    + "             break;"
                    + "        default:"
                    + "             args[i] = String(arg);"
                    + "             break;"
                    + "   }"
                    + "};"
                    + "window.$jsBridge.$onCallback('$name', args);"
                    + "}")
                    .replace("$jsBridge", jsBridge)
                    .replace("$onCallback", ON_CALLBACK)
                    .replace("$name", name);
        } else if (object instanceof JSONObject) {
            return "(" + object.toString() + ")";
        } else if (object != null) {
            return "(" + toJSONObject(object).toString() + ")";
        }
        return null;
    }

    private Object convert(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (text.startsWith("'") && text.endsWith("'")) {
                return text.replace("'", "");
            } else if (text.startsWith("[") && text.endsWith("]")) {
                try {
                    return new JSONArray(text);
                } catch (JSONException e) {
                    log(e);
                }
            } else if (text.startsWith("{") && text.endsWith("}")) {
                try {
                    return new JSONObject(text);
                } catch (JSONException e) {
                    log(e);
                }
            }
        }
        return text;
    }

    private JSONObject toJSONObject(Object object) throws IllegalAccessException, JSONException {
        JSONObject jsonObject = new JSONObject();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            String key = field.isAnnotationPresent(JsBridgeField.class) ?
                    field.getAnnotation(JsBridgeField.class).value()
                    : field.getName();
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            if (isAssignableFromBaseType(field.getType())) {
                jsonObject.put(key, field.get(object));
            } else {
                jsonObject.put(key, toJSONObject(field.get(object)));
            }
        }
        return jsonObject;
    }

    private JSONArray toJSONArray(Object[] objects) throws JSONException, IllegalAccessException {
        JSONArray jsonArray = new JSONArray();
        for (Object o : objects) {
            if (o instanceof Object[]) {
                //解决内部数组对象不会自动转成JSON数组的问题
                jsonArray.put(toJSONArray((Object[]) o));
            } else {
                if (instanceOfBaseType(o)) {
                    jsonArray.put(o);
                } else {
                    jsonArray.put(toJSONObject(o));
                }
            }
        }
        return jsonArray;
    }

    private <T> T parseJSON(String json, Class<T> tClass) throws JSONException, InstantiationException, IllegalAccessException {
        return parseJSON(new JSONObject(json), tClass);
    }

    private <T> T parseJSON(JSONObject jsonObject, Class<T> tClass) throws JSONException, IllegalAccessException, InstantiationException {
        T object = tClass.newInstance();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            String key = field.isAnnotationPresent(JsBridgeField.class) ?
                    field.getAnnotation(JsBridgeField.class).value()
                    : field.getName();
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            if (isAssignableFromBaseType(field.getType())) {
                field.set(object, jsonObject.get(key));
            } else if (List.class.isAssignableFrom(field.getType())) {
                Type type = field.getGenericType();
                Type[] types = type instanceof ParameterizedType ?
                        ((ParameterizedType) type).getActualTypeArguments()
                        : null;
                if (types != null && types.length > 0) {
                    JSONArray jsonArray = jsonObject.getJSONArray(key);
                    if (jsonArray.length() > 0) {
                        List list = new ArrayList();
                        Class aClass = (Class) types[0];
                        if (isAssignableFromBaseType(aClass)) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                list.add(jsonArray.get(i));
                            }
                        } else {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                list.add(parseJSON(jsonObject1, aClass));
                            }
                        }
                        field.set(object, list);
                    }
                }
            } else if (Map.class.isAssignableFrom(field.getType())) {
                JSONObject jsonObject1 = jsonObject.getJSONObject(key);
                if (jsonObject1 != null) {
                    Map<String, Object> map = new HashMap<>();
                    Iterator<String> keys = jsonObject1.keys();
                    while (keys.hasNext()) {
                        String key1 = keys.next();
                        map.put(key1, jsonObject1.get(key1));
                    }
                    field.set(object, map);
                }
            } else if (jsonObject.get(key) != null) {
                field.set(object, parseJSON(jsonObject.getJSONObject(key), field.getType()));
            }
        }
        return object;
    }

    private boolean instanceOfBaseType(Object obj) {
        return obj instanceof Boolean
                || obj instanceof Integer
                || obj instanceof Long
                || obj instanceof Float
                || obj instanceof Double
                || obj instanceof String
                || obj instanceof JSONObject
                || obj instanceof JSONArray;
    }

    private boolean isAssignableFromBaseType(Class<?> cls) {
        return boolean.class.isAssignableFrom(cls)
                || int.class.isAssignableFrom(cls)
                || long.class.isAssignableFrom(cls)
                || float.class.isAssignableFrom(cls)
                || double.class.isAssignableFrom(cls)
                || Boolean.class.isAssignableFrom(cls)
                || Integer.class.isAssignableFrom(cls)
                || Long.class.isAssignableFrom(cls)
                || Float.class.isAssignableFrom(cls)
                || Double.class.isAssignableFrom(cls)
                || String.class.isAssignableFrom(cls)
                || JSONObject.class.isAssignableFrom(cls)
                || JSONArray.class.isAssignableFrom(cls);
    }

    private String construct(Object[] args) {
        StringBuilder argsStr = new StringBuilder("[");
        for (Object arg : args) {
            String converted = null;
            try {
                converted = convert(arg);
            } catch (IllegalAccessException e) {
                log(e);
            } catch (JSONException e) {
                log(e);
            }
            /* 为了js数组转字符串后根节点下的字符串、
             * JSON对象(包括数组)不被直接转成js对象，
             * 即保留字符串类型，在首尾增加引号来实现 */
            if (arg instanceof JSONObject
                    || arg instanceof Object[]) {
                converted = "'" + converted + "'";
            } else if (arg instanceof String) {
                converted = "\"" + converted + "\"";
            }
            argsStr.append(converted).append(",");
        }
        int index = argsStr.lastIndexOf(",");
        if (index != -1) {
            argsStr.delete(index, index + ",".length());
        }
        argsStr.append("]");
        return argsStr.toString();
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
            collectExceptionMessage(message, e.getCause());
        }
    }

    public interface Callback {
        void call(Object... args);
    }

    public interface Callback2<T> {
        @JsBridgeApi(CALL)
        void call(T t);
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
