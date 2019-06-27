package org.albianj.service.impl;

import org.albianj.aop.*;
import org.albianj.aop.impl.*;
import org.albianj.aop.tags.*;
import org.albianj.argument.RefArg;
import org.albianj.boot.*;
import org.albianj.boot.loader.AlbianClassLoader;
import org.albianj.service.*;
import org.albianj.verify.Validate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class AlbianServiceRantParser {

    public static HashMap<String, Object> scanPackage(String pkgName) throws IOException, ClassNotFoundException {
        return AlbianClassScanner.filter(AlbianClassLoader.getInstance(),
                pkgName,

                new IAlbianClassFilter() {
                    @Override
                    public boolean verify(Class<?> cls) {
                        //must flag with anno and extends IService
                        // extends interface is compatibling the last version
                        return cls.isAnnotationPresent(ServiceTag.class)
                                && IService.class.isAssignableFrom(cls)
                                && !cls.isInterface()
                                && !Modifier.isAbstract(cls.getModifiers());
                    }
                },

                new IAlbianClassExcavator() {
                    @Override
                    public Object finder(Class<?> clzz) {
                        return scanAlbianService(clzz);
                    }
                });
    }

    public static IServiceAttribute scanAlbianService(Class<?> implClzz) {
        IServiceAttribute asa = new ServiceAttribute();
        ServiceTag rant = implClzz.getAnnotation(ServiceTag.class);
        asa.setId(rant.Id());
        if (Validate.isNullOrEmptyOrAllSpace(rant.sInterface()) && null == rant.Interface()) {
            asa.setInterface(IService.class.getName());
        } else {
            asa.setInterface(null != rant.Interface() ? rant.Interface().getName() : rant.sInterface());
        }
        asa.setEnable(rant.Enable());
        asa.setType(implClzz.getName());
        asa.setServiceClass(implClzz.asSubclass(IService.class));

        if (implClzz.isAnnotationPresent(ServiceProxyTags.class)) {
            ServiceProxyTags prants = implClzz.getAnnotation(ServiceProxyTags.class);
            Map<String, IServiceAspectAttribute> asaas = new HashMap<>();
            for (ServiceProxyTag prant : prants.Rants()) {
                IServiceAspectAttribute aspa = new ServiceAspectAttribute();
                aspa.setServiceName(prant.ServiceName());
                aspa.setProxyName(prant.ProxyName());

                if (!Validate.isNullOrEmptyOrAllSpace(prant.BeginWith())) {
                    aspa.setBeginWith(prant.BeginWith());
                }
                if (!Validate.isNullOrEmptyOrAllSpace(prant.NotBeginWith())) {
                    aspa.setNotBeginWith(prant.NotBeginWith());
                }

                if (!Validate.isNullOrEmptyOrAllSpace(prant.EndWith())) {
                    aspa.setEndWith(prant.EndWith());
                }
                if (!Validate.isNullOrEmptyOrAllSpace(prant.NotEndWith())) {
                    aspa.setNotEndWith(prant.NotEndWith());
                }

                if (!Validate.isNullOrEmptyOrAllSpace(prant.Contain())) {
                    aspa.setContain(prant.Contain());
                }
                if (!Validate.isNullOrEmptyOrAllSpace(prant.NotContain())) {
                    aspa.setNotContain(prant.NotContain());
                }
                if (!Validate.isNullOrEmptyOrAllSpace(prant.FullName())) {
                    aspa.setFullName(prant.FullName());
                }
                aspa.setIsAll(prant.IsAll());
                asaas.put(aspa.getProxyName(), aspa);
            }

            asa.setAopAttributes(asaas);
            asa.setUseProxy(true);
        }

        Map<String, IServiceFieldAttribute> fields = scanFields(implClzz);
        if (!Validate.isNullOrEmpty(fields)) {
            asa.setServiceFields(fields);
        }

        RefArg<Boolean> useProxy = new RefArg<>();
        Map<String, IMethodAttribute> methods = scanMethods(implClzz,useProxy);
        if (!Validate.isNullOrEmpty(methods)) {
            asa.setMethodsAttribute(methods);
        }
        if(useProxy.getValue()){
            asa.setUseProxy(true);
        }

        return asa;
    }

    private static Map<String, IServiceFieldAttribute> scanFields(Class<?> clzz) {
        Class tempClass = clzz;
        List<Field> fields = new ArrayList<>();
        while (tempClass != null && !tempClass.getName().toLowerCase().equals("java.lang.object")) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        Map<String, IServiceFieldAttribute> fieldsAttr = new HashMap<>();
        for (Field f : fields) {
            if (f.isAnnotationPresent(ServiceFieldTag.class)) {
                f.setAccessible(true);
                IServiceFieldAttribute aspa = new ServiceFieldAttribute();
                ServiceFieldTag frant = f.getAnnotation(ServiceFieldTag.class);
                aspa.setName(f.getName());
                aspa.setType(frant.Type().name());
                aspa.setValue(frant.Value());
                aspa.setField(f);
                aspa.setAllowNull(frant.AllowNull());
                aspa.setSetterLifetime(frant.SetterLifetime());
                fieldsAttr.put(f.getName(), aspa);
            }
        }
        return 0 == fieldsAttr.size() ? null : fieldsAttr;
    }

    private static Map<String, IMethodAttribute> scanMethods(Class<?> clzz, RefArg<Boolean> useProxy) {
        Method[] methods = FinalAlbianReflectService.Instance.getAllMethod(clzz);
        if (null == methods || 0 == methods.length) return null;

        Map<String, IMethodAttribute> methodsAttribute = new HashMap<>();
        for (Method m : methods) {
            IMethodAttribute mAttr = scanMethod(m,useProxy);
            String methodSignature = FinalAlbianReflectService.Instance.getMethodSignature(m);
            methodsAttribute.put(methodSignature, mAttr);
        }
        return methodsAttribute;
    }

    private static IMethodAttribute scanMethod(Method m, RefArg<Boolean> useProxy) {
        IMethodAttribute mAttr = new MethodAttribute();
        if (m.isAnnotationPresent(MethodIgnoreProxyTag.class)) {
            MethodIgnoreProxyTag mr = m.getAnnotation(MethodIgnoreProxyTag.class);
            mAttr.setIgnore(mr.Ignore());
            useProxy.setValue(true);
        }
        if (m.isAnnotationPresent(MethodRetryTag.class)) {
            MethodRetryTag mrr = m.getAnnotation(MethodRetryTag.class);
            IMethodRetryAttribute mra = new MethodRetryAttribute();
            mra.setDelayMs(mrr.DelayMs());
            mra.setRetryTimes(mrr.RetryTimes());
            useProxy.setValue(true);
        }
        if (m.isAnnotationPresent(AlbianAopAttribute.class)) {
            AlbianAopAttribute aa = m.getAnnotation(AlbianAopAttribute.class);
            mAttr.setIgnore(aa.avoid());
            useProxy.setValue(true);
        }
        if(m.isAnnotationPresent(MethodMonitorTag.class)){
            MethodMonitorTag tr = m.getAnnotation(MethodMonitorTag.class);
            IMethodMonitorAttribute mtr = new MethodMonitorAttribute();
            mtr.setEnable(tr.Enable());
            mtr.setLogTagName(tr.LogTagName());
            mAttr.setStatisticsAttribute(mtr);
            useProxy.setValue(true);
        }
        if(m.isAnnotationPresent(MethodTimeoutTag.class)){
            MethodTimeoutTag tor = m.getAnnotation(MethodTimeoutTag.class);
            IMethodTimeoutAttribute mtoa = new MethodTimeoutAttribute();
            mtoa.setTimetampMs(tor.TimestampMs());
            mAttr.setTimeoutAttribute(mtoa);
            useProxy.setValue(true);
        }
        return mAttr;
    }
}
