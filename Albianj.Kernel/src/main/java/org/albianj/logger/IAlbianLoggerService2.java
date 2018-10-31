package org.albianj.logger;

import org.albianj.comment.Comments;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.IAlbianService;
//import org.apache.http.annotation.Contract;

/**
 * Created by xuhaifeng on 17/2/9.
 */
@org.albianj.comment.Comments("logger service的v2版本，解决log-v1中无法正确标识文件位置问题")
public interface IAlbianLoggerService2 extends IAlbianService {

    @Comments("Albianj Logger Service2在server.xml中的标识")
    String Name = "AlbianLoggerService2";

    @Comments("the session id for albianj inner thread")
    String InnerThreadName = "AlbianInnerThread";

    @Comments("albianj sql presistence logger name")
    String AlbianSqlLoggerName = "AlbianSqlLogger";

    @Comments("albianj runtime logger name")
    String AlbianRunningLoggerName = "AlbianRunningLogger";

    void log(@Comments("当前日志记录处的文件名") String filename,
             @Comments("当前日志记录处的方法名") String methodName,
             @Comments("当前日志记录处的行数") int lineNumber,
             @Comments("记录到的日志名称") String loggerName,
             @Comments("当前的访问id") Object sessionId,
             @Comments("日志的等级") AlbianLoggerLevel level,
             @Comments("日志的message") String format,
             @Comments("格式化参数") Object... values);

    void log(@Comments("当前日志记录处的文件名") String filename,
             @Comments("当前日志记录处的方法名") String methodName,
             @Comments("当前日志记录处的行数") int lineNumber,
             @Comments("记录到的日志名称") String loggerName,
             @Comments("当前的访问id") Object sessionId,
             @Comments("日志的等级") AlbianLoggerLevel level,
             @Comments("记录的异常") Throwable e,
             @Comments("日志的message") String format,
             @Comments("格式化参数") Object... values);

    @Comments("记录日志并且重新抛出异常，抛出的异常都为RuntimeException或其子类")
    @Deprecated
    void logAndThrow(@Comments("当前日志记录处的文件名") String filename,
                     @Comments("当前日志记录处的方法名") String methodName,
                     @Comments("当前日志记录处的行数") int lineNumber,
                     @Comments("记录到的日志名称") String loggerName,
                     @Comments("当前的访问id") Object sessionId,
                     @Comments("日志的等级") AlbianLoggerLevel level,
                     @Comments("记录的异常") Throwable e,
                     @Comments("当前异常发生的模块") AlbianModuleType module,
                     @Comments("重新抛出异常的信息") String throwInfo,
                     @Comments("日志的message") String format,
                     @Comments("格式化参数") Object... values);

    void log(@Comments("记录到的日志名称") String loggerName,
             @Comments("当前的访问id") Object sessionId,
             @Comments("日志的等级") AlbianLoggerLevel level,
             @Comments("日志的message") String format,
             @Comments("格式化参数") Object... values);

    void log(@Comments("记录到的日志名称") String loggerName,
             @Comments("当前的访问id") Object sessionId,
             @Comments("日志的等级") AlbianLoggerLevel level,
             @Comments("记录的异常") Throwable e,
             @Comments("日志的message") String format,
             @Comments("格式化参数") Object... values);

    @Deprecated
    @Comments("记录日志并且重新抛出异常，抛出的异常都为RuntimeException或其子类")
    void logAndThrow(@Comments("记录到的日志名称") String loggerName,
                     @Comments("当前的访问id") Object sessionId,
                     @Comments("日志的等级") AlbianLoggerLevel level,
                     @Comments("记录的异常") Throwable e,
                     @Comments("当前异常发生的模块") AlbianModuleType module,
                     @Comments("重新抛出异常的信息") String throwInfo,
                     @Comments("日志的message") String format,
                     @Comments("格式化参数") Object... values);

    public void log3(String loggerName, AlbianLoggerLevel level,String ctx);
}
