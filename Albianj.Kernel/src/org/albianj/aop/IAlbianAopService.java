package org.albianj.aop;

import org.albianj.service.IAlbianService;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public interface IAlbianAopService extends IAlbianService {
    void before(IAlbianService service, Method method,Object[] args);
    void after(IAlbianService service, Method method, Object[] args);
    void exception(IAlbianService service, Method method,Throwable t, Object[] args);
}
