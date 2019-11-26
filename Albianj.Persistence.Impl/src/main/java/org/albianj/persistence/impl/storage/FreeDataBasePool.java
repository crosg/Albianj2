package org.albianj.persistence.impl.storage;

import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.db.IDataBasePool;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.service.AlbianServiceRouter;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xuhaifeng on 2017/11/10.
 */
public abstract class FreeDataBasePool implements IDataBasePool {

    private final ConcurrentMap<String, DataSource> _dataSource = new ConcurrentHashMap<>();

    protected DataSource getDatasource(final String key, IRunningStorageAttribute rsa) {
        DataSource ds = _dataSource.get(key);
        if (ds != null) {
            return ds;
        }

        synchronized (_dataSource) {
            ds = _dataSource.get(key);
            if (ds == null) {
                AlbianServiceRouter.getLogger2()
                        .log(IAlbianLoggerService2.AlbianSqlLoggerName, IAlbianLoggerService2.InnerThreadName,
                                AlbianLoggerLevel.Info, "create datasource storage:%s ,database:%s",
                                rsa.getStorageAttribute().getName(), rsa.getDatabase());
                ds = setupDataSource(key, rsa);
                _dataSource.putIfAbsent(key, ds);
            }
        }
        return ds;
    }

    protected DataSource getDatasource(String key) {
        DataSource ds = _dataSource.get(key);
        if (ds != null) {
            return ds;
        }

        synchronized (_dataSource) {
            ds = _dataSource.get(key);
        }
        return ds;
    }


    protected abstract DataSource setupDataSource(final String key, final IRunningStorageAttribute rsa);

    //释放连接回连接池
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn, Statement pst, ResultSet rs) {

        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                sessionId, AlbianLoggerLevel.Info,
                "return the connection from storage:%s and database:%s by connection pool.",
                storageName, databaseName);

        try {
            if (rs != null) {
                rs.close();
            }

        } catch (SQLException e) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error, e,
                    "close the result by connection to storage:%s database:%s is fail.",
                    storageName, databaseName);
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }

            } catch (SQLException e) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error, e,
                        "close the statement by connection to storage:%s database:%s is fail.",
                        storageName, databaseName);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }

                } catch (SQLException e) {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Error, e,
                            "close the  connection to storage:%s database:%s is fail.",
                            storageName, databaseName);
                }
            }
        }

    }

    //释放连接回连接池
    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn, List<Statement> statements) {
        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                sessionId, AlbianLoggerLevel.Info,
                "return the connection from storage:%s and database:%s by connection pool.",
                storageName, databaseName);

        try {
            if (statements != null) {
                for (Statement statement : statements) {
                    try {
                        ((PreparedStatement) statement).clearParameters();
                    } catch (SQLException e) {
                        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                sessionId, AlbianLoggerLevel.Error, e,
                                "close the statement to storage:%s database:%s is fail.",
                                storageName, databaseName);
                    } finally {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                    sessionId, AlbianLoggerLevel.Error, e,
                                    "close the statconnectionement to storage:%s database:%s is fail.",
                                    storageName, databaseName);
                        }
                    }
                }
            }
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException e) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error, e,
                        "close the  connection to storage:%s database:%s is fail.",
                        storageName, databaseName);
            }
        }
    }


//    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn) {
//        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
//                sessionId, AlbianLoggerLevel.Info,
//                "return the connection from storage:%s and database:%s by connection pool.",
//                storageName, databaseName);
//
//
//        try {
//            if (conn != null) {
//                conn.close();
//            }
//
//        } catch (SQLException e) {
//            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
//                    sessionId, AlbianLoggerLevel.Error, e,
//                    "close the  connection to storage:%s database:%s is fail.",
//                    storageName, databaseName);
//        }
//    }

}
