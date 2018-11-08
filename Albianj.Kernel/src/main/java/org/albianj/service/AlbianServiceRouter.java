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

import org.albianj.datetime.AlbianDateTime;
import org.albianj.except.AlbianRuntimeException;
import org.albianj.kernel.IAlbianLogicIdService;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.logger.RuntimeLogType;
import org.albianj.verify.Validate;

/**
 * albianj的service管理类，交由albianj托管的service全部由这个类提供获取service。
 */
public class AlbianServiceRouter extends ServiceContainer {

    public static String AlbianRuntimeLogName = "AlbianRuntime";

    @Deprecated
    public static IAlbianLoggerService getLogger() {
        return getSingletonService(IAlbianLoggerService.class, IAlbianLoggerService.Name, false);
    }

    public static IAlbianLogicIdService getLogIdService() {
        return getSingletonService(IAlbianLogicIdService.class, IAlbianLogicIdService.Name, false);
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
    public static <T extends IAlbianService> T getSingletonService(Class<T> cla, String id, boolean isThrowIfException)
            throws IllegalArgumentException {
        if (Validate.isNullOrEmptyOrAllSpace(id)) {

            getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, IllegalArgumentException.class,
                    "Kernel is error.", "service id is null or empty,and can not found.");
        }

        try {
            IAlbianService service = (IAlbianService) ServiceContainer.getService(id);
            if (null == service)
                return null;
            return cla.cast(service);
        } catch (IllegalArgumentException exc) {
            getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, exc, "Get service:%1$s is error.", id);

            if (isThrowIfException)
                throw exc;
        }
        return null;
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
    public static <T extends IAlbianService> T getSingletonService(Class<T> cla, String id) {
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
    public static <T extends IAlbianService> T getService(Class<T> cla, String id, boolean isThrowIfException)
            throws IllegalArgumentException {
        if (Validate.isNullOrEmptyOrAllSpace(id)) {

            getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, IllegalArgumentException.class,
                    "Kernel is error.", "service id is null or empty,and can not found.");
        }

        try {
            IAlbianService service = (IAlbianService) ServiceContainer.getService(id);
            if (null == service)
                return null;
            return cla.cast(service);
        } catch (IllegalArgumentException exc) {
            getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, exc, "Get service:%1$s is error.", id);

            if (isThrowIfException)
                throw exc;
        }
        return null;
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
    public static <T extends IAlbianService> T getService(Class<T> cla, String id) {
        return getService(cla, id, false);
    }

    @Deprecated
    public static IAlbianLoggerService2 getLogger2() {
        return getSingletonService(IAlbianLoggerService2.class, IAlbianLoggerService2.Name, false);
    }

    // 时间 级别 call-chain fmt -args
    private static String logFmt = "%s %s SessionId:%s Thread:%d CallChain:[%s] ctx:[%s]";
    public static void addLog(String sessionId, String logName, AlbianLoggerLevel logLevel, String fmt, Object... args){
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        int count = stes.length >= 7 ? 7: stes.length;
        StringBuilder sb = new StringBuilder();
        for(int i = 2;i < count;i++){
            StackTraceElement ste = stes[i];
            sb.append(ste.getFileName())
                    .append("$").append(ste.getMethodName())
                    .append("$").append(ste.getLineNumber())
                    .append(" -> ");
        }
        if(0!= sb.length()) {
            sb.delete(sb.length()-4,sb.length() -1);
        }


        IAlbianLoggerService2 log = getSingletonService(IAlbianLoggerService2.class,IAlbianLoggerService2.Name, false);
        if(null != log){
            String msg = String.format(logFmt, AlbianDateTime.fmtCurrentLongDatetime(),logLevel.getTag(),sessionId,
                                        Thread.currentThread().getId(),sb, String.format(fmt,args));
            log.log3(logName,logLevel,msg);
        }
    }

    private static String logExceptionFmt = "%s %s SessionId:%s Thread:%d CallChain:[%s] except:[type:%s msg:%s] ctx:[%s]";
    public static void addLog(String sessionId, String logName,AlbianLoggerLevel logLevel, Throwable t, String fmt, Object... args){
        StackTraceElement[] stes = t.getStackTrace();
        int count = stes.length >=6 ? 6: stes.length;

        StringBuilder sb = new StringBuilder();
        for(int i = 0;i < count;i++){
            StackTraceElement ste = stes[i];
            sb.append(ste.getFileName())
                    .append("$").append(ste.getMethodName())
                    .append("$").append(ste.getLineNumber())
                    .append(" -> ");
        }
        if(0!= sb.length()) {
            sb.delete(sb.length()-4,sb.length() -1);
        }

        IAlbianLoggerService2 log = getSingletonService(IAlbianLoggerService2.class,IAlbianLoggerService2.Name, false);
        if(null != log){
            String msg = String.format(logExceptionFmt, AlbianDateTime.fmtCurrentLongDatetime(),logLevel.getTag(),sessionId,
                    Thread.currentThread().getId(),sb,t.getClass().getName(),t.getMessage(), String.format(fmt,args));
            log.log3(logName,logLevel,msg);
        }
    }

    public static void throwException(String sessionId,String logName,Throwable throwable){
        throwException(sessionId,logName,"throw",throwable,true);
    }

    public static void throwException(String sessionId,String logName,String brief,Throwable throwable){
        throwException(sessionId,logName,brief,throwable,true);
    }

    public static void throwException(String sessionId,String logName,String brief,Throwable throwable,boolean throwsOut){
        if(AlbianRuntimeException.class.isAssignableFrom(throwable.getClass())){
            //warp once over,and not again
            addLog(sessionId,logName, AlbianLoggerLevel.Warn,throwable,"throw exception -> %s",throwable.getClass().getName());
            if(throwsOut){
                throw  ((AlbianRuntimeException) throwable);
            }
            return;
        }
        AlbianRuntimeException thw = new AlbianRuntimeException(throwable);
        addLog(sessionId,logName,AlbianLoggerLevel.Warn,throwable,
                "brief-> %s warp excetion -> %s with msg ->%s to new AlbianRuntimeException.",
                brief,throwable.getClass().getName(),throwable.getMessage());
        if(throwsOut){
            throw  thw;
        }
    }

    public static void throwException(String sessionId,String logName, String brief,String msg){
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        AlbianRuntimeException thw = new AlbianRuntimeException(stacks[2].getClassName(),stacks[2].getMethodName(),stacks[2].getLineNumber(),msg);
        addLog(sessionId,logName,AlbianLoggerLevel.Warn,
                "brief-> %s new excetion with msg ->%s to AlbianRuntimeException.",
                brief,msg);
        throw thw;
    }

    public static void throwException(String sessionId,String logName, String msg){
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        AlbianRuntimeException thw = new AlbianRuntimeException(stacks[2].getClassName(),stacks[2].getMethodName(),stacks[2].getLineNumber(),msg);
        addLog(sessionId,logName,AlbianLoggerLevel.Warn,
                "new excetion with msg ->%s to AlbianRuntimeException.",
               msg);
        throw thw;
    }
}
