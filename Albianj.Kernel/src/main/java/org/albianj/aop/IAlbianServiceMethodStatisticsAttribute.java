package org.albianj.aop;

public interface IAlbianServiceMethodStatisticsAttribute {
    boolean isEnable();

    void setEnable(boolean enable);

    String getLogTagName();

    void setLogTagName(String logTagName);
}
