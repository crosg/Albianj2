package org.albianj.persistence.impl.dbpool;

import java.sql.Connection;
import java.sql.SQLException;

public interface IPoolingConnection extends Connection {

    public long getStartupTimeMs();

    public void setStartupTimeMs(long startupTimeMs);

    public boolean isPooling();

    public void setPooling(boolean pooling);

    public long getLastUsedTimeMs();

    public void setLastUsedTimeMs(long lastUsedTimeMs);

    public long getReuseTimes();

    public void addReuseTimes();

    String getServer();

    void setServer(String server);

    int getPort();

    void setPort(int port);

    public Boolean isValid() throws SQLException;

    String getSessionId();

    void setSessionId(String sessionId);


    String getId();
}
