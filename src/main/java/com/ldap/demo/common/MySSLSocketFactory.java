package com.ldap.demo.common;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author yh128
 * @version 1.0.0
 * @ClassName MySSLSocketFactory.java
 * @Description 自定义 SSLSocketFactory
 * @Param
 * @createTime 2019年10月28日 18:52:00
 */
public class MySSLSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory factory;

    public MySSLSocketFactory() {
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");

            sslcontext.init(null,
                    new TrustManager[]{new MyX509TrustManager()},
                    new java.security.SecureRandom());
            factory = sslcontext.getSocketFactory();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static SocketFactory getDefault() {

        return new MySSLSocketFactory();

    }

    @Override
    public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3)
            throws IOException {
        return factory.createSocket(arg0, arg1, arg2, arg3);
    }


    @Override
    public String[] getDefaultCipherSuites() {
        return factory.getSupportedCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return factory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(String arg0, int arg1) throws IOException,
            UnknownHostException {
        return factory.createSocket(arg0, arg1);
    }

    @Override
    public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
        return factory.createSocket(arg0, arg1);
    }

    @Override
    public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3)
            throws IOException, UnknownHostException {
        return factory.createSocket(arg0, arg1, arg2, arg3);
    }

    @Override
    public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2,
                               int arg3) throws IOException {
        return factory.createSocket(arg0, arg1, arg2, arg3);
    }

}