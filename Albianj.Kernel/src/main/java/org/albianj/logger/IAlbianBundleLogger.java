package org.albianj.logger;

public interface IAlbianBundleLogger {
    public  void addLog(String logName, AlbianLoggerLevel level, Throwable excp, String msg);
}
