package org.albianj.aop.impl;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.albianj.aop.*;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.loader.FinalAlbianReflectService;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.IAlbianService;
import org.albianj.service.IAlbianServiceAttribute;
import org.albianj.verify.Validate;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public class AlbianServiceProxyExecutor implements MethodInterceptor {
    IAlbianService _service = null;
    Map<String,IAlbianServiceAopAttribute> _aopAttributes = null;


    public Object newInstance(IAlbianService service,IAlbianServiceAttribute serviceAttr) {
        this._service = service;
        this._aopAttributes = serviceAttr.getAopAttributes();
        try {
            Enhancer enhancer = new Enhancer();  //增强类
            //不同于JDK的动态代理。它不能在创建代理时传obj对 象，obj对象必须被CGLIB包来创建
            // enhancer.setClassLoader(this.getClass().getClassLoader());
            enhancer.setClassLoader(AlbianClassLoader.getInstance());

            enhancer.setSuperclass(this._service.getClass()); //设置被代理类字节码（obj将被代理类设置成父类；作为产生的代理的父类传进来的）。CGLIB依据字节码生成被代理类的子类
            enhancer.setCallback(this);    //设置回调函数，即一个方法拦截
            Object proxy = enhancer.create(); //创建代理类
            return proxy;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String mName = method.getName();
        if (mName.equals("hashCode")
                || mName.equals("toString")
                || mName.equals("equals")
                || mName.equals("clone")
                || mName.equals("finalize")) {
            Object rc = methodProxy.invokeSuper(proxy, args);
            return rc;
        }

//        Method rm = this._service.getClass().getDeclaredMethod(mName, method.getParameterTypes());
        AlbianAopAttribute attr = method.getAnnotation(AlbianAopAttribute.class);
        if (null != attr && attr.avoid()) {
            Object rc = methodProxy.invokeSuper(proxy, args);
            return rc;
        }

        if (Validate.isNullOrEmpty(_aopAttributes)) {
            Object rc = methodProxy.invokeSuper(proxy, args);
            return rc;
        }

        IAlbianAopContext ctx = new AlbianAopContext();

        Object rc = null;
        for(IAlbianServiceAopAttribute asaa : _aopAttributes.values()) {
            IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
                    IAlbianAopService.class, asaa.getServiceName(), false);
            if (null == aas) continue;

            if (asaa.matches(mName)) {
                try {
                    aas.before(ctx, _service, method, args);
                } catch (Throwable e) {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                            IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,e,
                            "execute the before method in the aop service:%s for real service:%s is fail.",asaa.getServiceName(),this._service.getServiceName());
                }
            }
        }

        Throwable throwable = null;
        try {
            rc = methodProxy.invokeSuper(proxy, args);
        }catch (Throwable t){
            throwable = t;
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,t,
                    "execute the proxy service:%s method:%s is fail.",this._service.getServiceName(),mName);
        }

        for(IAlbianServiceAopAttribute asaa : _aopAttributes.values()) {
            IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
                    IAlbianAopService.class, asaa.getServiceName(), false);
            if (null == aas) continue;

            if (asaa.matches(mName)) {
                try {
                    aas.after(ctx, _service, method, rc, throwable,args);
                } catch (Throwable e) {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                            IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,e,
                            "execute the after method in the aop service:%s for real service:%s is fail.",asaa.getServiceName(),this._service.getServiceName());
                }
            }
        }

        if(null != throwable) throw throwable;
        return rc;

    }


