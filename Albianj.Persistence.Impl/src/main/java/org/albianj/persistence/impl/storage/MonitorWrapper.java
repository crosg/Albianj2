package org.albianj.persistence.impl.storage;

import org.albianj.persistence.db.IDataBasePool;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.service.IConnectionMonitorService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * project : com.yuewen.nrzx.albianj
 *
 * @ccversion 新建 - liyuqi 2018-07-30 10:07</br>
 */
public class MonitorWrapper implements IDataBasePool {

    private IDataBasePool delegatePool;
    private IConnectionMonitorService connectionMonitorService;

    public MonitorWrapper(IDataBasePool delegatePool, IConnectionMonitorService connectionMonitorService) {
        this.delegatePool = delegatePool;
        this.connectionMonitorService = connectionMonitorService;
    }

    @Override
    public Connection getConnection(String sessionId, IRunningStorageAttribute rsa,boolean isAutoCommit) {
        long start = System.currentTimeMillis();
        Connection connection = null;
        try {
            connection = delegatePool.getConnection(sessionId, rsa,isAutoCommit);
            return connection;
        } finally {
            connectionMonitorService.monitorConnectionCost(rsa.getStorageAttribute().getName(), rsa.getDatabase(),
                    IConnectionMonitorService.MonitorMethod.GetConnection, System.currentTimeMillis() - start,
                    connection != null);
        }

    }

    @Override
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn,
                                 Statement pst, ResultSet rs) {
        long start = System.currentTimeMillis();
        try {
            delegatePool.returnConnection(sessionId, storageName, databaseName, conn, pst, rs);
        } finally {
            connectionMonitorService.monitorConnectionCost(storageName, databaseName,
                    IConnectionMonitorService.MonitorMethod.ReturnConnection, System.currentTimeMillis() - start,
                    true);
        }

    }

    @Override
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn,
                                 List<Statement> pst) {
        long start = System.currentTimeMillis();
        try {
            delegatePool.returnConnection(sessionId, storageName, databaseName, conn, pst);
        } finally {
            connectionMonitorService.monitorConnectionCost(storageName, databaseName,
                    IConnectionMonitorService.MonitorMethod.ReturnConnection, System.currentTimeMillis() - start,
                    true);
        }

    }

    @Override
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn) {
        long start = System.currentTimeMillis();
        try {
            delegatePool.returnConnection(sessionId, storageName, databaseName, conn);
        } finally {
            connectionMonitorService.monitorConnectionCost(storageName, databaseName,
                    IConnectionMonitorService.MonitorMethod.ReturnConnection, System.currentTimeMillis() - start,
                    true);
        }
    }
}
