package org.albianj.mvc.service;

import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.mvc.config.RequestCounter;
import org.albianj.service.BuiltinNames;
import org.albianj.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * 防刷机制服务
 * 注意，使用防刷机制的时候，如果web有负载均衡，必须使用hash（client-ip）的方式，否则防刷无效
 * 如果无法使用hash（client-ip）的机制，那么必须更改防刷的存储机制，目前使用session存储
 * Created by xuhaifeng on 17/1/19.
 */

public interface IBrushingService extends IService {

    String Name = BuiltinNames.AlbianBrushingServiceName;

    void setHttpConfigurtion(AlbianHttpConfigurtion c);

    boolean consume(HttpServletRequest request);

    public RequestCounter getRequestCounter(HttpServletRequest request, String ip);

    public void storeRequestCounter(HttpServletRequest request, String ip, RequestCounter counter);
}
