package org.albianj.persistence.impl.rant;

import org.albianj.loader.*;
import org.albianj.persistence.impl.object.AlbianEntityFieldAttribute;
import org.albianj.persistence.impl.object.AlbianObjectAttribute;
import org.albianj.persistence.impl.object.DataRouterAttribute;
import org.albianj.persistence.impl.routing.AlbianDataRouterParserService;
import org.albianj.persistence.impl.storage.AlbianStorageParserService;
import org.albianj.persistence.impl.toolkit.Convert;
import org.albianj.persistence.object.*;
import org.albianj.persistence.object.rants.AlbianObjectDataFieldRant;
import org.albianj.persistence.object.rants.AlbianObjectDataRouterRant;
import org.albianj.persistence.object.rants.AlbianObjectDataRoutersRant;
import org.albianj.persistence.object.rants.AlbianObjectRant;
import org.albianj.persistence.service.AlbianEntityMetadata;
import org.albianj.reflection.AlbianReflect;
import org.albianj.service.AlbianBuiltinNames;
import org.albianj.text.StringHelper;
import org.albianj.verify.Validate;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Types;
import java.util.*;

public class AlbianEntityRantScaner {

    public static HashMap<String, Object> scanPackage(final AlbianBundleContext bundleContext,final String pkgName) throws IOException, ClassNotFoundException {
        return AlbianClassScanner.filter(bundleContext.getClassLoader(),
                pkgName,

                new IAlbianClassFilter() {
                    @Override
                    public boolean verify(Class<?> cls) {
                        //must flag with anno and extends IAlbianObject
                        // extends interface is compatibling the last version
                        return cls.isAnnotationPresent(AlbianObjectRant.class)
                                && IAlbianObject.class.isAssignableFrom(cls)
                                && !cls.isInterface()
                                && !Modifier.isAbstract(cls.getModifiers());
                    }
                },

                new IAlbianClassExcavator() {
                    @Override
                    public Object finder(Class<?> clzz) {
                        IAlbianObjectAttribute objAttr = null;
                        AlbianObjectRant or = clzz.getAnnotation(AlbianObjectRant.class);
                        if (null == or.Interface()) {
                            return null;
                        }

                        Class<?> itfClzz = or.Interface();
                        String sItf = itfClzz.getName();
                        AlbianEntityMetadata entityMetadata = bundleContext.getModuleConfAndNewIfNotExist(AlbianBuiltinNames.Conf.Persistence,AlbianEntityMetadata.class);
                        if (entityMetadata.exist(sItf)) {
                            objAttr = entityMetadata.getEntityMetadata(sItf);
                        } else {
                            objAttr = new AlbianObjectAttribute();
                            objAttr.setType(clzz.getName());
                            objAttr.setInterface(sItf);
                            entityMetadata.put(sItf, objAttr);
                        }

                        objAttr.setImplClzz(clzz);

                        Map<String, IAlbianEntityFieldAttribute> fields = scanFields(clzz);
                        if (!Validate.isNullOrEmpty(fields)) {
                            objAttr.setFields(fields);
                        }

                        IDataRouterAttribute defaultRouting = makeDefaultDataRouter(clzz);
                        objAttr.setDefaultRouting(defaultRouting);


                        AlbianObjectDataRoutersRant drr = or.DataRouters();
                        IDataRoutersAttribute pkgDataRouterAttr = scanRouters(clzz, drr);
                        //set data router
                        if (null != pkgDataRouterAttr) {
                            IDataRoutersAttribute cfgDataRouterAttr = objAttr.getDataRouters();
                            if (null == cfgDataRouterAttr) { // not exist data router from drouter.xml
                                objAttr.setDataRouters(pkgDataRouterAttr);
                            } else {
                                Map<String, IDataRouterAttribute> cfgWRouter = cfgDataRouterAttr.getWriterRouters();
                                Map<String, IDataRouterAttribute> cfgRRouter = cfgDataRouterAttr.getReaderRouters();
                                Map<String, IDataRouterAttribute> pkgWRouter = pkgDataRouterAttr.getWriterRouters();
                                Map<String, IDataRouterAttribute> pkgRRouter = pkgDataRouterAttr.getReaderRouters();
                                if (null != pkgRRouter) {
                                    if (null != cfgRRouter) {
                                        //exist pkg datarouter and cfg datarouter,merger them base cfg datarouter
                                        pkgRRouter.putAll(cfgRRouter);
                                    }
                                    //if not exist cfg drouter or memgered drouter,set to total drouter
                                    cfgDataRouterAttr.setReaderRouters(pkgRRouter);

                                }

                                if (null != pkgWRouter) {
                                    if (null != cfgWRouter) {
                                        pkgWRouter.putAll(cfgWRouter);
                                    }
                                    cfgDataRouterAttr.setWriterRouters(pkgRRouter);
                                }
                            }
                        }
                        return objAttr;
                    }
                });
    }

