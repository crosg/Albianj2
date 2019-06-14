package org.albianj.persistence.service;

import org.albianj.loader.AlbianBundleContext;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.object.IAlbianEntityFieldAttribute;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianBuiltinNames;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by xuhaifeng on 17/3/14.
 */
public class AlbianObjectCreator {


    public static IAlbianObject newInstance(String sessionId,AlbianBundleContext bundleContext, String itf) {
    AlbianEntityMetadata entityMetadata = bundleContext.getModuleConf(AlbianBuiltinNames.Conf.Persistence);
    IAlbianObjectAttribute attr = entityMetadata.getEntityMetadata(itf);
        if (null == attr) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error,
                    "can not found interface:%s attribute,please lookup persistence config.", itf);
            throw new RuntimeException("no found interface attribute.");
        }
        String className = attr.getType();
        if (Validate.isNullOrEmptyOrAllSpace(className)) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error,
                    " can not found impl-class for interface:%s,please lookup persistence config.", itf);
            throw new RuntimeException("no implements class.");
        }
        Class<?> cls = null;
        try {
            Class<?> itfs = AlbianClassLoader.getInstance().loadClass(itf);
            if (!itfs.isInterface()) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error,
                        "%s in not a interface.", itf);
                throw new RuntimeException("no found interface.");
            }
            if (!IAlbianObject.class.isAssignableFrom(itfs)) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error,
                        "%s in not a interface.", itf);
                throw new RuntimeException("interface inherit error.");
            }
            cls = AlbianClassLoader.getInstance().loadClass(className);
            if (null == cls) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error,
                        "class:%s is not found.", className);
                throw new RuntimeException("not found class.");

            }
            if (!IAlbianObject.class.isAssignableFrom(cls)) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error,
                        "class:%s is not extends from IAlbianObject.", className);
                throw new RuntimeException("class inherit error.");
            }
            if (!itfs.isAssignableFrom(cls)) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error,
                        "class:%s is not extends from interface:%s.", className, itf);
                throw new RuntimeException("class inherit error.");
            }
            IAlbianObject obj = (IAlbianObject) cls.newInstance();
            return obj;
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "class:%s is not found.", className);
        }

        return null;
    }

    public static IAlbianObject newInstance(String sessionId, AlbianBundleContext bundleContext,Class<? extends IAlbianObject> clazz) {
        return newInstance(sessionId,bundleContext, clazz.getName());
    }

    public static void copyObject(IAlbianObject dest,IAlbianObject src){
        copyObject(dest,src);
    }

    /**
     *  将src中的值按照field复制到dest
     * @param dest
     * @param src
     * @param ctrlFields 控制字段
     *                      1. !fielename，field前加!,表示此字段不被复制
     *                      2. destFieldName=secFieldName，表示src中的field和dest中的对应复制
     */
    public static void copyObject(AlbianBundleContext bundleContext,IAlbianObject dest,IAlbianObject src,String[] ctrlFields){

        Set<String> ignoreFields = new HashSet();
        Map<String,String>  rltFields = new HashMap<>();
        if(null != ctrlFields) {
            for (String f : ctrlFields) {
                if (f.startsWith("!")) {
                    ignoreFields.add(f.substring(1).toLowerCase());
                } else if (f.contains("=")) {
                    String[] ds = f.split("=");
                    rltFields.put(ds[0].toLowerCase(), ds[1].toLowerCase());
                }

            }
        }

        Class destClzz = dest.getClass();
        Class srcClzz = src.getClass();
        AlbianEntityMetadata entityMetadata = bundleContext.getModuleConf(AlbianBuiltinNames.Conf.Persistence);
        IAlbianObjectAttribute destObjAttr = entityMetadata.getEntityMetadataByType(destClzz);
        IAlbianObjectAttribute srcObjAttr = entityMetadata.getEntityMetadataByType(srcClzz);
        Map<String , IAlbianEntityFieldAttribute> destFieldAttrs = destObjAttr.getFields();
        Map<String , IAlbianEntityFieldAttribute> srcFieldAttrs = srcObjAttr.getFields();

        for(Map.Entry<String,IAlbianEntityFieldAttribute> entry : destFieldAttrs.entrySet()) {
            String key = entry.getKey();
            if(ignoreFields.contains(key)) { // 忽略这个field
                continue;
            }

           String srcKey =  rltFields.containsKey(key) ? rltFields.get(key) : key;
            if(!srcFieldAttrs.containsKey(srcKey)) { // src中没有这个field
                continue;
            }

            IAlbianEntityFieldAttribute srcFieldAttr =  srcFieldAttrs.get(srcKey);
            IAlbianEntityFieldAttribute destFieldAttr =  entry.getValue();
            try {
                destFieldAttr.getEntityField().set(dest,srcFieldAttr.getEntityField().get(src));
            } catch (IllegalAccessException e) {

            }
        }
    }
}
