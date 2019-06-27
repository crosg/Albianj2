package org.albianj.aop;

import org.albianj.service.FreeService;
import org.albianj.service.IService;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public abstract class FreeAspectService extends FreeService implements IAspectService {

    public void before(IAspectContext ctx, IService service, Method method, Object[] args) {
        return;
    }

    public void after(IAspectContext ctx, IService service, Method method, Object rc, Throwable t, Object[] args) {
        return;
    }

}
