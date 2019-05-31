package org.albianj.aop.impl;

import net.sf.cglib.proxy.MethodProxy;
import org.albianj.aop.*;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.statistics.IAlbianStatisticsService;

import java.lang.reflect.Method;

public class AlbianMethodExecutor {
    public static AlbianMethodExecutor Instance;
    static {
        Instance = new AlbianMethodExecutor();
    }

    public Object call(Object proxy, Method method, Object[] args, MethodProxy methodProxy,
                           boolean isIgnoreProxy, IAlbianServiceMethodAttribute funcAttr)throws Throwable{
        if(isIgnoreProxy){
            Object rc = methodProxy.invokeSuper(proxy, args);
            return rc;
        }

        boolean isRetry = false;
        int retryTimes = 1;
        IAlbianServiceMethodRetryAttribute mra =  funcAttr.getRetryAttribute();
        if(null == mra){
            isRetry = false;
        } else {
            retryTimes += mra.getRetryTimes();
        }
        boolean isStatistics = false;
        IAlbianServiceMethodTimeoutAttribute mtoa = funcAttr.getTimeoutAttribute();
        IAlbianServiceMethodStatisticsAttribute msa = funcAttr.getStatisticsAttribute();
        if(null != msa) {
            isStatistics = true;
        }
        long begin =  0;
        if(isStatistics) {
            System.currentTimeMillis();
        }
        Object rc = null;
        boolean isNeedRetry = false;
        do {
            try {
                isNeedRetry = false;
                if (null == mtoa) {
                    rc = methodProxy.invokeSuper(proxy, args);
                } else {
                    rc = AlbianMethodTimeoutProxyExecutor.Instance.execute(proxy, args, methodProxy, mtoa.getTimetampMs());
                }
            }catch (AlbianRetryException e){
                isNeedRetry = true;
            }
        }while(isRetry && isNeedRetry && (0 < -- retryTimes));

        long execTimeMs = 0;
        if(isStatistics) {
            long end = System.currentTimeMillis();
            execTimeMs =  end - begin;
            IAlbianStatisticsService ass = AlbianServiceRouter.getSingletonService(IAlbianStatisticsService.class,IAlbianStatisticsService.Name);
            ass.add(msa.getLogTagName(),end,execTimeMs);
        }
        return rc;
    }
}
