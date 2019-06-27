package org.albianj.aop.impl;

import org.albianj.aop.IMethodAttribute;
import org.albianj.aop.IMethodMonitorAttribute;
import org.albianj.aop.IMethodRetryAttribute;
import org.albianj.aop.IMethodTimeoutAttribute;

public class MethodAttribute implements IMethodAttribute {
    private boolean ignore = true;
    private IMethodRetryAttribute mra;
    private IMethodMonitorAttribute mtr;
    private IMethodTimeoutAttribute toa;
    @Override
    public boolean isIgnore() {
        return ignore;
    }

    @Override
    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    @Override
    public IMethodRetryAttribute getRetryAttribute() {
        return mra;
    }

    @Override
    public void setRetryAttribute(IMethodRetryAttribute mra) {
        this.mra = mra;
    }

    @Override
    public IMethodMonitorAttribute getStatisticsAttribute() {
        return mtr;
    }

    @Override
    public void setStatisticsAttribute(IMethodMonitorAttribute mtr) {
        this.mtr = mtr;
    }

    @Override
    public IMethodTimeoutAttribute getTimeoutAttribute() {
        return toa;
    }

    @Override
    public void setTimeoutAttribute(IMethodTimeoutAttribute toa) {
        this.toa = toa;
    }
}
