package org.restful.client.impl;//package org.restful.client.impl;

import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.pooling.IReusableObjectPool;
import org.albianj.restful.client.IAlbianHttpClientService;
import org.albianj.restful.client.config.*;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianServiceException;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.service.parser.FreeAlbianParserService;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.Map;

//
//import org.albianj.datetime.AlbianDateTime;
//import org.albianj.io.Path;
//import org.albianj.kernel.KernelSetting;
//import org.albianj.logger.AlbianLoggerLevel;
//import org.albianj.logger.IAlbianLoggerService2;
//import org.albianj.net.AlbianHost;
//import org.albianj.restful.client.IAlbianHttpClientService;
//import org.albianj.restful.client.MultiParams;
//import org.albianj.restful.client.config.*;
//import org.albianj.restful.client.config.HttpConnection;
//import org.albianj.restful.client.config.HttpHeaders;
//import org.albianj.runtime.AlbianModuleType;
//import org.albianj.service.AlbianServiceException;
//import org.albianj.service.AlbianServiceRouter;
//import org.albianj.service.FreeAlbianService;
//import org.albianj.verify.Validate;
//import org.albianj.xml.Xml2Object;
//import org.apache.http.*;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.entity.GzipDecompressingEntity;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpRequestBase;
//import org.apache.http.config.SocketConfig;
//import org.apache.http.conn.routing.HttpRoute;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
//import org.apache.http.impl.client.HttpClientsConfig;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by xuhaifeng on 17/2/10.
// */
public abstract class FreeAlbianHttpClientService  extends FreeAlbianParserService implements IAlbianHttpClientService {

    private HttpClientAppConfig httpClientAppConfig;
    private String cfName = "httpclient.xml";
    private IReusableObjectPool httpGetPool;
    private IReusableObjectPool httpPostPool;
    private Map<String,IReusableObjectPool> httpClientPools;

    public IReusableObjectPool getHttpGetPool() {
        return httpGetPool;
    }

    public IReusableObjectPool getHttpPostPool() {
        return httpPostPool;
    }

    public IReusableObjectPool getHttpClientPool(String site) {
       if(Validate.isNullOrEmpty(httpClientPools)) return null;
       return httpClientPools.get(site);
    }

    public HttpClientAppConfig getHttpClientAppConfig() {
        return httpClientAppConfig;
    }

    @Override
    public void loading() throws AlbianServiceException, AlbianParserException {
        Document doc = null;
        try {
            String realFilename = findConfigFile(cfName);
            doc = XmlParser.load(realFilename);
        } catch (Exception e) {
            AlbianServiceRouter.throwException("KernelThread",IAlbianLoggerService2.AlbianRunningLoggerName,
                    "load httpclient config error.",e);
        }
        if (null == doc) {
            AlbianServiceRouter.throwException("KernelThread",IAlbianLoggerService2.AlbianRunningLoggerName,
                    "httpclient config is null.","httpclient config is null.");
        }
        httpClientAppConfig = parserHttpClientAppConfig(doc);

        httpGetPool = newHttpGetPool(httpClientAppConfig.getHttpGetPoolingConfig());
        httpPostPool = newHttpPostPool(httpClientAppConfig.getHttpPostPoolingConfig());
        httpClientPools = newHttpClientsPool(httpClientAppConfig.getHttpClientsConfig());
        super.loading();
    }

    protected abstract HttpClientAppConfig parserHttpClientAppConfig(Document doc);

    protected abstract HttpEntityPoolingConfig parserHttpGetPoolingConfig(Document doc);

    protected abstract HttpEntityPoolingConfig parserHttpPostPoolingConfig(Document doc);

    protected abstract HttpClientsConfig parserHttpClientsConfig(Document doc);

    protected abstract HttpEntityPoolingConfig parserHttpClientPoolingConfig(Element elt);

    protected abstract HttpClientHeadersConfig parserHttpClientHeadersConfig(Element elt);

    protected abstract HttpClientSocketConfig parserHttpClientSocketConfig(Element elt);

    protected abstract HttpClientSSLConfig parserHttpClientSSLConfig(Element elt);

    protected abstract IReusableObjectPool newHttpGetPool(HttpEntityPoolingConfig httpGetPoolConfig);

    protected abstract IReusableObjectPool newHttpPostPool(HttpEntityPoolingConfig httpPostPoolConfig);

