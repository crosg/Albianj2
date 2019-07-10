package org.albianj.aop;

import org.albianj.service.FreeAlbianService;
import org.albianj.service.IAlbianService;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public abstract class FreeAlbianAopService extends FreeAlbianService implements IAlbianAopService {

    public void before(IAlbianAopContext ctx, IAlbianService service, Method method, Object[] args) {
        return;
    }

    public void after(IAlbianAopContext ctx, IAlbianService service, Method method, Object rc, Throwable t, Object[] args) {
        return;
    }

}
