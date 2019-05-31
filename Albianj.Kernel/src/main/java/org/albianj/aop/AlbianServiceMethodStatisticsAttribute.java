package org.albianj.aop;

public class AlbianServiceMethodStatisticsAttribute implements IAlbianServiceMethodStatisticsAttribute {
    private boolean enable = true;
    private String logTagName ;

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String getLogTagName() {
        return logTagName;
    }

    @Override
    public void setLogTagName(String logTagName) {
        this.logTagName = logTagName;
    }
}
