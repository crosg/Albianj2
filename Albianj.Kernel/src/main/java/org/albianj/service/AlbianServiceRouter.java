/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
package org.albianj.service;

import org.albianj.boot.BundleContext;
import org.albianj.boot.except.ThrowableServant;
import org.albianj.boot.loader.BundleClassLoader;
import org.albianj.boot.tags.CommentsTag;
import org.albianj.datetime.AlbianDateTime;
import org.albianj.boot.except.DisplayException;
import org.albianj.boot.except.HiddenException;
import org.albianj.except.AlbianRuntimeException;
import org.albianj.kernel.ILogicIdService;
import org.albianj.loader.*;
import org.albianj.boot.IAlbianBundleService;
import org.albianj.logger.*;
import org.albianj.verify.Validate;

/**
 * albianj的service管理类，交由albianj托管的service全部由这个类提供获取service。
 */
public class AlbianServiceRouter {

    /**
     * 运行时logger，记录日志的loggerName
     */
    public final static String LoggerRunning = ILoggerService2.AlbianRunningLoggerName;
    /**
     * 数据库语句logger 记录日志的loggerName
     */
    public final static String LoggerSql = ILoggerService2.AlbianSqlLoggerName;

    /**
     *  日志的级别
     */
    public final static AlbianLoggerLevel Debug = AlbianLoggerLevel.Debug;
    public final static AlbianLoggerLevel Info = AlbianLoggerLevel.Info;
    public final static AlbianLoggerLevel Warn = AlbianLoggerLevel.Warn;
    public final static AlbianLoggerLevel Error = AlbianLoggerLevel.Error;
    public final static AlbianLoggerLevel Mark = AlbianLoggerLevel.Mark;

    /**
     * 异常级别,表示正常的异常,可能只是一个过程的需要,或者用来控制一个程序的流程
     */
    public final static int ExceptForNormal = ThrowableServant.ExceptForNormal;
    /**
     * 警告的异常,通常对程序无实质性影响,一把会使用默认值等处理掉或者容错机制处理掉
     */
    public final static int ExceptForWarn = ThrowableServant.ExceptForWarn;
    /**
     * 错误的异常,程序无法对该异常做出任何可修正的措施,程序必须中断或者停止
     */
    public final static int ExceptForError = ThrowableServant.ExceptForError;
    /**
     * 无比重要的异常,比刑爷还要重要的异常,必须引起所有人的注意,不管什么程序都需要12w分警惕
     */
    public final static int ExceptForMark = ThrowableServant.ExceptForMark;


    // 时间 级别 call-chain fmt -args
    private static String logFmt = "%s %s SessionId:%s Thread:%d CallChain:[%s] ctx:[%s]";
    private static String logExceptionFmt = "%s %s SessionId:%s Thread:%d CallChain:[%s] except:[type:%s showMsg:%s] ctx:[%s]";

    @Deprecated
    public static ILoggerService getLogger() {
        return getSingletonService(ILoggerService.class, ILoggerService.Name, false);
    }

    public static ILogicIdService getLogIdService() {
        return getSingletonService(ILogicIdService.class, ILogicIdService.Name, false);
    }

    /**
     * 获取service.xml中配置的service.
     * 注意： 1：获取的service都是单例模式
     *
     * @param <T>                获取serivce的定义接口类
     * @param cla                获取serivce的定义接口类的class信息
     * @param id                 service。xml中配置的id
     * @param isThrowIfException 是否在获取service出错或者没有获取service时候抛出异常，true为抛出异常；false不抛出异常，但是service返回null
     * @return 返回获取的service
     * @throws IllegalArgumentException id在service.xml中找不到或者是获取的service不能转换陈cla提供的class信息，将抛出遗产
     */
    public static <T extends IService> T getSingletonService(Class<T> cla, String id, boolean isThrowIfException)
            throws IllegalArgumentException {
        if (Validate.isNullOrEmptyOrAllSpace(id)) {

            getLogger().errorAndThrow(ILoggerService.AlbianRunningLoggerName, IllegalArgumentException.class,
                    "Kernel is error.", "service id is null or empty,and can not found.");
        }
        String currBundleName = AlbianBootContext.Instance.getCurrentBundleContext().getBundleName();
        return getSingletonService(currBundleName,cla,id,isThrowIfException);
    }


