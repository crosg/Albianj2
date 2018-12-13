package org.albianj.pooling.impl;


import org.albianj.pooling.IPoolingObject;

/*
 池化对象的托管类
 */
public class PoolingObject<T extends AutoCloseable> implements IPoolingObject {
    T _obj;
    //startup timestamp
    long startupTimeMs;
    String sessionId;

    //     from pool but not return to pool
    // use to bc
    boolean isPooling;
    long lastUsedTimeMs;
    // reuse times in lifecycle
    long reuseTimes = 0;
    boolean isClosed = false;

    public PoolingObject(T obj, long startupTimeMs, boolean isPooling) {
        _obj = obj;
        this.startupTimeMs = startupTimeMs;
        this.isPooling = isPooling;
    }

    public long getStartupTimeMs() {
        return startupTimeMs;
    }

    public void setStartupTimeMs(long startupTimeMs) {
        this.startupTimeMs = startupTimeMs;
    }

    public boolean isPooling() {
        return isPooling;
    }

    public void setPooling(boolean pooling) {
        isPooling = pooling;
    }

    public long getLastUsedTimeMs() {
        return lastUsedTimeMs;
    }

    public void setLastUsedTimeMs(long lastUsedTimeMs) {
        this.lastUsedTimeMs = lastUsedTimeMs;
    }

    public long getReuseTimes() {
        return reuseTimes;
    }

    public void addReuseTimes() {
        ++this.reuseTimes;
    }

    public Boolean isValid() {
        return !this.isClosed;
    }

    @Override
    public String getSessionId() {
        return this.sessionId;
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public T getWrappedObject() {
        return _obj;
    }
}
