package org.albianj.pooling;

public interface IReusableObjectPool {

    <T extends AutoCloseable> void returnPoolingObject(IPoolingObject<T> conn) throws Exception;

    public void destroy();

    public boolean isActive();

    public int getBusyCount();

    public int getFreeCount();

    public IPoolingConfig getConfig();

    void setConfig(IPoolingConfig config);

    public String getPoolName();

    public void setPoolName(String name);

    <T extends AutoCloseable> IPoolingObject<T> getPoolingObject(String sessionId) throws Exception;
}