    /**
     * 获取service.xml中配置的service.
     * 注意： 1：获取的service都是单例模式
     * 2：这个方法已经被废弃，不再进行维护，请使用getSingletonService替代
     *
     * @param <T> 获取serivce的定义接口类
     * @param cla 获取serivce的定义接口类的class信息
     * @param id  service。xml中配置的id
     * @return 返回获取的service，在获取service出错或者没有获取service时候抛出异常
     * @throws IllegalArgumentException id在service.xml中找不到或者是获取的service不能转换陈cla提供的class信息，将抛出遗产
     */
    public static <T extends IService> T getSingletonService(Class<T> cla, String id) {
        return getSingletonService(cla, id, false);
    }

    /**
     * 获取service.xml中配置的service.
     * 注意： 1：获取的service都是单例模式
     * 2：这个方法已经被废弃，不再进行维护，请使用getSingletonService替代
     *
     * @param <T>                获取serivce的定义接口类
     * @param cla                获取serivce的定义接口类的class信息
     * @param id                 service。xml中配置的id
     * @param isThrowIfException 是否在获取service出错或者没有获取service时候抛出异常，true为抛出异常；false不抛出异常，但是service返回null
     * @return 返回获取的service
     * @throws IllegalArgumentException id在service.xml中找不到或者是获取的service不能转换陈cla提供的class信息，将抛出遗产
     */
    @Deprecated
    public static <T extends IService> T getService(Class<T> cla, String id, boolean isThrowIfException)
            throws IllegalArgumentException {
        if (Validate.isNullOrEmptyOrAllSpace(id)) {

            getLogger().errorAndThrow(ILoggerService.AlbianRunningLoggerName, IllegalArgumentException.class,
                    "Kernel is error.", "service id is null or empty,and can not found.");
        }
        return getSingletonService(cla,id,isThrowIfException);
    }

    /**
     * 获取service.xml中配置的service.
     * 注意： 1：获取的service都是单例模式
     * 2：这个方法已经被废弃，不再进行维护，请使用getSingletonService替代
     *
     * @param <T> 获取serivce的定义接口类
     * @param cla 获取serivce的定义接口类的class信息
     * @param id  service。xml中配置的id
     * @return 返回获取的service，在获取service出错或者没有获取service时候抛出异常
     * @throws IllegalArgumentException id在service.xml中找不到或者是获取的service不能转换陈cla提供的class信息，将抛出遗产
     */
    @Deprecated
    public static <T extends IService> T getService(Class<T> cla, String id) {
        return getService(cla, id, false);
    }

    @Deprecated
    public static ILoggerService2 getLogger2() {
        return getSingletonService(ILoggerService2.class, ILoggerService2.Name, false);
    }

    @Deprecated
    public static void addLog(String sessionId, String logName, AlbianLoggerLevel logLevel, String fmt, Object... args) {
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        int count = stes.length >= 7 ? 7 : stes.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < count; i++) {
            StackTraceElement ste = stes[i];
            sb.append(ste.getFileName())
                    .append("$").append(ste.getMethodName())
                    .append("$").append(ste.getLineNumber())
                    .append(" -> ");
        }
        if (0 != sb.length()) {
            sb.delete(sb.length() - 4, sb.length() - 1);
        }


