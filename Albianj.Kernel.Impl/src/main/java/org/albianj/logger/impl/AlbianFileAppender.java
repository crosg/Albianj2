package org.albianj.logger.impl;

import org.albianj.datetime.AlbianDateTime;
import org.albianj.kernel.KernelSetting;
import org.apache.log4j.FileAppender;

import java.io.IOException;

/**
 * Created by xuhaifeng on 17/3/10.
 */
public class AlbianFileAppender extends FileAppender {
    protected String format = "yyyyMMddHHmmss";
    protected String suffix = "log";
    protected String prefix = "albianj";
    protected String path = "logs";

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String fileName) {
        this.prefix = fileName;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
            throws IOException {
        fileName = path;
        if (fileName.endsWith(KernelSetting.getPathSep())) {
            fileName += AlbianDateTime.getDateString() + KernelSetting.getPathSep();
        } else {
            fileName += KernelSetting.getPathSep() + AlbianDateTime.getDateString() + KernelSetting.getPathSep();
        }

        if (fileName.endsWith(this.suffix)) {
            fileName = fileName.substring(0, fileName.lastIndexOf(this.getPrefix()));
        }
        StringBuilder sbFileName = new StringBuilder();


        sbFileName.append(fileName).append("albianj_").append(this.getPrefix()).append("_")
                .append(AlbianDateTime.getTimeString()).append(".").append(this.suffix);


        super.setFile(sbFileName.toString(), append, this.bufferedIO, this.bufferSize);
    }
}
