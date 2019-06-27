package org.albianj.aop.impl;

import org.albianj.aop.IMethodRetryAttribute;

public class MethodRetryAttribute implements IMethodRetryAttribute {
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
