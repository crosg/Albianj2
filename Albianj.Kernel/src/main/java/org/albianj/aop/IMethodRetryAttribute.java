package org.albianj.aop;

public interface IMethodRetryAttribute {
    public int getRetryTimes();
    public void setRetryTimes(int rt);

    public long getDelayMs();
    public void setDelayMs(long delayMs);
}