    protected abstract  Map<String,IReusableObjectPool> newHttpClientsPool(HttpClientsConfig httpClientsConfig);

}
//
//    protected HttpRequestConfig hrc = null;
//    protected Map<String,PoolingHttpClientConnectionManager> clientPool = new HashMap<>();
//
//    @Override
//    public void loading() throws AlbianServiceException {
//        try {
//            String xmlfile =  Path.getExtendResourcePath(KernelSetting
//                    .getAlbianConfigFilePath() + "rservice.xml");
//            try {
//                hrc = (HttpRequestConfig) Xml2Object.convertfile(xmlfile,HttpRequestConfig.class);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (Exception exc) {
//            throw new AlbianServiceException(exc.getMessage(), exc.getCause());
//        }
//
//        RemoteServices rss = hrc.getRemoteServices();
//        if(null == rss){
//
//        }
//
//        Map<String,RemoteService> maps = rss.getRemoteServices();
//        if(Validate.isNullOrEmpty(maps)){
//
//        }
//
//        for(Map.Entry<String,RemoteService> entry : maps.entrySet()){
//            RemoteService rs = entry.getValue();
//            PoolingHttpClientConnectionManager phccm = new PoolingHttpClientConnectionManager();
//            HttpConnectionPool hcp = rs.getHttpConnectionPool();
//            if(null == hcp) hcp = new HttpConnectionPool();
//            phccm.setMaxTotal(hcp.getTotalMax());
//            phccm.setDefaultMaxPerRoute(hcp.getMaxPerRoute());
//            String website = rs.getWebsite();
//            if(Validate.isNullOrEmptyOrAllSpace(website)){
//
//            }
//            phccm.setMaxPerRoute(new HttpRoute(new HttpHost(website,rs.getPort())),hcp.getTotalMax());
//            clientPool.put(rs.getId(),phccm);
//        }
//
//        super.loading();
//    }
//
//
//    protected String makeRequestUrl(RemoteService rs,String path,String service,String action,String queryString){
//        String website = rs.getWebsite();
//        int port = rs.getPort();
//        if(website.endsWith("/")){
//            website = website.substring(website.length() - 1);
//        }
//        StringBuilder url = new StringBuilder();
//        url.append(website);
//        if(80 != port){
//            url.append(":").append(port);
//        }
//        url.append("/").append(path).append("/").append(service).append("/").append(action);
//
//        if(!Validate.isNullOrEmptyOrAllSpace(queryString)){
//            if(queryString.startsWith("?")) {
//                url.append(queryString);
//            } else {
//                url.append("?").append(queryString);
//            }
//        }
//        return url.toString();
//    }
//
//    protected CloseableHttpClient makeRequestClient(HttpRequestBase request,String sessionId,
//                                                  PoolingHttpClientConnectionManager phccm,RemoteService rs){
//        request.addHeader("X-Client-IP", AlbianHost.getLocalIP());
//        request.addHeader("X-Client-RequestId",sessionId);
//        HttpHeaders headers = rs.getHttpHeaders();
//        for(HttpHeader h : headers){
//            request.addHeader(h.getName(),h.getValue());
//        }
//        HttpConnection hc = rs.getHttpConnection();
//        if (null == hc) hc = new HttpConnection();
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setSocketTimeout(hc.getSocketTimeout() * 1000)
//                .setConnectionRequestTimeout(hc.getConnectionRequestTimeout() * 1000)
//                .setConnectTimeout(hc.getConnectTimeout() * 1000)
//                .build();
//
//        DefaultHttpRequestRetryHandler dhrrh = new DefaultHttpRequestRetryHandler(hc.getRetry(), 0 != hc.getRetry());
//        CloseableHttpClient client = null;
//        client = HttpClientsConfig.custom()
//                .setConnectionManager(phccm)
//                .setRetryHandler(dhrrh)
//                .setDefaultRequestConfig(requestConfig)
//                .setDefaultSocketConfig(SocketConfig.copy(SocketConfig.DEFAULT)
//                        .setTcpNoDelay(false)
//                        .setSoKeepAlive(true)
//                        .setSoReuseAddress(true)
//                        .setSoLinger(0)
//                        .setSoTimeout(hc.getSocketTimeout() * 1000)
//                        .build())
//                .build();
//
//        request.setConfig(requestConfig);
//        return client;
//    }
//
//    protected String unzipAndParserRealServer(CloseableHttpResponse response,HttpEntity entity){
//        String realServer = null;
//        Header[] responseHeaders = response.getAllHeaders();
//        // 用于得到返回的文件头
//        for (Header h : responseHeaders) {
//            if (h.getName().equalsIgnoreCase("Content-Encoding") && h.getValue().equalsIgnoreCase("gzip")) {
//                response.setEntity(new GzipDecompressingEntity(entity));
//                continue;
//            }
//            if (h.getName().equalsIgnoreCase("x-real-server")) {
//                realServer = h.getValue();
//                continue;
//            }
//        }
//        return realServer;
//    }
//
//
//
//}
