package org.albianj.pooling.impl;

import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.pooling.*;
import org.albianj.service.AlbianServiceRouter;
import sun.plugin.util.PluginConsoleController;

import java.util.LinkedList;

public class ReusableObjectPool implements IReusableObjectPool {
    private IPoolingConfig cf = null;
    private Boolean isActive = true;
    private String name;
    private IPoolingObjectFactory objFactory;
    private IPoolingObjectConfig objConfig;

    private LinkedList<IPoolingObject> freeConnections = new LinkedList<>();
    private LinkedList<IPoolingObject> busyConnections = new LinkedList<>();
    private LinkedList<IPoolingObject> remebyConnections = new LinkedList<>();

    private ReusableObjectPool() {
        super();
    }

    public static IReusableObjectPool newConnectionPool(IPoolingConfig cf,
                                                                       IPoolingObjectFactory objFactory,
                                                                       IPoolingObjectConfig objConfig) {

        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                "DBPOOL", AlbianLoggerLevel.Mark,
                "create dbpool ->%s with argument: minConnections -> %d,maxConnections -> %d,"
                        + "waitTimeWhenGetMs -> %d, lifeTimeMs -> %d, freeTimeMs -> %d,"
                        + "maxRemedyConnectionCount - > %d,max request timeout -> %d,cleanup timestamp -> %d.s",
                cf.getPoolName(), cf.getMinPoolingCount(), cf.getMaxPoolingCount(),
                cf.getWaitTimeWhenGetMs(), cf.getLifeCycleTimeMs(), cf.getWaitInFreePoolMs(),
                cf.getMaxRemedyObjectCount(), cf.getMaxRequestTimeMs(),
                cf.getCleanupTimestampMs());

        ReusableObjectPool pool = new ReusableObjectPool();
        pool.cf = cf;
        pool.objFactory = objFactory;
        pool.setPoolName(cf.getPoolName());
        pool.objConfig = objConfig;

        for (int i = 0; i < pool.cf.getMinPoolingCount(); i++) {
            try {
                IPoolingObject conn = objFactory.newPoolingObject(true,objConfig);
                pool.freeConnections.add(conn);
            } catch (Exception e) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        "ObjectPool", AlbianLoggerLevel.Error, e,
                        "create ObjectPool -> %s is fail.", cf.getPoolName());
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
    public IPoolingConfig getConfig() {
        return cf;
    }

    @Override
    public void setConfig(IPoolingConfig config) {
        this.cf = config;
    }

    @Override
    public int getBusyCount() {
        synchronized (busyConnections) {
            return this.busyConnections.size();
        }
    }

    @Override
    public int getFreeCount() {
        synchronized (freeConnections) {
            return this.freeConnections.size();
        }
    }

    public int getRemedyCount() {
        synchronized (remebyConnections) {
            return remebyConnections.size();
        }
    }

    private IPoolingObject pollFreeConnection() {
        synchronized (freeConnections) {
            return freeConnections.pollFirst();
        }
    }

    private void pushFreeConnection(IPoolingObject pconn) {
        synchronized (freeConnections) {
            freeConnections.addLast(pconn);
        }
    }

    private void removeFreeConnection(IPoolingObject pconn) {
        synchronized (freeConnections) {
            freeConnections.remove(pconn);
        }
    }

    private void pushBusyConnection(IPoolingObject pconn) {
        synchronized (busyConnections) {
            busyConnections.addLast(pconn);
        }
    }

    private void removeBusyConnection(IPoolingObject pconn) {
        synchronized (busyConnections) {
            busyConnections.remove(pconn);
        }
    }

    private void pushRemedyConnection(IPoolingObject pconn) {
        synchronized (remebyConnections) {
            remebyConnections.addLast(pconn);
        }
    }

    private void removeRemedyConnection(IPoolingObject pconn) {
        synchronized (remebyConnections) {
            remebyConnections.remove(pconn);
        }
    }

    private void usePoolingConnection(String sessionId, IPoolingObject pconn) {
        pushBusyConnection(pconn);
        pconn.setLastUsedTimeMs(System.currentTimeMillis());
        pconn.addReuseTimes();
        pconn.setSessionId(sessionId);
    }

    private void useRemedyConnection(String sessionId, IPoolingObject pconn) {
        pushRemedyConnection(pconn);
        pconn.setLastUsedTimeMs(System.currentTimeMillis());
        pconn.setSessionId(sessionId);
    }

