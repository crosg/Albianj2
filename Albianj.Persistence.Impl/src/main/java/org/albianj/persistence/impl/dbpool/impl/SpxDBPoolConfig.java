package org.albianj.persistence.impl.dbpool.impl;

import org.albianj.persistence.impl.dbpool.ISpxDBPoolConfig;

public class SpxDBPoolConfig implements ISpxDBPoolConfig {

    private String poolName;
    private String driverName;
    private String url;
    private String username;
    private String password;
    private int maxConnections = 10;
    private int minConnections = 5;
    private int waitTimeWhenGetMs = 200;

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
    private int maxRemedyConnectionCount = 50;

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
    public String getDriverName() {
        return driverName;
    }

    @Override
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword(){
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int getMaxConnections() {
        return maxConnections;
    }

    @Override
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    @Override
    public int getMinConnections() {
        return minConnections;
    }

    @Override
    public void setMinConnections(int minConnections) {
        this.minConnections = minConnections;
    }

    @Override
    public int getWaitTimeWhenGetMs() {
        return waitTimeWhenGetMs;
    }

    @Override
    public void setWaitTimeWhenGetMs(int waitTimeWhenGetMs) {
        this.waitTimeWhenGetMs = waitTimeWhenGetMs;
    }

    @Override
    public long getLifeTimeMs() {
        return lifeTimeMs;
    }

    @Override
    public void setLifeTimeMs(long lifeTimeMs) {
        this.lifeTimeMs = lifeTimeMs;
    }

    @Override
    public long getFreeTimeMs() {
        return freeTimeMs;
    }

    @Override
    public void setFreeTimeMs(long freeTimeMs) {
        this.freeTimeMs = freeTimeMs;
    }

    @Override
    public int getMaxRemedyConnectionCount() {
        return maxRemedyConnectionCount;
    }

    @Override
    public void setMaxRemedyConnectionCount(int maxRemedyConnectionCount) {
        this.maxRemedyConnectionCount = maxRemedyConnectionCount;
    }

    public void setMaxRequestTimeMs(long maxRequestTimeMs){
        this.maxRequestTimeMs = maxRequestTimeMs;
    }

    public long getMaxRequestTimeMs(){
        return this.maxRequestTimeMs;
    }


}
