package org.albianj.kernel.impl;

import ognl.Ognl;
import org.albianj.aop.impl.AlbianServiceProxyExecutor;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.reflection.AlbianTypeConvert;
import org.albianj.service.*;
import org.albianj.verify.Validate;

import java.util.Map;

public class AlbianServiceLoader {
    private static String sessionId = "AlbianServiceLoader";

    public static IAlbianService makeupService(IAlbianServiceAttribute serviceAttr, Map<String, IAlbianServiceAttribute> servAttrs) {
        String sImplClzz = serviceAttr.getType();
        String id = serviceAttr.getId();
        IAlbianService rtnService = null;

        String sInterface = serviceAttr.getInterface();
        try {
            Class<?> cla = AlbianClassLoader.getInstance().loadClass(sImplClzz);
            if (null == cla) {
                AlbianServiceRouter.throwException(sessionId,
                        IAlbianLoggerService2.AlbianRunningLoggerName,
                        String.format("load impl class :%s is null for service:%s.", sImplClzz, id));
            }

            if (!IAlbianService.class.isAssignableFrom(cla)) {
                AlbianServiceRouter.throwException(sessionId,
                        IAlbianLoggerService2.AlbianRunningLoggerName,
                        String.format("Service -> %s class -> %s of loading by %s is not extends IAlbianService of loading by %s.Current thread loader -> %s.",
                                id, sImplClzz,cla.getClassLoader().toString(),
                                IAlbianService.class.getClassLoader().toString(),
                                Thread.currentThread().getContextClassLoader().toString()));

            }

            AlbianServiceRouter.addLogV2(sessionId,
                    IAlbianLoggerService2.AlbianRunningLoggerName,
                    AlbianServiceRouter.Mark,null,"ClassMatedata",
                    String.format("Service -> %s class -> %s of loading by %s extends IAlbianService of loading by %s.Current thread loader -> %s.",
                            id, sImplClzz,cla.getClassLoader().toString(),
                            IAlbianService.class.getClassLoader().toString(),
                            Thread.currentThread().getContextClassLoader().toString()));

            Class<?> itf = null;
            if (!Validate.isNullOrEmptyOrAllSpace(sInterface)) {
                itf = AlbianClassLoader.getInstance().loadClass(sInterface);
                if (!itf.isAssignableFrom(cla)) {
                    AlbianServiceRouter.throwException(sessionId,
                            IAlbianLoggerService2.AlbianRunningLoggerName,
                            String.format("Service -> %s class -> %s is not impl from itf -> %s.",
                                    id, sImplClzz, sInterface));
                }

                if (!IAlbianService.class.isAssignableFrom(itf)) {
                    AlbianServiceRouter.throwException(sessionId,
                            IAlbianLoggerService2.AlbianRunningLoggerName,
                            String.format("Service -> %s itf -> %s is not extends IAlbianService.",
                                    id, sInterface));
                }
            }


            IAlbianService service = (IAlbianService) cla.newInstance();
            service.setRealService(service);
            setServiceFields(service, serviceAttr, AlbianServiceFieldSetterLifetime.AfterNew, servAttrs);
            service.beforeLoad();
            setServiceFields(service, serviceAttr, AlbianServiceFieldSetterLifetime.BeforeLoading, servAttrs);
            service.loading();
            setServiceFields(service, serviceAttr, AlbianServiceFieldSetterLifetime.AfterLoading, servAttrs);
            service.afterLoading();
            service.setServiceId(id);
            service.setServiceAttribute(serviceAttr);
            rtnService = service;
            if (serviceAttr.isUseProxy()) {
                AlbianServiceProxyExecutor proxy = new AlbianServiceProxyExecutor();
                IAlbianService serviceProxy = (IAlbianService)proxy.newInstance(service, serviceAttr);
                rtnService = serviceProxy;

//            } else {

//                IAlbianService serviceProxy = (IAlbianService) AlbianServiceProxyExecutor.Instance.newProxyService(service, serviceAttr);

//                serviceProxy.setRealService(service);
//                setServiceFields(serviceProxy, serviceAttr, AlbianServiceFieldSetterLifetime.AfterNew, servAttrs);
//                serviceProxy.beforeLoad();
//                setServiceFields(serviceProxy, serviceAttr, AlbianServiceFieldSetterLifetime.BeforeLoading, servAttrs);
//                serviceProxy.loading();
//                setServiceFields(serviceProxy, serviceAttr, AlbianServiceFieldSetterLifetime.AfterLoading, servAttrs);
//                serviceProxy.afterLoading();
//                serviceProxy.setServiceId(id);
//                serviceProxy.setServiceAttribute(serviceAttr);
            }
        } catch (Exception e) {
            AlbianServiceRouter.throwException(sessionId,
                    IAlbianLoggerService2.AlbianRunningLoggerName,
                    String.format("load and init service:%s with class:%s is fail.", id, sImplClzz),
                    e);
        }
        return rtnService;
    }

