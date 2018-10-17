package org.albianj.persistence.impl.dbpool.impl;

import com.mysql.jdbc.NotImplemented;
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

    private LinkedList<IPoolingConnection> freeConnections = new LinkedList<>();
    private LinkedList<IPoolingConnection> busyConnections = new LinkedList<>();
    private LinkedList<IPoolingConnection> remebyConnections = new LinkedList<>();

    private SpxDBPool(){
        super();
    }

    public static synchronized SpxDBPool createConnectionPool(ISpxDBPoolConfig cf) {

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
        pool.setPoolName(cf.getPoolName());

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

    public String getPoolName() {
        return name;
    }

    public void setPoolName(String name) {
        this.name = name;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
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
    public  int getBusyCount() {
        synchronized(busyConnections) {
            return this.busyConnections.size();
        }
    }

    @Override
    public synchronized int getFreeCount() {
        synchronized(freeConnections) {
            return this.freeConnections.size();
        }
    }

    public synchronized int getRemedyCount(){
        synchronized(remebyConnections) {
            return remebyConnections.size();
        }
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
        synchronized(freeConnections){
            freeConnections.remove(pconn);
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

    private  void pushRemedyConnection(IPoolingConnection pconn){
        synchronized(remebyConnections){
            remebyConnections.addLast(pconn);
        }
    }

    private  void removeRemedyConnection(IPoolingConnection pconn){
        synchronized(remebyConnections){
            remebyConnections.remove(pconn);
        }
    }

    private IPoolingConnection newConnection(boolean isPooling) throws SQLException {

        Connection conn = null;
        IPoolingConnection pconn = null;
        long now = System.currentTimeMillis();
        if (this.cf != null) {
            conn = DriverManager.getConnection(this.cf.getUrl(),
                    this.cf.getUsername(),
                    this.cf.getPassword());
            pconn = new PoolingConnection(conn,System.currentTimeMillis(),isPooling);
            pconn.setLastUsedTimeMs(now);
            pconn.setStartupTimeMs(now);
        }
        return pconn;
    }

    private void usePoolingConnection(String sessionId, IPoolingConnection pconn){
        pushBusyConnection(pconn);
        pconn.setLastUsedTimeMs(System.currentTimeMillis());
        pconn.addReuseTimes();
        pconn.setSessionId(sessionId);
    }

    private void useRemedyConnection(String sessionId,IPoolingConnection pconn){
        pushRemedyConnection(pconn);
        pconn.setLastUsedTimeMs(System.currentTimeMillis());
        pconn.setSessionId(sessionId);
    }

    @Override
    public  Connection getConnection() throws SQLException {
        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                "DBPOOL", AlbianLoggerLevel.Error,
                "please use the same function with argument sessionid.",cf.getPoolName());
        throw new NotImplemented();
    }

    @Override
    public Connection getConnection(String sessionId) throws SQLException {
        IPoolingConnection pconn = null;
        long now = System.currentTimeMillis();
        pconn = pollFreeConnection();
        if(null != pconn) { // have free connection
            if(pconn.getLastUsedTimeMs() + this.cf.getFreeTimeMs() <= now || !pconn.isValid()) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Warn,
                        "DBPOOL -> %s.free time expired connection which lastUsedTime -> %d, startup -> %d, reuse -> %d,timout -> %d,valid -> %s.close it and new pooling one.",
                        cf.getPoolName(),pconn.getLastUsedTimeMs(), pconn.getStartupTimeMs(),pconn.getReuseTimes(),
                        (now - pconn.getLastUsedTimeMs() - cf.getFreeTimeMs()),pconn.isValid() ? "true" : "false");
                pconn.close();
                pconn = newConnection(true);
            }
            usePoolingConnection(sessionId,pconn);
            return pconn;
        }

        // not have free connection
        // new one and add to dbpool
        if(this.getBusyCount() < this.cf.getMaxConnections()) { // maybe not threadsafe but soso
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Info,
                    "DBPOOL -> %s.not have free connection and new pooling one.",
                    cf.getPoolName());
            pconn = newConnection(true);
            usePoolingConnection(sessionId,pconn);
            return pconn;
        }

        //all connection is busy
        if (cf.getWaitTimeWhenGetMs() <= 0) { // not wait and do remedy
            if (this.getRemedyCount() < cf.getMaxRemedyConnectionCount()) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Warn,
                        "DBPOOL -> %s.all connection is busy,the config is not waitting and new remedy one.",
                        cf.getPoolName());
                pconn = newConnection(false);
                useRemedyConnection(sessionId,pconn);
                if (this.getRemedyCount() >= cf.getMaxRemedyConnectionCount() / 2) {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Mark,
                            "DBPOOL -> %s.the remedy connections count -> %d over the half by max  remedy connections -> %d.Critical,maybe dbpool is overflow.",
                            cf.getPoolName(), this.getRemedyCount(), cf.getMaxRemedyConnectionCount());
                }
                return pconn;
            }
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Mark,
                    "DBPOOL -> %s.current remedy connections -> %d over the maxsize -> %d,not connection can use..Critical,maybe dbpool is overflow.",
                    cf.getPoolName(), this.getRemedyCount(), cf.getMaxRemedyConnectionCount());
            return null;
        }

        // wait
        long beginWait = System.currentTimeMillis();
        synchronized (this) {
            try {
                this.wait(cf.getWaitTimeWhenGetMs());
            } catch (InterruptedException  e) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error, e,
                        "DBPOOL -> %s.get connection when wait was be Interrupted.",
                        cf.getPoolName());
            }
        }

        long endWait = System.currentTimeMillis();
        if (beginWait + cf.getWaitTimeWhenGetMs() > endWait) {
            //wakeup by notify
            return this.getConnection(sessionId);
        }

        // timeout and do remedy
        if (this.getRemedyCount() < cf.getMaxRemedyConnectionCount()) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error,
                    "DBPOOL -> %s.all connection is busy and wait timeout.try new remedy connection.",
                    cf.getPoolName());

            pconn = newConnection(false);
            useRemedyConnection(sessionId,pconn);
            if (this.getRemedyCount() >= cf.getMaxRemedyConnectionCount() / 2) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Mark,
                        "DBPOOL -> %s.the remedy connections count -> %d over the half by max  remedy connections -> %d.Critical,maybe dbpool is overflow.",
                        cf.getPoolName(), this.getRemedyCount(), cf.getMaxRemedyConnectionCount());
            }
            return pconn;
        }

        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                sessionId, AlbianLoggerLevel.Mark,
                "DBPOOL -> %s.current remedy connections -> %d over the maxsize -> %d,not connection can use.Critical,maybe dbpool is overflow.",
                cf.getPoolName(), this.getRemedyCount(), cf.getMaxRemedyConnectionCount());
        return null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                "DBPOOL", AlbianLoggerLevel.Error,
                "please use the same function with argument sessionid.",cf.getPoolName());
        throw new NotImplemented();
    }

    @Override
    public synchronized void rtnConnection(Connection conn) throws SQLException {
        IPoolingConnection pconn = (IPoolingConnection) conn;
        String sessionId = pconn.getSessionId();
        if(!pconn.isPooling()) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Info,
                    "DBPOOL -> %s.back remedy connecton.close it.",cf.getPoolName());
            removeRemedyConnection(pconn);
            pconn.close();
            return;
        }

        //back pooling connection
        removeBusyConnection(pconn);
        long now = System.currentTimeMillis();
        if(pconn.getStartupTimeMs() + cf.getLifeTimeMs() < now) {//over the max lifecycle,kill it
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Info,
                    "DBPOOL -> %s.close pooling connection which over the maxlife.startup -> %d,now -> %d,max life -> %d.reuse -> %d.",
                    cf.getPoolName(),pconn.getStartupTimeMs(),now,cf.getLifeTimeMs(),pconn.getReuseTimes());
            pconn.close();
            return;
        }
        if(pconn.isValid()) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Info,
                    "DBPOOL -> %s.back pooling connection.startup -> %d,now -> %d,max life -> %d.reuse -> %d.",
                    cf.getPoolName(),pconn.getStartupTimeMs(),now,cf.getLifeTimeMs(),pconn.getReuseTimes());
            pconn.setSessionId(null); // cleanup last sessionid
            pushFreeConnection(pconn);
        } else {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Info,
                    "DBPOOL -> %s.close pooling connection which valid is false.startup -> %d,now -> %d,max life -> %d.reuse -> %d.",
                    cf.getPoolName(),pconn.getStartupTimeMs(),now,cf.getLifeTimeMs(),pconn.getReuseTimes());
            pconn.close();
        }
        this.notifyAll(); // keep wakeup sleep thread
    }

    @Override
    public synchronized void destroy() {
        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                "DBPOOL", AlbianLoggerLevel.Mark,
                "destory the dbpool -> %s.",
                this.getPoolName());

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
        synchronized (remebyConnections) {
            for (IPoolingConnection pconn : this.remebyConnections) {
                try {
                    if (pconn.isValid()) {
                        pconn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            this.remebyConnections.clear();
        }
        this.isActive = false;
    }

    private void regeditCleanupTask() {

        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                "DBPOOL", AlbianLoggerLevel.Mark,
                "regedit cleanup task for dbpool -> %s.which startup every millisecond -> %d.",
                this.getPoolName(),cf.getCleanupTimestampMs());
        new Thread(new cleanupTask(this)).start();
    }

    class cleanupTask extends Thread {
        private SpxDBPool pool = null;

        public cleanupTask(SpxDBPool pool) {
            this.pool = pool;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            "DBPOOL", AlbianLoggerLevel.Mark,
                            "cleanup task is wakeup. pool -> %s,current state : busy -> %d,free -> %d,remedy -> %d..",
                            pool.getPoolName(), pool.getBusyCount(), pool.getFreeCount(), pool.getRemedyCount());
                    long now = System.currentTimeMillis();
                    synchronized (busyConnections) {
                        for (IPoolingConnection pconn : busyConnections) {
                            try {
                                if (pconn.getLastUsedTimeMs() + cf.getMaxRequestTimeMs() < now) { // exec timeout
                                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                            pconn.getSessionId(), AlbianLoggerLevel.Mark,
                                            "DBPOOL -> %s Cleanup Task. busy connection is request timeout,close it force.request time -> %d,now -> %d,timeout ->%d.",
                                            pool.getPoolName(), pconn.getLastUsedTimeMs(), now, cf.getMaxRequestTimeMs());
                                    removeBusyConnection(pconn);
                                    pconn.close();
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    synchronized (freeConnections) {
                        for (IPoolingConnection pconn : freeConnections) {
                            try {
                                if (pconn.getLastUsedTimeMs() + cf.getFreeTimeMs() < now) { // free timeout
                                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                            pconn.getSessionId(), AlbianLoggerLevel.Mark,
                                            "DBPOOL -> %s Cleanup Task.free connection is timeout,close it force.last used time -> %d,now -> %d,timeout -> %d.",
                                            pool.getPoolName(), pconn.getLastUsedTimeMs(), now, cf.getFreeTimeMs());
                                    removeFreeConnection(pconn);
                                    pconn.close();
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    synchronized (remebyConnections) {
                        for (IPoolingConnection pconn : remebyConnections) {
                            try {
                                if (pconn.getLastUsedTimeMs() + cf.getMaxRequestTimeMs() < now) { // exec timeout
                                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                            pconn.getSessionId(), AlbianLoggerLevel.Mark,
                                            "DBPOOL -> %s Cleanup Task. remedy connection is request timeout,close it force.begin time -> %d,now -> %d,timeout ->%d.",
                                            pool.getPoolName(), pconn.getLastUsedTimeMs(), now, cf.getMaxRequestTimeMs());
                                    removeRemedyConnection(pconn);
                                    pconn.close();
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    int currConnsCount = freeConnections.size() + busyConnections.size();//maybe not thread safe but soso
                    if (currConnsCount < cf.getMinConnections()) {
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
                } catch (Throwable t) {

                } finally {
                    try {
                        Thread.sleep(cf.getCleanupTimestampMs());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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

}
