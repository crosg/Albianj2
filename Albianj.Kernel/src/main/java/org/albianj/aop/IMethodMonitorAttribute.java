package org.albianj.aop;

public interface IMethodMonitorAttribute {
    boolean isEnable();

    void setEnable(boolean enable);

    String getLogTagName();

    void setLogTagName(String logTagName);
}
