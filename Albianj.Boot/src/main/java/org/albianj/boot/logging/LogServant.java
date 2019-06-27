package org.albianj.boot.logging;

import org.albianj.boot.BundleContext;
import org.albianj.boot.except.ThrowableServant;
import org.albianj.boot.except.DisplayException;
import org.albianj.boot.except.HiddenException;
import org.albianj.boot.helpers.StringServant;
import org.albianj.boot.logging.impl.Logger;
import org.albianj.boot.AlbianApplicationServant;
import org.albianj.boot.tags.BundleSharingTag;

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
@BundleSharingTag
public class LogServant {

    public static LogServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new LogServant();
        }
    }

    protected LogServant() {

    }

    public void newRuntimeLogger(String logName,String logsFolder,String level,boolean isOpenConsole){
        ILogger logger =  new Logger(logName,logsFolder,level,isOpenConsole);
        BundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
        bundleContext.setRuntimeLogger(logger);
    }

    public void newRuntimeLogger(ILoggerAttribute logAttr){
        ILogger logger =  new Logger(logAttr.getLoggerName(),logAttr.getPath(),logAttr.getLevel(),logAttr.isOpenConsole());
        BundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
        bundleContext.setRuntimeLogger(logger);
    }

    public void updateRuntimeLogger(String level,boolean isOpenConsole,String maxFilesize){
        BundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
        ILogger logger = bundleContext.getRuntimeLogger();
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
     * @param refType 当前记录日志的class，不能为空
     * @param e 当前抛出的系统异常，如没有，赋值null
     * @param brief 日志的简短秒数，比如，User Error 或者 XmlParser Error
     * @param secretMsg 带有敏感信息的记录内容，注意该内容不能直接抛出，如没有，直接复制null
     * @param fmt 日志的格式化字段，使用位置为索引。例如"Workfolder ->{0} is not exist,but it must setting by class -> {1}.",workFolder,mainClzz.getName()
     * @param objs 供日志格式化字段使用的参数
     */
    public void addRuntimeLog(String sessionId, LoggerLevel level, Class<?> refType, Throwable e, String brief, String secretMsg, String fmt, Object... objs) {
       BundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
        String msg = StringServant.Instance.format(fmt,objs);
        ILogger logger = null;
        if(null == bundleContext){
            BundleContext bootBundleCtx = AlbianApplicationServant.Instance.getBootBundleContext();
            logger = bootBundleCtx.getRuntimeLogger();
        } else {
            logger = bundleContext.getRuntimeLogger();
            if (null == logger) {
                BundleContext bootBundleCtx = AlbianApplicationServant.Instance.getBootBundleContext();
                logger = bootBundleCtx.getRuntimeLogger();
            }
        }
       logger.log(sessionId, bundleContext.getBundleName(), level,  refType, e,brief,secretMsg, msg);
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
     * @param refType 当前记录日志的class，不能为空
     * @param e 当前抛出的系统异常，如没有，赋值null
     * @param brief 日志的简短秒数，比如，User Error 或者 XmlParser Error
     * @param secretMsg 带有敏感信息的记录内容，注意该内容不能直接抛出，如没有，直接复制null
     * @param fmt 日志的格式化字段，使用位置为索引。例如"Workfolder ->{0} is not exist,but it must setting by class -> {1}.",workFolder,mainClzz.getName()
     * @param objs 供日志格式化字段使用的参数
     */
    public void addRuntimeLogAndThrow(String sessionId, LoggerLevel level, Class<?> refType, Throwable e, String brief, String secretMsg, String fmt, Object... objs){
        addRuntimeLog(sessionId,level,refType,e,brief,secretMsg,fmt,objs);
        if(e instanceof HiddenException) {
            throw (HiddenException) e;
        }
        if(e instanceof DisplayException) {
                throw (DisplayException) e;
        }
        String msg = StringServant.Instance.format(fmt,objs);
        if(!StringServant.Instance.isNullOrEmptyOrAllSpace(secretMsg)){
            ThrowableServant.Instance.throwHiddenException(ThrowableServant.Code.Error,refType,e,secretMsg, brief, msg);
        }
        ThrowableServant.Instance.throwDisplayException(ThrowableServant.Code.Error,refType,e, brief, msg);
    }

//    public void addMonitorLog(String sessionId, LoggerLevel level, Class<?> calledClzz, Throwable e,String brief, String fmt, Object... objs) {
//        BundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
//        String showMsg = StringServant.Instance.format(fmt,objs);
//        bundleContext.getMonitorLogger().log(sessionId,bundleContext.getBundleName(),level,calledClzz,e,brief,null,showMsg);
//        return;
//    }
//
//    public void addActiveLog(String sessionId, LoggerLevel level, Class<?> calledClzz, Throwable e,String brief, String fmt, Object... objs) {
//        BundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
//        String showMsg = StringServant.Instance.format(fmt,objs);
//        bundleContext.getActiveLogger().log(sessionId,bundleContext.getBundleName(),level,calledClzz,e,brief,null,showMsg);
//        return;
//    }
//


//    public void addRuntimeLogAndThrow(String sessionId, LoggerLevel level,  Class<?> calledClzz, Throwable e,String breif,String secretMsg, String fmt, Object... objs){
//        addRuntimeLog(sessionId,level,calledClzz,e,breif,fmt,objs);
//        if(e instanceof HiddenException){
//            throw (HiddenException) e;
//        }
//        String showMsg = StringServant.Instance.format(fmt,objs);
//        throw new HiddenException(ThrowableServant.Instance.logLevel2Code(level),e,secretMsg, breif, showMsg);
//    }
}
