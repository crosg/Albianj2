package org.albianj.pooling;

public interface IPoolingConfig {

    public long getCleanupTimestampMs();

    public void setCleanupTimestampMs(long cleanupTimestampMs);

    public String getPoolName();

    public void setPoolName(String poolName);

    public int getMaxPoolingCount();

    public void setMaxPoolingCount(int maxPoolingCount);

    public int getMinPoolingCount();

    public void setMinPoolingCount(int minPoolingCount) ;

    public long getWaitTimeWhenGetMs();

    public void setWaitTimeWhenGetMs(long waitTimeWhenGetMs);

    public long getLifeCycleTimeMs();

    public void setLifeCycleTimeMs(long lifeTimeMs);

    public long getWaitInFreePoolMs();

    public void setWaitInFreePoolMs(long freeTimeMs);

    public int getMaxRemedyObjectCount();

    public void setMaxRemedyObjectCount(int maxRemedyObjectCount) ;

    public long getMaxRequestTimeMs();

    public void setMaxRequestTimeMs(long maxRequestTimeMs);

}
