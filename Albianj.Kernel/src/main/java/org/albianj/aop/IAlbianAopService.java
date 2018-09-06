package org.albianj.aop;

import org.albianj.service.IAlbianService;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public interface IAlbianAopService extends IAlbianService {
    void before(IAlbianAopContext ctx,IAlbianService service, Method method, Object[] args);

    void after(IAlbianAopContext ctx,IAlbianService service, Method method, Object rc, Throwable t,Object[] args);
}
