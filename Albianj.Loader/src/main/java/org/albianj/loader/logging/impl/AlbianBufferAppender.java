package org.albianj.loader.logging.impl;

import org.albianj.loader.helpers.AlbianDailyServant;
import org.albianj.loader.helpers.AlbianOptConvertServant;
import org.albianj.loader.helpers.AlbianStringServant;
import org.albianj.loader.logging.AlbianLoggerLevel;
import org.albianj.loader.logging.IAlbianLoggerAppender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

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
    }

    public String getMaxFilesize() {
        return sMaxFilesize;
    }

    public void setMaxFilesize(String sMaxFilesize) {
        this.sMaxFilesize = sMaxFilesize;
    }
}
