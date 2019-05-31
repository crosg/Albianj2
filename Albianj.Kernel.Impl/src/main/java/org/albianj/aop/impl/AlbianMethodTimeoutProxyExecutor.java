package org.albianj.aop.impl;

import net.sf.cglib.proxy.MethodProxy;
import org.albianj.aop.AlbianRetryException;
import org.albianj.except.AlbianExternalException;
import org.albianj.except.AlbianInternalException;
import org.albianj.service.AlbianServiceRouter;

import java.util.concurrent.*;

public class AlbianMethodTimeoutProxyExecutor {
    public static AlbianMethodTimeoutProxyExecutor Instance;
    private static ExecutorService executorService;
    static {
        Instance = new AlbianMethodTimeoutProxyExecutor();
        executorService = Executors.newScheduledThreadPool(100);
    }

    public Object execute(final Object proxy, final Object[] args, final MethodProxy methodProxy,long timeoutMs) {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
        FutureTask<Object> futureTask = new FutureTask<>(new Callable<Object>() {
            @Override
            public Object call() {
                try {
                    Object rc = methodProxy.invokeSuper(proxy, args);
                    return rc;
                }catch (AlbianRetryException e){
                    throw e;
                }catch (AlbianExternalException | AlbianInternalException e){

                }catch (Throwable e){

                }
                return null;
            }
        });

        executorService.execute(futureTask);
        Object rc = null;
        try {
            rc = futureTask.get(timeoutMs, TimeUnit.MILLISECONDS);
        }catch (TimeoutException e) {
            throw new AlbianRetryException(AlbianServiceRouter.ExceptForError,e,"timeout");
        } catch (InterruptedException | ExecutionException e) {
            futureTask.cancel(true);
        }
        return rc;
    }


}
