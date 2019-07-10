package org.albianj.aop;

public class AlbianServiceMethodAttribute implements  IAlbianServiceMethodAttribute{
    private boolean ignore = true;
    private IAlbianServiceMethodRetryAttribute mra;
    private IAlbianServiceMethodStatisticsAttribute mtr;
    private IAlbianServiceMethodTimeoutAttribute toa;
    @Override
    public boolean isIgnore() {
        return ignore;
    }

    @Override
    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    @Override
    public IAlbianServiceMethodRetryAttribute getRetryAttribute() {
        return mra;
    }

    @Override
    public void setRetryAttribute(IAlbianServiceMethodRetryAttribute mra) {
        this.mra = mra;
    }

    @Override
    public IAlbianServiceMethodStatisticsAttribute getStatisticsAttribute() {
        return mtr;
    }

    @Override
    public void setStatisticsAttribute(IAlbianServiceMethodStatisticsAttribute mtr) {
        this.mtr = mtr;
    }

    @Override
    public IAlbianServiceMethodTimeoutAttribute getTimeoutAttribute() {
        return toa;
    }

    @Override
    public void setTimeoutAttribute(IAlbianServiceMethodTimeoutAttribute toa) {
        this.toa = toa;
    }
}
