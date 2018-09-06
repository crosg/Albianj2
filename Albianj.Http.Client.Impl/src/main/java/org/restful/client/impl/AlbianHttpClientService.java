package org.restful.client.impl;//package org.restful.client.impl;
//
//import org.albianj.datetime.AlbianDateTime;
//import org.albianj.logger.AlbianLoggerLevel;
//import org.albianj.logger.IAlbianLoggerService2;
//import org.albianj.restful.client.MultiParams;
//import org.albianj.restful.client.config.RemoteService;
//import org.albianj.runtime.AlbianModuleType;
//import org.albianj.service.AlbianServiceRouter;
//import org.albianj.verify.Validate;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpStatus;
//import org.apache.http.StatusLine;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpRequestBase;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by xuhaifeng on 17/2/10.
// */
//public class AlbianHttpClientService extends  FreeAlbianHttpClientService {
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
