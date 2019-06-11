package org.albianj.logger.impl.bundle;

import org.albianj.datetime.AlbianDateTime;
import org.apache.log4j.RollingFileAppender;

import java.io.File;
import java.io.IOException;

public class AlbianBundleLoggerAppender extends RollingFileAppender {
    private String appName = "AlbianApps";
    private String path;
    private String loggerName;

    public AlbianBundleLoggerAppender(String path, String appName, String loggerName, String maxFileSize, int backupIndex) {
        super();
        if(!path.endsWith(File.separator)) {
            this.path = path + File.separator;
        }
        this.loggerName = loggerName;
        this.appName = appName;
        this.setMaxFileSize(maxFileSize);
        if(backupIndex > 0) {
            this.setMaxBackupIndex(backupIndex);
        }
    }

    public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
            throws IOException {
        // file path format : path/yyyy-mm-dd/appname-loggername-hhmmss.addLog

        StringBuilder filePath = new StringBuilder(this.path);

        filePath.append(AlbianDateTime.getDateString()).append(File.separator)
                .append(appName).append("-").append(loggerName).append("-")
                .append(AlbianDateTime.getTimeString()).append(".addLog");

        super.setFile(filePath.toString(), append, this.bufferedIO, this.bufferSize);
    }
}