    @Override
    public <T extends AutoCloseable> IPoolingObject<T> getPoolingObject(String sessionId) throws Exception{
        IPoolingObject<T> pconn = null;
        long now = System.currentTimeMillis();
        pconn = pollFreeConnection();
        if (null != pconn) { // have free connection
            if (pconn.getLastUsedTimeMs() + this.cf.getWaitInFreePoolMs() <= now || !pconn.isValid()) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Warn,
                        "ObjectPool -> %s.free time expired connection which lastUsedTime -> %d, startup -> %d, reuse -> %d,timout -> %d,valid -> %s.close it and new pooling one.",
                        cf.getPoolName(), pconn.getLastUsedTimeMs(), pconn.getStartupTimeMs(), pconn.getReuseTimes(),
                        (now - pconn.getLastUsedTimeMs() - cf.getWaitInFreePoolMs()), pconn.isValid() ? "true" : "false");
                AutoCloseable autoCloseable =  pconn.getWrappedObject();
                autoCloseable.close();
                pconn = objFactory.newPoolingObject(true,objConfig);
            }
            usePoolingConnection(sessionId, pconn);
            return pconn;
        }

        // not have free connection
        // new one and add to dbpool
        if (this.getBusyCount() < this.cf.getMaxPoolingCount()) { // maybe not threadsafe but soso
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Info,
                    "ObjectPool -> %s.not have free connection and new pooling one.",
                    cf.getPoolName());
            pconn = objFactory.newPoolingObject(true,objConfig);
            usePoolingConnection(sessionId, pconn);
            return pconn;
        }

        //all connection is busy
        if (cf.getWaitTimeWhenGetMs() <= 0) { // not wait and do remedy
            if (this.getRemedyCount() < cf.getMaxRemedyObjectCount()) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Warn,
                        "ObjectPool -> %s.all connection is busy,the config is not waitting and new remedy one.",
                        cf.getPoolName());
                pconn = objFactory.newPoolingObject(false,objConfig);
                useRemedyConnection(sessionId, pconn);
                if (this.getRemedyCount() >= cf.getMaxRemedyObjectCount() / 2) {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Mark,
                            "ObjectPool -> %s.the remedy connections count -> %d over the half by max  remedy connections -> %d.Critical,maybe dbpool is overflow.",
                            cf.getPoolName(), this.getRemedyCount(), cf.getMaxRemedyObjectCount());
                }
                return pconn;
            }
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Mark,
                    "ObjectPool -> %s.current remedy connections -> %d over the maxsize -> %d,not connection can use..Critical,maybe dbpool is overflow.",
                    cf.getPoolName(), this.getRemedyCount(), cf.getMaxRemedyObjectCount());
            return null;
        }

        // wait
        long beginWait = System.currentTimeMillis();
        synchronized (this) {
            try {
                this.wait(cf.getWaitTimeWhenGetMs());
            } catch (InterruptedException e) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error, e,
                        "ObjectPool -> %s.get connection when wait was be Interrupted.",
                        cf.getPoolName());
            }
        }

        long endWait = System.currentTimeMillis();
        if (beginWait + cf.getWaitTimeWhenGetMs() > endWait) {
            //wakeup by notify
            return this.getPoolingObject(sessionId);
        }

        // wait timeout and do remedy
        if (this.getRemedyCount() < cf.getMaxRemedyObjectCount()) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error,
                    "ObjectPool -> %s.all connection is busy and wait timeout.try new remedy connection.",
                    cf.getPoolName());

            pconn = objFactory.newPoolingObject(false,objConfig);
            useRemedyConnection(sessionId, pconn);
            if (this.getRemedyCount() >= cf.getMaxRemedyObjectCount() / 2) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Mark,
                        "ObjectPool -> %s.the remedy connections count -> %d over the half by max  remedy connections -> %d.Critical,maybe dbpool is overflow.",
                        cf.getPoolName(), this.getRemedyCount(), cf.getMaxRemedyObjectCount());
            }
            return pconn;
        }

        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                sessionId, AlbianLoggerLevel.Mark,
                "ObjectPool -> %s.current remedy connections -> %d over the maxsize -> %d,not connection can use.Critical,maybe dbpool is overflow.",
                cf.getPoolName(), this.getRemedyCount(), cf.getMaxRemedyObjectCount());
        return null;
    }

    public synchronized <T extends AutoCloseable> void returnPoolingObject(IPoolingObject<T> conn) throws Exception {
        IPoolingObject<T> pconn = (IPoolingObject) conn;
        String sessionId = pconn.getSessionId();
        if (!pconn.isPooling()) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Info,
                    "ObjectPool -> %s.back remedy connecton.close it.", cf.getPoolName());
            removeRemedyConnection(pconn);
            AutoCloseable autoCloseable =  pconn.getWrappedObject();
            autoCloseable.close();
            return;
        }

        //back pooling connection
        removeBusyConnection(pconn);
        long now = System.currentTimeMillis();
        if (pconn.getStartupTimeMs() + cf.getLifeCycleTimeMs() < now) {//over the max lifecycle,kill it
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Info,
                    "ObjectPool -> %s.close pooling connection which over the maxlife.startup -> %d,now -> %d,max life -> %d.reuse -> %d.",
                    cf.getPoolName(), pconn.getStartupTimeMs(), now, cf.getLifeCycleTimeMs(), pconn.getReuseTimes());
            AutoCloseable autoCloseable =  pconn.getWrappedObject();
            autoCloseable.close();
            return;
        }
        if (pconn.isValid()) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Info,
                    "ObjectPool -> %s.back pooling connection.startup -> %d,now -> %d,max life -> %d.reuse -> %d.",
                    cf.getPoolName(), pconn.getStartupTimeMs(), now, cf.getLifeCycleTimeMs(), pconn.getReuseTimes());
            pconn.setSessionId(null); // cleanup last sessionid
            pushFreeConnection(pconn);
        } else {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Info,
                    "ObjectPool -> %s.close pooling connection which valid is false.startup -> %d,now -> %d,max life -> %d.reuse -> %d.",
                    cf.getPoolName(), pconn.getStartupTimeMs(), now, cf.getLifeCycleTimeMs(), pconn.getReuseTimes());
            AutoCloseable autoCloseable =  pconn.getWrappedObject();
            autoCloseable.close();
        }
        this.notifyAll(); // keep wakeup sleep thread
    }

    @Override
    public synchronized void destroy() {
        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                "ObjectPool", AlbianLoggerLevel.Mark,
                "destory the ObjectPool -> %s.",
                this.getPoolName());

        synchronized (freeConnections) {
            for (IPoolingObject pconn : this.freeConnections) {
                closeObject(pconn);
            }
            this.freeConnections.clear();
        }
        synchronized (busyConnections) {
            for (IPoolingObject pconn : this.busyConnections) {
                closeObject(pconn);
            }
            this.busyConnections.clear();
        }
        synchronized (remebyConnections) {
            for (IPoolingObject pconn : this.remebyConnections) {
                closeObject(pconn);
            }
            this.remebyConnections.clear();
        }
        this.isActive = false;
    }

    protected void closeObject(IPoolingObject pconn) {
        try {
            if (pconn.isValid()) {
                AutoCloseable autoCloseable =  pconn.getWrappedObject();
                autoCloseable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void regeditCleanupTask() {

        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                "ObjectPool", AlbianLoggerLevel.Mark,
                "regedit cleanup task for ObjectPool -> %s.which startup every millisecond -> %d.",
                this.getPoolName(), cf.getCleanupTimestampMs());
        new Thread(new cleanupTask(this)).start();
    }


    class cleanupTask extends Thread {
        private ReusableObjectPool pool = null;

        public cleanupTask(ReusableObjectPool pool) {
            this.pool = pool;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(cf.getCleanupTimestampMs());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            "ObjectPool", AlbianLoggerLevel.Mark,
                            "cleanup task is wakeup. pool -> %s,current state : busy -> %d,free -> %d,remedy -> %d..",
                            pool.getPoolName(), pool.getBusyCount(), pool.getFreeCount(), pool.getRemedyCount());
                    long now = System.currentTimeMillis();
                    synchronized (busyConnections) {
                        for (IPoolingObject pconn : busyConnections) {
                            try {
                                if (pconn.getLastUsedTimeMs() + cf.getMaxRequestTimeMs() < now) { // exec timeout
                                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                            pconn.getSessionId(), AlbianLoggerLevel.Mark,
                                            "ObjectPool -> %s Cleanup Task. busy connection is request timeout,close it force.request time -> %d,now -> %d,timeout ->%d.",
                                            pool.getPoolName(), pconn.getLastUsedTimeMs(), now, cf.getMaxRequestTimeMs());
                                    removeBusyConnection(pconn);
                                    AutoCloseable autoCloseable =  pconn.getWrappedObject();
                                    autoCloseable.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    synchronized (freeConnections) {
                        for (IPoolingObject pconn : freeConnections) {
                            try {
                                if (pconn.getLastUsedTimeMs() + cf.getWaitInFreePoolMs() < now) { // free timeout
                                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                            pconn.getSessionId(), AlbianLoggerLevel.Mark,
                                            "ObjectPool -> %s Cleanup Task.free connection is timeout,close it force.last used time -> %d,now -> %d,timeout -> %d.",
                                            pool.getPoolName(), pconn.getLastUsedTimeMs(), now, cf.getWaitInFreePoolMs());
                                    removeFreeConnection(pconn);
                                    AutoCloseable autoCloseable =  pconn.getWrappedObject();
                                    autoCloseable.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    synchronized (remebyConnections) {
                        for (IPoolingObject pconn : remebyConnections) {
                            try {
                                if (pconn.getLastUsedTimeMs() + cf.getMaxRequestTimeMs() < now) { // exec timeout
                                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                            pconn.getSessionId(), AlbianLoggerLevel.Mark,
                                            "ObjectPool -> %s Cleanup Task. remedy connection is request timeout,close it force.begin time -> %d,now -> %d,timeout ->%d.",
                                            pool.getPoolName(), pconn.getLastUsedTimeMs(), now, cf.getMaxRequestTimeMs());
                                    removeRemedyConnection(pconn);
                                    AutoCloseable autoCloseable =  pconn.getWrappedObject();
                                    autoCloseable.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    int currConnsCount = freeConnections.size() + busyConnections.size();//maybe not thread safe but soso
                    if (currConnsCount < cf.getMinPoolingCount()) {
                        int sub = cf.getMinPoolingCount() - currConnsCount;
                        for (int i = 0; i < sub; i++) {
                            try {
                                IPoolingObject pconn = objFactory.newPoolingObject(true,objConfig);
                                pushFreeConnection(pconn);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Throwable t) {

                }
            }
        }
    }

}
