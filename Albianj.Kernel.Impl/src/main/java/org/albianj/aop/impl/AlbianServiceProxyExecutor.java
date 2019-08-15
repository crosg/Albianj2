package org.albianj.aop.impl;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.albianj.aop.*;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.IAlbianService;
import org.albianj.service.IAlbianServiceAttribute;
import org.albianj.verify.Validate;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/5/30.
 * ServiceProxy 仅仅只是拿来作为执行method的intercepter，而不会在proxy的service上执行method
 * 执行method的时候，还是在原始的service中执行。
 * 之所以这样的做的原因是：
 * 1. service初始化有状态的时候（比如解析xml，创建状态），状态只能被执行一次，所以无法在proxyservice中再次被load
 * 2. 当在proxymethod中执行的时候，会引起递归调用，引起stackoverflow
 * 3. 因为service可能会有状态，所以在realservice中执行会直接更改service的状态，不会引起状态的2面性
 * 4. 不提倡service有状态，但是某些service需要加载xml文件等无法避免
 */
public class AlbianServiceProxyExecutor implements MethodInterceptor {
    IAlbianService realServ = null;
    IAlbianServiceAttribute serviceAttr;

    public Object newInstance(IAlbianService service,IAlbianServiceAttribute serviceAttr) {
        this.realServ = service;
        this.serviceAttr = serviceAttr;
        try {
            Enhancer enhancer = new Enhancer();  //增强类
            //不同于JDK的动态代理。它不能在创建代理时传obj对 象，obj对象必须被CGLIB包来创建
            // enhancer.setClassLoader(this.getClass().getClassLoader());
            enhancer.setClassLoader(AlbianClassLoader.getInstance());
            enhancer.setSuperclass(this.realServ.getClass()); //设置被代理类字节码（obj将被代理类设置成父类；作为产生的代理的父类传进来的）。CGLIB依据字节码生成被代理类的子类
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
            Object rc = method.invoke(realServ, args);
            return rc;
        }

        AlbianAopAttribute attr = method.getAnnotation(AlbianAopAttribute.class);
        if (null != attr && attr.avoid()) {
            Object rc = method.invoke(realServ, args);
            return rc;
        }

        if (Validate.isNullOrEmpty(serviceAttr.getAopAttributes())) {
            Object rc = method.invoke(realServ, args);
            return rc;
        }

        IAlbianAopContext ctx = new AlbianAopContext();

        Object rc = null;
        for (IAlbianServiceAopAttribute asaa : serviceAttr.getAopAttributes().values()) {
            IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
                    IAlbianAopService.class, asaa.getServiceName(), false);
            if (null == aas) continue;

            if (asaa.matches(mName)) {
                try {
                    aas.before(ctx, realServ, method, args);
                } catch (Throwable e) {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                            IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, e,
                            "execute the before method in the aop service:%s for real service:%s is fail.", asaa.getServiceName(), this.realServ.getServiceName());
                }
            }
        }

        Throwable throwable = null;
        try {
            rc = method.invoke(realServ, args);
        } catch (Throwable t) {
            throwable = t;
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, t,
                    "execute the proxy service:%s method:%s is fail.", this.realServ.getServiceName(), mName);
        }

        for (IAlbianServiceAopAttribute asaa : serviceAttr.getAopAttributes().values()) {
            IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
                    IAlbianAopService.class, asaa.getServiceName(), false);
            if (null == aas) continue;

            if (asaa.matches(mName)) {
                try {
                    aas.after(ctx, realServ, method, rc, throwable, args);
                } catch (Throwable e) {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                            IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, e,
                            "execute the after method in the aop service:%s for real service:%s is fail.", asaa.getServiceName(), this.realServ.getServiceName());
                }
            }
        }

        if (null != throwable) throw throwable;
        return rc;
    }
}
