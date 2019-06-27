package org.albianj.aop;

import org.albianj.service.IService;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public interface IAspectService extends IService {
    void before(IAspectContext ctx, IService service, Method method, Object[] args);

    void after(IAspectContext ctx, IService service, Method method, Object rc, Throwable t, Object[] args);
}
