package org.albianj.aop;

public class AlbianServiceMethodRetryAttribute implements IAlbianServiceMethodRetryAttribute {
    private int retryTimes = 2;
    private long delayMs = 100;
    @Override
    public int getRetryTimes() {
        return retryTimes;
    }

    @Override
    public void setRetryTimes(int rt) {
        this.retryTimes = rt;
    }

    @Override
    public long getDelayMs() {
        return delayMs;
    }

    @Override
    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }
}
