package org.albianj.logger;

import org.albianj.boot.tags.CommentsTag;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.BuiltinNames;
import org.albianj.service.IService;
//import org.apache.http.annotation.Contract;

/**
 * Created by xuhaifeng on 17/2/9.
 */
@CommentsTag("logger service的v2版本，解决log-v1中无法正确标识文件位置问题")
public interface ILoggerService2 extends IService {

    @CommentsTag("Albianj Logger Service2在server.xml中的标识")
    String Name = BuiltinNames.AlbianLoggerService2Name;

    @CommentsTag("the session id for albianj inner thread")
    String InnerThreadName = "AlbianInnerThread";

    @CommentsTag("albianj sql presistence logger name")
    String AlbianSqlLoggerName = "AlbianSqlLogger";

    @CommentsTag("albianj runtime logger name")
    String AlbianRunningLoggerName = "AlbianRunningLogger";

    boolean isExistLogger(String logName);


    void log(@CommentsTag("当前日志记录处的文件名") String filename,
             @CommentsTag("当前日志记录处的方法名") String methodName,
             @CommentsTag("当前日志记录处的行数") int lineNumber,
             @CommentsTag("记录到的日志名称") String loggerName,
             @CommentsTag("当前的访问id") Object sessionId,
             @CommentsTag("日志的等级") AlbianLoggerLevel level,
             @CommentsTag("日志的message") String format,
             @CommentsTag("格式化参数") Object... values);

    void log(@CommentsTag("当前日志记录处的文件名") String filename,
             @CommentsTag("当前日志记录处的方法名") String methodName,
             @CommentsTag("当前日志记录处的行数") int lineNumber,
             @CommentsTag("记录到的日志名称") String loggerName,
             @CommentsTag("当前的访问id") Object sessionId,
             @CommentsTag("日志的等级") AlbianLoggerLevel level,
             @CommentsTag("记录的异常") Throwable e,
             @CommentsTag("日志的message") String format,
             @CommentsTag("格式化参数") Object... values);

    @CommentsTag("记录日志并且重新抛出异常，抛出的异常都为RuntimeException或其子类")
    @Deprecated
    void logAndThrow(@CommentsTag("当前日志记录处的文件名") String filename,
                     @CommentsTag("当前日志记录处的方法名") String methodName,
                     @CommentsTag("当前日志记录处的行数") int lineNumber,
                     @CommentsTag("记录到的日志名称") String loggerName,
                     @CommentsTag("当前的访问id") Object sessionId,
                     @CommentsTag("日志的等级") AlbianLoggerLevel level,
                     @CommentsTag("记录的异常") Throwable e,
                     @CommentsTag("当前异常发生的模块") AlbianModuleType module,
                     @CommentsTag("重新抛出异常的信息") String throwInfo,
                     @CommentsTag("日志的message") String format,
                     @CommentsTag("格式化参数") Object... values);

    void log(@CommentsTag("记录到的日志名称") String loggerName,
             @CommentsTag("当前的访问id") Object sessionId,
             @CommentsTag("日志的等级") AlbianLoggerLevel level,
             @CommentsTag("日志的message") String format,
             @CommentsTag("格式化参数") Object... values);

    void log(@CommentsTag("记录到的日志名称") String loggerName,
             @CommentsTag("当前的访问id") Object sessionId,
             @CommentsTag("日志的等级") AlbianLoggerLevel level,
             @CommentsTag("记录的异常") Throwable e,
             @CommentsTag("日志的message") String format,
             @CommentsTag("格式化参数") Object... values);

    @Deprecated
    @CommentsTag("记录日志并且重新抛出异常，抛出的异常都为RuntimeException或其子类")
    void logAndThrow(@CommentsTag("记录到的日志名称") String loggerName,
                     @CommentsTag("当前的访问id") Object sessionId,
                     @CommentsTag("日志的等级") AlbianLoggerLevel level,
                     @CommentsTag("记录的异常") Throwable e,
                     @CommentsTag("当前异常发生的模块") AlbianModuleType module,
                     @CommentsTag("重新抛出异常的信息") String throwInfo,
                     @CommentsTag("日志的message") String format,
                     @CommentsTag("格式化参数") Object... values);

    public void log3(String loggerName, AlbianLoggerLevel level, String ctx);
}
