package org.albianj.aop;

public interface IAlbianServiceMethodAttribute {
    public boolean isIgnore();
    public void setIgnore(boolean ignore);

    public IAlbianServiceMethodRetryAttribute  getRetryAttribute();
    public void setRetryAttribute(IAlbianServiceMethodRetryAttribute mra);

    IAlbianServiceMethodStatisticsAttribute getStatisticsAttribute();

    void setStatisticsAttribute(IAlbianServiceMethodStatisticsAttribute mtr);

    IAlbianServiceMethodTimeoutAttribute getTimeoutAttribute();

    void setTimeoutAttribute(IAlbianServiceMethodTimeoutAttribute toa);
}
