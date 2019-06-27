package org.albianj.kernel.impl;

import ognl.Ognl;
import org.albianj.aop.impl.AlbianServiceProxyExecutor;
import org.albianj.boot.BundleContext;
import org.albianj.logger.ILoggerService2;
import org.albianj.reflection.AlbianTypeConvert;
import org.albianj.service.*;
import org.albianj.verify.Validate;

import java.util.Map;

public class AlbianServiceLoader {
    private static String sessionId = "AlbianServiceLoader";

    /**
     * 创建service，并且将其依附与BundleContext管理
     * @param bundleContext
     * @param serviceAttr
     * @param servAttrs
     * @return
     */
    public static IService makeupServiceAndAttachBundleContext(BundleContext bundleContext, IServiceAttribute serviceAttr, Map<String, IServiceAttribute> servAttrs) {
        String sImplClzz = serviceAttr.getType();
        String id = serviceAttr.getId();
        IService rtnService = null;

        String sInterface = serviceAttr.getInterface();
        try {
//            Class<?> cla = AlbianClassLoader.getInstance().loadClass(sImplClzz);
            Class<?> cla = bundleContext.getClassLoader().loadClass(sImplClzz);
            if (null == cla) {
                AlbianServiceRouter.throwException(sessionId,
                        ILoggerService2.AlbianRunningLoggerName,
                        String.format("load impl class :%s is null for service:%s.", sImplClzz, id));
            }

            if (!IService.class.isAssignableFrom(cla)) {
                AlbianServiceRouter.throwException(sessionId,
                        ILoggerService2.AlbianRunningLoggerName,
                        String.format("Service -> %s class -> %s is not extends IService.",
                                id, sImplClzz));
            }

            Class<?> itf = null;
            if (!Validate.isNullOrEmptyOrAllSpace(sInterface)) {
//                itf = AlbianClassLoader.getInstance().loadClass(sInterface);
                itf = bundleContext.getClassLoader().loadClass(sInterface);
                if (!itf.isAssignableFrom(cla)) {
                    AlbianServiceRouter.throwException(sessionId,
                            ILoggerService2.AlbianRunningLoggerName,
                            String.format("Service -> %s class -> %s is not impl from itf -> %s.",
                                    id, sImplClzz, sInterface));
                }

                if (!IService.class.isAssignableFrom(itf)) {
                    AlbianServiceRouter.throwException(sessionId,
                            ILoggerService2.AlbianRunningLoggerName,
                            String.format("Service -> %s itf -> %s is not extends IService.",
                                    id, sInterface));
                }
            }

            IService service = (IService) cla.newInstance();
            service.setBundleContext(bundleContext);
            setServiceFields(service, serviceAttr, ServiceFieldSetterLifetime.AfterNew, servAttrs);
            service.beforeLoad();
            setServiceFields(service, serviceAttr, ServiceFieldSetterLifetime.BeforeLoading, servAttrs);
            service.loading();
            setServiceFields(service, serviceAttr, ServiceFieldSetterLifetime.AfterLoading, servAttrs);
            service.afterLoading();
            service.setServiceId(id);
            service.setServiceAttribute(serviceAttr);
            if (!serviceAttr.isUseProxy()) {
                rtnService = service;
            } else {
//                AlbianServiceProxyExecutor proxy = new AlbianServiceProxyExecutor();
                IService serviceProxy = (IService) AlbianServiceProxyExecutor.Instance.newProxyService(bundleContext.getClassLoader(),service, serviceAttr.getAopAttributes());
                serviceProxy.setBundleContext(bundleContext);
                serviceProxy.setRealService(service);
                serviceProxy.beforeLoad();
                serviceProxy.loading();
                serviceProxy.afterLoading();
                serviceProxy.setServiceId(id);
                service.setServiceAttribute(serviceAttr);
                rtnService = serviceProxy;
            }
            bundleContext.addBundleService(id,rtnService);
        } catch (Exception e) {
            AlbianServiceRouter.throwException(sessionId,
                    ILoggerService2.AlbianRunningLoggerName,
                    String.format("load and loadConf service:%s with class:%s is fail.", id, sImplClzz),
                    e);
        }
        return rtnService;
    }

    public static void setServiceFields(IService serv, IServiceAttribute servAttr, ServiceFieldSetterLifetime lifetime, Map<String, IServiceAttribute> servAttrs) {
        if(Validate.isNullOrEmpty(servAttr.getServiceFields())) {
            return;
        }
        for (IServiceFieldAttribute fAttr : servAttr.getServiceFields().values()) {
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
                            ILoggerService2.AlbianRunningLoggerName,
                            String.format("set field %s.%s = %s is fail.",
                                    servAttr.getId(), fAttr.getName(), fAttr.getValue()), e);
                }
                continue;
            }

            String value = fAttr.getValue();
            Object realObject = null;
            int indexof = value.indexOf(".");
            if (-1 == indexof) { // real ref service
                realObject = AlbianServiceRouter.getSingletonService(IService.class, value, false);
                if (!fAttr.getAllowNull() && null == realObject) {
                    AlbianServiceRouter.throwException(sessionId,
                            ILoggerService2.AlbianRunningLoggerName,
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
                                ILoggerService2.AlbianRunningLoggerName,
                                String.format("set field %s.%s = %s is fail.the field type is ref.",
                                        servAttr.getId(), fAttr.getName(), fAttr.getValue()), e);
                    }
                }
                continue;
            }

            String refServiceId = value.substring(0, indexof);
            String exp = value.substring(indexof + 1);
            IService refService = AlbianServiceRouter.getSingletonService(
                    IService.class, refServiceId, false);

            if (!fAttr.getAllowNull() && null == refService) {
                AlbianServiceRouter.throwException(sessionId,
                        ILoggerService2.AlbianRunningLoggerName,
                        String.format("%s.%s = %s.%s is fail. not found ref service -> %s exp -> %s. ",
                                servAttr.getId(), fAttr.getName(), refServiceId, exp, refServiceId, exp));
                continue;
            }

            if (null != refService) {
                IServiceAttribute sAttr = servAttrs.get(refServiceId);
                Object refRealObj = sAttr.getServiceClass().cast(refService);//must get service full type sign
                try {
                    realObject = Ognl.getValue(exp, refRealObj);// get read value from full-sgin ref service
                } catch (Exception e) {
                    AlbianServiceRouter.throwException(sessionId,
                            ILoggerService2.AlbianRunningLoggerName,
                            String.format("%s.%s = %s.%s is fail. not found exp -> %s in ref service -> %s. ",
                                    servAttr.getId(), fAttr.getName(), refServiceId, exp, exp, refServiceId), e);
                    continue;
                }
                if (null == realObject && !fAttr.getAllowNull()) {
                    AlbianServiceRouter.throwException(sessionId,
                            ILoggerService2.AlbianRunningLoggerName,
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
                                ILoggerService2.AlbianRunningLoggerName,
                                String.format("%s.%s = %s.%s is fail. ",
                                        servAttr.getId(), fAttr.getName(), refServiceId, exp));
                        continue;
                    }
                }
            }
        }
    }
}
