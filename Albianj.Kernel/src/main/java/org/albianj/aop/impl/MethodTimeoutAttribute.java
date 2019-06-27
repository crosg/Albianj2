package org.albianj.aop.impl;

import org.albianj.aop.IMethodTimeoutAttribute;

public class MethodTimeoutAttribute implements IMethodTimeoutAttribute {
    private long timetampMs = 100;

    @Override
    public long getTimetampMs() {
        return timetampMs;
    }

    @Override
    public void setTimetampMs(long timetampMs) {
        this.timetampMs = timetampMs;
    }
}
