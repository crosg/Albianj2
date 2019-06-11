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
package org.albianj.logger.impl;

import org.albianj.io.Path;
import org.albianj.kernel.KernelSetting;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.reflection.AlbianReflect;
import org.albianj.runtime.IStackTrace;
import org.albianj.runtime.RuningTrace;
import org.albianj.service.AlbianServiceException;
import org.albianj.service.AlbianServiceRant;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianService;
import org.albianj.verify.Validate;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Formatter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Seapeak
 */
@AlbianServiceRant(Id = IAlbianLoggerService.Name, Interface = IAlbianLoggerService.class)
public class AlbianLoggerService extends FreeAlbianService implements
        IAlbianLoggerService {

    private static ConcurrentHashMap<String, Logger> loggers = null;

    public String getServiceName() {
        return Name;
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#loading()
     */
    @Override
    public void loading() throws AlbianServiceException {
        try {
            Thread.currentThread().setContextClassLoader(AlbianClassLoader.getInstance());
            if (KernelSetting.getAlbianConfigFilePath().startsWith("http://")) {
                DOMConfigurator.configure(new URL(Path
                        .getExtendResourcePath(KernelSetting
                                .getAlbianConfigFilePath() + "log4j.xml")));
            } else {

                DOMConfigurator.configure(Path
                        .getExtendResourcePath(KernelSetting
                                .getAlbianConfigFilePath() + "log4j.xml"));
            }

            super.loading();
            loggers = new ConcurrentHashMap<String, Logger>();
        } catch (Exception exc) {
            throw new AlbianServiceException(exc.getMessage(), exc.getCause());
        }
    }

    public boolean isExistLogger(String logName){
        return (null != loggers) && loggers.contains(logName);
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#getLogger(java.lang.String)
     */
    @Override
    public Logger getLogger(String name) {
        Thread.currentThread().setContextClassLoader(AlbianClassLoader.getInstance());
        if (loggers.containsKey(name)) {
            return loggers.get(name);
        } else {
            Logger logger = LoggerFactory.getLogger(name);
            loggers.put(name, logger);
            return logger;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.logger.IAlbianLoggerService#error(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#error(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public void error(String loggerName, String format, Object... values) {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        logger.error(getErrorMsg(format, values));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.logger.IAlbianLoggerService#warn(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#warn(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public void warn(String loggerName, String format, Object... values) {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        logger.warn(getWarnMsg(format, values));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.logger.IAlbianLoggerService#info(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#info(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public void info(String loggerName, String format, Object... values) {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        logger.info(getInfoMsg(format, values));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.logger.IAlbianLoggerService#debug(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#debug(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public void debug(String loggerName, String format, Object... values) {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        logger.debug(getDebugMsg(format, values));
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#error(java.lang.String, java.lang.Exception, java.lang.String, java.lang.Object)
     */
    @Override
    public void error(String loggerName, Exception e, String format,
                      Object... values) {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        logger.error(getErrorMsg(e, format, values));
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#errorAndThrow(java.lang.String, T, java.lang.String, java.lang.Object)
     */
    @Override
    public <T extends Exception> void errorAndThrow(String loggerName,
                                                    T e, String format, Object... values) throws RuntimeException {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        if (logger.isErrorEnabled()) {
            logger.error(getErrorMsg(e, format, values));
            throw new RuntimeException(e);
        }

    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#errorAndThrow(java.lang.String, java.lang.Class, java.lang.Exception, java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public <T extends Exception> void errorAndThrow(String loggerName,
                                                    Class<T> cls, Exception e, String eInfo, String format,
                                                    Object... values) throws RuntimeException {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        if (logger.isErrorEnabled()) {
            String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
            String msg = getErrorMsg(e, format, values);
            String tMsg = String.format("%s | %s.", id, msg);
            logger.error(tMsg);
            Class[] clss = new Class[]{String.class};
            Object[] vars = new Object[]{"Error:" + id + "," + eInfo};
            T throwObject = null;
            try {
                throwObject = (T) AlbianReflect.newInstance(cls, clss, vars);
            } catch (Exception e1) {
                throw new RuntimeException(e);
            }
            if (null != throwObject)
                throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#errorAndThrow(java.lang.String, java.lang.Class, java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public <T extends Exception> void errorAndThrow(String loggerName,
                                                    Class<T> cls, String eInfo, String format, Object... values)
            throws RuntimeException {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        if (logger.isErrorEnabled()) {
            String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
            String msg = getErrorMsg(format, values);
            String tMsg = String.format("%s | %s.", id, msg);
            logger.error(tMsg);
            Class[] clss = new Class[]{String.class};
            Object[] vars = new Object[]{"Error:" + id + "," + eInfo};
            T throwObject = null;
            try {
                throwObject = (T) AlbianReflect.newInstance(cls, clss, vars);
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
            if (null != throwObject)
                throw new RuntimeException(throwObject);
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#warn(java.lang.String, java.lang.Exception, java.lang.String, java.lang.Object)
     */
    @Override
    public void warn(String loggerName, Exception e, String format,
                     Object... values) {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        logger.warn(getWarnMsg(e, format, values));
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#warnAndThrow(java.lang.String, T, java.lang.String, java.lang.Object)
     */
    @Override
    public <T extends Exception> void warnAndThrow(String loggerName,
                                                   T e, String format, Object... values) throws T {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        if (logger.isWarnEnabled()) {
            logger.warn(getWarnMsg(e, format, values));
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#warnAndThrow(java.lang.String, java.lang.Class, java.lang.Exception, java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public <T extends Exception> void warnAndThrow(String loggerName,
                                                   Class<T> cls, Exception e, String eInfo, String format,
                                                   Object... values) throws RuntimeException {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        if (logger.isWarnEnabled()) {
            String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
            String msg = getWarnMsg(e, format, values);
            String tMsg = String.format("%s | %s.", id, msg);
            logger.warn(tMsg);
            Class[] clss = new Class[]{String.class};
            Object[] vars = new Object[]{"Warn:" + id + "," + eInfo};
            T throwObject = null;
            try {
                throwObject = (T) AlbianReflect.newInstance(cls, clss, vars);
            } catch (Exception e1) {
                throw new RuntimeException(e);
            }
            if (null != throwObject)
                throw new RuntimeException(throwObject);
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#warnAndThrow(java.lang.String, java.lang.Class, java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public <T extends Exception> void warnAndThrow(String loggerName,
                                                   Class<T> cls, String eInfo, String format, Object... values)
            throws RuntimeException {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        if (logger.isWarnEnabled()) {
            String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
            String msg = getWarnMsg(format, values);
            String tMsg = String.format("%s | %s.", id, msg);
            logger.warn(tMsg);
            Class[] clss = new Class[]{String.class};
            Object[] vars = new Object[]{"Warn:" + id + "," + eInfo};
            T throwObject = null;
            try {
                throwObject = (T) AlbianReflect.newInstance(cls, clss, vars);
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
            if (null != throwObject)
                throw new RuntimeException(throwObject);
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#info(java.lang.String, java.lang.Exception, java.lang.String, java.lang.Object)
     */
    @Override
    public void info(String loggerName, Exception e, String format,
                     Object... values) {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        logger.info(getInfoMsg(e, format, values));
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#infoAndThrow(java.lang.String, T, java.lang.String, java.lang.Object)
     */
    @Override
    public <T extends Exception> void infoAndThrow(String loggerName,
                                                   T e, String format, Object... values) throws RuntimeException {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        if (logger.isInfoEnabled()) {
            logger.info(getInfoMsg(e, format, values));
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#infoAndThrow(java.lang.String, java.lang.Class, java.lang.Exception, java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public <T extends Exception> void infoAndThrow(String loggerName,
                                                   Class<T> cls, Exception e, String eInfo, String format,
                                                   Object... values) throws RuntimeException {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        if (logger.isInfoEnabled()) {
            String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
            String msg = getInfoMsg(e, format, values);
            String tMsg = String.format("%s | %s.", id, msg);
            logger.info(tMsg);
            Class[] clss = new Class[]{String.class};
            Object[] vars = new Object[]{"Info:" + id + "," + eInfo};
            T throwObject = null;
            try {
                throwObject = (T) AlbianReflect.newInstance(cls, clss, vars);
            } catch (Exception e1) {
                throw new RuntimeException(e);
            }
            if (null != throwObject)
                throw new RuntimeException(throwObject);
            ;
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#infoAndThrow(java.lang.String, java.lang.Class, java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public <T extends Exception> void infoAndThrow(String loggerName,
                                                   Class<T> cls, String eInfo, String format, Object... values)
            throws RuntimeException {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        if (logger.isInfoEnabled()) {
            String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
            String msg = getInfoMsg(format, values);
            String tMsg = String.format("%s | %s.", id, msg);
            logger.info(tMsg);
            Class[] clss = new Class[]{String.class};
            Object[] vars = new Object[]{"EID" + id + "." + eInfo};
            T throwObject = null;
            try {
                throwObject = (T) AlbianReflect.newInstance(cls, clss, vars);
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
            if (null != throwObject)
                throw new RuntimeException(throwObject);
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#debug(java.lang.String, java.lang.Exception, java.lang.String, java.lang.Object)
     */
    @Override
    public void debug(String loggerName, Exception e, String format,
                      Object... values) {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        logger.debug(getDebugMsg(e, format, values));
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#debugAndThrow(java.lang.String, T, java.lang.String, java.lang.Object)
     */
    @Override
    public <T extends Exception> void debugAndThrow(String loggerName,
                                                    T e, String format, Object... values) throws RuntimeException {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        if (logger.isDebugEnabled()) {
            logger.debug(getDebugMsg(e, format, values));
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#debugAndThrow(java.lang.String, java.lang.Class, java.lang.Exception, java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public <T extends Exception> void debugAndThrow(String loggerName,
                                                    Class<T> cls, Exception e, String eInfo, String format,
                                                    Object... values) throws RuntimeException {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        if (logger.isDebugEnabled()) {
            String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
            String msg = getDebugMsg(e, format, values);
            String tMsg = String.format("%s | %s.", id, msg);
            logger.debug(tMsg);
            Class[] clss = new Class[]{String.class};
            Object[] vars = new Object[]{"Debug:" + id + "," + eInfo};
            T throwObject = null;
            try {
                throwObject = (T) AlbianReflect.newInstance(cls, clss, vars);
            } catch (Exception e1) {
                throw new RuntimeException(e);
            }
            if (null != throwObject)
                throw new RuntimeException(throwObject);
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#debugAndThrow(java.lang.String, java.lang.Class, java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public <T extends Exception> void debugAndThrow(String loggerName,
                                                    Class<T> cls, String eInfo, String format, Object... values)
            throws RuntimeException {
        Logger logger = getLogger(loggerName);
        if (null == logger)
            return;
        if (logger.isDebugEnabled()) {
            String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
            String msg = getDebugMsg(format, values);
            String tMsg = String.format("%s | %s.", id, msg);
            logger.debug(tMsg);
            Class[] clss = new Class[]{String.class};
            Object[] vars = new Object[]{"Debug" + id + "," + eInfo};
            T throwObject = null;
            try {
                throwObject = (T) AlbianReflect.newInstance(cls, clss, vars);
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
            if (null != throwObject)
                throw new RuntimeException(throwObject);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.albianj.logger.IAlbianLoggerService#getErrorMsg(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#getErrorMsg(java.lang.String, java.lang.Object)
     */
    @Override
    public String getErrorMsg(String format, Object... values) {
        return getMessage(MARK_ERROR, format, values);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.logger.IAlbianLoggerService#getWarnMsg(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#getWarnMsg(java.lang.String, java.lang.Object)
     */
    @Override
    public String getWarnMsg(String format, Object... values) {
        return getMessage(MARK_WARN, format, values);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.logger.IAlbianLoggerService#getInfoMsg(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#getInfoMsg(java.lang.String, java.lang.Object)
     */
    @Override
    public String getInfoMsg(String format, Object... values) {
        return getMessage(MARK_INFO, format, values);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.albianj.logger.IAlbianLoggerService#getDebugMsg(java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#getDebugMsg(java.lang.String, java.lang.Object)
     */
    @Override
    public String getDebugMsg(String format, Object... values) {
        return getMessage(MARK_DEBUG, format, values);
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#getErrorMsg(java.lang.Exception, java.lang.String, java.lang.Object)
     */
    @Override
    public String getErrorMsg(Exception e, String format,
                              Object... values) {
        return getMessage(MARK_ERROR, e, format, values);
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#getWarnMsg(java.lang.Exception, java.lang.String, java.lang.Object)
     */
    @Override
    public String getWarnMsg(Exception e, String format,
                             Object... values) {
        return getMessage(MARK_WARN, e, format, values);
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#getInfoMsg(java.lang.Exception, java.lang.String, java.lang.Object)
     */
    @Override
    public String getInfoMsg(Exception e, String format,
                             Object... values) {
        return getMessage(MARK_INFO, e, format, values);
    }

    /* (non-Javadoc)
     * @see org.albianj.logger.impl.qqqqqq#getDebugMsg(java.lang.Exception, java.lang.String, java.lang.Object)
     */
    @Override
    public String getDebugMsg(Exception e, String format,
                              Object... values) {
        return getMessage(MARK_DEBUG, e, format, values);
    }

    protected String getMessage(String level, Exception e,
                                String format, Object... values) {
        IStackTrace trace = RuningTrace.getTraceInfo(e);
        StringBuilder sb = new StringBuilder();
        @SuppressWarnings("resource")
        Formatter f = new Formatter(sb);
        f.format("%s.Trace:%s,Exception:%s.", level, trace.toString(),
                e.getMessage());
        if (null != values)
            f.format(format, values);
        if (!Validate.isNullOrEmptyOrAllSpace(KernelSetting.getAppName())) {
            return KernelSetting.getAppName() + " " + f.toString();
        }
        return f.toString();
    }

    protected String getMessage(String level, String format,
                                Object... values) {
        IStackTrace trace = RuningTrace.getTraceInfo();
        StringBuilder sb = new StringBuilder();
        @SuppressWarnings("resource")
        Formatter f = new Formatter(sb);
        f.format("%s.Trace:%s.", level, trace.toString());
        if (null != values)
            f.format(format, values);

        if (!Validate.isNullOrEmptyOrAllSpace(KernelSetting.getAppName())) {
            return KernelSetting.getAppName() + " " + f.toString();
        }
        return f.toString();
    }

}
