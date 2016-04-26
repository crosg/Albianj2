package org.albianj.logger;

import org.albianj.service.AlbianServiceException;
import org.albianj.service.IAlbianService;
import org.slf4j.Logger;

public interface IAlbianLoggerService extends IAlbianService {
	static String Name = "AlbianLoggerService";
	
	static String AlbianSqlLoggerName = "AlbianSqlLogger";
	static String AlbianRunningLoggerName = "AlbianRunningLogger";
	
	String MARK_ERROR = "!";
	String MARK_WARN = "@";
	String MARK_INFO = "$";
	String MARK_DEBUG = "*";

	void loading() throws AlbianServiceException;

	Logger getLogger(String name);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.albianj.logger.IAlbianLoggerService#error(java.lang.String)
	 */
	void error(String loggerName, String format, Object... values);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.albianj.logger.IAlbianLoggerService#warn(java.lang.String)
	 */
	void warn(String loggerName, String format, Object... values);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.albianj.logger.IAlbianLoggerService#info(java.lang.String)
	 */
	void info(String loggerName, String format, Object... values);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.albianj.logger.IAlbianLoggerService#debug(java.lang.String)
	 */
	void debug(String loggerName, String format, Object... values);

	void error(String loggerName, Exception e, String format, Object... values);

	<T extends Exception> void errorAndThrow(String loggerName, T e, String format, Object... values) throws T;

	<T extends Exception> void errorAndThrow(String loggerName, Class<T> cls, Exception e, String eInfo, String format,
			Object... values) throws T;

	<T extends Exception> void errorAndThrow(String loggerName, Class<T> cls, String eInfo, String format,
			Object... values) throws T;

	void warn(String loggerName, Exception e, String format, Object... values);

	<T extends Exception> void warnAndThrow(String loggerName, T e, String format, Object... values) throws T;

	<T extends Exception> void warnAndThrow(String loggerName, Class<T> cls, Exception e, String eInfo, String format,
			Object... values) throws T;

	<T extends Exception> void warnAndThrow(String loggerName, Class<T> cls, String eInfo, String format,
			Object... values) throws T;

	void info(String loggerName, Exception e, String format, Object... values);

	<T extends Exception> void infoAndThrow(String loggerName, T e, String format, Object... values) throws T;

	<T extends Exception> void infoAndThrow(String loggerName, Class<T> cls, Exception e, String eInfo, String format,
			Object... values) throws T;

	<T extends Exception> void infoAndThrow(String loggerName, Class<T> cls, String eInfo, String format,
			Object... values) throws T;

	void debug(String loggerName, Exception e, String format, Object... values);

	<T extends Exception> void debugAndThrow(String loggerName, T e, String format, Object... values) throws T;

	<T extends Exception> void debugAndThrow(String loggerName, Class<T> cls, Exception e, String eInfo, String format,
			Object... values) throws T;

	<T extends Exception> void debugAndThrow(String loggerName, Class<T> cls, String eInfo, String format,
			Object... values) throws T;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.albianj.logger.IAlbianLoggerService#getErrorMsg(java.lang.String)
	 */
	String getErrorMsg(String format, Object... values);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.albianj.logger.IAlbianLoggerService#getWarnMsg(java.lang.String)
	 */
	String getWarnMsg(String format, Object... values);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.albianj.logger.IAlbianLoggerService#getInfoMsg(java.lang.String)
	 */
	String getInfoMsg(String format, Object... values);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.albianj.logger.IAlbianLoggerService#getDebugMsg(java.lang.String)
	 */
	String getDebugMsg(String format, Object... values);

	String getErrorMsg(Exception e, String format, Object... values);

	String getWarnMsg(Exception e, String format, Object... values);

	String getInfoMsg(Exception e, String format, Object... values);

	String getDebugMsg(Exception e, String format, Object... values);

}