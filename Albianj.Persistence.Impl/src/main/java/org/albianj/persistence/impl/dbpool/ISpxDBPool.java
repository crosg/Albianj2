package org.albianj.persistence.impl.dbpool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface ISpxDBPool extends DataSource {

    /**
     * 释放当前线程数据库连接
     * @param conn 数据库连接对象
     * @throws SQLException
     */
    public void rtnConnection(Connection conn) throws SQLException;

    /**
     * 销毁清空当前连接池
     */
    public void destroy();

    /**
     * 连接池可用状态
     * @return 连接池是否可用
     */
    public boolean isActive();

    /**
     * 获取线程池活动连接数
     * @return 线程池活动连接数
     */
    public int getBusyCount();

    /**
     * 获取线程池空闲连接数
     * @return 线程池空闲连接数
     */
    public int getFreeCount();

    public ISpxDBPoolConfig getConfig();

    void setConfig(ISpxDBPoolConfig config);


    public String getPoolName() ;

    public void setPoolName(String name);
    public int getCurrRemedyConnectionsCount() ;

}
