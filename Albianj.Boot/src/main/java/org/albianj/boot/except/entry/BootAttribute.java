package org.albianj.boot.except.entry;

import org.albianj.boot.logging.ILoggerAttribute;

import java.util.Map;

public class BootAttribute {
    private String appName = "AlbianDefaultApp";
    private String machineId;
    private String machineKey = "wefet45y56gd&^%&$($$fbf943sf98^&*&*%$@%$34tksdjfvh823r2=sdfssdfsdp[sfshfwwefwffwe";
    private String runtimeLevel = "DEBUG";

    /**
     * 根logger的配置信息
     */
    private ILoggerAttribute rootLoggerAttr;

    private Map<String, ILoggerAttribute> loggerAttrs;

    private Map<String, BundleAttribute> bundles;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineKey() {
        return machineKey;
    }

    public void setMachineKey(String machineKey) {
        this.machineKey = machineKey;
    }

    public String getRuntimeLevel() {
        return runtimeLevel;
    }

    public void setRuntimeLevel(String runtimeLevel) {
        this.runtimeLevel = runtimeLevel;
    }

    public Map<String, ILoggerAttribute> getLoggerAttrs() {
        return loggerAttrs;
    }

    public void setLoggerAttrs(Map<String, ILoggerAttribute> loggerAttrs) {
        this.loggerAttrs = loggerAttrs;
    }

    public ILoggerAttribute getRootLoggerAttr() {
        return rootLoggerAttr;
    }

    public void setRootLoggerAttr(ILoggerAttribute rootLoggerAttr) {
        this.rootLoggerAttr = rootLoggerAttr;
    }

    public Map<String, BundleAttribute> getBundleAttrs() {
        return bundles;
    }

    public void setBundleAttrs(Map<String, BundleAttribute> bundles) {
        this.bundles = bundles;
    }
}
