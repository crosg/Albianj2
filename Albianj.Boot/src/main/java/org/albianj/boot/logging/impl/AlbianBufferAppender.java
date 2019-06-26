package org.albianj.boot.logging.impl;

import org.albianj.boot.logging.IAlbianLoggerAppender;

public class AlbianBufferAppender implements IAlbianLoggerAppender {

    private String logName;
    private String path;
    private String sMaxFilesize = "10MB";
    private AlbianLoggerFile logFile;

    public AlbianBufferAppender(String logName, String path, String maxFilesize) {
        this.logName = logName;
        this.path = path;
        this.sMaxFilesize = maxFilesize;
        logFile = new AlbianLoggerFile(logName,path,maxFilesize);
    }

    @Override
    public void write(String src) {
        do {
            if (!logFile.write(src)) {
                logFile.close();
                logFile = new AlbianLoggerFile(this.logName,this.path,this.sMaxFilesize);
                continue;
            }
        }while(false);
    }

    @Override
    public void flush() {
        logFile.flush();
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
