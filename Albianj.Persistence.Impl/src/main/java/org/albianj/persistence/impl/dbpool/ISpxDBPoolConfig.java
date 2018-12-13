package org.albianj.persistence.impl.dbpool;

public interface ISpxDBPoolConfig {
    String getPoolName();

    void setPoolName(String poolName);

    String getDriverName();

    void setDriverName(String driverName);

    String getUrl();

    void setUrl(String url);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    int getMaxConnections();

    void setMaxConnections(int maxConnections);

    int getMinConnections();

    void setMinConnections(int minConnections);

    int getWaitTimeWhenGetMs();

    void setWaitTimeWhenGetMs(int waitTimeWhenGetMs);

    long getLifeCycleTime();

    void setLifeCycleTime(long lifeTimeMs);

    long getWaitInFreePoolMs();

    void setWaitInFreePoolMs(long freeTimeMs);

    int getMaxRemedyConnectionCount();

    void setMaxRemedyConnectionCount(int maxRemedyConnectionCount);

    public long getCleanupTimestampMs();

    public void setCleanupTimestampMs(long cleanupTimestampMs);

    public long getMaxRequestTimeMs();

    public void setMaxRequestTimeMs(long maxRequestTimeMs);
}
