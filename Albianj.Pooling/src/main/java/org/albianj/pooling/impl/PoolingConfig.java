package org.albianj.pooling.impl;

import org.albianj.pooling.IPoolingConfig;

public class PoolingConfig implements IPoolingConfig {

    private String poolName;
    private int maxPoolingCount = 10;
    private int minPoolingCount = 5;
    private long waitTimeWhenGetMs = 200;

    //整个生命的时间，最长的生存时间，到了时间
    //不管链接是否可用，全部kill掉
    //默认一小时
    private long lifeTimeMs = 60 * 60 * 1000;


    //connection free time
    //in free pool max time
    //mysql的wait time设置值，目前公司内部的默认为3分钟
    //即180s，故程序设置150s，2'30
    private long freeTimeMs = 150 * 1000;

    //max request time for one data operator
    private long maxRequestTimeMs = 30 * 1000;

    //最大补救链接的数量
    //当内存池内的链接全部被使用，或者链接池发生泄漏的时候，
    //补救措施是直接生成一个新的链接，先供业务使用
    //该链接使用完毕后不会被放入链接池，直接close掉
    private int maxRemedyObjectCount = 50;

    //timestamp for startup cleanup connection;
    private long cleanupTimestampMs = 30 * 1000;


    public long getCleanupTimestampMs() {
        return cleanupTimestampMs;
    }

    public void setCleanupTimestampMs(long cleanupTimestampMs) {
        this.cleanupTimestampMs = cleanupTimestampMs;
    }


    @Override
    public String getPoolName() {
        return poolName;
    }

    @Override
    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public int getMaxPoolingCount() {
        return maxPoolingCount;
    }

    @Override
    public void setMaxPoolingCount(int maxPoolingCount) {
        this.maxPoolingCount = maxPoolingCount;
    }

    @Override
    public int getMinPoolingCount() {
        return minPoolingCount;
    }

    @Override
    public void setMinPoolingCount(int minPoolingCount) {
        this.minPoolingCount = minPoolingCount;
    }

    @Override
    public long getWaitTimeWhenGetMs() {
        return waitTimeWhenGetMs;
    }

    @Override
    public void setWaitTimeWhenGetMs(long waitTimeWhenGetMs) {
        this.waitTimeWhenGetMs = waitTimeWhenGetMs;
    }

    @Override
    public long getLifeCycleTimeMs() {
        return lifeTimeMs;
    }

    @Override
    public void setLifeCycleTimeMs(long lifeTimeMs) {
        this.lifeTimeMs = lifeTimeMs;
    }

    @Override
    public long getWaitInFreePoolMs() {
        return freeTimeMs;
    }

    @Override
    public void setWaitInFreePoolMs(long freeTimeMs) {
        this.freeTimeMs = freeTimeMs;
    }

    @Override
    public int getMaxRemedyObjectCount() {
        return maxRemedyObjectCount;
    }

    @Override
    public void setMaxRemedyObjectCount(int maxRemedyObjectCount) {
        this.maxRemedyObjectCount = maxRemedyObjectCount;
    }

    public long getMaxRequestTimeMs() {
        return this.maxRequestTimeMs;
    }

    public void setMaxRequestTimeMs(long maxRequestTimeMs) {
        this.maxRequestTimeMs = maxRequestTimeMs;
    }


}
