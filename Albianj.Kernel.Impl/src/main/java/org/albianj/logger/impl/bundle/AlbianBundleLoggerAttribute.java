package org.albianj.logger.impl.bundle;

import org.albianj.loader.entry.IAlbianBundleLoggerAttribute;

public class AlbianBundleLoggerAttribute implements IAlbianBundleLoggerAttribute {
    private String loggerName;
    private String path;
    private String level = "INFO";
    private boolean isOpenConsole = false;
    private String maxFilesize = "10MB";

    @Override
    public String getLoggerName() {
        return loggerName;
    }

    @Override
    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getLevel() {
        return level;
    }

    @Override
    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public boolean isOpenConsole() {
        return isOpenConsole;
    }

    @Override
    public void setOpenConsole(boolean openConsole) {
        isOpenConsole = openConsole;
    }

    @Override
    public String getMaxFilesize() {
        return maxFilesize;
    }

    @Override
    public void setMaxFilesize(String maxFilesize) {
        this.maxFilesize = maxFilesize;
    }
}
