package org.albianj.boot.logging;

public interface IAlbianLogger {

    void log(String sessionId, String bundleName, AlbianLoggerLevel level,  Class<?> calledClzz, Throwable e, String breif,String secretMsg,String msg);

    void openBufferAppender(String logName, String path, String maxFilesize);

    void openConsoleAppender();

    void closeConsoleAppender();

    boolean isConsoleAppenderOpened();

    void setLoggerLevel(String level);

    void setMaxFilesize(String maxFilesize);

}
