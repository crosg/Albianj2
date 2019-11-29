package org.albianj.persistence.impl.storage;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.albianj.argument.RefArg;
import org.albianj.kernel.AlbianLevel;
import org.albianj.kernel.KernelSetting;
import org.albianj.l5bridge.IL5BridgeService;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.impl.dbpool.impl.JDBCDriverWapper;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.security.IAlbianSecurityService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.text.StringFormat;
import org.albianj.verify.Validate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by xuhaifeng on 17/2/26.
 */
public class C3P0Wapper extends FreeDataBasePool {
    public final static String DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";

    public C3P0Wapper() {
    }

    @Override
    public Connection getConnection(String sessionid, IRunningStorageAttribute rsa,boolean isAutoCommit) {
        IStorageAttribute sa = rsa.getStorageAttribute();
        String server = null;
        int port = 0;
        if(sa.isEnableL5()) {
            RefArg<String> refServer = new RefArg<>();
            RefArg<Integer> refPort = new RefArg<>();
            if (!Validate.isNullOrEmptyOrAllSpace(sa.getL5())) {
                IL5BridgeService l5bs = AlbianServiceRouter.getSingletonService(IL5BridgeService.class, IL5BridgeService.Name, false);
                if (null == l5bs) {
                    return null;
                }
                l5bs.exchange(sa.getL5(), refServer, refPort);
                server = refServer.getValue();
                port = refPort.getValue();
            }
        } else {
            server = sa.getServer();
            port = sa.getPort();
        }
        /**
         * key : storageName-databaseName-ip-port
         */
        String key = StringFormat.byBlock("{}-{}-{}-{}",sa.getName() ,rsa.getDatabase(),server,port);
        DataSource ds = getDatasource(key, rsa,server,port); // key:storageName + databaseName

        AlbianServiceRouter.getLogger2()
                .log(IAlbianLoggerService2.AlbianSqlLoggerName, sessionid, AlbianLoggerLevel.Info,
                        "Get the connection from storage:%s and database:%s by connection pool.", sa.getName(),
                        rsa.getDatabase());
        try {
            Connection conn = ds.getConnection();
            if (null == conn)
                return null;
            if (Connection.TRANSACTION_NONE != sa.getTransactionLevel()) {
                conn.setTransactionIsolation(sa.getTransactionLevel());
            }
            conn.setAutoCommit(isAutoCommit);
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
    public DataSource setupDataSource(String key, IRunningStorageAttribute rsa,String server,int port) {
        ComboPooledDataSource ds = null;
        try {
            ds = new ComboPooledDataSource();
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2()
                    .logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName, IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(), "create dabasepool for storage:%s is fail.",
                            key);
        }
        try {
            IStorageAttribute storageAttribute = rsa.getStorageAttribute();
            String url = FreeAlbianStorageParserService.generateConnectionUrl(rsa);
            String fmtUrl = StringFormat.byIndex(url,server,port,rsa.getDatabase());

            ds.setDriverClass(DRIVER_CLASSNAME);
            try {
                Driver driver = (Driver) Class.forName(DRIVER_CLASSNAME, true, AlbianClassLoader.getInstance()).newInstance();
                DriverManager.registerDriver(new JDBCDriverWapper(driver));
            } catch (ClassNotFoundException e) {
                AlbianServiceRouter.getLogger2()
                        .logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName, IAlbianLoggerService2.InnerThreadName,
                                AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                                AlbianModuleType.AlbianPersistence.getThrowInfo(), "regedit JDBC Driver classname:%s is fail.",
                                DRIVER_CLASSNAME);
            }
            ds.setJdbcUrl(fmtUrl);
            if (AlbianLevel.Debug == KernelSetting.getAlbianLevel()) {
                ds.setUser(storageAttribute.getUser());
                ds.setPassword(storageAttribute.getPassword());
            } else {
                IAlbianSecurityService ass = AlbianServiceRouter
                        .getSingletonService(IAlbianSecurityService.class, IAlbianSecurityService.Name, false);
                if (null != ass) {
                    ds.setUser(ass.decryptDES(storageAttribute.getUser()));
                    ds.setPassword(ass.decryptDES(storageAttribute.getPassword()));
                } else {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                            IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null,
                            AlbianModuleType.AlbianPersistence, AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "the run level is release in the kernel config but security is null,so not use security service.");

                    ds.setUser(storageAttribute.getUser());
                    ds.setPassword(storageAttribute.getPassword());
                }
            }
            ds.setAutoCommitOnClose(false); //连接关闭时默认将所有未提交的操作回滚

            if (storageAttribute.getPooling()) {
                ds.setMaxPoolSize(storageAttribute.getMaxSize());
                ds.setMinPoolSize(storageAttribute.getMinSize());
                ds.setInitialPoolSize(storageAttribute.getMinSize());
                ds.setMaxIdleTime(storageAttribute.getAliveTime() - 5);
                ds.setMaxConnectionAge(storageAttribute.getAliveTime() - 5);

            } else {
                ds.setMaxPoolSize(8);
                ds.setMinPoolSize(4);
                ds.setInitialPoolSize(4);
                ds.setMaxIdleTime(50);
                ds.setMaxConnectionAge(50);
            }

            ds.setAcquireIncrement(2); //链接用完了自动增量2个
            ds.setAcquireRetryAttempts(3); //链接失败后重新试3次
            ds.setAcquireRetryDelay(1000); //两次连接中间隔1000毫秒
            ds.setCheckoutTimeout(1000); //程序从连接池checkout session的时候等待1000毫秒，超时则抛出异常
            ds.setIdleConnectionTestPeriod(30); //每30秒检查所有连接池中的空闲连接
            ds.setNumHelperThreads(3); //异步操作，提升性能通过多线程实现多个操作同时被执行。
            ds.setPreferredTestQuery("SELECT 1");
            ds.setMaxStatements(0); //定义了连接池内单个连接所拥有的最大缓存statements数
            ds.setDebugUnreturnedConnectionStackTraces(true);//打开链接池的泄露调试
            ds.setUnreturnedConnectionTimeout(120); //增加没有返回的链接超时机制，防止链接泄露，单位是秒
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2()
                    .logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName, IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(), "startup database connection pools is fail.");
            return null;
        }

        return ds;
    }

}