    private static IDataRoutersAttribute scanRouters(Class<?> clzz, AlbianObjectDataRoutersRant drr) {
        if (null == drr.DataRouter()) {
            return null;
        }

        Class<?> clazz = drr.DataRouter();

        if (!IAlbianObjectDataRouter.class.isAssignableFrom(clazz)) {
            // datarouter not impl IAlbianObjectDataRouter
            return null;
        }

        IDataRoutersAttribute drsAttr = new DataRoutersAttribute();
        IAlbianObjectDataRouter dr = null;
        try {
            dr = (IAlbianObjectDataRouter) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        drsAttr.setDataRouter(dr);
        drsAttr.setReaderRouterEnable(drr.ReaderRoutersEnable());
        drsAttr.setWriterRouterEnable(drr.WriterRoutersEnable());

        Map<String, IDataRouterAttribute> rMap = scanRouter(clzz, drr.ReaderRouters());
        drsAttr.setReaderRouters(rMap);

        Map<String, IDataRouterAttribute> wMap = scanRouter(clzz, drr.WriterRouters());
        drsAttr.setWriterRouters(wMap);
        return drsAttr;

    }

    private static Map<String, IDataRouterAttribute> scanRouter(Class<?> clzz, AlbianObjectDataRouterRant[] rrs) {
        Map<String, IDataRouterAttribute> map = new HashMap<>();
        for (AlbianObjectDataRouterRant odrr : rrs) {
            if (odrr.Enable()) {
                IDataRouterAttribute dra = new DataRouterAttribute();
                dra.setEnable(true);
                dra.setName(odrr.Name());
                dra.setStorageName(odrr.StorageName());

                if (!Validate.isNullOrEmptyOrAllSpace(odrr.TableOwner())) {
                    dra.setOwner(odrr.TableOwner());
                }
                if (!Validate.isNullOrEmptyOrAllSpace(odrr.TableName())) {
                    dra.setTableName(odrr.TableName());
                } else {
                    dra.setTableName(clzz.getSimpleName());
                }
                map.put(dra.getName(), dra);

            }
        }
        return map;
    }


    public static Map<String, IAlbianEntityFieldAttribute> scanFields(Class<?> clzz) {

        Class tempClass = clzz;
        List<Field> fields = new ArrayList<>() ;
        while (tempClass !=null && !tempClass.getName().toLowerCase().equals("java.lang.object") ) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }

        Map<String, IAlbianEntityFieldAttribute> fieldsAttrs = new HashMap<>();
        for (Field f : fields) {
            IAlbianEntityFieldAttribute fAttr = null;
            if (f.isAnnotationPresent(AlbianObjectDataFieldRant.class)) {
                fAttr = new AlbianEntityFieldAttribute();
                AlbianObjectDataFieldRant fr = f.getAnnotation(AlbianObjectDataFieldRant.class);
                if (fr.Ignore()) {
                    continue;
                }
                fAttr.setName(f.getName());
                f.setAccessible(true);
                fAttr.setEntityField(f);
                String propertyName = null;
                if (Validate.isNullOrEmptyOrAllSpace(fr.PropertyName())) {
                    propertyName = FieldConvert.fieldName2PropertyName(f.getName());
                    fAttr.setPropertyName(propertyName);
                } else {
                    propertyName = StringHelper.lowercasingFirstLetter(fr.PropertyName());
                    fAttr.setPropertyName(propertyName);
                }


                if (Validate.isNullOrEmptyOrAllSpace(fr.FieldName())) {
                    fAttr.setSqlFieldName(StringHelper.uppercasingFirstLetter(propertyName));
                } else {
                    fAttr.setSqlFieldName(fr.FieldName());
                }


                fAttr.setAllowNull(fr.IsAllowNull());
                if (Types.OTHER == fr.DbType()) {
                    fAttr.setDatabaseType(Convert.toSqlType(f.getType()));
                } else {
                    fAttr.setDatabaseType(fr.DbType());
                }
                fAttr.setIsSave(fr.IsSave());
                fAttr.setLength(fr.Length());
                fAttr.setPrimaryKey(fr.IsPrimaryKey());
                fAttr.setAutoGenKey(fr.IsAutoGenKey());
                try {
                    PropertyDescriptor pd = AlbianReflect.getBeanPropertyDescriptor(clzz, propertyName);
                    if (null != pd) {
                        if (null != pd.getReadMethod()) {
                            fAttr.setPropertyGetter(pd.getReadMethod());
                        }
                        if (null != pd.getWriteMethod()) {
                            fAttr.setPropertySetter(pd.getWriteMethod());
                        }
                    }
                } catch (ClassNotFoundException | IntrospectionException e) {
                }

            } else {
                fAttr = new AlbianEntityFieldAttribute();
                f.setAccessible(true);
                fAttr.setName(f.getName());
                String propertyName = FieldConvert.fieldName2PropertyName(f.getName());
                fAttr.setPropertyName(propertyName);
                fAttr.setSqlFieldName(StringHelper.uppercasingFirstLetter(propertyName));
                fAttr.setDatabaseType(Convert.toSqlType(f.getType()));
                fAttr.setEntityField(f);
                try {
                    PropertyDescriptor pd = AlbianReflect.getBeanPropertyDescriptor(clzz, propertyName);
                    if (null != pd) {
                        if (null != pd.getReadMethod()) {
                            fAttr.setPropertyGetter(pd.getReadMethod());
                        }
                        if (null != pd.getWriteMethod()) {
                            fAttr.setPropertySetter(pd.getWriteMethod());
                        }
                    }
                } catch (ClassNotFoundException | IntrospectionException e) {
                }
            }
            fieldsAttrs.put(fAttr.getPropertyName().toLowerCase(), fAttr);
        }
        return 0 == fieldsAttrs.size() ? null : fieldsAttrs;
    }


    private static IDataRouterAttribute makeDefaultDataRouter(Class<?> implClzz) {
        IDataRouterAttribute defaultRouting = new DataRouterAttribute();
        defaultRouting.setName(AlbianDataRouterParserService.DEFAULT_ROUTING_NAME);
        defaultRouting.setOwner("dbo");
        defaultRouting.setStorageName(AlbianStorageParserService.DEFAULT_STORAGE_NAME);
        defaultRouting.setTableName(implClzz.getSimpleName());
        return defaultRouting;
    }


}


