package org.restful.client.impl.pool;

import org.albianj.pooling.IPoolingObject;
import org.albianj.pooling.IPoolingObjectConfig;
import org.albianj.pooling.IPoolingObjectFactory;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class HttpClientPoolingFactory implements IPoolingObjectFactory {
    @Override
    public IPoolingObject newPoolingObject(boolean isPooling, IPoolingObjectConfig poolingObjectConfig) {
        return null;
    }
//    @Override
//    public IPoolingObject newPoolingObject(boolean isPooling) {
//        SocketConfig socketConfig = SocketConfig.custom()
//                .setTcpNoDelay(true)
//                .setBacklogSize(10)
//                .setRcvBufSize(10)
//                .setSndBufSize(10)
//                .setSoKeepAlive(true)
//                .setSoLinger(-1)
//                .setSoReuseAddress(true)
//                .setSoTimeout(10)
//                .build();
//        ConnectionConfig connConfig = ConnectionConfig.custom()
//                .setBufferSize()
//                .setCharset()
//                .build();
////        SSLContext sslCtx = SSLContext.getDefault().getServerSessionContext();

        //tomcat是我自己的密钥库的密码，你可以替换成自己的
        //如果密码为空，则用"nopassword"代替
//        SSLContext sslcontext = custom("D:\\keys\\wsriakey", "tomcat");
//        //采用绕过验证的方式处理https请求
//        SSLContext sslCtxWithoutPwd = createIgnoreVerifySSL();
//
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setConnectionRequestTimeout()
//                .setConnectTimeout()
//                .setContentCompressionEnabled()
//                .setRedirectsEnabled()
//                .setSocketTimeout()
//                .build();
//        CloseableHttpClient httpclient = HttpClients.custom()
//                .setSSLContext(sslCtxWithoutPwd)
//                .setDefaultSocketConfig()
//                .setDefaultConnectionConfig()
//                .setDefaultRequestConfig()
//                .setRetryHandler()
//                .setDefaultHeaders()
//                .build();
//
//        HttpGet httpget = new HttpGet("http://localhost/");
//        httpget.setConfig(requestConfig);
//        CloseableHttpResponse response = httpclient.execute(httpget);
//        StatusLine statusLine = response.getStatusLine();
//        if (200 == statusLine.getStatusCode()){
//            HttpEntity entity = response.getEntity();
//            EntityUtils.toString(entity);
//            EntityUtils.consume(entity);
//            InputStream isContext = entity.getContent();
//            entity.getContentEncoding();
//            entity.getContentLength();
//            entity.getContentType();
//            isContext.read()
//        }
//
//        HttpPost httpPost = new HttpPost("http://localhost/");
//        NameValuePair nvps = new BasicNameValuePair("","");
//        httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
//        CloseableHttpResponse response = httpclient.execute(httpPost);

    }



//}
