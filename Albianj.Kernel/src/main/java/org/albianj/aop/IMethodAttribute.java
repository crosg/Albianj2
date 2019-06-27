package org.albianj.aop;

public interface IMethodAttribute {
    public boolean isIgnore();
    public void setIgnore(boolean ignore);

    public IMethodRetryAttribute getRetryAttribute();
    public void setRetryAttribute(IMethodRetryAttribute mra);

    IMethodMonitorAttribute getStatisticsAttribute();

    void setStatisticsAttribute(IMethodMonitorAttribute mtr);

    IMethodTimeoutAttribute getTimeoutAttribute();

    void setTimeoutAttribute(IMethodTimeoutAttribute toa);
}
