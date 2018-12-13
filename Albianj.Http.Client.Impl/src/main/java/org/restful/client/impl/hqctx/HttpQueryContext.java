package org.restful.client.impl.hqctx;

import org.albianj.pooling.IPoolingObject;
import org.albianj.restful.client.IAlbianHttpClientService;
import org.albianj.restful.client.IHttpQueryContext;
import org.albianj.restful.client.KeyValuePair;
import org.albianj.restful.client.config.HttpClientConfig;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.restful.client.impl.hget.CloseableHttpGet;
import org.restful.client.impl.hpost.CloseableHttpPost;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpQueryContext implements IHttpQueryContext {

    private String site;
    private String serviceName;
    private String actionName;
    private List<KeyValuePair> paras;
    private long timeoutMs = 1000;
    private List<KeyValuePair> headers;
    private boolean useSSL = false;
    private boolean ignoreVerify = false;
    Map<String,byte[]> files = new LinkedHashMap<>();

    public IHttpQueryContext to(String site) {
        this.site = site;
        return this;
    }

    public IHttpQueryContext query(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public IHttpQueryContext exec(String actionName) {
        this.actionName = actionName;
        return this;
    }

    public IHttpQueryContext withParas(List<KeyValuePair> paras) {
        this.paras = paras;
        return this;
    }

    public IHttpQueryContext inTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    public IHttpQueryContext addHeaders(List<KeyValuePair> headers) {
        this.headers = headers;
        return this;
    }

    public IHttpQueryContext bySSL() {
        this.useSSL = true;
        return this;
    }

    public IHttpQueryContext bySSLButIgnoreVerify() {
        this.useSSL = true;
        this.ignoreVerify = true;
        return this;
    }

    public IHttpQueryContext addFiles(Map<String,byte[]> files){
        this.files.putAll(files);
        return this;
    }

    public IHttpQueryContext addFile(String filename,byte[] file){
        this.files.put(filename,file);
        return this;
    }

    public byte[] doGet(String sessionId) {
        IAlbianHttpClientService httpClientService = AlbianServiceRouter.getSingletonService(IAlbianHttpClientService.class, "");
        IPoolingObject<CloseableHttpGet> httpgetWapped = null;
        IPoolingObject<CloseableHttpClient> httpClientWapped = null;
        CloseableHttpGet httpget = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse resp = null;
        HttpEntity httpEntity = null;

        try {
            httpgetWapped = httpClientService.getHttpGetPool().getPoolingObject(sessionId);
            httpget = httpgetWapped.getWrappedObject();
            httpClientWapped = httpClientService.getHttpClientPool(this.site).getPoolingObject(sessionId);
            httpClient = httpClientWapped.getWrappedObject();

            if(Validate.isNullOrEmpty(this.headers)){
                for(KeyValuePair kvp : this.headers){
                    httpget.setHeader(kvp.getKey(),kvp.getValue().toString());
                }
            }

            addDefineHeaders(httpget,sessionId);

            HttpClientConfig hcConfig =  httpClientService.getHttpClientAppConfig().getHttpClientsConfig().get(this.site);

            String url = String.format("%s/%s/%s",hcConfig.getHost(),this.serviceName,this.actionName);
            httpget.setURI(URI.create(url));

            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
            requestConfigBuilder.setConnectionRequestTimeout((int) this.timeoutMs);
            requestConfigBuilder.setConnectTimeout((int) this.timeoutMs);
            requestConfigBuilder.setContentCompressionEnabled(true);
            requestConfigBuilder.setRedirectsEnabled(true);
            requestConfigBuilder.setSocketTimeout((int) this.timeoutMs);
            RequestConfig rconf = requestConfigBuilder.build();
            httpget.setConfig(rconf);

            resp = httpClient.execute(httpget);
            StatusLine status = resp.getStatusLine();
            if (status.getStatusCode() != 200) {

            }
            httpEntity = resp.getEntity();
           return  EntityUtils.toByteArray(httpEntity);
        } catch (Exception e) {

        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (Exception ee) {

            }
            try {
                resp.close();
            } catch (Exception ee) {

            }
            try {
                httpget.reset();
                httpClientService.getHttpGetPool().returnPoolingObject(httpgetWapped);
            } catch (Exception ee) {

            }
            try {
                httpClientService.getHttpClientPool(site).returnPoolingObject(httpgetWapped);
            } catch (Exception ee) {

            }
        }
        return null;
    }

    public byte[] doPost(String sessionId) {
//        FileEntity fe = new FileEntity();
//        StringEntity se = new StringEntity("","");
        try {
            FileBody fileBody = new FileBody(new File(""));
            StringBody stringBody = new StringBody("");
            HttpPost httpPost = new HttpPost("");
            MultipartEntityBuilder meb = MultipartEntityBuilder.create();
            meb.addPart("name", fileBody);
            meb.addPart("Sname",stringBody);
            HttpEntity httpEntity =  meb.build();
            httpPost.setEntity(httpEntity);
        }catch (Exception e){

        }
//        NameValuePair nameValuePair = new FileNa("","");
//        eb.setParameters()
//        HttpEntity httpEntity =  eb.build();
//        CloseableHttpPost httpPost = null;
//        httpPost.setEntity(httpEntity);
//        StringBody userName = new StringBody("Scott", ContentType.create(
//                                     "text/plain", Consts.UTF_8));
        return null;
    }

    private static void addDefineHeaders(HttpRequestBase req,String sessionId){
        req.setHeader("X-SessionId",sessionId);
        req.addHeader("Cache-Control","no-cache no-store");
        req.addHeader("Pragma","no-cache");
    }


}
