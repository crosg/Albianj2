package org.albianj.mvc.config;

/**
 * Created by xuhaifeng on 17/1/19.
 */
public class BrushingConfigurtion {
    /**
     * 单位时间，单位秒
     */
    private long unitTime = 60;
    /**
     * 单位时间内的访问次数
     */
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


}
