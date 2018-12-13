package org.albianj.pooling;

/*
 对象池的接口对象
 对象池接管的对象必须是AutoCloseable的实现类
 */
public interface IPoolingObject<T extends AutoCloseable>{
    public long getStartupTimeMs();

    public void setStartupTimeMs(long startupTimeMs);

    public boolean isPooling();

    public void setPooling(boolean pooling);

    public long getLastUsedTimeMs();

    public void setLastUsedTimeMs(long lastUsedTimeMs);

    public long getReuseTimes();

    public void addReuseTimes();

    public Boolean isValid();

    String getSessionId();

    void setSessionId(String sessionId);

    /*
    获取被pooling阶段的包装对象
     */
    T getWrappedObject();

}
