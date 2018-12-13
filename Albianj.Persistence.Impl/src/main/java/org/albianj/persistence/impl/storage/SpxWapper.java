package org.albianj.persistence.impl.storage;

import org.albianj.kernel.AlbianLevel;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.impl.dbpool.ISpxDBPool;
import org.albianj.persistence.impl.dbpool.ISpxDBPoolConfig;
import org.albianj.persistence.impl.dbpool.impl.SpxDBPool;
import org.albianj.persistence.impl.dbpool.impl.SpxDBPoolConfig;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.security.IAlbianSecurityService;
import org.albianj.service.AlbianServiceRouter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SpxWapper extends FreeDataBasePool {
    public final static String DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";

    public Connection getConnection(String sessionid, IRunningStorageAttribute rsa) {
        IStorageAttribute sa = rsa.getStorageAttribute();
        String key = sa.getName() + rsa.getDatabase();
        DataSource ds = getDatasource(key, rsa);
        ISpxDBPool pool = (ISpxDBPool) ds;

        AlbianServiceRouter.getLogger2()
                .log(IAlbianLoggerService2.AlbianSqlLoggerName, sessionid, AlbianLoggerLevel.Info,
                        "Get the connection from storage:%s and database:%s by connection pool.", sa.getName(),
                        rsa.getDatabase());
        try {
            Connection conn = pool.getConnection(sessionid);
            if (null == conn)
                return null;
            if (Connection.TRANSACTION_NONE != sa.getTransactionLevel()) {
                conn.setTransactionIsolation(sa.getTransactionLevel());
            }
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException e) {
            AlbianServiceRouter.getLogger2()
                    .log(IAlbianLoggerService2.AlbianSqlLoggerName, sessionid, AlbianLoggerLevel.Error, e,
                            "Get the connection with storage:%s and database:%s form connection pool is error.", sa.getName(),
                            rsa.getDatabase());
            return null;
        }
    }

    @Override
    public DataSource setupDataSource(String key, IRunningStorageAttribute rsa) {
        ISpxDBPoolConfig cf = null;
        try {
            cf = new SpxDBPoolConfig();
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2()
                    .logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName, IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(), "create dabasepool for storage:%s is fail.",
                            key);
        }
        try {
            IStorageAttribute stgAttr = rsa.getStorageAttribute();
            String url = FreeAlbianStorageParserService.generateConnectionUrl(rsa);
            cf.setDriverName(DRIVER_CLASSNAME);
            cf.setUrl(url);
            if (AlbianLevel.Debug == KernelSetting.getAlbianLevel()) {
                cf.setUsername(stgAttr.getUser());
                cf.setPassword(stgAttr.getPassword());
            } else {
                IAlbianSecurityService ass = AlbianServiceRouter
                        .getSingletonService(IAlbianSecurityService.class, IAlbianSecurityService.Name, false);
                if (null != ass) {
                    cf.setUsername(ass.decryptDES(stgAttr.getUser()));
                    cf.setPassword(ass.decryptDES(stgAttr.getPassword()));
                } else {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                            IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null,
                            AlbianModuleType.AlbianPersistence, AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "the run level is release in the kernel config but security is null,so not use security service.");

                    cf.setUsername(stgAttr.getUser());
                    cf.setPassword(stgAttr.getPassword());
                }
            }

            cf.setMaxConnections(stgAttr.getMaxSize());
            cf.setMinConnections(stgAttr.getMinSize());

            cf.setCleanupTimestampMs(stgAttr.getCleanupTimestampMs());
            cf.setWaitInFreePoolMs(stgAttr.getWaitInFreePoolMs());
            cf.setLifeCycleTime(stgAttr.getLifeCycleTime());
            cf.setMaxRemedyConnectionCount(stgAttr.getMaxRemedyConnectionCount());
            cf.setMaxRequestTimeMs(stgAttr.getMaxRequestTimeMs());//最大单次执行sql时间为1分钟
            cf.setPoolName(key);
            cf.setWaitTimeWhenGetMs(stgAttr.getWaitTimeWhenGetMs());
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2()
                    .logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName, IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(), "startup database connection pools is fail.");
            return null;
        }

        DataSource pool = SpxDBPool.createConnectionPool(cf);
        return pool;
    }

    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn, List<Statement> statements) {
        for (Statement stmt : statements) {
            try {
                stmt.close();
            } catch (SQLException e) {

            }
        }
        this.returnConnection(sessionId, storageName, databaseName, conn);
    }

    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn, Statement pst, ResultSet rs) {
        try {
            pst.close();
        } catch (SQLException e) {

        }
        try {
            rs.close();
        } catch (SQLException e) {

        }
        this.returnConnection(sessionId, storageName, databaseName, conn);
    }

    public void returnConnection(String sessionId, String storageName, String databaseName, Connection conn) {
        try {
            String key = storageName + databaseName;
            DataSource ds = getDatasource(key);
            if (null == ds) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Info,
                        "return the connection from storage:%s and database:%s by connection pool.",
                        storageName, databaseName);
                conn.close();
            }
            ISpxDBPool pool = (ISpxDBPool) ds;
            pool.rtnConnection(conn);
        } catch (SQLException e) {

        }
    }
}
