package org.albianj.restful.client;

import org.albianj.pooling.IReusableObjectPool;
import org.albianj.restful.client.config.HttpClientAppConfig;
import org.albianj.service.IAlbianService;

import java.util.List;

/**
 * Created by xuhaifeng on 17/2/7.
 */
@org.albianj.comment.Comments("连接albianj restful service的客户端服务")
public interface IAlbianHttpClientService extends IAlbianService {

    public HttpClientAppConfig getHttpClientAppConfig();

    public IReusableObjectPool getHttpGetPool() ;

    public IReusableObjectPool getHttpPostPool() ;

    public IReusableObjectPool getHttpClientPool(String site) ;

//    String Name = "AlbianHttpClientService";
//    IHttpQueryContext hqCtx = IAlbianHttpClientService.newHttpQueryContext();
//    String resp = hqctx.to(site).addHeader().addQueryString().doGet(service,action);
//    hqctx.to(site).addHeader().addQueryString().doPost(service,action);
//     hqctx.to(site).addHeader().addQueryString().withSSL().doPost(service,action);

//    public byte[] sendGetRequest(String id, String sessionId,
//                                 String path, String service, String action, String queryString);
//
//    public byte[] sendPostRequest(String id, String sessionId,
//                                  String path, String service, String action,
//                                  String queryString, KeyValuePair... formParams);
//
//    public byte[] sendPostRequest(String id, String sessionId,
//                                  String path, String service, String action,
//                                  String queryString, String json);
//
//    public byte[] sendPostMultiRequest(String id, String sessionId,
//                                       String path, String service, String action,
//                                       String queryString,
//                                       List<MultiParams> multiParams,
//                                       KeyValuePair... formParams);
}
