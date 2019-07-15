package com.mylive.live.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Developer Zailong Shi on 2019-07-12.
 */
public final class SSLContextWrapper {

    private static SSLContext sslContext;
    private static SimpleX509TrustManager x509TrustManager;

    public static SSLContext getInstance() {
        if (sslContext == null) {
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null,
                        new TrustManager[]{x509TrustManager
                                = SimpleX509TrustManager.trustAll()},
                        new SecureRandom());
            } catch (NoSuchAlgorithmException ignore) {
            } catch (KeyManagementException ignore) {
            }
        }
        return sslContext;
    }

    public static X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }
}
