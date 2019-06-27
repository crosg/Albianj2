package org.albianj.boot.logging.impl;

import org.albianj.boot.except.LocationInfo;
import org.albianj.boot.logging.LoggerLevel;
import org.albianj.boot.logging.ILogger;
import org.albianj.boot.logging.ILoggerAppender;
import org.albianj.boot.tags.BundleSharingTag;

import java.util.ArrayList;
import java.util.List;

@BundleSharingTag
public class Logger implements ILogger {
    private List<ILoggerAppender> appenders = null;
    private LoggerLevel level = LoggerLevel.All;
    private boolean isConsoleAppenderOpened = false;

    public Logger(String logName, String path, String level, String maxFilesize, boolean isOpenConsole) {
        this();
        if (isOpenConsole) {
            this.openConsoleAppender();
        }
        this.openBufferAppender(logName, path, maxFilesize);
        this.setLoggerLevel(level);
    }

    public Logger(String logName, String path, String level, boolean isOpenConsole) {
        this();
        if (isOpenConsole) {
            this.openConsoleAppender();
        }
        this.openBufferAppender(logName, path, "10MB");
        this.setLoggerLevel(level);
    }

    private Logger() {
        appenders = new ArrayList<>();
    }

    @Override
    public void log(String sessionId, String bundleName, LoggerLevel level, Class<?> currType, Throwable e, String breif, String secretMsg, String msg) {
        if(this.level.getLevel() > level.getLevel()) {
            return ;
        }

        LocationInfo localInfo = new LocationInfo(null == e ? new Throwable() : e, currType);
        String logText = LayoutServant.Instance.makeLayoutBuffer(sessionId, bundleName, level, localInfo, breif,secretMsg, msg);
        for (ILoggerAppender appender : appenders) {
            appender.write(logText);
        }
    }

    @Override
    public void openBufferAppender(String logName, String path, String maxFilesize) {
        appenders.add(new BufferAppender(logName, path, maxFilesize));
    }

    @Override
    public void openConsoleAppender() {
        appenders.add(new ConsoleAppender());
        isConsoleAppenderOpened = true;
    }

    @Override
    public void closeConsoleAppender() {
        List<ILoggerAppender> consoleAppenders = new ArrayList<>();
        for (ILoggerAppender appender : appenders) {
            if (appender instanceof ConsoleAppender) {
                consoleAppenders.add(appender);
            }
        }

        for (ILoggerAppender appender : consoleAppenders) {
            appenders.remove(appender);
        }
        isConsoleAppenderOpened = false;
    }

    public boolean isConsoleAppenderOpened(){
        return this.isConsoleAppenderOpened;
    }

    public void setLoggerLevel(String level){
        this.level = LoggerLevel.toLevel(level);
    }
    public void setMaxFilesize(String maxFilesize){
        for (ILoggerAppender appender : appenders) {
            appender.setMaxFilesize(maxFilesize);
        }
    }

}
