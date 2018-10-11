package org.albianj.persistence.impl.dbpool;

public class DBPropertyBean {

    private String nodeName;
    //数据连接驱动
    private String driverName;
    //数据连接url
    private String url;
    //数据连接username
    private String username;
    //数据连接密码
    private String password;
    //连接池最大连接数
    private int maxConnections ;
    //连接池最小连接数
    private int minConnections;
    //连接池初始连接数
    private int initConnections;
    //重连间隔时间 ，单位毫秒
    private int conninterval ;
    //获取连接超时时间 ，单位毫秒，0永不超时
    private int timeout ;

    //构造方法
    public DBPropertyBean(){
        super();
    }

    //下面是getter and setter

    /**
     * 获取数据库连接节点名称
     * @return
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * 设置数据库连接节点名称
     * @param nodeName
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * 获取数据库驱动
     * @return
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * 设置数据库驱动
     * @param driverName
     */
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    /**
     * 获取数据库url
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置数据库url
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取用户名
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取数据库连接密码
     * @return
     */
    public String getPassword(){
        return password;
    }

    /**
     * 设置数据库连接密码
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取最大连接数
     * @return
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * 设置最大连接数
     * @param maxConnections
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * 获取最小连接数（也是数据池初始连接数）
     * @return
     */
    public int getMinConnections() {
        return minConnections;
    }

    /**
     * 设置最小连接数（也是数据池初始连接数）
     * @param minConnections
     */
    public void setMinConnections(int minConnections) {
        this.minConnections = minConnections;
    }

    /**
     * 获取初始加接数
     * @return
     */
    public int getInitConnections() {
        return initConnections;
    }

    /**
     * 设置初始连接数
     * @param initConnections
     */
    public void setInitConnections(int initConnections) {
        this.initConnections = initConnections;
    }

    /**
     * 获取重连间隔时间，单位毫秒
     * @return
     */
    public int getConninterval() {
        return conninterval;
    }

    /**
     * 设置重连间隔时间，单位毫秒
     * @param conninterval
     */
    public void setConninterval(int conninterval) {
        this.conninterval = conninterval;
    }

    /**
     * 获取连接超时时间，单位毫秒
     * @return
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置连接超时时间 ，单位毫秒，0-无限重连
     * @param timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
