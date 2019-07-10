package org.albianj.aop;

public class AlbianServiceMethodTimeoutAttribute implements IAlbianServiceMethodTimeoutAttribute {
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
