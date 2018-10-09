package org.albianj.persistence.service;

import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;


/**
 * Created by xuhaifeng on 17/3/14.
 */
public class AlbianObjectCreator {


    public static IAlbianObject newInstance(String sessionId, String itf){
        IAlbianObjectAttribute attr  = AlbianEntityMetadata.getEntityMetadata(itf);
//            IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
//            IAlbianObjectAttribute attr = amps.getAlbianObjectAttribute(itf);
            if(null == attr){
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error,
                        "can not found interface:%s attribute,please lookup persistence config.",itf);
                throw new RuntimeException("no found interface attribute.");
            }
            String className = attr.getType();
            if(Validate.isNullOrEmptyOrAllSpace(className)){
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error,
                        " can not found impl-class for interface:%s,please lookup persistence config.",itf);
                throw new RuntimeException("no implements class.");
            }
            Class<?> cls = null;
            try {
                Class<?> itfs = AlbianClassLoader.getInstance().loadClass(itf);
                if(!itfs.isInterface()){
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Error,
                            "%s in not a interface.",itf);
                    throw new RuntimeException("no found interface.");
                }
                if(!IAlbianObject.class.isAssignableFrom(itfs)){
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Error,
                            "%s in not a interface.",itf);
                    throw new RuntimeException("interface inherit error.");
                }
                cls = AlbianClassLoader.getInstance().loadClass(className);
                if(null == cls){
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Error,
                            "class:%s is not found.",className);
                    throw new RuntimeException("not found class.");

                }
                if(!IAlbianObject.class.isAssignableFrom(cls)){
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Error,
                            "class:%s is not extends from IAlbianObject.",className);
                    throw new RuntimeException("class inherit error.");
                }
                if(!itfs.isAssignableFrom(cls)){
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Error,
                            "class:%s is not extends from interface:%s.",className,itf);
                    throw new RuntimeException("class inherit error.");
                }
                IAlbianObject obj =(IAlbianObject) cls.newInstance();
                return obj;
            } catch (Exception e) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error,e, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "class:%s is not found.",className);
            }

        return null;
    }

    public static IAlbianObject newInstance(String sessionId, Class<? extends IAlbianObject> clazz){
        return newInstance(sessionId, clazz.getName());
    }
}
