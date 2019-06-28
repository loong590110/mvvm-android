package com.mylive.live.interceptor;

import com.mylive.live.arch.observer.Observer;
import com.mylive.live.arch.thread.ThreadsScheduler;
import com.mylive.live.config.HttpStateCode;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpResponseInterceptor extends ObservableInterceptor<Observer<String>> {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.isSuccessful() && response.body() != null) {
            String respText = response.body().string();
            String respTextCopy = String.copyValueOf(respText.toCharArray());
            int code = parseCodeValue(respTextCopy);
            switch (code) {
                case HttpStateCode.OK:
                case HttpStateCode.TOKEN_EXPIRE:
                    notifyObservers(code, respText);
                    break;
            }
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
        if (code == HttpStateCode.OK) {
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
