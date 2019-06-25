package org.albianj.service.impl;

import org.albianj.aop.*;
import org.albianj.aop.impl.AlbianServiceAopAttribute;
import org.albianj.aop.rant.*;
import org.albianj.argument.RefArg;
import org.albianj.boot.*;
import org.albianj.boot.loader.AlbianClassLoader;
import org.albianj.loader.*;
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
                        //must flag with anno and extends IAlbianService
                        // extends interface is compatibling the last version
                        return cls.isAnnotationPresent(AlbianServiceRant.class)
                                && IAlbianService.class.isAssignableFrom(cls)
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

    public static IAlbianBundleServiceAttribute scanAlbianService(Class<?> implClzz) {
        IAlbianBundleServiceAttribute asa = new AlbianBundleServiceAttribute();
        AlbianServiceRant rant = implClzz.getAnnotation(AlbianServiceRant.class);
        asa.setId(rant.Id());
        if (Validate.isNullOrEmptyOrAllSpace(rant.sInterface()) && null == rant.Interface()) {
            asa.setInterface(IAlbianService.class.getName());
        } else {
            asa.setInterface(null != rant.Interface() ? rant.Interface().getName() : rant.sInterface());
        }
        asa.setEnable(rant.Enable());
        asa.setType(implClzz.getName());
        asa.setServiceClass(implClzz.asSubclass(IAlbianService.class));

        if (implClzz.isAnnotationPresent(AlbianServiceProxyRants.class)) {
            AlbianServiceProxyRants prants = implClzz.getAnnotation(AlbianServiceProxyRants.class);
            Map<String, IAlbianServiceAopAttribute> asaas = new HashMap<>();
            for (AlbianServiceProxyRant prant : prants.Rants()) {
                IAlbianServiceAopAttribute aspa = new AlbianServiceAopAttribute();
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

        Map<String, IAlbianServiceFieldAttribute> fields = scanFields(implClzz);
        if (!Validate.isNullOrEmpty(fields)) {
            asa.setServiceFields(fields);
        }

        RefArg<Boolean> useProxy = new RefArg<>();
        Map<String, IAlbianServiceMethodAttribute> methods = scanMethods(implClzz,useProxy);
        if (!Validate.isNullOrEmpty(methods)) {
            asa.setMethodsAttribute(methods);
        }
        if(useProxy.getValue()){
            asa.setUseProxy(true);
        }

        return asa;
    }

    private static Map<String, IAlbianServiceFieldAttribute> scanFields(Class<?> clzz) {
        Class tempClass = clzz;
        List<Field> fields = new ArrayList<>();
        while (tempClass != null && !tempClass.getName().toLowerCase().equals("java.lang.object")) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        Map<String, IAlbianServiceFieldAttribute> fieldsAttr = new HashMap<>();
        for (Field f : fields) {
            if (f.isAnnotationPresent(AlbianServiceFieldRant.class)) {
                f.setAccessible(true);
                IAlbianServiceFieldAttribute aspa = new AlbianServiceFieldAttribute();
                AlbianServiceFieldRant frant = f.getAnnotation(AlbianServiceFieldRant.class);
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

    private static Map<String, IAlbianServiceMethodAttribute> scanMethods(Class<?> clzz, RefArg<Boolean> useProxy) {
        Method[] methods = FinalAlbianReflectService.Instance.getAllMethod(clzz);
        if (null == methods || 0 == methods.length) return null;

        Map<String, IAlbianServiceMethodAttribute> methodsAttribute = new HashMap<>();
        for (Method m : methods) {
            IAlbianServiceMethodAttribute mAttr = scanMethod(m,useProxy);
            String methodSignature = FinalAlbianReflectService.Instance.getMethodSignature(m);
            methodsAttribute.put(methodSignature, mAttr);
        }
        return methodsAttribute;
    }

    private static IAlbianServiceMethodAttribute scanMethod(Method m, RefArg<Boolean> useProxy) {
        IAlbianServiceMethodAttribute mAttr = new AlbianServiceMethodAttribute();
        if (m.isAnnotationPresent(AlbianMethodNonProxyRant.class)) {
            AlbianMethodNonProxyRant mr = m.getAnnotation(AlbianMethodNonProxyRant.class);
            mAttr.setIgnore(mr.Ignore());
            useProxy.setValue(true);
        }
        if (m.isAnnotationPresent(AlbianMethodRetryRant.class)) {
            AlbianMethodRetryRant mrr = m.getAnnotation(AlbianMethodRetryRant.class);
            IAlbianServiceMethodRetryAttribute mra = new AlbianServiceMethodRetryAttribute();
            mra.setDelayMs(mrr.DelayMs());
            mra.setRetryTimes(mrr.RetryTimes());
            useProxy.setValue(true);
        }
        if (m.isAnnotationPresent(AlbianAopAttribute.class)) {
            AlbianAopAttribute aa = m.getAnnotation(AlbianAopAttribute.class);
            mAttr.setIgnore(aa.avoid());
            useProxy.setValue(true);
        }
        if(m.isAnnotationPresent(AlbianMethodStatisticsRant.class)){
            AlbianMethodStatisticsRant tr = m.getAnnotation(AlbianMethodStatisticsRant.class);
            IAlbianServiceMethodStatisticsAttribute mtr = new AlbianServiceMethodStatisticsAttribute();
            mtr.setEnable(tr.Enable());
            mtr.setLogTagName(tr.LogTagName());
            mAttr.setStatisticsAttribute(mtr);
            useProxy.setValue(true);
        }
        if(m.isAnnotationPresent(AlbianMethodTimeoutRant.class)){
            AlbianMethodTimeoutRant tor = m.getAnnotation(AlbianMethodTimeoutRant.class);
            IAlbianServiceMethodTimeoutAttribute mtoa = new AlbianServiceMethodTimeoutAttribute();
            mtoa.setTimetampMs(tor.TimestampMs());
            mAttr.setTimeoutAttribute(mtoa);
            useProxy.setValue(true);
        }
        return mAttr;
    }
}
