package org.albianj.persistence.impl.dbpool.impl;

import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.impl.dbpool.IDBPoolConfig;
import org.albianj.persistence.impl.dbpool.ISpxDBPool;
import org.albianj.service.AlbianServiceRouter;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpxDBPool implements ISpxDBPool {
    private static final Logger log = Logger.getLogger(SpxDBPool.class);

    private IDBPoolConfig cf = null;
    private Boolean isActive = true;

    private LinkedList<Connection> freeConnections = new LinkedList<Connection>();
    private LinkedList<Connection> busyConnections = new LinkedList<Connection>();

    private SpxDBPool(){
        super();
    }

    public static SpxDBPool createConnectionPool(DBPoolConfig prop) {

        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                "DBPOOL", AlbianLoggerLevel.Mark,
        "create dbpool ->%s with argument: minConnections -> %d,maxConnections -> %d,"
                + "waitTimeWhenGetMs -> %d, lifeTimeMs -> %d, freeTimeMs -> %d,"
                +"maxRemedyConnectionCount - > %d,",
                prop.getPoolName(),prop.getMinConnections(),prop.getMaxConnections(),
                prop.getWaitTimeWhenGetMs(),prop.getLifeTimeMs(),prop.getFreeTimeMs(),
                prop.getMaxRemedyConnectionCount());

        SpxDBPool pool = new SpxDBPool();
        pool.cf = prop;

        //基本点2、始使化时根据配置中的初始连接数创建指定数量的连接
        for (int i = 0; i < pool.cf.getMinConnections(); i++) {
            try {
                Connection conn = pool.newConnection();
                pool.freeConnections.add(conn);
            } catch (SQLException e) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        "DBPOOL", AlbianLoggerLevel.Mark,e,
                "create dbpool -> %s is fail.",prop.getPoolName());
                return null;
            }
        }

        pool.isActive = true;
        return pool;
    }



    /**
     * 检测连接是否有效
     * @return Boolean
     */
    private Boolean isValidConnection(Connection conn) throws SQLException {
        if(conn == null || conn.isClosed()){
            return false;
        }
        return true;
    }

    /**
     * 创建一个新的连接
     * @return 数据库连接对象
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private Connection newConnection() throws SQLException {

        Connection conn = null;
        if (this.cf != null) {
            conn = DriverManager.getConnection(this.cf.getUrl(),
                    this.cf.getUsername(),
                    this.cf.getPassword());
        }
        return conn;
    }


    @Override
    public synchronized Connection getConn() {
        Connection conn = null;
        if (this.getBusyCount() < this.cf.getMaxConnections()) {
            // 分支1：当前使用的连接没有达到最大连接数
            // 基本点3、在连接池没有达到最大连接数之前，如果有可用的空闲连接就直接使用空闲连接，如果没有，就创建新的连接。
            if (this.getFreeCount() > 0) {
                // 分支1.1：如果空闲池中有连接，就从空闲池中直接获取
                log.info("分支1.1：如果空闲池中有连接，就从空闲池中直接获取");
                conn = this.freeConnections.pollFirst();

                //连接闲置久了也会超时，因此空闲池中的有效连接会越来越少，需要另一个进程进行扫描监测，不断保持一定数量的可用连接。
                //在下面定义了checkFreepools的TimerTask类，在checkPool()方法中进行调用。

                //基本点5、由于数据库连接闲置久了会超时关闭，因此需要连接池采用机制保证每次请求的连接都是有效可用的。
                try {
                    if(this.isValidConnection(conn)){
                        this.busyConnections.add(conn);
//                        currentConnection.set(conn);
                    }else{
                        conn = getConn();//同步方法是可重入锁
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                // 分支1.2：如果空闲池中无可用连接，就创建新的连接
                log.info("分支1.2：如果空闲池中无可用连接，就创建新的连接");
                try {
                    conn = this.newConnection();
                    this.busyConnections.add(conn);
                } catch ( SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // 分支2：当前已到达最大连接数
            // 基本点4、当连接池中的活动连接数达到最大连接数，新的请求进入等待状态，直到有连接被释放。
            log.info("分支2：当前已到达最大连接数 ");
            long startTime = System.currentTimeMillis();

            //进入等待状态。等待被notify(),notifyALL()唤醒或者超时自动苏醒
            try{
                this.wait(this.cf.getConninterval());
            }catch(InterruptedException e) {
                log.error("线程等待被打断");
            }

            //若线程超时前被唤醒并成功获取连接，就不会走到return null。
            //若线程超时前没有获取连接，则返回null。
            //如果timeout设置为0，就无限重连。
            if(this.cf.getWaitTimeWhenGetMs()!=0){
                if(System.currentTimeMillis() - startTime > this.cf.getWaitTimeWhenGetMs())
                    return null;
            }
            conn = this.getConn();

        }
        return conn;
    }


//    @Override
//    public Connection getCurrConn() {
//        Connection conn=currentConnection.get();
//        try {
//            if(! isValidConnection(conn)){
//                conn=this.getConn();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return conn;
//    }


    @Override
    public synchronized void rlsConn(Connection conn) throws SQLException {

        log.info(Thread.currentThread().getName()+"关闭连接：busyConnections.remove:"+conn);
        this.busyConnections.remove(conn);
//        this.currentConnection.remove();
        //活动连接池删除的连接，相应的加到空闲连接池中
        try {
            if(isValidConnection(conn)){
                freeConnections.add(conn);
            }else{
                freeConnections.add(this.newConnection());
            }

        } catch (  SQLException e) {
            e.printStackTrace();
        }
        //唤醒getConnection()中等待的线程
        this.notifyAll();
    }

    @Override
    public synchronized void destroy() {
        for (Connection conn : this.freeConnections) {
            try {
                if (this.isValidConnection(conn)) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        for (Connection conn : this.busyConnections) {
            try {
                if (this.isValidConnection(conn)) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        this.isActive = false;
        this.freeConnections.clear();
        this.busyConnections.clear();
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }


    @Override
    public void checkPool() {

        final String nodename=this.cf.getPoolName();

        ScheduledExecutorService ses= Executors.newScheduledThreadPool(2);

        //功能一：开启一个定时器线程输出状态
        ses.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println(nodename +"空闲连接数："+ getFreeCount());
                System.out.println(nodename +"活动连接数："+ getBusyCount());

            }
        }, 1, 1, TimeUnit.SECONDS);

        //功能二：开启一个定时器线程，监测并维持空闲池中的最小连接数
        ses.scheduleAtFixedRate(new checkFreepools(this), 1, 5, TimeUnit.SECONDS);
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
    public IDBPoolConfig getConfig() {
        return cf;
    }

    @Override
    public void setConfig(IDBPoolConfig config) {
        this.cf = config;
    }

    //基本点6、连接池内部要保证指定最小连接数量的空闲连接
    class checkFreepools extends TimerTask {
        private SpxDBPool conpool = null;

        public checkFreepools(SpxDBPool cp) {
            this.conpool = cp;
        }

        @Override
        public void run() {
            if (this.conpool != null && this.conpool.isActive()) {
                int poolstotalnum = conpool.getFreeCount()
                        + conpool.getBusyCount();
                int subnum = conpool.cf.getMinConnections()
                        - poolstotalnum;

                if (subnum > 0) {
                    System.out.println(conpool.cf.getPoolName()
                            + "扫描并维持空闲池中的最小连接数，需补充" + subnum + "个连接");
                    for (int i = 0; i < subnum; i++) {
                        try {
                            conpool.freeConnections
                                    .add(conpool.newConnection());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

        }

    }
}
