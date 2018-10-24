package org.albianj.persistence.impl.dbpool.impl;

import org.albianj.persistence.impl.dbpool.IPoolingConnection;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class PoolingConnection  implements IPoolingConnection {
    Connection _conn;
    //startup timestamp
    long startupTimeMs;
    String sessionId;

//     from pool but not return to pool
    // use to bc
    boolean isPooling;
    long lastUsedTimeMs;
    // reuse times in lifecycle
    long reuseTimes = 0;

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

    public PoolingConnection(Connection conn,long startupTimeMs,boolean isPooling){
        _conn = conn;
        this.startupTimeMs = startupTimeMs;
        this.isPooling = isPooling;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return _conn.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return _conn.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return _conn.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return _conn.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        _conn.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return _conn.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        _conn.commit();
    }

    @Override
    public void rollback() throws SQLException {
        _conn.rollback();
    }

    @Override
    public void close() throws SQLException {
        _conn.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return _conn.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return _conn.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        _conn.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return _conn.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        _conn.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return _conn.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        _conn.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return _conn.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return _conn.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        _conn.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return _conn.createStatement(resultSetType,resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return _conn.prepareStatement(sql,resultSetType,resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return _conn.prepareCall(sql,resultSetType,resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return _conn.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        _conn.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        _conn.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return _conn.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return _conn.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return _conn.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        _conn.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        _conn.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return _conn.createStatement(resultSetType,resultSetConcurrency,resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return _conn.prepareStatement(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return _conn.prepareCall(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return _conn.prepareStatement(sql,autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return _conn.prepareStatement(sql,columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return _conn.prepareStatement(sql,columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return _conn.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return _conn.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return _conn.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return _conn.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return _conn.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        _conn.setClientInfo(name,value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        _conn.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return _conn.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return _conn.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return _conn.createArrayOf(typeName,elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return _conn.createStruct(typeName,attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        _conn.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return _conn.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        _conn.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        _conn.setNetworkTimeout(executor,milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return _conn.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return _conn.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return _conn.isWrapperFor(iface);
    }

    public Boolean isValid() throws SQLException {
       return !this.isClosed();
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return this.sessionId;
    }
}
