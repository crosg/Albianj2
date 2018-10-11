package org.albianj.persistence.impl.dbpool;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesManager {
    private static Properties pro = new Properties();

    private PropertiesManager() {

    }

    static {
        try {
            pro.load(PropertiesManager.class.getClassLoader().getResourceAsStream("DB.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return pro.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return pro.getProperty(key, defaultValue);
    }

    public static Enumeration<?> propertiesNames() {
        return pro.propertyNames();
    }
}
