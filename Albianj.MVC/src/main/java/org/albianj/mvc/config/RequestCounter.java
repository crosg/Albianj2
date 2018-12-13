package org.albianj.mvc.config;

/**
 * Created by xuhaifeng on 17/1/19.
 */
public class RequestCounter {

    private long unitTime = 60;
    private long requestCount = 120;

    public long getUnitTime() {
        return this.unitTime;
    }

    public void setUnitTime(long unitTime) {
        this.unitTime = unitTime;
    }

    public long getRequestCount() {
        return this.requestCount;
    }

    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public synchronized void subCounter() {
        this.requestCount--;
    }
}

