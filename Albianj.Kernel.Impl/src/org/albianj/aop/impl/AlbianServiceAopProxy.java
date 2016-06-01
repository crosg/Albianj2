package org.albianj.aop.impl;

import org.albianj.aop.AlbianAopAttribute;
import org.albianj.aop.IAlbianAopService;
import org.albianj.aop.IAlbianServiceAopAttribute;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.IAlbianService;
import org.albianj.verify.Validate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by xuhaifeng on 16/5/30.
 */
public class AlbianServiceAopProxy implements InvocationHandler {
    IAlbianService _service = null;
    List<IAlbianServiceAopAttribute> _aopAttributes = null;

    public AlbianServiceAopProxy(IAlbianService service, List<IAlbianServiceAopAttribute> aopAttributes) {
        _service = service;
        _aopAttributes = aopAttributes;
    }

    /**
     * Processes a method invocation on a proxy instance and returns
     * the result.  This method will be invoked on an invocation handler
     * when a method is invoked on a proxy instance that it is
     * associated with.
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@code Method} instance corresponding to
     *               the interface method invoked on the proxy instance.  The declaring
     *               class of the {@code Method} object will be the interface that
     *               the method was declared in, which may be a superinterface of the
     *               proxy interface that the proxy class inherits the method through.
     * @param args   an array of objects containing the values of the
     *               arguments passed in the method invocation on the proxy instance,
     *               or {@code null} if interface method takes no arguments.
     *               Arguments of primitive types are wrapped in instances of the
     *               appropriate primitive wrapper class, such as
     *               {@code java.lang.Integer} or {@code java.lang.Boolean}.
     * @return the value to return from the method invocation on the
     * proxy instance.  If the declared return type of the interface
     * method is a primitive type, then the value returned by
     * this method must be an instance of the corresponding primitive
     * wrapper class; otherwise, it must be a type assignable to the
     * declared return type.  If the value returned by this method is
     * {@code null} and the interface method's return type is
     * primitive, then a {@code NullPointerException} will be
     * thrown by the method invocation on the proxy instance.  If the
     * value returned by this method is otherwise not compatible with
     * the interface method's declared return type as described above,
     * a {@code ClassCastException} will be thrown by the method
     * invocation on the proxy instance.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        AlbianAopAttribute attr = method.getAnnotation(AlbianAopAttribute.class);
        if (null != attr && attr.avoid()) {
            Object rc = method.invoke(_service, args);
            return rc;
        }

        if (Validate.isNullOrEmpty(_aopAttributes)) {
            Object rc = method.invoke(_service, args);
            return rc;
        }

        for (IAlbianServiceAopAttribute aaa : _aopAttributes) {
            IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
                    IAlbianAopService.class, aaa.getServiceName(), false);

            if (null == aas) continue;

            if (aaa.matches(method.getName())) {
                aas.before(_service, method, args);
            }

        }

        Object rc = null;
        Throwable throwable = null;
        try {
            rc = method.invoke(_service, args);
        } catch (Throwable t) {
            for (IAlbianServiceAopAttribute aaa : _aopAttributes) {
                IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
                        IAlbianAopService.class, aaa.getServiceName(), false);

                if (null == aas) continue;

                if (aaa.matchsException(method.getName(), t)) {
                    aas.exception(_service, method, t, args);
                }

            }
        }

        for (IAlbianServiceAopAttribute aaa : _aopAttributes) {
            IAlbianAopService aas = AlbianServiceRouter.getSingletonService(
                    IAlbianAopService.class, aaa.getServiceName(), false);

            if (null == aas) continue;

            if (aaa.matches(method.getName())) {
                aas.after(_service, method, args);
            }
        }

        return rc;

    }
}
