package org.albianj.service.impl;

import org.albianj.aop.AlbianServiceProxyRant;
import org.albianj.aop.AlbianServiceProxyRants;
import org.albianj.aop.IAlbianServiceAopAttribute;
import org.albianj.aop.impl.AlbianServiceAopAttribute;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.loader.AlbianClassScanner;
import org.albianj.loader.IAlbianClassExcavator;
import org.albianj.loader.IAlbianClassFilter;
import org.albianj.service.*;
import org.albianj.verify.Validate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

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

    public static IAlbianServiceAttribute scanAlbianService(Class<?> implClzz) {
        IAlbianServiceAttribute asa = new AlbianServiceAttribute();
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
        }

        Map<String, IAlbianServiceFieldAttribute> fields = scanFields(implClzz);
        if (!Validate.isNullOrEmpty(fields)) {
            asa.setServiceFields(fields);
        }

        return asa;
    }

    private static Map<String, IAlbianServiceFieldAttribute> scanFields(Class<?> clzz) {
        Field[] fields = clzz.getDeclaredFields();
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
                fieldsAttr.put(f.getName(), aspa);
            }
        }
        return 0 == fieldsAttr.size() ? null : fieldsAttr;
    }
}