////    public static AlbianServiceProxyExecutor Instance;
////
////    static {
////        Instance = new AlbianServiceProxyExecutor();
////    }
//    private IAlbianService realServ = null;
//    private IAlbianServiceAttribute servAttr;
//
//    public Object newProxyService(IAlbianService service,
//                                  IAlbianServiceAttribute serviceAttr) {
//        try {
//            Enhancer enhancer = new Enhancer();  //增强类
//            //不同于JDK的动态代理。它不能在创建代理时传obj对 象，obj对象必须被CGLIB包来创建
//            enhancer.setClassLoader(AlbianClassLoader.getInstance());
//
//            enhancer.setSuperclass(service.getClass()); //设置被代理类字节码（obj将被代理类设置成父类；作为产生的代理的父类传进来的）。CGLIB依据字节码生成被代理类的子类
//            enhancer.setCallback(this);    //设置回调函数，即一个方法拦截
//            Object proxy = enhancer.create(); //创建代理类
////            IAlbianService proxyServ = (IAlbianService) proxy;
////            proxyServ.setRealService(service);
////            proxyServ.setServiceId(service.getServiceId());
////            proxyServ.setServiceAttribute(serviceAttr);
//            this.realServ = realServ;
//            this.servAttr = serviceAttr;
//
//            return proxy;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        AlbianAopAttribute attr = method.getAnnotation(AlbianAopAttribute.class);
//        if (null != attr && attr.avoid()) {
//            Object rc = method.invoke(realServ, args);
//            return rc;
//        }
//
//        if (Validate.isNullOrEmpty(_aopAttributes)) {
//            Object rc = method.invoke(realServ, args);
//            return rc;
//        }
//
//        for (IAlbianServiceAopAttribute aaa : _aopAttributes) {
//            IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
//                    IAlbianAopService.class, aaa.getServiceName(), false);
//
//            if (null == aas) continue;
//
//            if (aaa.matches(method.getName())) {
//                aas.before(realServ, method, args);
//            }
//
//        }
//
//        Object rc = null;
//        Throwable throwable = null;
//        try {
//            rc = method.invoke(_service, args);
//        } catch (Throwable t) {
//            for (IAlbianServiceAopAttribute aaa : _aopAttributes) {
//                IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
//                        IAlbianAopService.class, aaa.getServiceName(), false);
//
//                if (null == aas) continue;
//
//                if (aaa.matchsException(method.getName(), t)) {
//                    aas.exception(realServ, method, t, args);
//                }
//
//            }
//        }
//
//        for (IAlbianServiceAopAttribute aaa : _aopAttributes) {
//            IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
//                    IAlbianAopService.class, aaa.getServiceName(), false);
//
//            if (null == aas) continue;
//
//            if (aaa.matches(method.getName())) {
//                aas.after(realServ, method, args);
//            }
//        }
//
//        return rc;
//
//    }
//
//    @Override
//    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
//        String mName = method.getName();
//        if (mName.equals("hashCode")
//                || mName.equals("toString")
//                || mName.equals("equals")
//                || mName.equals("clone")
//                || mName.equals("finalize")
//                || mName.equals("getServiceId")
//                || mName.equals("setServiceId")
//                || mName.equals("setServiceAttribute")
//                || mName.equals("getServiceAttribute")
//                || mName.equals("setRealService")
//                || mName.equals("getRealService")
//        ) {
//            Object rc = methodProxy.invoke(this.realServ, args);
//            return rc;
//        }
//
//        Class<?> cls = method.getDeclaringClass();
//        Method func = cls.getMethod(mName, method.getParameterTypes());
//        IAlbianService proxyServ = (IAlbianService) proxy;
//        IAlbianService realServ = proxyServ.getRealService();
//
//        boolean isIgnore = false;
//        if (func.isAnnotationPresent(AlbianAopAttribute.class)) {
//            AlbianAopAttribute aat = func.getAnnotation(AlbianAopAttribute.class);
//            if (aat.avoid()) {
//                isIgnore = true;
//            }
//        }
//
//        IAlbianServiceAttribute attr = proxyServ.getServiceAttribute();
//        Map<String, IAlbianServiceMethodAttribute> funcsAttr = attr.getMethodsAttribute();
//        String sigFuncName = FinalAlbianReflectService.Instance.getMethodSignature(func);
//        IAlbianServiceMethodAttribute sma = funcsAttr.get(sigFuncName);
//        if (null != sma && sma.isIgnore()) {
//            isIgnore = true;
//        }
//
//        Map<String, IAlbianServiceAopAttribute> saa = attr.getAopAttributes();
//
//        if (Validate.isNullOrEmpty(saa)) {
//            Object rc = AlbianMethodExecutor.Instance.call(proxy, method, args, methodProxy, isIgnore, sma);
////            Object rc = methodProxy.invokeSuper(proxy, args);
//            return rc;
//        }
//
//        IAlbianAopContext ctx = new AlbianAopContext();
//
//        Object rc = null;
//        for (IAlbianServiceAopAttribute asaa : saa.values()) {
//            IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
//                    IAlbianAopService.class, asaa.getServiceName(), false);
//            if (null == aas) continue;
//
//            if (asaa.matches(mName)) {
//                try {
//                    aas.before(ctx, realServ, method, args);
//                } catch (Throwable e) {
//                    AlbianServiceRouter.addLog("AOPService", AlbianServiceRouter.LoggerRunning, AlbianLoggerLevel.Error, e,
//                            "execute before method in the aop service:%s for real service:%s is fail.",
//                            asaa.getServiceName(), realServ.getServiceId());
//                }
//            }
//        }
//
//        Throwable throwable = null;
//        try {
//            rc = AlbianMethodExecutor.Instance.call(proxy, method, args, methodProxy, isIgnore, sma);
////            rc = methodProxy.invokeSuper(proxy, args);
//        } catch (Throwable t) {
//            throwable = t;
//            AlbianServiceRouter.addLog("AOPService", AlbianServiceRouter.LoggerRunning, AlbianLoggerLevel.Error, t,
//                    "exception in proxy service:%s method:%s.",
//                    realServ.getServiceId(), mName);
//        }
//
//        for (IAlbianServiceAopAttribute asaa : saa.values()) {
//            IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
//                    IAlbianAopService.class, asaa.getServiceName(), false);
//            if (null == aas) continue;
//
//            if (asaa.matches(mName)) {
//                try {
//                    aas.after(ctx, realServ, method, rc, throwable, args);
//                } catch (Throwable e) {
//                    AlbianServiceRouter.addLog("AOPService", AlbianServiceRouter.LoggerRunning, AlbianLoggerLevel.Error, e,
//                            "exception in the after method in the aop service:%s for real service:%s is fail.",
//                            asaa.getServiceName(), realServ.getServiceId());
//                }
//            }
//        }
//
//        if (null != throwable) throw throwable;
//        return rc;
//
//    }
}
