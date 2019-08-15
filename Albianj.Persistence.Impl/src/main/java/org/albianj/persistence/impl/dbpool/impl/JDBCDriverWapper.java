package org.albianj.persistence.impl.dbpool.impl;


import java.sql.*;
import java.util.Properties;

public class JDBCDriverWapper implements Driver {
    private Driver driver;

    public JDBCDriverWapper(Driver d) {
        this.driver = d;
    }

    public boolean acceptsURL(String u) throws SQLException {
        return this.driver.acceptsURL(u);
    }

    public Connection connect(String u, Properties p) throws SQLException {
        return this.driver.connect(u, p);
    }

    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }

    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }

    public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
        return this.driver.getPropertyInfo(u, p);
    }

    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }

    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
