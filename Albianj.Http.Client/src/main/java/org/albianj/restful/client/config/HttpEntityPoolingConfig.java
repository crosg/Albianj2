package org.albianj.restful.client.config;

import org.albianj.config.parser.ConfigField2NodeRant;
import org.albianj.pooling.IPoolingConfig;
import org.albianj.xml.IAlbianXml2ObjectSigning;

public class HttpEntityPoolingConfig implements IAlbianXml2ObjectSigning, IPoolingConfig {
    @ConfigField2NodeRant()
    private int minPoolSize = 10000;
    @ConfigField2NodeRant()
    private int maxPoolSize = 10000;
    @ConfigField2NodeRant()
    private long waitTimeWhenGetMs = 10;
    @ConfigField2NodeRant()
    private long lifeCycleTimeMs = 3600000;
    @ConfigField2NodeRant()
    private long waitInFreePoolMs = 300000;
    @ConfigField2NodeRant()
    private int maxRemedyObjectCount = 500;
    @ConfigField2NodeRant()
    private long cleanupTimestampMs = 30000;
    @ConfigField2NodeRant()
    private long maxRequestTimeMs = 30000;
    @ConfigField2NodeRant()
    private String name;

    public int getMinPoolingCount() {
        return minPoolSize;
    }

    public void setMinPoolingCount(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMaxPoolingCount() {
        return maxPoolSize;
    }

    public void setMaxPoolingCount(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public long getWaitTimeWhenGetMs() {
        return waitTimeWhenGetMs;
    }

    public void setWaitTimeWhenGetMs(long waitTimeWhenGetMs) {
        this.waitTimeWhenGetMs = waitTimeWhenGetMs;
    }

    public long getLifeCycleTimeMs() {
        return lifeCycleTimeMs;
    }

    public void setLifeCycleTimeMs(long lifeCycleTimeMs) {
        this.lifeCycleTimeMs = lifeCycleTimeMs;
    }

    public long getWaitInFreePoolMs() {
        return waitInFreePoolMs;
    }

    public void setWaitInFreePoolMs(long waitInFreePoolMs) {
        this.waitInFreePoolMs = waitInFreePoolMs;
    }

    public int getMaxRemedyObjectCount() {
        return maxRemedyObjectCount;
    }

    public void setMaxRemedyObjectCount(int maxRemedyObjectCount) {
        this.maxRemedyObjectCount = maxRemedyObjectCount;
    }

    public long getCleanupTimestampMs() {
        return cleanupTimestampMs;
    }

    public void setCleanupTimestampMs(long cleanupTimestampMs) {
        this.cleanupTimestampMs = cleanupTimestampMs;
    }

    @Override
    public String getPoolName() {
        return name;
    }

    @Override
    public void setPoolName(String poolName) {
        this.name = name;
    }

    public long getMaxRequestTimeMs() {
        return maxRequestTimeMs;
    }

    public void setMaxRequestTimeMs(long maxRequestTimeMs) {
        this.maxRequestTimeMs = maxRequestTimeMs;
    }
}
