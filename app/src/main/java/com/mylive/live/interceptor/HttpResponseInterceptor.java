package com.mylive.live.interceptor;

import com.mylive.live.config.HttpStateCode;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.isSuccessful() && response.body() != null) {
            String respText = response.body().string();
            int code = parseCode(respText);
            if (code == HttpStateCode.TOKEN_EXPIRE) {
                //do something
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

    private int parseCode(String text) {
        if (text.startsWith("{") && text.endsWith("}")) {
            String codeKey = "\"code\":";
            int index = text.indexOf(codeKey);
            if (index > 0) {
                int startIndex = index + codeKey.length();
                int length = String.valueOf(Integer.MAX_VALUE).length() + 1;
                String code = text.substring(startIndex, startIndex + length);
                int endIndex = code.indexOf(',');
                if (endIndex > 0) {
                    code = code.substring(0, endIndex).trim();
                    return Integer.valueOf(code);
                }
            }
        }
        return Integer.MIN_VALUE;
    }
}
