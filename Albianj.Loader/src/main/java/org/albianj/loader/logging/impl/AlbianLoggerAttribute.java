package org.albianj.loader.logging.impl;

import org.albianj.loader.logging.IAlbianLoggerAttribute;

public class AlbianLoggerAttribute  implements IAlbianLoggerAttribute {

    private String loggerName = null;
    private String path = null;
    private String level = "INFO";
    private boolean openConsole = false;
    private String maxFilesize = "10MB";

    public AlbianLoggerAttribute(String loggerName,String path,String level,boolean isOpenConsole,String maxFilesize){
        this.loggerName = loggerName;
        this.path = path;
        this.level = level;
        this.openConsole = isOpenConsole;
        this.maxFilesize = maxFilesize;
    }
    @Override
    public String getLoggerName() {
        return this.loggerName;
    }

    @Override
    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getLevel() {
        return this.level;
    }

    @Override
    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public boolean isOpenConsole() {
        return this.openConsole;
    }

    @Override
    public void setOpenConsole(boolean openConsole) {
        this.openConsole = openConsole;
    }

    @Override
    public String getMaxFilesize() {
        return this.maxFilesize;
    }

    @Override
    public void setMaxFilesize(String maxFilesize) {
        this.maxFilesize = maxFilesize;
    }
}
