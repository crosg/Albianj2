package Albian.Core.Service;

import org.albianj.comment.Comments;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.service.AlbianObjectCreator;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianServiceRouter;

public final class AlbianServiceHub extends AlbianServiceRouter {

    public static IAlbianObject newInstance(String sessionId, String itf){
        return AlbianObjectCreator.newInstance(sessionId,itf);
    }

    public static <T extends  IAlbianObject> T newInstance(String sessionId, Class<T> clazz){
        return (T) newInstance(sessionId, clazz.getName());
    }

        public static  void log(@Comments("记录到的日志名称") String loggerName,
             @Comments("当前的访问id") Object sessionId,
             @Comments("日志的等级") AlbianLoggerLevel level,
             @Comments("日志的message") String format,
             @Comments("格式化参数") Object... values){

        }

        public static  void log(@Comments("记录到的日志名称") String loggerName,
             @Comments("当前的访问id") Object sessionId,
             @Comments("日志的等级") AlbianLoggerLevel level,
             @Comments("记录的异常") Throwable e,
             @Comments("日志的message") String format,
             @Comments("格式化参数") Object... values){

        }

        @Comments("记录日志并且重新抛出异常，抛出的异常都为RuntimeException或其子类")
        public static void logAndThrow(@Comments("记录到的日志名称") String loggerName,
                     @Comments("当前的访问id") Object sessionId,
                     @Comments("日志的等级") AlbianLoggerLevel level,
                   @Comments("重新抛出异常的信息") String throwInfo,
                     @Comments("记录的异常") Throwable e,
                     @Comments("日志的message") String format,
                     @Comments("格式化参数") Object... values){

        }

}
