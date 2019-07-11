package com.mylive.live.interceptor;

import android.util.Log;

import com.mylive.live.arch.observer.Observer;
import com.mylive.live.arch.thread.ThreadsScheduler;
import com.mylive.live.config.HttpStatusCode;
import com.sun.script.javascript.RhinoScriptEngine;

import java.io.IOException;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpResponseInterceptor extends ObservableInterceptor<Observer<String>> {

    private ScriptEngine scriptEngine = new RhinoScriptEngine();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.isSuccessful() && response.body() != null) {
            String respText = response.body().string();
            //region: intercept scripts
            String result = handleScript(response.request(), respText);
            if (result != null) {
                respText = result;
            }
            //endregion
            //region: intercept code
            String respTextCopy = String.copyValueOf(respText.toCharArray());
            int code = parseCodeValue(respTextCopy);
            switch (code) {
                case HttpStatusCode.OK:
                case HttpStatusCode.TOKEN_EXPIRE:
                    notifyObservers(code, respText);
                    break;
            }
            //endregion
            return new Response.Builder()
                    .request(response.request())
                    .protocol(response.protocol())
                    .handshake(response.handshake())
                    .headers(response.headers())
                    .code(response.code())
                    .message(response.message())
                    .body(new RealResponseBody(null,
                            respText.length(),
                            new Buffer().writeUtf8(respText)))
                    .build();
        }
        return response;
    }

    private String handleScript(Request request, String script) {
        try {
            String url = request.url().toString();
            if (url.contains("/api/")) {
                Set<String> names = request.url().queryParameterNames();
                Bindings bindings = new SimpleBindings();
                for (String name : names) {
                    bindings.put(name, request.url().queryParameter(name));
                }
                return (String) scriptEngine.eval(script, bindings);
            }
        } catch (ScriptException ignore) {
            Log.e("handleScript", ignore.getMessage());
        } catch (Exception ignore) {
            Log.e("handleScript", ignore.getMessage());
        }
        return null;
    }

    private int parseCodeValue(String text) {
        if (text.startsWith("{") && text.endsWith("}")) {
            String codeKey = "\"code\":";
            int startIndex = text.indexOf(codeKey);
            if (startIndex > 0) {
                //截取已codeKey为开头的字符串，结果是："code":...。
                text = text.substring(startIndex);
                //查找第一个逗号（，）的位置，即为codeValue的结束位置。
                int endIndex = text.indexOf(',');
                if (endIndex > 0) {
                    //截取codeValue的字符串，起始位置为codeKey的长度
                    String codeValue = text.substring(
                            codeKey.length(),
                            endIndex
                    ).trim();
                    return Integer.valueOf(codeValue);
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    private void notifyObservers(int code, String respText) {
        if (code == HttpStatusCode.OK) {
            notifyObservers(respText);
        }
    }

    private void notifyObservers(String respText) {
        for (Observer<String> observer : mObservers) {
            ThreadsScheduler.runOnUiThread(
                    () -> observer.onChanged(respText)
            );
        }
    }
}
