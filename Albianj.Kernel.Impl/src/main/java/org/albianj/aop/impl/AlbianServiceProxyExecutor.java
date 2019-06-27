package org.albianj.aop.impl;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.albianj.aop.*;
import org.albianj.boot.FinalAlbianReflectService;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.IService;
import org.albianj.service.IServiceAttribute;
import org.albianj.verify.Validate;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public class AlbianServiceProxyExecutor implements MethodInterceptor {

    public static AlbianServiceProxyExecutor Instance;

    static {
        Instance = new AlbianServiceProxyExecutor();
    }

    public Object newProxyService(ClassLoader loader, IService service, Map<String, IServiceAspectAttribute> aopAttributes) {
        try {
            Enhancer enhancer = new Enhancer();  //增强类
            //不同于JDK的动态代理。它不能在创建代理时传obj对 象，obj对象必须被CGLIB包来创建
            enhancer.setClassLoader(loader);

            enhancer.setSuperclass(service.getClass()); //设置被代理类字节码（obj将被代理类设置成父类；作为产生的代理的父类传进来的）。CGLIB依据字节码生成被代理类的子类
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
        Class<?> cls = method.getDeclaringClass();
        Method func = cls.getMethod(mName, method.getParameterTypes());
        IService proxyServ = (IService) proxy;
        IService realServ = proxyServ.getRealService();

        boolean isIgnore = false;
        if (func.isAnnotationPresent(AlbianAopAttribute.class)) {
            AlbianAopAttribute aat = func.getAnnotation(AlbianAopAttribute.class);
            if (aat.avoid()) {
                isIgnore = true;
            }
        }

        IServiceAttribute attr = proxyServ.getServiceAttribute();
        Map<String, IMethodAttribute> funcsAttr = attr.getMethodsAttribute();
        String sigFuncName = FinalAlbianReflectService.Instance.getMethodSignature(func);
        IMethodAttribute sma = funcsAttr.get(sigFuncName);
        if (null != sma && sma.isIgnore()) {
            isIgnore = true;
        }

        Map<String, IServiceAspectAttribute> saa = attr.getAopAttributes();

        if (Validate.isNullOrEmpty(saa)) {
            Object rc = AlbianMethodExecutor.Instance.call(proxy, method, args, methodProxy, isIgnore, sma);
//            Object rc = methodProxy.invokeSuper(proxy, args);
            return rc;
        }

        IAspectContext ctx = new AspectContext();

        Object rc = null;
        for (IServiceAspectAttribute asaa : saa.values()) {
            IAspectService aas = AlbianServiceRouter.getSingletonService(
                    IAspectService.class, asaa.getServiceName(), false);
            if (null == aas) continue;

            if (asaa.matches(mName)) {
                try {
                    aas.before(ctx, realServ, method, args);
                } catch (Throwable e) {
                    AlbianServiceRouter.addLog("AOPService", AlbianServiceRouter.LoggerRunning, AlbianLoggerLevel.Error, e,
                            "execute before method in the aop service:%s for real service:%s is fail.",
                            asaa.getServiceName(), realServ.getServiceId());
                }
            }
        }

        Throwable throwable = null;
        try {
            rc = AlbianMethodExecutor.Instance.call(proxy, method, args, methodProxy, isIgnore, sma);
//            rc = methodProxy.invokeSuper(proxy, args);
        } catch (Throwable t) {
            throwable = t;
            AlbianServiceRouter.addLog("AOPService", AlbianServiceRouter.LoggerRunning, AlbianLoggerLevel.Error, t,
                    "exception in proxy service:%s method:%s.",
                    realServ.getServiceId(), mName);
        }

        for (IServiceAspectAttribute asaa : saa.values()) {
            IAspectService aas = AlbianServiceRouter.getSingletonService(
                    IAspectService.class, asaa.getServiceName(), false);
            if (null == aas) continue;

            if (asaa.matches(mName)) {
                try {
                    aas.after(ctx, realServ, method, rc, throwable, args);
                } catch (Throwable e) {
                    AlbianServiceRouter.addLog("AOPService", AlbianServiceRouter.LoggerRunning, AlbianLoggerLevel.Error, e,
                            "exception in the after method in the aop service:%s for real service:%s is fail.",
                            asaa.getServiceName(), realServ.getServiceId());
                }
            }
        }

        if (null != throwable) throw throwable;
        return rc;

    }
}