        ILoggerService2 log = getSingletonService(ILoggerService2.class, ILoggerService2.Name, false);
        if (null != log) {
            String msg = String.format(logFmt, AlbianDateTime.fmtCurrentLongDatetime(), logLevel.getTag(), sessionId,
                    Thread.currentThread().getId(), sb, String.format(fmt, args));
            log.log3(logName, logLevel, msg);
        }
    }
    @Deprecated
    public static void addLog(String sessionId, String logName, AlbianLoggerLevel logLevel, Throwable t, String fmt, Object... args) {
        StackTraceElement[] stes = t.getStackTrace();
        int count = stes.length >= 6 ? 6 : stes.length;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            StackTraceElement ste = stes[i];
            sb.append(ste.getFileName())
                    .append("$").append(ste.getMethodName())
                    .append("$").append(ste.getLineNumber())
                    .append(" -> ");
        }
        if (0 != sb.length()) {
            sb.delete(sb.length() - 4, sb.length() - 1);
        }

        ILoggerService2 log = getSingletonService(ILoggerService2.class, ILoggerService2.Name, false);
        if (null != log) {
            String msg = String.format(logExceptionFmt, AlbianDateTime.fmtCurrentLongDatetime(), logLevel.getTag(), sessionId,
                    Thread.currentThread().getId(), sb, t.getClass().getName(), t.getMessage(), String.format(fmt, args));
            log.log3(logName, logLevel, msg);
        }
    }
    @Deprecated
    public static void throwException(String sessionId, String logName, Throwable throwable) {
        throwException(sessionId, logName, "throw", throwable, true);
    }
    @Deprecated
    public static void throwException(String sessionId, String logName, String brief, Throwable throwable) {
        throwException(sessionId, logName, brief, throwable, true);
    }
    @Deprecated
    public static void throwException(String sessionId, String logName, String brief, Throwable throwable, boolean throwsOut) {
        if (AlbianRuntimeException.class.isAssignableFrom(throwable.getClass())) {
            //warp once over,and not again
            addLog(sessionId, logName, AlbianLoggerLevel.Warn, throwable, "throw exception -> %s", throwable.getClass().getName());
            if (throwsOut) {
                throw ((AlbianRuntimeException) throwable);
            }
            return;
        }
        AlbianRuntimeException thw = new AlbianRuntimeException(throwable);
        addLog(sessionId, logName, AlbianLoggerLevel.Warn, throwable,
                "brief-> %s warp excetion -> %s with showMsg ->%s to new AlbianRuntimeException.",
                brief, throwable.getClass().getName(), throwable.getMessage());
        if (throwsOut) {
            throw thw;
        }
    }
    @Deprecated
    public static void throwException(String sessionId, String logName, String brief, String msg) {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        AlbianRuntimeException thw = new AlbianRuntimeException(stacks[2].getClassName(), stacks[2].getMethodName(), stacks[2].getLineNumber(), msg);
        addLog(sessionId, logName, AlbianLoggerLevel.Warn,
                "brief-> %s new excetion with showMsg ->%s to AlbianRuntimeException.",
                brief, msg);
        throw thw;
    }
    @Deprecated
    public static void throwException(String sessionId, String logName, String msg) {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        AlbianRuntimeException thw = new AlbianRuntimeException(stacks[2].getClassName(), stacks[2].getMethodName(), stacks[2].getLineNumber(), msg);
        addLog(sessionId, logName, AlbianLoggerLevel.Warn,
                "new excetion with showMsg ->%s to AlbianRuntimeException.",
                msg);
        throw thw;
    }

    /**
     * 异常级别,表示正常的异常,可能只是一个过程的需要,或者用来控制一个程序的流程
     */
    public final static int ExceptCodeForNormal = ThrowableServant.ExceptForNormal;
    /**
     * 警告的异常,通常对程序无实质性影响,一把会使用默认值等处理掉或者容错机制处理掉
     */
    public final static int ExceptCodeForWarn = ThrowableServant.ExceptForWarn;
    /**
     * 错误的异常,程序无法对该异常做出任何可修正的措施,程序必须中断或者停止
     */
    public final static int ExceptCodeForError = ThrowableServant.ExceptForError;
    /**
     * 无比重要的异常,比刑爷还要重要的异常,必须引起所有人的注意,不管什么程序都需要12w分警惕
     */
    public final static int ExceptCodeForMark = ThrowableServant.ExceptForMark;

    /**
     * 找到bundle模式下的bundle名
     * 如果bundleName有值，直接返回bundleName
     * 如果bundleName无值，如果当前线程的classloader为AlbianBundleClassLoader类型，返回AlbianBundleClassLoader中的bundleName，否则返回默认值 AlbianBootService.BootBundleName
     * @param bundleName
     * @return
     */
    private static String findBundleName(String bundleName){
        if(!Validate.isNullOrEmptyOrAllSpace(bundleName)) {
            return bundleName;
        }

        ClassLoader loader =  Thread.currentThread().getContextClassLoader();
        if(!loader.getClass().isAssignableFrom(BundleClassLoader.class)) {
            return AlbianBootService.RootBundleName;
        }

        BundleClassLoader bundleClassLoader = (BundleClassLoader) loader;
        return bundleClassLoader.getBundleName();
    }

    public static <T extends IService> T getSingletonService(String bundleName, Class<T> clzz, String serviceId, boolean isThrowIfServiceNotExist){
        BundleContext bundleContext = AlbianBootContext.Instance.findBundleContext(bundleName,isThrowIfServiceNotExist);
        IAlbianBundleService bundleService =  bundleContext.getBundleService(serviceId);
        if((null == bundleContext) && isThrowIfServiceNotExist){

        }
        return clzz.cast(bundleService);
    }

    public static <T extends IService> T getSingletonService(String bundleName, Class<T> clzz, String serviceId){
        return getSingletonService(bundleName,clzz,serviceId,false);
    }

    public static String LogRoot4Runtime = IBundleLoggerService.LogName4Runtime;
    public static String LogRoot4State = IBundleLoggerService.LogName4State;
    public static String LogRoot4Monitor = IBundleLoggerService.LogName4Monitor;

    @CommentsTag("统一的日志处理方法,记录非敏感日志")
    public static void addLogV2(String sessionId,String logName,AlbianLoggerLevel level,
                                Throwable excp,String brief,Object... info){
        try {
            StackTraceElement[] stes = null;
            if (null == excp) {
                stes = Thread.currentThread().getStackTrace();
            } else {
                stes = excp.getStackTrace();
            }

            String msg = AlbianLoggerOpt.Instance.buildMsg(sessionId, AlbianBootContext.Instance.getCurrentBundleContext().getBundleName(),level,stes, brief,excp, null, info);
            AlbianLoggerOpt.Instance.logMsg(logName, level, excp, msg);
        }catch (Throwable t){
            System.out.println("logger in fail and ignore the exception -> " + t.getMessage());
        }
    }
    @CommentsTag("统一的日志处理方法,记录非敏感日志")
    public static void addLogV2(String sessionId,String logName,AlbianLoggerLevel level,
                                Throwable excp,String interMsg,String brief,Object... info){
        try {
            StackTraceElement[] stes = null;
            if (null == excp) {
                stes = Thread.currentThread().getStackTrace();
            } else {
                stes = excp.getStackTrace();
            }

            String msg = AlbianLoggerOpt.Instance.buildMsg(sessionId, AlbianBootContext.Instance.getCurrentBundleContext().getBundleName(),level,stes, brief,excp, interMsg, info);
            AlbianLoggerOpt.Instance.logMsg(logName, level, excp, msg);
        }catch (Throwable t){
            System.out.println("logger in fail and ignore the exception -> " + t.getMessage());
        }
    }
    @CommentsTag("统一的日志处理方法")
    public static void throwEnterExceptionV2(String sessionId, String logName, AlbianLoggerLevel level,
                                             Throwable excp, String brief, Object... info){
        addLogV2(sessionId,AlbianBootContext.Instance.getCurrentBundleContext().getBundleName(),logName,level,excp, brief,info);
        if (DisplayException.class.isAssignableFrom(excp.getClass())) {
            throw (DisplayException) excp;
        }

        if(null == excp) {
            throw new DisplayException(ThrowableServant.logLevel2Code(level), brief, info);
        }

        throw new DisplayException(ThrowableServant.logLevel2Code(level), excp,brief, info);
    }

    @CommentsTag("统一的日志处理方法")
    public static void throwInterExceptionV2(String sessionId,String logName,AlbianLoggerLevel level,
                                             Throwable excp,String interMsg,String brief,Object... info){
        addLogV2(sessionId,AlbianBootContext.Instance.getCurrentBundleContext().getBundleName(),logName,level,excp,interMsg, brief,info);
        if (HiddenException.class.isAssignableFrom(excp.getClass())) {
            throw (HiddenException) excp;
        }

        if(null == excp) {
            throw new HiddenException(ThrowableServant.logLevel2Code(level),interMsg, brief, info);
        }

        throw new HiddenException(ThrowableServant.logLevel2Code(level), excp,interMsg,brief, info);
    }

    @CommentsTag("统一的日志处理方法,记录非敏感日志")
    public static void addLogV2(String sessionId,String bundleName,String logName,AlbianLoggerLevel level,
                                Throwable excp,String brief,Object... info){
        try {
            StackTraceElement[] stes = null;
            if (null == excp) {
                stes = Thread.currentThread().getStackTrace();
            } else {
                stes = excp.getStackTrace();
            }

            String msg = AlbianLoggerOpt.Instance.buildMsg(sessionId, bundleName,level,stes, brief,excp, null, info);
            AlbianLoggerOpt.Instance.logMsg(logName, level, excp, msg);
        }catch (Throwable t){
            System.out.println("logger in fail and ignore the exception -> " + t.getMessage());
        }
    }

    @CommentsTag("统一的日志处理方法,记录非敏感日志")
    public static void addLogV2(String sessionId,String bundleName,String logName,AlbianLoggerLevel level,
                                Throwable excp,String interMsg,String brief,Object... info){
        try {
            StackTraceElement[] stes = null;
            if (null == excp) {
                stes = Thread.currentThread().getStackTrace();
            } else {
                stes = excp.getStackTrace();
            }
            String msg = AlbianLoggerOpt.Instance.buildMsg(sessionId,bundleName, level,stes, brief,excp, interMsg, info);
            AlbianLoggerOpt.Instance.logMsg(logName, level, excp, msg);
        }catch (Throwable t){
            System.out.println("logger in fail and ignore the exception -> " + t.getMessage());
        }
    }


    @CommentsTag("统一的日志处理方法")
    public static void throwEnterExceptionV2(String sessionId, String bundleName,String logName, AlbianLoggerLevel level,
                                             Throwable excp, String brief, Object... info){
        addLogV2(sessionId,bundleName,logName,level,excp, brief,info);
        if (DisplayException.class.isAssignableFrom(excp.getClass())) {
            throw (DisplayException) excp;
        }

        if(null == excp) {
            throw new DisplayException(ThrowableServant.logLevel2Code(level), brief, info);
        }

        throw new DisplayException(ThrowableServant.logLevel2Code(level), excp,brief, info);
    }

    @CommentsTag("统一的日志处理方法")
    public static void throwInterExceptionV2(String sessionId,String bundleName,String logName,AlbianLoggerLevel level,
                                             Throwable excp,String interMsg,String brief,Object... info){
        addLogV2(sessionId,bundleName,logName,level,excp,interMsg, brief,info);
        if (HiddenException.class.isAssignableFrom(excp.getClass())) {
            throw (HiddenException) excp;
        }

        if(null == excp) {
            throw new HiddenException(ThrowableServant.logLevel2Code(level),interMsg, brief, info);
        }

        throw new HiddenException(ThrowableServant.logLevel2Code(level), excp,interMsg,brief, info);
    }
}
