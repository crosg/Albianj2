package org.albianj.kernel.impl;

import org.albianj.aop.impl.AlbianServiceAopProxy;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.IAlbianService;
import org.albianj.service.IAlbianServiceAttribute;
import org.albianj.verify.Validate;

public class AlbianServiceLoader {
    public static IAlbianService makeupService(IAlbianServiceAttribute serviceAttr) {
        String sImplClzz = serviceAttr.getType();
        String id = serviceAttr.getId();
        IAlbianService rtnService = null;

        String sInterface = serviceAttr.getInterface();
        try {

            Class<?> cla = AlbianClassLoader.getInstance().loadClass(sImplClzz);
            if (null == cla) {
                AlbianServiceRouter.throwException("Albian Transmitter",
                        IAlbianLoggerService2.AlbianRunningLoggerName,
                        String.format("load impl class :%s is null for service:%s.", sImplClzz, id));
            }

            if (!IAlbianService.class.isAssignableFrom(cla)) {
                AlbianServiceRouter.throwException("Albian Transmitter",
                        IAlbianLoggerService2.AlbianRunningLoggerName,
                        String.format("Service -> %s class -> %s is not extends IAlbianService.",
                                id, sImplClzz));
            }

            Class<?> itf = null;
            if (!Validate.isNullOrEmptyOrAllSpace(sInterface)) {
                itf = AlbianClassLoader.getInstance().loadClass(sInterface);
                if (!itf.isAssignableFrom(cla)) {
                    AlbianServiceRouter.throwException("Albian Transmitter",
                            IAlbianLoggerService2.AlbianRunningLoggerName,
                            String.format("Service -> %s class -> %s is not impl from itf -> %s.",
                                    id, sImplClzz, sInterface));
                }

                if (!IAlbianService.class.isAssignableFrom(itf)) {
                    AlbianServiceRouter.throwException("Albian Transmitter",
                            IAlbianLoggerService2.AlbianRunningLoggerName,
                            String.format("Service -> %s itf -> %s is not extends IAlbianService.",
                                    id, sInterface));
                }
            }

            IAlbianService service = (IAlbianService) cla.newInstance();
            service.beforeLoad();
            service.loading();
            service.afterLoading();
            if (Validate.isNullOrEmpty(serviceAttr.getAopAttributes())) {
                rtnService = service;
            } else {
                AlbianServiceAopProxy proxy = new AlbianServiceAopProxy();
                IAlbianService serviceProxy = (IAlbianService) proxy.newInstance(service, serviceAttr.getAopAttributes());
                serviceProxy.setRealService(service);
                serviceProxy.beforeLoad();
                serviceProxy.loading();
                serviceProxy.afterLoading();
                rtnService = serviceProxy;
            }
        } catch (Exception e) {
            AlbianServiceRouter.throwException("Albian Transmitter",
                    IAlbianLoggerService2.AlbianRunningLoggerName,
                    String.format("load and init service:%s with class:%s is fail.", id, sImplClzz),
                    e);
        }
        return rtnService;
    }
}