    public static void setServiceFields(IAlbianService serv, IAlbianServiceAttribute servAttr, AlbianServiceFieldSetterLifetime lifetime, Map<String, IAlbianServiceAttribute> servAttrs) {
        if(Validate.isNullOrEmpty(servAttr.getServiceFields())) {
            return;
        }
        for (IAlbianServiceFieldAttribute fAttr : servAttr.getServiceFields().values()) {
            if (lifetime != fAttr.getSetterLifetime() || fAttr.isReady()) { //when in the lifecycle
                continue;
            }
            if (!fAttr.getType().toLowerCase().equals("ref")) { // not set ref
                try {
                    Object o = AlbianTypeConvert.toRealObject(fAttr.getType(), fAttr.getValue());
                    fAttr.getField().set(serv, o);
                    fAttr.setReady(true);
                } catch (Exception e) {
                    AlbianServiceRouter.throwException(sessionId,
                            IAlbianLoggerService2.AlbianRunningLoggerName,
                            String.format("set field %s.%s = %s is fail.",
                                    servAttr.getId(), fAttr.getName(), fAttr.getValue()), e);
                }
                continue;
            }

            String value = fAttr.getValue();
            Object realObject = null;
            int indexof = value.indexOf(".");
            if (-1 == indexof) { // real ref service
                realObject = AlbianServiceRouter.getSingletonService(IAlbianService.class, value, false);
                if (!fAttr.getAllowNull() && null == realObject) {
                    AlbianServiceRouter.throwException(sessionId,
                            IAlbianLoggerService2.AlbianRunningLoggerName,
                            String.format("not found ref service ->%s to set field -> %s in service -> %s. ",
                                    value, fAttr.getName(), servAttr.getId()));
                    continue;
                }

                if (null != realObject) {
                    try {
                        fAttr.getField().set(serv, realObject);
                        fAttr.setReady(true);
                    } catch (Exception e) {
                        AlbianServiceRouter.throwException(sessionId,
                                IAlbianLoggerService2.AlbianRunningLoggerName,
                                String.format("set field %s.%s = %s is fail.the field type is ref.",
                                        servAttr.getId(), fAttr.getName(), fAttr.getValue()), e);
                    }
                }
                continue;
            }

            String refServiceId = value.substring(0, indexof);
            String exp = value.substring(indexof + 1);
            IAlbianService refService = AlbianServiceRouter.getSingletonService(
                    IAlbianService.class, refServiceId, false);

            if (!fAttr.getAllowNull() && null == refService) {
                AlbianServiceRouter.throwException(sessionId,
                        IAlbianLoggerService2.AlbianRunningLoggerName,
                        String.format("%s.%s = %s.%s is fail. not found ref service -> %s exp -> %s. ",
                                servAttr.getId(), fAttr.getName(), refServiceId, exp, refServiceId, exp));
                continue;
            }

            if (null != refService) {
                IAlbianServiceAttribute sAttr = servAttrs.get(refServiceId);
                Object refRealObj = sAttr.getServiceClass().cast(refService);//must get service full type sign
                try {
                    realObject = Ognl.getValue(exp, refRealObj);// get read value from full-sgin ref service
                } catch (Exception e) {
                    AlbianServiceRouter.throwException(sessionId,
                            IAlbianLoggerService2.AlbianRunningLoggerName,
                            String.format("%s.%s = %s.%s is fail. not found exp -> %s in ref service -> %s. ",
                                    servAttr.getId(), fAttr.getName(), refServiceId, exp, exp, refServiceId), e);
                    continue;
                }
                if (null == realObject && !fAttr.getAllowNull()) {
                    AlbianServiceRouter.throwException(sessionId,
                            IAlbianLoggerService2.AlbianRunningLoggerName,
                            String.format("%s.%s = %s.%s is fail. not found ref service -> %s exp -> %s. ",
                                    servAttr.getId(), fAttr.getName(), refServiceId, exp, refServiceId, exp));
                    continue;
                }
                if (null != realObject) {
                    try {
                        fAttr.getField().set(serv, realObject);
                        fAttr.setReady(true);
                    } catch (Exception e) {
                        AlbianServiceRouter.throwException(sessionId,
                                IAlbianLoggerService2.AlbianRunningLoggerName,
                                String.format("%s.%s = %s.%s is fail. ",
                                        servAttr.getId(), fAttr.getName(), refServiceId, exp));
                        continue;
                    }
                }
            }
        }
    }
}
