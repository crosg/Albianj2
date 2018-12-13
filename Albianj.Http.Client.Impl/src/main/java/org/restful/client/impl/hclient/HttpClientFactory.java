package org.restful.client.impl.hclient;

import org.albianj.net.AlbianHost;
import org.albianj.pooling.IPoolingObject;
import org.albianj.pooling.IPoolingObjectConfig;
import org.albianj.pooling.IPoolingObjectFactory;
import org.albianj.pooling.impl.ReusableObjectPoolMgr;
import org.albianj.restful.client.config.*;
import org.albianj.verify.Validate;
import org.apache.http.Header;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientFactory implements IPoolingObjectFactory {

    public IPoolingObject newPoolingObject(boolean isPooling, IPoolingObjectConfig objConfig) {
        HttpClientConfig httpClientConfig = (HttpClientConfig) objConfig;
        HttpClientBuilder hcb = HttpClientBuilder.create();
        HttpClientSSLConfig sslConfig = httpClientConfig.getHttpClientSSLConfig();
        if(null != sslConfig &&sslConfig.isEnable()){
            if(sslConfig.isByPass()){
                SSLContext sslCtx =  newSSLContextButIgnoreVerify();
                hcb.setSSLContext(sslCtx);
            } else {
                SSLContext sslCtx = newSSLContext(sslConfig.getKeyStorePath(),sslConfig.getKeyStorePwd());
                hcb.setSSLContext(sslCtx);
            }
        }

        HttpClientSocketConfig socketConfig = httpClientConfig.getHttpClientSocketConfig();
        if(null != socketConfig){
            SocketConfig.Builder scb = SocketConfig.custom();
            scb.setTcpNoDelay(socketConfig.isTcpNoDelay());
            if(-1 != socketConfig.getBacklogSizeB()) {
                scb.setBacklogSize((int) socketConfig.getBacklogSizeB());
            }
            if(-1 != socketConfig.getSndBufSizeB()) {
                scb.setSndBufSize((int) socketConfig.getSndBufSizeB());
            }
            if(-1 != socketConfig.getRcvBufSizeB()) {
                scb.setRcvBufSize((int) socketConfig.getRcvBufSizeB());
            }
            scb.setSoKeepAlive(true);
            scb.setSoLinger(socketConfig.getSoLinger());
            scb.setSoReuseAddress(true);
            scb.setSoTimeout((int) socketConfig.getSoTimeoutMs());
            SocketConfig sktConfig =  scb.build();
            hcb.setDefaultSocketConfig(sktConfig);
        }

        HttpClientHeadersConfig headersConfig = httpClientConfig.getHttpClientHeadersConfig();
        if(Validate.isNullOrEmpty(headersConfig)){
            List<Header> headers = new ArrayList();
            for(HttpClientHeaderConfig h : headersConfig.values()){
                headers.add(new BasicHeader(h.getKey(),h.getValue()));
            }
            Header cipHeader = new BasicHeader("X-Client-IP",
                    AlbianHost.getLocalIPByName(Validate.isNullOrEmptyOrAllSpace(socketConfig.getNetworkInterface())
                            ? "eth1" : socketConfig.getNetworkInterface()));
            headers.add(cipHeader);
            hcb.setDefaultHeaders(headers);
        }

        CloseableHttpClient httpClient = hcb.build();
        IPoolingObject<CloseableHttpClient> wappedHttpClient = ReusableObjectPoolMgr.newPoolingObject(httpClient, System.currentTimeMillis(), true);
        return wappedHttpClient;
    }

//        HttpClientConfig hcConfig = (HttpClientConfig) objConfig;
//        String site = hcConfig.getSite();
//        String host = hcConfig.getHost();
//        HttpClientHeadersConfig headersConfig = hcConfig.getHttpClientHeadersConfig();
//        HttpClientSocketConfig sktConfig = hcConfig.getHttpClientSocketConfig();
//        HttpClientSSLConfig sslConfig = hcConfig.getHttpClientSSLConfig();
//
//        HttpClientBuilder hcb = HttpClientBuilder.create();
//
//        List<Header> headers = new ArrayList<>();
//        Header clientIpHeader = new BasicHeader("x-chain-clientip", AlbianHost.getLocalIPByName(socketConfig.getNetworkInterface()));
//        headers.add(clientIpHeader);
//
//        if(null != headersConfig &&0 != headersConfig.size()){
//            for(HttpClientHeaderConfig hc : headersConfig.values()){
//                Header header = new BasicHeader(hc.getKey(),hc.getValue());
//                headers.add(header);
//            }
//        }
//        hcb.setDefaultHeaders(headers);
//
//        if(null != sslConfig && !sslConfig.isEnable()) {
//            if(sslConfig.isByPass()){
//                SSLContext sslCtx = newSSLContextButIgnoreVerify();
//                hcb.setSSLContext(sslCtx);
//            } else {
//                SSLContext sslCtx = newSSLContext(sslConfig.getKeyStorePath(),sslConfig.getKeyStorePwd());
//                hcb.setSSLContext(sslCtx);
//            }
//        }
//
//        hcb.setRetryHandler(new HttpRequestRetryHandler() {
//            @Override
//            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
//                return false;
//            }
//        });
//
//        if(null != sktConfig) {
//            SocketConfig.Builder sb = SocketConfig.custom();
//            sb.setTcpNoDelay()
//                    .setTcpNoDelay(true)
//                    .setBacklogSize(10)
//                    .setRcvBufSize(10)
//                    .setSndBufSize(10)
//                    .setSoKeepAlive(true)
//                    .setSoLinger(-1)
//                    .setSoReuseAddress(true)
//                    .setSoTimeout(10);
//            SocketConfig socketConfig = sb.build();
//        }
//
//        hcb.build();
//
//        CloseableHttpClient orgHttpClient = HttpClients.custom()
//                .setDefaultSocketConfig()
//                .setDefaultConnectionConfig()
//                .setDefaultRequestConfig()
//                .setRetryHandler()
//                .build();
//        PoolingHttpClient httpGet = new PoolingHttpClient(orgHttpClient,System.currentTimeMillis(),isPooling);
//        return httpGet;
//    }
    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext newSSLContextButIgnoreVerify() {
        try {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
        } catch ( NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置信任自签名证书
     *
     * @param keyStorePath		密钥库路径
     * @param keyStorepass		密钥库密码
     * @return
     */
    public static SSLContext newSSLContext(String keyStorePath, String keyStorepass){
        SSLContext sc = null;
        FileInputStream instream = null;
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            instream = new FileInputStream(new File(keyStorePath));
            trustStore.load(instream, keyStorepass.toCharArray());
            // 相信自己的CA和所有自签名的证书
            sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
        } catch (KeyStoreException | NoSuchAlgorithmException| CertificateException | IOException | KeyManagementException e) {
            e.printStackTrace();
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
            }
        }
        return sc;
    }

}
