package com.mylive.live.interceptor;

import com.alibaba.fastjson.JSON;
import com.mylive.live.config.HttpStateCode;
import com.mylive.live.model.HttpResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.body() != null) {
            try {
                HttpResponse httpResponse = JSON.parseObject(response.body().string(),
                        HttpResponse.class);
                if (httpResponse != null) {
                    if (httpResponse.getCode() == HttpStateCode.TOKEN_EXPIRE){

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}
