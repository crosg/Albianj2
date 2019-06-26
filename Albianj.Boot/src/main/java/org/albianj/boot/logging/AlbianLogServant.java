package org.albianj.boot.logging;

import org.albianj.boot.logging.impl.AlbianLogger;
import org.albianj.boot.AlbianApplicationServant;
import org.albianj.boot.AlbianBundleContext;
import org.albianj.boot.except.AlbianDisplayException;
import org.albianj.boot.except.AlbianExceptionServant;
import org.albianj.boot.except.AlbianHiddenException;
import org.albianj.boot.helpers.AlbianStringServant;

/**
 * 日志的实用类,所有的日志都由这个类在使用.
 * 当app启动的时候,程序会默认在logspath下建立Runtime日志,默认大小为10MB,级别为DEBUG.
 * 该日志会负责启动部分的日志记录.
 * 当app解析了boot.xml文件后,会解析到其中的logger配置节,该配置节可以重新设置Runtime日志的大小与级别,但是文件路径无法更改
 * 并且,该配置信息的级别会立即起作用,文件大小会对下一个日志文件起作用.
 *
 * Albian的日志只有buffer appender与console appender.
 * 一般情况下,开发环境下buffer append与console的appender全部打开,线上环境只打开buffer appender即可.
 *
 * 日志buffer appender的flush有2中模式,一种是当buffer缓冲池满的时候
 *
 */
public class AlbianLogServant {

    public static AlbianLogServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new AlbianLogServant();
        }
    }

    protected AlbianLogServant() {

    }

    public void newRuntimeLogger(String logName,String logsFolder,String level,boolean isOpenConsole){
        IAlbianLogger logger =  new AlbianLogger(logName,logsFolder,level,isOpenConsole);
        AlbianBundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
        bundleContext.setRuntimeLogger(logger);
    }

    public void updateRuntimeLogger(String level,boolean isOpenConsole,String maxFilesize){
        AlbianBundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
        IAlbianLogger logger = bundleContext.getRuntimeLogger();
        if(isOpenConsole && !logger.isConsoleAppenderOpened()){
            logger.openConsoleAppender();
        } else {
            if(logger.isConsoleAppenderOpened()){
                logger.closeConsoleAppender();
            }
        }

        logger.setLoggerLevel(level);
        logger.setMaxFilesize(maxFilesize);
    }

    /**
     *  记录日志
     *
     *  优先记录到当前bundle的RuntimeLogger，如果当前的bundle没有找到，或者当前bundle的RuntimeLogger未初始化，记录到bootBundle的Runtime
     *  so，必须确保在执行该方法前已经存在RuntimeLogger。
     *
     * @param sessionId 当前执行方法的人员或者userid，不要为空
     * @param level 日志的级别
     * @param local 当前记录日志的class，不能为空
     * @param e 当前抛出的系统异常，如没有，赋值null
     * @param brief 日志的简短秒数，比如，User Error 或者 XmlParser Error
     * @param secretMsg 带有敏感信息的记录内容，注意该内容不能直接抛出，如没有，直接复制null
     * @param fmt 日志的格式化字段，使用位置为索引。例如"Workfolder ->{0} is not exist,but it must setting by class -> {1}.",workFolder,mainClzz.getName()
     * @param objs 供日志格式化字段使用的参数
     */
    public void addRuntimeLog(String sessionId, AlbianLoggerLevel level,  Class<?> local, Throwable e,String brief,String secretMsg,String fmt, Object... objs) {
       AlbianBundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
        String msg = AlbianStringServant.Instance.format(fmt,objs);
        IAlbianLogger logger = null;
        if(null == bundleContext){
            AlbianBundleContext bootBundleCtx = AlbianApplicationServant.Instance.getBootBundleContext();
            logger = bootBundleCtx.getRuntimeLogger();
        } else {
            logger = bundleContext.getRuntimeLogger();
            if (null == logger) {
                AlbianBundleContext bootBundleCtx = AlbianApplicationServant.Instance.getBootBundleContext();
                logger = bootBundleCtx.getRuntimeLogger();
            }
        }
       logger.log(sessionId, bundleContext.getBundleName(), level,  local, e,brief,secretMsg, msg);
       return;
    }

    /**
     *  记录日志，并且抛出异常
     *
     *  当secretMsg 为null，为空或者都为空格时，抛出AlbianExterException，反之，抛出抛出AlbianInterException
     *
     *  优先记录到当前bundle的RuntimeLogger，如果当前的bundle没有找到，或者当前bundle的RuntimeLogger未初始化，记录到bootBundle的Runtime
     *  so，必须确保在执行该方法前已经存在RuntimeLogger。
     *
     * @param sessionId 当前执行方法的人员或者userid，不要为空
     * @param level 日志的级别
     * @param local 当前记录日志的class，不能为空
     * @param e 当前抛出的系统异常，如没有，赋值null
     * @param brief 日志的简短秒数，比如，User Error 或者 XmlParser Error
     * @param secretMsg 带有敏感信息的记录内容，注意该内容不能直接抛出，如没有，直接复制null
     * @param fmt 日志的格式化字段，使用位置为索引。例如"Workfolder ->{0} is not exist,but it must setting by class -> {1}.",workFolder,mainClzz.getName()
     * @param objs 供日志格式化字段使用的参数
     */
    public void addRuntimeLogAndThrow(String sessionId, AlbianLoggerLevel level, Class<?> local, Throwable e,String brief,String secretMsg,String fmt, Object... objs){
        addRuntimeLog(sessionId,level,local,e,brief,secretMsg,fmt,objs);
        if(e instanceof AlbianHiddenException) {
            throw (AlbianHiddenException) e;
        }
        if(e instanceof AlbianDisplayException) {
                throw (AlbianDisplayException) e;
        }
        String msg = AlbianStringServant.Instance.format(fmt,objs);
        if(!AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(secretMsg)){
            AlbianExceptionServant.Instance.throwHiddenException(AlbianExceptionServant.Code.Error,local,e,secretMsg, brief, msg);
        }
        AlbianExceptionServant.Instance.throwDisplayException(AlbianExceptionServant.Code.Error,local,e, brief, msg);
    }

//    public void addMonitorLog(String sessionId, AlbianLoggerLevel level, Class<?> calledClzz, Throwable e,String brief, String fmt, Object... objs) {
//        AlbianBundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
//        String showMsg = AlbianStringServant.Instance.format(fmt,objs);
//        bundleContext.getMonitorLogger().log(sessionId,bundleContext.getBundleName(),level,calledClzz,e,brief,null,showMsg);
//        return;
//    }
//
//    public void addActiveLog(String sessionId, AlbianLoggerLevel level, Class<?> calledClzz, Throwable e,String brief, String fmt, Object... objs) {
//        AlbianBundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
//        String showMsg = AlbianStringServant.Instance.format(fmt,objs);
//        bundleContext.getActiveLogger().log(sessionId,bundleContext.getBundleName(),level,calledClzz,e,brief,null,showMsg);
//        return;
//    }
//


//    public void addRuntimeLogAndThrow(String sessionId, AlbianLoggerLevel level,  Class<?> calledClzz, Throwable e,String breif,String secretMsg, String fmt, Object... objs){
//        addRuntimeLog(sessionId,level,calledClzz,e,breif,fmt,objs);
//        if(e instanceof AlbianHiddenException){
//            throw (AlbianHiddenException) e;
//        }
//        String showMsg = AlbianStringServant.Instance.format(fmt,objs);
//        throw new AlbianHiddenException(AlbianExceptionServant.Instance.logLevel2Code(level),e,secretMsg, breif, showMsg);
//    }
}
