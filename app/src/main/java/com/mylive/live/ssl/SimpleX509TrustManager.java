package com.mylive.live.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Created by Developer Zailong Shi on 2019-07-12.
 */
public class SimpleX509TrustManager implements X509TrustManager {

    public static SimpleX509TrustManager trustAll() {
        return new SimpleX509TrustManager();
    }

    private SimpleX509TrustManager() {
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
