package org.albianj.boot.logging.impl;

import org.albianj.boot.except.AlbianLocationInfo;
import org.albianj.boot.logging.AlbianLoggerLevel;
import org.albianj.boot.logging.IAlbianLogger;
import org.albianj.boot.logging.IAlbianLoggerAppender;

import java.util.ArrayList;
import java.util.List;

public class AlbianLogger implements IAlbianLogger {
    private List<IAlbianLoggerAppender> appenders = null;
    private AlbianLoggerLevel level = AlbianLoggerLevel.All;
    private boolean isConsoleAppenderOpened = false;

    public AlbianLogger(String logName, String path, String level, String maxFilesize,boolean isOpenConsole) {
        this();
        if (isOpenConsole) {
            this.openConsoleAppender();
        }
        this.openBufferAppender(logName, path, maxFilesize);
        this.setLoggerLevel(level);
    }

    public AlbianLogger(String logName,String path,String level,boolean isOpenConsole) {
        this();
        if (isOpenConsole) {
            this.openConsoleAppender();
        }
        this.openBufferAppender(logName, path, "10MB");
        this.setLoggerLevel(level);
    }

    private AlbianLogger() {
        appenders = new ArrayList<>();
    }

    @Override
    public void log(String sessionId, String bundleName, AlbianLoggerLevel level, Class<?> calledClzz, Throwable e, String breif,String secretMsg,String msg) {
        if(this.level.getLevel() > level.getLevel()) {
            return ;
        }

        AlbianLocationInfo localInfo = new AlbianLocationInfo(null == e ? new Throwable() : e, calledClzz);
        String logText = AlbianLayoutServant.Instance.makeLayoutBuffer(sessionId, bundleName, level, localInfo, breif,secretMsg, msg);
        for (IAlbianLoggerAppender appender : appenders) {
            appender.write(logText);
        }
    }

    @Override
    public void openBufferAppender(String logName, String path, String maxFilesize) {
        appenders.add(new AlbianBufferAppender(logName, path, maxFilesize));
    }

    @Override
    public void openConsoleAppender() {
        appenders.add(new AlbianConsoleAppender());
        isConsoleAppenderOpened = true;
    }

    @Override
    public void closeConsoleAppender() {
        List<IAlbianLoggerAppender> consoleAppenders = new ArrayList<>();
        for (IAlbianLoggerAppender appender : appenders) {
            if (appender instanceof AlbianConsoleAppender) {
                consoleAppenders.add(appender);
            }
        }

        for (IAlbianLoggerAppender appender : consoleAppenders) {
            appenders.remove(appender);
        }
        isConsoleAppenderOpened = false;
    }

    public boolean isConsoleAppenderOpened(){
        return this.isConsoleAppenderOpened;
    }

    public void setLoggerLevel(String level){
        this.level = AlbianLoggerLevel.toLevel(level);
    }
    public void setMaxFilesize(String maxFilesize){
        for (IAlbianLoggerAppender appender : appenders) {
            appender.setMaxFilesize(maxFilesize);
        }
    }

}
