package org.albianj.logger.impl;

import java.net.URL;
import java.util.Formatter;
import java.util.concurrent.ConcurrentHashMap;

import org.albianj.io.Path;
import org.albianj.kernel.KernelSetting;
import org.albianj.kernel.impl.AlbianLogicIdService;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.reflection.AlbianReflect;
import org.albianj.runtime.IStackTrace;
import org.albianj.runtime.RuningTrace;
import org.albianj.service.AlbianServiceException;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianService;
import org.albianj.verify.Validate;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Seapeak
 * 
 */
public class AlbianLoggerService extends FreeAlbianService implements
		IAlbianLoggerService {
	private static ConcurrentHashMap<String, Logger> loggers = null;

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
			// logger = LoggerFactory.getLogger(ALBIAN_LOGGER);
		} catch (Exception exc) {
			throw new AlbianServiceException(exc.getMessage(), exc.getCause());
		}
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
			T e, String format, Object... values) throws T {
		Logger logger = getLogger(loggerName);
		if (null == logger)
			return;
		if (logger.isErrorEnabled()) {
			logger.error(getErrorMsg(e, format, values));
			throw e;
		}

	}

	/* (non-Javadoc)
	 * @see org.albianj.logger.impl.qqqqqq#errorAndThrow(java.lang.String, java.lang.Class, java.lang.Exception, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T extends Exception> void errorAndThrow(String loggerName,
			Class<T> cls, Exception e, String eInfo, String format,
			Object... values) throws T {
		Logger logger = getLogger(loggerName);
		if (null == logger)
			return;
		if (logger.isErrorEnabled()) {
			String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
			String msg = getErrorMsg(e, format, values);
			String tMsg = String.format("%s | %s.", id, msg);
			logger.error(tMsg);
			Class[] clss = new Class[] { String.class };
			Object[] vars = new Object[] { "Error:" + id + "," + eInfo };
			T throwObject = null;
			try {
				throwObject = AlbianReflect.newInstance(cls, clss, vars);
			} catch (Exception e1) {

			}
			if (null != throwObject)
				throw throwObject;
		}
	}

	/* (non-Javadoc)
	 * @see org.albianj.logger.impl.qqqqqq#errorAndThrow(java.lang.String, java.lang.Class, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T extends Exception> void errorAndThrow(String loggerName,
			Class<T> cls, String eInfo, String format, Object... values)
			throws T {
		Logger logger = getLogger(loggerName);
		if (null == logger)
			return;
		if (logger.isErrorEnabled()) {
			String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
			String msg = getErrorMsg(format, values);
			String tMsg = String.format("%s | %s.", id, msg);
			logger.error(tMsg);
			Class[] clss = new Class[] { String.class };
			Object[] vars = new Object[] { "Error:" + id + "," + eInfo };
			T throwObject = null;
			try {
				throwObject = AlbianReflect.newInstance(cls, clss, vars);
			} catch (Exception e1) {

			}
			if (null != throwObject)
				throw throwObject;
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
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see org.albianj.logger.impl.qqqqqq#warnAndThrow(java.lang.String, java.lang.Class, java.lang.Exception, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T extends Exception> void warnAndThrow(String loggerName,
			Class<T> cls, Exception e, String eInfo, String format,
			Object... values) throws T {
		Logger logger = getLogger(loggerName);
		if (null == logger)
			return;
		if (logger.isWarnEnabled()) {
			String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
			String msg = getWarnMsg(e, format, values);
			String tMsg = String.format("%s | %s.", id, msg);
			logger.warn(tMsg);
			Class[] clss = new Class[] { String.class };
			Object[] vars = new Object[] { "Warn:" + id + "," + eInfo };
			T throwObject = null;
			try {
				throwObject = AlbianReflect.newInstance(cls, clss, vars);
			} catch (Exception e1) {

			}
			if (null != throwObject)
				throw throwObject;
		}
	}

	/* (non-Javadoc)
	 * @see org.albianj.logger.impl.qqqqqq#warnAndThrow(java.lang.String, java.lang.Class, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T extends Exception> void warnAndThrow(String loggerName,
			Class<T> cls, String eInfo, String format, Object... values)
			throws T {
		Logger logger = getLogger(loggerName);
		if (null == logger)
			return;
		if (logger.isWarnEnabled()) {
			String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
			String msg = getWarnMsg(format, values);
			String tMsg = String.format("%s | %s.", id, msg);
			logger.warn(tMsg);
			Class[] clss = new Class[] { String.class };
			Object[] vars = new Object[] { "Warn:" + id + "," + eInfo };
			T throwObject = null;
			try {
				throwObject = AlbianReflect.newInstance(cls, clss, vars);
			} catch (Exception e1) {

			}
			if (null != throwObject)
				throw throwObject;
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
			T e, String format, Object... values) throws T {
		Logger logger = getLogger(loggerName);
		if (null == logger)
			return;
		if (logger.isInfoEnabled()) {
			logger.info(getInfoMsg(e, format, values));
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see org.albianj.logger.impl.qqqqqq#infoAndThrow(java.lang.String, java.lang.Class, java.lang.Exception, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T extends Exception> void infoAndThrow(String loggerName,
			Class<T> cls, Exception e, String eInfo, String format,
			Object... values) throws T {
		Logger logger = getLogger(loggerName);
		if (null == logger)
			return;
		if (logger.isInfoEnabled()) {
			String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
			String msg = getInfoMsg(e, format, values);
			String tMsg = String.format("%s | %s.", id, msg);
			logger.info(tMsg);
			Class[] clss = new Class[] { String.class };
			Object[] vars = new Object[] { "Info:" + id + "," + eInfo };
			T throwObject = null;
			try {
				throwObject = AlbianReflect.newInstance(cls, clss, vars);
			} catch (Exception e1) {

			}
			if (null != throwObject)
				throw throwObject;
		}
	}

	/* (non-Javadoc)
	 * @see org.albianj.logger.impl.qqqqqq#infoAndThrow(java.lang.String, java.lang.Class, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T extends Exception> void infoAndThrow(String loggerName,
			Class<T> cls, String eInfo, String format, Object... values)
			throws T {
		Logger logger = getLogger(loggerName);
		if (null == logger)
			return;
		if (logger.isInfoEnabled()) {
			String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
			String msg = getInfoMsg(format, values);
			String tMsg = String.format("%s | %s.", id, msg);
			logger.info(tMsg);
			Class[] clss = new Class[] { String.class };
			Object[] vars = new Object[] { "EID" + id + "." + eInfo };
			T throwObject = null;
			try {
				throwObject = AlbianReflect.newInstance(cls, clss, vars);
			} catch (Exception e1) {

			}
			if (null != throwObject)
				throw throwObject;
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
			T e, String format, Object... values) throws T {
		Logger logger = getLogger(loggerName);
		if (null == logger)
			return;
		if (logger.isDebugEnabled()) {
			logger.debug(getDebugMsg(e, format, values));
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see org.albianj.logger.impl.qqqqqq#debugAndThrow(java.lang.String, java.lang.Class, java.lang.Exception, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T extends Exception> void debugAndThrow(String loggerName,
			Class<T> cls, Exception e, String eInfo, String format,
			Object... values) throws T {
		Logger logger = getLogger(loggerName);
		if (null == logger)
			return;
		if (logger.isDebugEnabled()) {
			String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
			String msg = getDebugMsg(e, format, values);
			String tMsg = String.format("%s | %s.", id, msg);
			logger.debug(tMsg);
			Class[] clss = new Class[] { String.class };
			Object[] vars = new Object[] { "Debug:" + id + "," + eInfo };
			T throwObject = null;
			try {
				throwObject = AlbianReflect.newInstance(cls, clss, vars);
			} catch (Exception e1) {

			}
			if (null != throwObject)
				throw throwObject;
		}
	}

	/* (non-Javadoc)
	 * @see org.albianj.logger.impl.qqqqqq#debugAndThrow(java.lang.String, java.lang.Class, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T extends Exception> void debugAndThrow(String loggerName,
			Class<T> cls, String eInfo, String format, Object... values)
			throws T {
		Logger logger = getLogger(loggerName);
		if (null == logger)
			return;
		if (logger.isDebugEnabled()) {
			String id = AlbianServiceRouter.getLogIdService().makeLoggerId();
			String msg = getDebugMsg(format, values);
			String tMsg = String.format("%s | %s.", id, msg);
			logger.debug(tMsg);
			Class[] clss = new Class[] { String.class };
			Object[] vars = new Object[] { "Debug" + id + "," + eInfo };
			T throwObject = null;
			try {
				throwObject = AlbianReflect.newInstance(cls, clss, vars);
			} catch (Exception e1) {

			}
			if (null != throwObject)
				throw throwObject;
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
		if(!Validate.isNullOrEmptyOrAllSpace(KernelSetting.getAppName())){
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
		
		if(!Validate.isNullOrEmptyOrAllSpace(KernelSetting.getAppName())){
			return KernelSetting.getAppName() + " " + f.toString();
		}
		return f.toString();
	}

}
