package org.albianj.boot.entry;

import org.albianj.boot.logging.IAlbianLoggerAttribute;

import java.util.Map;

public class AlbianBootAttribute {
    private String appName = "AlbianDefaultApp";
    private String machineId;
    private String machineKey = "wefet45y56gd&^%&$($$fbf943sf98^&*&*%$@%$34tksdjfvh823r2=sdfssdfsdp[sfshfwwefwffwe";
    private String runtimeLevel = "DEBUG";

    /**
     * 根logger的配置信息
     */
    private IAlbianLoggerAttribute rootLoggerAttr;

    private Map<String, IAlbianLoggerAttribute> loggerAttrs;

    private Map<String,AlbianBundleAttribute> bundles;

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

    public Map<String, IAlbianLoggerAttribute> getLoggerAttrs() {
        return loggerAttrs;
    }

    public void setLoggerAttrs(Map<String, IAlbianLoggerAttribute> loggerAttrs) {
        this.loggerAttrs = loggerAttrs;
    }

    public IAlbianLoggerAttribute getRootLoggerAttr() {
        return rootLoggerAttr;
    }

    public void setRootLoggerAttr(IAlbianLoggerAttribute rootLoggerAttr) {
        this.rootLoggerAttr = rootLoggerAttr;
    }

    public Map<String, AlbianBundleAttribute> getBundleAttrs() {
        return bundles;
    }

    public void setBundleAttrs(Map<String, AlbianBundleAttribute> bundles) {
        this.bundles = bundles;
    }
}
