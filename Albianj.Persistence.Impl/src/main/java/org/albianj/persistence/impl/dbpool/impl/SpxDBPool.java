package org.albianj.persistence.impl.dbpool.impl;

import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.impl.dbpool.ISpxDBPoolConfig;
import org.albianj.persistence.impl.dbpool.IPoolingConnection;
import org.albianj.persistence.impl.dbpool.ISpxDBPool;
import org.albianj.service.AlbianServiceRouter;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SpxDBPool implements ISpxDBPool {
    private ISpxDBPoolConfig cf = null;
    private Boolean isActive = true;
    private String name;

    public String getPoolName() {
        return name;
    }

    public void setPoolName(String name) {
        this.name = name;
    }

    private LinkedList<IPoolingConnection> freeConnections = new LinkedList<>();
    private LinkedList<IPoolingConnection> busyConnections = new LinkedList<>();
    private Object remebyLocker = new Object();

    private int CurrRemedyConnectionsCount = 0;

    private SpxDBPool(){
        super();
    }

    private IPoolingConnection pollFreeConnection(){
        synchronized(freeConnections) {
            return freeConnections.pollFirst();
        }
    }

    private void pushFreeConnection(IPoolingConnection pconn){
        synchronized (freeConnections) {
            freeConnections.addLast(pconn);
        }
    }

    private  void removeFreeConnection(IPoolingConnection pconn){
        synchronized(busyConnections){
            freeConnections.remove(pconn);
        }
    }

    private IPoolingConnection pollBusyConnection(){
        synchronized (busyConnections) {
            return busyConnections.pollFirst();
        }
    }

    private  void pushBusyConnection(IPoolingConnection pconn){
        synchronized(busyConnections){
            busyConnections.addLast(pconn);
        }
    }

    private  void removeBusyConnection(IPoolingConnection pconn){
        synchronized(busyConnections){
            busyConnections.remove(pconn);
        }
    }



    public static SpxDBPool createConnectionPool(ISpxDBPoolConfig cf) {

        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                "DBPOOL", AlbianLoggerLevel.Mark,
        "create dbpool ->%s with argument: minConnections -> %d,maxConnections -> %d,"
                + "waitTimeWhenGetMs -> %d, lifeTimeMs -> %d, freeTimeMs -> %d,"
                +"maxRemedyConnectionCount - > %d,max request timeout -> %d,cleanup timestamp -> %d.s",
                cf.getPoolName(),cf.getMinConnections(),cf.getMaxConnections(),
                cf.getWaitTimeWhenGetMs(),cf.getLifeTimeMs(),cf.getFreeTimeMs(),
                cf.getMaxRemedyConnectionCount(),cf.getMaxRequestTimeMs(),
                cf.getCleanupTimestampMs());

        SpxDBPool pool = new SpxDBPool();
        pool.cf = cf;

        for (int i = 0; i < pool.cf.getMinConnections(); i++) {
            try {
                IPoolingConnection conn = pool.newConnection(true);
                pool.freeConnections.add(conn);
            } catch (SQLException e) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        "DBPOOL", AlbianLoggerLevel.Error,e,
                "create dbpool -> %s is fail.",cf.getPoolName());
                return null;
            }
        }

        pool.isActive = true;
        pool.regeditCleanupTask();
        return pool;
    }

    /**
     * 创建一个新的连接
     * @return 数据库连接对象
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private IPoolingConnection newConnection(boolean isPooling) throws SQLException {

        Connection conn = null;
        IPoolingConnection pconn = null;
        if (this.cf != null) {
            conn = DriverManager.getConnection(this.cf.getUrl(),
                    this.cf.getUsername(),
                    this.cf.getPassword());
            pconn = new PoolingConnection(conn,System.currentTimeMillis(),isPooling);
        }
        return pconn;
    }

//    private void clsConnection(Connection conn){
//        conn.close();
//    }


    private void usePoolingConnection(IPoolingConnection pconn){
        pushBusyConnection(pconn);
        pconn.setLastUsedTimeMs(System.currentTimeMillis());
        pconn.addReuseTimes();
    }
    @Override
    public  Connection getConnection() throws SQLException {
        IPoolingConnection pconn = null;
        long now = System.currentTimeMillis();
        pconn = pollFreeConnection();
        if(null != pconn) { // have free connection
            if(pconn.getLastUsedTimeMs() + this.cf.getFreeTimeMs() <= now || pconn.isValid()) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        "DBPOOL", AlbianLoggerLevel.Error,
                        "free time expired connection in pool -> %s which lastUsedTime -> %d, startup -> %d, reuse -> %d.close it and new one.",
                        cf.getPoolName(),pconn.getLastUsedTimeMs(), pconn.getStartupTimeMs(),pconn.getReuseTimes());
                pconn.close();
                pconn = newConnection(true);
            }
            usePoolingConnection(pconn);
            return pconn;
        }

        // not have free connection
        // new one and add to dbpool
        if(this.getBusyCount() < this.cf.getMaxConnections()) { // maybe not threadsafe but soso
            pconn = newConnection(true);
            usePoolingConnection(pconn);
            return pconn;
        }

        synchronized (remebyLocker) {
            //all connection is busy
            if (cf.getWaitTimeWhenGetMs() <= 0) { // not wait and do remedy
                if (this.CurrRemedyConnectionsCount < cf.getMaxRemedyConnectionCount()) {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            "DBPOOL", AlbianLoggerLevel.Error,
                            "all connection in pool-> %s is busy,the config is not waitting and do remedy:new a connection.",
                            cf.getPoolName());
                    pconn = newConnection(false);
                    ++this.CurrRemedyConnectionsCount;
                    if (this.CurrRemedyConnectionsCount >= cf.getMaxRemedyConnectionCount() / 2) {
                        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                "DBPOOL", AlbianLoggerLevel.Mark,
                                "the remedy connections count -> %d over the half by max connections -> %d.Critical overflow the dbppol.",
                                cf.getPoolName(), this.CurrRemedyConnectionsCount, cf.getMaxRemedyConnectionCount());
                    }
                    return pconn;
                }
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        "DBPOOL", AlbianLoggerLevel.Mark,
                        "can not get connection,the remedy connections count -> %d over max connections -> %d.Critical overflow the dbppol.",
                        cf.getPoolName(), this.CurrRemedyConnectionsCount, cf.getMaxRemedyConnectionCount());
                return null;
            }

            // wait
            long beginWait = System.currentTimeMillis();
            try {
                this.wait(cf.getWaitTimeWhenGetMs());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long endWait = System.currentTimeMillis();
            if (beginWait + cf.getWaitTimeWhenGetMs() > endWait) {
                //wakeup by notify
                return this.getConnection();
            }

            // timeout and do remedy
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    "DBPOOL", AlbianLoggerLevel.Error,
                    "all connection in pool-> %s is busy,the config is not waitting and do remedy:new a connection.",
                    cf.getPoolName());
            pconn = newConnection(false);

            ++this.CurrRemedyConnectionsCount;

            if (this.CurrRemedyConnectionsCount >= cf.getMaxRemedyConnectionCount() / 2) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        "DBPOOL", AlbianLoggerLevel.Mark,
                        "the remedy connections count -> %d over the half by max connections -> %d.Critical overflow the dbppol.",
                        cf.getPoolName(), this.CurrRemedyConnectionsCount, cf.getMaxRemedyConnectionCount());
            }
            return pconn;
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return  getConnection();
    }


    @Override
    public synchronized void rtnConnection(Connection conn) throws SQLException {
        IPoolingConnection pconn = (IPoolingConnection) conn;
        if(!pconn.isPooling()) {
            synchronized (remebyLocker) {
                --this.CurrRemedyConnectionsCount;
            }
            pconn.close();
        }
        removeBusyConnection(pconn);
        long now = System.currentTimeMillis();
        if(pconn.getStartupTimeMs() + cf.getLifeTimeMs() >= now) {//over the max lifecycle,kill it
            pconn.close();
            return;
        }
        if(pconn.isValid()) {
            pushFreeConnection(pconn);
        } else {
            pconn.close();
        }
        this.notifyAll(); // keep wakeup sleep thread
    }

    public int getCurrRemedyConnectionsCount() {
        return CurrRemedyConnectionsCount;
    }

    @Override
    public synchronized void destroy() {
        synchronized (freeConnections) {
            for (IPoolingConnection pconn : this.freeConnections) {
                try {
                    if (pconn.isValid()) {
                        pconn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            this.freeConnections.clear();
        }
        synchronized (busyConnections) {
            for (IPoolingConnection pconn : this.busyConnections) {
                try {
                    if (pconn.isValid()) {
                        pconn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            this.busyConnections.clear();
        }
        this.isActive = false;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }


    private void regeditCleanupTask() {

        ScheduledExecutorService ses= Executors.newScheduledThreadPool(1);
        ses.scheduleAtFixedRate(new cleanupTask(this), this.cf.getCleanupTimestampMs(),
                                this.cf.getCleanupTimestampMs(), TimeUnit.MILLISECONDS);
    }

    @Override
    public synchronized int getBusyCount() {
        return this.busyConnections.size();
    }

    @Override
    public synchronized int getFreeCount() {
        return this.freeConnections.size();
    }

    @Override
    public ISpxDBPoolConfig getConfig() {
        return cf;
    }

    @Override
    public void setConfig(ISpxDBPoolConfig config) {
        this.cf = config;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    class cleanupTask extends TimerTask {
        private SpxDBPool pool = null;

        public cleanupTask(SpxDBPool pool) {
            this.pool = pool;
        }

        @Override
        public void run() {

            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    "DBPOOL", AlbianLoggerLevel.Mark,
                    "cleanup task is wakeup. pool -> %s,current state : busy -> %d,free -> %d,remedy -> %d..",
                    pool.getPoolName(),pool.getBusyCount(),pool.getFreeCount(),pool.getCurrRemedyConnectionsCount());
            long now = System.currentTimeMillis();
            synchronized (busyConnections) {
                for (IPoolingConnection pconn : busyConnections) {
                    try {
                        if (pconn.getLastUsedTimeMs() + cf.getMaxRequestTimeMs() < now ) { // exec timeout
                            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                    "DBPOOL", AlbianLoggerLevel.Mark,
                                    "connection in pool -> %s is request timeout,close it force.begin time -> %d,now -> %d,timeout ->%d..",
                                    pool.getPoolName(),pconn.getLastUsedTimeMs(),now,cf.getMaxRequestTimeMs());
                            removeBusyConnection(pconn);
                            pconn.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            synchronized (freeConnections){
                for (IPoolingConnection pconn : freeConnections) {
                    try {
                        if (pconn.getLastUsedTimeMs() + cf.getFreeTimeMs() < now ) { // free timeout
                            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                    "DBPOOL", AlbianLoggerLevel.Mark,
                                    "connection in pool -> %s is free timeout,close it force.",
                                    pool.getPoolName(),pconn.getLastUsedTimeMs(),now,cf.getMaxRequestTimeMs());
                            removeFreeConnection(pconn);
                            pconn.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            int currConnsCount = freeConnections.size() + busyConnections.size();//maybe not thread safe but soso
            if(currConnsCount < cf.getMinConnections()) {
                int sub = cf.getMinConnections() - currConnsCount;
                for (int i = 0; i < sub; i++) {
                    try {
                        IPoolingConnection pconn = pool.newConnection(true);
                        pushFreeConnection(pconn);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
