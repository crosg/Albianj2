package org.albianj.persistence.impl.dbpool.impl;

import org.albianj.persistence.impl.dbpool.IDBPoolConfig;

public class DBPoolConfig implements IDBPoolConfig {

    private String poolName;
    //数据连接驱动
    private String driverName;
    //数据连接url
    private String url;
    //数据连接username
    private String username;
    //数据连接密码
    private String password;
    //连接池最大连接数
    private int maxConnections = 10;
    //连接池最小连接数
    private int minConnections = 5;
    //重连间隔时间 ，单位毫秒
    private int conninterval ;
    //获取连接超时时间 ，单位毫秒，0永不超时
    private int waitTimeWhenGetMs = 200;

    //整个生命的时间，最长的生存时间，到了时间
    //不管链接是否可用，全部kill掉
    //默认一小时
    private long lifeTimeMs = 60 * 60 * 1000;


    //connection free time
    //in free pool max time
    //mysql的wait time设置值，目前公司内部的默认为3分钟
    //即180s，故程序设置150s，2'30
    private long freeTimeMs = 150 * 1000;

    //最大补救链接的数量
    //当内存池内的链接全部被使用，或者链接池发生泄漏的时候，
    //补救措施是直接生成一个新的链接，先供业务使用
    //该链接使用完毕后不会被放入链接池，直接close掉
    private int maxRemedyConnectionCount = 50;

    //下面是getter and setter

    /**
     * 获取数据库连接节点名称
     * @return
     */
    @Override
    public String getPoolName() {
        return poolName;
    }

    /**
     * 设置数据库连接节点名称
     * @param poolName
     */
    @Override
    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    /**
     * 获取数据库驱动
     * @return
     */
    @Override
    public String getDriverName() {
        return driverName;
    }

    /**
     * 设置数据库驱动
     * @param driverName
     */
    @Override
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    /**
     * 获取数据库url
     * @return
     */
    @Override
    public String getUrl() {
        return url;
    }

    /**
     * 设置数据库url
     * @param url
     */
    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取用户名
     * @return
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取数据库连接密码
     * @return
     */
    @Override
    public String getPassword(){
        return password;
    }

    /**
     * 设置数据库连接密码
     * @param password
     */
    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取最大连接数
     * @return
     */
    @Override
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * 设置最大连接数
     * @param maxConnections
     */
    @Override
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * 获取最小连接数（也是数据池初始连接数）
     * @return
     */
    @Override
    public int getMinConnections() {
        return minConnections;
    }

    /**
     * 设置最小连接数（也是数据池初始连接数）
     * @param minConnections
     */
    @Override
    public void setMinConnections(int minConnections) {
        this.minConnections = minConnections;
    }

//    /**
//     * 获取初始加接数
//     * @return
//     */
//    public int getInitConnections() {
//        return initConnections;
//    }
//
//    /**
//     * 设置初始连接数
//     * @param initConnections
//     */
//    public void setInitConnections(int initConnections) {
//        this.initConnections = initConnections;
//    }

    /**
     * 获取重连间隔时间，单位毫秒
     * @return
     */
    @Override
    public int getConninterval() {
        return conninterval;
    }

    /**
     * 设置重连间隔时间，单位毫秒
     * @param conninterval
     */
    @Override
    public void setConninterval(int conninterval) {
        this.conninterval = conninterval;
    }

    /**
     * 获取连接超时时间，单位毫秒
     * @return
     */
    @Override
    public int getWaitTimeWhenGetMs() {
        return waitTimeWhenGetMs;
    }

    /**
     * 设置连接超时时间 ，单位毫秒，0-无限重连
     * @param waitTimeWhenGetMs
     */
    @Override
    public void setWaitTimeWhenGetMs(int waitTimeWhenGetMs) {
        this.waitTimeWhenGetMs = waitTimeWhenGetMs;
    }

    @Override
    public long getLifeTimeMs() {
        return lifeTimeMs;
    }

    @Override
    public void setLifeTimeMs(long lifeTimeMs) {
        this.lifeTimeMs = lifeTimeMs;
    }

    @Override
    public long getFreeTimeMs() {
        return freeTimeMs;
    }

    @Override
    public void setFreeTimeMs(long freeTimeMs) {
        this.freeTimeMs = freeTimeMs;
    }

    @Override
    public int getMaxRemedyConnectionCount() {
        return maxRemedyConnectionCount;
    }

    @Override
    public void setMaxRemedyConnectionCount(int maxRemedyConnectionCount) {
        this.maxRemedyConnectionCount = maxRemedyConnectionCount;
    }


}
