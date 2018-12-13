package org.restful.client.impl;//package org.restful.client.impl;

import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.pooling.IReusableObjectPool;
import org.albianj.pooling.impl.ReusableObjectPoolMgr;
import org.albianj.restful.client.IHttpQueryContext;
import org.albianj.restful.client.config.*;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.restful.client.impl.hclient.HttpClientFactory;
import org.restful.client.impl.hget.HttpGetFactory;
import org.restful.client.impl.hpost.HttpPostFactory;
import org.restful.client.impl.hqctx.HttpQueryContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlbianHttpClientService extends  FreeAlbianHttpClientService {

    @Override
    protected HttpClientAppConfig parserHttpClientAppConfig(Document doc) {
        HttpEntityPoolingConfig httpGetPools = parserHttpGetPoolingConfig(doc);
        HttpEntityPoolingConfig httpPostPools = parserHttpPostPoolingConfig(doc);
        HttpClientsConfig httpClientsConfig = parserHttpClientsConfig(doc);
        HttpClientAppConfig appConfig = new HttpClientAppConfig();
        appConfig.setHttpClientsConfig(httpClientsConfig);
        appConfig.setHttpGetPoolingConfig(httpGetPools);
        appConfig.setHttpPostPoolingConfig(httpPostPools);
        return appConfig;
    }

    private HttpEntityPoolingConfig paraerPoolsConfig(Element poolsNode){
        HttpEntityPoolingConfig poolsConfig = new HttpEntityPoolingConfig();

        String sName = XmlParser.getValueByAttrOrChileNode(poolsNode,"Name");
        poolsConfig.setPoolName(sName);
        String sMinPoolSize = XmlParser.getValueByAttrOrChileNode(poolsNode,"MinPoolSize");
        if(!Validate.isNullOrEmptyOrAllSpace(sMinPoolSize)){
            poolsConfig.setMinPoolingCount(Integer.parseInt(sMinPoolSize));
        }
        String sMaxPoolSize = XmlParser.getValueByAttrOrChileNode(poolsNode,"MaxPoolSize");
        if(!Validate.isNullOrEmptyOrAllSpace(sMaxPoolSize)){
            poolsConfig.setMaxPoolingCount(Integer.parseInt(sMaxPoolSize));
        }
        String sWaitTimeWhenGetMs = XmlParser.getValueByAttrOrChileNode(poolsNode,"WaitTimeWhenGetMs");
        if(!Validate.isNullOrEmptyOrAllSpace(sWaitTimeWhenGetMs)){
            poolsConfig.setWaitTimeWhenGetMs(Long.parseLong(sWaitTimeWhenGetMs));
        }
        String sLifeCycleTimeMs = XmlParser.getValueByAttrOrChileNode(poolsNode,"LifeCycleTimeMs");
        if(!Validate.isNullOrEmptyOrAllSpace(sLifeCycleTimeMs)){
            poolsConfig.setLifeCycleTimeMs(Long.parseLong(sLifeCycleTimeMs));
        }
        String sWaitInFreePoolMs = XmlParser.getValueByAttrOrChileNode(poolsNode,"WaitInFreePoolMs");
        if(!Validate.isNullOrEmptyOrAllSpace(sWaitInFreePoolMs)){
            poolsConfig.setWaitInFreePoolMs(Long.parseLong(sWaitInFreePoolMs));
        }
        String sMaxRemedyObjectCount = XmlParser.getValueByAttrOrChileNode(poolsNode,"MaxRemedyObjectCount");
        if(!Validate.isNullOrEmptyOrAllSpace(sMaxRemedyObjectCount)){
            poolsConfig.setMaxRemedyObjectCount(Integer.parseInt(sMaxRemedyObjectCount));
        }
        String sCleanupTimestampMs = XmlParser.getValueByAttrOrChileNode(poolsNode,"CleanupTimestampMs");
        if(!Validate.isNullOrEmptyOrAllSpace(sCleanupTimestampMs)){
            poolsConfig.setCleanupTimestampMs(Long.parseLong(sCleanupTimestampMs));
        }
        String sMaxRequestTimeMs = XmlParser.getValueByAttrOrChileNode(poolsNode,"MaxRequestTimeMs");
        if(!Validate.isNullOrEmptyOrAllSpace(sMaxRequestTimeMs)){
            poolsConfig.setMaxRequestTimeMs(Long.parseLong(sMaxRequestTimeMs));
        }
        return poolsConfig;
    }

    @Override
    protected HttpEntityPoolingConfig parserHttpGetPoolingConfig(Document doc) {
        Element poolsNode = XmlParser.selectNode(doc,"HttpClientAppConfig/HttpGet");
        if(null == poolsNode) return new HttpEntityPoolingConfig();
        return paraerPoolsConfig(poolsNode);
    }

    @Override
    protected HttpEntityPoolingConfig parserHttpPostPoolingConfig(Document doc) {
        Element poolsNode = XmlParser.selectNode(doc,"HttpClientAppConfig/HttpPost");
        if(null == poolsNode) return new HttpEntityPoolingConfig();
        return paraerPoolsConfig(poolsNode);
    }

    @Override
    protected HttpClientsConfig parserHttpClientsConfig(Document doc) {
        HttpClientsConfig httpClientsConfig = new HttpClientsConfig();
        Element httpClientsNode = XmlParser.selectNode(doc,"HttpClientAppConfig/HttpClients");
        if(null == httpClientsNode){
            AlbianServiceRouter.throwException("KernelThread", IAlbianLoggerService2.AlbianRunningLoggerName,
                    "No HttpClient Config.","No HttpClient Config");
        }
        List httpClientNodes = XmlParser.getChildNodes(httpClientsNode,"HttpClient");
        if(Validate.isNullOrEmpty(httpClientNodes)){
            AlbianServiceRouter.throwException("KernelThread", IAlbianLoggerService2.AlbianRunningLoggerName,
                    "HttpClient Config is null or empty.","No HttpClient Config");
        }
        for(Object node : httpClientNodes){
            HttpClientConfig httpClientConfig = new HttpClientConfig();
            Element elt = (Element) node;

            String sSite = XmlParser.getValueByAttrOrChileNode(elt,"Site");
            if(Validate.isNullOrEmptyOrAllSpace(sSite)){
                AlbianServiceRouter.throwException("KernelThread", IAlbianLoggerService2.AlbianRunningLoggerName,
                        "Site in HttpClient Config is null or empty.","No HttpClient Config");
            }
            httpClientConfig.setSite(sSite);

            String sHost = XmlParser.getValueByAttrOrChileNode(elt,"Host");
            if(Validate.isNullOrEmptyOrAllSpace(sHost)){
                AlbianServiceRouter.throwException("KernelThread", IAlbianLoggerService2.AlbianRunningLoggerName,
                        "Host in HttpClient Config is null or empty.","No HttpClient Config");
            }
            httpClientConfig.setHost(sHost);

            HttpEntityPoolingConfig httpClientPoolingConfig = null;
            Node httpClientPoolingNode = XmlParser.getChildNode(elt,"Pooling");
            if(null == httpClientPoolingNode) {
                httpClientPoolingConfig = new HttpEntityPoolingConfig();
            } else {
                httpClientPoolingConfig = parserHttpClientPoolingConfig((Element) httpClientPoolingNode);
            }
            httpClientConfig.setHttpClientPoolingConfig(httpClientPoolingConfig);

            Node headersNode = XmlParser.getChildNode(elt,"Headers");
            if(null != headersNode){
                HttpClientHeadersConfig headersConfig = parserHttpClientHeadersConfig((Element) headersNode);
                httpClientConfig.setHttpClientHeadersConfig(headersConfig);
            }

            Node sslNode = XmlParser.getChildNode(elt,"SSL");
            if(null != sslNode){
                HttpClientSSLConfig sslConfig =  parserHttpClientSSLConfig((Element)sslNode);
                httpClientConfig.setHttpClientSSLConfig(sslConfig);
            }

            Node sktNode = XmlParser.getChildNode(elt,"Socket");
            if(null != sktNode){
                HttpClientSocketConfig sktConfig =  parserHttpClientSocketConfig((Element)sktNode);
                httpClientConfig.setHttpClientSocketConfig(sktConfig);
            }
            httpClientsConfig.put(sSite,httpClientConfig);
        }
        return httpClientsConfig;
    }

    @Override
    protected HttpEntityPoolingConfig parserHttpClientPoolingConfig(Element elt) {
        return paraerPoolsConfig(elt);
    }

    private HttpClientHeaderConfig parserHttpClientHeaderConfig(Element elt){
        HttpClientHeaderConfig headerConfig = new HttpClientHeaderConfig();
        String sKey = XmlParser.getValueByAttrOrChileNode(elt,"Key");
        if(!Validate.isNullOrEmptyOrAllSpace(sKey)) {
            headerConfig.setKey(sKey);
            String sVal = XmlParser.getValueByAttrOrChileNode(elt, "Value");
            headerConfig.setValue(sVal);
        }
        return headerConfig;
    }

    @Override
    protected HttpClientHeadersConfig parserHttpClientHeadersConfig(Element elt) {
        HttpClientHeadersConfig headersConfig = null;
        List headerNodes = XmlParser.getChildNodes(elt,"Header");
        if(!Validate.isNullOrEmpty(headerNodes)) {
            headersConfig = new HttpClientHeadersConfig();
            for(Object hObj : headerNodes){
                HttpClientHeaderConfig headerConfig = parserHttpClientHeaderConfig((Element) hObj);
                headersConfig.addNode(headerConfig.getKey(),headerConfig);
            }
        }
        return Validate.isNullOrEmpty(headersConfig) ? null : headersConfig;
    }

    @Override
    protected HttpClientSocketConfig parserHttpClientSocketConfig(Element elt) {
        HttpClientSocketConfig sktConfig = new HttpClientSocketConfig();
        String sNoDelay = XmlParser.getValueByAttrOrChileNode(elt,"NoDelay");
        if(!Validate.isNullOrEmptyOrAllSpace(sNoDelay)){
            sktConfig.setTcpNoDelay(Boolean.parseBoolean(sNoDelay));
        }
        String sBacklogSizeB = XmlParser.getValueByAttrOrChileNode(elt,"BacklogSizeB");
        if(!Validate.isNullOrEmptyOrAllSpace(sBacklogSizeB)){
            sktConfig.setBacklogSizeB(Long.parseLong(sBacklogSizeB));
        }
        String sRcvBufSizeB = XmlParser.getValueByAttrOrChileNode(elt,"RcvBufSizeB");
        if(!Validate.isNullOrEmptyOrAllSpace(sRcvBufSizeB)){
            sktConfig.setRcvBufSizeB(Long.parseLong(sRcvBufSizeB));
        }
        String sSndBufSizeB = XmlParser.getValueByAttrOrChileNode(elt,"SndBufSizeB");
        if(!Validate.isNullOrEmptyOrAllSpace(sSndBufSizeB)){
            sktConfig.setSndBufSizeB(Long.parseLong(sSndBufSizeB));
        }
        String sSoKeepAlive = XmlParser.getValueByAttrOrChileNode(elt,"SoKeepAlive");
        if(!Validate.isNullOrEmptyOrAllSpace(sSoKeepAlive)){
            sktConfig.setSoKeepAlive(Boolean.parseBoolean(sSoKeepAlive));
        }
        String sSoLinger = XmlParser.getValueByAttrOrChileNode(elt,"SoLinger");
        if(!Validate.isNullOrEmptyOrAllSpace(sSoLinger)){
            sktConfig.setSoLinger(Integer.parseInt(sSoLinger));
        }
        String sSoReuseAddress = XmlParser.getValueByAttrOrChileNode(elt,"SoReuseAddress");
        if(!Validate.isNullOrEmptyOrAllSpace(sSoReuseAddress)){
            sktConfig.setSoReuseAddress(Boolean.parseBoolean(sSoReuseAddress));
        }
        String sSoTimeoutMs = XmlParser.getValueByAttrOrChileNode(elt,"SoTimeoutMs");
        if(!Validate.isNullOrEmptyOrAllSpace(sSoTimeoutMs)){
            sktConfig.setSoTimeoutMs(Long.parseLong(sSoTimeoutMs));
        }
        String sBufferSizeB = XmlParser.getValueByAttrOrChileNode(elt,"BufferSizeB");
        if(!Validate.isNullOrEmptyOrAllSpace(sBufferSizeB)){
            sktConfig.setBufferSizeB(Long.parseLong(sBufferSizeB));
        }
        String sCharset = XmlParser.getValueByAttrOrChileNode(elt,"Charset");
        if(!Validate.isNullOrEmptyOrAllSpace(sCharset)){
            sktConfig.setCharset(sCharset);
        }
        String sConnectionRequestTimeoutMs = XmlParser.getValueByAttrOrChileNode(elt,"ConnectionRequestTimeoutMs");
        if(!Validate.isNullOrEmptyOrAllSpace(sConnectionRequestTimeoutMs)){
            sktConfig.setConnectionRequestTimeoutMs(Long.parseLong(sConnectionRequestTimeoutMs));
        }
        String sConnectTimeoutMs = XmlParser.getValueByAttrOrChileNode(elt,"ConnectTimeoutMs");
        if(!Validate.isNullOrEmptyOrAllSpace(sConnectTimeoutMs)){
            sktConfig.setConnectTimeoutMs(Long.parseLong(sConnectTimeoutMs));
        }
        String sContentCompressionEnabled = XmlParser.getValueByAttrOrChileNode(elt,"ContentCompressionEnabled");
        if(!Validate.isNullOrEmptyOrAllSpace(sContentCompressionEnabled)){
            sktConfig.setContentCompressionEnabled(Boolean.parseBoolean(sContentCompressionEnabled));
        }
        String sRedirectsEnabled = XmlParser.getValueByAttrOrChileNode(elt,"RedirectsEnabled");
        if(!Validate.isNullOrEmptyOrAllSpace(sRedirectsEnabled)){
            sktConfig.setRedirectsEnabled(Boolean.parseBoolean(sRedirectsEnabled));
        }
        String sSocketTimeoutMs = XmlParser.getValueByAttrOrChileNode(elt,"SocketTimeoutMs");
        if(!Validate.isNullOrEmptyOrAllSpace(sSocketTimeoutMs)){
            sktConfig.setSocketTimeoutMs(Long.parseLong(sSocketTimeoutMs));
        }
        String sNetworkInterface = XmlParser.getValueByAttrOrChileNode(elt,"NetworkInterface");
        if(!Validate.isNullOrEmptyOrAllSpace(sNetworkInterface)){
            sktConfig.setNetworkInterface(sNetworkInterface);
        }

//        <NoDelay></NoDelay>
//            <BacklogSizeB></BacklogSizeB>
//            <RcvBufSizeB></RcvBufSizeB>
//            <SndBufSizeB></SndBufSizeB>
//            <SoKeepAlive></SoKeepAlive>
//            <SoLinger></SoLinger>
//            <SoReuseAddress></SoReuseAddress>
//            <SoTimeoutMs></SoTimeoutMs>
//            <BufferSizeB></BufferSizeB>
//            <Charset></Charset>
//            <ConnectionRequestTimeoutMs></ConnectionRequestTimeoutMs>
//            <ConnectTimeoutMs></ConnectTimeoutMs>
//            <ContentCompressionEnabled></ContentCompressionEnabled>
//            <RedirectsEnabled></RedirectsEnabled>
//            <SocketTimeoutMs></SocketTimeoutMs>
//            <NetworkInterface></NetworkInterface>
        return null;
    }

    @Override
    protected HttpClientSSLConfig parserHttpClientSSLConfig(Element elt) {
        HttpClientSSLConfig sslConfig = new HttpClientSSLConfig();
        String sEanble = XmlParser.getValueByAttrOrChileNode(elt,"Enable");
        if(!Validate.isNullOrEmptyOrAllSpace(sEanble)){
            sslConfig.setEnable(Boolean.parseBoolean(sEanble));
        }
        String sByPass = XmlParser.getValueByAttrOrChileNode(elt,"ByPass");
        if(!Validate.isNullOrEmptyOrAllSpace(sByPass)){
            sslConfig.setByPass(Boolean.parseBoolean(sByPass));
        }
        String sKeyStorePath = XmlParser.getValueByAttrOrChileNode(elt,"KeyStorePath");
        if(!Validate.isNullOrEmptyOrAllSpace(sKeyStorePath)){
            sslConfig.setKeyStorePath(sKeyStorePath);
        }
        String sKeyStorePwd = XmlParser.getValueByAttrOrChileNode(elt,"KeyStorePwd");
        if(!Validate.isNullOrEmptyOrAllSpace(sKeyStorePwd)) {
            sslConfig.setKeyStorePwd(sKeyStorePwd);
        }
        return sslConfig;
    }

    protected  IReusableObjectPool newHttpGetPool(HttpEntityPoolingConfig httpGetPoolConfig){
        IReusableObjectPool httpGetPool = ReusableObjectPoolMgr.newObjectPool(httpGetPoolConfig,new HttpGetFactory(),null);
        return httpGetPool;
    }

    protected  IReusableObjectPool newHttpPostPool(HttpEntityPoolingConfig httpPostPoolConfig){
        IReusableObjectPool httpPostPool = ReusableObjectPoolMgr.newObjectPool(httpPostPoolConfig,new HttpPostFactory(),null);
        return httpPostPool;
    }

    protected Map<String,IReusableObjectPool> newHttpClientsPool(HttpClientsConfig httpClientsConfig){
        if(null == httpClientsConfig) return null;
        Map<String,IReusableObjectPool> pool = new LinkedHashMap<>();
        for(HttpClientConfig httpClientConfig : httpClientsConfig.values()){
            IReusableObjectPool httpClientPool = ReusableObjectPoolMgr.newObjectPool(httpClientConfig.getHttpClientPoolingConfig(),new HttpClientFactory(),httpClientConfig);
            pool.put(httpClientConfig.getSite(),httpClientPool);
        }
        return pool;
    }


    public IHttpQueryContext newHttpQueryContext(){
//        IHttpQueryContext hqCtx = null;
//        hqCtx.to(url).query(service).exec(actionName).withParas().inTimeoutMs(100).addHeaders().bySSL().doGet();
//        hqCtx.to(url).query(service).exec(actionName).withParas().inTimeoutMs(100).addHeaders().bySSLButIgroneVafiye().doPost();
        return new HttpQueryContext();
    }
}
//
//    public String getServiceName(){
//        return Name;
//    }
//
//    public byte[] sendGetRequest(String id,String sessionId,
//                                 String path,String service,String action,String queryString) {
//        PoolingHttpClientConnectionManager phccm = clientPool.get(id);
//        if(null == phccm){
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                    AlbianLoggerLevel.Error,
//                    null, AlbianModuleType.RestfulClient,
//                    "connect service is fail.","get pooling http client is fail.pool id : %s.", id);
//        }
//
//
//        RemoteService rs = hrc.getRemoteServices().get(id);
//        if(null == rs){
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                    AlbianLoggerLevel.Error,
//                    null, AlbianModuleType.RestfulClient,
//                    "connect service is fail.","get remote service is null.service id : %s.", id);
//        }
//
//        String url = makeRequestUrl(rs,path,service,action,queryString);
//        HttpRequestBase httpget = null;
//        CloseableHttpClient client = null;
//        CloseableHttpResponse response = null;
//        HttpEntity entity = null;
//        try {
//            httpget = new HttpGet(url.toString());
//            client = makeRequestClient(httpget, sessionId,phccm, rs);
//
//            response = client.execute(httpget);
//            StatusLine status = response.getStatusLine();
//            entity = response.getEntity();
//            String realServer = unzipAndParserRealServer(response,entity);
//            if(status.getStatusCode() == HttpStatus.SC_OK) {
//                byte[] bytes = EntityUtils.toByteArray(entity);
//                return bytes;
//            }else {//fail
//                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                        AlbianLoggerLevel.Error,
//                        null, AlbianModuleType.RestfulClient,
//                        "connect service is fail.",
//                        "connect to service:%s with url:%s is success,but response is fail.state code:%d.", id,url,status.getStatusCode());
//                return null;
//            }
//        }catch (Exception e){
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                    AlbianLoggerLevel.Error,
//                    e, AlbianModuleType.RestfulClient,
//                    "connect service is fail.",
//                    "connect to service:%s with url:%s is excepted.", id,url);
//        }finally {
//            if(null != httpget) {
//                httpget.releaseConnection();
//            }
//            if(null != client){
//                try {
//                    client.close();
//                } catch (IOException e) {
//
//                }
//            }
//            if(null != entity){
//                try {
//                    EntityUtils.consume(entity);
//                } catch (IOException e) {
//                }
//            }
//            if(null != response){
//                try {
//                    response.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//        return null;
//    }
//
//    public byte[] sendPostRequest(String id, String sessionId,
//                                  String path, String service, String action,
//                                  String queryString, BasicNameValuePair... formParams) {
//        PoolingHttpClientConnectionManager phccm = clientPool.get(id);
//        if(null == phccm){
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                    AlbianLoggerLevel.Error,
//                    null, AlbianModuleType.RestfulClient,
//                    "connect service is fail.","get pooling http client is fail.pool id : %s.", id);
//        }
//
//
//        RemoteService rs = hrc.getRemoteServices().get(id);
//        if(null == rs){
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                    AlbianLoggerLevel.Error,
//                    null, AlbianModuleType.RestfulClient,
//                    "connect service is fail.","get remote service is null.service id : %s.", id);
//        }
//
//        String url = makeRequestUrl(rs,path,service,action,queryString);
//        HttpRequestBase httppost = null;
//        CloseableHttpClient client = null;
//        CloseableHttpResponse response = null;
//        HttpEntity entityRequest = null;
//        HttpEntity entityResponse = null;
//        try {
//            httppost = new HttpPost(url.toString());
//            client = makeRequestClient(httppost, sessionId,phccm, rs);
//
//            if(null != formParams){
//                ArrayList<BasicNameValuePair> array = new ArrayList();
//                for (BasicNameValuePair nvp : formParams){
//                    array.add(nvp);
//                }
//                entityRequest = new UrlEncodedFormEntity(array, "UTF-8");
//                ((HttpPost)httppost).setEntity(entityRequest);
//            }
//
//            long begin = AlbianDateTime.getCurrentMillis();
//            response = client.execute(httppost);
//            long end = AlbianDateTime.getCurrentMillis();
//            StatusLine status = response.getStatusLine();
//            entityResponse = response.getEntity();
//            String realServer = unzipAndParserRealServer(response,entityResponse);
//            if(status.getStatusCode() == HttpStatus.SC_OK) {
//                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                        AlbianLoggerLevel.Info,
//                        "connect to server:%s to deal service:%s with url:%s is success,execute time:%d.",
//                        realServer,id,url,end - begin);
//                byte[] bytes = EntityUtils.toByteArray(entityResponse);
//                return bytes;
//            }else {//fail
//                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                        AlbianLoggerLevel.Error,
//                        null, AlbianModuleType.RestfulClient,
//                        "connect service is fail.",
//                        "connect to service:%s with url:%s is success,but response is fail.state code:%d.execute time:%d.",
//                        id,url,status.getStatusCode(),end - begin);
//                return null;
//            }
//        }catch (Exception e){
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                    AlbianLoggerLevel.Error,
//                    e, AlbianModuleType.RestfulClient,
//                    "connect service is fail.",
//                    "connect to service:%s with url:%s is excepted.", id,url);
//        }finally {
//            if(null != httppost) {
//                httppost.releaseConnection();
//            }
//            if(null != client){
//                try {
//                    client.close();
//                } catch (IOException e) {
//
//                }
//            }
//            if(null != entityRequest){
//                try {
//                    EntityUtils.consume(entityRequest);
//                } catch (IOException e) {
//                }
//            }
//            if(null != entityResponse){
//                try {
//                    EntityUtils.consume(entityResponse);
//                } catch (IOException e) {
//                }
//            }
//            if(null != response){
//                try {
//                    response.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//        return null;
//    }
//
//    public byte[] sendPostRequest(String id, String sessionId,
//                                  String path, String service, String action,
//                                  String queryString, String json) {
//
//        PoolingHttpClientConnectionManager phccm = clientPool.get(id);
//        if(null == phccm){
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                    AlbianLoggerLevel.Error,
//                    null, AlbianModuleType.RestfulClient,
//                    "connect service is fail.","get pooling http client is fail.pool id : %s.", id);
//        }
//
//
//        RemoteService rs = hrc.getRemoteServices().get(id);
//        if(null == rs){
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                    AlbianLoggerLevel.Error,
//                    null, AlbianModuleType.RestfulClient,
//                    "connect service is fail.","get remote service is null.service id : %s.", id);
//        }
//
//        String url = makeRequestUrl(rs,path,service,action,queryString);
//        HttpRequestBase httppost = null;
//        CloseableHttpClient client = null;
//        CloseableHttpResponse response = null;
//        HttpEntity entityResponse = null;
//        StringEntity postingString = null;
//        try {
//            httppost = new HttpPost(url.toString());
//            client = makeRequestClient(httppost, sessionId,phccm, rs);
//
//            postingString = new StringEntity(json, ContentType.APPLICATION_JSON);//gson.tojson() converts your pojo to json
//            ((HttpPost)httppost).setEntity(postingString);
//            httppost.setHeader("Content-type", "application/json");
//
//            long begin = AlbianDateTime.getCurrentMillis();
//            response = client.execute(httppost);
//            long end = AlbianDateTime.getCurrentMillis();
//            StatusLine status = response.getStatusLine();
//            entityResponse = response.getEntity();
//            String realServer = unzipAndParserRealServer(response,entityResponse);
//            if(status.getStatusCode() == HttpStatus.SC_OK) {
//                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                        AlbianLoggerLevel.Info,
//                        "connect to server:%s to deal service:%s with url:%s is success,execute time:%d.",
//                        realServer,id,url,end - begin);
//                byte[] bytes = EntityUtils.toByteArray(entityResponse);
//                return bytes;
//            }else {//fail
//                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                        AlbianLoggerLevel.Error,
//                        null, AlbianModuleType.RestfulClient,
//                        "connect service is fail.",
//                        "connect to service:%s with url:%s is success,but response is fail.state code:%d.execute time:%d.",
//                        id,url,status.getStatusCode(),end - begin);
//                return null;
//            }
//        }catch (Exception e){
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                    AlbianLoggerLevel.Error,
//                    e, AlbianModuleType.RestfulClient,
//                    "connect service is fail.",
//                    "connect to service:%s with url:%s is excepted.", id,url);
//        }finally {
//            if(null != httppost) {
//                httppost.releaseConnection();
//            }
//            if(null != client){
//                try {
//                    client.close();
//                } catch (IOException e) {
//
//                }
//            }
//            if(null != entityResponse){
//                try {
//                    EntityUtils.consume(entityResponse);
//                } catch (IOException e) {
//                }
//            }
//            if(null != postingString){
//                try {
//                    EntityUtils.consume(postingString);
//                } catch (IOException e) {
//                }
//            }
//            if(null != response){
//                try {
//                    response.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//        return null;
//    }
//
//    public byte[] sendPostMultiRequest(String id, String sessionId,
//                                       String path, String service, String action,
//                                       String queryString,
//                                       List<MultiParams> multiParams,
//                                       BasicNameValuePair... formParams){
//
//        PoolingHttpClientConnectionManager phccm = clientPool.get(id);
//        if(null == phccm){
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                    AlbianLoggerLevel.Error,
//                    null, AlbianModuleType.RestfulClient,
//                    "connect service is fail.","get pooling http client is fail.pool id : %s.", id);
//        }
//
//
//        RemoteService rs = hrc.getRemoteServices().get(id);
//        if(null == rs){
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                    AlbianLoggerLevel.Error,
//                    null, AlbianModuleType.RestfulClient,
//                    "connect service is fail.","get remote service is null.service id : %s.", id);
//        }
//
//        String url = makeRequestUrl(rs,path,service,action,queryString);
//        HttpRequestBase httppost = null;
//        CloseableHttpClient client = null;
//        CloseableHttpResponse response = null;
//        HttpEntity entityRequest = null;
//        HttpEntity entityResponse = null;
//        try {
//            httppost = new HttpPost(url.toString());
//            client = makeRequestClient(httppost, sessionId,phccm, rs);
//
//            MultipartEntityBuilder meb =  MultipartEntityBuilder.create();
//            for(MultiParams mp : multiParams){
//                if(Validate.isNullOrEmptyOrAllSpace(mp.getFilename())){
//                    meb.addBinaryBody(mp.getName(),mp.getBody());
//                } else {
//                    meb.addBinaryBody(mp.getName(), mp.getBody(), ContentType.create(mp.getMimeType()), mp.getFilename());
//                }
//            }
//            if(null != formParams){
//                for(BasicNameValuePair bnvp : formParams){
//                    meb.addTextBody(bnvp.getName(),bnvp.getValue());
//                }
//            }
//
//            entityRequest = meb.build();
//            ((HttpPost)httppost).setEntity(entityRequest);
//
//            long begin = AlbianDateTime.getCurrentMillis();
//            response = client.execute(httppost);
//            long end = AlbianDateTime.getCurrentMillis();
//            StatusLine status = response.getStatusLine();
//            entityResponse = response.getEntity();
//            String realServer = unzipAndParserRealServer(response,entityResponse);
//            if(status.getStatusCode() == HttpStatus.SC_OK) {
//                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                        AlbianLoggerLevel.Info,
//                        "connect to server:%s to deal service:%s with url:%s is success,execute time:%d.",
//                        realServer,id,url,end - begin);
//                byte[] bytes = EntityUtils.toByteArray(entityResponse);
//                return bytes;
//            }else {//fail
//                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                        AlbianLoggerLevel.Error,
//                        null, AlbianModuleType.RestfulClient,
//                        "connect service is fail.",
//                        "connect to service:%s with url:%s is success,but response is fail.state code:%d.execute time:%d.",
//                        id,url,status.getStatusCode(),end - begin);
//                return null;
//            }
//        }catch (Exception e){
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,sessionId,
//                    AlbianLoggerLevel.Error,
//                    e, AlbianModuleType.RestfulClient,
//                    "connect service is fail.",
//                    "connect to service:%s with url:%s is excepted.", id,url);
//        }finally {
//            if(null != httppost) {
//                httppost.releaseConnection();
//            }
//            if(null != client){
//                try {
//                    client.close();
//                } catch (IOException e) {
//
//                }
//            }
//            if(null != entityResponse){
//                try {
//                    EntityUtils.consume(entityResponse);
//                } catch (IOException e) {
//                }
//            }
//            if(null != entityRequest){
//                try {
//                    EntityUtils.consume(entityRequest);
//                } catch (IOException e) {
//                }
//            }
//            if(null != response){
//                try {
//                    response.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//        return null;
//    }
//}
