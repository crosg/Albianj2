package org.albianj.persistence.impl.dbpool;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectionPool implements IConnectionPool {
    private static final Logger log = Logger.getLogger(ConnectionPool.class);

    private DBPropertyBean propertyBean=null;

    //连接池可用状态
    private Boolean isActive = true;

    // 空闲连接池 。由于List读写频繁，使用LinkedList存储比较合适
    private LinkedList<Connection> freeConnections = new LinkedList<Connection>();
    // 活动连接池。活动连接数 <= 允许最大连接数(maxConnections)
    private LinkedList<Connection> activeConnections = new LinkedList<Connection>();

    //当前线程获得的连接
    private ThreadLocal<Connection> currentConnection= new ThreadLocal<Connection>();

    //构造方法无法返回null，所以取消掉。在下面增加了CreateConnectionPool静态方法。
    private ConnectionPool(){
        super();
    }

    public static ConnectionPool CreateConnectionPool(DBPropertyBean propertyBean) {
        ConnectionPool connpool=new ConnectionPool();
        connpool.propertyBean = propertyBean;

        //加载驱动

        //在多节点环境配置下，因为在这里无法判断驱动是否已经加载,可能会造成多次重复加载相同驱动。
        //因此加载驱动的动作，挪到connectionManager管理类中去实现了。
        /*try {
            Class.forName(connpool.propertyBean.getDriverName());
            log.info("加载JDBC驱动"+connpool.propertyBean.getDriverName()+"成功");
        } catch (ClassNotFoundException e) {
            log.info("未找到JDBC驱动" + connpool.propertyBean.getDriverName() + "，请引入相关包");
            return null;
        }*/

        //基本点2、始使化时根据配置中的初始连接数创建指定数量的连接
        for (int i = 0; i < connpool.propertyBean.getInitConnections(); i++) {
            try {
                Connection conn = connpool.NewConnection();
                connpool.freeConnections.add(conn);
            } catch (SQLException | ClassNotFoundException e) {
                log.error(connpool.propertyBean.getNodeName()+"节点连接池初始化失败");
                return null;
            }
        }

        connpool.isActive = true;
        return connpool;
    }



    /**
     * 检测连接是否有效
     * @return Boolean
     */
    private Boolean isValidConnection(Connection conn) throws SQLException {
        try {
            if(conn==null || conn.isClosed()){
                return false;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return true;
    }

    /**
     * 创建一个新的连接
     * @return 数据库连接对象
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private Connection NewConnection() throws ClassNotFoundException,
            SQLException {

        Connection conn = null;
        try {
            if (this.propertyBean != null) {
                //Class.forName(this.propertyBean.getDriverName());
                conn = DriverManager.getConnection(this.propertyBean.getUrl(),
                        this.propertyBean.getUsername(),
                        this.propertyBean.getPassword());
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }



        return conn;
    }


    @Override
    public synchronized Connection getConnection() {
        Connection conn = null;
        if (this.getActiveNum() < this.propertyBean.getMaxConnections()) {
            // 分支1：当前使用的连接没有达到最大连接数
            // 基本点3、在连接池没有达到最大连接数之前，如果有可用的空闲连接就直接使用空闲连接，如果没有，就创建新的连接。
            if (this.getFreeNum() > 0) {
                // 分支1.1：如果空闲池中有连接，就从空闲池中直接获取
                log.info("分支1.1：如果空闲池中有连接，就从空闲池中直接获取");
                conn = this.freeConnections.pollFirst();

                //连接闲置久了也会超时，因此空闲池中的有效连接会越来越少，需要另一个进程进行扫描监测，不断保持一定数量的可用连接。
                //在下面定义了checkFreepools的TimerTask类，在checkPool()方法中进行调用。

                //基本点5、由于数据库连接闲置久了会超时关闭，因此需要连接池采用机制保证每次请求的连接都是有效可用的。
                try {
                    if(this.isValidConnection(conn)){
                        this.activeConnections.add(conn);
                        currentConnection.set(conn);
                    }else{
                        conn = getConnection();//同步方法是可重入锁
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                // 分支1.2：如果空闲池中无可用连接，就创建新的连接
                log.info("分支1.2：如果空闲池中无可用连接，就创建新的连接");
                try {
                    conn = this.NewConnection();
                    this.activeConnections.add(conn);
                } catch (ClassNotFoundException | SQLException e) {
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
                this.wait(this.propertyBean.getConninterval());
            }catch(InterruptedException e) {
                log.error("线程等待被打断");
            }

            //若线程超时前被唤醒并成功获取连接，就不会走到return null。
            //若线程超时前没有获取连接，则返回null。
            //如果timeout设置为0，就无限重连。
            if(this.propertyBean.getTimeout()!=0){
                if(System.currentTimeMillis() - startTime > this.propertyBean.getTimeout())
                    return null;
            }
            conn = this.getConnection();

        }
        return conn;
    }


    @Override
    public Connection getCurrentConnecton() {
        Connection conn=currentConnection.get();
        try {
            if(! isValidConnection(conn)){
                conn=this.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


    @Override
    public synchronized void releaseConn(Connection conn) throws SQLException {

        log.info(Thread.currentThread().getName()+"关闭连接：activeConnections.remove:"+conn);
        this.activeConnections.remove(conn);
        this.currentConnection.remove();
        //活动连接池删除的连接，相应的加到空闲连接池中
        try {
            if(isValidConnection(conn)){
                freeConnections.add(conn);
            }else{
                freeConnections.add(this.NewConnection());
            }

        } catch (ClassNotFoundException | SQLException e) {
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
        for (Connection conn : this.activeConnections) {
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
        this.activeConnections.clear();
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }


    @Override
    public void checkPool() {

        final String nodename=this.propertyBean.getNodeName();

        ScheduledExecutorService ses= Executors.newScheduledThreadPool(2);

        //功能一：开启一个定时器线程输出状态
        ses.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println(nodename +"空闲连接数："+getFreeNum());
                System.out.println(nodename +"活动连接数："+getActiveNum());

            }
        }, 1, 1, TimeUnit.SECONDS);

        //功能二：开启一个定时器线程，监测并维持空闲池中的最小连接数
        ses.scheduleAtFixedRate(new checkFreepools(this), 1, 5, TimeUnit.SECONDS);
    }

    @Override
    public synchronized int getActiveNum() {
        return this.activeConnections.size();
    }

    @Override
    public synchronized int getFreeNum() {
        return this.freeConnections.size();
    }

    //基本点6、连接池内部要保证指定最小连接数量的空闲连接
    class checkFreepools extends TimerTask {
        private ConnectionPool conpool = null;

        public checkFreepools(ConnectionPool cp) {
            this.conpool = cp;
        }

        @Override
        public void run() {
            if (this.conpool != null && this.conpool.isActive()) {
                int poolstotalnum = conpool.getFreeNum()
                        + conpool.getActiveNum();
                int subnum = conpool.propertyBean.getMinConnections()
                        - poolstotalnum;

                if (subnum > 0) {
                    System.out.println(conpool.propertyBean.getNodeName()
                            + "扫描并维持空闲池中的最小连接数，需补充" + subnum + "个连接");
                    for (int i = 0; i < subnum; i++) {
                        try {
                            conpool.freeConnections
                                    .add(conpool.NewConnection());
                        } catch (ClassNotFoundException | SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

        }

    }
}
