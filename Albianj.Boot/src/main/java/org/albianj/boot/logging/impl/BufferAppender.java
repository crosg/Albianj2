package org.albianj.boot.logging.impl;

import org.albianj.boot.logging.ILoggerAppender;
import org.albianj.boot.tags.BundleSharingTag;

@BundleSharingTag
public class BufferAppender implements ILoggerAppender {

    private String logName;
    private String path;
    private String sMaxFilesize = "10MB";
    private LoggerFile logFile;

    public BufferAppender(String logName, String path, String maxFilesize) {
        this.logName = logName;
        this.path = path;
        this.sMaxFilesize = maxFilesize;
        logFile = new LoggerFile(logName,path,maxFilesize);
    }

    @Override
    public void write(String src) {
        do {
            if (!logFile.write(src)) {
                logFile.close();
                logFile = new LoggerFile(this.logName,this.path,this.sMaxFilesize);
                continue;
            }
        }while(false);
    }

    @Override
    public void flush() {
        logFile.flushWithMutex();
    }

    @Override
    public void close() {
          logFile.close();
          logFile  = null;
    }

    public String getMaxFilesize() {
        return sMaxFilesize;
    }

    public void setMaxFilesize(String sMaxFilesize) {
        this.sMaxFilesize = sMaxFilesize;
    }
}
