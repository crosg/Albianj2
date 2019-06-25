package org.albianj.loader.logging;

import org.albianj.loader.AlbianApplicationServant;
import org.albianj.loader.AlbianBundleContext;
import org.albianj.loader.except.AlbianExceptionServant;
import org.albianj.loader.except.AlbianExterException;
import org.albianj.loader.except.AlbianInterException;
import org.albianj.loader.helpers.AlbianStringServant;
import org.albianj.loader.logging.impl.AlbianLogger;
import org.albianj.loader.rant.AlbianServantRant;

@AlbianServantRant
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
        if(e instanceof AlbianInterException) {
            throw (AlbianInterException) e;
        }
        if(e instanceof AlbianExterException) {
                throw (AlbianExterException) e;
        }
        String msg = AlbianStringServant.Instance.format(fmt,objs);
        if(!AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(secretMsg)){
            AlbianExceptionServant.Instance.throwInterException(AlbianExceptionServant.Code.Error,local,e,secretMsg, brief, msg);
        }
        AlbianExceptionServant.Instance.throwExterException(AlbianExceptionServant.Code.Error,local,e, brief, msg);
    }

//    public void addMonitorLog(String sessionId, AlbianLoggerLevel level, Class<?> calledClzz, Throwable e,String brief, String fmt, Object... objs) {
//        AlbianBundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
//        String msg = AlbianStringServant.Instance.format(fmt,objs);
//        bundleContext.getMonitorLogger().log(sessionId,bundleContext.getBundleName(),level,calledClzz,e,brief,null,msg);
//        return;
//    }
//
//    public void addActiveLog(String sessionId, AlbianLoggerLevel level, Class<?> calledClzz, Throwable e,String brief, String fmt, Object... objs) {
//        AlbianBundleContext bundleContext = AlbianApplicationServant.Instance.getCurrentBundleContext();
//        String msg = AlbianStringServant.Instance.format(fmt,objs);
//        bundleContext.getActiveLogger().log(sessionId,bundleContext.getBundleName(),level,calledClzz,e,brief,null,msg);
//        return;
//    }
//


//    public void addRuntimeLogAndThrow(String sessionId, AlbianLoggerLevel level,  Class<?> calledClzz, Throwable e,String breif,String secretMsg, String fmt, Object... objs){
//        addRuntimeLog(sessionId,level,calledClzz,e,breif,fmt,objs);
//        if(e instanceof AlbianInterException){
//            throw (AlbianInterException) e;
//        }
//        String msg = AlbianStringServant.Instance.format(fmt,objs);
//        throw new AlbianInterException(AlbianExceptionServant.Instance.logLevel2Code(level),e,secretMsg, breif, msg);
//    }
}
